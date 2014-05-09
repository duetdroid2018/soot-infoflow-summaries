package soot.jimple.infoflow.test.methodSummary.junit;

public abstract class ApiTestHelper extends TestHelper {
	protected final static String CLASS_SIG = "soot.jimple.infoflow.test.methodSummary.ApiClass";
	
	protected final static String DATA_TYPE = "soot.jimple.infoflow.test.methodSummary.Data";
	protected final static String NON_PRIMITIVE_VAR1 = "<"+CLASS_SIG + ": " + DATA_TYPE + " nonPrimitiveVariable>";
	//protected final static String NON_PRIMITIVE_VAR1_VALUE = "<"+CLASS_SIG + ": " + DATA_TYPE
	//		+ " nonPrimitiveVariable" + ";" + DATA_TYPE + ": int value";
	protected final static String NON_PRIMITIVE_VAR2 = CLASS_SIG + ": " + DATA_TYPE + " nonPrimitive2Variable";
	protected final static String PRIMITIVE_VAR = CLASS_SIG + ": " + INT_TYPE + " primitiveVariable";
	protected final static String STATIC_NON_PRIMITIVE_VAR = CLASS_SIG + ": " + DATA_TYPE
			+ " staticNonPrimitiveVariable";
	protected final static String STATIC_PRIMITIVE_VAR = CLASS_SIG + ": " + INT_TYPE + " staticPrimitiveVariable";
	
}
