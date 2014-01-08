package soot.jimple.infoflow.test.methodSummary.junit;

import static org.junit.Assert.assertTrue;



import java.util.Set;

import org.junit.Test;

import soot.jimple.infoflow.methodSummary.Summary;
import soot.jimple.infoflow.methodSummary.data.AbstractMethodFlow;

public class FieldToParaTests extends TestHelper {

	static final String className = "soot.jimple.infoflow.test.methodSummary.FieldToPara";

	@Test(timeout = 100000)
	public void fieldToPara1() {
		Summary s = new Summary();
		String mSig = "<soot.jimple.infoflow.test.methodSummary.FieldToPara: void dataParameter(soot.jimple.infoflow.test.methodSummary.Data)>";
		Set<AbstractMethodFlow> flow = s.createMethodSummary(mSig).get(mSig);
		assertTrue(containsFieldToParaFlow(flow,"<soot.jimple.infoflow.test.methodSummary.FieldToPara: java.lang.Object obField>",NO_ACCESS_PATH, 0, DATA,DATACLASS_OBJECT_FIELD));
		assertTrue(flow.size() == 1);
	}

	@Test(timeout = 100000)
	public void fieldToPara2() {
		Summary s = new Summary();
		String mSig = "<soot.jimple.infoflow.test.methodSummary.FieldToPara: void dataParameter2(soot.jimple.infoflow.test.methodSummary.Data)>";
		Set<AbstractMethodFlow> flow = s.createMethodSummary(mSig).get(mSig);
		assertTrue(containsFieldToParaFlow(flow,"<soot.jimple.infoflow.test.methodSummary.FieldToPara: java.lang.Object[] arrayField>",NO_ACCESS_PATH,0,DATA,DATACLASS_OBJECT_FIELD));
		assertTrue(flow.size() == 1);
	}

	@Test(timeout = 100000)
	public void fieldToPara3() {
		Summary s = new Summary();
		String mSig = "<soot.jimple.infoflow.test.methodSummary.FieldToPara: void dataParameter3(soot.jimple.infoflow.test.methodSummary.Data)>";
		Set<AbstractMethodFlow> flow = s.createMethodSummary(mSig).get(mSig);
		assertTrue(containsFieldToParaFlow(flow,"<soot.jimple.infoflow.test.methodSummary.FieldToPara: java.util.LinkedList listField>",LINKEDLIST_FIRST,0,DATA,DATACLASS_OBJECT_FIELD));
		assertTrue(containsFieldToParaFlow(flow,"<soot.jimple.infoflow.test.methodSummary.FieldToPara: java.util.LinkedList listField>",LINKEDLIST_LAST,0,DATA,DATACLASS_OBJECT_FIELD));
		assertTrue(flow.size() == 2);
	}

	@Test(timeout = 100000)
	public void fieldToPara4() {
		Summary s = new Summary();
		String mSig = "<soot.jimple.infoflow.test.methodSummary.FieldToPara: void dataParameter4(soot.jimple.infoflow.test.methodSummary.Data)>";
		Set<AbstractMethodFlow> flow = s.createMethodSummary(mSig).get(mSig);

		assertTrue(containsFieldToParaFlow(flow,"<soot.jimple.infoflow.test.methodSummary.FieldToPara: soot.jimple.infoflow.test.methodSummary.Data dataField>",DATACLASS_OBJECT_FIELD,0,DATA,DATACLASS_OBJECT_FIELD));
		assertTrue(flow.size() == 1);
	}

	@Test(timeout = 100000)
	public void fieldToPara5() {
		Summary s = new Summary();
		String mSig = "<soot.jimple.infoflow.test.methodSummary.FieldToPara: void dataParameter5(soot.jimple.infoflow.test.methodSummary.Data)>";
		Set<AbstractMethodFlow> flow = s.createMethodSummary(mSig).get(mSig);
		assertTrue(containsFieldToParaFlow(flow,"<soot.jimple.infoflow.test.methodSummary.FieldToPara: soot.jimple.infoflow.test.methodSummary.Data dataField>",DATACLASS_OBJECT_FIELD,0,DATA,DATACLASS_OBJECT_FIELD));
		assertTrue(flow.size() == 1);
	}

	@Test(timeout = 100000)
	public void fieldToParaRec1() {
		Summary s = new Summary();
		String mSig = "<soot.jimple.infoflow.test.methodSummary.FieldToPara: void dataParameterRec(soot.jimple.infoflow.test.methodSummary.Data,int)>";
		Set<AbstractMethodFlow> flow = s.createMethodSummary(mSig).get(mSig);
		assertTrue(containsFieldToParaFlow(flow,"<soot.jimple.infoflow.test.methodSummary.FieldToPara: java.lang.Object obField>",NO_ACCESS_PATH,0,DATA,DATACLASS_OBJECT_FIELD));
		assertTrue(flow.size() == 1);
	}

	@Test//(timeout = 100000)
	public void fieldToArrayParameter() {
		Summary s = new Summary();
		String mSig = "<soot.jimple.infoflow.test.methodSummary.FieldToPara: void objArrayParameter(java.lang.Object[])>";
		Set<AbstractMethodFlow> flow = s.createMethodSummary(mSig).get(mSig);
		assertTrue(containsFieldToParaFlow(flow,"<soot.jimple.infoflow.test.methodSummary.FieldToPara: java.lang.Object obField>",NO_ACCESS_PATH,0,OBJECT_ARRAY,NO_ACCESS_PATH));
		assertTrue(flow.size() == 1);
	}

	@Test(timeout = 100000)
	public void fieldToArrayParameter2() {
		Summary s = new Summary();
		String mSig = "<soot.jimple.infoflow.test.methodSummary.FieldToPara: void objArrayParameter2(java.lang.Object[])>";
		Set<AbstractMethodFlow> flow = s.createMethodSummary(mSig).get(mSig);
		assertTrue(containsFieldToParaFlow(flow,"<soot.jimple.infoflow.test.methodSummary.FieldToPara: java.lang.Object[] arrayField>",NO_ACCESS_PATH,0,OBJECT_ARRAY,NO_ACCESS_PATH));
		assertTrue(flow.size() == 1);
	}

	@Test(timeout = 250000)
	public void fieldToArrayParameter3() {
		Summary s = new Summary();
		String mSig = "<soot.jimple.infoflow.test.methodSummary.FieldToPara: void objArrayParameter3(java.lang.Object[])>";
		Set<AbstractMethodFlow> flow = s.createMethodSummary(mSig).get(mSig);
		assertTrue(containsFieldToParaFlow(flow,"<soot.jimple.infoflow.test.methodSummary.FieldToPara: java.util.LinkedList listField>",LINKEDLIST_FIRST,0,OBJECT_ARRAY,NO_ACCESS_PATH));
		assertTrue(flow.size() == 2);
	}

	@Test(timeout = 100000)
	public void fieldToArrayParameter4() {
			Summary s = new Summary();
		String mSig = "<soot.jimple.infoflow.test.methodSummary.FieldToPara: void objArrayParameter4(java.lang.Object[])>";
		Set<AbstractMethodFlow> flow = s.createMethodSummary(mSig).get(mSig);
		assertTrue(containsFieldToParaFlow(flow,"<soot.jimple.infoflow.test.methodSummary.FieldToPara: soot.jimple.infoflow.test.methodSummary.Data dataField>",DATACLASS_OBJECT_FIELD,0,OBJECT_ARRAY,NO_ACCESS_PATH));
		assertTrue(flow.size() == 1);
	}

	@Test(timeout = 100000)
	public void fieldToArrayParameter5() {
		Summary s = new Summary();
		String mSig = "<soot.jimple.infoflow.test.methodSummary.FieldToPara: void objArrayParameter5(java.lang.Object[])>";
		Set<AbstractMethodFlow> flow = s.createMethodSummary(mSig).get(mSig);
		assertTrue(containsFieldToParaFlow(flow,"<soot.jimple.infoflow.test.methodSummary.FieldToPara: soot.jimple.infoflow.test.methodSummary.Data dataField>",DATACLASS_OBJECT_FIELD,0,OBJECT_ARRAY,NO_ACCESS_PATH));
		assertTrue(flow.size() == 1);
	}

	@Test(timeout = 100000)
	public void listFieldToParameter1() {
		Summary s = new Summary();
		String mSig = "<soot.jimple.infoflow.test.methodSummary.FieldToPara: void listParameter(java.util.List)>";
		Set<AbstractMethodFlow> flow = s.createMethodSummary(mSig).get(mSig);
		assertTrue(containsFieldToParaFlow(flow,"<soot.jimple.infoflow.test.methodSummary.FieldToPara: int intField>",NO_ACCESS_PATH,0,LIST,NO_ACCESS_PATH));
	}

	@Test(timeout = 100000)
	public void listFieldToParameter2() {
		Summary s = new Summary();
		String mSig = "<soot.jimple.infoflow.test.methodSummary.FieldToPara: void listParameter2(java.util.List)>";
		Set<AbstractMethodFlow> flow = s.createMethodSummary(mSig).get(mSig);
		assertTrue(containsFieldToParaFlow(flow,"<soot.jimple.infoflow.test.methodSummary.FieldToPara: java.lang.Object obField>",NO_ACCESS_PATH,0,LIST,NO_ACCESS_PATH));
	}

	@Test(timeout = 100000)
	public void listFieldToParameter3() {
		Summary s = new Summary();
		String mSig = "<soot.jimple.infoflow.test.methodSummary.FieldToPara: void listParameter3(java.util.List)>";
		Set<AbstractMethodFlow> flow = s.createMethodSummary(mSig).get(mSig);
		assertTrue(containsFieldToParaFlow(flow,"<soot.jimple.infoflow.test.methodSummary.FieldToPara: java.util.LinkedList listField>",NO_ACCESS_PATH,0,LIST,NO_ACCESS_PATH));
	}

	@Test(timeout = 100000)
	public void listFieldToParameter4() {
		Summary s = new Summary();
		String mSig = "<soot.jimple.infoflow.test.methodSummary.FieldToPara: void listParameter4(java.util.List)>";
		Set<AbstractMethodFlow> flow = s.createMethodSummary(mSig).get(mSig);
		assertTrue(containsFieldToParaFlow(flow,"<soot.jimple.infoflow.test.methodSummary.FieldToPara: java.lang.Object[] arrayField>",NO_ACCESS_PATH,0,LIST,NO_ACCESS_PATH));
	}

	@Test(timeout = 100000)
	public void listFieldToParameter5() {
		Summary s = new Summary();
		String mSig = "<soot.jimple.infoflow.test.methodSummary.FieldToPara: void listParameter5(java.util.List)>";
		Set<AbstractMethodFlow> flow = s.createMethodSummary(mSig).get(mSig);
		assertTrue(containsFieldToParaFlow(flow,"<soot.jimple.infoflow.test.methodSummary.FieldToPara: int[] intArray>",NO_ACCESS_PATH,0,LIST,NO_ACCESS_PATH));
	}

	@Test(timeout = 100000)
	public void listFieldToParameter6() {
		Summary s = new Summary();
		String mSig = "<soot.jimple.infoflow.test.methodSummary.FieldToPara: void listParameter6(java.util.List)>";
		Set<AbstractMethodFlow> flow = s.createMethodSummary(mSig).get(mSig);
		assertTrue(containsFieldToParaFlow(flow,"<soot.jimple.infoflow.test.methodSummary.FieldToPara: soot.jimple.infoflow.test.methodSummary.Data dataField>",DATACLASS_OBJECT_FIELD,0,LIST,NO_ACCESS_PATH));
	}
}
