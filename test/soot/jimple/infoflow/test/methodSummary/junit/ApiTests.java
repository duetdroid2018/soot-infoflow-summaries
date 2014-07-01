package soot.jimple.infoflow.test.methodSummary.junit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static soot.jimple.infoflow.methodSummary.data.SourceSinkType.Field;
import static soot.jimple.infoflow.methodSummary.data.SourceSinkType.Parameter;
import static soot.jimple.infoflow.methodSummary.data.SourceSinkType.Return;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.junit.Ignore;
import org.junit.Test;

import soot.jimple.infoflow.methodSummary.data.MethodFlow;
import soot.jimple.infoflow.methodSummary.generator.SummaryGenerator;
import soot.jimple.infoflow.test.methodSummary.ApiClass;

public class ApiTests extends ApiTestHelper {
	static final String className = "soot.jimple.infoflow.test.methodSummary.ApiClass";
	

	@Test(timeout = 100000)
	public void standardFlow1() {
		SummaryGenerator s = getSummary();
		String mSig = "<" + className + ": int standardFlow(int)>";
		Set<MethodFlow> res = s.createMethodSummary(mSig,methods()).getFlowsForMethod(mSig);
		assertTrue(res.size() == 1);
		assertTrue(containsFlow(res, Parameter, 0, null, Return, null));
	}

	@Test(timeout = 100000)
	public void standardFlow11() {
		SummaryGenerator s = getSummary();
		String mSig = "<" + className + ": int standardFlow(int)>";
		Set<MethodFlow> res = s.createMethodSummary(mSig).getFlowsForMethod(mSig);
		assertTrue(res.size() == 1);
	}

	@Test(timeout = 100000)
	public void standardFlow2() {
		SummaryGenerator s = getSummary();
		String mSig = "<" + className + ": int standardFlow2(int,int)>";
		Set<MethodFlow> res = s.createMethodSummary(mSig).getFlowsForMethod(mSig);
		assertTrue(containsFlow(res, Parameter, 0, null, Return, null));
		assertTrue(containsFlow(res, Parameter, 1, null, Return, null));
	}

	@Test(timeout = 100000)
	public void standardFlow2Com() {
		SummaryGenerator s = getSummary();
		String mSig = "<" + className + ": int standardFlow2Com(int,int)>";
		Set<MethodFlow> res = s.createMethodSummary(mSig).getFlowsForMethod(mSig);
		assertTrue(containsFlow(res, Parameter, 0, null, Return, null));
		assertTrue(containsFlow(res, Parameter, 0, null, Return, null));
	}

	@Test(timeout = 100000)
	public void standardFlow22() {
		SummaryGenerator s = getSummary();
		String mSig = "<" + className + ": int standardFlow2(int,int)>";
		Set<MethodFlow> res = s.createMethodSummary(mSig).getFlowsForMethod(mSig);
		assertTrue(res.size() == 2);
		assertTrue(containsFlow(res, Parameter, 0, null, Return, null));
		assertTrue(containsFlow(res, Parameter, 0, null, Return, null));
	}

	@Test(timeout = 100000)
	public void standardFlow3() {
		SummaryGenerator s = getSummary();
		String mSig = "<" + className + ": int standardFlow3(" + DATA_TYPE + ")>";
		Set<MethodFlow> res = s.createMethodSummary(mSig).getFlowsForMethod(mSig);
		assertTrue(containsFlow(res, Parameter, 0, new String[] {DATACLASS_INT_FIELD}, Return, null));
		 assertTrue(res.size() == 1);
	}

	@Test(timeout = 100000)
	public void standardFlow31() {
		SummaryGenerator s = getSummary();
		String mSig = "<" + className + ": int standardFlow3(" + DATA_TYPE + ")>";
		Set<MethodFlow> res = s.createMethodSummary(mSig).getFlowsForMethod(mSig);
		assertEquals(1, res.size());
	}

	@Test(timeout = 100000)
	public void standardFlow4() {
		SummaryGenerator s = getSummary();
		String mSig = "<" + className + ": " + DATA_TYPE + " standardFlow4(int,java.lang.Object)>";
		Set<MethodFlow> res = s.createMethodSummary(mSig).getFlowsForMethod(mSig);
		assertTrue(containsFlow(res, Parameter, 0, new String[] {}, Return, new String[] {DATACLASS_INT_FIELD}));
		assertTrue(containsFlow(res, Parameter, 1, new String[] {}, Return, new String[] {DATACLASS_OBJECT_FIELD}));
		 assertTrue(res.size() == 2);
	}

	@Test(timeout = 100000)
	public void standardFlow6() {
		SummaryGenerator s = getSummary();
		String mSig = "<" + className + ": " + DATA_TYPE + " standardFlow6(java.lang.Object)>";
		Set<MethodFlow> res = s.createMethodSummary(mSig).getFlowsForMethod(mSig);
		assertTrue(containsFlow(res, Parameter, 0, new String[] {}, Return, new String[] {DATACLASS_OBJECT_FIELD}));
	}

	@Test(timeout = 100000)
	public void standardFlow8() {
		SummaryGenerator s = getSummary();
		String mSig = "<soot.jimple.infoflow.test.methodSummary.ApiClass: soot.jimple.infoflow.test.methodSummary.Data standardFlow8(soot.jimple.infoflow.test.methodSummary.Data)>";
		Set<MethodFlow> res = s.createMethodSummary(mSig).getFlowsForMethod(mSig);
		assertTrue(containsFlow(res, Parameter, 0, new String[] {DATACLASS_OBJECT_FIELD}, Return, new String[] {DATACLASS_OBJECT_FIELD}));
		assertEquals(1,res.size());
	}

	@Test(timeout = 100000)
	public void staticStandardFlow1() {
		SummaryGenerator s = getSummary();
		String mSig = "<" + className + ": int staticStandardFlow1(int,int)>";
		Set<MethodFlow> res = s.createMethodSummary(mSig).getFlowsForMethod(mSig);
		assertTrue(containsFlow(res, Parameter, 0, new String[] {}, Return, new String[] {}));
		assertTrue(containsFlow(res, Parameter, 1, new String[] {}, Return, new String[] {}));
		assertTrue(res.size() == 2);
	}

	@Test(timeout = 100000)
	public void staticStandardFlow11() {
		SummaryGenerator s = getSummary();
		String mSig = "<" + className + ": int staticStandardFlow1(int,int)>";
		Set<MethodFlow> res = s.createMethodSummary(mSig).getFlowsForMethod(mSig);
		assertTrue(containsFlow(res, Parameter, 0, new String[] {}, Return, new String[] {}));
		assertTrue(containsFlow(res, Parameter, 1, new String[] {}, Return, new String[] {}));
		assertTrue(res.size() == 2);
	}

	@Test(timeout = 100000)
	public void staticStandardFlow2() {
		SummaryGenerator s = getSummary();
		String mSig = "<" + className + ": " + DATA_TYPE + " staticStandardFlow2(int,java.lang.Object)>";
		Set<MethodFlow> res = s.createMethodSummary(mSig).getFlowsForMethod(mSig);
		assertTrue(containsFlow(res, Parameter, 0, new String[] {}, Return, new String[] {DATACLASS_INT_FIELD}));
		assertTrue(containsFlow(res, Parameter, 1, new String[] {}, Return, new String[] {DATACLASS_OBJECT_FIELD}));
		//assertTrue(containsParaToReturn(res, 0, INT_TYPE, NO_ACCESS_PATH, DATACLASS_INT_FIELD));
		//assertTrue(containsParaToReturn(res, 1, OBJECT_TYPE, NO_ACCESS_PATH, DATACLASS_OBJECT_FIELD));
		// assertTrue(res.size() == 2);
	}

	@Test(timeout = 100000)
	public void staticStandardFlow21() {
		SummaryGenerator s = getSummary();
		String mSig = "<" + className + ": " + DATA_TYPE + " staticStandardFlow2(int,java.lang.Object)>";
		Set<MethodFlow> res = s.createMethodSummary(mSig).getFlowsForMethod(mSig);
		
		assertTrue(containsFlow(res, Parameter, 0, new String[] {}, Return, new String[] {DATACLASS_INT_FIELD}));
		assertTrue(containsFlow(res, Parameter, 1, new String[] {}, Return, new String[] {DATACLASS_OBJECT_FIELD}));
		assertTrue(res.size() == 2);
	}

	@Test(timeout = 100000)
	public void noFlow() {
		SummaryGenerator s = getSummary();
		String mSig = "<" + className + ": int noFlow(int)>";
		Set<MethodFlow> res = s.createMethodSummary(mSig).getFlowsForMethod(mSig);
		assertTrue(res == null || res.size() == 0);
	}

	@Test(timeout = 100000)
	public void noFlow2() {
		SummaryGenerator s = getSummary();
		String mSig = "<" + className + ": int noFlow2(int,int)>";
		Set<MethodFlow> res = s.createMethodSummary(mSig).getFlowsForMethod(mSig);
		assertTrue(res == null || res.size() == 0);
	}

	@Test(timeout = 100000)
	public void noFlow3() {
		SummaryGenerator s = getSummary();
		String mSig = "<" + className + ": " + DATA_TYPE + " noFlow3(" + DATA_TYPE + ")>";
		Set<MethodFlow> res = s.createMethodSummary(mSig).getFlowsForMethod(mSig);
		assertTrue(res == null || res.size() == 0);
	}

	@Test(timeout = 100000)
	public void noFlow4() {
		SummaryGenerator s = getSummary();
		String mSig = "<" + className + ": " + DATA_TYPE + " noFlow4(int,java.lang.Object)>";
		Set<MethodFlow> res = s.createMethodSummary(mSig).getFlowsForMethod(mSig);
		assertTrue(res == null || res.size() == 0);
	}

	@Test(timeout = 100000)
	public void paraToVar() {
		SummaryGenerator s = getSummary();
		String mSig = "<" + className + ": int paraToVar(int,int)>";
		Set<MethodFlow> res = s.createMethodSummary(mSig).getFlowsForMethod(mSig);
		assertTrue(containsFlow(res, Parameter, 0, new String[] {}, Field, new String[] {PRIMITIVE_VAR}));
		assertTrue(containsFlow(res, Parameter, 1, new String[] {}, Field, new String[] {PRIMITIVE_VAR}));
	}

	@Test(timeout = 100000)
	public void paraToVar12() {
		SummaryGenerator s = getSummary();
		String mSig = "<" + className + ": int paraToVar(int,int)>";
		Set<MethodFlow> res = s.createMethodSummary(mSig).getFlowsForMethod(mSig);
		assertTrue(containsFlow(res, Parameter, 0, new String[] {}, Field, new String[] {PRIMITIVE_VAR}));
		assertTrue(containsFlow(res, Parameter, 1, new String[] {}, Field, new String[] {PRIMITIVE_VAR}));
		assertTrue(res.size() == 2);
	}

	@Test(timeout = 100000)
	public void paraToVar2() {
		SummaryGenerator s = getSummary();
		String mSig = "<" + className + ": " + DATA_TYPE + " paraToVar2(int,java.lang.Object)>";
		Set<MethodFlow> res = s.createMethodSummary(mSig).getFlowsForMethod(mSig);
		assertTrue(containsFlow(res, Parameter, 0, new String[] {}, Field, new String[] {NON_PRIMITIVE_VAR1,DATACLASS_INT_FIELD}));
		assertTrue(containsFlow(res, Parameter, 1, new String[] {}, Field, new String[] {NON_PRIMITIVE_VAR1,DATACLASS_OBJECT_FIELD}));
		assertTrue(res.size() == 4);
	}

	@Test(timeout = 100000)
	public void paraToVar21() {
		SummaryGenerator s = getSummary();
		String mSig = "<" + className + ": " + DATA_TYPE + " paraToVar2(int,java.lang.Object)>";
		Set<MethodFlow> res = s.createMethodSummary(mSig).getFlowsForMethod(mSig);
		
		assertTrue(containsFlow(res, Parameter, 0, new String[] {}, Field, new String[] {NON_PRIMITIVE_VAR1,DATACLASS_INT_FIELD}));
		assertTrue(containsFlow(res, Parameter, 1, new String[] {}, Field, new String[] {NON_PRIMITIVE_VAR1,DATACLASS_OBJECT_FIELD}));
		assertTrue(containsFlow(res, Parameter, 0, new String[] {}, Return, new String[] {DATACLASS_INT_FIELD}));
		assertTrue(containsFlow(res, Parameter, 1, new String[] {}, Return, new String[] {DATACLASS_OBJECT_FIELD}));
		assertTrue(res.size() == 4); 
	}

	@Test(timeout = 100000)
	public void paraToparaFlow1WrongSinkSigAccepted() {
		SummaryGenerator s = getSummary();
		String mSig = "<" + className + ": void paraToparaFlow1(int," + DATA_TYPE + ")>";
		Set<MethodFlow> res = s.createMethodSummary(mSig).getFlowsForMethod(mSig);
		assertTrue(containsFlow(res, Parameter, 0, new String[] {}, Parameter, 1,new String[] {DATACLASS_INT_FIELD}));
		assertTrue(res.size() == 1);
	}

	@Test(timeout = 100000)
	public void paraToparaFlow2() {
		SummaryGenerator s = getSummary();
		String mSig = "<" + className + ": void paraToparaFlow2(int,java.lang.Object," + DATA_TYPE + ")>";
		Set<MethodFlow> res = s.createMethodSummary(mSig).getFlowsForMethod(mSig);
		
		assertTrue(containsFlow(res, Parameter, 0, new String[] {}, Parameter,2 ,new String[] {DATACLASS_INT_FIELD}));
		assertTrue(containsFlow(res, Parameter, 1, new String[] {}, Parameter,2, new String[] {DATACLASS_OBJECT_FIELD}));
		assertTrue(res.size() == 2);

	}

	@Test(timeout = 100000)
	public void paraToparaFlow3WrongSinkSigAccepted() {
		SummaryGenerator s = getSummary();
		String mSig = "<" + className + ": void paraToparaFlow3(int,java.lang.Object," + DATA_TYPE + ","
				+ DATA_TYPE + ")>";
		Set<MethodFlow> res = s.createMethodSummary(mSig).getFlowsForMethod(mSig);
		
		assertTrue(containsFlow(res, Parameter, 0, new String[] {}, Parameter,2, new String[] {DATACLASS_INT_FIELD}));
		assertTrue(containsFlow(res, Parameter, 1, new String[] {}, Parameter,2, new String[] {DATACLASS_OBJECT_FIELD}));
		assertTrue(containsFlow(res, Parameter, 1, new String[] {}, Parameter,3, new String[] {DATACLASS_OBJECT_FIELD}));
	}

	@Test(timeout = 100000)
	public void staticParaToParaFlow1WrongSinkSigAccepted() {
		SummaryGenerator s = getSummary();
		String mSig = "<" + className + ": void staticParaToparaFlow1(int," + DATA_TYPE + ")>";
		Set<MethodFlow> res = s.createMethodSummary(mSig).getFlowsForMethod(mSig);
		assertTrue(containsFlow(res, Parameter, 0, new String[] {}, Parameter,1, new String[] {DATACLASS_INT_FIELD}));
		assertTrue(res.size() == 1);
	}

	@Test(timeout = 100000)
	public void staticParaToParaFlow2WrongSinkSigAccepted() {
		SummaryGenerator s = getSummary();
		String mSig = "<" + className + ": void staticParaToparaFlow2(int,java.lang.Object," + DATA_TYPE + ")>";
		Set<MethodFlow> res = s.createMethodSummary(mSig).getFlowsForMethod(mSig);
		assertTrue(containsFlow(res, Parameter, 0, new String[] {}, Parameter,2, new String[] {DATACLASS_INT_FIELD}));
		assertTrue(containsFlow(res, Parameter, 1, new String[] {}, Parameter,2, new String[] {DATACLASS_OBJECT_FIELD}));
		assertTrue(res.size() == 2);
	}

	@Test(timeout = 100000)
	public void staticParaToParaFlow3WrongSinkSigAccepted() {
		SummaryGenerator s = getSummary();
		String mSig = "<" + className + ": void staticParaToparaFlow3(int,java.lang.Object," + DATA_TYPE + ","
				+ DATA_TYPE + ")>";
		Set<MethodFlow> res = s.createMethodSummary(mSig).getFlowsForMethod(mSig);
		assertTrue(containsFlow(res, Parameter, 0, new String[] {}, Parameter,2, new String[] {DATACLASS_INT_FIELD}));
		assertTrue(containsFlow(res, Parameter, 1, new String[] {}, Parameter,2, new String[] {DATACLASS_OBJECT_FIELD}));
		
//		assertTrue(containsParaToParaFlow(res, 0, INT_TYPE,NO_ACCESS_PATH, 2, DATA_TYPE,DATACLASS_INT_FIELD));
//		assertTrue(containsParaToParaFlow(res, 1, OBJECT_TYPE,NO_ACCESS_PATH, 2, DATA_TYPE,DATACLASS_OBJECT_FIELD));
	}

	@Test(timeout = 100000)
	public void mixedFlow1OneFalseFlow() {
		SummaryGenerator s = getSummary();
		String mSig = "<" + className + ": " + DATA_TYPE + " mixedFlow1(int," + DATA_TYPE + ")>";
		Set<MethodFlow> res = s.createMethodSummary(mSig).getFlowsForMethod(mSig);
		assertTrue(containsFlow(res, Parameter, 1, new String[] {}, Return, new String[] {}));
		assertTrue(containsFlow(res, Parameter, 0, new String[] {}, Parameter,1, new String[] {DATACLASS_INT_FIELD}));
		assertTrue(containsFlow(res, Parameter, 1, new String[] {DATACLASS_INT_FIELD}, Field, new String[] {PRIMITIVE_VAR}));
		assertTrue(containsFlow(res, Parameter,0,new String[] {},Return ,  new String[] {DATACLASS_INT_FIELD}));
	}
	@Ignore
	@Test(timeout = 100000)
	public void mixedFlow1() {
		SummaryGenerator s = getSummary();
		String mSig = "<" + className + ": " + DATA_TYPE + " mixedFlow1(int," + DATA_TYPE + ")>";
		Set<MethodFlow> res = s.createMethodSummary(mSig).getFlowsForMethod(mSig);
		assertTrue(containsFlow(res, Parameter, 1, new String[] {}, Return, new String[] {}));
		assertTrue(containsFlow(res, Parameter, 0, new String[] {}, Parameter,1, new String[] {DATACLASS_INT_FIELD}));
		assertTrue(containsFlow(res, Parameter, 1, new String[] {DATACLASS_INT_FIELD}, Field, new String[] {PRIMITIVE_VAR}));
		assertEquals(4,res.size() );
	}

	@Test(timeout = 100000)
	public void paraToparaFlow1() {
		SummaryGenerator s = getSummary();
		String mSig = "<" + className + ": void paraToparaFlow1(int," + DATA_TYPE + ")>";
		Set<MethodFlow> res = s.createMethodSummary(mSig).getFlowsForMethod(mSig);
		assertTrue(containsFlow(res, Parameter, 0, new String[] {}, Parameter,1, new String[] {DATACLASS_INT_FIELD}));
		assertTrue(res.size() == 1);
	}

	@Test(timeout = 100000)
	public void paraToparaFlow3() {
		SummaryGenerator s = getSummary();
		String mSig = "<" + className + ": void paraToparaFlow3(int,java.lang.Object," + DATA_TYPE + ","
				+ DATA_TYPE + ")>";
		Set<MethodFlow> res = s.createMethodSummary(mSig).getFlowsForMethod(mSig);
		assertTrue(containsFlow(res, Parameter, 0, new String[] {}, Parameter,2, new String[] {DATACLASS_INT_FIELD}));
		assertTrue(containsFlow(res, Parameter, 1, new String[] {}, Parameter,2, new String[] {DATACLASS_OBJECT_FIELD}));
		assertTrue(containsFlow(res, Parameter, 1, new String[] {}, Parameter,3, new String[] {DATACLASS_OBJECT_FIELD}));
		assertEquals(3,res.size() );
	}

	@Test(timeout = 100000)
	public void staticParaToParaFlow1() {
		SummaryGenerator s = getSummary();
		String mSig = "<" + className + ": void staticParaToparaFlow1(int," + DATA_TYPE + ")>";
		Set<MethodFlow> res = s.createMethodSummary(mSig).getFlowsForMethod(mSig);
		assertTrue(containsFlow(res, Parameter, 0, new String[] {}, Parameter,1, new String[] {DATACLASS_INT_FIELD}));
		assertTrue(res.size() == 1);
	}

	@Test(timeout = 100000)
	public void staticParaToParaFlow2() {
		SummaryGenerator s = getSummary();
		String mSig = "<" + className + ": void staticParaToparaFlow2(int,java.lang.Object," + DATA_TYPE + ")>";
		Set<MethodFlow> res = s.createMethodSummary(mSig).getFlowsForMethod(mSig);

		assertTrue(containsFlow(res, Parameter, 0, new String[] {}, Parameter,2, new String[] {DATACLASS_INT_FIELD}));
		assertTrue(containsFlow(res, Parameter, 1, new String[] {}, Parameter,2, new String[] {DATACLASS_OBJECT_FIELD}));
		assertTrue(res.size() == 2);
	}

	@Test(timeout = 100000)
	public void staticParaToParaFlow3() {
		SummaryGenerator s = getSummary();
		String mSig = "<" + className + ": void staticParaToparaFlow3(int,java.lang.Object," + DATA_TYPE + ","
				+ DATA_TYPE + ")>";
		Set<MethodFlow> res = s.createMethodSummary(mSig).getFlowsForMethod(mSig);

		assertTrue(containsFlow(res, Parameter, 0, new String[] {}, Parameter,2, new String[] {DATACLASS_INT_FIELD}));
		assertTrue(containsFlow(res, Parameter, 1, new String[] {}, Parameter,2, new String[] {DATACLASS_OBJECT_FIELD}));
		assertTrue(containsFlow(res, Parameter, 1, new String[] {}, Parameter,3, new String[] {DATACLASS_OBJECT_FIELD}));
		assertEquals(3,res.size() );
	}

	@Test(timeout = 100000)
	public void primitivVarToReturn1() {
		SummaryGenerator s = getSummary();
		String mSig = "<" + className + ": int intParaToReturn()>";
		Set<MethodFlow> res = s.createMethodSummary(mSig).getFlowsForMethod(mSig);
		assertTrue(containsFlow(res, Field,new String[] {PRIMITIVE_VAR}, Return,new String[] {}));
		
		//assertTrue(containsFieldToReturn(res, PRIMITIVE_VAR,NO_ACCESS_PATH,NO_ACCESS_PATH));
	}

	@Test(timeout = 100000)
	public void nonPrimitivVarToReturn1() {
		SummaryGenerator s = getSummary();
		String mSig = "<" + className + ": int intInDataToReturn()>";
		Set<MethodFlow> res = s.createMethodSummary(mSig).getFlowsForMethod(mSig);
		assertTrue(containsFlow(res, Field,new String[] {NON_PRIMITIVE_VAR1,DATACLASS_INT_FIELD}, Return,new String[] {}));
	}

	@Test(timeout = 100000)
	public void nonPrimitivVarToReturn11() {
		SummaryGenerator s = getSummary();
		String mSig = "<" + className + ": int intInDataToReturn()>";
		Set<MethodFlow> res = s.createMethodSummary(mSig).getFlowsForMethod(mSig);
		assertTrue(containsFlow(res, Field,new String[] {NON_PRIMITIVE_VAR1,DATACLASS_INT_FIELD}, Return,new String[] {}));
		assertTrue(res.size() == 1);
	}

	@Test(timeout = 100000)
	public void nonPrimitivVarToReturn2() {
		SummaryGenerator s = getSummary();
		String mSig = "<" + className + ": int intInDataToReturn2()>";
		Set<MethodFlow> res = s.createMethodSummary(mSig,methods()).getFlowsForMethod(mSig);
		for(MethodFlow f : res){
			System.out.println(f.toString());
		}
		assertTrue(containsFlow(res, Field,new String[] {NON_PRIMITIVE_VAR1,DATACLASS_INT_FIELD}, Return,new String[] {}));
	}

	@Test(timeout = 100000)
	public void nonPrimitivVarToReturn3() {
		SummaryGenerator s = getSummary();
		String mSig = "<" + className + ": int intInDataToReturn3()>";
		Set<MethodFlow> res = s.createMethodSummary(mSig).getFlowsForMethod(mSig);
		assertTrue(containsFlow(res, Field,new String[] {NON_PRIMITIVE_VAR1,DATACLASS_INT_FIELD}, Return,new String[] {}));
	}

	@Test(timeout = 100000)
	public void nonPrimitivVarToReturn4() {
		SummaryGenerator s = getSummary();
		String mSig = "<" + className + ": " + DATA_TYPE + " dataFieldToReturn()>";
		Set<MethodFlow> res = s.createMethodSummary(mSig).getFlowsForMethod(mSig);
		assertTrue(containsFlow(res, Field,new String[] {NON_PRIMITIVE_VAR1}, Return,new String[] {}));
	}

	@Test(timeout = 200000)
	public void nonPrimitivVarToReturn5() {
		SummaryGenerator s = getSummary();
		String mSig = "<" + className + ": " + DATA_TYPE + " dataFieldToReturn2()>";
		Set<MethodFlow> res = s.createMethodSummary(mSig).getFlowsForMethod(mSig);
		
		assertTrue(containsFlow(res, Field,new String[] {NON_PRIMITIVE_VAR1}, Return,new String[] {}));
	}

	@Test(timeout = 100000)
	public void swap() {
		SummaryGenerator s = getSummary();
		String mSig = "<" + className + ": void swap()>";
		Set<MethodFlow> res = s.createMethodSummary(mSig).getFlowsForMethod(mSig);
		assertTrue(containsFlow(res, Field,new String[] {NON_PRIMITIVE_VAR1}, Field,new String[] {NON_PRIMITIVE_VAR2}));
		assertTrue(containsFlow(res, Field,new String[] {NON_PRIMITIVE_VAR1}, Field,new String[] {NON_PRIMITIVE_VAR2}));
	}
	
	
	@Test(timeout = 100000)
	public void swap2() {
		SummaryGenerator s = getSummary();
		String mSig = "<" + className + ": void swap2()>";
		Set<MethodFlow> res = s.createMethodSummary(mSig).getFlowsForMethod(mSig);
		assertTrue(containsFlow(res, Field,new String[] {NON_PRIMITIVE_VAR1,DATACLASS_OBJECT_FIELD}, Field,new String[] {NON_PRIMITIVE_VAR2,DATACLASS_OBJECT_FIELD}));
		assertTrue(containsFlow(res, Field,new String[] {NON_PRIMITIVE_VAR2,DATACLASS_INT_FIELD}, Field,new String[] {NON_PRIMITIVE_VAR1,DATACLASS_INT_FIELD}));
	}

	@Test(timeout = 100000)
	public void data1ToDate2() {
		SummaryGenerator s = getSummary();
		String mSig = "<" + className + ": void data1ToDate2()>";
		Set<MethodFlow> res = s.createMethodSummary(mSig).getFlowsForMethod(mSig);
		assertTrue(containsFlow(res, Field,new String[] {NON_PRIMITIVE_VAR1}, Field,new String[] {NON_PRIMITIVE_VAR2}));
	}

	@Test(timeout = 150000)
	public void fieldToPara1() {
		SummaryGenerator s = getSummary();
		String mSig = "<" + className + ": void fieldToPara(" + DATA_TYPE + ")>";
		Set<MethodFlow> res = s.createMethodSummary(mSig).getFlowsForMethod(mSig);
		assertTrue(containsFlow(res, Field,new String[] {NON_PRIMITIVE_VAR1,DATACLASS_INT_FIELD}, Parameter,0,new String[] {DATACLASS_INT_FIELD}));
	}
	
	@Test(timeout = 150000)
	public void ListGetTest() {
		SummaryGenerator s = getSummary();
		String mSig = "<" + className + ": java.lang.Object get()>";
		Set<MethodFlow> res = s.createMethodSummary(mSig,java.util.Collections.singletonList("<soot.jimple.infoflow.test.methodSummary.ApiClass: void set(soot.jimple.infoflow.test.methodSummary.ApiClass$Node)>")).getFlowsForMethod(mSig);
		assertTrue(containsFlow(res, Field,new String[] {"<soot.jimple.infoflow.test.methodSummary.ApiClass: soot.jimple.infoflow.test.methodSummary.ApiClass$Node first>",
				"<soot.jimple.infoflow.test.methodSummary.ApiClass$Node: java.lang.Object item>"}, Return,new String[] {}));
	}

	@Test(timeout = 100000)
	public void fieldToField1() {
		SummaryGenerator s = getSummary();
		String mSig = "<" + className + ": void fieldToField1()>";
		Set<MethodFlow> res = s.createMethodSummary(mSig).getFlowsForMethod(mSig);
		assertTrue(containsFlow(res, Field,new String[] {NON_PRIMITIVE_VAR1}, Field,new String[] {NON_PRIMITIVE_VAR2}));
	}
	
	@Test//(timeout = 100000)
	public void fieldToField2() {
		SummaryGenerator s = getSummary();
		String mSig = "<" + className + ": void fieldToField2()>";
		Set<MethodFlow> res = s.createMethodSummary(mSig).getFlowsForMethod(mSig);
		assertTrue(containsFlow(res, Field,new String[] {NON_PRIMITIVE_VAR1, DATACLASS_OBJECT_FIELD}, Field,new String[] {OBJECT_FIELD}));
	}

	
	@Test(timeout = 100000)
	public void fieldToField3() {
		SummaryGenerator s = getSummary();
		String mSig = "<" + className + ": void fieldToField3()>";
		Set<MethodFlow> res = s.createMethodSummary(mSig).getFlowsForMethod(mSig);
		assertTrue(containsFlow(res, Field,new String[] {NON_PRIMITIVE_VAR2, DATACLASS_INT_FIELD}, Field,new String[] {PRIMITIVE_VAR}));
	}

	@Test(timeout = 100000)
	public void fieldToField4() {
		SummaryGenerator s = getSummary();
		String mSig = "<" + className + ": void fieldToField4()>";
		Set<MethodFlow> res = s.createMethodSummary(mSig).getFlowsForMethod(mSig);
		assertTrue(containsFlow(res, Field,new String[] {OBJECT_FIELD}, Field,new String[] {NON_PRIMITIVE_VAR2, DATACLASS_OBJECT_FIELD}));
	}

	@Test(timeout = 100000)
	public void fieldToField5() {
		SummaryGenerator s = getSummary();
		String mSig = "<" + className + ": void fieldToField5()>";
		Set<MethodFlow> res = s.createMethodSummary(mSig).getFlowsForMethod(mSig);
		assertTrue(containsFlow(res, Field,new String[] {NON_PRIMITIVE_VAR1,DATACLASS_OBJECT_FIELD}, Field,new String[] {NON_PRIMITIVE_VAR2, DATACLASS_OBJECT_FIELD}));
	}
	
	@Override
	Class<?> getClazz() {
		return ApiClass.class;
	}

	@Override
	SummaryGenerator getSummary() {
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
