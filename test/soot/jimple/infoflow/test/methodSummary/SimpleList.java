package soot.jimple.infoflow.test.methodSummary;

public class SimpleList<E> {
	transient Node<E> first ;//= new Node();

	private static class Node<E> {
		E item ;
	}

	public E get() {
		return node().item;
	}


	Node<E> node() {
			return first;
	}
}
