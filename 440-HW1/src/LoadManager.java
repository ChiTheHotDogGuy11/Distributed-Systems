import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
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
					ArrayList<Socket> connections = ssw.getScokets();
					PrintWriter out = null;
					BufferedReader in = null;
					
					if (connections != null && connections.size() > 0) {
						int[] numProcesses = new int[connections.size()];
						for (int i = 0; i < connections.size(); i++) {
							Socket cur = connections.get(i);
							if (!cur.isClosed()) {
								try {
									out = new PrintWriter(cur.getOutputStream(), true);
									in = new BufferedReader(new InputStreamReader(cur.getInputStream()));
									out.println("NumProcesses?");
									String response = in.readLine();
									numProcesses[i] = Integer.parseInt(response);
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
						ArrayList<Thread> migrations = new ArrayList<Thread>();
						for (int i = 0; i < connections.size(); i++) {
							Socket cur = connections.get(i);
							if (!cur.isClosed()) {
								int over = numProcesses[i] - avg;
								if (over > 0) {
									out.println("migrate " + over);
								}
								for(int j = 1; j <= over; j++) {
									ObjectInputStream oin;
									Thread obj = null;
									try {
										oin = new ObjectInputStream(cur.getInputStream());
										obj = (Thread) oin.readObject();
										oin.close();
									} catch (IOException e) {
										e.printStackTrace();
									} catch (ClassNotFoundException e) {
										e.printStackTrace();
									}
									
									if (obj != null) {
										migrations.add(obj);
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
							
							ObjectOutputStream ob_out = null;
							Socket cur = connections.get(low_j);
							
							if (!cur.isClosed()) {
								try {
									out = new PrintWriter(cur.getOutputStream());
									ob_out = new ObjectOutputStream(cur.getOutputStream());
									
									out.println("incoming");
									Thread.sleep(50);
									ob_out.writeObject(migrations.get(i));
									
									ob_out.close();
								} catch (IOException e) {
									e.printStackTrace();
								} catch (InterruptedException e) {
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
