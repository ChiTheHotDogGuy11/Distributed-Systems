import java.io.Serializable;

public class Queue <F> implements Serializable{
	private Node<F> first, last;
	private int numElems;
	
	public Queue(F mp) {
		Node<F> newNode = new Node<F>(mp);
		first = newNode;
		last = newNode;
		numElems = 1;
	}
	
	private class Node <E> implements Serializable{
		public E value;
		public Node<E> next;
		
		public Node(E mp) {
			value = mp;
			next = null;
		}
		
		public E getValue() {
			return value;
		}
	}
	
	public F dequeue() {
		if (!isEmpty()) {
			F result = first.value;
			first = first.next;
			numElems -= 1;
			return result;
		}
		else throw new IllegalStateException("Can't dequeue from an empty queue.");
	}
	
	public void enqueue(F mp) {
		Node<F> newNode = new Node<F>(mp);
		last.next = newNode;
		last = newNode;
		if (isEmpty()) first = newNode;
		numElems += 1;
	}
	
	public Node<F> getHead() {
		return first;
	}
	
	public boolean isEmpty() {
		return (numElems == 0);
	}
	
	public int size() {
		return numElems;
	}
}
