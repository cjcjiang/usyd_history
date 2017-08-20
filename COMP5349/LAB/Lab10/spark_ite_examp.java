import java.util.Arrays;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.SparkConf;

public class PiIterativeProcessing {
	public static void main(String[] args) {
		final SparkConf conf = new SparkConf().setAppName("Pi Iterative Processing");
		final JavaSparkContext sc = new JavaSparkContext(conf);
		final int numIters = 100;
		
		JavaRDD<Integer> count = sc.parallelize(Arrays.asList(0));
		for(int i = 0; i < numIters; i++) {
			count = count.map(c -> {
				double x = Math.random();
				double y = Math.random();
				return c + ((x * x + y * y < 1) ? 1 : 0);
				});
		}
		System.out.println(count.collect().get(0) / (double) numIters * 4);
	}
}