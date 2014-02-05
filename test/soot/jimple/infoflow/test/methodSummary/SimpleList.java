package soot.jimple.infoflow.test.methodSummary;

public class SimpleList<E> {
	transient int size = 0;
	transient Node<E> first;
	transient Node<E> last;

	private static class Node<E> {
		E item;
		Node<E> next;
		Node<E> prev;
	}

	public E get(int index) {
		return node(index).item;
	}


	Node<E> node(int index) {
			Node<E> x = first;
			return x;
	}
}
