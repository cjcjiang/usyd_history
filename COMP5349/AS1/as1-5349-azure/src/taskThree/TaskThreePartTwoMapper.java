package taskThree;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Hashtable;
import java.util.ArrayList;

import org.apache.hadoop.filecache.DistributedCache;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.IntWritable;

import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Mapper.Context;

/**
 * In the TaskThreePartTwoMapper's setup method, 
 * we read the file from the distributedcache and store its content in
 * a Hashtable called "placeTable", which stays in the 
 * memory during calls of the map methods.
 * 
 * The TaskThreePartTwoMapper's input is all the photo information. 
 * 
 * input record format
 * photo-id \t owner \t tags \t date-taken \t place-id \t accuracy
 * 
 * output record format:
 * 
 * localityName \t numberOfPhotos \t tag -> 1
 * 
 * @author Yuming JIANG
 *
 */
 
public class TaskThreePartTwoMapper extends Mapper<Object, Text, Text, IntWritable> {
	
	private Hashtable <String, String> placeTable = new Hashtable<String, String>();
	private Text keyOut = new Text();
	private IntWritable valueOut = new IntWritable();
	
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
					//In the distributedcache, it is the output of TaskThreePartOne
					//Format is localityName \t numberOfPhotos \t place-id
					tokens = line.split("\t");
					String localityName = tokens[0] + "\t" + tokens[1];
					placeTable.put(tokens[2], localityName);
				}
			} 
			finally {
				placeReader.close();
			}
		}
	}
	
	public void map(Object key, Text value, Context context
	) throws IOException, InterruptedException {
		//value is one of the line of data in photo
		//split this line of data into string array
		String[] dataArray = value.toString().split("\t");
		if (dataArray.length < 6){ // a not complete record with all data
			return; // don't emit anything
		}
		String placeId = dataArray[4];
		String localityName = placeTable.get(placeId);
		if (localityName !=null){
			//abandonedTag will store all the parent locality information
			ArrayList<String> abandonedTag = new ArrayList<String>();
			String[] tag_array =  dataArray[2].split(" ");
			//The format of localityName is localityName \t numberOfPhotos
			String[] localityName_array = localityName.split("\t");
			//Get only the localityURL
			String localityURL = localityName_array[0];
			//localityURL can be something like United+States/NY/New+York
			//split localityURL with / or +
			//Each single part of localityURL is stored in localityURL_array
			String[] localityURL_array = localityURL.split("/|\\+");
			
			//Add these locality url information into the list of tags that should be abandoned
			for(String locality : localityURL_array){
				abandonedTag.add(locality.toLowerCase());
			}
			
			//Some special strings that should be filtered, also added into the list of tags that should be abandoned
			String special_str = "usa city uk europe sf pennsylvania roma italia lazio danmark quebec montréal québec minnesota 東京 日本 台灣 台北 上海 中国 京都 北京";
			String[] special_str_array = special_str.split(" ");
			for(String temp : special_str_array){
				abandonedTag.add(temp);
			}
			
			for(String tag_temp : tag_array){
				//0: this tag will be abandoned, 1: this tag will not be abandoned
				int abandon_result = 1;
				
				for(String str_ab : abandonedTag){
					//Set the abandon_result flag
					//if this tag contain any string about the parent locality
					//the abandon_result flag will be set to 0, means this tag will be abandoned
					if(tag_temp.toLowerCase().contains(str_ab)){
						abandon_result = 0;
					}
				}
				
				if(tag_temp.matches("[0-9]+")){
					//tag only contain numbers, always means the year, filtered 
				}else if(abandon_result == 0){
					//tag contains information about the parent locality, filtered
				}else{
					String localityName_plus_tag = localityName + "\t" + tag_temp;
					keyOut.set(localityName_plus_tag);
					valueOut.set(1);
					context.write(keyOut, valueOut);
				}
			}
		}
	}
}
