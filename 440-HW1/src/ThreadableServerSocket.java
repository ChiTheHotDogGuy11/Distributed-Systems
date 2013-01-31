import java.io.IOException;
import java.net.ServerSocket;

public class ThreadableServerSocket implements Runnable{

	private ServerSocket ss;
	
	public ThreadableServerSocket(int port, int backlog) throws IOException {
		ss = new ServerSocket(port, backlog);
	}
	
	@Override
	public void run() {
		try {
			ss.accept();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
