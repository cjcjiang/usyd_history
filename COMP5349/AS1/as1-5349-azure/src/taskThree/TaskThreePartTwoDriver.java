package taskThree;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.filecache.DistributedCache;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.IntWritable;

import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

/**
 * This job called "TaskThreePartTwo" is designed to get the
 * total number of the occurrence of each tag of each locality.
 * 
 * This TaskThreePartTwoDriver is designed to wire TaskThreePartTwoMapper and TaskThreePartTwoReducer.
 * 3 reducers, TaskThreePartTwoReducer, are used. 
 * TaskThreePartTwoReducer is also used as a combiner to accelerate this job.
 * No sort comparator, grouping comparator or partitioner is used.
 * 
 * @author Yuming JIANG
 *
 */

public class TaskThreePartTwoDriver {
	public static void main(String[] args) throws Exception {
		
		Configuration conf = new Configuration();
		String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
		if (otherArgs.length < 3) {
			System.err.println("Usage: Task Three Part Two Driver <inPartOneOutput> <inPhoto> <out> ");
			System.exit(2);
		}
		
		Job job = new Job(conf, "Task Three Part Two");
		//The final result of TaskThreePartOne will be loaded into the distributedCache.
		//The final result of TaskThreePartOne only contains 50 lines of data.
		//It is small enough to be loaded into the  distributedCache.
		DistributedCache.addCacheFile(new Path(otherArgs[0]).toUri(),job.getConfiguration());
		job.setJarByClass(TaskThreePartTwoDriver.class);
		job.setNumReduceTasks(3);
		job.setMapperClass(TaskThreePartTwoMapper.class);
		job.setCombinerClass(TaskThreePartTwoReducer.class);
		job.setReducerClass(TaskThreePartTwoReducer.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);
		
		TextInputFormat.addInputPath(job, new Path(otherArgs[1]));
		TextOutputFormat.setOutputPath(job, new Path(otherArgs[2]));
		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}
}
