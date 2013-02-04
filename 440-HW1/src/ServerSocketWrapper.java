import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

/** ServerSocketWrapper
 * 
 * Wrapper class for ServerSocket
 * Allows it to be started and stopped in parallel to the ProcessManager
 * accepting commands.
 * 
 * @author Tyler Healy (thealy)
 */
public class ServerSocketWrapper {

	//Thread in which the process will be run
	private Thread thread;
	
	//ServerSocket that will be accepting connections
    private ServerSocket server;
    
    //boolean that determines whether the main thread should run
    private volatile boolean running;
    
    //List of connections made to the master ProcessManager
    private ArrayList<SocketWrapper> sockets = new ArrayList<SocketWrapper>();
    
    //port, backlog for the Socket connection
    private int port;
    private int backlog;
	
    /** ServerSocketWrapper(int port, int backlog)
     * 
     * Constructor that accepts a port and backlog for the Socket connection
     * @param port - port for the Socket connection
     * @param backlog - backlog for the Socket connection
     */
	public ServerSocketWrapper(int port, int backlog) {
		this.port = port;
		this.backlog = backlog;
	}
	
	/** start()
	 * 
	 * Starts the process of accepting connections by running server.accept
	 * within a Thread
	 * @throws Exception - thrown when start is attempted and the process is
	 * 					   already running
	 */
	public synchronized void start() throws Exception {
		if (thread != null) {
			throw new Exception("ServerSocket already started.");
		}
		
		server = new ServerSocket(port, backlog);
		thread = new Thread(new Runnable() {
			/** run()
			 * 
			 * Accepts connections and adds it to the managed list
			 */
			@Override
			public void run() {
				running = true;
				while(running) {
					try {
						sockets.add(new SocketWrapper(server.accept()));
					} catch (IOException e) { }
				}
			}
		});
		
		thread.start();
	}
	
	/** stop()
	 * 
	 * Stops the ServerSocket from accepting connections and closes it
	 */
	public synchronized void stop() {
		if (thread == null) {
			return;
		}
		
		running = false;
		if (server != null) {
			try {
				server.close();
			} catch (Exception e) { }
		}
		thread = null;
	}
	
	/** getSockets()
	 * 
	 * @return a list of the connections to the master ProcessManager
	 */
	public synchronized ArrayList<SocketWrapper> getScokets() {
		return sockets;
	}
}
