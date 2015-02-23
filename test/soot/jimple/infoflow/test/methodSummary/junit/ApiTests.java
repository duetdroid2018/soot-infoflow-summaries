package soot.jimple.infoflow.test.methodSummary.junit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static soot.jimple.infoflow.methodSummary.data.SourceSinkType.Field;
import static soot.jimple.infoflow.methodSummary.data.SourceSinkType.Parameter;
import static soot.jimple.infoflow.methodSummary.data.SourceSinkType.Return;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import soot.jimple.infoflow.methodSummary.data.MethodFlow;
import soot.jimple.infoflow.methodSummary.generator.SummaryGenerator;

public class ApiTests extends ApiTestHelper {
	static final String className = "soot.jimple.infoflow.test.methodSummary.ApiClass";
	

	@Test(timeout = 100000)
	public void standardFlow1() {
		String mSig = "<" + className + ": int standardFlow(int)>";
		Set<MethodFlow> res = createSummaries(mSig);
		
		assertTrue(containsFlow(res, Parameter, 0, null, Return, null));
		assertEquals(1, res.size());
	}

	@Test(timeout = 100000)
	public void standardFlow11() {
		String mSig = "<" + className + ": int standardFlow(int)>";
		Set<MethodFlow> res = createSummaries(mSig);
		
		assertEquals(1, res.size());
	}

	@Test(timeout = 100000)
	public void standardFlow2() {
		String mSig = "<" + className + ": int standardFlow2(int,int)>";
		Set<MethodFlow> res = createSummaries(mSig);
		
		assertTrue(containsFlow(res, Parameter, 0, null, Return, null));
		assertTrue(containsFlow(res, Parameter, 1, null, Return, null));
	}

	@Test(timeout = 100000)
	public void standardFlow2Com() {
		String mSig = "<" + className + ": int standardFlow2Com(int,int)>";
		Set<MethodFlow> res = createSummaries(mSig);
		
		assertTrue(containsFlow(res, Parameter, 0, null, Return, null));
		assertTrue(containsFlow(res, Parameter, 0, null, Return, null));
	}

	@Test(timeout = 100000)
	public void standardFlow22() {
		String mSig = "<" + className + ": int standardFlow2(int,int)>";
		Set<MethodFlow> res = createSummaries(mSig);
		
		assertTrue(containsFlow(res, Parameter, 0, null, Return, null));
		assertTrue(containsFlow(res, Parameter, 0, null, Return, null));
		assertEquals(2, res.size());
	}

	@Test(timeout = 100000)
	public void standardFlow3() {
		String mSig = "<" + className + ": int standardFlow3(" + DATA_TYPE + ")>";
		Set<MethodFlow> res = createSummaries(mSig);
		
		assertTrue(containsFlow(res, Parameter, 0, new String[] {DATACLASS_INT_FIELD}, Return, null));
		assertEquals(1, res.size());
	}

	@Test(timeout = 100000)
	public void standardFlow31() {
		String mSig = "<" + className + ": int standardFlow3(" + DATA_TYPE + ")>";
		Set<MethodFlow> res = createSummaries(mSig);
		
		assertEquals(1, res.size());
	}

	@Test(timeout = 100000)
	public void standardFlow4() {
		String mSig = "<" + className + ": " + DATA_TYPE + " standardFlow4(int,java.lang.Object)>";
		Set<MethodFlow> res = createSummaries(mSig);
		
		assertTrue(containsFlow(res, Parameter, 0, new String[] {}, Return, new String[] {DATACLASS_INT_FIELD}));
		assertTrue(containsFlow(res, Parameter, 1, new String[] {}, Return, new String[] {DATACLASS_OBJECT_FIELD}));
		assertEquals(2, res.size());
	}

	@Test(timeout = 100000)
	public void standardFlow6() {
		String mSig = "<" + className + ": " + DATA_TYPE + " standardFlow6(java.lang.Object)>";
		Set<MethodFlow> res = createSummaries(mSig);
		
		assertTrue(containsFlow(res, Parameter, 0, new String[] {}, Return, new String[] {DATACLASS_OBJECT_FIELD}));
	}

	@Test(timeout = 100000)
	public void standardFlow8() {
		String mSig = "<soot.jimple.infoflow.test.methodSummary.ApiClass: soot.jimple.infoflow.test.methodSummary.Data standardFlow8(soot.jimple.infoflow.test.methodSummary.Data)>";
		Set<MethodFlow> res = createSummaries(mSig);
		
		assertTrue(containsFlow(res, Parameter, 0, new String[] {DATACLASS_OBJECT_FIELD}, Return, new String[] {DATACLASS_OBJECT_FIELD}));
		assertEquals(1 ,res.size());
	}

	@Test(timeout = 100000)
	public void staticStandardFlow1() {
		String mSig = "<" + className + ": int staticStandardFlow1(int,int)>";
		Set<MethodFlow> res = createSummaries(mSig);
		
		assertTrue(containsFlow(res, Parameter, 0, new String[] {}, Return, new String[] {}));
		assertTrue(containsFlow(res, Parameter, 1, new String[] {}, Return, new String[] {}));
		assertEquals(2, res.size());
	}

	@Test(timeout = 100000)
	public void staticStandardFlow11() {
		String mSig = "<" + className + ": int staticStandardFlow1(int,int)>";
		Set<MethodFlow> res = createSummaries(mSig);
		
		assertTrue(containsFlow(res, Parameter, 0, new String[] {}, Return, new String[] {}));
		assertTrue(containsFlow(res, Parameter, 1, new String[] {}, Return, new String[] {}));
		assertEquals(2, res.size());
	}

	@Test(timeout = 100000)
	public void staticStandardFlow2() {
		String mSig = "<" + className + ": " + DATA_TYPE + " staticStandardFlow2(int,java.lang.Object)>";
		Set<MethodFlow> res = createSummaries(mSig);
		
		assertTrue(containsFlow(res, Parameter, 0, new String[] {}, Return, new String[] {DATACLASS_INT_FIELD}));
		assertTrue(containsFlow(res, Parameter, 1, new String[] {}, Return, new String[] {DATACLASS_OBJECT_FIELD}));
		//assertTrue(containsParaToReturn(res, 0, INT_TYPE, NO_ACCESS_PATH, DATACLASS_INT_FIELD));
		//assertTrue(containsParaToReturn(res, 1, OBJECT_TYPE, NO_ACCESS_PATH, DATACLASS_OBJECT_FIELD));
		// assertTrue(res.size() == 2);
	}

	@Test(timeout = 100000)
	public void staticStandardFlow21() {
		String mSig = "<" + className + ": " + DATA_TYPE + " staticStandardFlow2(int,java.lang.Object)>";
		Set<MethodFlow> res = createSummaries(mSig);
		
		assertTrue(containsFlow(res, Parameter, 0, new String[] {}, Return, new String[] {DATACLASS_INT_FIELD}));
		assertTrue(containsFlow(res, Parameter, 1, new String[] {}, Return, new String[] {DATACLASS_OBJECT_FIELD}));
		assertEquals(2, res.size());
	}

	@Test(timeout = 100000)
	public void noFlow() {
		String mSig = "<" + className + ": int noFlow(int)>";
		Set<MethodFlow> res = createSummaries(mSig);
		
		assertTrue(res == null || res.size() == 0);
	}

	@Test(timeout = 100000)
	public void noFlow2() {
		String mSig = "<" + className + ": int noFlow2(int,int)>";
		Set<MethodFlow> res = createSummaries(mSig);
		
		assertTrue(res == null || res.size() == 0);
	}

	@Test(timeout = 100000)
	public void noFlow3() {
		String mSig = "<" + className + ": " + DATA_TYPE + " noFlow3(" + DATA_TYPE + ")>";
		Set<MethodFlow> res = createSummaries(mSig);
		
		assertTrue(res == null || res.size() == 0);
	}

	@Test(timeout = 100000)
	public void noFlow4() {
		String mSig = "<" + className + ": " + DATA_TYPE + " noFlow4(int,java.lang.Object)>";
		Set<MethodFlow> res = createSummaries(mSig);
		
		assertTrue(res == null || res.size() == 0);
	}

	@Test(timeout = 100000)
	public void paraToVar() {
		String mSig = "<" + className + ": int paraToVar(int,int)>";
		Set<MethodFlow> res = createSummaries(mSig);
		
		assertTrue(containsFlow(res, Parameter, 0, new String[] {}, Field, new String[] {PRIMITIVE_VAR}));
		assertTrue(containsFlow(res, Parameter, 1, new String[] {}, Field, new String[] {PRIMITIVE_VAR}));
	}

	@Test(timeout = 100000)
	public void paraToVar12() {
		String mSig = "<" + className + ": int paraToVar(int,int)>";
		Set<MethodFlow> res = createSummaries(mSig);
		
		assertTrue(containsFlow(res, Parameter, 0, new String[] {}, Field, new String[] {PRIMITIVE_VAR}));
		assertTrue(containsFlow(res, Parameter, 1, new String[] {}, Field, new String[] {PRIMITIVE_VAR}));
		assertEquals(2, res.size());
	}
	
	@Test(timeout = 100000)
	public void paraToVar2() {
		String mSig = "<" + className + ": " + DATA_TYPE + " paraToVar2(int,java.lang.Object)>";
		Set<MethodFlow> res = createSummaries(mSig);
		
		assertTrue(containsFlow(res, Parameter, 0, new String[] {}, Field, new String[] {NON_PRIMITIVE_VAR1,DATACLASS_INT_FIELD}));
		assertTrue(containsFlow(res, Parameter, 1, new String[] {}, Field, new String[] {NON_PRIMITIVE_VAR1,DATACLASS_OBJECT_FIELD}));
		assertEquals(4, res.size());
	}

	@Test(timeout = 100000)
	public void paraToVar21() {
		String mSig = "<" + className + ": " + DATA_TYPE + " paraToVar2(int,java.lang.Object)>";
		Set<MethodFlow> res = createSummaries(mSig);
		
		assertTrue(containsFlow(res, Parameter, 0, new String[] {}, Field, new String[] {NON_PRIMITIVE_VAR1,DATACLASS_INT_FIELD}));
		assertTrue(containsFlow(res, Parameter, 1, new String[] {}, Field, new String[] {NON_PRIMITIVE_VAR1,DATACLASS_OBJECT_FIELD}));
		assertTrue(containsFlow(res, Parameter, 0, new String[] {}, Return, new String[] {DATACLASS_INT_FIELD}));
		assertTrue(containsFlow(res, Parameter, 1, new String[] {}, Return, new String[] {DATACLASS_OBJECT_FIELD}));
		assertEquals(4, res.size()); 
	}

	@Test(timeout = 100000)
	public void paraToparaFlow1WrongSinkSigAccepted() {
		String mSig = "<" + className + ": void paraToparaFlow1(int," + DATA_TYPE + ")>";
		Set<MethodFlow> res = createSummaries(mSig);
		
		assertTrue(containsFlow(res, Parameter, 0, new String[] {}, Parameter, 1,new String[] {DATACLASS_INT_FIELD}));
		assertEquals(1, res.size());
	}

	@Test(timeout = 100000)
	public void paraToparaFlow2() {
		String mSig = "<" + className + ": void paraToparaFlow2(int,java.lang.Object," + DATA_TYPE + ")>";
		Set<MethodFlow> res = createSummaries(mSig);
		
		assertTrue(containsFlow(res, Parameter, 0, new String[] {}, Parameter,2 ,new String[] {DATACLASS_INT_FIELD}));
		assertTrue(containsFlow(res, Parameter, 1, new String[] {}, Parameter,2, new String[] {DATACLASS_OBJECT_FIELD}));
		assertEquals(2, res.size());

	}

	@Test(timeout = 100000)
	public void paraToparaFlow3WrongSinkSigAccepted() {
		String mSig = "<" + className + ": void paraToparaFlow3(int,java.lang.Object," + DATA_TYPE + ","
				+ DATA_TYPE + ")>";
		Set<MethodFlow> res = createSummaries(mSig);
		
		assertTrue(containsFlow(res, Parameter, 0, new String[] {}, Parameter,2, new String[] {DATACLASS_INT_FIELD}));
		assertTrue(containsFlow(res, Parameter, 1, new String[] {}, Parameter,2, new String[] {DATACLASS_OBJECT_FIELD}));
		assertTrue(containsFlow(res, Parameter, 1, new String[] {}, Parameter,3, new String[] {DATACLASS_OBJECT_FIELD}));
	}

	@Test(timeout = 100000)
	public void staticParaToParaFlow1WrongSinkSigAccepted() {
		String mSig = "<" + className + ": void staticParaToparaFlow1(int," + DATA_TYPE + ")>";
		Set<MethodFlow> res = createSummaries(mSig);
		
		assertTrue(containsFlow(res, Parameter, 0, new String[] {}, Parameter,1, new String[] {DATACLASS_INT_FIELD}));
		assertEquals(1, res.size());
	}

	@Test(timeout = 100000)
	public void staticParaToParaFlow2WrongSinkSigAccepted() {
		String mSig = "<" + className + ": void staticParaToparaFlow2(int,java.lang.Object," + DATA_TYPE + ")>";
		Set<MethodFlow> res = createSummaries(mSig);
		
		assertTrue(containsFlow(res, Parameter, 0, new String[] {}, Parameter,2, new String[] {DATACLASS_INT_FIELD}));
		assertTrue(containsFlow(res, Parameter, 1, new String[] {}, Parameter,2, new String[] {DATACLASS_OBJECT_FIELD}));
		assertEquals(2, res.size());
	}

	@Test(timeout = 100000)
	public void staticParaToParaFlow3WrongSinkSigAccepted() {
		String mSig = "<" + className + ": void staticParaToparaFlow3(int,java.lang.Object," + DATA_TYPE + ","
				+ DATA_TYPE + ")>";
		Set<MethodFlow> res = createSummaries(mSig);
		
		assertTrue(containsFlow(res, Parameter, 0, new String[] {}, Parameter,2, new String[] {DATACLASS_INT_FIELD}));
		assertTrue(containsFlow(res, Parameter, 1, new String[] {}, Parameter,2, new String[] {DATACLASS_OBJECT_FIELD}));
	}

	@Test(timeout = 100000)
	public void mixedFlow1OneFalseFlow() {
		String mSig = "<" + className + ": " + DATA_TYPE + " mixedFlow1(int," + DATA_TYPE + ")>";
		Set<MethodFlow> res = createSummaries(mSig);
		
		assertTrue(containsFlow(res, Parameter, 1, new String[] {}, Return, new String[] {}));
		assertTrue(containsFlow(res, Parameter, 0, new String[] {}, Parameter,1, new String[] {DATACLASS_INT_FIELD}));
		assertTrue(containsFlow(res, Parameter, 1, new String[] {DATACLASS_INT_FIELD}, Field, new String[] {PRIMITIVE_VAR}));
		assertTrue(containsFlow(res, Parameter,0,new String[] {},Return ,  new String[] {DATACLASS_INT_FIELD}));
	}
	
	@Test(timeout = 100000)
	public void mixedFlow1() {
		String mSig = "<" + className + ": " + DATA_TYPE + " mixedFlow1(int," + DATA_TYPE + ")>";
		Set<MethodFlow> res = createSummaries(mSig);
		
		assertTrue(containsFlow(res, Parameter, 1, new String[] {}, Return, new String[] {}));
		assertTrue(containsFlow(res, Parameter, 0, new String[] {}, Return, new String[] {DATACLASS_INT_FIELD}));
		assertTrue(containsFlow(res, Parameter, 0, new String[] {}, Parameter,1, new String[] {DATACLASS_INT_FIELD}));
		assertTrue(containsFlow(res, Parameter, 1, new String[] {DATACLASS_INT_FIELD}, Field, new String[] {PRIMITIVE_VAR}));
		assertEquals(5, res.size());
	}

	@Test(timeout = 100000)
	public void paraToparaFlow1() {
		String mSig = "<" + className + ": void paraToparaFlow1(int," + DATA_TYPE + ")>";
		Set<MethodFlow> res = createSummaries(mSig);
		
		assertTrue(containsFlow(res, Parameter, 0, new String[] {}, Parameter,1, new String[] {DATACLASS_INT_FIELD}));
		assertEquals(1, res.size());
	}

	@Test(timeout = 100000)
	public void paraToparaFlow3() {
		String mSig = "<" + className + ": void paraToparaFlow3(int,java.lang.Object," + DATA_TYPE + ","
				+ DATA_TYPE + ")>";
		Set<MethodFlow> res = createSummaries(mSig);
		
		assertTrue(containsFlow(res, Parameter, 0, new String[] {}, Parameter,2, new String[] {DATACLASS_INT_FIELD}));
		assertTrue(containsFlow(res, Parameter, 1, new String[] {}, Parameter,2, new String[] {DATACLASS_OBJECT_FIELD}));
		assertTrue(containsFlow(res, Parameter, 1, new String[] {}, Parameter,3, new String[] {DATACLASS_OBJECT_FIELD}));
		assertEquals(3,res.size());
	}

	@Test(timeout = 100000)
	public void staticParaToParaFlow1() {
		String mSig = "<" + className + ": void staticParaToparaFlow1(int," + DATA_TYPE + ")>";
		Set<MethodFlow> res = createSummaries(mSig);
		
		assertTrue(containsFlow(res, Parameter, 0, new String[] {}, Parameter,1, new String[] {DATACLASS_INT_FIELD}));
		assertEquals(1, res.size());
	}

	@Test(timeout = 100000)
	public void staticParaToParaFlow2() {
		String mSig = "<" + className + ": void staticParaToparaFlow2(int,java.lang.Object," + DATA_TYPE + ")>";
		Set<MethodFlow> res = createSummaries(mSig);

		assertTrue(containsFlow(res, Parameter, 0, new String[] {}, Parameter,2, new String[] {DATACLASS_INT_FIELD}));
		assertTrue(containsFlow(res, Parameter, 1, new String[] {}, Parameter,2, new String[] {DATACLASS_OBJECT_FIELD}));
		assertEquals(2, res.size());
	}

	@Test(timeout = 100000)
	public void staticParaToParaFlow3() {
		String mSig = "<" + className + ": void staticParaToparaFlow3(int,java.lang.Object," + DATA_TYPE + ","
				+ DATA_TYPE + ")>";
		Set<MethodFlow> res = createSummaries(mSig);

		assertTrue(containsFlow(res, Parameter, 0, new String[] {}, Parameter,2, new String[] {DATACLASS_INT_FIELD}));
		assertTrue(containsFlow(res, Parameter, 1, new String[] {}, Parameter,2, new String[] {DATACLASS_OBJECT_FIELD}));
		assertTrue(containsFlow(res, Parameter, 1, new String[] {}, Parameter,3, new String[] {DATACLASS_OBJECT_FIELD}));
		assertEquals(3, res.size());
	}

	@Test(timeout = 100000)
	public void primitivVarToReturn1() {
		String mSig = "<" + className + ": int intParaToReturn()>";
		Set<MethodFlow> res = createSummaries(mSig);
		
		assertTrue(containsFlow(res, Field,new String[] {PRIMITIVE_VAR}, Return,new String[] {}));
		//assertTrue(containsFieldToReturn(res, PRIMITIVE_VAR,NO_ACCESS_PATH,NO_ACCESS_PATH));
	}

	@Test(timeout = 100000)
	public void nonPrimitivVarToReturn1() {
		String mSig = "<" + className + ": int intInDataToReturn()>";
		Set<MethodFlow> res = createSummaries(mSig);
		
		assertTrue(containsFlow(res, Field,new String[] {NON_PRIMITIVE_VAR1,DATACLASS_INT_FIELD}, Return,new String[] {}));
	}

	@Test(timeout = 100000)
	public void nonPrimitivVarToReturn11() {
		String mSig = "<" + className + ": int intInDataToReturn()>";
		Set<MethodFlow> res = createSummaries(mSig);
		
		assertTrue(containsFlow(res, Field,new String[] {NON_PRIMITIVE_VAR1,DATACLASS_INT_FIELD}, Return,new String[] {}));
		assertEquals(1, res.size());
	}

	@Test(timeout = 100000)
	public void nonPrimitivVarToReturn2() {
		String mSig = "<" + className + ": int intInDataToReturn2()>";
		Set<MethodFlow> res = createSummaries(mSig);
		
		assertTrue(containsFlow(res, Field,new String[] {NON_PRIMITIVE_VAR1,DATACLASS_INT_FIELD}, Return,new String[] {}));
	}

	@Test(timeout = 100000)
	public void nonPrimitivVarToReturn3() {
		String mSig = "<" + className + ": int intInDataToReturn3()>";
		Set<MethodFlow> res = createSummaries(mSig);
		
		assertTrue(containsFlow(res, Field,new String[] {NON_PRIMITIVE_VAR1,DATACLASS_INT_FIELD}, Return,new String[] {}));
	}

	@Test(timeout = 100000)
	public void nonPrimitivVarToReturn4() {
		String mSig = "<" + className + ": " + DATA_TYPE + " dataFieldToReturn()>";
		Set<MethodFlow> res = createSummaries(mSig);
		
		assertTrue(containsFlow(res, Field,new String[] {NON_PRIMITIVE_VAR1}, Return,new String[] {}));
	}

	@Test(timeout = 200000)
	public void nonPrimitivVarToReturn5() {
		String mSig = "<" + className + ": " + DATA_TYPE + " dataFieldToReturn2()>";
		Set<MethodFlow> res = createSummaries(mSig);
		
		assertTrue(containsFlow(res, Field,new String[] {NON_PRIMITIVE_VAR1}, Return,new String[] {}));
	}

	@Test(timeout = 100000)
	public void swap() {
		String mSig = "<" + className + ": void swap()>";
		Set<MethodFlow> res = createSummaries(mSig);
		
		assertTrue(containsFlow(res, Field,new String[] {NON_PRIMITIVE_VAR1}, Field,new String[] {NON_PRIMITIVE_VAR2}));
		assertTrue(containsFlow(res, Field,new String[] {NON_PRIMITIVE_VAR1}, Field,new String[] {NON_PRIMITIVE_VAR2}));
	}
	
	@Test(timeout = 100000)
	public void swap2() {
		String mSig = "<" + className + ": void swap2()>";
		Set<MethodFlow> res = createSummaries(mSig);
		
		assertTrue(containsFlow(res, Field,new String[] {NON_PRIMITIVE_VAR1,DATACLASS_OBJECT_FIELD}, Field,new String[] {NON_PRIMITIVE_VAR2,DATACLASS_OBJECT_FIELD}));
		assertTrue(containsFlow(res, Field,new String[] {NON_PRIMITIVE_VAR2,DATACLASS_INT_FIELD}, Field,new String[] {NON_PRIMITIVE_VAR1,DATACLASS_INT_FIELD}));
	}

	@Test(timeout = 100000)
	public void data1ToDate2() {
		String mSig = "<" + className + ": void data1ToDate2()>";
		Set<MethodFlow> res = createSummaries(mSig);
		
		assertTrue(containsFlow(res, Field,new String[] {NON_PRIMITIVE_VAR1}, Field,new String[] {NON_PRIMITIVE_VAR2}));
	}

	@Test(timeout = 150000)
	public void fieldToPara1() {
		String mSig = "<" + className + ": void fieldToPara(" + DATA_TYPE + ")>";
		Set<MethodFlow> res = createSummaries(mSig);
		
		assertTrue(containsFlow(res, Field,new String[] {NON_PRIMITIVE_VAR1,DATACLASS_INT_FIELD}, Parameter,0,new String[] {DATACLASS_INT_FIELD}));
	}
	
	@Test(timeout = 150000)
	public void ListGetTest() {
		String mSig = "<" + className + ": java.lang.Object get()>";
		Set<MethodFlow> res = createSummaries(mSig);
		
		assertTrue(containsFlow(res, Field,new String[] {"<soot.jimple.infoflow.test.methodSummary.ApiClass: soot.jimple.infoflow.test.methodSummary.ApiClass$Node first>",
				"<soot.jimple.infoflow.test.methodSummary.ApiClass$Node: java.lang.Object item>"}, Return,new String[] {}));
	}

	@Test(timeout = 100000)
	public void fieldToField1() {
		String mSig = "<" + className + ": void fieldToField1()>";
		Set<MethodFlow> res = createSummaries(mSig);
		
		assertTrue(containsFlow(res, Field,new String[] {NON_PRIMITIVE_VAR1}, Field,new String[] {NON_PRIMITIVE_VAR2}));
	}
	
	@Test//(timeout = 100000)
	public void fieldToField2() {
		String mSig = "<" + className + ": void fieldToField2()>";
		Set<MethodFlow> res = createSummaries(mSig);
		
		assertTrue(containsFlow(res, Field,new String[] {NON_PRIMITIVE_VAR1, DATACLASS_OBJECT_FIELD}, Field,new String[] {OBJECT_FIELD}));
	}

	
	@Test(timeout = 100000)
	public void fieldToField3() {
		String mSig = "<" + className + ": void fieldToField3()>";
		Set<MethodFlow> res = createSummaries(mSig);
		
		assertTrue(containsFlow(res, Field,new String[] {NON_PRIMITIVE_VAR2, DATACLASS_INT_FIELD}, Field,new String[] {PRIMITIVE_VAR}));
	}

	@Test(timeout = 100000)
	public void fieldToField4() {
		String mSig = "<" + className + ": void fieldToField4()>";
		Set<MethodFlow> res = createSummaries(mSig);
		
		assertTrue(containsFlow(res, Field,new String[] {OBJECT_FIELD}, Field,new String[] {NON_PRIMITIVE_VAR2, DATACLASS_OBJECT_FIELD}));
	}

	@Test(timeout = 100000)
	public void fieldToField5() {
		String mSig = "<" + className + ": void fieldToField5()>";
		Set<MethodFlow> res = createSummaries(mSig);
		
		assertTrue(containsFlow(res, Field,new String[] {NON_PRIMITIVE_VAR1,DATACLASS_OBJECT_FIELD}, Field,new String[] {NON_PRIMITIVE_VAR2, DATACLASS_OBJECT_FIELD}));
	}
	
	@Override
	protected SummaryGenerator getSummary() {
		SummaryGenerator sg = new SummaryGenerator();
		List<String> sub = new LinkedList<String>();
		sub.add("java.util.ArrayList");
		sg.setSubstitutedWith(sub);
		sg.setUseRecursiveAccessPaths(false);
		sg.setAnalyseMethodsTogether(true);
		sg.setAccessPathLength(5);
		sg.setIgnoreFlowsInSystemPackages(false);
		return sg;
	}


}
