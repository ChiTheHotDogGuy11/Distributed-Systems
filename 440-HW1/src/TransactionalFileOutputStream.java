import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;


public class TransactionalFileOutputStream extends OutputStream implements Serializable {
	private String src;
	private boolean append;
	
	public TransactionalFileOutputStream(String src, boolean append) {
		this.src = src;
		this.append = append;
	}
	
	@Override
	public void write(int inputByte) throws IOException {
		FileOutputStream fos = new FileOutputStream(src, append);
	    fos.write(inputByte);
	    fos.close();
	    append = true;
	}
}
