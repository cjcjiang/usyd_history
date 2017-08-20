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
 * This job called "taskOne" is designed to get the number of photos taken per locality
 *
 * The TaskOneDeiver is designed to trigger TaskOneMapper, TaskOneReducer, TaskOnePartitioner
 * This driver also configures a combiner which uses the same algorithm as TaslOneReducer to make 
 * the job more efficiently
 */
public class TaskOneDriver {
	public static void main(String[] args) throws Exception {
		
		Configuration conf = new Configuration();
		String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
		if (otherArgs.length < 3) {
			System.err.println("Usage: TaskOneDriver <inPlace> <inPhoto> <out> ");
			System.exit(2);
		}
		
		Job job = new Job(conf, "Task One");
		DistributedCache.addCacheFile(new Path(otherArgs[0]).toUri(),job.getConfiguration());
		job.setJarByClass(TaskOneDriver.class);
		job.setNumReduceTasks(3);
		job.setMapperClass(TaskOneMapper.class);
		
		job.setCombinerClass(TaskOneReducer.class);
		
		job.setReducerClass(TaskOneReducer.class);
		
		//set Partitioner Class
		job.setPartitionerClass(TaskOnePartitioner.class);
		
		job.setOutputKeyClass(Text.class);
		
		//to count the number, so the output is using IntWritable
		job.setOutputValueClass(IntWritable.class);
		TextInputFormat.addInputPath(job, new Path(otherArgs[1]));
		TextOutputFormat.setOutputPath(job, new Path(otherArgs[2]));
		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}
}
