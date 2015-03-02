package soot.jimple.infoflow.methodSummary.xml;

public class XMLConstants {
	//xml summary tree
	/*
	 * <methods>
	 * 	<method>
	 * 		<flows>
	 * 			<flow>
	 * 				<from></from>
	 * 				<to></to>
	 * 			</flow>
	 * 			...
	 * 		</flows>
	 * 	</method>
	 * 	...
	 * </methods>
	 */
	public static final String TREE_METHODS = "methods";
	public static final String TREE_METHOD = "method";
	public static final String TREE_FLOWS = "flows";
	public static final String TREE_FLOW = "flow";
	public static final String TREE_SINK = "to";
	public static final String TREE_SOURCE = "from";
	
	public static final String ATTRIBUT_METHOD_SIG = "id";
	public static final String ATTRIBUTE_FLOWTYPE = "sourceSinkType";
	public static final String ATTRIBUTE_PARAMTER_INDEX = "ParameterIndex";
	public static final String ATTRIBUTE_ACCESSPATH = "AccessPath";
	public static final String ATTRIBUTE_ACCESSPATHTYPES = "AccessPathTypes";
	public static final String ATTRIBUTE_BASETYPE = "BaseType";
	public static final String ATTRIBUTE_ERROR = "ERROR";
	public static final String ATTRIBUTE_RETURN = "ReturnLocal";
	public static final String ATTRIBUTE_TAINT_SUB_FIELDS = "taintSubFields";
	
	public static final String VALUE_TRUE = "true";
	public static final String VALUE_FALSE = "false";

	public static final String VALUE_PARAMETER = "parameter";
	public static final String VALUE_FIELD = "field";
	public static final String VALUE_RETURN = "return";
	
}
