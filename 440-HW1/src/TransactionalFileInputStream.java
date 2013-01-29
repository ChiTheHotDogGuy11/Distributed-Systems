import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.io.InputStream;


public class TransactionalFileInputStream extends InputStream implements Serializable {

	private int byteCounter;
	private String src;
	
	public TransactionalFileInputStream(String src) {
		byteCounter = 0;
		this.src = src;
	}
	
	@Override
	public int read() throws IOException {
		// TODO Auto-generated method stub
		
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
