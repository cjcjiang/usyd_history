package taskTwo;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.hadoop.io.Text;

import org.apache.hadoop.io.IntWritable;

import org.apache.hadoop.mapreduce.Reducer;

/**
 * Input record format
 * dog -> {48889082718@N01, 48889082718@N01, 3423249@N01}
 *
 * Output for the above input key valueList
 * dog -> 48889082718@N01=2,3423249@N01=1,
 * 
 * @author Ying Zhou
 *
 */
public class TaskTwoReducer extends Reducer<IntWritable, Text, Text, IntWritable> {
	
	public void reduce(IntWritable key, Iterable<Text> values, 
			Context context
	) throws IOException, InterruptedException {

		for(Text localityName : values){
			context.write(localityName, key);
		}
		
	}
}
