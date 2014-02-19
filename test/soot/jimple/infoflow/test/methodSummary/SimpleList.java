package soot.jimple.infoflow.test.methodSummary;

public class SimpleList<E> {
	Data first ;//= new Node();

	private static class Node<E> {
		E item ;
	}

	public Object  get() {
		return first.data;
	}


	Data node() {
			return first;
	}
}
