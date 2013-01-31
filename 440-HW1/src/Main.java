import java.io.IOException;


public class Main {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws IOException, InterruptedException {
		ProcessManager pm = new ProcessManager();
		pm.receiveCommands();
	}

}
