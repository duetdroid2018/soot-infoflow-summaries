package soot.jimple.infoflow.test.methodSummary;

public class ArbitraryAccessPath {
	Data d;
	
	public Object ap1(){
		return d.d;
	}
	public Object ap2(){
		return d.d.d;
	}
	public Object ap3(){
		return d.d.d.d;
	}
	
	public Object parameterAp3(Data p){
		return p.d.d.d;
	}
}
