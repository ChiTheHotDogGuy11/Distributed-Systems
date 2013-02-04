import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class SlaveListener {
	
	//Thread in which the process will be run
	private Thread thread;
	
	//boolean that determines whether the main thread should run
    private volatile boolean running;
    
    private SocketWrapper sck;
    private ProcessManager pm;
    
    public SlaveListener(SocketWrapper sck, ProcessManager pm) {
    	this.sck = sck;
    	this.pm = pm;
    }
    
    public synchronized void start() throws Exception {
		if (thread != null) {
			throw new Exception("Slave Listener already started.");
		}
		
		thread = new Thread(new Runnable() {
			/** run()
			 * 
			 * Accepts connections and adds it to the managed list
			 */
			@Override
			public void run() {
				running = true;
				while(running) {
					if (!sck.isClosed()) {
						Object request = null;
						
						try {
							request = sck.getIn().readObject();
						} catch (IOException e) {
							e.printStackTrace();
						} catch (ClassNotFoundException e) {
							e.printStackTrace();
						}
						
						if (request.equals("NumProcesses?")) {
							try {
								sck.getOut().writeObject(pm.getNumProcesses());
							} catch (IOException e) {
								e.printStackTrace();
							}
						} else if (request.equals("migrate")) {
							try {
								pm.migrate();
							} catch (IOException e) {
								e.printStackTrace();
							}
						} else {
							pm.addProcess((MigratableProcessWrapper) request);
						}
					}
				}
			}
		});
		
		thread.start();
	}
    
    public synchronized void stop() {
		if (thread == null) {
			return;
		}
		
		running = false;
		thread = null;
	}
}
