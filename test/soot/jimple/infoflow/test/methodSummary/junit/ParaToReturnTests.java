package soot.jimple.infoflow.test.methodSummary.junit;

import static org.junit.Assert.assertTrue;

import java.util.Map;
import java.util.Set;

import org.junit.Test;

import soot.jimple.infoflow.methodSummary.SummaryGenerator;
import soot.jimple.infoflow.methodSummary.data.AbstractMethodFlow;

public class ParaToReturnTests extends TestHelper{
	protected static Map<String, Set<AbstractMethodFlow>> flows;
	static final String className = "soot.jimple.infoflow.test.methodSummary.ParaToReturn";
	static boolean executeSummary = true;

	
	
	@Test(timeout = 100000)
	public void primitiv() {
		SummaryGenerator s = new SummaryGenerator();
		String mSig = "<soot.jimple.infoflow.test.methodSummary.ParaToReturn: int return1(int)>";
		Set<AbstractMethodFlow> flow = s.createMethodSummary(mSig).getFlowsForMethod(mSig);

		assertTrue(containsParaToReturn(flow,0,INT,NO_ACCESS_PATH,NO_ACCESS_PATH));
		assertTrue(flow.size() == 1);
	}

	@Test(timeout = 100000)
	public void primitivRec() {
		SummaryGenerator s = new SummaryGenerator();
		String mSig = "<soot.jimple.infoflow.test.methodSummary.ParaToReturn: int returnRec(int,int)>";
		Set<AbstractMethodFlow> flow = s.createMethodSummary(mSig).getFlowsForMethod(mSig);

		assertTrue(containsParaToReturn(flow,0,INT,NO_ACCESS_PATH,NO_ACCESS_PATH));
		//assertTrue(flow.size() == 1);
	}

	@Test(timeout = 100000)
	public void object() {
		
		SummaryGenerator s = new SummaryGenerator();
		String mSig = "<soot.jimple.infoflow.test.methodSummary.ParaToReturn: java.lang.Object return2(java.lang.Object)>";
		Set<AbstractMethodFlow> flow = s.createMethodSummary(mSig).getFlowsForMethod(mSig);

		assertTrue(containsParaToReturn(flow,0,OBJECT,NO_ACCESS_PATH,NO_ACCESS_PATH));
		assertTrue(flow.size() == 1);
	}

	@Test(timeout = 100000)
	public void list() {
		
		SummaryGenerator s = new SummaryGenerator();
		String mSig = "<soot.jimple.infoflow.test.methodSummary.ParaToReturn: java.util.List return3(java.util.List)>";
		Set<AbstractMethodFlow> flow = s.createMethodSummary(mSig).getFlowsForMethod(mSig);

		assertTrue(containsParaToReturn(flow,0,LIST,NO_ACCESS_PATH,NO_ACCESS_PATH));
		assertTrue(flow.size() == 1);
	}

	@Test(timeout = 100000)
	public void list2() {
		SummaryGenerator s = new SummaryGenerator();
		String mSig = "<soot.jimple.infoflow.test.methodSummary.ParaToReturn: java.lang.Object return31(java.util.List)>";
		Set<AbstractMethodFlow> flow = s.createMethodSummary(mSig).getFlowsForMethod(mSig);

		assertTrue(containsParaToReturn(flow,0,LIST,LINKEDLIST_FIRST,NO_ACCESS_PATH));
		//assertTrue(flow.size() == 1);
	}

	@Test(timeout = 100000)
	public void array1() {
		SummaryGenerator s = new SummaryGenerator();
		String mSig = "<soot.jimple.infoflow.test.methodSummary.ParaToReturn: java.lang.Object return4(java.lang.Object[])>";
		Set<AbstractMethodFlow> flow = s.createMethodSummary(mSig).getFlowsForMethod(mSig);

		assertTrue(containsParaToReturn(flow,0,OBJECT_ARRAY,NO_ACCESS_PATH,NO_ACCESS_PATH));
		assertTrue(flow.size() == 1);
	}

	@Test(timeout = 100000)
	public void array2() {
		SummaryGenerator s = new SummaryGenerator();
		String mSig = "<soot.jimple.infoflow.test.methodSummary.ParaToReturn: java.lang.Object[] return5(java.lang.Object[])>";
		Set<AbstractMethodFlow> flow = s.createMethodSummary(mSig).getFlowsForMethod(mSig);

		assertTrue(containsParaToReturn(flow,0,OBJECT_ARRAY,NO_ACCESS_PATH,NO_ACCESS_PATH));
		assertTrue(flow.size() == 1);
	}

	@Test(timeout = 100000)
	public void data1() {
		SummaryGenerator s = new SummaryGenerator();
		String mSig = "<soot.jimple.infoflow.test.methodSummary.ParaToReturn: java.lang.Object return6(soot.jimple.infoflow.test.methodSummary.Data)>";
		Set<AbstractMethodFlow> flow = s.createMethodSummary(mSig).getFlowsForMethod(mSig);

		assertTrue(containsParaToReturn(flow,0,DATA,DATACLASS_OBJECT_FIELD,NO_ACCESS_PATH));
		//assertTrue(flow.size() == 1);
	}

	@Test(timeout = 100000)
	public void data2() {
		SummaryGenerator s = new SummaryGenerator();
		String mSig = "<soot.jimple.infoflow.test.methodSummary.ParaToReturn: java.lang.Object return7(soot.jimple.infoflow.test.methodSummary.Data)>";
		Set<AbstractMethodFlow> flow = s.createMethodSummary(mSig).getFlowsForMethod(mSig);

		assertTrue(containsParaToReturn(flow,0,DATA,DATACLASS_OBJECT_FIELD,NO_ACCESS_PATH));
		//assertTrue(flow.size() == 1);
	}

}
