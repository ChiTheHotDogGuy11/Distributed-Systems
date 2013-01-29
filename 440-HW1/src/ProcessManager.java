import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;


public class ProcessManager {
	
	private Queue processes;
	private int numProcesses;
	
	public String migrate() throws IOException {
		MigratableProcess pToMigrate = processes.dequeue();
		pToMigrate.suspend();
		String fileName = "hmmKay";
        FileOutputStream fileOut = new FileOutputStream(fileName);
        ObjectOutputStream out = new ObjectOutputStream(fileOut);
        out.writeObject(pToMigrate);
        out.close();
        fileOut.close();
        return fileName;
	}
	
	public void runProcess(String inputObj) {
		
	}
	
	public void addProcess(String newProcess) {
		processes.enqueue(newProcess);
	}
}
