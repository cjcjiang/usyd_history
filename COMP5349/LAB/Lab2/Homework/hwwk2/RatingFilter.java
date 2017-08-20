package comp5349.mrsim;


/**
 * Example of a RatingFilter that does nothing at the moment.
 * Your task is to finish the constructor, so that the search range of film ids can be configured,
 * and to implement the filter method so that ratings of films in the given range are filtered.
 * The filter method shall return a Record that contains a key and a rating value
 * for further processing by our MR-simulator.
 */
public class RatingFilter extends Filter<String, String, Integer>{

    public RatingFilter(int min_filmid, int max_filmid) {
    	// to be done
    }

    public Record<String, Integer> filter ( String line ) {
    	// to be done
    	return null;
    }

}
