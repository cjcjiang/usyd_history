package taskThree;

import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;

import org.apache.hadoop.io.Text;

/**
 * 
 * This TaskThreePartThreeGroupingComparator is designed 
 * to group all of the records with the same "localityName \t numberOfPhotos"
 * 
 * The input key format
 * localityName \t numberOfPhotos \t freq
 * 
 * @author Yuming JIANG
 *
 */ 

public class TaskThreePartThreeGroupingComparator extends WritableComparator {

	/**
	 * Constructor.
	 */
	protected TaskThreePartThreeGroupingComparator() {
		super(Text.class, true);
	}
	
	@Override
	public int compare(WritableComparable w1, WritableComparable w2) {
		//key format: localityName \t numberOfPhotos \t freq
		Text k1 = (Text)w1;
		Text k2 = (Text)w2;
		String key1 = k1.toString();
		String key2 = k2.toString();
		String[] key1_array = key1.split("\t");
		String[] key2_array = key2.split("\t");
		
		String natrualKey1 = key1_array[0] + "\t" + key1_array[1];
		String natrualKey2 = key2_array[0] + "\t" + key2_array[1];
		return natrualKey1.compareTo(natrualKey2);
		
	}
}
