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
 * The map reads a file named place.txt which is stored
 * in the distributed cache and select two attributes: 
 * place-id and place-url. The data under attritube place-url
 * is specified to locality level. The main input file join
 * the place.txt with place-id. This program process the
 * place-url and make it to locality-name. It also do the
 * counting job to output 1 every time it counts a photo.
 *
 */

// to count the number of the occurence of the place
// Mapper<Object, Text, Text, Text> to Mapper<Object, Text, Text, IntWritable>
 
public class TaskOneMapper extends Mapper<Object, Text, Text, IntWritable> {
	private Hashtable <String, String> placeTable = new Hashtable<String, String>();
	//private Text keyOut = new Text(), valueOut = new Text();
	private Text keyOut = new Text();
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
					//to get the url of the place, the index should be 6,
					//to get locality, split("/") the url
					//format "/India/NCT/New+Delhi/Mehrauli"
					//after spliting, there should be 5 string at most, 4 at least
					//get placeNameArray[1], placeNameArray[2], placeNameArray[3]
					placeTable.put(tokens[0], tokens[6]);
				}
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
		String placeName = placeTable.get(placeId);
		if (placeName !=null){
			// keyOut.set(dataArray[0]);
			// valueOut.set(dataArray[3] + "\t" + placeName);
			// context.write(keyOut, valueOut);		
			// the final output should be localityName \t numberOfPhotos,
			// for the mapper, the key should be localityName, the value should be 1
			// to get the localityName, first split(",") 
			String[] placeNameArray = placeName.split("/");
			
			// judge if this is a right place URL format
			int placeNameArrayLength = placeNameArray.length;
			if(placeNameArrayLength > 5 || placeNameArrayLength < 4){
				System.out.println("the format of the placeName is wrong");
				return;
			}
			if(placeNameArrayLength < 6 && placeNameArrayLength >3){
				String localityName = placeNameArray[1] + "/" 
				+ placeNameArray[2] + "/" + 
				placeNameArray[3];
				keyOut.set(localityName);
				valueOut.set(1);
				context.write(keyOut, valueOut);
			}
			
			
			
		}
		
	}

}
