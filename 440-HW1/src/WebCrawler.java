import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class WebCrawler implements MigratableProcess {
	private Queue<URL> linksToCheck;
	private HashMap<URL, Boolean> seenLinks;
	private TransactionalFileOutputStream outFile;
	
	private volatile boolean suspending;
	
	public WebCrawler(String[] args) {
		URL startingURL;
		try {
			if (args.length != 2) throw new IllegalArgumentException("WebCrawler usage: $startingURL $targetFilename");
			startingURL = new URL(args[0]);
			String targetFilename = args[1];
			seenLinks = new HashMap<URL, Boolean>();
			linksToCheck = new Queue(startingURL);
			outFile = new TransactionalFileOutputStream(targetFilename, false);
			suspending = false;
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			System.out.println("ERROR: Invalid url");
		}
	}
	
	public void run() {
		runCrawler();
	}
	
	private void runCrawler() {
		PrintStream outStream = new PrintStream(outFile);
		
		while (linksToCheck.isEmpty() == false && suspending == false) {
			URL curURL = linksToCheck.dequeue();
			if (seenLinks.containsKey(curURL)) continue;
			else seenLinks.put(curURL, true);
			//outStream.println("LINKS FROM " + curURL);
			System.out.println("LINKS FROM " + curURL);
			BufferedReader incomingHTML;
			try {
				incomingHTML = new BufferedReader(new InputStreamReader(curURL.openStream()));
		        String curLine;
		        curLine = incomingHTML.readLine();
				while (curLine != null) {
					Pattern linkPattern = Pattern.compile("(?i)<a([^>]+)>(.+?)</a>");
					Pattern urlFromLinkPattern = Pattern.compile("\\s*(?i)href\\s*=\\s*\"([^\"]*\"|'[^']*'|[^'\">\\s]+)");
					Matcher matchOnLink = linkPattern.matcher(curLine);
					while (matchOnLink.find()){
						String href = matchOnLink.group(1);
						Matcher hrefPart = urlFromLinkPattern.matcher(href);
						while (hrefPart.find()) {
							String newLink = hrefPart.group(1);
							newLink = newLink.replace("\"", "");
							try {
								linksToCheck.enqueue(new URL(newLink));
								System.out.println("OUTGOING LINK: " + newLink);
							}
							catch (MalformedURLException e) {
							}
						}
					}
				   // System.out.println(curLine);
				    curLine = incomingHTML.readLine();
				}
		        incomingHTML.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("ERROR: Bad URL on queue.");
				e.printStackTrace();
			}
		}
		suspending = false;
	}
	
	public void checkPageForLinks() {
		
	}
	
	public void suspend()
	{
		System.out.println("");
		System.out.println("");
		System.out.println("");
		System.out.println("");
		System.out.println("Suspending");
		System.out.println("");
		System.out.println("");
		System.out.println("");
		System.out.println("");
		suspending = true;
		while (suspending);
	}
}
