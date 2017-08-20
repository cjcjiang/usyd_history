package taskThree;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.filecache.DistributedCache;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;

import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

/**
 * This job called "TaskThreePartOne" is designed to get the top 50 localityName's place-id.
 * 
 * This TaskThreePartOneDriver is designed to trigger TaskThreePartOneMapper.
 * No reducer, combiner, sort comparator, grouping comparator or partitioner is used.
 * 
 * @author Yuming JIANG
 *
 */

public class TaskThreePartOneDriver {
	public static void main(String[] args) throws Exception {
		
		Configuration conf = new Configuration();
		String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
		if (otherArgs.length < 3) {
			System.err.println("Usage: TaskThreePartOneDriver <TaskTwoOutput> <inPlace> <out> ");
			System.exit(2);
		}
		
		Job job = new Job(conf, "Task Three Part One");
		//The final result of Task Two will be loaded into the distributedCache.
		//The final result of Task Two only contains 50 lines of data.
		//It is small enough to be loaded into the distributedCache.
		DistributedCache.addCacheFile(new Path(otherArgs[0]).toUri(),job.getConfiguration());
		job.setJarByClass(TaskThreePartOneDriver.class);
		job.setNumReduceTasks(0);
		job.setMapperClass(TaskThreePartOneMapper.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		TextInputFormat.addInputPath(job, new Path(otherArgs[1]));
		TextOutputFormat.setOutputPath(job, new Path(otherArgs[2]));
		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}
}
