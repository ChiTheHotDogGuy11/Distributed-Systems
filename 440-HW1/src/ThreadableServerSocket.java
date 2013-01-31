import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ThreadableServerSocket implements Runnable{

	private ServerSocket ss;
	
	public ThreadableServerSocket(int port, int backlog) throws IOException {
		ss = new ServerSocket(port, backlog);
	}
	
	@Override
	public void run() {
		Socket conn = null;
		try {
			while(true) {
				conn = ss.accept();
				System.out.println(conn.getInetAddress());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
