package soot.jimple.infoflow.test.methodSummary;



public class SimpleList<E> {
	Data first = null;
	Object keySet;
	private static class Node<E> {
		E item;
	}
	
	public Object get() {
		return node().data;
//		return first == null ? null : first.data;
	}
	
	public void getViaParameter(Data d){
		d.data = get(); 
	}
	
	public void set(Object data) {
		Data next = first;
		if (first == null)
			first = new Data();
		first.d = next;
		first.data = data;
	}
	public void arg(){
		keySet = first;
	}
	Data node() {
		return first;
	}
	
    public Object keySet() {
    	Object ks = keySet;
        return (ks != null ? ks : (keySet = new Object()));
    }

}
