
public class ProcessChild {
	
	private MigratableProcessWrapper mpw;
	
	private Thread thread;
	
	private boolean isComplete = false;
	private boolean isTerminated = false;
	
	public ProcessChild(MigratableProcessWrapper mpw) {
		this.mpw = mpw;
	}
	
	public synchronized void start() throws Exception {
		if (thread != null) {
			throw new Exception("Process has already started.");
		}
		
		thread = new Thread(new Runnable() {
			@Override
			/** run()
			 * 
			 * The run function of the runnable object within the thread
			 * Runs the processes the ProcessManager is managing
			 */
			public void run() {
				try {
					mpw.start();
					mpw.getThread().join();
					isComplete = true;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		thread.start();
	}
	
	public synchronized void stop() {
		if (thread == null) {
			return;
		}
		
		mpw.stop();
		isTerminated = true;
		thread = null;
	}
	
	public synchronized MigratableProcessWrapper getMPW() {
		return mpw;
	}
	
	public synchronized boolean isComplete() {
		return isComplete;
	}
	
	public synchronized boolean isTerminated() {
		return isTerminated;
	}
}
