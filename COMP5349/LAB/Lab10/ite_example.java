import org.apache.flink.api.java.*;
import org.apache.flink.api.java.operators.*;

public class PiIterativeProcessing {
	public static void main(String[] args) throws Exception {
		final ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
		final int numIters = 100000;
		IterativeDataSet<Integer> initial = env.fromElements(0).iterate(numIters);
		DataSet<Integer> iteration = initial.map(i -> {
			double x = Math.random();
			double y = Math.random();
			return i + ((x * x + y * y < 1) ? 1 : 0);
		});
		DataSet<Integer> count = initial.closeWith(iteration);
		count.map(c -> c / (double) numIters * 4).print();
	}
}