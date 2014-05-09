package soot.jimple.infoflow.test.methodSummary.junit;

import org.junit.Test;



public class ApiTestNotWorking extends ApiTestHelper{

	@Override
	Class getClazz() {
		return null;
	}
	
	@Test
	public void doNothing(){
		
	}
	
//	@Ignore
//	@Test
//	public void nonPrimitivVarToReturn1(){
//		Set<AbstractMethodFlow> res = getMethodFlows("int intInDataToReturn()");
//		assertTrue(containsFieldToReturn(res, NON_PRIMITIVE_VAR1_VALUE));
//	}
//	@Ignore
//	@Test
//	public void nonPrimitivVarToReturn11(){
//		Set<AbstractMethodFlow> res = getMethodFlows("int intInDataToReturn()");
//		assertTrue(containsFieldToReturn(res, NON_PRIMITIVE_VAR1_VALUE));
//		assertTrue(res.size() == 1);
//	}
//	
//	@Ignore
//	@Test
//	public void nonPrimitivVarToReturn2(){
//		Set<AbstractMethodFlow> res = getMethodFlows("int intInDataToReturn2()");
//		assertTrue(containsFieldToReturn(res, NON_PRIMITIVE_VAR1_VALUE));
//	}
//	@Ignore
//	@Test
//	public void nonPrimitivVarToReturn3(){
//		Set<AbstractMethodFlow> res = getMethodFlows("int intInDataToReturn3()");
//		assertTrue(containsFieldToReturn(res, NON_PRIMITIVE_VAR1_VALUE));
//	}
	//TODO add the test again
//	@Ignore
//	@Test
//	public void mixedFlow11() {
//		Set<AbstractMethodFlow> res = getMethodFlows(NON_PRIMITIVE_TYP + " mixedFlow1(int," + NON_PRIMITIVE_TYP + ")");
//		assertTrue(containsParaToReturn(res, 1, NON_PRIMITIVE_TYP));
//		assertTrue(containsParaToParaFlow(res, 0,INT_TYPE,1,NON_PRIMITIVE_TYP));
//		assertTrue(containsParaToFieldFlow(res, 1, NON_PRIMITIVE_TYP, PRIMITIVE_VAR));
//		
//		//there is a flow para 0 -> para 1 -> return
//		assertTrue(containsParaToReturn(res, 0, INT_TYPE));
//
//		//assertTrue(res.size() == 2);
//	}
//	@Ignore
//	@Test
//	public void paraToStaticVar1() {
//		Set<AbstractMethodFlow> res = getMethodFlows("int paraToStaticVar1(int,int)");
//		assertTrue(containsParaToFieldFlow(res, 0, INT_TYPE, STATIC_PRIMITIVE_VAR));
//		assertTrue(containsParaToFieldFlow(res, 1, INT_TYPE, STATIC_PRIMITIVE_VAR));
//	}
//	@Ignore
//	@Test
//	public void paraToStaticVar11() {
//		Set<AbstractMethodFlow> res = getMethodFlows("int paraToStaticVar1(int,int)");
//		assertTrue(containsParaToFieldFlow(res, 0, INT_TYPE, STATIC_PRIMITIVE_VAR));
//		assertTrue(containsParaToFieldFlow(res, 1, INT_TYPE, STATIC_PRIMITIVE_VAR));
//		assertTrue(res.size() == 2);
//	}
//
//	@Ignore
//	@Test
//	public void paraToStaticVar2() {
//		Set<AbstractMethodFlow> res = getMethodFlows(NON_PRIMITIVE_TYP + " paraToStaticVar2(int,java.lang.Object)");
//		assertTrue(containsParaToReturn(res, 0, INT_TYPE));
//		assertTrue(containsParaToReturn(res, 1, OBJECT_TYPE));
//		assertTrue(containsParaToFieldFlow(res, 0, INT_TYPE, STATIC_NON_PRIMITIVE_VAR));
//		assertTrue(containsParaToFieldFlow(res, 1, OBJECT_TYPE, STATIC_NON_PRIMITIVE_VAR));
//	}
//	@Ignore
//	@Test
//	public void paraToStaticVar21() {
//		Set<AbstractMethodFlow> res = getMethodFlows(NON_PRIMITIVE_TYP + " paraToStaticVar2(int,java.lang.Object)");
//		assertTrue(containsParaToReturn(res, 0, INT_TYPE));
//		assertTrue(containsParaToReturn(res, 1, OBJECT_TYPE));
//		assertTrue(containsParaToFieldFlow(res, 0, INT_TYPE, STATIC_NON_PRIMITIVE_VAR));
//		assertTrue(containsParaToFieldFlow(res, 1, OBJECT_TYPE, STATIC_NON_PRIMITIVE_VAR));
//		assertTrue(res.size() == 4);
//	}
	
	
}
