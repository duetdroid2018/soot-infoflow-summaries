package soot.jimple.infoflow.test.methodSummary;



public class SimpleList<E> {
	Data first ;//= new Node();
	Object keySet;
	private static class Node<E> {
		E item ;
	}

	public Object  get() {
		return first.data;
	}


	Data node() {
			return first;
	}
    public Object keySet() {
    	Object ks = keySet;
        return (ks != null ? ks : (keySet = new Object()));
    }

}
