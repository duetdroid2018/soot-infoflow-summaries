package soot.jimple.infoflow.test.methodSummary.junit;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;


@RunWith(Suite.class)
@Suite.SuiteClasses({ /*GetPathsTest.class,*/ApiTests.class,FieldToParaTests.class, FieldToReturnTest.class, ParaToFieldTests.class,
		ParaToReturnTests.class, ParaToParaTest.class ,SummaryTaintWrapperTests.class, WrapperListTests.class,ApiTestNotWorking.class,
		SimpleListTest.class})
public class AllTests {
}
