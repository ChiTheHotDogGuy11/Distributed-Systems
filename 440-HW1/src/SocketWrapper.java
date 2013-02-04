import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**  SocketWrapper
 * 
 * Wrapper class for socket
 * Connects and ObjectOutputStream and ObjectInputStream to the socket
 * 
 * @author Tyler Healy (thealy)
 */
public class SocketWrapper extends Socket {
	
	//Socket that this class wraps
	private Socket sck;
	
	//Object streams for this socket
	private ObjectOutputStream out;
	private ObjectInputStream in;
	
	/** SocketWrapper(Socket sck)
	 * 
	 * Constructor for SocketWrapper
	 * @param sck - the Socket that this class wraps
	 */
	public SocketWrapper(Socket sck) {
		this.sck = sck;
		try {
			this.out = new ObjectOutputStream(this.sck.getOutputStream());
			this.in = new ObjectInputStream(this.sck.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
	
	/** getOut()
	 * 
	 * @return the ObjectOutputStream for this socket
	 */
	public ObjectOutputStream getOut() {
		return out;
	}
	
	/** getIn()
	 * 
	 * @return the ObjectInputStream for this socket
	 */
	public ObjectInputStream getIn() {
		return in;
	}
}
