package soot.jimple.infoflow.test.methodSummary;

public class ComplexFlows {
	Data d = new Data();
	String gVar;
	int gVarInt;
	
	public Data getD(){
		return d;
	}
	private Data returnD(Data d){
		return d;
	}
	
	public String paraToRetunAndToGVar1(String i){
		return StringernalM1(i); 	
	}
	public String paraToRetunAndToGVar2(String i){
		return StringernalM2(i); 	
	}
	
	public void paraToTwoGVar(String i){
		StringernalM3(i); 	
	}
	
	public void paraToGVar1(String i){
		d.i = i;
	}
	
	public void paraToGVar2(String i){
		Data d2 = getD();
		d2.i = i;
	}
	public void paraToGVar3(String i){
		getD().i = i;
	}
	
	public void paraToGVar4(String i){
		returnD(d).i = i;
	}
	
	private String StringernalM1(String i){		
		String j = ComplexFlowsExternalMethods.staticReturnPara(i);
		gVar = j;
		return gVar + 3;
	}
	private String StringernalM2(String i){
		String j = (new ComplexFlowsExternalMethods()).returnPara(i);
		gVar = j;
		return gVar + 3;
	}
	
	
	private void StringernalM3(String i){
		d.i = i;
		gVar = d.i;
	}
	
	public int paraToParaQuick(int i){
		gVar = d.i;
		return i;
	}
	public int allFlows(int inI, Data inD){
		int tmp = inD.value;
		int tmp2 = d.value + gVarInt;
		inD.value = inI + d.value;
		d.value = tmp + inI;
		gVarInt = inI + tmp2;
		return inI + tmp2;
	}

}
