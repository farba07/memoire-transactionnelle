package transaction.transaction.tp3;

import java.util.concurrent.atomic.AtomicReference;

public class WfreeDictionary {

	private static class Node {

		// The character of the string encoded in this node of the dictionary
		char character;
		// True if the string leading to this node has already been inserted, false otherwise
		boolean absent = true;
		// Encodes the set of strings starting with the string leading to this word, 
		// including the character encoded by this node
		Node suffix = null;
		// Encodes the set of strings starting with the string leading to this word, 
		// excluding the character encoded by this node, 
		// and whose next character is strictly greater than the character encoded by this node
		Node next;

		Node(char character, Node next) { 
			this.character = character; 
			this.next = next;
		}

		boolean add(String s, int depth) {

			// First case: we are at the end of the string and this is the correct node
			if(depth >= s.length() || (s.charAt(depth) == character) && depth == s.length() - 1) {
				boolean result = absent;
				absent = false;
				return result;
			}

			// Second case: the next character in the string was found, but this is not the end of the string
			// We continue in member "suffix"
			if(s.charAt(depth) == character) {
				AtomicReference<Node> currentNode ;
				Node old = null; 
				Node newNode = new Node(s.charAt(depth+1), old);
				do {
					currentNode = new AtomicReference<Node>(this);
					old = currentNode.get();
					newNode.suffix = old;
				}while(currentNode.compareAndSet(old,newNode)==false);// || (newNode.character > s.charAt(depth+1)==false));
				return newNode.add(s, depth+1);
			}

			// Third case: the next character in the string was not found
			// We continue in member "next"
			// To maintain the order, we may have to add a new node before "next" first
			AtomicReference<Node> currentNode; 
			Node old = null;
			Node newNode = new Node(s.charAt(depth), old);
			do{
				currentNode = new AtomicReference<Node>(this);
				old = currentNode.get();
				newNode.next = old;
			}while(currentNode.compareAndSet(old, newNode)==false );//|| (newNode.character > s.charAt(depth)==false));
			return newNode.add(s, depth);
		}
	
	}

	// We start with a first node, to simplify the algorithm, that encodes the smallest non-empty string "\0".
	private Node start = new Node('\0',null);
	// The empty string is stored separately
	private boolean emptyAbsent = true;
	
	public boolean add(String s) {
		//Node old;
		//old = start.get()
		if (s != "") {
			return start.add(s, 0);
		}
		boolean result = emptyAbsent;
		emptyAbsent = false;
		return result;
	}
}
