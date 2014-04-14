package soot.jimple.infoflow.test.methodSummary.junit;

import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.junit.Test;

import soot.jimple.infoflow.methodSummary.SummaryGenerator;
import soot.jimple.infoflow.methodSummary.data.AbstractMethodFlow;

public class ArbitraryAccessPathTest  extends TestHelper{
	private static final String CLASS_NAME = "soot.jimple.infoflow.test.methodSummary.ArbitraryAccessPath";
	private static final String NULL_FIELD = "<soot.jimple.infoflow.test.methodSummary.ArbitraryAccessPath: soot.jimple.infoflow.test.methodSummary.Data nullData>";
	private static final String _D = "<soot.jimple.infoflow.test.methodSummary.Data: soot.jimple.infoflow.test.methodSummary.Data d>";
	private static final String DATA_FIELD = "<soot.jimple.infoflow.test.methodSummary.ArbitraryAccessPath: soot.jimple.infoflow.test.methodSummary.Data data>";
	
	
	@Test //(timeout = 100000)
	public void getNullData() {
		SummaryGenerator s = getSummary();
		String mSig = mSig(DATA,"getNullData","");
		Set<AbstractMethodFlow> res = s.createMethodSummary(mSig).getFlowsForMethod(mSig);
		fieldToReturnFlow(res,  new String[] {NULL_FIELD}, new String[] {});
	}
	@Test //(timeout = 100000)
	public void getData() {
		SummaryGenerator s = getSummary();
		String mSig = mSig(DATA,"getData","");
		Set<AbstractMethodFlow> res = s.createMethodSummary(mSig).getFlowsForMethod(mSig);
		fieldToReturnFlow(res,  new String[] {DATA_FIELD}, new String[] {});
	}
	@Test
	public void getNullData2() {
		SummaryGenerator s = getSummary();
		String mSig = mSig(DATA,"getNullData2","");
		Set<AbstractMethodFlow> res = s.createMethodSummary(mSig).getFlowsForMethod(mSig);
		fieldToReturnFlow(res,  new String[] {NULL_FIELD,_D}, new String[] {});
	}
	@Test
	public void getData2() {
		SummaryGenerator s = getSummary();
		String mSig = mSig(DATA,"getData2","");
		Set<AbstractMethodFlow> res = s.createMethodSummary(mSig).getFlowsForMethod(mSig);
		fieldToReturnFlow(res,  new String[] {DATA_FIELD,_D}, new String[] {});
	}
	@Test
	public void getNullData3() {
		SummaryGenerator s = getSummary();
		String mSig = mSig(DATA,"getNullData3","");
		Set<AbstractMethodFlow> res = s.createMethodSummary(mSig).getFlowsForMethod(mSig);
		fieldToReturnFlow(res,  new String[] {NULL_FIELD,_D,_D}, new String[] {});
	}
	@Test
	public void getData3() {
		SummaryGenerator s = getSummary();
		String mSig = mSig(DATA,"getData3","");
		Set<AbstractMethodFlow> res = s.createMethodSummary(mSig).getFlowsForMethod(mSig);
		fieldToReturnFlow(res,  new String[] {DATA_FIELD,_D,_D}, new String[] {});
	}
	
	@Test
	public void setData2() {
		SummaryGenerator s = getSummary();
		String mSig = mSig("void","setData2",DATA);
		Set<AbstractMethodFlow> res = s.createMethodSummary(mSig).getFlowsForMethod(mSig);
		paramterToFieldFlow(res,0,  new String[] {DATA,_D}, new String[] {DATA_FIELD});
	}
	@Test
	public void setData3() {
		SummaryGenerator s = getSummary();
		String mSig = mSig("void","setData3",DATA);
		Set<AbstractMethodFlow> res = s.createMethodSummary(mSig).getFlowsForMethod(mSig);
		paramterToFieldFlow(res, 0, new String[] {_D,_D}, new String[] {DATA_FIELD});
	}
	
	private String mSig(String rTyp, String mName, String pTyps){
		return "<" + CLASS_NAME + ": "+rTyp+" "+mName+"("+pTyps+")>";
	}
	

}
