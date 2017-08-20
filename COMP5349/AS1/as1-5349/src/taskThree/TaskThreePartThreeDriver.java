package taskThree;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;

import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

/**
 * This job called "TaskThreePartThree" is designed to get the
 * final result of task three.
 * 
 * This TaskThreePartThreeDriver is designed to wire TaskThreePartThreeMapper and TaskThreePartThreeReducer.
 * As the final result will only contain 50 lines of data, it will not be too big,
 * so only 1 reducer, TaskThreePartThreeReducer, is used. 
 * 
 * "TaskThreePartThreeGroupingComparator" is used as the grouping comparator.
 * "TaskThreePartThreeComparator" is used as the sort comparator.
 * No combiner or partitioner is used.
 * 
 * @author Yuming JIANG
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
		job.setNumReduceTasks(1); 
		job.setJarByClass(TaskThreePartThreeDriver.class);
		job.setMapperClass(TaskThreePartThreeMapper.class);
		job.setReducerClass(TaskThreePartThreeReducer.class);
		
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
