import java.io.IOException;

/** SlaveListener
 * 
 * Process run by a slave ProcessManager
 * Listens to requests and objects sent by the master ProcessManager
 * 
 * @author Tyler Healy (thealy)
 */
public class SlaveListener {
	
	//Thread in which the process will be run
	private Thread thread;
	
	//boolean that determines whether the main thread should run
    private volatile boolean running;
    
    /* SocketWrapper containing the socket connection between this
     * slave ProcessManager and its master
     */
    private SocketWrapper sck;
    
    // The ProcessManager for which this class is listening
    private ProcessManager pm;
    
    /** SlaveListener(SocketWrapper sck, ProcessManager pm)
     * 
     * @param sck - SocketWrapper containing the socket connection bewteen
     *              this slave ProcessManager and its master
     * @param pm - The ProcessManager for which this class is listening
     */
    public SlaveListener(SocketWrapper sck, ProcessManager pm) {
    	this.sck = sck;
    	this.pm = pm;
    }
    
    /** start()
     * 
     * Starts the process of listening for and responding to requests
     * from the master ProcessManager
     * @throws Exception - thrown when start is attempted and the process is
	 * 					   already running
     */
    public synchronized void start() throws Exception {
		if (thread != null) {
			throw new Exception("Slave Listener already started.");
		}
		
		thread = new Thread(new Runnable() {
			/** run()
			 * 
			 * Listens for requests from the ProcessManager
			 * Interprets the requests and responds to them
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
    
    /** stop()
     * 
     * Stops the process of listening for requests
     */
    public synchronized void stop() {
		if (thread == null) {
			return;
		}
		
		running = false;
		thread = null;
	}
}
