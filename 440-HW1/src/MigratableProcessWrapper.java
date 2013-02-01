
public class MigratableProcessWrapper {

	//Thread in which the process will be run
	private Thread thread;
    
    private MigratableProcess mp;
    
    private String name;
    
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
    
    public synchronized Thread getThread() {
    	return thread;
    }
    
    public synchronized MigratableProcess getProcess() {
    	return mp;
    }
    
    public synchronized String getName() {
    	return name;
    }
    
    public synchronized void setName(String name) {
    	this.name = name;
    }
}
