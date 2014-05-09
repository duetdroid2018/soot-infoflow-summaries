package soot.jimple.infoflow.test.methodSummary;



public class SimpleList<E> {
	Data first = null;
	Object keySet;
	int intField;
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
	
	
    public Data getFirst() {
		return first;
	}

	public void setFirst(Data first) {
		this.first = first;
	}

	public Object getKeySet() {
		return keySet;
	}

	public void setKeySet(Object keySet) {
		this.keySet = keySet;
	}

	public Object keySet() {
    	Object ks = keySet;
        return (ks != null ? ks : (keySet = new Object()));
    }

	public int getIntField() {
		return intField;
	}

	public void setIntField(int intField) {
		this.intField = intField;
		first.setData(intField);
	}

	
	
}
