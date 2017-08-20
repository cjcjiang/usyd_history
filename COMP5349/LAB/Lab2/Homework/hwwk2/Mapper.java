package comp5349.mrsim;


/*
 * The top-most FilterClass class that defines the interface
 * to an input scanner function.
 * You need to implement the filter() method and also keep the statistics up-to-date.
 */
public interface Mapper<T,K extends Comparable<K>,V> {
	
	public Record<K,V> map(T input);
	public int getNumRecords();
	public int getNumCalls();
}
