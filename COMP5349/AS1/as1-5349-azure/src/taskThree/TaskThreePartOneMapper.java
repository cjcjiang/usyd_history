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

/**
 * In the TaskThreePartOneMapper's setup method, 
 * we read the file from the distributedcache and store its content in
 * a Hashtable called "placeTable", which stays in the 
 * memory during calls of the map methods.
 * 
 * The TaskThreePartOneMapper's input is place.txt, 
 * representing all the place information. 
 * 
 * input record format
 * place-id \t woeid \t latitude \t longitude \t place-name \t place-type-id \t place-url
 * 
 * output record format:
 * 
 * localityName \t numberOfPhotos \t place-id
 * 
 * @author Yuming JIANG
 *
 */

public class TaskThreePartOneMapper extends Mapper<Object, Text, Text, Text> {
	
	private Hashtable <String, String> placeTable = new Hashtable<String, String>();
	private Text keyOut = new Text();
	private Text valueOut = new Text();
	
	// get the distributed file and parse it
	public void setup(Context context)
		throws java.io.IOException, InterruptedException{
		
		Path[] cacheFiles = DistributedCache.getLocalCacheFiles(context.getConfiguration());
		if (cacheFiles != null && cacheFiles.length > 0) {
			String line;
			String[] tokens;
			BufferedReader placeReader = new BufferedReader(new FileReader(cacheFiles[0].toString()));
			try {
				while ((line = placeReader.readLine()) != null) {
					//In the distributedcache, it is the output of taskTwo
					//Format is localityName \t numberOfPhotos
					//Make localityName as the key of the hashtable
					//Make localityName \t numberOfPhotos as the value of the hashtable
					tokens = line.split("\t");
					//tokens[0] is localityName
					String placeURL = tokens[0];
					//tokens[1] numberOfPhotos, the number of photos taken in this locality
					String taskTwoResult = tokens[0] + "\t" + tokens[1];
					placeTable.put(placeURL, taskTwoResult);
				}
			} 
			finally {
				placeReader.close();
			}
		}
	}
	
	public void map(Object key, Text value, Context context
	) throws IOException, InterruptedException {
		//value is one of the line of data in place.txt
		//split this line of data in place.txt into string array
		String[] dataArray = value.toString().split("\t"); 
		if (dataArray.length < 7){ // a not complete record with all data
			return; // don't emit anything
		}
		String placeURL = dataArray[6];
		for(String top50Place : placeTable.keySet()){
			if(placeURL.contains(top50Place)){
				String taskTwoResult = placeTable.get(top50Place);
				keyOut.set(taskTwoResult);
				//dataArray[0] is place-id
				valueOut.set(dataArray[0]);
				context.write(keyOut, valueOut);
			}
		}
	}

}
