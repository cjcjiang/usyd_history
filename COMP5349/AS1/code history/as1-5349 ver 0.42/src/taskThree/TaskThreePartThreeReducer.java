package taskThree;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;

import org.apache.hadoop.io.Text;

import org.apache.hadoop.mapreduce.Reducer;

/**
 * Input record format
 * dog -> {48889082718@N01, 48889082718@N01, 3423249@N01}
 *
 * Output for the above input key valueList
 * dog -> 48889082718@N01=2,3423249@N01=1,
 * 
 * @author Ying Zhou
 *
 */
public class TaskThreePartThreeReducer extends Reducer<Text, Text, Text, Text> {
	Text outputValue = new Text();
	Text outputKey = new Text();
	Map<String, ArrayList<String>> localityTable = new HashMap<String,ArrayList<String>>();
	
	public void reduce(Text key, Iterable<Text> values, 
			Context context
	) throws IOException, InterruptedException {

		//the input key is localityName \t numberOfPhotos \t freq of this tag
		String key_str = key.toString();
		String[] key_array = key_str.split("\t");
		String localityName = key_array[0] + "\t" + key_array[1];
		String freq = key_array[2];
		
		for(Text value: values){
			//the input values is tag
			String tag = value.toString();
			
			if(localityTable.containsKey(localityName)){
				ArrayList<String> tag_arraylist = localityTable.get(localityName);
				if(tag_arraylist.size() < 10){
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
		
		for(String localityNameInTable : localityTable.keySet()){
			StringBuffer strBuf = new StringBuffer();
			ArrayList<String> tag_arraylist = localityTable.get(localityNameInTable);
			for(String tag_and_freq : tag_arraylist){
				strBuf.append(tag_and_freq + " ");
			}
			
			outputKey.set(localityNameInTable);
			outputValue.set(strBuf.toString());
			context.write(outputKey, outputValue);
		}
	}
}
