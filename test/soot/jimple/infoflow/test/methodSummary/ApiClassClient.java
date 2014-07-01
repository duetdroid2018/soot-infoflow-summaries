package soot.jimple.infoflow.test.methodSummary;

import org.junit.Ignore;

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
		api.setStringField(s);
		api.setStringField(null);
		Object tmp = api.getStringField();
		sink(tmp);
	}
	public void flow1(){
		ApiClass api = new ApiClass();
		String s = stringSource();
		api.setStringField(s);
		Object tmp = api.getStringField();
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
	public void paraReturnFlowOverInterface(){
		IApiClass api = new ApiClass();
		Object s = source();
		Object tmp = api.standardFlow(s);
		sink(tmp);
	}
	
	
	public void paraFieldFieldReturnFlowOverInterface(){
		IApiClass api = new ApiClass();
		Object s = source();
		api.paraToVar2(-3,s);
		Object tmp = api.objInDataFieldToReturn();
		sink(tmp);
	}
	@Ignore //not working
	public void paraFieldSwapFieldReturnFlowOverInterface(){
		IApiClass api = new ApiClass();
		Object s = source();
		api.paraToVar2(-3,s);
		api.swap();
		Object tmp = api.getNonPrimitive2Variable().getData();
		sink(tmp);
	}
	@Ignore //not working if we get api as a paramter
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
	
	public void apl3NoFlow(){
		ApiClass api = new ApiClass();
		Object s = source();
		api.setNonPrimitiveData1APL3(s);
		Object tmp = api.getNonPrimitiveData2AP3();
		sink(tmp);
	}
	
	public void apl3Flow(){
		ApiClass api = new ApiClass();
		Object s = source();
		api.setNonPrimitiveData1APL3(s);
		Object tmp = api.getNonPrimitiveData1APL3();
		sink(tmp);
	}
	

	public void sink(Object out) {
		System.out.println(out);
	}

}
