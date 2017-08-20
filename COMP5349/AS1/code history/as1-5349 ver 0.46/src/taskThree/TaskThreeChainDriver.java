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

public class TaskThreeChainDriver {
	public static void main(String[] args) throws Exception {
		
		Configuration conf = new Configuration();
		String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
		if (otherArgs.length < 3) {
			System.err.println("Usage: Task Three Chain Driver <inPlace> <inPhoto> <out> ");
			System.exit(2);
		}
		
		Path tmpTaskOneOut = new Path("tmpTaskOneOut");
		Path tmpTaskTwoOut = new Path("tmpTaskTwoOut");
		Path tmpTaskThreePartOneOut = new Path("tmpTaskThreePartOneOut");
		Path tmpTaskThreePartTwoOut = new Path("tmpTaskThreePartTwoOut");
		
		Job taskOneJob = new Job(conf, "Task One");
		DistributedCache.addCacheFile(new Path(otherArgs[0]).toUri(),taskOneJob.getConfiguration());
		taskOneJob.setJarByClass(TaskOneDriver.class);
		taskOneJob.setNumReduceTasks(3);
		taskOneJob.setMapperClass(TaskOneMapper.class);
		taskOneJob.setReducerClass(TaskOneReducer.class);
		taskOneJob.setPartitionerClass(TaskOnePartitioner.class);
		taskOneJob.setOutputKeyClass(Text.class);
		taskOneJob.setOutputValueClass(IntWritable.class);
		TextInputFormat.addInputPath(taskOneJob, new Path(otherArgs[1]));
		TextOutputFormat.setOutputPath(taskOneJob, tmpTaskOneOut);
		taskOneJob.waitForCompletion(true);
		
		Job taskTwoJob = new Job(conf, "Task Two");
		taskTwoJob.setJarByClass(TaskTwoDriver.class);
		taskTwoJob.setNumReduceTasks(1);
		taskTwoJob.setMapperClass(TaskTwoMapper.class);
		taskTwoJob.setReducerClass(TaskTwoReducer.class);
		taskTwoJob.setSortComparatorClass(DescendingComparator.class);
		taskTwoJob.setMapOutputKeyClass(IntWritable.class);
		taskTwoJob.setMapOutputValueClass(Text.class);
		taskTwoJob.setOutputKeyClass(Text.class);
		taskTwoJob.setOutputValueClass(IntWritable.class);
		TextInputFormat.addInputPath(taskTwoJob, tmpTaskOneOut);
		TextOutputFormat.setOutputPath(taskTwoJob, tmpTaskTwoOut);
		taskTwoJob.waitForCompletion(true);
		
		Job taskThreePartOneJob = new Job(conf, "Task Three Part One");
		DistributedCache.addCacheFile(new Path("/user/yjia4072/tmpTaskTwoOut/part-r-00000").toUri(),taskThreePartOneJob.getConfiguration());
		taskThreePartOneJob.setJarByClass(TaskThreePartOneDriver.class);
		taskThreePartOneJob.setNumReduceTasks(0);
		taskThreePartOneJob.setMapperClass(TaskThreePartOneMapper.class);
		taskThreePartOneJob.setOutputKeyClass(Text.class);
		taskThreePartOneJob.setOutputValueClass(Text.class);
		TextInputFormat.addInputPath(taskThreePartOneJob, new Path(otherArgs[0]));
		TextOutputFormat.setOutputPath(taskThreePartOneJob, tmpTaskThreePartOneOut);
		taskThreePartOneJob.waitForCompletion(true);
		
		Job taskThreePartTwoJob = new Job(conf, "Task Three Part Two");
		DistributedCache.addCacheFile(new Path("/user/yjia4072/tmpTaskThreePartOneOut/part-m-00000").toUri(),taskThreePartTwoJob.getConfiguration());
		taskThreePartTwoJob.setJarByClass(TaskThreePartTwoDriver.class);
		taskThreePartTwoJob.setNumReduceTasks(3);
		taskThreePartTwoJob.setMapperClass(TaskThreePartTwoMapper.class);
		taskThreePartTwoJob.setReducerClass(TaskThreePartTwoReducer.class);
		taskThreePartTwoJob.setOutputKeyClass(Text.class);
		taskThreePartTwoJob.setOutputValueClass(IntWritable.class);
		TextInputFormat.addInputPath(taskThreePartTwoJob, new Path(otherArgs[1]));
		TextOutputFormat.setOutputPath(taskThreePartTwoJob, tmpTaskThreePartTwoOut);
		taskThreePartTwoJob.waitForCompletion(true);
		
		Job taskThreePartThreeJob = new Job(conf, "Task Three Part Three");
		taskThreePartThreeJob.setNumReduceTasks(1); // we use three reducers, you may modify the number
		taskThreePartThreeJob.setJarByClass(TaskThreePartThreeDriver.class);
		taskThreePartThreeJob.setMapperClass(TaskThreePartThreeMapper.class);
		taskThreePartThreeJob.setReducerClass(TaskThreePartThreeReducer.class);
		taskThreePartThreeJob.setGroupingComparatorClass(TaskThreePartThreeGroupingComparator.class);
		taskThreePartThreeJob.setSortComparatorClass(TaskThreePartThreeComparator.class);
		taskThreePartThreeJob.setMapOutputKeyClass(Text.class);
		taskThreePartThreeJob.setMapOutputValueClass(Text.class);
		taskThreePartThreeJob.setOutputKeyClass(Text.class);
		taskThreePartThreeJob.setOutputValueClass(Text.class);
		TextInputFormat.addInputPath(taskThreePartThreeJob, tmpTaskThreePartTwoOut);
		TextOutputFormat.setOutputPath(taskThreePartThreeJob, new Path(otherArgs[2]));
		taskThreePartThreeJob.waitForCompletion(true);
		
		
		FileSystem.get(conf).delete(tmpTaskOneOut, true);
		FileSystem.get(conf).delete(tmpTaskTwoOut, true);
		FileSystem.get(conf).delete(tmpTaskThreePartOneOut, true);
		FileSystem.get(conf).delete(tmpTaskThreePartTwoOut, true);
		
	}
}
