public class ShittyProcess implements MigratableProcess {

	private volatile boolean suspending;
	
	public ShittyProcess(String args[]) {
		
	}
	
	@Override
	public void run() {
		int i = 0;
		while (!suspending) { 
			Fib(i++);
			if (i > 50) {
				break;
			}
		}
		suspending = false;
	}

	@Override
	public void suspend()
	{
		suspending = true;
		while (suspending);
	}
	
	public int Fib(int n) {
		switch(n) {
		case 0: return 1;
		case 1: return 1;
		default: return Fib(n-1) + Fib(n-2);
		}
	}
}
