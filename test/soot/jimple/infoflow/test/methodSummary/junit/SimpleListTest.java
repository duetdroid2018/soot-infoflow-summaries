package soot.jimple.infoflow.test.methodSummary.junit;

import java.util.Set;

import junit.framework.Assert;

import org.junit.Test;

import soot.jimple.infoflow.methodSummary.SummaryGenerator;
import soot.jimple.infoflow.methodSummary.data.AbstractMethodFlow;

public class SimpleListTest extends TestHelper {
	
	@Test
	public void listGet() {
		SummaryGenerator s = new SummaryGenerator();
		String mSig = "<soot.jimple.infoflow.test.methodSummary.SimpleList: java.lang.Object get()>";
		Set<AbstractMethodFlow> flow = s.createMethodSummary(mSig).getFlowsForMethod(mSig);
		Assert.assertEquals(1, flow.size());
	}
	
	@Test
	public void keySet() {
		SummaryGenerator s = new SummaryGenerator();
		String mSig = "<soot.jimple.infoflow.test.methodSummary.SimpleList: java.lang.Object keySet()>";
		Set<AbstractMethodFlow> flow = s.createMethodSummary(mSig).getFlowsForMethod(mSig);
		Assert.assertEquals(1, flow.size());
	}
}
