package taskTwo;

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
 
public class TaskOneMapper extends Mapper<Object, Text, Text, IntWritable> {
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
					tokens = line.split("\t");
					//use full place.txt index is 6, other wise it is 1.
					//this is not right, with full place.txt
					//to get the name of the place, the index should be 4, by jiang
					//to get locality, split(",") the name, pin jie zui hou 3 ge string
					//format Sydney, NSW, Australia				
					placeTable.put(tokens[0], tokens[4]);
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
		String[] dataArray = value.toString().split("\t"); //split the data into array
		if (dataArray.length < 5){ // a not complete record with all data
			return; // don't emit anything
		}
		String placeId = dataArray[4];
		String placeName = placeTable.get(placeId);
		if (placeName !=null){
			// keyOut.set(dataArray[0]);
			// valueOut.set(dataArray[3] + "\t" + placeName);
			// context.write(keyOut, valueOut);
			
			//the final output should be localityName \t numberOfPhotos, by jiang
			//for the mapper, the key should be localityName, the value should be 1
			//to get the localityName, first split(",") the 
			//ATTENTION: there can be something wrong, for example, Chamblandes, Pully, VD, CH, Switzerland
			//TODO: when placeName is wrong, but placeUrl is right
			//here only use the placeName and make sure it is right
			String[] placeNameArray = placeName.split(",");
			
			//judge if this is a right placeName format
			int placeNameArrayLength = placeNameArray.length;
			if(placeNameArrayLength > 4 || placeNameArrayLength < 3){
				System.out.println("the format of the placeName is wrong");
				return;
			}
			if(placeNameArrayLength < 5 && placeNameArrayLength >2){
				String localityName = placeNameArray[placeNameArrayLength-3] + "," 
				+ placeNameArray[placeNameArrayLength-2] + "," + 
				placeNameArray[placeNameArrayLength-1];
				keyOut.set(localityName);
				valueOut.set(1);
				context.write(keyOut, valueOut);
			}
			
			
			
		}
		
	}

}
