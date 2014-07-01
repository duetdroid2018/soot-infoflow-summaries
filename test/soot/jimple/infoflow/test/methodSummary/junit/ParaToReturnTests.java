package soot.jimple.infoflow.test.methodSummary.junit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static soot.jimple.infoflow.methodSummary.data.SourceSinkType.Parameter;
import static soot.jimple.infoflow.methodSummary.data.SourceSinkType.Return;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Ignore;
import org.junit.Test;

import soot.jimple.infoflow.methodSummary.data.MethodFlow;
import soot.jimple.infoflow.methodSummary.generator.SummaryGenerator;
import soot.jimple.infoflow.test.methodSummary.ArbitraryAccessPath;

public class ParaToReturnTests extends TestHelper {
	protected static Map<String, Set<MethodFlow>> flows;
	static final String className = "soot.jimple.infoflow.test.methodSummary.ParaToReturn";
	static boolean executeSummary = true;

	@Test(timeout = 100000)
	public void primitiv() {
		SummaryGenerator s = getSummary();
		String mSig = "<soot.jimple.infoflow.test.methodSummary.ParaToReturn: int return1(int)>";
		Set<MethodFlow> flow = s.createMethodSummary(mSig).getFlowsForMethod(mSig);

		assertTrue(containsFlow(flow, Parameter, 0, new String[] {}, Return, new String[] {}));
		assertEquals(1,flow.size());
		}

	@Test(timeout = 100000)
	public void primitivRec() {
		SummaryGenerator s = getSummary();
		String mSig = "<soot.jimple.infoflow.test.methodSummary.ParaToReturn: int returnRec(int,int)>";
		Set<MethodFlow> flow = s.createMethodSummary(mSig).getFlowsForMethod(mSig);

		assertTrue(containsFlow(flow, Parameter, 0, new String[] {}, Return, new String[] {}));
		assertEquals(1,flow.size());
	}

	@Test(timeout = 100000)
	public void object() {

		SummaryGenerator s = getSummary();
		String mSig = "<soot.jimple.infoflow.test.methodSummary.ParaToReturn: java.lang.Object return2(java.lang.Object)>";
		Set<MethodFlow> flow = s.createMethodSummary(mSig).getFlowsForMethod(mSig);

		assertTrue(containsFlow(flow, Parameter, 0, new String[] {}, Return, new String[] {}));
		assertEquals(1,flow.size());
	}

	@Test(timeout = 100000)
	public void list() {

		SummaryGenerator s = getSummary();
		String mSig = "<soot.jimple.infoflow.test.methodSummary.ParaToReturn: java.util.List return3(java.util.List)>";
		Set<MethodFlow> flow = s.createMethodSummary(mSig).getFlowsForMethod(mSig);

		assertTrue(containsFlow(flow, Parameter, 0, new String[] {}, Return, new String[] {}));
		assertEquals(1,flow.size());
	}

	@Ignore //list.node.item is not identified as source
	@Test(timeout = 100000)
	public void list2() {
		SummaryGenerator s = getSummary();
		String mSig = "<soot.jimple.infoflow.test.methodSummary.ParaToReturn: java.lang.Object return31(java.util.List)>";
		Set<MethodFlow> flow = s.createMethodSummary(mSig,methods()).getFlowsForMethod(mSig);

		assertTrue(containsFlow(flow, Parameter, 0, new String[] {LINKEDLIST_FIRST,LINKEDLIST_ITEM}, Return, new String[] {}));
		assertEquals(1,flow.size());
	}

	@Test(timeout = 100000)
	public void array1() {
		SummaryGenerator s = getSummary();
		String mSig = "<soot.jimple.infoflow.test.methodSummary.ParaToReturn: java.lang.Object return4(java.lang.Object[])>";
		Set<MethodFlow> flow = s.createMethodSummary(mSig).getFlowsForMethod(mSig);

		assertTrue(containsFlow(flow, Parameter, 0, new String[] {}, Return, new String[] {}));
		assertEquals(1,flow.size());
	}

	@Test(timeout = 100000)
	public void array2() {
		SummaryGenerator s = getSummary();
		String mSig = "<soot.jimple.infoflow.test.methodSummary.ParaToReturn: java.lang.Object[] return5(java.lang.Object[])>";
		Set<MethodFlow> flow = s.createMethodSummary(mSig).getFlowsForMethod(mSig);

		assertTrue(containsFlow(flow, Parameter, 0, new String[] {}, Return, new String[] {}));
		assertEquals(1,flow.size());
	}

	@Test(timeout = 100000)
	public void data1() {
		SummaryGenerator s = getSummary();
		String mSig = "<soot.jimple.infoflow.test.methodSummary.ParaToReturn: java.lang.Object return6(soot.jimple.infoflow.test.methodSummary.Data)>";
		Set<MethodFlow> flow = s.createMethodSummary(mSig).getFlowsForMethod(mSig);

		assertTrue(containsFlow(flow, Parameter, 0, new String[] { DATACLASS_OBJECT_FIELD }, Return, new String[] {}));
		assertEquals(1,flow.size());
	}

	@Test(timeout = 100000)
	public void data2() {
		SummaryGenerator s = getSummary();
		String mSig = "<soot.jimple.infoflow.test.methodSummary.ParaToReturn: java.lang.Object return7(soot.jimple.infoflow.test.methodSummary.Data)>";
		Set<MethodFlow> flow = s.createMethodSummary(mSig).getFlowsForMethod(mSig);

		assertTrue(containsFlow(flow, Parameter, 0, new String[] { DATACLASS_OBJECT_FIELD }, Return, new String[] {}));
		assertEquals(1,flow.size());
	}

	@Override
	Class<?> getClazz() {
		return ArbitraryAccessPath.class;
	}
	
	@Override
	SummaryGenerator getSummary() {
		SummaryGenerator sg = new SummaryGenerator();
		List<String> sub = new LinkedList<String>();
		sub.add("java.util.LinkedList");
		sg.setSubstitutedWith(sub);
		sg.setUseRecursiveAccessPaths(true);
		sg.setAnalyseMethodsTogether(false);
		sg.setAccessPathLength(3);
		sg.setIgnoreFlowsInSystemPackages(false);
		return sg;
	}
}
