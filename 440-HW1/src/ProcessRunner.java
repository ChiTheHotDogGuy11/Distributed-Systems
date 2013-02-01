import java.util.ArrayList;

/** ProcessRunner
 * 
 * Class designed to manage and run processes in parallel with the
 * ProcessManager accepting commands
 * 
 * @author Tyler Healy (thealy)
 */
public class ProcessRunner {
	
	//Boolean to detect if thread should still run
	private volatile boolean running;
	
	//Thread on which this process runs
	private Thread thread;
	
	//List of threads being managed by the ProcessManager
	private ArrayList<MigratableProcessWrapper> processes = new ArrayList<MigratableProcessWrapper>();
	
	/** start()
	 * 
	 * Creates a thread to run the ProcessRunner
	 * Runs processes within this thread
	 * 
	 * @throws Exception - if start is run once the thread has already started
	 */
	public synchronized void start() throws Exception {
		if (thread != null) {
			throw new Exception("ProcessRunner already started.");
		}
			
		thread = new Thread(new Runnable() {
			@Override
			/** run()
			 * 
			 * The run function of the runnable object within the thread
			 * Runs the processes the ProcessManager is managing
			 */
			public void run() {
				running = true;
				while(running) {
					if (processes.size() > 0) {
						MigratableProcessWrapper cur = processes.get(0);
						try {
							cur.start();
							cur.getThread().join();
						} catch (Exception e1) {
							e1.printStackTrace();
						}
						System.out.println(cur.getName() + " has terminated");
						processes.remove(0);
					}
				}
			}
		});
		
		thread.start();
	}
	
	/** stop()
	 * 
	 * Stops the ProcessRunner by setting the running boolean to false
	 */
	public synchronized void stop() {
		for (int i = 0; i < processes.size(); i++) {
			processes.get(i).stop();
		}
		
		running = false;
		thread = null;
	}
	
	/** addThread(Thread t)
	 * 
	 * Adds a thread to the list being managed by the ProcessRunner
	 * @param t - Thread to be added to the list
	 */
	public synchronized void addThread(MigratableProcessWrapper t) {
		processes.add(t);
	}
	
	/** printProcesses()
	 * 
	 * Prints the list of processes being managed by this ProcessRunner
	 * Called as a result of a ps command to a ProcessManager
	 */
	public synchronized void printProcesses() {
		if (processes.size() == 0) {
			System.out.println("No processes running.");
			return;
		}
		
		for (int i = 0; i < processes.size(); i++) {
			MigratableProcessWrapper cur = processes.get(i);
			System.out.println(cur.getName());
		}
	}
	
	/** getLast()
	 * 
	 * Returns a Thread to be migrated
	 * @return the last Thread in the list being managed by the ProcessRunner
	 */
	@SuppressWarnings("deprecation")
	public synchronized MigratableProcessWrapper getLast() {
		if (processes.size() == 0) {
			return null;
		} 
		
		processes.get(processes.size() - 1).stop();
		return processes.remove(processes.size() - 1);
	}
	
	public synchronized int getSize() {
		return processes.size();
	}
}
