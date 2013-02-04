import java.io.IOException;
import java.util.ArrayList;


public class LoadManager {
	
	//Thread in which the process will be run
	private Thread thread;
	
	//boolean that determines whether the main thread should run
    private volatile boolean running;
    
    private ServerSocketWrapper ssw;
    
    public LoadManager(ServerSocketWrapper ssw) {
    	this.ssw = ssw;
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
			throw new Exception("LoadManager already started.");
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
					ArrayList<SocketWrapper> connections = ssw.getScokets();
					int size;
					
					if (connections != null && (size = connections.size()) > 0) {
						int[] numProcesses = new int[connections.size()];
						for (int i = 0; i < connections.size(); i++) {
							SocketWrapper cur = connections.get(i);
							if (!cur.isClosed()) {
								try {
									cur.getOut().writeObject("NumProcesses?");
									Object response = null;
									try {
										response = cur.getIn().readObject();
									} catch (ClassNotFoundException e) {
										e.printStackTrace();
									}
									numProcesses[i] = (Integer) response;
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
						}
						
						int sum = 0;
						for (int i = 0; i < numProcesses.length; i++) {
							sum += numProcesses[i];
						}
						int avg = ((sum - 1) / numProcesses.length) + 1;
						ArrayList<MigratableProcessWrapper> migrations = new ArrayList<MigratableProcessWrapper>();
						for (int i = 0; i < connections.size(); i++) {
							SocketWrapper cur = connections.get(i);
							if (!cur.isClosed()) {
								int over = numProcesses[i] - avg;
								for(int j = 1; j <= over; j++) {
									Object nm = null;
									Object obj = null;
									try {
										cur.getOut().writeObject("migrate");
										nm = cur.getIn().readObject();
										obj = cur.getIn().readObject();
									} catch (IOException e) {
										e.printStackTrace();
									} catch (ClassNotFoundException e) {
										e.printStackTrace();
									}
									
									if (nm != null && obj != null) {
										MigratableProcessWrapper mpw = new MigratableProcessWrapper((MigratableProcess) obj);
										mpw.setName((String) nm);
										migrations.add(mpw);
									}
								}
							}
						}
						
						
						for (int i = 0; i < migrations.size(); i++) {
							int leastProcesses = -1;
							int low_j = 0;
							
							for(int j = 0; j < numProcesses.length; j++) {
								if (leastProcesses == -1 || numProcesses[j] < leastProcesses) {
									leastProcesses = numProcesses[j];
									low_j = j;
								}
							}
							
							SocketWrapper cur = connections.get(low_j);
							
							if (!cur.isClosed()) {
								try {
									cur.getOut().writeObject(migrations.get(i));
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
						}
					}
					try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
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
		thread = null;
	}
}
