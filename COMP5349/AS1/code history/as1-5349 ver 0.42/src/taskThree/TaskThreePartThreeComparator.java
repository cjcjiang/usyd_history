package taskThree;

import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;

import org.apache.hadoop.io.Text;

import java.lang.Integer;

public class TaskThreePartThreeComparator extends WritableComparator {
	
	//where the first parameter is the key class and 
	//the second parameter's value ensures WritableComparator is instantiated 
	//details on http://stackoverflow.com/questions/30587940/nullpointerexception-in-mapreduce-sorting-program
	//Without whis constructor, there will be NullPointerException on w1 and w2
	protected TaskThreePartThreeComparator() {
		super(Text.class, true);
	}
	
	@Override
	public int compare(WritableComparable w1, WritableComparable w2) {
		Text k1 = (Text)w1;
		Text k2 = (Text)w2;
		String key1 = k1.toString();
		String key2 = k2.toString();
		String[] key1_array = key1.split("\t");
		String[] key2_array = key2.split("\t");
		String localityName1 = key1_array[0];
		String localityName2 = key2_array[0];
		Integer numOfPhotos1 = new Integer(key1_array[1]);
		Integer numOfPhotos2 = new Integer(key2_array[1]);
		Integer freq1 = new Integer(key1_array[2]);
		Integer freq2 = new Integer(key2_array[2]);
		int result = localityName1.compareTo(localityName2);
		if(0 == result) {
			result = -1 * numOfPhotos1.compareTo(numOfPhotos2);
		}
		if(0 == result) {
			result = -1 * freq1.compareTo(freq2);
		}
		return result;
	}
}