package soot.jimple.infoflow.test.methodSummary.junit;

import static org.junit.Assert.assertTrue;

import java.util.Set;

import junit.framework.Assert;

import org.junit.Test;

import soot.jimple.infoflow.methodSummary.SummaryGenerator;
import soot.jimple.infoflow.methodSummary.data.AbstractMethodFlow;

public class ApiTests extends ApiTestHelper {
	static final String className = "soot.jimple.infoflow.test.methodSummary.ApiClass";
	

	@Test //(timeout = 100000)
	public void standardFlow1() {
		SummaryGenerator s = getSummary();
		String mSig = "<" + className + ": int standardFlow(int)>";
		Set<AbstractMethodFlow> res = s.createMethodSummary(mSig).getFlowsForMethod(mSig);
		assertTrue(res.size() == 1);
		assertTrue(containsParaToReturn(res, 0, INT_TYPE, NO_ACCESS_PATH, NO_ACCESS_PATH));
	}

	@Test(timeout = 100000)
	public void standardFlow11() {
		SummaryGenerator s = getSummary();
		String mSig = "<" + className + ": int standardFlow(int)>";
		Set<AbstractMethodFlow> res = s.createMethodSummary(mSig).getFlowsForMethod(mSig);
		assertTrue(res.size() == 1);
	}

	@Test//(timeout = 100000)
	public void standardFlow2() {
		SummaryGenerator s = getSummary();
		String mSig = "<" + className + ": int standardFlow2(int,int)>";
		Set<AbstractMethodFlow> res = s.createMethodSummary(mSig).getFlowsForMethod(mSig);
		assertTrue(containsParaToReturn(res, 0, INT_TYPE, NO_ACCESS_PATH, NO_ACCESS_PATH));
		assertTrue(containsParaToReturn(res, 1, INT_TYPE, NO_ACCESS_PATH, NO_ACCESS_PATH));
	}

	@Test(timeout = 100000)
	public void standardFlow2Com() {
		SummaryGenerator s = getSummary();
		String mSig = "<" + className + ": int standardFlow2Com(int,int)>";
		Set<AbstractMethodFlow> res = s.createMethodSummary(mSig).getFlowsForMethod(mSig);
		assertTrue(containsParaToReturn(res, 0, INT_TYPE, NO_ACCESS_PATH, NO_ACCESS_PATH));
		assertTrue(containsParaToReturn(res, 1, INT_TYPE, NO_ACCESS_PATH, NO_ACCESS_PATH));
	}

	@Test(timeout = 100000)
	public void standardFlow22() {
		SummaryGenerator s = getSummary();
		String mSig = "<" + className + ": int standardFlow2(int,int)>";
		Set<AbstractMethodFlow> res = s.createMethodSummary(mSig).getFlowsForMethod(mSig);
		assertTrue(res.size() == 2);
		assertTrue(containsParaToReturn(res, 0, INT_TYPE, NO_ACCESS_PATH, NO_ACCESS_PATH));
		assertTrue(containsParaToReturn(res, 1, INT_TYPE, NO_ACCESS_PATH, NO_ACCESS_PATH));
	}

	@Test//(timeout = 100000)
	public void standardFlow3() {
		SummaryGenerator s = getSummary();
		String mSig = "<" + className + ": int standardFlow3(" + DATA_TYPE + ")>";
		Set<AbstractMethodFlow> res = s.createMethodSummary(mSig).getFlowsForMethod(mSig);
		assertTrue(containsParaToReturn(res, 0, DATA_TYPE, NO_ACCESS_PATH, NO_ACCESS_PATH));
		assertTrue(containsParaToReturn(res, 0, DATA_TYPE, DATACLASS_INT_FIELD, NO_ACCESS_PATH));
		// assertTrue(res.size() == 1);
	}

	@Test(timeout = 100000)
	public void standardFlow31() {
		SummaryGenerator s = getSummary();
		String mSig = "<" + className + ": int standardFlow3(" + DATA_TYPE + ")>";
		Set<AbstractMethodFlow> res = s.createMethodSummary(mSig).getFlowsForMethod(mSig);
		Assert.assertEquals(1, res.size());
	}

	@Test//(timeout = 100000)
	public void standardFlow4() {
		SummaryGenerator s = getSummary();
		String mSig = "<" + className + ": " + DATA_TYPE + " standardFlow4(int,java.lang.Object)>";
		Set<AbstractMethodFlow> res = s.createMethodSummary(mSig).getFlowsForMethod(mSig);
		assertTrue(containsParaToReturn(res, 0, INT_TYPE, NO_ACCESS_PATH, DATACLASS_INT_FIELD));
		assertTrue(containsParaToReturn(res, 1, OBJECT_TYPE, NO_ACCESS_PATH, DATACLASS_OBJECT_FIELD));
		 assertTrue(res.size() == 2);
	}

//	@Test(timeout = 100000)
//	public void standardFlow41() {
//		Summary s = getSummary();
//		String mSig = "<" + className + ": " + NON_PRIMITIVE_TYP + " standardFlow4(int,java.lang.Object)>";
//		Set<AbstractMethodFlow> res = s.createMethodSummary(mSig).get(mSig);
//		assertTrue(res.size() == 4);
//	}

	@Test(timeout = 100000)
	public void standardFlow6() {
		SummaryGenerator s = getSummary();
		String mSig = "<" + className + ": " + DATA_TYPE + " standardFlow6(java.lang.Object)>";
		Set<AbstractMethodFlow> res = s.createMethodSummary(mSig).getFlowsForMethod(mSig);
		assertTrue(containsParaToReturn(res, 0, OBJECT_TYPE, NO_ACCESS_PATH, DATACLASS_OBJECT_FIELD));
		// assertTrue(res.size() == 2);
	}

	@Test(timeout = 100000)
	public void standardFlow8() {
		SummaryGenerator s = getSummary();
		String mSig = "<soot.jimple.infoflow.test.methodSummary.ApiClass: soot.jimple.infoflow.test.methodSummary.Data standardFlow8(soot.jimple.infoflow.test.methodSummary.Data)>";
		Set<AbstractMethodFlow> res = s.createMethodSummary(mSig).getFlowsForMethod(mSig);
		assertTrue(containsParaToReturn(res, 0, DATA, DATACLASS_OBJECT_FIELD, DATACLASS_OBJECT_FIELD));
		// assertTrue(res.size() == 2);
	}

	@Test(timeout = 100000)
	public void staticStandardFlow1() {
		SummaryGenerator s = getSummary();
		String mSig = "<" + className + ": int staticStandardFlow1(int,int)>";
		Set<AbstractMethodFlow> res = s.createMethodSummary(mSig).getFlowsForMethod(mSig);
		assertTrue(containsParaToReturn(res, 0, INT_TYPE, NO_ACCESS_PATH, NO_ACCESS_PATH));
		assertTrue(containsParaToReturn(res, 1, INT_TYPE, NO_ACCESS_PATH, NO_ACCESS_PATH));
		// assertTrue(res.size() == 2);
	}

	@Test(timeout = 100000)
	public void staticStandardFlow11() {
		SummaryGenerator s = getSummary();
		String mSig = "<" + className + ": int staticStandardFlow1(int,int)>";
		Set<AbstractMethodFlow> res = s.createMethodSummary(mSig).getFlowsForMethod(mSig);

		assertTrue(containsParaToReturn(res, 0, INT_TYPE, NO_ACCESS_PATH, NO_ACCESS_PATH));
		assertTrue(containsParaToReturn(res, 1, INT_TYPE, NO_ACCESS_PATH, NO_ACCESS_PATH));
		assertTrue(res.size() == 2);
	}

	@Test(timeout = 100000)
	public void staticStandardFlow2() {
		SummaryGenerator s = getSummary();
		String mSig = "<" + className + ": " + DATA_TYPE + " staticStandardFlow2(int,java.lang.Object)>";
		Set<AbstractMethodFlow> res = s.createMethodSummary(mSig).getFlowsForMethod(mSig);
		assertTrue(containsParaToReturn(res, 0, INT_TYPE, NO_ACCESS_PATH, DATACLASS_INT_FIELD));
		assertTrue(containsParaToReturn(res, 1, OBJECT_TYPE, NO_ACCESS_PATH, DATACLASS_OBJECT_FIELD));
		// assertTrue(res.size() == 2);
	}

	@Test(timeout = 100000)
	public void staticStandardFlow21() {
		SummaryGenerator s = getSummary();
		String mSig = "<" + className + ": " + DATA_TYPE + " staticStandardFlow2(int,java.lang.Object)>";
		Set<AbstractMethodFlow> res = s.createMethodSummary(mSig).getFlowsForMethod(mSig);
		assertTrue(containsParaToReturn(res, 0, INT_TYPE, NO_ACCESS_PATH, DATACLASS_INT_FIELD));
		assertTrue(containsParaToReturn(res, 1, OBJECT_TYPE, NO_ACCESS_PATH, DATACLASS_OBJECT_FIELD));
		//assertTrue(res.size() == 2);
	}

	@Test(timeout = 100000)
	public void noFlow() {
		SummaryGenerator s = getSummary();
		String mSig = "<" + className + ": int noFlow(int)>";
		Set<AbstractMethodFlow> res = s.createMethodSummary(mSig).getFlowsForMethod(mSig);

		assertTrue(res == null || res.size() == 0);
	}

	@Test(timeout = 100000)
	public void noFlow2() {
		SummaryGenerator s = getSummary();
		String mSig = "<" + className + ": int noFlow2(int,int)>";
		Set<AbstractMethodFlow> res = s.createMethodSummary(mSig).getFlowsForMethod(mSig);
		assertTrue(res == null || res.size() == 0);
	}

	@Test(timeout = 100000)
	public void noFlow3() {
		SummaryGenerator s = getSummary();
		String mSig = "<" + className + ": " + DATA_TYPE + " noFlow3(" + DATA_TYPE + ")>";
		Set<AbstractMethodFlow> res = s.createMethodSummary(mSig).getFlowsForMethod(mSig);
		assertTrue(res == null || res.size() == 0);
	}

	@Test(timeout = 100000)
	public void noFlow4() {
		SummaryGenerator s = getSummary();
		String mSig = "<" + className + ": " + DATA_TYPE + " noFlow4(int,java.lang.Object)>";
		Set<AbstractMethodFlow> res = s.createMethodSummary(mSig).getFlowsForMethod(mSig);
		assertTrue(res == null || res.size() == 0);
	}

	@Test(timeout = 100000)
	public void paraToVar() {
		SummaryGenerator s = getSummary();
		String mSig = "<" + className + ": int paraToVar(int,int)>";
		Set<AbstractMethodFlow> res = s.createMethodSummary(mSig).getFlowsForMethod(mSig);
		assertTrue(containsParaToFieldFlow(res, 0, INT_TYPE, NO_ACCESS_PATH,PRIMITIVE_VAR , NO_ACCESS_PATH));
		assertTrue(containsParaToFieldFlow(res, 1, INT_TYPE, NO_ACCESS_PATH, PRIMITIVE_VAR, NO_ACCESS_PATH));
	}

	@Test(timeout = 100000)
	public void paraToVar12() {
		SummaryGenerator s = getSummary();
		String mSig = "<" + className + ": int paraToVar(int,int)>";
		Set<AbstractMethodFlow> res = s.createMethodSummary(mSig).getFlowsForMethod(mSig);
		assertTrue(containsParaToFieldFlow(res, 0, INT_TYPE, NO_ACCESS_PATH,PRIMITIVE_VAR, NO_ACCESS_PATH));
		assertTrue(containsParaToFieldFlow(res, 1, INT_TYPE, NO_ACCESS_PATH,PRIMITIVE_VAR, NO_ACCESS_PATH));
		assertTrue(res.size() == 2);
	}

	@Test//(timeout = 100000)
	public void paraToVar2() {
		SummaryGenerator s = getSummary();
		String mSig = "<" + className + ": " + DATA_TYPE + " paraToVar2(int,java.lang.Object)>";
		Set<AbstractMethodFlow> res = s.createMethodSummary(mSig).getFlowsForMethod(mSig);
		assertTrue(containsParaToFieldFlow(res, 0, INT_TYPE, NO_ACCESS_PATH,NON_PRIMITIVE_VAR1, DATACLASS_INT_FIELD));
		assertTrue(containsParaToFieldFlow(res, 1, OBJECT_TYPE, NO_ACCESS_PATH,NON_PRIMITIVE_VAR1,
				DATACLASS_OBJECT_FIELD));
		// assertTrue(res.size() == 2);
	}

	@Test(timeout = 100000)
	public void paraToVar21() {
		SummaryGenerator s = getSummary();
		String mSig = "<" + className + ": " + DATA_TYPE + " paraToVar2(int,java.lang.Object)>";
		Set<AbstractMethodFlow> res = s.createMethodSummary(mSig).getFlowsForMethod(mSig);
		assertTrue(containsParaToFieldFlow(res, 0, INT_TYPE, NO_ACCESS_PATH,NON_PRIMITIVE_VAR1 , DATACLASS_INT_FIELD));
		assertTrue(containsParaToFieldFlow(res, 1, OBJECT_TYPE,NO_ACCESS_PATH, NON_PRIMITIVE_VAR1, 
				DATACLASS_OBJECT_FIELD));
		assertTrue(containsParaToReturn(res, 0, INT_TYPE, NO_ACCESS_PATH, DATACLASS_INT_FIELD));
		assertTrue(containsParaToReturn(res, 1, OBJECT_TYPE, NO_ACCESS_PATH, DATACLASS_OBJECT_FIELD));
		//assertTrue(res.size() == 4); //TODO add me
	}

	@Test(timeout = 100000)
	public void paraToparaFlow1WrongSinkSigAccepted() {
		SummaryGenerator s = getSummary();
		String mSig = "<" + className + ": void paraToparaFlow1(int," + DATA_TYPE + ")>";
		Set<AbstractMethodFlow> res = s.createMethodSummary(mSig).getFlowsForMethod(mSig);
		assertTrue(containsParaToParaFlow(res, 0, INT_TYPE, NO_ACCESS_PATH, 1, DATA_TYPE, DATACLASS_INT_FIELD));
		assertTrue(res.size() == 1);
	}

	@Test(timeout = 100000)
	public void paraToparaFlow2() {
		SummaryGenerator s = getSummary();
		String mSig = "<" + className + ": void paraToparaFlow2(int,java.lang.Object," + DATA_TYPE + ")>";
		Set<AbstractMethodFlow> res = s.createMethodSummary(mSig).getFlowsForMethod(mSig);
		assertTrue(containsParaToParaFlow(res, 0, INT_TYPE, NO_ACCESS_PATH, 2, DATA_TYPE, DATACLASS_INT_FIELD));
		assertTrue(containsParaToParaFlow(res, 1, OBJECT_TYPE, NO_ACCESS_PATH, 2, DATA_TYPE,
				DATACLASS_OBJECT_FIELD));
		assertTrue(res.size() == 2);

	}

	@Test(timeout = 100000)
	public void paraToparaFlow3WrongSinkSigAccepted() {
		SummaryGenerator s = getSummary();
		String mSig = "<" + className + ": void paraToparaFlow3(int,java.lang.Object," + DATA_TYPE + ","
				+ DATA_TYPE + ")>";
		Set<AbstractMethodFlow> res = s.createMethodSummary(mSig).getFlowsForMethod(mSig);
		assertTrue(containsParaToParaFlow(res, 0, INT_TYPE, NO_ACCESS_PATH, 2, DATA_TYPE, DATACLASS_INT_FIELD));
		assertTrue(containsParaToParaFlow(res, 1, OBJECT_TYPE, NO_ACCESS_PATH, 2, DATA_TYPE,
				DATACLASS_OBJECT_FIELD));
		assertTrue(containsParaToParaFlow(res, 1, OBJECT_TYPE, NO_ACCESS_PATH, 3, DATA_TYPE,
				DATACLASS_OBJECT_FIELD));
	}

	@Test(timeout = 100000)
	public void staticParaToParaFlow1WrongSinkSigAccepted() {
		SummaryGenerator s = getSummary();
		String mSig = "<" + className + ": void staticParaToparaFlow1(int," + DATA_TYPE + ")>";
		Set<AbstractMethodFlow> res = s.createMethodSummary(mSig).getFlowsForMethod(mSig);
		assertTrue(containsParaToParaFlow(res, 0, INT_TYPE, NO_ACCESS_PATH, 1, DATA_TYPE, DATACLASS_INT_FIELD));
		assertTrue(res.size() == 1);
	}

	@Test(timeout = 100000)
	public void staticParaToParaFlow2WrongSinkSigAccepted() {
		SummaryGenerator s = getSummary();
		String mSig = "<" + className + ": void staticParaToparaFlow2(int,java.lang.Object," + DATA_TYPE + ")>";
		Set<AbstractMethodFlow> res = s.createMethodSummary(mSig).getFlowsForMethod(mSig);
		assertTrue(containsParaToParaFlow(res, 0, INT_TYPE, NO_ACCESS_PATH, 2, DATA_TYPE, DATACLASS_INT_FIELD));
		assertTrue(containsParaToParaFlow(res, 1, OBJECT_TYPE, NO_ACCESS_PATH, 2, DATA_TYPE,
				DATACLASS_OBJECT_FIELD));
		assertTrue(res.size() == 2);
	}

	@Test(timeout = 100000)
	public void staticParaToParaFlow3WrongSinkSigAccepted() {
		SummaryGenerator s = getSummary();
		String mSig = "<" + className + ": void staticParaToparaFlow3(int,java.lang.Object," + DATA_TYPE + ","
				+ DATA_TYPE + ")>";
		Set<AbstractMethodFlow> res = s.createMethodSummary(mSig).getFlowsForMethod(mSig);
		assertTrue(containsParaToParaFlow(res, 0, INT_TYPE,NO_ACCESS_PATH, 2, DATA_TYPE,DATACLASS_INT_FIELD));
		assertTrue(containsParaToParaFlow(res, 1, OBJECT_TYPE,NO_ACCESS_PATH, 2, DATA_TYPE,DATACLASS_OBJECT_FIELD));
	}

	@Test(timeout = 100000)
	public void mixedFlow1() {
		SummaryGenerator s = getSummary();
		String mSig = "<" + className + ": " + DATA_TYPE + " mixedFlow1(int," + DATA_TYPE + ")>";
		Set<AbstractMethodFlow> res = s.createMethodSummary(mSig).getFlowsForMethod(mSig);
		assertTrue(containsParaToReturn(res, 1, DATA_TYPE,NO_ACCESS_PATH,NO_ACCESS_PATH));
		// assertTrue(containsParaToReturn(res, 0, INT_TYPE));
		assertTrue(containsParaToParaFlow(res, 0, INT_TYPE,NO_ACCESS_PATH, 1, DATA_TYPE,DATACLASS_INT_FIELD));
		assertTrue(containsParaToFieldFlow(res, 1, DATA_TYPE,DATACLASS_INT_FIELD, PRIMITIVE_VAR,NO_ACCESS_PATH));

		// assertTrue(res.size() == 2);
	}

	@Test(timeout = 100000)
	public void paraToparaFlow1() {
		SummaryGenerator s = getSummary();
		String mSig = "<" + className + ": void paraToparaFlow1(int," + DATA_TYPE + ")>";
		Set<AbstractMethodFlow> res = s.createMethodSummary(mSig).getFlowsForMethod(mSig);
		assertTrue(containsParaToParaFlow(res, 0, INT_TYPE,NO_ACCESS_PATH, 1, DATA_TYPE,NO_ACCESS_PATH));
		assertTrue(res.size() == 1);
	}

	@Test(timeout = 100000)
	public void paraToparaFlow3() {
		SummaryGenerator s = getSummary();
		String mSig = "<" + className + ": void paraToparaFlow3(int,java.lang.Object," + DATA_TYPE + ","
				+ DATA_TYPE + ")>";
		Set<AbstractMethodFlow> res = s.createMethodSummary(mSig).getFlowsForMethod(mSig);
		assertTrue(containsParaToParaFlow(res, 0, INT_TYPE,NO_ACCESS_PATH, 2, DATA_TYPE,DATACLASS_INT_FIELD));
		assertTrue(containsParaToParaFlow(res, 1, OBJECT_TYPE,NO_ACCESS_PATH, 2, DATA_TYPE,DATACLASS_OBJECT_FIELD));
		assertTrue(containsParaToParaFlow(res, 1, OBJECT_TYPE,NO_ACCESS_PATH, 3, DATA_TYPE,NO_ACCESS_PATH));
		// assertTrue(res.size() == 2);
	}

	@Test(timeout = 100000)
	public void staticParaToParaFlow1() {
		SummaryGenerator s = getSummary();
		String mSig = "<" + className + ": void staticParaToparaFlow1(int," + DATA_TYPE + ")>";
		Set<AbstractMethodFlow> res = s.createMethodSummary(mSig).getFlowsForMethod(mSig);
		assertTrue(containsParaToParaFlow(res, 0, INT_TYPE,NO_ACCESS_PATH, 1, DATA_TYPE,DATACLASS_INT_FIELD));
		assertTrue(res.size() == 1);
	}

	@Test(timeout = 100000)
	public void staticParaToParaFlow2() {
		SummaryGenerator s = getSummary();
		String mSig = "<" + className + ": void staticParaToparaFlow2(int,java.lang.Object," + DATA_TYPE + ")>";
		Set<AbstractMethodFlow> res = s.createMethodSummary(mSig).getFlowsForMethod(mSig);

		assertTrue(containsParaToParaFlow(res, 0, INT_TYPE,NO_ACCESS_PATH, 2, DATA_TYPE,DATACLASS_INT_FIELD));
		assertTrue(containsParaToParaFlow(res, 1, OBJECT_TYPE,NO_ACCESS_PATH, 2, DATA_TYPE,DATACLASS_OBJECT_FIELD));
		assertTrue(res.size() == 2);
	}

	@Test(timeout = 100000)
	public void staticParaToParaFlow3() {
		SummaryGenerator s = getSummary();
		String mSig = "<" + className + ": void staticParaToparaFlow3(int,java.lang.Object," + DATA_TYPE + ","
				+ DATA_TYPE + ")>";
		Set<AbstractMethodFlow> res = s.createMethodSummary(mSig).getFlowsForMethod(mSig);

		assertTrue(containsParaToParaFlow(res, 0, INT_TYPE,NO_ACCESS_PATH, 2, DATA_TYPE,DATACLASS_INT_FIELD));
		assertTrue(containsParaToParaFlow(res, 1, OBJECT_TYPE,NO_ACCESS_PATH, 2, DATA_TYPE,DATACLASS_OBJECT_FIELD));
		assertTrue(containsParaToParaFlow(res, 1, OBJECT_TYPE,NO_ACCESS_PATH, 3, DATA_TYPE,DATACLASS_OBJECT_FIELD));
		// assertTrue(res.size() == 2);
	}

	@Test(timeout = 100000)
	public void primitivVarToReturn1() {
		SummaryGenerator s = getSummary();
		String mSig = "<" + className + ": int intParaToReturn()>";
		Set<AbstractMethodFlow> res = s.createMethodSummary(mSig).getFlowsForMethod(mSig);
		assertTrue(containsFieldToReturn(res, PRIMITIVE_VAR,NO_ACCESS_PATH,NO_ACCESS_PATH));
	}

	@Test(timeout = 100000)
	public void nonPrimitivVarToReturn1() {
		SummaryGenerator s = getSummary();
		String mSig = "<" + className + ": int intInDataToReturn()>";
		Set<AbstractMethodFlow> res = s.createMethodSummary(mSig).getFlowsForMethod(mSig);

		assertTrue(containsFieldToReturn(res, NON_PRIMITIVE_VAR1,NO_ACCESS_PATH,NO_ACCESS_PATH)
				|| containsFieldToReturn(res, NON_PRIMITIVE_VAR1_VALUE,NO_ACCESS_PATH,NO_ACCESS_PATH));
	}

	@Test(timeout = 100000)
	public void nonPrimitivVarToReturn11() {
		SummaryGenerator s = getSummary();
		String mSig = "<" + className + ": int intInDataToReturn()>";
		Set<AbstractMethodFlow> res = s.createMethodSummary(mSig).getFlowsForMethod(mSig);
		assertTrue(containsFieldToReturn(res, NON_PRIMITIVE_VAR1,DATACLASS_INT_FIELD,NO_ACCESS_PATH));
		assertTrue(res.size() == 1);
	}

	@Test//(timeout = 100000)
	public void nonPrimitivVarToReturn2() {
		SummaryGenerator s = getSummary();
		String mSig = "<" + className + ": int intInDataToReturn2()>";
		Set<AbstractMethodFlow> res = s.createMethodSummary(mSig).getFlowsForMethod(mSig);
		assertTrue(containsFieldToReturn(res, NON_PRIMITIVE_VAR1,DATACLASS_INT_FIELD,NO_ACCESS_PATH));
	}

	@Test(timeout = 100000)
	public void nonPrimitivVarToReturn3() {
		SummaryGenerator s = getSummary();
		String mSig = "<" + className + ": int intInDataToReturn3()>";
		Set<AbstractMethodFlow> res = s.createMethodSummary(mSig).getFlowsForMethod(mSig);

		assertTrue(containsFieldToReturn(res, NON_PRIMITIVE_VAR1,DATACLASS_INT_FIELD,NO_ACCESS_PATH));
	}

	@Test(timeout = 100000)
	public void nonPrimitivVarToReturn4() {
		SummaryGenerator s = getSummary();
		String mSig = "<" + className + ": " + DATA_TYPE + " dataFieldToReturn()>";
		Set<AbstractMethodFlow> res = s.createMethodSummary(mSig).getFlowsForMethod(mSig);
		assertTrue(containsFieldToReturn(res, NON_PRIMITIVE_VAR1,NO_ACCESS_PATH,NO_ACCESS_PATH));
	}

	@Test(timeout = 200000)
	public void nonPrimitivVarToReturn5() {
		SummaryGenerator s = getSummary();
		String mSig = "<" + className + ": " + DATA_TYPE + " dataFieldToReturn2()>";
		Set<AbstractMethodFlow> res = s.createMethodSummary(mSig).getFlowsForMethod(mSig);
		assertTrue(containsFieldToReturn(res, "<soot.jimple.infoflow.test.methodSummary.ApiClass: soot.jimple.infoflow.test.methodSummary.Data nonPrimitiveVariable>",NO_ACCESS_PATH,NO_ACCESS_PATH));
	}

	@Test//(timeout = 100000)
	public void swap() {
		SummaryGenerator s = getSummary();
		String mSig = "<" + className + ": void swap()>";
		Set<AbstractMethodFlow> res = s.createMethodSummary(mSig).getFlowsForMethod(mSig);
		assertTrue(containsFieldToFieldFlow(res, NON_PRIMITIVE_VAR1,NO_ACCESS_PATH, NON_PRIMITIVE_VAR2,NO_ACCESS_PATH));
		assertTrue(containsFieldToFieldFlow(res, NON_PRIMITIVE_VAR2,NO_ACCESS_PATH, NON_PRIMITIVE_VAR1,NO_ACCESS_PATH));
	}
	
	@Test//(timeout = 100000)
	public void swap2() {
		SummaryGenerator s = getSummary();
		String mSig = "<" + className + ": void swap2()>";
		Set<AbstractMethodFlow> res = s.createMethodSummary(mSig).getFlowsForMethod(mSig);
		assertTrue(containsFieldToFieldFlow(res, NON_PRIMITIVE_VAR1,DATACLASS_OBJECT_FIELD, NON_PRIMITIVE_VAR2,DATACLASS_OBJECT_FIELD));
		assertTrue(containsFieldToFieldFlow(res, NON_PRIMITIVE_VAR2,DATACLASS_INT_FIELD, NON_PRIMITIVE_VAR1,DATACLASS_INT_FIELD));
	}

	@Test(timeout = 100000)
	public void fieldToField1() {
		SummaryGenerator s = getSummary();
		String mSig = "<" + className + ": void data1ToDate2()>";
		Set<AbstractMethodFlow> res = s.createMethodSummary(mSig).getFlowsForMethod(mSig);
		assertTrue(containsFieldToFieldFlow(res, NON_PRIMITIVE_VAR1,NO_ACCESS_PATH, NON_PRIMITIVE_VAR2,NO_ACCESS_PATH));
	}

	@Test(timeout = 150000)
	public void fieldToPara1() {
		SummaryGenerator s = getSummary();
		String mSig = "<" + className + ": void fieldToPara(" + DATA_TYPE + ")>";
		Set<AbstractMethodFlow> res = s.createMethodSummary(mSig).getFlowsForMethod(mSig);
		assertTrue(containsFieldToParaFlow(res, NON_PRIMITIVE_VAR1,DATACLASS_INT_FIELD, 0,DATA_TYPE,DATACLASS_INT_FIELD));
	}
	@Test(timeout = 150000)
	public void ListGetTest() {
		SummaryGenerator s = getSummary();
		String mSig = "<" + className + ": java.lang.Object get()>";
		Set<AbstractMethodFlow> res = s.createMethodSummary(mSig,java.util.Collections.singletonList("<soot.jimple.infoflow.test.methodSummary.ApiClass: void set(soot.jimple.infoflow.test.methodSummary.ApiClass$Node)>")).getFlowsForMethod(mSig);
		assertTrue(containsFieldToReturn(res,
					"<soot.jimple.infoflow.test.methodSummary.ApiClass: soot.jimple.infoflow.test.methodSummary.ApiClass$Node first>", 
					"<soot.jimple.infoflow.test.methodSummary.ApiClass$Node: java.lang.Object item>",
					null));
	}


}
