package taskThree;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.mapreduce.Reducer;

/**
 * Input record format
 * localityName \t numberOfPhotos \t tag -> 1
 *
 * Output record format
 * localityName \t numberOfPhotos \t tag \t freq
 * "freq" means the number of occurence times of this tag in this locality
 * 
 * @author Yuming JIANG
 *
 */

public class TaskThreePartTwoReducer extends Reducer<Text, IntWritable, Text, IntWritable> {
	IntWritable valueOut = new IntWritable();
	public void reduce(Text key, Iterable<IntWritable> values, 
			Context context
	) throws IOException, InterruptedException {
		
		int totalNumOfTags = 0;
		for (IntWritable numOfTags: values){
			totalNumOfTags += numOfTags.get();
		}
		valueOut.set(totalNumOfTags);
		context.write(key, valueOut);
	}
}