
public class Main {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		ProcessManager pm = new ProcessManager();
		pm.receiveCommands();
		/*String[] args2 = new String[2];
		args2[0] = "http://www.google.com";
		args2[1] = "src\\weby.txt";
		WebCrawler heyThere = new WebCrawler(args2);
		Thread t = new Thread((Runnable)heyThere);
		t.start();*/
	}

}
