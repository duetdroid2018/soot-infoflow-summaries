package soot.jimple.infoflow.test.methodSummary.junit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.stream.XMLStreamException;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import soot.jimple.infoflow.Infoflow;
import soot.jimple.infoflow.InfoflowResults;
import soot.jimple.infoflow.config.ConfigForTest;
import soot.jimple.infoflow.entryPointCreators.DefaultEntryPointCreator;
import soot.jimple.infoflow.methodSummary.cmdSummary;
import soot.jimple.infoflow.methodSummary.taintWrappers.SummaryTaintWrapper;
import soot.jimple.infoflow.methodSummary.taintWrappers.TaintWrapperFactory;
import soot.jimple.infoflow.methodSummary.util.ClassFileInformation;
import soot.jimple.infoflow.test.methodSummary.ApiClass;

public class SummaryTaintWrapperTests {
	private static String appPath, libPath;

	private String[] source = new String[] {
			"<soot.jimple.infoflow.test.methodSummary.ApiClassClient: java.lang.Object source()>",
			"<soot.jimple.infoflow.test.methodSummary.ApiClassClient: int intSource()>",
			"<soot.jimple.infoflow.test.methodSummary.ApiClassClient: java.lang.String stringSource()>" };
	private String sink = "<soot.jimple.infoflow.test.methodSummary.ApiClassClient: void sink(java.lang.Object)>";
	private SummaryTaintWrapper summaryWrapper;

//	@BeforeClass
//	public static void init() throws FileNotFoundException, XMLStreamException {
//		String mSig = "";
//		for (Method m : ApiClass.class.getDeclaredMethods()) {
//			mSig = mSig + ClassFileInformation.getMethodSig(m) + ";";
//		}
//		mSig = mSig.substring(0, mSig.length() - 1).trim();
//		List<String> runArgs = new ArrayList<String>();
//		runArgs.add("-m " + mSig);
//		cmdSummary.main(runArgs.toArray(new String[runArgs.size()]));
//	}

	@Before
	public void resetSootAndStream() throws IOException {
		soot.G.reset();
		System.gc();

	}

	@Ignore("kill flow")
	@Test
	public void noFlow1() {
		testNoFlowForMethod("<soot.jimple.infoflow.test.methodSummary.ApiClassClient: void noFlow1()>");
	}

	@Ignore("kill flow")
	@Test
	public void noFlow2() {
		testNoFlowForMethod("<soot.jimple.infoflow.test.methodSummary.ApiClassClient: void noFlow2()>");
	}

	@Test
	public void flow1() {
		testFlowForMethod("<soot.jimple.infoflow.test.methodSummary.ApiClassClient: void flow1()>");
	}

	@Test
	public void flow2() {
		testFlowForMethod("<soot.jimple.infoflow.test.methodSummary.ApiClassClient: void flow2()>");
	}

	@Test
	public void paraReturnFlow() {
		testFlowForMethod("<soot.jimple.infoflow.test.methodSummary.ApiClassClient: void paraReturnFlow()>");
	}

	@Test
	public void paraFieldSwapFieldReturnFlow() {
		testFlowForMethod("<soot.jimple.infoflow.test.methodSummary.ApiClassClient: void paraFieldSwapFieldReturnFlow()>");
	}

	@Test
	public void paraFieldFieldReturnFlow() {
		testFlowForMethod("<soot.jimple.infoflow.test.methodSummary.ApiClassClient: void paraFieldFieldReturnFlow()>");
	}

	@Test
	public void paraReturnFlowInterface() {
		testFlowForMethod("<soot.jimple.infoflow.test.methodSummary.ApiClassClient: void paraReturnFlowOverInterface()>");
	}

	@Test
	public void paraFieldSwapFieldReturnFlowInterface() {
		testFlowForMethod("<soot.jimple.infoflow.test.methodSummary.ApiClassClient: void paraFieldSwapFieldReturnFlowOverInterface()>");
	}

	@Test
	public void paraFieldFieldReturnFlowInterface() {
		testFlowForMethod("<soot.jimple.infoflow.test.methodSummary.ApiClassClient: void paraFieldFieldReturnFlowOverInterface()>");
	}

	@Test
	public void paraToParaFlow() {
		testFlowForMethod("<soot.jimple.infoflow.test.methodSummary.ApiClassClient: void paraToParaFlow()>");
	}

	@Test
	public void fieldToParaFlow() {
		testFlowForMethod("<soot.jimple.infoflow.test.methodSummary.ApiClassClient: void fieldToParaFlow()>");
	}
	@Test
	public void apl3NoFlow() {
		testFlowForMethod("<soot.jimple.infoflow.test.methodSummary.ApiClassClient: void apl3NoFlow()>");
	}
	@Test
	public void apl3Flow() {
		testFlowForMethod("<soot.jimple.infoflow.test.methodSummary.ApiClassClient: void apl3Flow()>");
	}

	private void testFlowForMethod(String m) {
		Infoflow iFlow = null;
		try {
			iFlow = initInfoflow();
			Infoflow.setAccessPathLength(3);
			iFlow.computeInfoflow(appPath, libPath, new DefaultEntryPointCreator(java.util.Collections.singletonList(m)),
					Arrays.asList(source),
					java.util.Collections.singletonList(sink));
		} catch (Exception e) {
			fail("failed to calc path for test" + e.toString());
		}
		checkInfoflow(iFlow, 1);
	}

	private void testNoFlowForMethod(String m) {
		Infoflow iFlow = null;

		try {
			iFlow = initInfoflow();
			iFlow.computeInfoflow(appPath, libPath, new DefaultEntryPointCreator(java.util.Collections.singletonList(m)),
					 Arrays.asList(source),
					java.util.Collections.singletonList(sink));
		} catch (Exception e) {
			fail("failed to calc path for test" + e.toString());
		}
		checkNoInfoflow(iFlow);
	}

	private void checkNoInfoflow(Infoflow infoflow) {
		assertTrue(!infoflow.isResultAvailable() || infoflow.getResults().size() == 0);

	}

	private void checkInfoflow(Infoflow infoflow, int resultCount) {
		if (infoflow.isResultAvailable()) {
			InfoflowResults map = infoflow.getResults();

			assertTrue(map.containsSinkMethod(sink));
			assertTrue(map.isPathBetweenMethods(sink, source[0]) || map.isPathBetweenMethods(sink, source[1])
					|| map.isPathBetweenMethods(sink, source[2]));
			assertEquals(resultCount, map.size());
		} else {
			fail("result is not available");
		}

	}

	protected Infoflow initInfoflow() throws FileNotFoundException, XMLStreamException {
		Infoflow result = new Infoflow();
		ConfigForTest testConfig = new ConfigForTest();
		result.setSootConfig(testConfig);

		summaryWrapper = (SummaryTaintWrapper) TaintWrapperFactory.createTaintWrapper
				("./TestSummaries/soot.jimple.infoflow.test.methodSummary.ApiClass.xml");
		result.setTaintWrapper(summaryWrapper);
		return result;
	}

	@BeforeClass
	public static void setUp() throws IOException {
		final String sep = System.getProperty("path.separator");
		File f = new File(".");
		File testSrc1 = new File(f, "bin");
		File testSrc2 = new File(f, "build" + File.separator + "classes");

		if (!(testSrc1.exists() || testSrc2.exists())) {
			fail("Test aborted - none of the test sources are available");
		}

		libPath = System.getProperty("java.home") + File.separator + "lib" + File.separator + "rt.jar";
		appPath = testSrc1.getCanonicalPath() + sep + testSrc2.getCanonicalPath();
	}

}
