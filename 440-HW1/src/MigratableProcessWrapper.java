
public class MigratableProcessWrapper {

	//Thread in which the process will be run
	private Thread thread;
    
    private MigratableProcess mp;
    
    public MigratableProcessWrapper(MigratableProcess mp) {
    	this.mp = mp;
    }
    
    public synchronized void start() throws Exception {
		if (thread != null) {
			throw new Exception("Process has already started.");
		}
		
		thread = new Thread(mp);
		
		thread.start();
	}
    
    public synchronized void stop() {
		if (thread == null) {
			return;
		}
		
		mp.suspend();
		thread = null;
	}
}
