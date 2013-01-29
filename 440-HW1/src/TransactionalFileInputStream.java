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
		
		return 0;
	}

}
