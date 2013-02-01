import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;

/** TransactionalFileOutputStream
 * 
 * Class designed to safely write to file across a distributed system
 * @author Tyler Healy (thealy)
 */
@SuppressWarnings("serial")
public class TransactionalFileOutputStream extends OutputStream implements Serializable {
	
	private String src;
	private boolean append;
	
	/** TransactionalFileOutputStream(String src, boolean append)
	 * 
	 * Constructor that takes a file and a boolean that represents whether or
	 * not to append to a file already at src
	 * @param src - Location of file to write to
	 * @param append - boolean that is true if write should append to file
	 * 				   already at src
	 */
	public TransactionalFileOutputStream(String src, boolean append) {
		this.src = src;
		this.append = append;
	}
	
	@Override
	/** write()
	 * 
	 * Safely writes across a distributed system by:
	 * Opening a FileOutputStream
	 * Writing the byte
	 * Closing the FileOutputStream
	 * Appending happens for every write after the first write
	 */
	public void write(int inputByte) throws IOException {
		FileOutputStream fos = new FileOutputStream(src, append);
	    fos.write(inputByte);
	    fos.close();
	    append = true;
	}
}
