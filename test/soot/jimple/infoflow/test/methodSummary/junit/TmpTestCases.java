package soot.jimple.infoflow.test.methodSummary.junit;

import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.junit.Test;

import soot.jimple.infoflow.methodSummary.SummaryGenerator;
import soot.jimple.infoflow.methodSummary.data.AbstractMethodFlow;

public class TmpTestCases extends TestHelper {
	
	@Test
	public void listGet() {
		SummaryGenerator s = new SummaryGenerator();
		String mSig = "<soot.jimple.infoflow.test.methodSummary.SimpleList: java.lang.Object get()>";
		Set<AbstractMethodFlow> flow = s.createMethodSummary(mSig).getFlowsForMethod(mSig);
		assertTrue(flow.size() == 1);
	}
}
