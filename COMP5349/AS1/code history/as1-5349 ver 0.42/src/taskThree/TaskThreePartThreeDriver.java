package taskThree;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;

import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

/**
 * A simple driver class to wire TagMapper and TagReducer
 * No combiner is used.
 * @author Ying Zhou
 *
 */
public class TaskThreePartThreeDriver {

	public static void main(String[] args) throws Exception {
		
		Configuration conf = new Configuration();
		String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
		if (otherArgs.length != 2) {
			System.err.println("Usage: Task Three Part Three Driver <in> <out>");
			System.exit(2);
		}
		Job job = new Job(conf, "Task Three Part Three");
		job.setNumReduceTasks(3); // we use three reducers, you may modify the number
		job.setJarByClass(TaskThreePartThreeDriver.class);
		job.setMapperClass(TaskThreePartThreeMapper.class);
		job.setReducerClass(TaskThreePartThreeReducer.class);
		
		job.setPartitionerClass(TaskThreePartThreePartitioner.class);
		job.setGroupingComparatorClass(TaskThreePartThreeGroupingComparator.class);
		job.setSortComparatorClass(TaskThreePartThreeComparator.class);
		
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(Text.class);
		
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		TextInputFormat.addInputPath(job, new Path(otherArgs[0]));
		TextOutputFormat.setOutputPath(job, new Path(otherArgs[1]));
		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}
}
