package taskTwo;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.mapreduce.Reducer;

public class TaskOneReducer extends Reducer<Text, IntWritable, Text, IntWritable> {
	IntWritable valueOut = new IntWritable();
	public void reduce(Text key, Iterable<IntWritable> values, 
			Context context
	) throws IOException, InterruptedException {

		Map<String, Integer> localityPhotoFrequency = new HashMap<String,Integer>();
		
		int totalNumOfPhotos = 0;
		for (IntWritable numOfPhotos: values){
			totalNumOfPhotos += numOfPhotos.get();
		}
		valueOut.set(totalNumOfPhotos);
		context.write(key, valueOut);
	}
}