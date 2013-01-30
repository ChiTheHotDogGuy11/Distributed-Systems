import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;


public class TransactionalFileOutputStream extends OutputStream implements Serializable {
	private int byteCounter;
	private String src;
	private boolean append;
	
	public TransactionalFileOutputStream(String src, boolean append) {
		byteCounter = 0;
		this.src = src;
		this.append = append;
	}
	
	@Override
	public void write(int inputByte) throws IOException {
		// TODO Auto-generated method stub
		
		FileOutputStream fos = new FileOutputStream(src, append);
		byte[] bit = new byte[1];
		bit[0] = (byte)inputByte;
	    fos.write(bit, byteCounter, 1);
	    byteCounter++;
	    fos.close();
	    
	}
}
