import java.util.ArrayList;

public class ProcessRunner {
	
	private volatile boolean running;
	private Thread thread;
	private ArrayList<Thread> threads = new ArrayList<Thread>();
	
	public synchronized void start() throws Exception {
		if (thread != null) {
			throw new Exception("ProcessRunner already started.");
		}
			
		thread = new Thread(new Runnable() {
			@Override
			public void run() {
				running = true;
				while(running) {
					if (threads.size() > 0) {
						Thread cur = threads.get(0);
						cur.start();
						try {
							cur.join();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						System.out.println(cur.getName() + " has terminated");
						threads.remove(0);
					}
				}
			}
		});
		
		thread.start();
	}
	
	public synchronized void stop() {
		for (int i = 0; i < threads.size(); i++) {
			threads.get(i).interrupt();
		}
		
		running = false;
		thread = null;
	}
	
	public synchronized void addThread(Thread t) {
		threads.add(t);
	}
	
	public synchronized void printProcesses() {
		if (threads.size() == 0) {
			System.out.println("No processes running.");
			return;
		}
		
		for (int i = 0; i < threads.size(); i++) {
			Thread cur = threads.get(i);
			System.out.println(cur.getName());
		}
	}
}
