package comp5349.mrsim;

public abstract class Filter<T, K extends Comparable<K>, V> implements Mapper<T,K,V>{

	int numRecords = 0, numCalls = 0;
	@Override
	public Record<K, V> map(T input) {
		numCalls++;
		Record<K,V>  rec = filter(input);
		if(rec != null) numRecords++;
		return rec;
	}

	public abstract Record<K,V> filter(T input);
	
	
	@Override
	/**
	 * Return the number of records this filter has produced.
	 * 
	 */
	public int getNumRecords() {
		return numRecords;
	}

	@Override
	/**
	 * Return the number of times this filter has been called.
	 */
	public int getNumCalls() {
		return numCalls;
	}

}
