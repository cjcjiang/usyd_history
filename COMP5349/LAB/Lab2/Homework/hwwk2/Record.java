package comp5349.mrsim;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * the class that implements a simple (Key,{Values}) Record
 */
public class Record<K extends Comparable<K>, V> implements Comparable<Record<K,V>> {

	// this class keeps statistics about the number of comparisons
	public long statsComparisons = 0;


	private K key;
	private List<V> values;

	// constructor 1
	public Record(K key, V value ) {
		this.key = key;
		values = new ArrayList<>();
		values.add(value);
	}

	// constructor 2
	Record(K key, V[] values) {
		this.key = key;
		this.values = Arrays.asList(values);
	}


	public int compareTo(K key) {
		this.statsComparisons++;
		return this.key.compareTo(key);
	}

	public int compareTo(Record<K,V> r) {
		return this.compareTo(r.getKey());
	}

	// toString method
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("(");
		sb.append(this.key.toString());
		
		for (V val : this.values) {
			sb.append(",");
			sb.append(val.toString());
		}
		sb.append(")");
		return sb.toString();
	}

	public K getKey() {
		return key;
	}
	
	public boolean appendValue(V value){
		return values.add(value);
	}

	public boolean appendValues(List<V> values){
		return this.values.addAll(values);
	}
	
	public List<V> getValues() {
		return values;
	}

	public int hashCode() {
		return this.key.hashCode();
	}
}
