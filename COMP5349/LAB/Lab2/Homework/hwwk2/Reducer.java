package comp5349.mrsim;

import java.util.List;

/*
 * The top-most Reducer interface.
 * You need to implement the reduce method.
 */
public interface Reducer<K,V,S> {

	/**
	 * Reduce operation on a list of input records.
	 * 
	 * @param key - the key accepted by this reducer
	 * @param values - a list of records grouped by this key to reduce
 	 * @return the result of reduction of the input values
	 */
    public S reduce (K key, List<V> values );
}
