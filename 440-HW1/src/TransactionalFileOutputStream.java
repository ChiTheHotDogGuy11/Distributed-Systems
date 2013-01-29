import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;


public class TransactionalFileOutputStream extends OutputStream implements Serializable {
	private int byteCounter;
	private String src;
	
	public TransactionalFileOutputStream(String src) {
		byteCounter = 0;
		this.src = src;
	}
	
	@Override
	public void write(int inputByte) throws IOException {
		// TODO Auto-generated method stub
		
		FileOutputStream fos = new FileOutputStream(src);
		byte[] bit = new byte[1];
		bit[0] = (byte)inputByte;
	    fos.write(bit, byteCounter, 1);
	    byteCounter++;
	    fos.close();
	    
	}
}
