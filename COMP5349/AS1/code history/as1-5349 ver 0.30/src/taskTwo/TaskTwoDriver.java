package taskTwo;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;

import org.apache.hadoop.io.IntWritable;

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
public class TaskTwoDriver {

	public static void main(String[] args) throws Exception {
		
		Configuration conf = new Configuration();
		String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
		if (otherArgs.length != 2) {
			System.err.println("Usage: TaskTwoDriver <in> <out>");
			System.exit(2);
		}
		Job job = new Job(conf, "Task Two");
		job.setNumReduceTasks(3); // we use three reducers, you may modify the number
		job.setJarByClass(TaskTwoDriver.class);
		job.setMapperClass(TaskTwoMapper.class);
		job.setReducerClass(TaskTwoReducer.class);
		
		job.setPartitionerClass(TaskTwoPartitioner.class);
		
		//make it the descending order of the numbers 
		job.setSortComparatorClass(DescendingComparator.class);
		
		job.setMapOutputKeyClass(IntWritable.class);
		job.setMapOutputValueClass(Text.class);
		
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);
		TextInputFormat.addInputPath(job, new Path(otherArgs[0]));
		TextOutputFormat.setOutputPath(job, new Path(otherArgs[1]));
		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}
}
