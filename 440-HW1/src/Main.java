import java.io.IOException;


public class Main {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		ProcessManager pm = new ProcessManager();
		pm.receiveCommands();
	}

}
