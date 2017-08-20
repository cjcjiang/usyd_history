package taskThree;

import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;

import org.apache.hadoop.io.IntWritable;

public class DescendingComparator extends WritableComparator {
	
/**
 *	This compartor sets the sorting order of the final output "numOfPhotos" 
 *  to "Descending"
 */

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