import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class SlaveListener {
	
	//Thread in which the process will be run
	private Thread thread;
	
	//boolean that determines whether the main thread should run
    private volatile boolean running;
    
    private Socket sck;
    private ProcessManager pm;
    
    public SlaveListener(Socket sck, ProcessManager pm) {
    	this.sck = sck;
    	this.pm = pm;
    }
    
    public synchronized void start() throws Exception {
		if (thread != null) {
			throw new Exception("Slave Listener already started.");
		}
		
		thread = new Thread(new Runnable() {
			/** run()
			 * 
			 * Accepts connections and adds it to the managed list
			 */
			@Override
			public void run() {
				running = true;
				while(running) {
					if (!sck.isClosed()) {
						PrintWriter out = null;
						BufferedReader in = null;
						ObjectInputStream oin = null;
						String request = "";
						
						try {
							out = new PrintWriter(sck.getOutputStream(), true);
							in = new BufferedReader(new InputStreamReader(sck.getInputStream()));
							request = in.readLine();
						} catch (IOException e) {
							e.printStackTrace();
						}
						
						if (request.equals("NumProcesses?")) {
							out.println(pm.getNumProcesses());
						} else if (request.equals("incoming")) {
							MigratableProcess newProcess = null;
							MigratableProcessWrapper mpw = null;
							try {
								oin = new ObjectInputStream(sck.getInputStream());
								newProcess = (MigratableProcess) oin.readObject();
								oin.close();
								mpw = new MigratableProcessWrapper(newProcess);
								pm.addProcess(mpw);
							} catch (IOException e) {
								e.printStackTrace();
							} catch (ClassNotFoundException e) {
								e.printStackTrace();
							}
						} else {
							try { 
								String[] reqArray = request.split(" ");
								if (reqArray.length == 2 && reqArray[0].equals("migrate")) {
									int over = Integer.parseInt(reqArray[1]);
									for (int i = 0; i < over; i++) {
											pm.migrate();
									}
								}
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
				}
			}
		});
		
		thread.start();
	}
    
    public synchronized void stop() {
		if (thread == null) {
			return;
		}
		
		running = false;
		thread = null;
	}
}
