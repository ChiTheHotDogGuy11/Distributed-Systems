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
	private ArrayList<Thread> threads = new ArrayList<Thread>();
	
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
					if (threads.size() > 0) {
						Thread cur = threads.get(0);
						cur.start();
						try {
							cur.join();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						System.out.println(cur.getName() + " has terminated");
						threads.remove(0);
					}
				}
			}
		});
		
		thread.start();
	}
	
	public synchronized void stop() {
		for (int i = 0; i < threads.size(); i++) {
			threads.get(i).interrupt();
		}
		
		running = false;
		thread = null;
	}
	
	public synchronized void addThread(Thread t) {
		threads.add(t);
	}
	
	public synchronized void printProcesses() {
		if (threads.size() == 0) {
			System.out.println("No processes running.");
			return;
		}
		
		for (int i = 0; i < threads.size(); i++) {
			Thread cur = threads.get(i);
			System.out.println(cur.getName());
		}
	}
	
	public synchronized Thread getLast() {
		if (threads.size() == 0) {
			return null;
		} 
		
		threads.get(threads.size() - 1).suspend();
		return threads.remove(threads.size() - 1);
	}
}
