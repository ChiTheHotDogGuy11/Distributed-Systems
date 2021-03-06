import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.io.InputStream;

/** TransactionalFileInputStream
 * 
 * Class designed to safely read from file across distributed system
 * @author Tyler Healy (thealy)
 */
@SuppressWarnings("serial")
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
	
	/** read()
	 * 
	 * Extends the abstract method in InputStream
	 * Opens a FileInputStream with the file at src
	 * Reads a single bit from the file (or returns -1 for end of file)
	 * Closes the FileInputStream
	 * Returns the result of the read (or returns -1 for end of file)
	 */
	@Override
	public int read() {
		
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(src);
		} catch (FileNotFoundException e1) {
			return -1;
		}
	    int result = -1;
		try {
			fis.skip(byteCounter++);
			result = fis.read();
		} catch (IOException e) {
			e.printStackTrace();
		}
	    try {
			fis.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	    
	    return result;
	}
}
