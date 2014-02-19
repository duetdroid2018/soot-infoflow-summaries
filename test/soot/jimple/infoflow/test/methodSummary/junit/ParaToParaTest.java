package soot.jimple.infoflow.test.methodSummary.junit;

import static org.junit.Assert.assertTrue;


import java.util.Map;
import java.util.Set;

import org.junit.Ignore;
import org.junit.Test;

import soot.jimple.infoflow.methodSummary.SummaryGenerator;
import soot.jimple.infoflow.methodSummary.data.AbstractMethodFlow;

public class ParaToParaTest extends TestHelper {
	protected static Map<String, Set<AbstractMethodFlow>> flows;
	static boolean executeSummary = true;
	static final String className = "soot.jimple.infoflow.test.methodSummary.ParaToParaFlows";

	@Test(timeout = 100000)
	public void array() {
		SummaryGenerator s = new SummaryGenerator();
		String mSig = "<soot.jimple.infoflow.test.methodSummary.ParaToParaFlows: void array(java.lang.Object,java.lang.Object[])>";
		Set<AbstractMethodFlow> flow = s.createMethodSummary(mSig).getFlowsForMethod(mSig);
		assertTrue(containsParaToParaFlow(flow,0,OBJECT,NO_ACCESS_PATH,1,OBJECT_ARRAY,NO_ACCESS_PATH));
		assertTrue(flow.size() == 1);
	}

	@Test(timeout = 100000)
	public void arrayRec() {
		SummaryGenerator s = new SummaryGenerator();
		String mSig = "<soot.jimple.infoflow.test.methodSummary.ParaToParaFlows: void arrayRec(java.lang.Object,java.lang.Object[],int)>";
		Set<AbstractMethodFlow> flow = s.createMethodSummary(mSig).getFlowsForMethod(mSig);
		assertTrue(containsParaToParaFlow(flow,0,OBJECT,NO_ACCESS_PATH,1,OBJECT_ARRAY,NO_ACCESS_PATH));
		assertTrue(flow.size() == 1);
	}

	@Test(timeout = 100000)
	public void list() {
		SummaryGenerator s = new SummaryGenerator();
		String mSig = "<soot.jimple.infoflow.test.methodSummary.ParaToParaFlows: int list(java.util.List,java.lang.Object)>";
		Set<AbstractMethodFlow> flow = s.createMethodSummary(mSig).getFlowsForMethod(mSig);

		assertTrue(containsParaToParaFlow(flow,1,OBJECT,NO_ACCESS_PATH,0,LIST,LINKEDLIST_LAST));
		assertTrue(containsParaToParaFlow(flow,1,OBJECT,NO_ACCESS_PATH,0,LIST,LINKEDLIST_FIRST));
		
	}

	@Ignore
	public void list2() {
		SummaryGenerator s = new SummaryGenerator();
		String mSig = "<soot.jimple.infoflow.test.methodSummary.ParaToParaFlows: int list(java.util.List,java.lang.Object)>";
		Set<AbstractMethodFlow> flow = s.createMethodSummary(mSig).getFlowsForMethod(mSig);

		assertTrue(containsParaToParaFlow(flow,1,OBJECT,NO_ACCESS_PATH,0,LIST,LINKEDLIST_LAST));
		assertTrue(containsParaToParaFlow(flow,1,OBJECT,NO_ACCESS_PATH,0,LIST,LINKEDLIST_FIRST));
		assertTrue(flow.size() ==2);
	}
	@Test(timeout = 100000)
	public void setter() {
		SummaryGenerator s = new SummaryGenerator();
		String mSig = "<soot.jimple.infoflow.test.methodSummary.ParaToParaFlows: int setter(java.lang.String,soot.jimple.infoflow.test.methodSummary.Data)>";
		Set<AbstractMethodFlow> flow = s.createMethodSummary(mSig).getFlowsForMethod(mSig);
		assertTrue(containsParaToParaFlow(flow,0,STRING,NO_ACCESS_PATH,1,DATA,DATACLASS_STRING_FIELD));
		assertTrue(flow.size() == 1);
	}

	@Test(timeout = 100000)
	public void setter2() {
		SummaryGenerator s = new SummaryGenerator();
		String mSig = "<soot.jimple.infoflow.test.methodSummary.ParaToParaFlows: int setter2(int,soot.jimple.infoflow.test.methodSummary.Data)>";
		Set<AbstractMethodFlow> flow = s.createMethodSummary(mSig).getFlowsForMethod(mSig);
		assertTrue(containsParaToParaFlow(flow,0,INT,NO_ACCESS_PATH,1,DATA,DATACLASS_INT_FIELD));
		assertTrue(flow.size() == 1);
	}

	@Test(timeout = 100000)
	public void innerClass() {
		SummaryGenerator s = new SummaryGenerator();
		String mSig = "<soot.jimple.infoflow.test.methodSummary.ParaToParaFlows: void innerClass(java.lang.Object,soot.jimple.infoflow.test.methodSummary.ParaToParaFlows$InnerClass)>";
		Set<AbstractMethodFlow> flow = s.createMethodSummary(mSig).getFlowsForMethod(mSig);
		assertTrue(containsParaToParaFlow(flow,0,OBJECT,NO_ACCESS_PATH,1,"soot.jimple.infoflow.test.methodSummary.ParaToParaFlows$InnerClass","<soot.jimple.infoflow.test.methodSummary.ParaToParaFlows$InnerClass: java.lang.Object o>"));
		assertTrue(flow.size() == 1);
	}
}
