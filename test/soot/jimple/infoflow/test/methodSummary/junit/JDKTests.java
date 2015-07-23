package soot.jimple.infoflow.test.methodSummary.junit;

import java.util.Collections;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import soot.jimple.infoflow.methodSummary.data.MethodFlow;
import soot.jimple.infoflow.methodSummary.data.summary.MethodSummaries;
import soot.jimple.infoflow.methodSummary.generator.SummaryGenerator;
import soot.jimple.infoflow.methodSummary.generator.SummaryGeneratorFactory;

public class JDKTests extends TestHelper {

	static final String className = "java.util.ArrayList";
		
	@Test(timeout = 100000)
	public void arrayListRemoveAll() {
		String mSig = "<java.util.ArrayList: boolean removeAll(java.util.Collection)>";
		Set<MethodFlow> flow = createSummaries(mSig).getAllFlows();
		Assert.assertNotNull(flow);
	}
	
	@Test(timeout = 100000)
	public void arrayListIterator() {
		String mSig = "<java.util.ArrayList: java.util.Iterator iterator()>";
		Set<MethodFlow> flow = createSummaries(mSig).getAllFlows();
		Assert.assertNotNull(flow);
	}
	
	@Test(timeout = 100000)
	public void abstractListEquals() {
		String mSig = "<java.util.AbstractList: boolean equals(java.lang.Object)>";
		Set<MethodFlow> flow = createSummaries(mSig).getAllFlows();
		Assert.assertNotNull(flow);
	}
	
	@Test(timeout = 100000)
	public void arrayListTest() {
		SummaryGenerator generator = new SummaryGeneratorFactory().initSummaryGenerator();
		MethodSummaries summaries = generator.createMethodSummaries(libPath,
				Collections.singleton("java.util.ArrayList"));
		Set<MethodFlow> flow = summaries.getAllFlows();
		Assert.assertNotNull(flow);
	}
		
	@Test(timeout = 100000)
	public void weakHashMapPut() {
		String mSig = "<java.util.WeakHashMap: java.lang.Object put(java.lang.Object,java.lang.Object)>";
		Set<MethodFlow> flow = createSummaries(mSig).getAllFlows();
		Assert.assertNotNull(flow);
	}
	
	@Test(timeout = 100000)
	public void gapTest1() {
		String mSig = "<java.util.Collections$UnmodifiableMap$UnmodifiableEntrySet: boolean containsAll(java.util.Collection)>";
		Set<MethodFlow> flow = createSummaries(mSig).getAllFlows();
		Assert.assertNotNull(flow);
	}
	
	@Test(timeout = 100000)
	public void gapTest2() {
		SummaryGenerator generator = new SummaryGeneratorFactory().initSummaryGenerator();
		MethodSummaries summaries = generator.createMethodSummaries(libPath,
				Collections.singleton("java.util.Collections$UnmodifiableMap$UnmodifiableEntrySet"));
		Set<MethodFlow> flow = summaries.getAllFlows();
		Assert.assertNotNull(flow);
	}
	
	@Override
	protected SummaryGenerator getSummary() {
		SummaryGenerator sg = new SummaryGenerator();
		sg.setAccessPathLength(4);
		sg.setSummaryAPLength(3);
		sg.setLoadFullJAR(false);
		sg.setIgnoreFlowsInSystemPackages(false);
		sg.setEnableExceptionTracking(true);
		return sg;
	}
}
