package soot.jimple.infoflow.test.methodSummary.junit;

import static org.junit.Assert.assertTrue;


import java.util.Map;
import java.util.Set;

import org.junit.Test;

import soot.jimple.infoflow.methodSummary.Summary;
import soot.jimple.infoflow.methodSummary.data.AbstractMethodFlow;

public class FieldToReturnTest extends TestHelper{
	protected static Map<String, Set<AbstractMethodFlow>> flows;
	static boolean executeSummary = true;
	static final String className = "soot.jimple.infoflow.test.methodSummary.FieldToReturn"; 


	@Test(timeout = 100000)
	public void fieldToReturn1() {
		Summary s = new Summary();
		String mSig = "<soot.jimple.infoflow.test.methodSummary.FieldToReturn: int fieldToReturn()>";
		Set<AbstractMethodFlow> flow = s.createMethodSummary(mSig).get(mSig);
		assertTrue(containsFieldToReturn(flow,"<soot.jimple.infoflow.test.methodSummary.FieldToReturn: int intField>",NO_ACCESS_PATH,NO_ACCESS_PATH));
		assertTrue(flow.size() == 1);
	}

	@Test(timeout = 100000)
	public void fieldToReturn2() {
		Summary s = new Summary();
		String mSig = "<soot.jimple.infoflow.test.methodSummary.FieldToReturn: java.lang.Object fieldToReturn2()>";
		Set<AbstractMethodFlow> flow = s.createMethodSummary(mSig).get(mSig);
		assertTrue(containsFieldToReturn(flow,"<soot.jimple.infoflow.test.methodSummary.FieldToReturn: java.lang.Object obField>",NO_ACCESS_PATH,NO_ACCESS_PATH));
		assertTrue(flow.size() == 1);
	}

	@Test(timeout = 100000)
	public void fieldToReturn3() {
		Summary s = new Summary();
		String mSig = "<soot.jimple.infoflow.test.methodSummary.FieldToReturn: java.util.List fieldToReturn3()>";
		Set<AbstractMethodFlow> flow = s.createMethodSummary(mSig).get(mSig);
		assertTrue(containsFieldToReturn(flow,"<soot.jimple.infoflow.test.methodSummary.FieldToReturn: java.util.LinkedList listField>",NO_ACCESS_PATH,NO_ACCESS_PATH));
		assertTrue(flow.size() == 1);
	}

	@Test(timeout = 100000)
	public void NotWfieldToReturn4() {
		Summary s = new Summary();
		String mSig = "<soot.jimple.infoflow.test.methodSummary.FieldToReturn: java.lang.Object fieldToReturn4()>";
		Set<AbstractMethodFlow> flow = s.createMethodSummary(mSig).get(mSig);
		assertTrue(containsFieldToReturn(flow,"<soot.jimple.infoflow.test.methodSummary.FieldToReturn: java.util.LinkedList listField>",NO_ACCESS_PATH,NO_ACCESS_PATH));
		assertTrue(containsFieldToReturn(flow,"<soot.jimple.infoflow.test.methodSummary.FieldToReturn: java.util.LinkedList listField>",NO_ACCESS_PATH,NO_ACCESS_PATH));
		assertTrue(flow.size() == 2);
	}

	@Test(timeout = 100000)
	public void fieldToReturn5() {
		Summary s = new Summary();
		String mSig = "<soot.jimple.infoflow.test.methodSummary.FieldToReturn: java.lang.Object fieldToReturn5()>";
		Set<AbstractMethodFlow> flow = s.createMethodSummary(mSig).get(mSig);
		assertTrue(containsFieldToReturn(flow,"<soot.jimple.infoflow.test.methodSummary.FieldToReturn: java.lang.Object[] arrayField>",NO_ACCESS_PATH,NO_ACCESS_PATH));
		assertTrue(flow.size() == 1);
	}

	@Test(timeout = 100000)
	public void fieldToReturn5Rec() {
		Summary s = new Summary();
		String mSig = "<soot.jimple.infoflow.test.methodSummary.FieldToReturn: java.lang.Object fieldToReturn5Rec(int)>";
		Set<AbstractMethodFlow> flow = s.createMethodSummary(mSig).get(mSig);
		assertTrue(containsFieldToReturn(flow,"<soot.jimple.infoflow.test.methodSummary.FieldToReturn: java.lang.Object[] arrayField>",NO_ACCESS_PATH,NO_ACCESS_PATH));
		//assertTrue(flow.size() == 2); //TODO readd
	}

	@Test(timeout = 100000)
	public void fieldToReturn6() {
		Summary s = new Summary();
		String mSig = "<soot.jimple.infoflow.test.methodSummary.FieldToReturn: java.lang.Object[] fieldToReturn6()>";
		Set<AbstractMethodFlow> flow = s.createMethodSummary(mSig).get(mSig);
		assertTrue(containsFieldToReturn(flow,"<soot.jimple.infoflow.test.methodSummary.FieldToReturn: java.lang.Object[] arrayField>",NO_ACCESS_PATH,NO_ACCESS_PATH));
		assertTrue(flow.size() == 1);
	}

	@Test(timeout = 100000)
	public void fieldToReturn7() {
		Summary s = new Summary();
		String mSig = "<soot.jimple.infoflow.test.methodSummary.FieldToReturn: int fieldToReturn7()>";
		Set<AbstractMethodFlow> flow = s.createMethodSummary(mSig).get(mSig);
		assertTrue(containsFieldToReturn(flow,"<soot.jimple.infoflow.test.methodSummary.FieldToReturn: int[] intArray>",NO_ACCESS_PATH,NO_ACCESS_PATH));
		assertTrue(flow.size() == 1);
	}

	@Test(timeout = 100000)
	public void fieldToReturn8() {
		Summary s = new Summary();
		String mSig = "<soot.jimple.infoflow.test.methodSummary.FieldToReturn: int[] fieldToReturn8()>";
		Set<AbstractMethodFlow> flow = s.createMethodSummary(mSig).get(mSig);
		assertTrue(containsFieldToReturn(flow,"<soot.jimple.infoflow.test.methodSummary.FieldToReturn: int[] intArray>",NO_ACCESS_PATH,NO_ACCESS_PATH));
		assertTrue(flow.size() == 1);
	}

	@Test(timeout = 100000)
	public void fieldToReturn9() {
		Summary s = new Summary();
		String mSig = "<soot.jimple.infoflow.test.methodSummary.FieldToReturn: int fieldToReturn9()>";
		Set<AbstractMethodFlow> flow = s.createMethodSummary(mSig).get(mSig);
		assertTrue(containsFieldToReturn(flow,"<soot.jimple.infoflow.test.methodSummary.FieldToReturn: soot.jimple.infoflow.test.methodSummary.Data dataField>",DATACLASS_INT_FIELD,NO_ACCESS_PATH));
		assertTrue(flow.size() == 1);
	}

	@Test(timeout = 100000)
	public void fieldToReturn10() {
		Summary s = new Summary();
		String mSig = "<soot.jimple.infoflow.test.methodSummary.FieldToReturn: java.lang.Object fieldToReturn10()>";
		Set<AbstractMethodFlow> flow = s.createMethodSummary(mSig).get(mSig);
		assertTrue(containsFieldToReturn(flow,"<soot.jimple.infoflow.test.methodSummary.FieldToReturn: soot.jimple.infoflow.test.methodSummary.Data dataField>",DATACLASS_OBJECT_FIELD,NO_ACCESS_PATH));
		assertTrue(flow.size() == 1);
	}

	@Test(timeout = 100000)
	public void fieldToReturn11() {
		Summary s = new Summary();
		String mSig = "<soot.jimple.infoflow.test.methodSummary.FieldToReturn: java.lang.String fieldToReturn11()>";
		Set<AbstractMethodFlow> flow = s.createMethodSummary(mSig).get(mSig);
		assertTrue(containsFieldToReturn(flow,"<soot.jimple.infoflow.test.methodSummary.FieldToReturn: soot.jimple.infoflow.test.methodSummary.Data dataField>",DATACLASS_STRING_FIELD,NO_ACCESS_PATH));
	//	assertTrue(flow.size() == 3);
	}
	
	@Test(timeout = 100000)
	public void fieldToReturn12() {
		Summary s = new Summary();
		String mSig = "<soot.jimple.infoflow.test.methodSummary.FieldToReturn: "+DATA+" fieldToReturn12()>";
		Set<AbstractMethodFlow> flow = s.createMethodSummary(mSig).get(mSig);
		assertTrue(containsFieldToReturn(flow,"<soot.jimple.infoflow.test.methodSummary.FieldToReturn: soot.jimple.infoflow.test.methodSummary.Data dataField>",DATACLASS_INT_FIELD,DATACLASS_INT_FIELD));
		assertTrue(flow.size() == 1);
	}
	
	@Test(timeout = 100000)
	public void fieldToReturn13() {
		Summary s = new Summary();
		String mSig = "<soot.jimple.infoflow.test.methodSummary.FieldToReturn: "+DATA+" fieldToReturn13()>";
		Set<AbstractMethodFlow> flow = s.createMethodSummary(mSig).get(mSig);
		assertTrue(containsFieldToReturn(flow,"<soot.jimple.infoflow.test.methodSummary.FieldToReturn: soot.jimple.infoflow.test.methodSummary.Data dataField>",DATACLASS_OBJECT_FIELD,DATACLASS_OBJECT_FIELD));
		assertTrue(flow.size() == 1);
	}
	
	@Test(timeout = 100000)
	public void fieldToReturn14() {
		Summary s = new Summary();
		String mSig = "<soot.jimple.infoflow.test.methodSummary.FieldToReturn: "+DATA+" fieldToReturn14()>";
		Set<AbstractMethodFlow> flow = s.createMethodSummary(mSig).get(mSig);
		assertTrue(containsFieldToReturn(flow,"<soot.jimple.infoflow.test.methodSummary.FieldToReturn: int intField>",NO_ACCESS_PATH,DATACLASS_INT_FIELD));
		assertTrue(flow.size() == 1);
	}
}
