package taskTwo;

import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;

import org.apache.hadoop.io.IntWritable;

public class DescendingComparator extends WritableComparator {
	
	//where the first parameter is the key class and 
	//the second parameter's value ensures WritableComparator is instantiated 
	//details on http://stackoverflow.com/questions/30587940/nullpointerexception-in-mapreduce-sorting-program
	//Without whis constructor, there will be NullPointerException on w1 and w2
	protected DescendingComparator() {
		super(IntWritable.class, true);
	}
	
	@Override
	public int compare(WritableComparable w1, WritableComparable w2) {
		IntWritable k1 = (IntWritable)w1;
		IntWritable k2 = (IntWritable)w2;
		int result = -1 * k1.compareTo(k2);
		return result;
	}
}