
public class Parser {
	
	public Parser(String command) {
		
	}
	
	String[] parse(String command) {
		String[] result = command.split(" ");
		
		if (result[0] == "ps" && result.length == 1) {
			//DISPLAY LIST OF PROCESSES
		} else if (result[0] == "quit" && result.length == 1) {
			//CLOSE PROCESSMANAGER
		} else {
			//RUN PROCESS [arg0] with arguments [arg1] [arg2] ...
		}
		
		return result;
	}
}
