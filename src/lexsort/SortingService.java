package lexsort;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class SortingService {
	// thread pool for parallel mergesort
	private static final int MAX_NUM_OF_SORTER_THREAD = 4;
	private static final int MAX_SIZE_PER_THREAD = 2; // need to bump this up for real systems
	private static final ExecutorService executor = Executors.newFixedThreadPool(MAX_NUM_OF_SORTER_THREAD);
	
	// limits on the input to prevent resource intensive DoS
	private static final int MAX_NUM_OF_STRINGS = 100;
	private static final int MAX_NUM_OF_CHAR_IN_STRINGS = 1000;
	
	private static void sanity_check(String[] list, String order) throws InvalidInputException {
		if (list == null || order == null || list.length == 0 || order.length() == 0) {
			throw new InvalidInputException("Empty list or order");
		} else if (list.length > MAX_NUM_OF_STRINGS || order.length() > MAX_NUM_OF_CHAR_IN_STRINGS) {
			throw new InvalidInputException("Input too large");
		} else {
			// This is fast since string length in Java is readily available (Strings not null terminated)
			for (int i = 0; i < list.length; i++) {
				if (list[i].length() > MAX_NUM_OF_CHAR_IN_STRINGS) {
					throw new InvalidInputException("Input too large");
				}
			}
		}
	}

	// sort the array of strings by the their lexical order specified in the 'order' argument
	public static void sort(String[] list, String order) throws InvalidInputException {
		// sanity check
		sanity_check(list, order);
		
		// 1. use merge sort to sort the list
		// 2. implement a comparator to compare two strings
		int size = list.length;
		mergesort(list, 0, size - 1, new String[size], new Comparator(order));
	}
	
	private static void mergesort(String[] destination, int start, int end,
			String[] work, Comparator comparator) throws InvalidInputException {
		
		// finished when start and end meets
		if (start == end) return;
		
		int mid = (end - start) / 2;
		
		// sort the first half in a separate thread if its size is larger than the threshold
		Future<?> workerComplete = null;
		if (mid - start + 1 >= MAX_SIZE_PER_THREAD) {
			workerComplete = executor.submit(new Sorter(destination, start, mid, work, comparator));
		} else {
			mergesort(destination, start, mid, work, comparator);
		}
		mergesort(destination, mid + 1, end, work, comparator);
		
		// wait for the first separate thread to finish
		if (workerComplete != null) {
			try {
				workerComplete.get();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		}
		
		// merge the results
		for (int i = start, j = mid + 1, index = 0; index <= end; index++) {
			if (j > end || (i <= mid && comparator.compare(destination[i], destination[j]) > 0)) {
				work[index] = destination[i++];
			} else {
				work[index] = destination[j++];
			}
		}
		
		// copy the results to destination
		for (int i = start; i <= end; i++) {
			destination[i] = work[i];
		}
	}

	static class Sorter implements Runnable {
		private String[] _destination;
		private int _start;
		private int _end;
		private String[] _work;
		private Comparator _comparator;
		
		Sorter(String[] destination, int start, int end, String[] work, Comparator comparator) {
			this._destination = destination;
			this._start = start;
			this._end = end;
			this._work = work;
			this._comparator = comparator;
		}
		
		@Override
		public void run() {
			try {
				mergesort(_destination, _start, _end, _work, _comparator);
			} catch (InvalidInputException e) {
				e.printStackTrace();
				System.exit(-1);
			}
		}
		
	}

	

}
