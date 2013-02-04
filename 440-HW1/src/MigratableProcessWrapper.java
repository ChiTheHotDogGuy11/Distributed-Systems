import java.io.Serializable;

public class MigratableProcessWrapper implements Serializable {

	//Thread in which the process will be run
	private Thread thread;
    
    private MigratableProcess mp;
    
    private String name;
    
    private volatile boolean hasStarted = false;
    
    public MigratableProcessWrapper(MigratableProcess mp) {
    	this.mp = mp;
    }
    
    public synchronized void start() throws Exception {
		if (thread != null) {
			throw new Exception("Process has already started.");
		}
		
		thread = new Thread(mp);
		
		if (hasStarted == false) {
			thread.start();
			hasStarted = true;
		} else {
			mp.run();
		}
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
