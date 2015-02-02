package soot.jimple.infoflow.test.methodSummary.junit;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;


@RunWith(Suite.class)
@Suite.SuiteClasses({ ApiTests.class,ArbitraryAccessPathTests.class,FieldToParaTests.class, FieldToReturnTests.class, ParaToFieldTests.class,
		ParaToReturnTests.class, ParaToParaTests.class ,SummaryTaintWrapperTests.class, WrapperListTests.class,ApiTestNotWorking.class,
		})
public class AllTests {
}
