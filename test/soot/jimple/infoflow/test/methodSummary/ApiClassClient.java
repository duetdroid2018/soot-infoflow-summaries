package soot.jimple.infoflow.test.methodSummary;

public class ApiClassClient {
	public Object source() {
		return "99";
	}
	public String stringSource() {
		return "99";
	}
	public int intSource() {
		return 99;
	}
	public void paraReturnFlow(){
		ApiClass api = new ApiClass();
		Object s = source();
		Object tmp = api.standardFlow(s);
		sink(tmp);
	}
	
	
	public void paraFieldFieldReturnFlow(){
		ApiClass api = new ApiClass();
		Object s = source();
		api.paraToVar2(-3,s);
		Object tmp = api.objInDataFieldToReturn();
		sink(tmp);
	}
	public void noFlow1(){
		ApiClass api = new ApiClass();
		String s = stringSource();
		api.setaString(s);
		api.setaString(null);
		Object tmp = api.getaString();
		sink(tmp);
	}
	public void flow1(){
		ApiClass api = new ApiClass();
		String s = stringSource();
		api.setaString(s);
		Object tmp = api.getaString();
		sink(tmp);
	}
	public void noFlow2(){
		ApiClass api = new ApiClass();
		int s = intSource();
		api.setPrimitiveVariable(s);
		api.setPrimitiveVariable(0);
		Object tmp = api.getPrimitiveVariable();
		sink(tmp);
	}
	public void flow2(){
		ApiClass api = new ApiClass();
		int s = intSource();
		api.setPrimitiveVariable(s);
		Object tmp = api.getPrimitiveVariable();
		sink(tmp);
	}
	
	public void paraFieldSwapFieldReturnFlow(){
		ApiClass api = new ApiClass();
		Object s = source();
		api.paraToVar2(-3,s);
		api.swap();
		Object tmp = api.getNonPrimitive2Variable().getData();
		sink(tmp);
	}
	public void paraReturnFlowOverInterface(IApiClass api){
		Object s = source();
		Object tmp = api.standardFlow(s);
		sink(tmp);
	}
	
	
	public void paraFieldFieldReturnFlowOverInterface(IApiClass api){
		Object s = source();
		api.paraToVar2(-3,s);
		Object tmp = api.objInDataFieldToReturn();
		sink(tmp);
	}
	
	public void paraFieldSwapFieldReturnFlowOverInterface(IApiClass api){
		Object s = source();
		api.paraToVar2(-3,s);
		api.swap();
		Object tmp = api.getNonPrimitive2Variable().getData();
		sink(tmp);
	}
	
	public void paraToParaFlow(){
		ApiClass api = new ApiClass();
		Data data = new Data();
		Object s = source();
		api.paraToparaFlow2(3, s, data);
		api.swap();
		Object tmp = data.getData();
		sink(tmp);
	}
	
	public void fieldToParaFlow(){
		ApiClass api = new ApiClass();
		Data data = new Data();
		Object s = source();
		api.paraToVarX(3,s);
		api.fieldToPara2(data);
		Object tmp = data.getData();
		sink(tmp);
	}
	

	public void sink(Object out) {
		System.out.println(out);
	}

}
