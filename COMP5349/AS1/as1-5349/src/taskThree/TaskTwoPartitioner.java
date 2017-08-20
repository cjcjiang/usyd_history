package taskThree;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Partitioner;

public class TaskTwoPartitioner extends Partitioner<IntWritable, Text> {
	public int getPartition(IntWritable key, Text value, int arg2) {
		// a simple and static partitioner to partition the key into 3
		// regions, assume we know before hand the number of reducer is 3
		int keyInt = key.get();
		if (keyInt < 5){
			return 0;
			}
			else if (keyInt < 37){
				return 1;
			}
			else{
				return 2;
				} 
	}
}