package soot.jimple.infoflow.test.methodSummary;

public class Callbacks {
	
	public interface MyCallbacks {
		
		public String transform(String in);
		
	}
	
	public String paraToCallbackToReturn(String data, MyCallbacks cbs) {
		String foo = cbs.transform(data);
		return foo;
	}

}
