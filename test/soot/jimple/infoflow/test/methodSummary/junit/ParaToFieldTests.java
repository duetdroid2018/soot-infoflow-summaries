package soot.jimple.infoflow.test.methodSummary.junit;

import static org.junit.Assert.assertTrue;


import java.util.Map;
import java.util.Set;

import org.junit.Test;

import soot.jimple.infoflow.methodSummary.SummaryGenerator;
import soot.jimple.infoflow.methodSummary.data.AbstractMethodFlow;

public class ParaToFieldTests extends TestHelper {
	protected static Map<String, Set<AbstractMethodFlow>> flows;
	static boolean executeSummary = true;
	static final String className = "soot.jimple.infoflow.test.methodSummary.ParaToField";

	@Test(timeout = 100000)
	public void intParameter() {
		SummaryGenerator s = new SummaryGenerator();
		String mSig = "<soot.jimple.infoflow.test.methodSummary.ParaToField: void intPara(int)>";
		Set<AbstractMethodFlow> flow = s.createMethodSummary(mSig).getFlowsForMethod(mSig);

		
		assertTrue(containsParaToFieldFlow(flow,0, INT_TYPE,NO_ACCESS_PATH,"<soot.jimple.infoflow.test.methodSummary.ParaToField: int intField>",NO_ACCESS_PATH));
		assertTrue(containsParaToFieldFlow(flow,0, INT_TYPE,NO_ACCESS_PATH,"<soot.jimple.infoflow.test.methodSummary.ParaToField: soot.jimple.infoflow.test.methodSummary.Data dataField>",DATACLASS_INT_FIELD));
		assertTrue(containsParaToFieldFlow(flow,0, INT_TYPE,NO_ACCESS_PATH,"<soot.jimple.infoflow.test.methodSummary.ParaToField: int[] intArray>",NO_ACCESS_PATH));
		assertTrue(flow.size() == 4);
	}

	@Test(timeout = 100000)
	public void intParameterRec() {
		SummaryGenerator s = new SummaryGenerator();
		String mSig = "<soot.jimple.infoflow.test.methodSummary.ParaToField: void intParaRec(int,int)>";
		Set<AbstractMethodFlow> flow = s.createMethodSummary(mSig).getFlowsForMethod(mSig);
		
		assertTrue(containsParaToFieldFlow(flow,0, INT_TYPE,NO_ACCESS_PATH,"<soot.jimple.infoflow.test.methodSummary.ParaToField: int intField>",NO_ACCESS_PATH));
		assertTrue(containsParaToFieldFlow(flow,0, INT_TYPE,NO_ACCESS_PATH,"<soot.jimple.infoflow.test.methodSummary.ParaToField: soot.jimple.infoflow.test.methodSummary.Data dataField>",DATACLASS_INT_FIELD));
		assertTrue(containsParaToFieldFlow(flow,0, INT_TYPE,NO_ACCESS_PATH,"<soot.jimple.infoflow.test.methodSummary.ParaToField: int[] intArray>",NO_ACCESS_PATH));
		assertTrue(flow.size() == 3);
	}

	@Test(timeout = 100000)
	public void objectParameter() {
		SummaryGenerator s = new SummaryGenerator();
		String mSig = "<soot.jimple.infoflow.test.methodSummary.ParaToField: void objPara(java.lang.Object)>";
		Set<AbstractMethodFlow> flow = s.createMethodSummary(mSig).getFlowsForMethod(mSig);
		assertTrue(containsParaToFieldFlow(flow,0,OBJECT_TYPE,NO_ACCESS_PATH,"<soot.jimple.infoflow.test.methodSummary.ParaToField: java.lang.Object obField>",NO_ACCESS_PATH));
		assertTrue(containsParaToFieldFlow(flow,0,OBJECT_TYPE,NO_ACCESS_PATH,"<soot.jimple.infoflow.test.methodSummary.ParaToField: java.lang.Object[] arrayField>",NO_ACCESS_PATH));
	}

	@Test(timeout = 100000)
	public void objectParameter2() {
		SummaryGenerator s = new SummaryGenerator();
		String mSig = "<soot.jimple.infoflow.test.methodSummary.ParaToField: void objPara(java.lang.Object)>";
		Set<AbstractMethodFlow> flow = s.createMethodSummary(mSig).getFlowsForMethod(mSig);
		assertTrue(containsParaToFieldFlow(flow,0, OBJECT_TYPE,NO_ACCESS_PATH,"<soot.jimple.infoflow.test.methodSummary.ParaToField: soot.jimple.infoflow.test.methodSummary.Data dataField>",DATACLASS_OBJECT_FIELD));
		assertTrue(containsParaToFieldFlow(flow,0, OBJECT_TYPE,NO_ACCESS_PATH,"<soot.jimple.infoflow.test.methodSummary.ParaToField: java.util.List listField>",LINKEDLIST_LAST));
	//	assertTrue(flow.size() == 4);
	}

	@Test(timeout = 100000)
	public void intAndObjectParameter() {
		SummaryGenerator s = new SummaryGenerator();
		String mSig = "<soot.jimple.infoflow.test.methodSummary.ParaToField: void intAndObj(int,java.lang.Object)>";
		Set<AbstractMethodFlow> flow = s.createMethodSummary(mSig).getFlowsForMethod(mSig);
		
		assertTrue(containsParaToFieldFlow(flow,0, INT,NO_ACCESS_PATH,"<soot.jimple.infoflow.test.methodSummary.ParaToField: int intField>",NO_ACCESS_PATH));
		
		assertTrue(containsParaToFieldFlow(flow,1, OBJECT_TYPE,NO_ACCESS_PATH,"<soot.jimple.infoflow.test.methodSummary.ParaToField: java.lang.Object obField>",NO_ACCESS_PATH));
		assertTrue(containsParaToFieldFlow(flow,1, OBJECT_TYPE,NO_ACCESS_PATH,"<soot.jimple.infoflow.test.methodSummary.ParaToField: java.lang.Object[] arrayField>",NO_ACCESS_PATH));
		assertTrue(containsParaToFieldFlow(flow,0, INT,NO_ACCESS_PATH,"<soot.jimple.infoflow.test.methodSummary.ParaToField: int[] intArray>",NO_ACCESS_PATH));

	}

	@Test(timeout = 100000)
	public void intAndObjectParameter2() {
		SummaryGenerator s = new SummaryGenerator();
		String mSig = "<soot.jimple.infoflow.test.methodSummary.ParaToField: void intAndObj(int,java.lang.Object)>";
		Set<AbstractMethodFlow> flow = s.createMethodSummary(mSig).getFlowsForMethod(mSig);
		assertTrue(containsParaToFieldFlow(flow,1,OBJECT_TYPE,NO_ACCESS_PATH,"<soot.jimple.infoflow.test.methodSummary.ParaToField: soot.jimple.infoflow.test.methodSummary.Data dataField>",DATACLASS_OBJECT_FIELD));
		
		assertTrue(containsParaToFieldFlow(flow,0, INT_TYPE,NO_ACCESS_PATH,"<soot.jimple.infoflow.test.methodSummary.ParaToField: int intField>",NO_ACCESS_PATH));
		
		assertTrue(containsParaToFieldFlow(flow,1,OBJECT_TYPE,NO_ACCESS_PATH,"<soot.jimple.infoflow.test.methodSummary.ParaToField: java.lang.Object obField>",NO_ACCESS_PATH));
		assertTrue(containsParaToFieldFlow(flow,1,OBJECT_TYPE,NO_ACCESS_PATH,"<soot.jimple.infoflow.test.methodSummary.ParaToField: java.util.List listField>",LINKEDLIST_LAST));
		assertTrue(containsParaToFieldFlow(flow,0, INT_TYPE,NO_ACCESS_PATH,"<soot.jimple.infoflow.test.methodSummary.ParaToField: soot.jimple.infoflow.test.methodSummary.Data dataField>",DATACLASS_INT_FIELD));
		assertTrue(containsParaToFieldFlow(flow,1,OBJECT_TYPE,NO_ACCESS_PATH,"<soot.jimple.infoflow.test.methodSummary.ParaToField: java.lang.Object[] arrayField>",NO_ACCESS_PATH));
		assertTrue(containsParaToFieldFlow(flow,0, INT_TYPE,NO_ACCESS_PATH,"<soot.jimple.infoflow.test.methodSummary.ParaToField: int[] intArray>",NO_ACCESS_PATH));
		//assertTrue(flow.size() == 10); //TODO readd when this.* is working
	}

	@Test(timeout = 100000)
	public void arrayParas() {
		SummaryGenerator s = new SummaryGenerator();
		String mSig = "<soot.jimple.infoflow.test.methodSummary.ParaToField: void arrayParas(int[],java.lang.Object[])>";
		Set<AbstractMethodFlow> flow = s.createMethodSummary(mSig).getFlowsForMethod(mSig);
		
		assertTrue(containsParaToFieldFlow(flow,0,INT_ARRAY,NO_ACCESS_PATH,"<soot.jimple.infoflow.test.methodSummary.ParaToField: int intField>",NO_ACCESS_PATH));
		assertTrue(containsParaToFieldFlow(flow,1,OBJECT_ARRAY,NO_ACCESS_PATH,"<soot.jimple.infoflow.test.methodSummary.ParaToField: java.lang.Object obField>",NO_ACCESS_PATH));
		assertTrue(containsParaToFieldFlow(flow,1,OBJECT_ARRAY,NO_ACCESS_PATH,"<soot.jimple.infoflow.test.methodSummary.ParaToField: java.lang.Object[] arrayField>",NO_ACCESS_PATH));
		assertTrue(containsParaToFieldFlow(flow,0,INT_ARRAY,NO_ACCESS_PATH,"<soot.jimple.infoflow.test.methodSummary.ParaToField: int[] intArray>",NO_ACCESS_PATH));
	}

	@Test(timeout = 300000)
	public void arrayParas2() {
		SummaryGenerator s = new SummaryGenerator();
		String mSig = "<soot.jimple.infoflow.test.methodSummary.ParaToField: void arrayParas(int[],java.lang.Object[])>";
		Set<AbstractMethodFlow> flow = s.createMethodSummary(mSig).getFlowsForMethod(mSig);

		assertTrue(containsParaToFieldFlow(flow,0,INT_ARRAY,NO_ACCESS_PATH,"<soot.jimple.infoflow.test.methodSummary.ParaToField: int intField>",NO_ACCESS_PATH));
		assertTrue(containsParaToFieldFlow(flow,0,INT_ARRAY,NO_ACCESS_PATH,"<soot.jimple.infoflow.test.methodSummary.ParaToField: soot.jimple.infoflow.test.methodSummary.Data dataField>",DATACLASS_INT_FIELD));
		assertTrue(containsParaToFieldFlow(flow,1,OBJECT_ARRAY,NO_ACCESS_PATH,"<soot.jimple.infoflow.test.methodSummary.ParaToField: java.lang.Object obField>",NO_ACCESS_PATH));
		assertTrue(containsParaToFieldFlow(flow,1,OBJECT_ARRAY,NO_ACCESS_PATH,"<soot.jimple.infoflow.test.methodSummary.ParaToField: soot.jimple.infoflow.test.methodSummary.Data dataField>",DATACLASS_OBJECT_FIELD));
		assertTrue(containsParaToFieldFlow(flow,1,OBJECT_ARRAY,NO_ACCESS_PATH,"<soot.jimple.infoflow.test.methodSummary.ParaToField: java.util.List listField>",LINKEDLIST_LAST));
		assertTrue(containsParaToFieldFlow(flow,1,OBJECT_ARRAY,NO_ACCESS_PATH,"<soot.jimple.infoflow.test.methodSummary.ParaToField: java.util.List listField>",LINKEDLIST_LAST));
		assertTrue(containsParaToFieldFlow(flow,1,OBJECT_ARRAY,NO_ACCESS_PATH,"<soot.jimple.infoflow.test.methodSummary.ParaToField: java.lang.Object[] arrayField>",NO_ACCESS_PATH));
		assertTrue(containsParaToFieldFlow(flow,0,INT_ARRAY,NO_ACCESS_PATH,"<soot.jimple.infoflow.test.methodSummary.ParaToField: int[] intArray>",NO_ACCESS_PATH));
		//assertTrue(flow.size() == 7);
	}

	@Test(timeout = 300000)
	public void dataAndListParameter() {
		SummaryGenerator s = new SummaryGenerator();
		String mSig = "<soot.jimple.infoflow.test.methodSummary.ParaToField: void dataAndList(soot.jimple.infoflow.test.methodSummary.Data,java.util.List)>";
		Set<AbstractMethodFlow> flow = s.createMethodSummary(mSig).getFlowsForMethod(mSig);
		
		assertTrue(containsParaToFieldFlow(flow,0,DATA,DATACLASS_INT_FIELD,"<soot.jimple.infoflow.test.methodSummary.ParaToField: int intField>",NO_ACCESS_PATH));
		assertTrue(containsParaToFieldFlow(flow,0,DATA,DATACLASS_OBJECT_FIELD,"<soot.jimple.infoflow.test.methodSummary.ParaToField: soot.jimple.infoflow.test.methodSummary.Data dataField>",DATACLASS_OBJECT_FIELD));
		assertTrue(containsParaToFieldFlow(flow,1,LIST,LINKEDLIST_FIRST,"<soot.jimple.infoflow.test.methodSummary.ParaToField: java.lang.Object obField>",NO_ACCESS_PATH));
		assertTrue(containsParaToFieldFlow(flow,0,DATA,DATACLASS_OBJECT_FIELD,"<soot.jimple.infoflow.test.methodSummary.ParaToField: java.lang.Object[] arrayField>",NO_ACCESS_PATH));
		assertTrue(containsParaToFieldFlow(flow,1,DATA,DATACLASS_INT_FIELD,"<soot.jimple.infoflow.test.methodSummary.ParaToField: int[] intArray>",NO_ACCESS_PATH));
	}

	@Test(timeout = 400000)
	public void dataAndListParameter2() {
		
		SummaryGenerator s = new SummaryGenerator();
		String mSig = "<soot.jimple.infoflow.test.methodSummary.ParaToField: void dataAndList(soot.jimple.infoflow.test.methodSummary.Data,java.util.List)>";
		Set<AbstractMethodFlow> flow = s.createMethodSummary(mSig).getFlowsForMethod(mSig);
		
		assertTrue(containsParaToFieldFlow(flow,0,DATA,DATACLASS_INT_FIELD,"<soot.jimple.infoflow.test.methodSummary.ParaToField: int intField>",NO_ACCESS_PATH));
		assertTrue(containsParaToFieldFlow(flow,0,DATA,DATACLASS_OBJECT_FIELD,"<soot.jimple.infoflow.test.methodSummary.ParaToField: soot.jimple.infoflow.test.methodSummary.Data dataField>",NO_ACCESS_PATH));
		assertTrue(containsParaToFieldFlow(flow,1,LIST,LINKEDLIST_FIRST,"<soot.jimple.infoflow.test.methodSummary.ParaToField: java.lang.Object obField>",NO_ACCESS_PATH));
		assertTrue(containsParaToFieldFlow(flow,1,LIST,LINKEDLIST_FIRST,"<soot.jimple.infoflow.test.methodSummary.ParaToField: java.util.List listField>",NO_ACCESS_PATH));
		assertTrue(containsParaToFieldFlow(flow,0,DATA,DATACLASS_OBJECT_FIELD,"<soot.jimple.infoflow.test.methodSummary.ParaToField: java.lang.Object[] arrayField>",NO_ACCESS_PATH));
		assertTrue(containsParaToFieldFlow(flow,0,DATA,DATACLASS_INT_FIELD,"<soot.jimple.infoflow.test.methodSummary.ParaToField: int[] intArray>",NO_ACCESS_PATH));
		//assertTrue(flow.size() == 6);
	}
}
