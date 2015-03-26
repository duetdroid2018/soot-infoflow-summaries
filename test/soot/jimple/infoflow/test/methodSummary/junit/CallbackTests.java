package soot.jimple.infoflow.test.methodSummary.junit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import soot.jimple.infoflow.config.IInfoflowConfig;
import soot.jimple.infoflow.methodSummary.data.SourceSinkType;
import soot.jimple.infoflow.methodSummary.data.summary.MethodSummaries;
import soot.jimple.infoflow.methodSummary.generator.SummaryGenerator;
import soot.options.Options;

public class CallbackTests extends TestHelper {

	static final String className = "soot.jimple.infoflow.test.methodSummary.Callbacks";
	
	private static final String FIELD_CALLBACK = "<soot.jimple.infoflow.test.methodSummary.Callbacks: soot.jimple.infoflow.test.methodSummary.Callbacks$MyCallbacks cbs>";
		
	@Test(timeout = 100000)
	public void paraToCallbackToReturn() {
		String mSig = "<soot.jimple.infoflow.test.methodSummary.Callbacks: java.lang.String paraToCallbackToReturn(java.lang.String,soot.jimple.infoflow.test.methodSummary.Callbacks$MyCallbacks)>";
		MethodSummaries flow = createSummaries(mSig);
		
		// Parameter 1 to gap base object
		assertTrue(containsFlow(flow.getAllFlows(), SourceSinkType.Parameter, 1, null, "",
				SourceSinkType.GapBaseObject, 0, null,
				"<soot.jimple.infoflow.test.methodSummary.Callbacks$MyCallbacks: java.lang.String transform(java.lang.String)>"));
		// Parameter 0 to gap argument 0
		assertTrue(containsFlow(flow.getAllFlows(), SourceSinkType.Parameter, 0, null, "", SourceSinkType.Parameter, 0, null,
				"<soot.jimple.infoflow.test.methodSummary.Callbacks$MyCallbacks: java.lang.String transform(java.lang.String)>"));
		// Gap return value to method return value
		assertTrue(containsFlow(flow.getAllFlows(), SourceSinkType.Return, -1, null,
				"<soot.jimple.infoflow.test.methodSummary.Callbacks$MyCallbacks: java.lang.String transform(java.lang.String)>",
				SourceSinkType.Return, -1, null, ""));
		
		assertEquals(3, flow.getFlowCount());
	}
	
	@Test(timeout = 100000)
	public void fieldCallbackToReturn() {
		String mSig = "<soot.jimple.infoflow.test.methodSummary.Callbacks: java.lang.String fieldCallbackToReturn(java.lang.String)>";
		MethodSummaries flow = createSummaries(mSig);
		
		// Parameter 1 to gap base object
		assertTrue(containsFlow(flow.getAllFlows(), SourceSinkType.Field, -1, new String[] { FIELD_CALLBACK }, "",
				SourceSinkType.GapBaseObject, 0, null,
				"<soot.jimple.infoflow.test.methodSummary.Callbacks$MyCallbacks: java.lang.String transform(java.lang.String)>"));
		// Parameter 0 to gap argument 0
		assertTrue(containsFlow(flow.getAllFlows(), SourceSinkType.Parameter, 0, null, "", SourceSinkType.Parameter, 0, null,
				"<soot.jimple.infoflow.test.methodSummary.Callbacks$MyCallbacks: java.lang.String transform(java.lang.String)>"));
		// Gap return value to method return value
		assertTrue(containsFlow(flow.getAllFlows(), SourceSinkType.Return, -1, null,
				"<soot.jimple.infoflow.test.methodSummary.Callbacks$MyCallbacks: java.lang.String transform(java.lang.String)>",
				SourceSinkType.Return, -1, null, ""));
		// Gap base to "this" field
		assertTrue(containsFlow(flow.getAllFlows(), SourceSinkType.Field, -1, null,
				"<soot.jimple.infoflow.test.methodSummary.Callbacks$MyCallbacks: java.lang.String transform(java.lang.String)>",
				SourceSinkType.Field, -1, new String[] { FIELD_CALLBACK }, ""));
		
		assertEquals(4, flow.getFlowCount());
	}

	@Override
	protected SummaryGenerator getSummary() {
		SummaryGenerator sg = new SummaryGenerator();
		List<String> sub = new LinkedList<String>();
		sub.add("java.util.ArrayList");
		sg.setSubstitutedWith(sub);
		sg.setAnalyseMethodsTogether(false);
		sg.setAccessPathLength(5);
		sg.setIgnoreFlowsInSystemPackages(false);
		sg.setConfig(new IInfoflowConfig() {
			
			@Override
			public void setSootOptions(Options options) {
				Options.v().set_exclude(Collections.singletonList("soot.jimple.infoflow.test.methodSummary.GapClass"));
			}
			
		});
		return sg;
	}
	
}
