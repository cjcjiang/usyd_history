package comp5349.mrsim;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * CSV file iterator.
 */
class FileScanner implements Iterator<String> {

    BufferedReader reader = null;

    public FileScanner(String filename) throws FileNotFoundException {
        
        //use buffering, reading one line at a time
        reader = new BufferedReader(new FileReader(filename));
    }

    @Override
    public String next() {
    	String res = null;
    	if(hasNext()){
    		try {
				res = reader.readLine();
			} catch (IOException e) {
				handleException(e);
			}
    	}
    	return res;
    }

    @Override
    public boolean hasNext() {
    	boolean res = false;
    	try {
			res = reader.ready();
		} catch (IOException e) {
            handleException(e);
		}
    	return res;
    }


    @Override
    protected void finalize() throws Throwable {
        try {
            if (reader != null) {
                reader.close();  // close opened file
                reader = null;
            }
        } finally {
            super.finalize();
        }
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    private void handleException(Exception ex){
    	System.err.println("Problem reading file: " + ex);
    }
}
