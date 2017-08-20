package taskThree;

import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;

import org.apache.hadoop.io.Text;

/**
 * Groups values based on 
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
		String localityName1 = key1_array[0];
		String localityName2 = key2_array[0];
		return localityName1.compareTo(localityName1);
	}
}
