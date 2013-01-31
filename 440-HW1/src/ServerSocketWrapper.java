import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerSocketWrapper {

	private Thread thread;
    private ServerSocket server;
    private volatile boolean running;
    
    private int port;
    private int backlog;
	
	public ServerSocketWrapper(int port, int backlog) {
		this.port = port;
		this.backlog = backlog;
	}
	
	public synchronized void start() throws Exception {
		if (thread != null) {
			throw new Exception("ServerSocket already started.");
		}
		
		server = new ServerSocket(port, backlog);
		thread = new Thread(new Runnable() {
			@Override
			public void run() {
				running = true;
				while(running) {
					try {
						server.accept();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		});
	}
	
	public synchronized void stop() {
		if (thread == null) {
			return;
		}
		
		running = false;
		if (server != null) {
			try {
				server.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		thread = null;
	}	
}
