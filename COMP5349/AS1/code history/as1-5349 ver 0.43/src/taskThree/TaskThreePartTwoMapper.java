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
 
public class TaskThreePartTwoMapper extends Mapper<Object, Text, Text, IntWritable> {
	private Hashtable <String, String> placeTable = new Hashtable<String, String>();
	//private Text keyOut = new Text(), valueOut = new Text();
	private Text keyOut = new Text();
	//iniatiate valueOut with IntWritable, by jiang
	private IntWritable valueOut = new IntWritable();
	
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
					//localityName \t numberOfPhotos \t place-id
					tokens = line.split("\t");
					String localityName = tokens[0] + "\t" + tokens[1];
					placeTable.put(tokens[2], localityName);
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
		//data is the photo
		String[] dataArray = value.toString().split("\t"); //split the data into array
		if (dataArray.length < 6){ // a not complete record with all data
			return; // don't emit anything
		}
		String placeId = dataArray[4];
		String localityName = placeTable.get(placeId);
		if (localityName !=null){
			String[] tag_array =  dataArray[2].split(" ");
			for(String tag_temp : tag_array){
				String localityName_plus_tag = localityName + "\t" + tag_temp;
				keyOut.set(localityName_plus_tag);
				valueOut.set(1);
				context.write(keyOut, valueOut);
			}
		}
	}
}
