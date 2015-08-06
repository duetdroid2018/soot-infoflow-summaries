package soot.jimple.infoflow.test.methodSummary.junit;

import java.util.Collections;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import soot.jimple.infoflow.InfoflowConfiguration;
import soot.jimple.infoflow.methodSummary.data.summary.ClassSummaries;
import soot.jimple.infoflow.methodSummary.data.summary.MethodFlow;
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
		ClassSummaries summaries = generator.createMethodSummaries(libPath,
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
		ClassSummaries summaries = generator.createMethodSummaries(libPath,
				Collections.singleton("java.util.Collections$UnmodifiableMap$UnmodifiableEntrySet"));
		Set<MethodFlow> flow = summaries.getAllFlows();
		Assert.assertNotNull(flow);
	}
	
	@Test(timeout = 300000)
	public void gapTest3() {
		SummaryGenerator generator = new SummaryGeneratorFactory().initSummaryGenerator();
		ClassSummaries summaries = generator.createMethodSummaries(libPath,
				Collections.singleton("java.util.HashMap$EntrySet"));
		Set<MethodFlow> flow = summaries.getAllFlows();
		Assert.assertNotNull(flow);
	}
	
	@Test(timeout = 300000)
	public void gapTest4() {
		SummaryGenerator generator = new SummaryGeneratorFactory().initSummaryGenerator();
		ClassSummaries summaries = generator.createMethodSummaries(libPath,
				Collections.singleton("java.util.concurrent.ConcurrentHashMap$Values"));
		Set<MethodFlow> flow = summaries.getAllFlows();
		Assert.assertNotNull(flow);
	}
	
	@Test(timeout = 300000)
	public void gapTest5() {
		SummaryGenerator generator = new SummaryGeneratorFactory().initSummaryGenerator();
		ClassSummaries summaries = generator.createMethodSummaries(libPath,
				Collections.singleton("java.lang.ProcessEnvironment$StringKeySet"));
		Set<MethodFlow> flow = summaries.getAllFlows();
		Assert.assertNotNull(flow);
	}
	
	@Test(timeout = 300000)
	public void gapTest6() {
		SummaryGenerator generator = new SummaryGeneratorFactory().initSummaryGenerator();
		ClassSummaries summaries = generator.createMethodSummaries(libPath,
				Collections.singleton("java.util.PriorityQueue"));
		Set<MethodFlow> flow = summaries.getAllFlows();
		Assert.assertNotNull(flow);
	}
	
	@Test//(timeout = 100000)
	public void gapTest7() {
		String mSig = "<java.util.concurrent.ConcurrentSkipListMap: java.lang.Object remove(java.lang.Object)>";
		Set<MethodFlow> flow = createSummaries(mSig).getAllFlows();
		Assert.assertNotNull(flow);
	}
	
	@Override
	protected SummaryGenerator getSummary() {
		SummaryGenerator sg = new SummaryGenerator();
		InfoflowConfiguration.setAccessPathLength(4);
		sg.getConfig().setLoadFullJAR(false);
		sg.getConfig().setEnableExceptionTracking(true);
		return sg;
	}
}
