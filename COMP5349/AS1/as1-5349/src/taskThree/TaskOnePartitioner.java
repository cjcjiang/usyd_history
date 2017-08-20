package taskThree;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Partitioner;

public class TaskOnePartitioner extends Partitioner<Text, IntWritable> {
	public int getPartition(Text key, IntWritable value, int arg2) {
		// a simple and static partitioner to partition the key into 3
		//regions, assume we know before hand the number of reducer is 3
		String localityName = key.toString();
		
		String partOneDivdingString = "Italy/Sardinia/Sorgono";
		String partTwoDivdingString = "United+Kingdom/England/Harton";
		
		if (localityName.compareTo(partOneDivdingString)<0)
			return 0;
		if (localityName.compareTo(partTwoDivdingString)<0)
			return 1;
		else
			return 2;
	}
}