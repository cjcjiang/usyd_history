package taskThree;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;

import org.apache.hadoop.io.Text;

import org.apache.hadoop.mapreduce.Reducer;

/**
 * In the TaskThreePartThreeReducer, 
 * we setup a HashMap called "localityTable", 
 * which stays in the memory during calls of the reduce methods.
 * 
 * This "localityTable" will store the final output of task three,
 * which contains 50 lines of data, the size will not be too big.
 *
 * Input record format
 * localityName \t numberOfPhotos \t freq -> tag
 *
 * Output record format
 * localityName \t numberOfPhotos \t (tag1:freq1) (tag2:freq2) ... (tag10:freq10)
 * 
 * @author Yuming JIANG
 *
 */
 
public class TaskThreePartThreeReducer extends Reducer<Text, Text, Text, Text> {
	
	Text outputValue = new Text();
	Text outputKey = new Text();
	//localityTable will store the final output of task three
	Map<String, ArrayList<String>> localityTable = new HashMap<String,ArrayList<String>>();
	
	public void reduce(Text key, Iterable<Text> values, 
			Context context
	) throws IOException, InterruptedException {

		//the input key format is localityName \t numberOfPhotos \t freq
		String key_str_as_output_key = key.toString();
		String[] key_array_as_output_key = key_str_as_output_key.split("\t");
		String localityNameAsOutputKey = key_array_as_output_key[0] + "\t" + key_array_as_output_key[1];
		
		for(Text value: values){
			String key_str = key.toString();
			String[] key_array = key_str.split("\t");
			String localityName = key_array[0] + "\t" + key_array[1];
			String freq = key_array[2];
			String tag = value.toString();
			
			if(localityTable.containsKey(localityName)){
				ArrayList<String> tag_arraylist = localityTable.get(localityName);
				if(tag_arraylist.size() < 10){
					//Make sure only the top 10 freq tag is stored
					String tag_and_freq = "(" + tag + ":" + freq + ")";
					tag_arraylist.add(tag_and_freq);
					localityTable.put(localityName, tag_arraylist);
				}
			}else{
				ArrayList<String> tag_arraylist = new ArrayList<String>();
				String tag_and_freq = "(" + tag + ":" + freq + ")";
				tag_arraylist.add(tag_and_freq);
				localityTable.put(localityName, tag_arraylist);
			}
		}
		
		
		StringBuffer strBuf = new StringBuffer();
		ArrayList<String> tag_arraylist = localityTable.get(localityNameAsOutputKey);
		for(String tag_and_freq : tag_arraylist){
			strBuf.append(tag_and_freq + " ");
		}
			
		outputKey.set(localityNameAsOutputKey);
		outputValue.set(strBuf.toString());
		context.write(outputKey, outputValue);
		
	}
}
