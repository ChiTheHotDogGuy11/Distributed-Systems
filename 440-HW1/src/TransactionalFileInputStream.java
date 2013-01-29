import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.io.InputStream;

/** TransactionalFileInputStream
 * 
 * Class designed to safely read from file across distributed system
 * 
 * @author Tyler Healy (thealy)
 */
public class TransactionalFileInputStream extends InputStream implements Serializable {

	private int byteCounter;
	private String src;
	
	/** TransactionalFileInputStream(String src)
	 * 
	 * @param src - String indicating the location of the file that is to be read
	 */
	public TransactionalFileInputStream(String src) {
		byteCounter = 0;
		this.src = src;
	}
	
	@Override
	/** read()
	 * 
	 * Extends the abstract method in InputStream
	 * Opens a FileInputStream with the fileat src
	 * Reads a single bit from the file (or returns -1 for end of file)
	 * Closes the FileInputStream
	 * Returns the result of the read (or returns -1 for end of file)
	 */
	public int read() throws IOException {
		
		FileInputStream fis = new FileInputStream(src);
		byte[] bit = new byte[1];
	    int result = fis.read(bit, byteCounter, 1);
	    byteCounter++;
	    fis.close();
	    
	    if (result == -1) {
		  return result;
	    } else {
	    	return bit[0];
	    }
	}
}
