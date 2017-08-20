package taskTwo;

import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;

import org.apache.hadoop.io.IntWritable;

public class DescendingComparator extends WritableComparator {
	@Override
	public int compare(WritableComparable w1, WritableComparable w2) {
		IntWritable k1 = (IntWritable)w1;
		IntWritable k2 = (IntWritable)w2;
		int result = 0;
		int key1 = k1.get();
		int key2 = k2.get();
		if(key1 == key1) {
			result = 0;
		}
		if(key1 < key1) {
			result = 1;
		}
		if(key1 < key1) {
			result = -1;
		}
		return result;
	}
}