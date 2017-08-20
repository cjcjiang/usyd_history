package taskThree;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Partitioner;

public class TaskThreePartThreePartitioner extends Partitioner<Text, Text> {
	public int getPartition(Text key, Text value, int arg2) {
		// a simple and static partitioner to partition the key into 3
		//regions, assume we know before hand the number of reducer is 3
		// [1,1000) => 0, [1000, 2000) => 1, [2000,-) => 2
		//key format: localityName \t numberOfPhotos \t freq
		String[] key_array = key.toString().split("\t");
		String localityName = key_array[0];
		
		String localityNameDiv1 = "Germany/BE/Berlin";
		String localityNameDiv2 = "China/Shanghai/Shanghai";
		
		int result = localityName.compareTo(localityNameDiv1);
		if(result < 0 || result == 0){
			return 0;
		}else{
			result = localityName.compareTo(localityNameDiv2);
			if(result < 0 || result == 0){
				return 1;
			}else{
				return 2;
			}
		}
	}
}