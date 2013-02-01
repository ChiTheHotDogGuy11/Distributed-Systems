import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.lang.Class;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class ProcessManager {
	
	private ArrayList<Thread> threads = new ArrayList<Thread>();
	private boolean isMaster = true;
	private boolean isRunning = true;
	private Socket sck = null;
	private ServerSocketWrapper server = null;
	private ProcessRunner pr = null;
	
	public void migrate() throws IOException {
		Thread threadToMigrate = pr.getLast();
        ObjectOutputStream out = new ObjectOutputStream(sck.getOutputStream());
        out.writeObject(threadToMigrate);
        out.close();
	}
	
	public Thread acceptProcess(String command, String[] args) {
		Class<?> processClass = null;
		Constructor<?> processCtr = null;
		Thread t = null;
		
		try {
			processClass = Class.forName(command);
		} catch (ClassNotFoundException e) {
			System.out.println(command + " not found");
			return null;
		}
		
		try {
			processCtr = processClass.getConstructor(String[].class);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
		
		try {
			Object[] initArgs = new Object[1];
			initArgs[0] = args;
			t = new Thread((MigratableProcess) processCtr.newInstance(initArgs));
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		
		return t;
	}
	
	public void receiveCommands() throws Exception {
		String result = "";
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		server = new ServerSocketWrapper(2004, 10);
		server.start();
		pr = new ProcessRunner();
		pr.start();
		
		while(isRunning) {
			System.out.print("==> ");
			result = br.readLine();
			parseCommand(result);
		}
	}
	
	public void parseCommand(String command) {
		String[] words = command.split(" ");
		String com = words[0];
		String[] args = new String[words.length - 1];
		Thread t = null;
		
		for (int i = 1; i < words.length; i++) {
			args[i-1] = words[i];
		}
		
		if (com.equals("-c") && args.length == 1) {
			if (isMaster) {
			  connectAssSlave(args[0]);
			} else {
				System.out.println("Already connected as slave");
			}
		} else if (com.equals("ps") && words.length == 1) {
			pr.printProcesses();
		} else if (com.equals("quit") && words.length == 1) {
			quitPM();
		} else {
			if((t = acceptProcess(com, args)) != null) {
				t.setName(command);
				addProcess(t);
			}
		}
	}
	
	public void addProcess(Thread newProcess) {
		pr.addThread(newProcess);
	}
	
	public void connectAssSlave(String hostname) {
		String[] hostArray = hostname.split(":");
		
		if (hostArray.length != 2) {
			try {
				throw new Exception("Invalide Hostname!");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		String host = hostArray[0];
		int port = Integer.parseInt(hostArray[1]);
		
		try {
			sck = new Socket(host, port);
		} catch (UnknownHostException e) {
			System.out.println("Unknown host. Could not connect to " + host + ".");
			return;
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		
		if (sck != null) {
			isMaster = false;
			server.stop();
		}
	}
	
	public void quitPM() {
		if (sck != null) {
			try {
				sck.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		isMaster = false;
		isRunning = false;

		server.stop();
		pr.stop();
	}
	
	public ArrayList<Thread> getThreads() {
		return threads;
	}
}
