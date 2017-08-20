package taskThree;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.filecache.DistributedCache;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;

//to use IntWritable, import by jiang
import org.apache.hadoop.io.IntWritable;

import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;
import org.apache.hadoop.fs.FileSystem;

public class TaskTwoChainDriver {
	public static void main(String[] args) throws Exception {
		
		Configuration conf = new Configuration();
		String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
		if (otherArgs.length < 3) {
			System.err.println("Usage: TaskTwoChainDriver <inPlace> <inPhoto> <out> ");
			System.exit(2);
		}
		
		Path tmpTaskOneOut = new Path("tmpTaskOneOut");
		
		Job taskOneJob = new Job(conf, "Task One");
		DistributedCache.addCacheFile(new Path(otherArgs[0]).toUri(),taskOneJob.getConfiguration());
		taskOneJob.setJarByClass(TaskOneDriver.class);
		taskOneJob.setNumReduceTasks(3);
		taskOneJob.setMapperClass(TaskOneMapper.class);
		taskOneJob.setReducerClass(TaskOneReducer.class);
		
		//set Partitioner Class
		taskOneJob.setPartitionerClass(TaskOnePartitioner.class);
		
		taskOneJob.setOutputKeyClass(Text.class);
		
		//to count the number, so the output is using IntWritable by jiang
		taskOneJob.setOutputValueClass(IntWritable.class);
		
		TextInputFormat.addInputPath(taskOneJob, new Path(otherArgs[1]));
		TextOutputFormat.setOutputPath(taskOneJob, tmpTaskOneOut);
		taskOneJob.waitForCompletion(true);
		
		Job taskTwoJob = new Job(conf, "Task Two");
		taskTwoJob.setJarByClass(TaskTwoDriver.class);
		taskTwoJob.setNumReduceTasks(1);
		taskTwoJob.setMapperClass(TaskTwoMapper.class);
		taskTwoJob.setReducerClass(TaskTwoReducer.class);
		
		//taskTwoJob.setPartitionerClass(TaskTwoPartitioner.class);
		
		//make it the descending order of the numbers 
		taskTwoJob.setSortComparatorClass(DescendingComparator.class);
		
		taskTwoJob.setMapOutputKeyClass(IntWritable.class);
		taskTwoJob.setMapOutputValueClass(Text.class);
		
		taskTwoJob.setOutputKeyClass(Text.class);
		taskTwoJob.setOutputValueClass(IntWritable.class);
		TextInputFormat.addInputPath(taskTwoJob, tmpTaskOneOut);
		TextOutputFormat.setOutputPath(taskTwoJob, new Path(otherArgs[2]));
		taskTwoJob.waitForCompletion(true);
		FileSystem.get(conf).delete(tmpTaskOneOut, true);
		
	}
}
