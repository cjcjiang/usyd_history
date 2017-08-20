package taskThree;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Hashtable;

import org.apache.hadoop.filecache.DistributedCache;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Mapper.Context;
/*
 * The Map set photoNum as a key and locality as values, 
 * so that Hadoop will sort the key photoNumAnd in order 
 * (the DescendingComparator specifies the exact sorting method).
 */
 
public class TaskTwoMapper extends Mapper<Object, Text, IntWritable, Text> {
        private Text locality = new Text();
		private IntWritable photoNum = new IntWritable();

	
	public void map(Object key, Text value, Context context
	) throws IOException, InterruptedException {
		String[] dataArray = value.toString().split("\t"); //split the data into array
		String localityName = dataArray[0];
		int number = Integer.parseInt(dataArray[1]);
	    locality.set(localityName);
		photoNum.set(number);
		context.write(photoNum,locality);
	}						
}
		