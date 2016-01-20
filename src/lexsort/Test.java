package lexsort;

import java.util.Arrays;

public class Test {
	public static void main(String[] args) {
		String[] array = null;
		String order = null;
		String[] result = null;
		
		// test 1
		array = new String[]{"acb", "abc", "bca"};
		order = "abc";
		result = new String[]{"abc", "acb", "bca"};
		test(array, order, result);
		
		// test 2
		array = new String[]{"acb", "abc", "bca"};
		order = "cba";
		result = new String[]{"bca", "acb", "abc"};
		test(array, order, result);
		
		// test 3
		array = new String[]{"aaa","aa",""};
		order = "a";
		result = new String[]{"", "aa", "aaa"};
		test(array, order, result);
	}
	
	private static void test(String[] array, String order, String[] result) {
		try {
			SortingService.sort(array, order);
		} catch (InvalidInputException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		for (int i = 0; i < array.length; i++) {
			if (!array[i].equals(result[i])) {
				System.err.println("Test failed");
				System.err.println("array: " + Arrays.deepToString(array));
				System.err.println("result: " + Arrays.deepToString(result));
				return;
			}
		}
		System.out.println("Test succeeds");
	}
}
