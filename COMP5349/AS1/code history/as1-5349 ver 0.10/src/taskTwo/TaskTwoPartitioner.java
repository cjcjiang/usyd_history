package taskTwo;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Partitioner;

public class TaskTwoPartitioner extends Partitioner<IntWritable, Text> {
	public int getPartition(IntWritable key, Text value, int arg2) {
		// a simple and static partitioner to partition the key into 3
		//regions, assume we know before hand the number of reducer is 3
		// [1,1000) => 0, [1000, 2000) => 1, [2000,-) => 2
           int keyInt = key.get();
               if (keyInt <1000)
                   return 0;
               if (keyInt < 2000 )
                   return 1;
               else
                   return 2;
	}
}