package soot.jimple.infoflow.test.methodSummary;



public class SimpleList<E> {
	Data first = new Data();
	Object keySet;
	private static class Node<E> {
		E item;
	}
	
	public Object get() {
		return first.data;
//		return first == null ? null : first.data;
	}
	
	public void set(Object data) {
		Data next = first;
		if (first == null)
			first = new Data();
		first.d = next;
		first.data = data;
	}
	
	Data node() {
		return first;
	}
	
    public Object keySet() {
    	Object ks = keySet;
        return (ks != null ? ks : (keySet = new Object()));
    }

}
