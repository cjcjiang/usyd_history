package taskThree;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Hashtable;

import org.apache.hadoop.filecache.DistributedCache;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;

//to use IntWritable, import by jiang
import org.apache.hadoop.io.IntWritable;

import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Mapper.Context;

/**
 * This is an example of using DistributedCache to join a large table with
 * a small one.
 * 
 * The files to be distributed are setup in the driver method
 * 
 * In the Mapper's setup method, we read the file and store its content in
 * desirable structure as the mapper's instance variable, which stays in the 
 * memory during calls of the map methods.
 * 
 * In this particular example, the file content is just a key value pair of
 * place_id and place_name. We use a hashtable to store it.
 * 
 * The Mapper's input is one of n0x.txt, representing photo information. 
 * 
 * input record format
 * 2048252769	48889082718@N01	dog francis lab	2007-11-19 17:49:49	RRBihiubApl0OjTtWA	16
 * 
 * output record format:
 * 
 * photo_id \t date_taken \t place_name
 * 
 * 
 * @see ReplicateJoinDriver
 * @author Ying Zhou
 *
 */

//to count the number of the occurence of the place
//jiang chang Mapper<Object, Text, Text, Text> to Mapper<Object, Text, Text, IntWritable>
 
public class TaskThreePartOneMapper extends Mapper<Object, Text, Text, Text> {
	private Hashtable <String, String> placeTable = new Hashtable<String, String>();
	//private Text keyOut = new Text(), valueOut = new Text();
	private Text keyOut = new Text();
	private Text valueOut = new Text();
	
	public void setPlaceTable(Hashtable<String,String> place){
		placeTable = place;
	}
	
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
					//Format is United+Kingdom/England/London	1911310
					//Make the /United+Kingdom/England/London as the key of the hashtable
					//Make United+Kingdom/England/London	1911310 as the value of the hashtable
					tokens = line.split("\t");
					//tokens[0] is United+Kingdom/England/London
					String placeURL = tokens[0];
					//tokens[1] 1911310, the number of photos taken in this locality
					String taskTwoResult = tokens[0] + "\t" + tokens[1];
					placeTable.put(placeURL, taskTwoResult);
				}
				//System.out.println("size of the place table is: " + placeTable.size());
			} 
			finally {
				placeReader.close();
			}
		}
	}
	
	public void map(Object key, Text value, Context context
	) throws IOException, InterruptedException {
		//data is the place.txt
		String[] dataArray = value.toString().split("\t"); //split the data into array
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
