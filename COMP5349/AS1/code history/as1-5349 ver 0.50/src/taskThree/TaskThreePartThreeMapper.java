package taskThree;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Hashtable;

import org.apache.hadoop.filecache.DistributedCache;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;

import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Mapper.Context;

/**
 * Input record format
 * localityName \t numberOfPhotos \t tag \t freq
 *
 * Output format
 * localityName \t numberOfPhotos \t freq -> tag,
 * 
 * @author Yuming JIANG
 *
 */ 
public class TaskThreePartThreeMapper extends Mapper<Object, Text, Text, Text> {
        private Text outPutKey = new Text();
		private Text outPutValue = new Text();

	public void map(Object key, Text value, Context context
	) throws IOException, InterruptedException {
		//data format: localityName \t numberOfPhotos \t tag \t freq
		String[] dataArray = value.toString().split("\t"); //split the data into array
		String localityNameAndFreq = dataArray[0] + "\t" + dataArray[1] + "\t" + dataArray[3];
		String tag = dataArray[2];
	    outPutKey.set(localityNameAndFreq);
		outPutValue.set(tag);
		context.write(outPutKey,outPutValue);
	}						
}
		