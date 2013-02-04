import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.Class;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.Socket;
import java.net.UnknownHostException;

public class ProcessManager {
	
	private boolean isMaster = true;
	private boolean isRunning = true;
	private SocketWrapper sck = null;
	private ServerSocketWrapper server = null;
	private ProcessRunner pr = null;
	private LoadManager lm = null;
	private SlaveListener sl = null;
	
	public void migrate() throws IOException {
		MigratableProcessWrapper processToMigrate = pr.getLast();
		sck.getOut().writeObject(processToMigrate.getName());
		sck.getOut().flush();
        sck.getOut().writeObject(processToMigrate.getProcess());
	}
	
	public MigratableProcessWrapper acceptProcess(String command, String[] args) {
		Class<?> processClass = null;
		Constructor<?> processCtr = null;
		MigratableProcessWrapper mpw = null;
		
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
			mpw = new MigratableProcessWrapper((MigratableProcess) processCtr.newInstance(initArgs));
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		
		return mpw;
	}
	
	public void receiveCommands() throws Exception {
		String result = "";
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		server = new ServerSocketWrapper(2014, 10);
		server.start();
		pr = new ProcessRunner();
		pr.start();
		lm = new LoadManager(server);
		lm.start();
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
		MigratableProcessWrapper mpw = null;
		
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
			if((mpw = acceptProcess(com, args)) != null) {
				mpw.setName(command);
				addProcess(mpw);
			}
		}
	}
	
	public void addProcess(MigratableProcessWrapper newProcess) {
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
			sck = new SocketWrapper(new Socket(host, port));
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
			lm.stop();
			sl = new SlaveListener(sck, this);
			try {
				sl.start();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void quitPM() {
		/*if (sck != null) {
			try {
				sck.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		isMaster = false;
		isRunning = false;

		server.stop();
		pr.stop();*/
		System.exit(1);
	}
	
	public int getNumProcesses() {
		return pr.getSize();
	}
}
