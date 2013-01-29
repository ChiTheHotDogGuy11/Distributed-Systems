public class Queue {
	private Node first, last;
	private int numElems;
	
	public Queue(MigratableProcess mp) {
		Node newNode = new Node(mp);
		first = newNode;
		last = newNode;
		numElems = 0;
	}
	
	private class Node {
		private MigratableProcess value;
		private Node next;
		
		public Node(MigratableProcess mp) {
			value = mp;
			next = null;
		}
	}
	
	public MigratableProcess dequeue() {
		if (!isEmpty()) {
			MigratableProcess result = first.value;
			first = first.next;
			numElems -= 1;
			return result;
		}
		else throw new IllegalStateException("Can't dequeue from an empty queue.");
	}
	
	public void enqueue(MigratableProcess mp) {
		Node newNode = new Node(mp);
		last.next = newNode;
		last = newNode;
		numElems += 1;
	}
	
	public boolean isEmpty() {
		return (numElems == 0);
	}
	
	public int size() {
		return numElems;
	}
}
