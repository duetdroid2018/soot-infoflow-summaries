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
		// Gap base object back to parameter 1
		assertTrue(containsFlow(flow.getAllFlows(), SourceSinkType.Field, -1, null,
				"<soot.jimple.infoflow.test.methodSummary.Callbacks$MyCallbacks: java.lang.String transform(java.lang.String)>",
				SourceSinkType.Parameter, 1, null, ""));
		
		assertEquals(4, flow.getFlowCount());
	}
	
	@Test(timeout = 100000)
	public void fieldCallbackToReturn() {
		String mSig = "<soot.jimple.infoflow.test.methodSummary.Callbacks: java.lang.String fieldCallbackToReturn(java.lang.String)>";
		MethodSummaries flow = createSummaries(mSig);
		
		// Field to gap base object
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
	
	@Test(timeout = 100000)
	public void fieldCallbackToField() {
		String mSig = "<soot.jimple.infoflow.test.methodSummary.Callbacks: soot.jimple.infoflow.test.methodSummary.Data fieldCallbackToField(soot.jimple.infoflow.test.methodSummary.Data)>";
		MethodSummaries flow = createSummaries(mSig);
		
		// Field to gap base object
		assertTrue(containsFlow(flow.getAllFlows(), SourceSinkType.Field, -1, new String[] { FIELD_CALLBACK }, "",
				SourceSinkType.GapBaseObject, 0, null,
				"<soot.jimple.infoflow.test.methodSummary.Callbacks$MyCallbacks: void transformObject(soot.jimple.infoflow.test.methodSummary.Data)>"));
		// Parameter 0 to gap argument 0
		assertTrue(containsFlow(flow.getAllFlows(), SourceSinkType.Parameter, 0, null, "", SourceSinkType.Parameter, 0, null,
				"<soot.jimple.infoflow.test.methodSummary.Callbacks$MyCallbacks: void transformObject(soot.jimple.infoflow.test.methodSummary.Data)>"));
		// Gap parameter 0 to method return value
		assertTrue(containsFlow(flow.getAllFlows(), SourceSinkType.Parameter, 0, null,
				"<soot.jimple.infoflow.test.methodSummary.Callbacks$MyCallbacks: void transformObject(soot.jimple.infoflow.test.methodSummary.Data)>",
				SourceSinkType.Return, -1, null, ""));
		// Gap parameter 0 to method parameter 0 due to aliasing
		assertTrue(containsFlow(flow.getAllFlows(), SourceSinkType.Parameter, 0, null,
				"<soot.jimple.infoflow.test.methodSummary.Callbacks$MyCallbacks: void transformObject(soot.jimple.infoflow.test.methodSummary.Data)>",
				SourceSinkType.Parameter, 0, null, ""));
		// Gap base to "this" field
		assertTrue(containsFlow(flow.getAllFlows(), SourceSinkType.Field, -1, null,
				"<soot.jimple.infoflow.test.methodSummary.Callbacks$MyCallbacks: void transformObject(soot.jimple.infoflow.test.methodSummary.Data)>",
				SourceSinkType.Field, -1, new String[] { FIELD_CALLBACK }, ""));
		
		assertEquals(5, flow.getFlowCount());
	}
	
	@Test(timeout = 100000)
	public void apiClassMakeString() {
		String mSig = "<soot.jimple.infoflow.test.methodSummary.ApiClass: java.lang.String makeString(soot.jimple.infoflow.test.methodSummary.IGapClass,java.lang.String)>";
		MethodSummaries flow = createSummaries(mSig);
		
		// Parameter 0 to gap base object
		assertTrue(containsFlow(flow.getAllFlows(), SourceSinkType.Parameter, 0, null, "",
				SourceSinkType.GapBaseObject, 0, null,
				"<soot.jimple.infoflow.test.methodSummary.IGapClass: java.lang.String callTheGap(java.lang.String)>"));
		// Parameter 1 to gap argument 0
		assertTrue(containsFlow(flow.getAllFlows(), SourceSinkType.Parameter, 1, null, "", SourceSinkType.Parameter, 0, null,
				"<soot.jimple.infoflow.test.methodSummary.IGapClass: java.lang.String callTheGap(java.lang.String)>"));
		// Gap return value to method return value
		assertTrue(containsFlow(flow.getAllFlows(), SourceSinkType.Return, -1, null,
				"<soot.jimple.infoflow.test.methodSummary.IGapClass: java.lang.String callTheGap(java.lang.String)>",
				SourceSinkType.Return, -1, null, ""));
		// Gap base object back to parameter 0
		assertTrue(containsFlow(flow.getAllFlows(), SourceSinkType.Field, -1, null,
				"<soot.jimple.infoflow.test.methodSummary.IGapClass: java.lang.String callTheGap(java.lang.String)>",
				SourceSinkType.Parameter, 0, null,
				""));
		
		assertEquals(4, flow.getFlowCount());
	}
	
	@Test(timeout = 100000)
	public void apifillDataObject() {
		String mSig = "<soot.jimple.infoflow.test.methodSummary.ApiClass: void fillDataObject(soot.jimple.infoflow.test.methodSummary.IGapClass,java.lang.String,soot.jimple.infoflow.test.methodSummary.Data)>";
		MethodSummaries flow = createSummaries(mSig);
		
		// Parameter 0 to gap base object
		assertTrue(containsFlow(flow.getAllFlows(), SourceSinkType.Parameter, 0, null, "",
				SourceSinkType.GapBaseObject, 0, null,
				"<soot.jimple.infoflow.test.methodSummary.IGapClass: void fillDataString(java.lang.String,soot.jimple.infoflow.test.methodSummary.Data)>"));
		// Parameter 1 to gap argument 0
		assertTrue(containsFlow(flow.getAllFlows(), SourceSinkType.Parameter, 1, null, "", SourceSinkType.Parameter, 0, null,
				"<soot.jimple.infoflow.test.methodSummary.IGapClass: void fillDataString(java.lang.String,soot.jimple.infoflow.test.methodSummary.Data)>"));
		// Parameter 2 to gap argument 1
		assertTrue(containsFlow(flow.getAllFlows(), SourceSinkType.Parameter, 2, null, "", SourceSinkType.Parameter, 1, null,
				"<soot.jimple.infoflow.test.methodSummary.IGapClass: void fillDataString(java.lang.String,soot.jimple.infoflow.test.methodSummary.Data)>"));
		// Gap base back to parameter 0
		assertTrue(containsFlow(flow.getAllFlows(), SourceSinkType.Field, -1, null,
				"<soot.jimple.infoflow.test.methodSummary.IGapClass: void fillDataString(java.lang.String,soot.jimple.infoflow.test.methodSummary.Data)>",
				SourceSinkType.Parameter, 0, null, ""));
		// Gap argument 1 back to parameter 2
		assertTrue(containsFlow(flow.getAllFlows(), SourceSinkType.Parameter, 1, null,
				"<soot.jimple.infoflow.test.methodSummary.IGapClass: void fillDataString(java.lang.String,soot.jimple.infoflow.test.methodSummary.Data)>",
				SourceSinkType.Parameter, 2, null, ""));
		
		assertEquals(5, flow.getFlowCount());
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
