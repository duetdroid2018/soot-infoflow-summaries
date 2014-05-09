package soot.jimple.infoflow.test.methodSummary.junit;

import static org.junit.Assert.*;
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
		
		assertEquals(3,flow.size());
		
	}

	@Test(timeout = 100000)
	public void intParameterRec() {
		SummaryGenerator s =  summaryGenerator();
		String mSig = "<soot.jimple.infoflow.test.methodSummary.ParaToField: void intParaRec(int,int)>";
		Set<AbstractMethodFlow> flow = s.createMethodSummary(mSig).getFlowsForMethod(mSig);
		assertTrue(containsFlow(flow, Parameter,0,new String[] {}, Field,new String[] {"<soot.jimple.infoflow.test.methodSummary.ParaToField: int intField>"}));
		assertTrue(containsFlow(flow, Parameter,0,new String[] {}, Field,new String[] {"<soot.jimple.infoflow.test.methodSummary.ParaToField: soot.jimple.infoflow.test.methodSummary.Data dataField>",DATACLASS_INT_FIELD}));
		assertTrue(containsFlow(flow, Parameter,0,new String[] {}, Field,new String[] {"<soot.jimple.infoflow.test.methodSummary.ParaToField: int[] intArray>"}));
		
		assertEquals(3,flow.size());
	}

	@Test(timeout = 100000)
	public void objectParameter() {
		SummaryGenerator s =  summaryGenerator();
		String mSig = "<soot.jimple.infoflow.test.methodSummary.ParaToField: void objPara(java.lang.Object)>";
		Set<AbstractMethodFlow> flow = s.createMethodSummary(mSig).getFlowsForMethod(mSig);
		
		assertTrue(containsFlow(flow, Parameter,0,new String[] {}, Field,new String[] {"<soot.jimple.infoflow.test.methodSummary.ParaToField: java.lang.Object obField>"}));
		assertTrue(containsFlow(flow, Parameter,0,new String[] {}, Field,new String[] {"<soot.jimple.infoflow.test.methodSummary.ParaToField: java.lang.Object[] arrayField>"}));
		
	}

	@Test(timeout = 100000)
	public void objectParameter2() {
		SummaryGenerator s =  summaryGenerator();
		String mSig = "<soot.jimple.infoflow.test.methodSummary.ParaToField: void objPara(java.lang.Object)>";
		Set<AbstractMethodFlow> flow = s.createMethodSummary(mSig).getFlowsForMethod(mSig);
		
		assertTrue(containsFlow(flow, Parameter,0,new String[] {}, Field,new String[] {"<soot.jimple.infoflow.test.methodSummary.ParaToField: soot.jimple.infoflow.test.methodSummary.Data dataField>",DATACLASS_OBJECT_FIELD}));
		assertTrue(containsFlow(flow, Parameter,0,new String[] {}, Field,LIST_ITEM));
		
		assertEquals(6,flow.size());
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
		
		assertEquals(9,flow.size());
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
		
		assertEquals(9,flow.size());
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
		assertEquals(9,flow.size());
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
		assertEquals(9,flow.size());
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
		assertEquals(6,flow.size());
	}
	@Ignore //We dont identify list.first.item as a source. that is because first = null
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
		assertEquals(6,flow.size());
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
		
		assertEquals(6,flow.size());
		}
	@Ignore //LinkedList works that as parameter source because we have list.null.null and then the points to doesnt work correctly
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
		assertEquals(6,flow.size());
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
