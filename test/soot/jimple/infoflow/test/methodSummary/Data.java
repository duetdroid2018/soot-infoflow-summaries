package soot.jimple.infoflow.test.methodSummary;

public class Data {
	Object data = new Integer(3);
	Object data2 = new Integer(5);
	Data d = new Data();
	int value;
	public String i;
	public Data() {
		
	
	}
	public void switchData(){
		Object tmp  = data;
		data = data2;
		data2 =tmp;
	}
	public void switchSwitch(){
		d.switchData();
	}
	
	public Data(Object o, int v) {
		data = o;
		value = v;
	}
	public Object getData() {
		return data;
	}
	public void setData(Object data) {
		this.data = data;
	}
	public int getValue() {
		return value;
	}
	public void setValue(int value) {
		this.value = value;
	}
	public void setO(Object o){
		data = o;
	}
	public Object getO(){
		return data;
	}
	public String getI() {
		return i;
	}
	public void setI(String i) {
		this.i = i;
	}
	
	
}
