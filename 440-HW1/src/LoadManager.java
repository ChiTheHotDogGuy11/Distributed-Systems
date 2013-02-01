import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
					if (connections != null && connections.size() > 0) {
						int[] numProcesses = new int[connections.size()];
						for (int i = 0; i < connections.size(); i++) {
							Socket cur = connections.get(i);
							PrintWriter out = null;
							BufferedReader in = null;
							
							try {
								out = new PrintWriter(cur.getOutputStream(), true);
								in = new BufferedReader(new InputStreamReader(cur.getInputStream()));
							} catch (IOException e) {
								e.printStackTrace();
							}
							
							out.println("NumProcesses?");
							
							try {
								numProcesses[i] = in.read();
								out.close();
								in.close();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
						
						int sum = 0;
						for (int i = 0; i < numProcesses.length; i++) {
							sum += numProcesses[i];
						}
						int avg = ((sum - 1) / numProcesses.length) + 1;
						for (int i = 0; i < connections.size(); i++) {
							Socket cur = connections.get(i);
							PrintWriter out = null;
							
							try {
								out = new PrintWriter(cur.getOutputStream(), true);
							} catch (IOException e) {
								e.printStackTrace();
							}
							
							int over = numProcesses[i] - avg;
							out.println("migrate " + over);
						}
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
