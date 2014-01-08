package soot.jimple.infoflow.test.methodSummary.junit;

public class ApiTestHelper extends TestHelper {
	protected final static String CLASS_SIG = "soot.jimple.infoflow.test.methodSummary.ApiClass";
	
	protected final static String NON_PRIMITIVE_TYP = "soot.jimple.infoflow.test.methodSummary.Data";
	protected final static String NON_PRIMITIVE_VAR1 = CLASS_SIG + ": " + NON_PRIMITIVE_TYP + " nonPrimitiveVariable";
	protected final static String NON_PRIMITIVE_VAR1_VALUE = CLASS_SIG + ": " + NON_PRIMITIVE_TYP
			+ " nonPrimitiveVariable" + ";" + NON_PRIMITIVE_TYP + ": int value";
	protected final static String NON_PRIMITIVE_VAR2 = CLASS_SIG + ": " + NON_PRIMITIVE_TYP + " nonPrimitive2Variable";
	protected final static String PRIMITIVE_VAR = CLASS_SIG + ": " + INT_TYPE + " primitiveVariable";
	protected final static String STATIC_NON_PRIMITIVE_VAR = CLASS_SIG + ": " + NON_PRIMITIVE_TYP
			+ " staticNonPrimitiveVariable";
	protected final static String STATIC_PRIMITIVE_VAR = CLASS_SIG + ": " + INT_TYPE + " staticPrimitiveVariable";
	
}
