package soot.jimple.infoflow.test.methodSummary;

public class MultiLevelApi {
	MultiLevelData data = new MultiLevelData(5);
	
//	public Object paraToReturnLength2(MultiLevelData d){
//		return d.getData(2);
//	}
	
	
	public Object fieldToReturnLength4(){
		return data.getData(4);
	}
	
}
