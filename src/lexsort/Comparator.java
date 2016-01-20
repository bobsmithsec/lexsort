package lexsort;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

class Comparator {
	
	private final String _weight;
	private final HashMap<Character, Integer> _weightMap;
	// simple thread safe cache
	private static final ConcurrentHashMap<String, HashMap<Character, Integer>> _cachedWeightMap;
	private static final int MAX_CACHE_SIZE = 10;
	
	static {
		_cachedWeightMap = new ConcurrentHashMap<String, HashMap<Character, Integer>>();
	}
	
	Comparator(String weight) throws InvalidInputException {
		this._weight = weight;
		
		// cache hit
		if (_cachedWeightMap.containsKey(weight)) {
			this._weightMap = _cachedWeightMap.get(weight);
		} else {
			this._weightMap = new HashMap<Character, Integer>();
			
			int size = weight.length();
			for (int i = 0; i < size; i++) {
				// the closer to the top, the higher the weight
				Character cchar = weight.charAt(i);
				if (this._weightMap.containsKey(cchar)) {
					throw new InvalidInputException("Duplicate character in the order string");
				}
				this._weightMap.put(weight.charAt(i), size - i);
			}
			
			// rough cache: cache the first few elements
			if (_cachedWeightMap.size() < MAX_CACHE_SIZE) {
				_cachedWeightMap.putIfAbsent(this._weight, this._weightMap);
			}
		}
		
	}
	
	int compare(String string1, String string2) throws InvalidInputException {
		// sanity check
		if (string1 == null) return 1;
		else if (string2 == null) return -1;
		
		int length1 = string1.length();
		int length2 = string2.length();
		
		int max = (length1 < length2)? length1:length2;
		for (int i = 0; i < max; i++) {
			Integer weight1 = this._weightMap.get(string1.charAt(i));
			Integer weight2 = this._weightMap.get(string2.charAt(i));
			if (weight1 == null) {
				throw new InvalidInputException("Character not recognized: " + String.valueOf(string1.charAt(i)));
			} else if (weight2 == null) {
				throw new InvalidInputException("Character not recognized: " + String.valueOf(string2.charAt(i)));
			}
			if (weight1 < weight2) {
				return -1;
			} else if (weight1 > weight2) {
				return 1;
			}
		}
		
		if (length1 > length2) {
			return -1;
		} else if (length1 < length2) {
			return 1;
		}
		
		return 0;
	}
}
