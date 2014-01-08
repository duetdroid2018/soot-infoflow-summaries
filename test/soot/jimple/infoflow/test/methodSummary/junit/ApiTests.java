package soot.jimple.infoflow.test.methodSummary.junit;

import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.junit.Test;

import soot.jimple.infoflow.methodSummary.Summary;
import soot.jimple.infoflow.methodSummary.data.AbstractMethodFlow;

public class ApiTests extends ApiTestHelper {
	static final String className = "soot.jimple.infoflow.test.methodSummary.ApiClass";
	

	@Test //(timeout = 100000)
	public void standardFlow1() {
		Summary s = getSummary();
		String mSig = "<" + className + ": int standardFlow(int)>";
		Set<AbstractMethodFlow> res = s.createMethodSummary(mSig).get(mSig);
		assertTrue(res.size() == 1);
		assertTrue(containsParaToReturn(res, 0, INT_TYPE, NO_ACCESS_PATH, NO_ACCESS_PATH));
	}

	@Test(timeout = 100000)
	public void standardFlow11() {
		Summary s = getSummary();
		String mSig = "<" + className + ": int standardFlow(int)>";
		Set<AbstractMethodFlow> res = s.createMethodSummary(mSig).get(mSig);
		assertTrue(res.size() == 1);
	}

	@Test//(timeout = 100000)
	public void standardFlow2() {
		Summary s = getSummary();
		String mSig = "<" + className + ": int standardFlow2(int,int)>";
		Set<AbstractMethodFlow> res = s.createMethodSummary(mSig).get(mSig);
		assertTrue(containsParaToReturn(res, 0, INT_TYPE, NO_ACCESS_PATH, NO_ACCESS_PATH));
		assertTrue(containsParaToReturn(res, 1, INT_TYPE, NO_ACCESS_PATH, NO_ACCESS_PATH));
	}

	@Test(timeout = 100000)
	public void standardFlow2Com() {
		Summary s = getSummary();
		String mSig = "<" + className + ": int standardFlow2Com(int,int)>";
		Set<AbstractMethodFlow> res = s.createMethodSummary(mSig).get(mSig);
		assertTrue(containsParaToReturn(res, 0, INT_TYPE, NO_ACCESS_PATH, NO_ACCESS_PATH));
		assertTrue(containsParaToReturn(res, 1, INT_TYPE, NO_ACCESS_PATH, NO_ACCESS_PATH));
	}

	@Test(timeout = 100000)
	public void standardFlow22() {
		Summary s = getSummary();
		String mSig = "<" + className + ": int standardFlow2(int,int)>";
		Set<AbstractMethodFlow> res = s.createMethodSummary(mSig).get(mSig);
		assertTrue(res.size() == 2);
		assertTrue(containsParaToReturn(res, 0, INT_TYPE, NO_ACCESS_PATH, NO_ACCESS_PATH));
		assertTrue(containsParaToReturn(res, 1, INT_TYPE, NO_ACCESS_PATH, NO_ACCESS_PATH));
	}

	@Test//(timeout = 100000)
	public void standardFlow3() {
		Summary s = getSummary();
		String mSig = "<" + className + ": int standardFlow3(" + NON_PRIMITIVE_TYP + ")>";
		Set<AbstractMethodFlow> res = s.createMethodSummary(mSig).get(mSig);
		assertTrue(containsParaToReturn(res, 0, NON_PRIMITIVE_TYP, NO_ACCESS_PATH, NO_ACCESS_PATH));
		assertTrue(containsParaToReturn(res, 0, NON_PRIMITIVE_TYP, DATACLASS_INT_FIELD, NO_ACCESS_PATH));
		// assertTrue(res.size() == 1);
	}

	@Test(timeout = 100000)
	public void standardFlow31() {
		Summary s = getSummary();
		String mSig = "<" + className + ": int standardFlow3(" + NON_PRIMITIVE_TYP + ")>";
		Set<AbstractMethodFlow> res = s.createMethodSummary(mSig).get(mSig);
		assertTrue(res.size() == 1);
	}

	@Test//(timeout = 100000)
	public void standardFlow4() {
		Summary s = getSummary();
		String mSig = "<" + className + ": " + NON_PRIMITIVE_TYP + " standardFlow4(int,java.lang.Object)>";
		Set<AbstractMethodFlow> res = s.createMethodSummary(mSig).get(mSig);
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
		Summary s = getSummary();
		String mSig = "<" + className + ": " + NON_PRIMITIVE_TYP + " standardFlow6(java.lang.Object)>";
		Set<AbstractMethodFlow> res = s.createMethodSummary(mSig).get(mSig);
		assertTrue(containsParaToReturn(res, 0, OBJECT_TYPE, NO_ACCESS_PATH, DATACLASS_OBJECT_FIELD));
		// assertTrue(res.size() == 2);
	}

	@Test(timeout = 100000)
	public void standardFlow8() {
		Summary s = getSummary();
		String mSig = "<soot.jimple.infoflow.test.methodSummary.ApiClass: soot.jimple.infoflow.test.methodSummary.Data standardFlow8(soot.jimple.infoflow.test.methodSummary.Data)>";
		Set<AbstractMethodFlow> res = s.createMethodSummary(mSig).get(mSig);
		assertTrue(containsParaToReturn(res, 0, DATA, DATACLASS_OBJECT_FIELD, DATACLASS_OBJECT_FIELD));
		// assertTrue(res.size() == 2);
	}

	@Test(timeout = 100000)
	public void staticStandardFlow1() {
		Summary s = getSummary();
		String mSig = "<" + className + ": int staticStandardFlow1(int,int)>";
		Set<AbstractMethodFlow> res = s.createMethodSummary(mSig).get(mSig);
		assertTrue(containsParaToReturn(res, 0, INT_TYPE, NO_ACCESS_PATH, NO_ACCESS_PATH));
		assertTrue(containsParaToReturn(res, 1, INT_TYPE, NO_ACCESS_PATH, NO_ACCESS_PATH));
		// assertTrue(res.size() == 2);
	}

	@Test(timeout = 100000)
	public void staticStandardFlow11() {
		Summary s = getSummary();
		String mSig = "<" + className + ": int staticStandardFlow1(int,int)>";
		Set<AbstractMethodFlow> res = s.createMethodSummary(mSig).get(mSig);

		assertTrue(containsParaToReturn(res, 0, INT_TYPE, NO_ACCESS_PATH, NO_ACCESS_PATH));
		assertTrue(containsParaToReturn(res, 1, INT_TYPE, NO_ACCESS_PATH, NO_ACCESS_PATH));
		assertTrue(res.size() == 2);
	}

	@Test(timeout = 100000)
	public void staticStandardFlow2() {
		Summary s = getSummary();
		String mSig = "<" + className + ": " + NON_PRIMITIVE_TYP + " staticStandardFlow2(int,java.lang.Object)>";
		Set<AbstractMethodFlow> res = s.createMethodSummary(mSig).get(mSig);
		assertTrue(containsParaToReturn(res, 0, INT_TYPE, NO_ACCESS_PATH, DATACLASS_INT_FIELD));
		assertTrue(containsParaToReturn(res, 1, OBJECT_TYPE, NO_ACCESS_PATH, DATACLASS_OBJECT_FIELD));
		// assertTrue(res.size() == 2);
	}

	@Test(timeout = 100000)
	public void staticStandardFlow21() {
		Summary s = getSummary();
		String mSig = "<" + className + ": " + NON_PRIMITIVE_TYP + " staticStandardFlow2(int,java.lang.Object)>";
		Set<AbstractMethodFlow> res = s.createMethodSummary(mSig).get(mSig);
		assertTrue(containsParaToReturn(res, 0, INT_TYPE, NO_ACCESS_PATH, DATACLASS_INT_FIELD));
		assertTrue(containsParaToReturn(res, 1, OBJECT_TYPE, NO_ACCESS_PATH, DATACLASS_OBJECT_FIELD));
		//assertTrue(res.size() == 2);
	}

	@Test(timeout = 100000)
	public void noFlow() {
		Summary s = getSummary();
		String mSig = "<" + className + ": int noFlow(int)>";
		Set<AbstractMethodFlow> res = s.createMethodSummary(mSig).get(mSig);

		assertTrue(res == null || res.size() == 0);
	}

	@Test(timeout = 100000)
	public void noFlow2() {
		Summary s = getSummary();
		String mSig = "<" + className + ": int noFlow2(int,int)>";
		Set<AbstractMethodFlow> res = s.createMethodSummary(mSig).get(mSig);
		assertTrue(res == null || res.size() == 0);
	}

	@Test(timeout = 100000)
	public void noFlow3() {
		Summary s = getSummary();
		String mSig = "<" + className + ": " + NON_PRIMITIVE_TYP + " noFlow3(" + NON_PRIMITIVE_TYP + ")>";
		Set<AbstractMethodFlow> res = s.createMethodSummary(mSig).get(mSig);
		assertTrue(res == null || res.size() == 0);
	}

	@Test(timeout = 100000)
	public void noFlow4() {
		Summary s = getSummary();
		String mSig = "<" + className + ": " + NON_PRIMITIVE_TYP + " noFlow4(int, java.lang.Object)>";
		Set<AbstractMethodFlow> res = s.createMethodSummary(mSig).get(mSig);
		assertTrue(res == null || res.size() == 0);
	}

	@Test(timeout = 100000)
	public void paraToVar() {
		Summary s = getSummary();
		String mSig = "<" + className + ": int paraToVar(int,int)>";
		Set<AbstractMethodFlow> res = s.createMethodSummary(mSig).get(mSig);
		assertTrue(containsParaToFieldFlow(res, 0, INT_TYPE, NO_ACCESS_PATH,PRIMITIVE_VAR , NO_ACCESS_PATH));
		assertTrue(containsParaToFieldFlow(res, 1, INT_TYPE, NO_ACCESS_PATH, PRIMITIVE_VAR, NO_ACCESS_PATH));
	}

	@Test(timeout = 100000)
	public void paraToVar12() {
		Summary s = getSummary();
		String mSig = "<" + className + ": int paraToVar(int,int)>";
		Set<AbstractMethodFlow> res = s.createMethodSummary(mSig).get(mSig);
		assertTrue(containsParaToFieldFlow(res, 0, INT_TYPE, NO_ACCESS_PATH,PRIMITIVE_VAR, NO_ACCESS_PATH));
		assertTrue(containsParaToFieldFlow(res, 1, INT_TYPE, NO_ACCESS_PATH,PRIMITIVE_VAR, NO_ACCESS_PATH));
		assertTrue(res.size() == 2);
	}

	@Test//(timeout = 100000)
	public void paraToVar2() {
		Summary s = getSummary();
		String mSig = "<" + className + ": " + NON_PRIMITIVE_TYP + " paraToVar2(int,java.lang.Object)>";
		Set<AbstractMethodFlow> res = s.createMethodSummary(mSig).get(mSig);
		assertTrue(containsParaToFieldFlow(res, 0, INT_TYPE, NO_ACCESS_PATH,NON_PRIMITIVE_VAR1, DATACLASS_INT_FIELD));
		assertTrue(containsParaToFieldFlow(res, 1, OBJECT_TYPE, NO_ACCESS_PATH,NON_PRIMITIVE_VAR1,
				DATACLASS_OBJECT_FIELD));
		// assertTrue(res.size() == 2);
	}

	@Test(timeout = 100000)
	public void paraToVar21() {
		Summary s = getSummary();
		String mSig = "<" + className + ": " + NON_PRIMITIVE_TYP + " paraToVar2(int,java.lang.Object)>";
		Set<AbstractMethodFlow> res = s.createMethodSummary(mSig).get(mSig);
		assertTrue(containsParaToFieldFlow(res, 0, INT_TYPE, NO_ACCESS_PATH,NON_PRIMITIVE_VAR1 , DATACLASS_INT_FIELD));
		assertTrue(containsParaToFieldFlow(res, 1, OBJECT_TYPE,NO_ACCESS_PATH, NON_PRIMITIVE_VAR1, 
				DATACLASS_OBJECT_FIELD));
		assertTrue(containsParaToReturn(res, 0, INT_TYPE, NO_ACCESS_PATH, DATACLASS_INT_FIELD));
		assertTrue(containsParaToReturn(res, 1, OBJECT_TYPE, NO_ACCESS_PATH, DATACLASS_OBJECT_FIELD));
		//assertTrue(res.size() == 4); //TODO add me
	}

	@Test(timeout = 100000)
	public void paraToparaFlow1WrongSinkSigAccepted() {
		Summary s = getSummary();
		String mSig = "<" + className + ": void paraToparaFlow1(int," + NON_PRIMITIVE_TYP + ")>";
		Set<AbstractMethodFlow> res = s.createMethodSummary(mSig).get(mSig);
		assertTrue(containsParaToParaFlow(res, 0, INT_TYPE, NO_ACCESS_PATH, 1, NON_PRIMITIVE_TYP, DATACLASS_INT_FIELD));
		assertTrue(res.size() == 1);
	}

	@Test(timeout = 100000)
	public void paraToparaFlow2() {
		Summary s = getSummary();
		String mSig = "<" + className + ": void paraToparaFlow2(int,java.lang.Object," + NON_PRIMITIVE_TYP + ")>";
		Set<AbstractMethodFlow> res = s.createMethodSummary(mSig).get(mSig);
		assertTrue(containsParaToParaFlow(res, 0, INT_TYPE, NO_ACCESS_PATH, 2, NON_PRIMITIVE_TYP, DATACLASS_INT_FIELD));
		assertTrue(containsParaToParaFlow(res, 1, OBJECT_TYPE, NO_ACCESS_PATH, 2, NON_PRIMITIVE_TYP,
				DATACLASS_OBJECT_FIELD));
		assertTrue(res.size() == 2);

	}

	@Test(timeout = 100000)
	public void paraToparaFlow3WrongSinkSigAccepted() {
		Summary s = getSummary();
		String mSig = "<" + className + ": void paraToparaFlow3(int,java.lang.Object," + NON_PRIMITIVE_TYP + ","
				+ NON_PRIMITIVE_TYP + ")>";
		Set<AbstractMethodFlow> res = s.createMethodSummary(mSig).get(mSig);
		assertTrue(containsParaToParaFlow(res, 0, INT_TYPE, NO_ACCESS_PATH, 2, NON_PRIMITIVE_TYP, DATACLASS_INT_FIELD));
		assertTrue(containsParaToParaFlow(res, 1, OBJECT_TYPE, NO_ACCESS_PATH, 2, NON_PRIMITIVE_TYP,
				DATACLASS_OBJECT_FIELD));
		assertTrue(containsParaToParaFlow(res, 1, OBJECT_TYPE, NO_ACCESS_PATH, 3, NON_PRIMITIVE_TYP,
				DATACLASS_OBJECT_FIELD));
	}

	@Test(timeout = 100000)
	public void staticParaToParaFlow1WrongSinkSigAccepted() {
		Summary s = getSummary();
		String mSig = "<" + className + ": void staticParaToparaFlow1(int," + NON_PRIMITIVE_TYP + ")>";
		Set<AbstractMethodFlow> res = s.createMethodSummary(mSig).get(mSig);
		assertTrue(containsParaToParaFlow(res, 0, INT_TYPE, NO_ACCESS_PATH, 1, NON_PRIMITIVE_TYP, DATACLASS_INT_FIELD));
		assertTrue(res.size() == 1);
	}

	@Test(timeout = 100000)
	public void staticParaToParaFlow2WrongSinkSigAccepted() {
		Summary s = getSummary();
		String mSig = "<" + className + ": void staticParaToparaFlow2(int,java.lang.Object," + NON_PRIMITIVE_TYP + ")>";
		Set<AbstractMethodFlow> res = s.createMethodSummary(mSig).get(mSig);
		assertTrue(containsParaToParaFlow(res, 0, INT_TYPE, NO_ACCESS_PATH, 2, NON_PRIMITIVE_TYP, DATACLASS_INT_FIELD));
		assertTrue(containsParaToParaFlow(res, 1, OBJECT_TYPE, NO_ACCESS_PATH, 2, NON_PRIMITIVE_TYP,
				DATACLASS_OBJECT_FIELD));
		assertTrue(res.size() == 2);
	}

	@Test(timeout = 100000)
	public void staticParaToParaFlow3WrongSinkSigAccepted() {
		Summary s = getSummary();
		String mSig = "<" + className + ": void staticParaToparaFlow3(int,java.lang.Object," + NON_PRIMITIVE_TYP + ","
				+ NON_PRIMITIVE_TYP + ")>";
		Set<AbstractMethodFlow> res = s.createMethodSummary(mSig).get(mSig);
		assertTrue(containsParaToParaFlow(res, 0, INT_TYPE,NO_ACCESS_PATH, 2, NON_PRIMITIVE_TYP,DATACLASS_INT_FIELD));
		assertTrue(containsParaToParaFlow(res, 1, OBJECT_TYPE,NO_ACCESS_PATH, 2, NON_PRIMITIVE_TYP,DATACLASS_OBJECT_FIELD));
	}

	@Test(timeout = 100000)
	public void mixedFlow1() {
		Summary s = getSummary();
		String mSig = "<" + className + ": " + NON_PRIMITIVE_TYP + " mixedFlow1(int," + NON_PRIMITIVE_TYP + ")>";
		Set<AbstractMethodFlow> res = s.createMethodSummary(mSig).get(mSig);
		assertTrue(containsParaToReturn(res, 1, NON_PRIMITIVE_TYP,NO_ACCESS_PATH,NO_ACCESS_PATH));
		// assertTrue(containsParaToReturn(res, 0, INT_TYPE));
		assertTrue(containsParaToParaFlow(res, 0, INT_TYPE,NO_ACCESS_PATH, 1, NON_PRIMITIVE_TYP,DATACLASS_INT_FIELD));
		assertTrue(containsParaToFieldFlow(res, 1, NON_PRIMITIVE_TYP,DATACLASS_INT_FIELD, PRIMITIVE_VAR,NO_ACCESS_PATH));

		// assertTrue(res.size() == 2);
	}

	@Test(timeout = 100000)
	public void paraToparaFlow1() {
		Summary s = getSummary();
		String mSig = "<" + className + ": void paraToparaFlow1(int," + NON_PRIMITIVE_TYP + ")>";
		Set<AbstractMethodFlow> res = s.createMethodSummary(mSig).get(mSig);
		assertTrue(containsParaToParaFlow(res, 0, INT_TYPE,NO_ACCESS_PATH, 1, NON_PRIMITIVE_TYP,NO_ACCESS_PATH));
		assertTrue(res.size() == 1);
	}

	@Test(timeout = 100000)
	public void paraToparaFlow3() {
		Summary s = getSummary();
		String mSig = "<" + className + ": void paraToparaFlow3(int,java.lang.Object," + NON_PRIMITIVE_TYP + ","
				+ NON_PRIMITIVE_TYP + ")>";
		Set<AbstractMethodFlow> res = s.createMethodSummary(mSig).get(mSig);
		assertTrue(containsParaToParaFlow(res, 0, INT_TYPE,NO_ACCESS_PATH, 2, NON_PRIMITIVE_TYP,DATACLASS_INT_FIELD));
		assertTrue(containsParaToParaFlow(res, 1, OBJECT_TYPE,NO_ACCESS_PATH, 2, NON_PRIMITIVE_TYP,DATACLASS_OBJECT_FIELD));
		assertTrue(containsParaToParaFlow(res, 1, OBJECT_TYPE,NO_ACCESS_PATH, 3, NON_PRIMITIVE_TYP,NO_ACCESS_PATH));
		// assertTrue(res.size() == 2);
	}

	@Test(timeout = 100000)
	public void staticParaToParaFlow1() {
		Summary s = getSummary();
		String mSig = "<" + className + ": void staticParaToparaFlow1(int," + NON_PRIMITIVE_TYP + ")>";
		Set<AbstractMethodFlow> res = s.createMethodSummary(mSig).get(mSig);
		assertTrue(containsParaToParaFlow(res, 0, INT_TYPE,NO_ACCESS_PATH, 1, NON_PRIMITIVE_TYP,DATACLASS_INT_FIELD));
		assertTrue(res.size() == 1);
	}

	@Test(timeout = 100000)
	public void staticParaToParaFlow2() {
		Summary s = getSummary();
		String mSig = "<" + className + ": void staticParaToparaFlow2(int,java.lang.Object," + NON_PRIMITIVE_TYP + ")>";
		Set<AbstractMethodFlow> res = s.createMethodSummary(mSig).get(mSig);

		assertTrue(containsParaToParaFlow(res, 0, INT_TYPE,NO_ACCESS_PATH, 2, NON_PRIMITIVE_TYP,DATACLASS_INT_FIELD));
		assertTrue(containsParaToParaFlow(res, 1, OBJECT_TYPE,NO_ACCESS_PATH, 2, NON_PRIMITIVE_TYP,DATACLASS_OBJECT_FIELD));
		assertTrue(res.size() == 2);
	}

	@Test(timeout = 100000)
	public void staticParaToParaFlow3() {
		Summary s = getSummary();
		String mSig = "<" + className + ": void staticParaToparaFlow3(int,java.lang.Object," + NON_PRIMITIVE_TYP + ","
				+ NON_PRIMITIVE_TYP + ")>";
		Set<AbstractMethodFlow> res = s.createMethodSummary(mSig).get(mSig);

		assertTrue(containsParaToParaFlow(res, 0, INT_TYPE,NO_ACCESS_PATH, 2, NON_PRIMITIVE_TYP,DATACLASS_INT_FIELD));
		assertTrue(containsParaToParaFlow(res, 1, OBJECT_TYPE,NO_ACCESS_PATH, 2, NON_PRIMITIVE_TYP,DATACLASS_OBJECT_FIELD));
		assertTrue(containsParaToParaFlow(res, 1, OBJECT_TYPE,NO_ACCESS_PATH, 3, NON_PRIMITIVE_TYP,DATACLASS_OBJECT_FIELD));
		// assertTrue(res.size() == 2);
	}

	@Test(timeout = 100000)
	public void primitivVarToReturn1() {
		Summary s = getSummary();
		String mSig = "<" + className + ": int intParaToReturn()>";
		Set<AbstractMethodFlow> res = s.createMethodSummary(mSig).get(mSig);
		assertTrue(containsFieldToReturn(res, PRIMITIVE_VAR,NO_ACCESS_PATH,NO_ACCESS_PATH));
	}

	@Test(timeout = 100000)
	public void nonPrimitivVarToReturn1() {
		Summary s = getSummary();
		String mSig = "<" + className + ": int intInDataToReturn()>";
		Set<AbstractMethodFlow> res = s.createMethodSummary(mSig).get(mSig);

		assertTrue(containsFieldToReturn(res, NON_PRIMITIVE_VAR1,NO_ACCESS_PATH,NO_ACCESS_PATH)
				|| containsFieldToReturn(res, NON_PRIMITIVE_VAR1_VALUE,NO_ACCESS_PATH,NO_ACCESS_PATH));
	}

	@Test(timeout = 100000)
	public void nonPrimitivVarToReturn11() {
		Summary s = getSummary();
		String mSig = "<" + className + ": int intInDataToReturn()>";
		Set<AbstractMethodFlow> res = s.createMethodSummary(mSig).get(mSig);
		assertTrue(containsFieldToReturn(res, NON_PRIMITIVE_VAR1,DATACLASS_INT_FIELD,NO_ACCESS_PATH));
		assertTrue(res.size() == 1);
	}

	@Test//(timeout = 100000)
	public void nonPrimitivVarToReturn2() {
		Summary s = getSummary();
		String mSig = "<" + className + ": int intInDataToReturn2()>";
		Set<AbstractMethodFlow> res = s.createMethodSummary(mSig).get(mSig);
		assertTrue(containsFieldToReturn(res, NON_PRIMITIVE_VAR1,DATACLASS_INT_FIELD,NO_ACCESS_PATH));
	}

	@Test(timeout = 100000)
	public void nonPrimitivVarToReturn3() {
		Summary s = getSummary();
		String mSig = "<" + className + ": int intInDataToReturn3()>";
		Set<AbstractMethodFlow> res = s.createMethodSummary(mSig).get(mSig);

		assertTrue(containsFieldToReturn(res, NON_PRIMITIVE_VAR1,DATACLASS_INT_FIELD,NO_ACCESS_PATH));
	}

	@Test(timeout = 100000)
	public void nonPrimitivVarToReturn4() {
		Summary s = getSummary();
		String mSig = "<" + className + ": " + NON_PRIMITIVE_TYP + " dataFieldToReturn()>";
		Set<AbstractMethodFlow> res = s.createMethodSummary(mSig).get(mSig);
		assertTrue(containsFieldToReturn(res, NON_PRIMITIVE_VAR1,NO_ACCESS_PATH,NO_ACCESS_PATH));
	}

	@Test(timeout = 200000)
	public void nonPrimitivVarToReturn5() {
		Summary s = getSummary();
		String mSig = "<" + className + ": " + NON_PRIMITIVE_TYP + " dataFieldToReturn2()>";
		Set<AbstractMethodFlow> res = s.createMethodSummary(mSig).get(mSig);
		assertTrue(containsFieldToReturn(res, "<soot.jimple.infoflow.test.methodSummary.ApiClass: soot.jimple.infoflow.test.methodSummary.Data nonPrimitiveVariable>",NO_ACCESS_PATH,NO_ACCESS_PATH));
	}

	@Test//(timeout = 100000)
	public void swap() {
		Summary s = getSummary();
		String mSig = "<" + className + ": void swap()>";
		Set<AbstractMethodFlow> res = s.createMethodSummary(mSig).get(mSig);
		assertTrue(containsFieldToFieldFlow(res, NON_PRIMITIVE_VAR1,NO_ACCESS_PATH, NON_PRIMITIVE_VAR2,NO_ACCESS_PATH));
		assertTrue(containsFieldToFieldFlow(res, NON_PRIMITIVE_VAR2,NO_ACCESS_PATH, NON_PRIMITIVE_VAR1,NO_ACCESS_PATH));
	}
	
	@Test//(timeout = 100000)
	public void swap2() {
		Summary s = getSummary();
		String mSig = "<" + className + ": void swap2()>";
		Set<AbstractMethodFlow> res = s.createMethodSummary(mSig).get(mSig);
		assertTrue(containsFieldToFieldFlow(res, NON_PRIMITIVE_VAR1,DATACLASS_OBJECT_FIELD, NON_PRIMITIVE_VAR2,DATACLASS_OBJECT_FIELD));
		assertTrue(containsFieldToFieldFlow(res, NON_PRIMITIVE_VAR2,DATACLASS_INT_FIELD, NON_PRIMITIVE_VAR1,DATACLASS_INT_FIELD));
	}

	@Test(timeout = 100000)
	public void fieldToField1() {
		Summary s = getSummary();
		String mSig = "<" + className + ": void data1ToDate2()>";
		Set<AbstractMethodFlow> res = s.createMethodSummary(mSig).get(mSig);
		assertTrue(containsFieldToFieldFlow(res, NON_PRIMITIVE_VAR1,NO_ACCESS_PATH, NON_PRIMITIVE_VAR2,NO_ACCESS_PATH));
	}

	@Test(timeout = 150000)
	public void fieldToPara1() {
		Summary s = getSummary();
		String mSig = "<" + className + ": void fieldToPara(" + NON_PRIMITIVE_TYP + ")>";
		Set<AbstractMethodFlow> res = s.createMethodSummary(mSig).get(mSig);
		assertTrue(containsFieldToParaFlow(res, NON_PRIMITIVE_VAR1,DATACLASS_INT_FIELD, 0,NON_PRIMITIVE_TYP,DATACLASS_INT_FIELD));
	}

}
