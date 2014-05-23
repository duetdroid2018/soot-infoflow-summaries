package soot.jimple.infoflow.test.methodSummary.junit;

import static org.junit.Assert.*;
import static soot.jimple.infoflow.methodSummary.data.SourceSinkType.Field;
import static soot.jimple.infoflow.methodSummary.data.SourceSinkType.Parameter;

import java.util.Set;

import org.junit.Test;

import soot.jimple.infoflow.methodSummary.SummaryGenerator;
import soot.jimple.infoflow.methodSummary.data.MethodFlow;
import soot.jimple.infoflow.methodSummary.data.SourceSinkType;
import soot.jimple.infoflow.test.methodSummary.ArbitraryAccessPath;
public class ArbitraryAccessPathTest  extends TestHelper{
	private static final String CLASS_NAME = "soot.jimple.infoflow.test.methodSummary.ArbitraryAccessPath";
	private static final String NULL_FIELD = "<soot.jimple.infoflow.test.methodSummary.ArbitraryAccessPath: soot.jimple.infoflow.test.methodSummary.Data nullData>";
	//private static final String NULL_FIELD2 = "<soot.jimple.infoflow.test.methodSummary.ArbitraryAccessPath: soot.jimple.infoflow.test.methodSummary.Data nullData2>";
	private static final String _D = "<soot.jimple.infoflow.test.methodSummary.Data: soot.jimple.infoflow.test.methodSummary.Data d>";
	private static final String DATA_FIELD = "<soot.jimple.infoflow.test.methodSummary.ArbitraryAccessPath: soot.jimple.infoflow.test.methodSummary.Data data>";
	private static final String DATA_FIELD2 = "<soot.jimple.infoflow.test.methodSummary.ArbitraryAccessPath: soot.jimple.infoflow.test.methodSummary.Data data2>";
	
	
	@Test(timeout = 100000)
	public void getNullData() {
		SummaryGenerator s = getSummary();
		String mSig = mSig(DATA_TYPE,"getNullData","");
		Set<MethodFlow> res = s.createMethodSummary(mSig,methods()).getFlowsForMethod(mSig);
		assertTrue(containsFlow(res, Field,new String[] {NULL_FIELD}, SourceSinkType.Return,new String[] {}));
		assertEquals(1,res.size());
	}
	@Test (timeout = 100000)
	public void getData() {
		SummaryGenerator s = getSummary();
		String mSig = mSig(DATA_TYPE,"getData","");
		Set<MethodFlow> res = s.createMethodSummary(mSig).getFlowsForMethod(mSig);
		assertTrue(containsFlow(res, Field,new String[] {DATA_FIELD}, SourceSinkType.Return,new String[] {}));
		assertEquals(1,res.size());
	}
	@Test(timeout = 100000)
	public void getNullData2() {
		SummaryGenerator s = getSummary();
		String mSig = mSig(DATA_TYPE,"getNullData2","");
		Set<MethodFlow> res = s.createMethodSummary(mSig,methods()).getFlowsForMethod(mSig);
		assertTrue(containsFlow(res, Field,new String[] {NULL_FIELD,_D}, SourceSinkType.Return,new String[] {}));
		assertEquals(1,res.size());
	}
	@Test//(timeout = 100000)
	public void getData2() {
		SummaryGenerator s = getSummary();
		String mSig = mSig(DATA_TYPE,"getData2","");
		Set<MethodFlow> res = s.createMethodSummary(mSig).getFlowsForMethod(mSig);
		assertTrue(containsFlow(res, Field,new String[] {DATA_FIELD,_D}, SourceSinkType.Return,new String[] {}));
		assertEquals(1,res.size());
	}
	@Test(timeout = 100000)
	public void getNullData3() {
		SummaryGenerator s = getSummary();
		String mSig = mSig(DATA_TYPE,"getNullData3","");
		Set<MethodFlow> res = s.createMethodSummary(mSig).getFlowsForMethod(mSig);
		assertTrue(containsFlow(res, Field,new String[] {NULL_FIELD,_D,_D}, SourceSinkType.Return,new String[] {}));
		assertEquals(1,res.size());
	}
	@Test(timeout = 100000)
	public void getData3() {
		SummaryGenerator s = getSummary();
		String mSig = mSig(DATA_TYPE,"getData3","");
		Set<MethodFlow> res = s.createMethodSummary(mSig).getFlowsForMethod(mSig);
		assertTrue(containsFlow(res, Field,new String[] {DATA_FIELD,_D,_D}, SourceSinkType.Return,new String[] {}));
		assertEquals(1,res.size());
	}
	
	@Test(timeout = 100000)
	public void setData2() {
		SummaryGenerator s = getSummary();
		String mSig = mSig("void","setData2",DATA_TYPE);
		Set<MethodFlow> res = s.createMethodSummary(mSig).getFlowsForMethod(mSig);
		assertTrue(containsFlow(res, Parameter,0,new String[] {_D}, Field,new String[] {DATA_FIELD}));
		assertEquals(1,res.size());
	}
	@Test(timeout = 100000)
	public void setData3() {
		SummaryGenerator s = getSummary();
		String mSig = mSig("void","setData3",DATA_TYPE);
		Set<MethodFlow> res = s.createMethodSummary(mSig).getFlowsForMethod(mSig);
		assertTrue(containsFlow(res, Parameter,0,new String[] {_D,_D}, Field,new String[] {DATA_FIELD}));
		assertEquals(1,res.size());
	}
	
	@Test(timeout = 100000)
	public void getDataViaParameter() {
		SummaryGenerator s = getSummary();
		String mSig = mSig("void","getDataViaParameter",DATA_TYPE);
		Set<MethodFlow> res = s.createMethodSummary(mSig).getFlowsForMethod(mSig);
		assertTrue(containsFlow(res, Field,new String[] {DATA_FIELD,_D,_D,_D}, Parameter,0,new String[] {_D,_D,_D}));
		assertEquals(1,res.size());
	}
	@Test(timeout = 100000)
	public void getNullDataViaParameter() {
		SummaryGenerator s = getSummary();
		String mSig = mSig("void","getNullDataViaParameter",DATA_TYPE);
		Set<MethodFlow> res = s.createMethodSummary(mSig).getFlowsForMethod(mSig);
		assertTrue(containsFlow(res, Field,new String[] {NULL_FIELD,_D,_D,_D}, Parameter,0,new String[] {_D,_D,_D}));
		assertEquals(1,res.size());
	}
	
	@Test//(timeout = 100000)
	public void fieldToField() {
		SummaryGenerator s = getSummary();
		String mSig = mSig("void","fieldToField");
		Set<MethodFlow> res = s.createMethodSummary(mSig).getFlowsForMethod(mSig);
		assertTrue(containsFlow(res, Field,new String[] {DATA_FIELD,_D,_D,_D}, Field,new String[] {DATA_FIELD2,_D,_D,_D}));
		assertEquals(1,res.size());
	}
	
	@Test(timeout = 100000)
	public void nullFieldToField() {
		SummaryGenerator s = getSummary();
		String mSig = mSig("void","nullFieldToField");
		Set<MethodFlow> res = s.createMethodSummary(mSig).getFlowsForMethod(mSig);
		assertTrue(containsFlow(res, Field,new String[] {NULL_FIELD,_D,_D,_D}, Field,new String[] {NULL_FIELD,_D,_D,_D}));
		assertEquals(1,res.size());
	}
	
	@Test(timeout = 100000)
	public void parameterToParameter() {
		SummaryGenerator s = getSummary();
		String mSig = mSig("void","parameterToParameter",DATA_TYPE,DATA_TYPE);
		Set<MethodFlow> res = s.createMethodSummary(mSig).getFlowsForMethod(mSig);
		assertTrue(containsFlow(res, Parameter,0,new String[] {_D,_D,_D}, Parameter,0,new String[] {_D,_D}));
		assertEquals(1,res.size());
	}
	
	@Test(timeout = 100000)
	public void parameterToReturn() {
		SummaryGenerator s = getSummary();
		String mSig = mSig(DATA_TYPE,"parameterToReturn",DATA_TYPE);
		Set<MethodFlow> res = s.createMethodSummary(mSig).getFlowsForMethod(mSig);
		assertTrue(containsFlow(res, Parameter,0,new String[] {_D,_D,_D}, SourceSinkType.Return,new String[] {_D,_D}));
		assertEquals(1,res.size());
	}
	
	private String mSig(String rTyp, String mName){
		return "<" + CLASS_NAME + ": "+rTyp+" "+mName+"()>";
	}
	private String mSig(String rTyp, String mName, String pTyp){
		return "<" + CLASS_NAME + ": "+rTyp+" "+mName+"("+pTyp+")>";
	}
	private String mSig(String rTyp, String mName, String pTyp, String pTyp2){
		return "<" + CLASS_NAME + ": "+rTyp+" "+mName+"("+pTyp+","+pTyp2+")>";
	}
	
	@Override
	Class<?> getClazz() {
		return ArbitraryAccessPath.class;
	}
	
	
}
