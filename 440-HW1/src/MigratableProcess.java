import java.lang.Runnable;
import java.io.Serializable;

/** MigrateableProcess
 * 
 * Interface for processes that can be run in a thread and serialized to be
 * sent to other ProcessManagers
 * 
 * @author Tyler Healy (thealy)
 */
public interface MigratableProcess extends Runnable, Serializable {

	/** suspend()
	 * 
	 *  Called before the object is serialized to allow an opportunity for the
	 *  process to enter a known safe state
	 *  
	 *  Make sure reads/writes are complete
	 */
	public void suspend();
	
	@Override
	/** toString()
	 * 
	 * @return the class name of the process as well as the original set of
	 * arguments with which it was called
	 */
	public String toString();
}
