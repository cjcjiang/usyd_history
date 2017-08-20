package comp5349.mrsim;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;

/**
 * This is the main program that scans through a given file and
 * calls your filtering class for each line.
 *
 * Based on your filtering rules, a Record<K,V> (or null) is output by your filter.
 */
class MapReduceSimulator {

	public static void main(String[] args) {
		String filename = "data.csv";
		if (args.length > 0){
			filename = args[0];
		}
		int min_filmid = 1;
		if (args.length > 1){
			min_filmid = Integer.parseInt(args[1]);
		}
		int max_filmid = 100;
		if (args.length > 2){
			max_filmid = Integer.parseInt(args[2]);
		}

		RatingFilter filter  = new RatingFilter(min_filmid, max_filmid);
		RatingReducer reducer= new RatingReducer();
	    int[] stats = MapReduceSimulator.<String, Integer, Float>mapreduce(filename, filter, reducer);

		// print some statistics
		System.out.println("MapReduce Simulator using mapper:  " + filter);
		System.out.println("MapReduce Simulator using reducer: " + reducer);
		System.out.println("Lines in File:    " + filter.getNumCalls() + " records (should be: "+ stats[0] + ")");
		System.out.println("Filtered records: " + filter.getNumRecords() + " (should be: "+ stats[1] + ")");
	}

	public static <K extends Comparable<K>, V, S> int[] mapreduce(String filename, Mapper<String, K,V> mapper, Reducer<K,V,S> reducer ) {
		//Map from key to Record
		Map<K, Record<K,V>> groupByKey = new TreeMap<>();

		int numLines = 0, numRecords = 0;
		try {
			FileScanner input = new FileScanner(filename);
			while (input.hasNext()) {
				numLines++;
				Record<K, V> result = mapper.map( input.next() );
				if ( result != null){
					numRecords++;
					Record<K,V> rec = groupByKey.get(result.getKey());
					if(rec == null){
						rec = result;
						groupByKey.put(rec.getKey(), rec);
					}else{
						rec.appendValues(result.getValues());
					}

				}
			}
		}catch (FileNotFoundException e) {
			System.out.println("File not found: " + filename);
			System.exit(1);
		}


		// now lets call the 'reducer'
		boolean someResult = false;
		for(Record<K, V> rec : groupByKey.values()){
			S res = reducer.reduce(rec.getKey(), rec.getValues());
			System.out.println(rec.getKey() + ": " + res);
			someResult |= res != null;
		}

		if (!someResult) System.out.println("<empty result>");

		return new int[]{numLines, numRecords};
	}
}
