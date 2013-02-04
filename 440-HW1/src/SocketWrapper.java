import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class SocketWrapper extends Socket {
	
	private Socket sck;
	private ObjectOutputStream out;
	private ObjectInputStream in;
	
	public SocketWrapper(Socket sck) {
		this.sck = sck;
		try {
			this.out = new ObjectOutputStream(this.sck.getOutputStream());
			this.in = new ObjectInputStream(this.sck.getInputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	
	public ObjectOutputStream getOut() {
		return out;
	}
	
	public ObjectInputStream getIn() {
		return in;
	}
}
