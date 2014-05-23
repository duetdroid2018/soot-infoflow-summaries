package soot.jimple.infoflow.test.methodSummary;

public class ArbitraryAccessPath {
	Data nullData ;
	Data nullData2 ;
	Data data = new Data();
	Data data2 = new Data();
	
	public ArbitraryAccessPath(){
		data.d = new Data();
		data.d.d = new Data();
		data.d.d.d = new Data();
		data2.d = new Data();
		data2.d.d = new Data();
		data2.d.d.d = new Data();
	}
	
	public Data getNullData() {
		return nullData;
	}

	public Data getData() {
		return data;
	}

	public Data getNullData2() {
		return getNullData().d;
	}
	
	public void setNullData2(Data nullData) {
		getNullData().d = nullData.d;
	}
	public Data getData2() {
		return data.d;
	}
	public void setData2(Data data) {
		this.data.d = data.d;
	}
	
	public Data getNullData3() {
		return nullData.d.d;
	}
	
	public void setNullData3(Data nullData) {
		getNullData().d.d = nullData.d.d;
	}
	public Data getData3() {
		return data.d.d;
	}
	public void setData3(Data data) {
		this.data.d.d = data.d.d;
	}
	
	public void setObject(Object d) {
		this.data.d.d.d.data = d;
	}

	public void setNullData(Data nullData) {
		this.nullData = nullData;
	}

	public void setData(Data data) {
		this.data = data;
	}
	
	public void getDataViaParameter(Data pdata){
		pdata.d.d.d = data.d.d.d;
	}
	
	public void getNullDataViaParameter(Data data){
		data.d.d.d = nullData.d.d.d;
	}
	
	public void fieldToField(){
		data2.d.d.d = data.d.d.d;
	}
	
	public void nullFieldToField(){
		nullData2.d.d.d = nullData.d.d.d;
	}
	
	public void parameterToParameter(Data p1, Data p2){
		p2.d.d  = p1.d.d.d;
	}

	public Data parameterToReturn(Data p1){
		Data resD = new Data();
		resD.d = new Data();
		resD.d.d = p1.d.d.d;
		return resD;
	}
}
