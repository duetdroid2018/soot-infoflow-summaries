package soot.jimple.infoflow.test.methodSummary.junit;

import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.junit.Test;

import soot.jimple.infoflow.methodSummary.SummaryGenerator;
import soot.jimple.infoflow.methodSummary.data.AbstractMethodFlow;

public class TmpTestCases extends TestHelper {
	@Test(timeout = 100000)
	public void intParameterRec() {
		SummaryGenerator s = new SummaryGenerator();
		String mSig = "<soot.jimple.infoflow.test.methodSummary.ParaToField: void intParaToData(int)>";
		Set<AbstractMethodFlow> flow = s.createMethodSummary(mSig).getFlowsForMethod(mSig);
		assertTrue(containsParaToFieldFlow(flow, 0, INT_TYPE, NO_ACCESS_PATH,
				"<soot.jimple.infoflow.test.methodSummary.ParaToField: soot.jimple.infoflow.test.methodSummary.Data dataField>", DATACLASS_INT_FIELD));
		assertTrue(flow.size() == 1);
	}
}
