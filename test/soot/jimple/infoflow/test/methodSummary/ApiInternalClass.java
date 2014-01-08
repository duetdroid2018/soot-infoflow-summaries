package soot.jimple.infoflow.test.methodSummary;

public class ApiInternalClass {
	
	
	public static Object getOStatic(Data d){
		return d.getData();
	
	}
	
	public Object getO(Data d){
		return d.getData();
	}

	public Data returnData(Data d){
		Data t = new Data();
		t.data = d.data;
		t.i = d.i;
		t.value = d.value;
		return t;
	}
	public void write(Data d1,Data d2){
		d2.setData(d1.getData());
				
	}
	public void write(Object o1,Data d2){
		d2.setData(o1);
				
	}
	
}
