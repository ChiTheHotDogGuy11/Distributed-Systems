import java.io.Serializable;

/** MigratableProcesssWrapper
 * 
 * Wrapper class for MigratableProcess
 * Allows the name of the process to be stored with the process
 * Allows the process to run in a separate thread
 * 
 * @author Tyler Healy (thealy)
 */
@SuppressWarnings("serial")
public class MigratableProcessWrapper implements Serializable {

	//Thread in which the process will be run
	private Thread thread;
    
	//The process to be run
    private MigratableProcess mp;
    
    //The name of the process (with arguments)
    private String name;
    
    //Boolean to determine if this is the process has started already
    private volatile boolean hasStarted = false;
    
    /** MigratableProcessWrapper(MigratableProcess mp)
     * 
     * Constructor for the MigratableProcessWrapper
     * @param mp - The MigratableProcess to be run within this wrapper
     */
    public MigratableProcessWrapper(MigratableProcess mp) {
    	this.mp = mp;
    }
    
    /**
     * Runs the MigratableProcess mp in a separate thread
     * @throws Exception - thrown when start is attempted and the process is
	 * 					   already running
     */
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
    
    /** stop()
     * 
     * Suspens the MigratableProcess and stops the thread in which it is
     * running
     */
    public synchronized void stop() {
		if (thread == null) {
			return;
		}
		
		mp.suspend();
		thread = null;
	}
    
    /** getThread()
     * 
     * Allows other processes to join on this thread
     * @return the thread in which the MigratableProcess is running
     */
    public synchronized Thread getThread() {
    	return thread;
    }
    
    /** getProcess()
     * 
     * @return the MigratableProcess contained in this class
     */
    public synchronized MigratableProcess getProcess() {
    	return mp;
    }
    
    public synchronized String getName() {
    	return name;
    }
    
    /** setName()
     * 
     * Sets the name of this process to the input name
     * @param name - String input to be the new name of the process
     */
    public synchronized void setName(String name) {
    	this.name = name;
    }
}
