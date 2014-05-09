package soot.jimple.infoflow.test.methodSummary.junit;

import static org.junit.Assert.assertTrue;
import static soot.jimple.infoflow.methodSummary.data.SourceSinkType.Field;
import static soot.jimple.infoflow.methodSummary.data.SourceSinkType.Return;
import static soot.jimple.infoflow.methodSummary.data.SourceSinkType.Parameter;


import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Ignore;
import org.junit.Test;

import soot.jimple.infoflow.methodSummary.SummaryGenerator;
import soot.jimple.infoflow.methodSummary.data.AbstractMethodFlow;
import soot.jimple.infoflow.methodSummary.data.SourceSinkType;
import soot.jimple.infoflow.test.methodSummary.ArbitraryAccessPath;

public class ParaToFieldTests extends TestHelper {
	protected static Map<String, Set<AbstractMethodFlow>> flows;
	static boolean executeSummary = true;
	static final String className = "soot.jimple.infoflow.test.methodSummary.ParaToField";
	static final String LIST_ITEM[] = new String[]{"<soot.jimple.infoflow.test.methodSummary.ParaToField: java.util.List listField>","<java.util.LinkedList: java.util.LinkedList$Node first>","<java.util.LinkedList$Node: java.lang.Object item>"} ;
	static final String DATA_FIELD = "<soot.jimple.infoflow.test.methodSummary.ParaToField: soot.jimple.infoflow.test.methodSummary.Data dataField>";

	@Test(timeout = 100000)
	public void intParameter() {
		SummaryGenerator s =  summaryGenerator();
		String mSig = "<soot.jimple.infoflow.test.methodSummary.ParaToField: void intPara(int)>";
		Set<AbstractMethodFlow> flow = s.createMethodSummary(mSig).getFlowsForMethod(mSig);

		assertTrue(containsFlow(flow, Parameter,0,new String[] {}, Field,new String[] {"<soot.jimple.infoflow.test.methodSummary.ParaToField: int intField>"}));
		assertTrue(containsFlow(flow, Parameter,0,new String[] {}, Field,new String[] {"<soot.jimple.infoflow.test.methodSummary.ParaToField: soot.jimple.infoflow.test.methodSummary.Data dataField>", DATACLASS_INT_FIELD}));
		assertTrue(containsFlow(flow, Parameter,0,new String[] {}, Field,new String[] {"<soot.jimple.infoflow.test.methodSummary.ParaToField: int[] intArray>"}));
		
		//assertTrue(containsParaToFieldFlow(flow,0, INT_TYPE,NO_ACCESS_PATH,"<soot.jimple.infoflow.test.methodSummary.ParaToField: int intField>",NO_ACCESS_PATH));
		//assertTrue(containsParaToFieldFlow(flow,0, INT_TYPE,NO_ACCESS_PATH,"<soot.jimple.infoflow.test.methodSummary.ParaToField: soot.jimple.infoflow.test.methodSummary.Data dataField>",DATACLASS_INT_FIELD));
		//assertTrue(containsParaToFieldFlow(flow,0, INT_TYPE,NO_ACCESS_PATH,"<soot.jimple.infoflow.test.methodSummary.ParaToField: int[] intArray>",NO_ACCESS_PATH));
		
	}

	@Test(timeout = 100000)
	public void intParameterRec() {
		SummaryGenerator s =  summaryGenerator();
		String mSig = "<soot.jimple.infoflow.test.methodSummary.ParaToField: void intParaRec(int,int)>";
		Set<AbstractMethodFlow> flow = s.createMethodSummary(mSig).getFlowsForMethod(mSig);
		assertTrue(containsFlow(flow, Parameter,0,new String[] {}, Field,new String[] {"<soot.jimple.infoflow.test.methodSummary.ParaToField: int intField>"}));
		assertTrue(containsFlow(flow, Parameter,0,new String[] {}, Field,new String[] {"<soot.jimple.infoflow.test.methodSummary.ParaToField: soot.jimple.infoflow.test.methodSummary.Data dataField>",DATACLASS_INT_FIELD}));
		assertTrue(containsFlow(flow, Parameter,0,new String[] {}, Field,new String[] {"<soot.jimple.infoflow.test.methodSummary.ParaToField: int[] intArray>"}));
		
		//assertTrue(containsParaToFieldFlow(flow,0, INT_TYPE,NO_ACCESS_PATH,"<soot.jimple.infoflow.test.methodSummary.ParaToField: int intField>",NO_ACCESS_PATH));
		//assertTrue(containsParaToFieldFlow(flow,0, INT_TYPE,NO_ACCESS_PATH,"<soot.jimple.infoflow.test.methodSummary.ParaToField: soot.jimple.infoflow.test.methodSummary.Data dataField>",DATACLASS_INT_FIELD));
		//assertTrue(containsParaToFieldFlow(flow,0, INT_TYPE,NO_ACCESS_PATH,"<soot.jimple.infoflow.test.methodSummary.ParaToField: int[] intArray>",NO_ACCESS_PATH));
		assertTrue(flow.size() == 3);
	}

	@Test(timeout = 100000)
	public void objectParameter() {
		SummaryGenerator s =  summaryGenerator();
		String mSig = "<soot.jimple.infoflow.test.methodSummary.ParaToField: void objPara(java.lang.Object)>";
		Set<AbstractMethodFlow> flow = s.createMethodSummary(mSig).getFlowsForMethod(mSig);
		
		assertTrue(containsFlow(flow, Parameter,0,new String[] {}, Field,new String[] {"<soot.jimple.infoflow.test.methodSummary.ParaToField: java.lang.Object obField>"}));
		assertTrue(containsFlow(flow, Parameter,0,new String[] {}, Field,new String[] {"<soot.jimple.infoflow.test.methodSummary.ParaToField: java.lang.Object[] arrayField>"}));
		
		
//		assertTrue(containsParaToFieldFlow(flow,0,OBJECT_TYPE,NO_ACCESS_PATH,"<soot.jimple.infoflow.test.methodSummary.ParaToField: java.lang.Object obField>",NO_ACCESS_PATH));
//		assertTrue(containsParaToFieldFlow(flow,0,OBJECT_TYPE,NO_ACCESS_PATH,"<soot.jimple.infoflow.test.methodSummary.ParaToField: java.lang.Object[] arrayField>",NO_ACCESS_PATH));
	}

	@Test(timeout = 100000)
	public void objectParameter2() {
		SummaryGenerator s =  summaryGenerator();
		String mSig = "<soot.jimple.infoflow.test.methodSummary.ParaToField: void objPara(java.lang.Object)>";
		Set<AbstractMethodFlow> flow = s.createMethodSummary(mSig).getFlowsForMethod(mSig);
		
		assertTrue(containsFlow(flow, Parameter,0,new String[] {}, Field,new String[] {"<soot.jimple.infoflow.test.methodSummary.ParaToField: soot.jimple.infoflow.test.methodSummary.Data dataField>",DATACLASS_OBJECT_FIELD}));
		assertTrue(containsFlow(flow, Parameter,0,new String[] {}, Field,LIST_ITEM));
		
		
//		assertTrue(containsParaToFieldFlow(flow,0, OBJECT_TYPE,NO_ACCESS_PATH,"<soot.jimple.infoflow.test.methodSummary.ParaToField: soot.jimple.infoflow.test.methodSummary.Data dataField>",DATACLASS_OBJECT_FIELD));
//		assertTrue(containsParaToFieldFlow(flow,0, OBJECT_TYPE,NO_ACCESS_PATH,"<soot.jimple.infoflow.test.methodSummary.ParaToField: java.util.List listField>",LINKEDLIST_LAST));
		assertTrue(flow.size() == 6);
	}

	@Test(timeout = 100000)
	public void intAndObjectParameter() {
		SummaryGenerator s =  summaryGenerator();
		String mSig = "<soot.jimple.infoflow.test.methodSummary.ParaToField: void intAndObj(int,java.lang.Object)>";
		Set<AbstractMethodFlow> flow = s.createMethodSummary(mSig).getFlowsForMethod(mSig);
		
		assertTrue(containsFlow(flow, Parameter,0,new String[] {}, Field,new String[] {"<soot.jimple.infoflow.test.methodSummary.ParaToField: int intField>"}));
		assertTrue(containsFlow(flow, Parameter,1,new String[] {}, Field,new String[] {"<soot.jimple.infoflow.test.methodSummary.ParaToField: java.lang.Object obField>"}));
		assertTrue(containsFlow(flow, Parameter,1,new String[] {}, Field,new String[] {"<soot.jimple.infoflow.test.methodSummary.ParaToField: java.lang.Object[] arrayField>"}));
		assertTrue(containsFlow(flow, Parameter,0,new String[] {}, Field,new String[] {"<soot.jimple.infoflow.test.methodSummary.ParaToField: int[] intArray>"}));
		
		
//		assertTrue(containsParaToFieldFlow(flow,0, INT,NO_ACCESS_PATH,"<soot.jimple.infoflow.test.methodSummary.ParaToField: int intField>",NO_ACCESS_PATH));
//		assertTrue(containsParaToFieldFlow(flow,1, OBJECT_TYPE,NO_ACCESS_PATH,"<soot.jimple.infoflow.test.methodSummary.ParaToField: java.lang.Object obField>",NO_ACCESS_PATH));
//		assertTrue(containsParaToFieldFlow(flow,1, OBJECT_TYPE,NO_ACCESS_PATH,"<soot.jimple.infoflow.test.methodSummary.ParaToField: java.lang.Object[] arrayField>",NO_ACCESS_PATH));
//		assertTrue(containsParaToFieldFlow(flow,0, INT,NO_ACCESS_PATH,"<soot.jimple.infoflow.test.methodSummary.ParaToField: int[] intArray>",NO_ACCESS_PATH));

	}

	@Test(timeout = 100000)
	public void intAndObjectParameter2() {
		SummaryGenerator s =  summaryGenerator();
		String mSig = "<soot.jimple.infoflow.test.methodSummary.ParaToField: void intAndObj(int,java.lang.Object)>";
		Set<AbstractMethodFlow> flow = s.createMethodSummary(mSig).getFlowsForMethod(mSig);
		
		assertTrue(containsFlow(flow, Parameter,1,new String[] {}, Field,new String[] {"<soot.jimple.infoflow.test.methodSummary.ParaToField: soot.jimple.infoflow.test.methodSummary.Data dataField>",DATACLASS_OBJECT_FIELD}));
		assertTrue(containsFlow(flow, Parameter,0,new String[] {}, Field,new String[] {"<soot.jimple.infoflow.test.methodSummary.ParaToField: int intField>"}));
		assertTrue(containsFlow(flow, Parameter,1,new String[] {}, Field,new String[] {"<soot.jimple.infoflow.test.methodSummary.ParaToField: java.lang.Object obField>"}));
		assertTrue(containsFlow(flow, Parameter,1,new String[] {}, Field,LIST_ITEM));
		assertTrue(containsFlow(flow, Parameter,0,new String[] {}, Field,new String[] {"<soot.jimple.infoflow.test.methodSummary.ParaToField: soot.jimple.infoflow.test.methodSummary.Data dataField>",DATACLASS_INT_FIELD}));
		assertTrue(containsFlow(flow, Parameter,1,new String[] {}, Field,new String[] {"<soot.jimple.infoflow.test.methodSummary.ParaToField: java.lang.Object[] arrayField>"}));
		assertTrue(containsFlow(flow, Parameter,0,new String[] {}, Field,new String[] {"<soot.jimple.infoflow.test.methodSummary.ParaToField: int[] intArray>"}));
		
//		assertTrue(containsParaToFieldFlow(flow,1,OBJECT_TYPE,NO_ACCESS_PATH,"<soot.jimple.infoflow.test.methodSummary.ParaToField: soot.jimple.infoflow.test.methodSummary.Data dataField>",DATACLASS_OBJECT_FIELD));
//		assertTrue(containsParaToFieldFlow(flow,0, INT_TYPE,NO_ACCESS_PATH,"<soot.jimple.infoflow.test.methodSummary.ParaToField: int intField>",NO_ACCESS_PATH));
//		assertTrue(containsParaToFieldFlow(flow,1,OBJECT_TYPE,NO_ACCESS_PATH,"<soot.jimple.infoflow.test.methodSummary.ParaToField: java.lang.Object obField>",NO_ACCESS_PATH));
//		assertTrue(containsParaToFieldFlow(flow,1,OBJECT_TYPE,NO_ACCESS_PATH,"<soot.jimple.infoflow.test.methodSummary.ParaToField: java.util.List listField>",LINKEDLIST_LAST));
//		assertTrue(containsParaToFieldFlow(flow,0, INT_TYPE,NO_ACCESS_PATH,"<soot.jimple.infoflow.test.methodSummary.ParaToField: soot.jimple.infoflow.test.methodSummary.Data dataField>",DATACLASS_INT_FIELD));
//		assertTrue(containsParaToFieldFlow(flow,1,OBJECT_TYPE,NO_ACCESS_PATH,"<soot.jimple.infoflow.test.methodSummary.ParaToField: java.lang.Object[] arrayField>",NO_ACCESS_PATH));
//		assertTrue(containsParaToFieldFlow(flow,0, INT_TYPE,NO_ACCESS_PATH,"<soot.jimple.infoflow.test.methodSummary.ParaToField: int[] intArray>",NO_ACCESS_PATH));
		assertTrue(flow.size() == 9);
	}

	@Test(timeout = 100000)
	public void arrayParas() {
		SummaryGenerator s =  summaryGenerator();
		String mSig = "<soot.jimple.infoflow.test.methodSummary.ParaToField: void arrayParas(int[],java.lang.Object[])>";
		Set<AbstractMethodFlow> flow = s.createMethodSummary(mSig).getFlowsForMethod(mSig);
		
		assertTrue(containsFlow(flow, Parameter,0,new String[] {}, Field,new String[] {"<soot.jimple.infoflow.test.methodSummary.ParaToField: int intField>"}));
		assertTrue(containsFlow(flow, Parameter,1,new String[] {}, Field,new String[] {"<soot.jimple.infoflow.test.methodSummary.ParaToField: java.lang.Object obField>"}));
		assertTrue(containsFlow(flow, Parameter,1,new String[] {}, Field,new String[] {"<soot.jimple.infoflow.test.methodSummary.ParaToField: java.lang.Object[] arrayField>"}));
		assertTrue(containsFlow(flow, Parameter,0,new String[] {}, Field,new String[] {"<soot.jimple.infoflow.test.methodSummary.ParaToField: int[] intArray>"}));
		assertTrue(containsFlow(flow, Parameter,1,new String[] {}, Field,new String[] {"<soot.jimple.infoflow.test.methodSummary.ParaToField: java.util.List listField>","<java.util.LinkedList: java.util.LinkedList$Node first>","<java.util.LinkedList$Node: java.lang.Object item>"}));
		assertTrue(containsFlow(flow, Parameter,1,new String[] {}, Field,new String[] {"<soot.jimple.infoflow.test.methodSummary.ParaToField: soot.jimple.infoflow.test.methodSummary.Data dataField>","<soot.jimple.infoflow.test.methodSummary.Data: java.lang.Object data>"}));	
		assertTrue(flow.size() == 9);
//		assertTrue(containsParaToFieldFlow(flow,0,INT_ARRAY,NO_ACCESS_PATH,"<soot.jimple.infoflow.test.methodSummary.ParaToField: int intField>",NO_ACCESS_PATH));
//		assertTrue(containsParaToFieldFlow(flow,1,OBJECT_ARRAY,NO_ACCESS_PATH,"<soot.jimple.infoflow.test.methodSummary.ParaToField: java.lang.Object obField>",NO_ACCESS_PATH));
//		assertTrue(containsParaToFieldFlow(flow,1,OBJECT_ARRAY,NO_ACCESS_PATH,"<soot.jimple.infoflow.test.methodSummary.ParaToField: java.lang.Object[] arrayField>",NO_ACCESS_PATH));
//		assertTrue(containsParaToFieldFlow(flow,0,INT_ARRAY,NO_ACCESS_PATH,"<soot.jimple.infoflow.test.methodSummary.ParaToField: int[] intArray>",NO_ACCESS_PATH));
	}

	@Test(timeout = 300000)
	public void arrayParas2() {
		SummaryGenerator s =  summaryGenerator();
		String mSig = "<soot.jimple.infoflow.test.methodSummary.ParaToField: void arrayParas(int[],java.lang.Object[])>";
		Set<AbstractMethodFlow> flow = s.createMethodSummary(mSig).getFlowsForMethod(mSig);
		
		assertTrue(containsFlow(flow, Parameter,0,new String[] {}, Field,new String[] {"<soot.jimple.infoflow.test.methodSummary.ParaToField: int intField>"}));
		assertTrue(containsFlow(flow, Parameter,0,new String[] {}, Field,new String[] {"<soot.jimple.infoflow.test.methodSummary.ParaToField: soot.jimple.infoflow.test.methodSummary.Data dataField>",DATACLASS_INT_FIELD}));
		assertTrue(containsFlow(flow, Parameter,1,new String[] {}, Field,new String[] {"<soot.jimple.infoflow.test.methodSummary.ParaToField: java.lang.Object obField>"}));
		assertTrue(containsFlow(flow, Parameter,1,new String[] {}, Field,new String[] {"<soot.jimple.infoflow.test.methodSummary.ParaToField: soot.jimple.infoflow.test.methodSummary.Data dataField>",DATACLASS_OBJECT_FIELD}));
		assertTrue(containsFlow(flow, Parameter,1,new String[] {}, Field,LIST_ITEM));
		assertTrue(containsFlow(flow, Parameter,1,new String[] {}, Field,new String[] {"<soot.jimple.infoflow.test.methodSummary.ParaToField: java.lang.Object[] arrayField>"}));
		assertTrue(containsFlow(flow, Parameter,0,new String[] {}, Field,new String[] {"<soot.jimple.infoflow.test.methodSummary.ParaToField: int[] intArray>"}));
		
//
//		assertTrue(containsParaToFieldFlow(flow,0,INT_ARRAY,NO_ACCESS_PATH,"<soot.jimple.infoflow.test.methodSummary.ParaToField: int intField>",NO_ACCESS_PATH));
//		assertTrue(containsParaToFieldFlow(flow,0,INT_ARRAY,NO_ACCESS_PATH,"<soot.jimple.infoflow.test.methodSummary.ParaToField: soot.jimple.infoflow.test.methodSummary.Data dataField>",DATACLASS_INT_FIELD));
//		assertTrue(containsParaToFieldFlow(flow,1,OBJECT_ARRAY,NO_ACCESS_PATH,"<soot.jimple.infoflow.test.methodSummary.ParaToField: java.lang.Object obField>",NO_ACCESS_PATH));
//		assertTrue(containsParaToFieldFlow(flow,1,OBJECT_ARRAY,NO_ACCESS_PATH,"<soot.jimple.infoflow.test.methodSummary.ParaToField: soot.jimple.infoflow.test.methodSummary.Data dataField>",DATACLASS_OBJECT_FIELD));
//		assertTrue(containsParaToFieldFlow(flow,1,OBJECT_ARRAY,NO_ACCESS_PATH,"<soot.jimple.infoflow.test.methodSummary.ParaToField: java.util.List listField>",LINKEDLIST_LAST));
//		assertTrue(containsParaToFieldFlow(flow,1,OBJECT_ARRAY,NO_ACCESS_PATH,"<soot.jimple.infoflow.test.methodSummary.ParaToField: java.util.List listField>",LINKEDLIST_LAST));
//		assertTrue(containsParaToFieldFlow(flow,1,OBJECT_ARRAY,NO_ACCESS_PATH,"<soot.jimple.infoflow.test.methodSummary.ParaToField: java.lang.Object[] arrayField>",NO_ACCESS_PATH));
//		assertTrue(containsParaToFieldFlow(flow,0,INT_ARRAY,NO_ACCESS_PATH,"<soot.jimple.infoflow.test.methodSummary.ParaToField: int[] intArray>",NO_ACCESS_PATH));
		assertTrue(flow.size() == 9);
	}

	@Test(timeout = 300000)
	public void dataAndListParameterNotCompled() {
		SummaryGenerator s = summaryGenerator();
		String mSig = "<soot.jimple.infoflow.test.methodSummary.ParaToField: void dataAndList(soot.jimple.infoflow.test.methodSummary.Data,java.util.List)>";
		Set<AbstractMethodFlow> flow = s.createMethodSummary(mSig).getFlowsForMethod(mSig);
		
		assertTrue(containsFlow(flow, Parameter,0,new String[] {DATACLASS_INT_FIELD}, Field,new String[] {"<soot.jimple.infoflow.test.methodSummary.ParaToField: int intField>"}));
		assertTrue(containsFlow(flow, Parameter,0,new String[] {DATACLASS_OBJECT_FIELD}, Field,new String[] {"<soot.jimple.infoflow.test.methodSummary.ParaToField: soot.jimple.infoflow.test.methodSummary.Data dataField>",DATACLASS_OBJECT_FIELD}));
		assertTrue(containsFlow(flow, Parameter,0,new String[] {DATACLASS_INT_FIELD}, Field,new String[] {"<soot.jimple.infoflow.test.methodSummary.ParaToField: soot.jimple.infoflow.test.methodSummary.Data dataField>",DATACLASS_INT_FIELD}));
		//assertTrue(containsFlow(flow, Parameter,1,new String[] {LINKEDLIST_FIRST}, Field,new String[] {"<soot.jimple.infoflow.test.methodSummary.ParaToField: java.lang.Object obField>"}));
		assertTrue(containsFlow(flow, Parameter,0,new String[] {DATACLASS_OBJECT_FIELD}, Field,new String[] {"<soot.jimple.infoflow.test.methodSummary.ParaToField: java.lang.Object[] arrayField>"}));
		assertTrue(containsFlow(flow, Parameter,0,new String[] {DATACLASS_INT_FIELD}, Field,new String[] {"<soot.jimple.infoflow.test.methodSummary.ParaToField: int[] intArray>"}));	
		assertTrue(flow.size() == 6);
//		assertTrue(containsParaToFieldFlow(flow,0,DATA,DATACLASS_INT_FIELD,"<soot.jimple.infoflow.test.methodSummary.ParaToField: int intField>",NO_ACCESS_PATH));
//		assertTrue(containsParaToFieldFlow(flow,0,DATA,DATACLASS_OBJECT_FIELD,"<soot.jimple.infoflow.test.methodSummary.ParaToField: soot.jimple.infoflow.test.methodSummary.Data dataField>",DATACLASS_OBJECT_FIELD));
//		assertTrue(containsParaToFieldFlow(flow,1,LIST,LINKEDLIST_FIRST,"<soot.jimple.infoflow.test.methodSummary.ParaToField: java.lang.Object obField>",NO_ACCESS_PATH));
//		assertTrue(containsParaToFieldFlow(flow,0,DATA,DATACLASS_OBJECT_FIELD,"<soot.jimple.infoflow.test.methodSummary.ParaToField: java.lang.Object[] arrayField>",NO_ACCESS_PATH));
//		assertTrue(containsParaToFieldFlow(flow,0,DATA,DATACLASS_INT_FIELD,"<soot.jimple.infoflow.test.methodSummary.ParaToField: int[] intArray>",NO_ACCESS_PATH));
	}
	@Ignore //List is not working
	@Test(timeout = 300000)
	public void dataAndListParameter() {
		SummaryGenerator s = summaryGenerator();
		String mSig = "<soot.jimple.infoflow.test.methodSummary.ParaToField: void dataAndList(soot.jimple.infoflow.test.methodSummary.Data,java.util.List)>";
		Set<AbstractMethodFlow> flow = s.createMethodSummary(mSig).getFlowsForMethod(mSig);
		
		assertTrue(containsFlow(flow, Parameter,0,new String[] {DATACLASS_INT_FIELD}, Field,new String[] {"<soot.jimple.infoflow.test.methodSummary.ParaToField: int intField>"}));
		assertTrue(containsFlow(flow, Parameter,0,new String[] {DATACLASS_OBJECT_FIELD}, Field,new String[] {"<soot.jimple.infoflow.test.methodSummary.ParaToField: soot.jimple.infoflow.test.methodSummary.Data dataField>",DATACLASS_OBJECT_FIELD}));
		
		assertTrue(containsFlow(flow, Parameter,0,new String[] {DATACLASS_OBJECT_FIELD}, Field,new String[] {"<soot.jimple.infoflow.test.methodSummary.ParaToField: java.lang.Object[] arrayField>"}));
		assertTrue(containsFlow(flow, Parameter,0,new String[] {DATACLASS_INT_FIELD}, Field,new String[] {"<soot.jimple.infoflow.test.methodSummary.ParaToField: int[] intArray>"}));
		assertTrue(containsFlow(flow, Parameter,1,new String[] {LINKEDLIST_FIRST}, Field,new String[] {"<soot.jimple.infoflow.test.methodSummary.ParaToField: java.lang.Object obField>"}));
		assertTrue(flow.size() == 6);
//		assertTrue(containsParaToFieldFlow(flow,0,DATA,DATACLASS_INT_FIELD,"<soot.jimple.infoflow.test.methodSummary.ParaToField: int intField>",NO_ACCESS_PATH));
//		assertTrue(containsParaToFieldFlow(flow,0,DATA,DATACLASS_OBJECT_FIELD,"<soot.jimple.infoflow.test.methodSummary.ParaToField: soot.jimple.infoflow.test.methodSummary.Data dataField>",DATACLASS_OBJECT_FIELD));
//		assertTrue(containsParaToFieldFlow(flow,1,LIST,LINKEDLIST_FIRST,"<soot.jimple.infoflow.test.methodSummary.ParaToField: java.lang.Object obField>",NO_ACCESS_PATH));
//		assertTrue(containsParaToFieldFlow(flow,0,DATA,DATACLASS_OBJECT_FIELD,"<soot.jimple.infoflow.test.methodSummary.ParaToField: java.lang.Object[] arrayField>",NO_ACCESS_PATH));
//		assertTrue(containsParaToFieldFlow(flow,0,DATA,DATACLASS_INT_FIELD,"<soot.jimple.infoflow.test.methodSummary.ParaToField: int[] intArray>",NO_ACCESS_PATH));
	}

	@Test(timeout = 400000)
	public void dataAndListParameter2NotComplet() {
		
		SummaryGenerator s =  summaryGenerator();
		String mSig = "<soot.jimple.infoflow.test.methodSummary.ParaToField: void dataAndList(soot.jimple.infoflow.test.methodSummary.Data,java.util.List)>";
		Set<AbstractMethodFlow> flow = s.createMethodSummary(mSig).getFlowsForMethod(mSig);
		
		assertTrue(containsFlow(flow, Parameter,0,new String[] {DATACLASS_INT_FIELD}, Field,new String[] {"<soot.jimple.infoflow.test.methodSummary.ParaToField: int intField>"}));
		assertTrue(containsFlow(flow, Parameter,0,new String[] {DATACLASS_INT_FIELD}, Field,new String[] {DATA_FIELD,DATACLASS_INT_FIELD}));
		assertTrue(containsFlow(flow, Parameter,0,new String[] {DATACLASS_INT_FIELD}, Field,new String[] {"<soot.jimple.infoflow.test.methodSummary.ParaToField: int[] intArray>"}));
		assertTrue(containsFlow(flow, Parameter,0,new String[] {DATACLASS_OBJECT_FIELD}, Field,new String[] {DATA_FIELD, DATACLASS_OBJECT_FIELD}));
		assertTrue(containsFlow(flow, Parameter,0,new String[] {DATACLASS_OBJECT_FIELD}, Field,new String[] {"<soot.jimple.infoflow.test.methodSummary.ParaToField: java.lang.Object[] arrayField>"}));
		
		
		//assertTrue(containsFlow(flow, Parameter,1,new String[] {LINKEDLIST_FIRST}, Field,new String[] {"<soot.jimple.infoflow.test.methodSummary.ParaToField: java.lang.Object obField>"}));
		//assertTrue(containsFlow(flow, Parameter,1,new String[] {LINKEDLIST_FIRST}, Field,new String[] {"<soot.jimple.infoflow.test.methodSummary.ParaToField: java.util.List listField>"}));
//		assertTrue(containsParaToFieldFlow(flow,0,DATA,DATACLASS_INT_FIELD,"<soot.jimple.infoflow.test.methodSummary.ParaToField: int intField>",NO_ACCESS_PATH));
//		assertTrue(containsParaToFieldFlow(flow,0,DATA,DATACLASS_OBJECT_FIELD,"<soot.jimple.infoflow.test.methodSummary.ParaToField: soot.jimple.infoflow.test.methodSummary.Data dataField>",NO_ACCESS_PATH));
//		assertTrue(containsParaToFieldFlow(flow,1,LIST,LINKEDLIST_FIRST,"<soot.jimple.infoflow.test.methodSummary.ParaToField: java.lang.Object obField>",NO_ACCESS_PATH));
//		assertTrue(containsParaToFieldFlow(flow,1,LIST,LINKEDLIST_FIRST,"<soot.jimple.infoflow.test.methodSummary.ParaToField: java.util.List listField>",NO_ACCESS_PATH));
//		assertTrue(containsParaToFieldFlow(flow,0,DATA,DATACLASS_OBJECT_FIELD,"<soot.jimple.infoflow.test.methodSummary.ParaToField: java.lang.Object[] arrayField>",NO_ACCESS_PATH));
//		assertTrue(containsParaToFieldFlow(flow,0,DATA,DATACLASS_INT_FIELD,"<soot.jimple.infoflow.test.methodSummary.ParaToField: int[] intArray>",NO_ACCESS_PATH));
		assertTrue(flow.size() == 6);
	}
	@Ignore
	@Test(timeout = 400000)
	public void dataAndListParameter2() {
		
		SummaryGenerator s =  summaryGenerator();
		String mSig = "<soot.jimple.infoflow.test.methodSummary.ParaToField: void dataAndList(soot.jimple.infoflow.test.methodSummary.Data,java.util.List)>";
		Set<AbstractMethodFlow> flow = s.createMethodSummary(mSig).getFlowsForMethod(mSig);
		
		assertTrue(containsFlow(flow, Parameter,0,new String[] {DATACLASS_INT_FIELD}, Field,new String[] {"<soot.jimple.infoflow.test.methodSummary.ParaToField: int intField>"}));
		assertTrue(containsFlow(flow, Parameter,0,new String[] {DATACLASS_INT_FIELD}, Field,new String[] {DATA_FIELD,DATACLASS_INT_FIELD}));
		assertTrue(containsFlow(flow, Parameter,0,new String[] {DATACLASS_INT_FIELD}, Field,new String[] {"<soot.jimple.infoflow.test.methodSummary.ParaToField: int[] intArray>"}));
		assertTrue(containsFlow(flow, Parameter,0,new String[] {DATACLASS_OBJECT_FIELD}, Field,new String[] {DATA_FIELD, DATACLASS_OBJECT_FIELD}));
		assertTrue(containsFlow(flow, Parameter,0,new String[] {DATACLASS_OBJECT_FIELD}, Field,new String[] {"<soot.jimple.infoflow.test.methodSummary.ParaToField: java.lang.Object[] arrayField>"}));
		
		
		assertTrue(containsFlow(flow, Parameter,1,new String[] {LINKEDLIST_FIRST,LINKEDLIST_ITEM}, Field,new String[] {"<soot.jimple.infoflow.test.methodSummary.ParaToField: java.lang.Object obField>"}));
		assertTrue(containsFlow(flow, Parameter,1,new String[] {LINKEDLIST_FIRST,LINKEDLIST_ITEM}, Field,new String[] {"<soot.jimple.infoflow.test.methodSummary.ParaToField: java.util.List listField>"}));
//		assertTrue(containsParaToFieldFlow(flow,0,DATA,DATACLASS_INT_FIELD,"<soot.jimple.infoflow.test.methodSummary.ParaToField: int intField>",NO_ACCESS_PATH));
//		assertTrue(containsParaToFieldFlow(flow,0,DATA,DATACLASS_OBJECT_FIELD,"<soot.jimple.infoflow.test.methodSummary.ParaToField: soot.jimple.infoflow.test.methodSummary.Data dataField>",NO_ACCESS_PATH));
//		assertTrue(containsParaToFieldFlow(flow,1,LIST,LINKEDLIST_FIRST,"<soot.jimple.infoflow.test.methodSummary.ParaToField: java.lang.Object obField>",NO_ACCESS_PATH));
//		assertTrue(containsParaToFieldFlow(flow,1,LIST,LINKEDLIST_FIRST,"<soot.jimple.infoflow.test.methodSummary.ParaToField: java.util.List listField>",NO_ACCESS_PATH));
//		assertTrue(containsParaToFieldFlow(flow,0,DATA,DATACLASS_OBJECT_FIELD,"<soot.jimple.infoflow.test.methodSummary.ParaToField: java.lang.Object[] arrayField>",NO_ACCESS_PATH));
//		assertTrue(containsParaToFieldFlow(flow,0,DATA,DATACLASS_INT_FIELD,"<soot.jimple.infoflow.test.methodSummary.ParaToField: int[] intArray>",NO_ACCESS_PATH));
		assertTrue(flow.size() == 6);
	}
	
	@Override
	Class getClazz() {
		return ParaToFieldTests.class;
	}
	
	private SummaryGenerator summaryGenerator() {
		SummaryGenerator sg = new SummaryGenerator() ;
		
		List<String> sub = new LinkedList<String>();
		sub.add("java.util.LinkedList");
		sg.setSubstitutedWith(sub);
		return sg;  
	}
}
