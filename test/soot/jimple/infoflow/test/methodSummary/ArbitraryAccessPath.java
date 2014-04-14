package soot.jimple.infoflow.test.methodSummary;

public class ArbitraryAccessPath {
	Data nullData;
	Data data = new Data();
	
	
	
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
	
	
}
