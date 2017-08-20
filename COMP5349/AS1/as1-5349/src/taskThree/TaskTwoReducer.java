package taskThree;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.hadoop.io.Text;

import org.apache.hadoop.io.IntWritable;

import org.apache.hadoop.mapreduce.Reducer;

/**
 * The Map set photoNum as a key and locality as values,
 * so that Hadoop will sort the key photoNumAnd in order 
 * (the DescendingComparator specifies the exact sorting method).
 */
public class TaskTwoReducer extends Reducer<IntWritable, Text, Text, IntWritable> {
	
	int countForTop50 = 0;
	public void reduce(IntWritable key, Iterable<Text> values, 
			Context context
	) throws IOException, InterruptedException {

		for(Text localityName : values){
			if(countForTop50 < 50){
				context.write(localityName, key);
				countForTop50 ++;
			}
			//context.write(localityName, key);
		}
		
	}
}
