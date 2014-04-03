package soot.jimple.infoflow.test.methodSummary;

import heros.solver.Pair;


public class ApiClass implements IApiClass {
	private int primitiveVariable;

	private String aString;
	private Object aObject;
	private Data nonPrimitiveVariable = new Data();
	private Data nonPrimitive2Variable = new Data();
	@SuppressWarnings("unused")
	private static int staticPrimitiveVariable;
	@SuppressWarnings("unused")
	private static Data staticNonPrimitiveVariable = new Data();

	Node first ;
	private static class Node {
		Object item;
	}
	
	public Object get() {
		return first.item;
//		return first == null ? null : first.data;
	}
	
	public void set(Node data) {
		first = data;
	}
	public Data getNonPrimitiveVariable() {
		return nonPrimitiveVariable;
	}

	public Object getNonPrimitive1Data() {
		return getNonPrimitive2Variable().data;
	}

	// standard flow: Source Paramter -> Sink return X
	public int standardFlow(int i) {
		return i;
	}

	public Object standardFlow(Object i) {
		return i;
	}

	public int standardFlow2(int i, int j) {
		return i + j;
	}

	public int standardFlow2Com(int i, int j) {
		int tmp1 = i + 3;
		int tmp2 = j + 7;
		int tmp3 = tmp1 * 15;
		int tmp4 = tmp2 - 10 / 2;
		return tmp4 + tmp3;
	}

	public int standardFlow3(Data data) {
		return data.value;
	}

	public Data standardFlow5(Object o) {
		Data data = new Data();
		data.setData(o);
		return data;
	}

	public Data standardFlow6(Object o) {
		Data data = new Data();
		data.data = o;
		return data;
	}

	public Data standardFlow7(Data o) {
		if (o.value > 3) {
			return o;
		} else {
			Data data = new Data();
			// data.data = o.data;
			return data;
		}
	}

	public Data standardFlow8(Data o) {
		Data data = new Data();
		data.data = o.data;
		return data;
	}

	public Data standardFlow9(Data d, Object o) {
		Data data = d;
		data.data = o;
		return data;
	}

	public Data standardFlow10(Data d) {
		Data data = d;
		return data;
	}

	public Data standardFlow11(Data d) {
		Data data = new Data();
		data.data = d.data;
		return data;
	}

	public Data standardFlow4(int i, Object o) {
		if (i > 3)
			return new Data(o, i);
		if (i == -3)
			return new Data(o, i);
		return new Data(new Object(), 3);
	}

	// standard static flow: Source Paramter -> Sink return X (but static
	// method)
	public static int staticStandardFlow1(int i, int j) {
		return i + j;
	}

	public static Data staticStandardFlow2(int i, Object o) {
		if (i > 3)
			return new Data(o, i);
		if (i == -3)
			return new Data(o, i);
		return new Data(new Object(), 3);
	}

	// Some no flow methods
	public int noFlow(int i) {
		return 3;
	}

	public int noFlow2(int i, int j) {
		int a = i + j;
		a = 3;
		return a;
	}

	public Data noFlow3(Data data) {
		return new Data();
	}

	public Data noFlow4(int i, Object o) {
		if (i > 3)
			return new Data();
		return new Data(new Object(), 3);
	}

	// paraToVar Flow: Source Para -> Sink global Var
	public int paraToVar(int i, int j) {
		primitiveVariable = i + j;
		return 3;
	}

	public Data paraToVar2(int i, Object o) {
		if (i > 3)
			return new Data(o, i);
		if (i == -3)
			nonPrimitiveVariable = new Data(o, i);
		return new Data(new Object(), 3);
	}

	public void paraToField2(int i, Object o) {
		nonPrimitiveVariable = new Data(o, i);

	}

	public void paraToField(int i) {
		nonPrimitiveVariable.setValue(i);
	}

	public Data paraToVarX(int i, Object o) {
		nonPrimitiveVariable = new Data(o, i);
		return new Data(o, i);

	}

	private Object[] objs = new Object[100];

	public void paraToVarY(int i, Object o) {
		objs[i] = o;
	}

	// static paraToVar Flow: Source Para -> Sink global Var (static method)
	public static int staticParaToVar(int i, int j) {
		staticPrimitiveVariable = i + j;
		return 3;
	}

	public static Data staticParaToVar2(int i, Object o) {
		if (i < 3)
			return new Data(o, i);
		if (i == -3)
			staticNonPrimitiveVariable = new Data(o, i);
		return new Data(new Object(), 3);
	}

	public int paraToStaticVar1(int i, int j) {
		staticPrimitiveVariable = i + j;
		return 3;
	}

	public Data paraToStaticVar2(int i, Object o) {
		if (i > 3)
			return new Data(o, i);
		if (i == -3)
			staticNonPrimitiveVariable = new Data(o, i);
		return new Data(new Object(), 3);
	}

	// paraToparaFlow: Source Para -> Sink para
	public void paraToparaFlow1(int i, Data o) {
		o.setValue(i);
	}

	public void paraToparaFlow2(int i, Object o, Data data) {
		data.value = i;
		data.data = o;
	}

	public void paraToparaFlow3(int i, Object o, Data data, Data data2) {
		data.setValue(i);
		data.setData(o);
		data2.setData(o);
	}

	// staticParaToparaFlow: Source Para -> Sink para
	public static void staticParaToparaFlow1(int i, Data o) {
		o.setValue(i);
	}

	public static void staticParaToparaFlow2(int i, Object o, Data data) {
		data.setValue(i);
		data.setData(o);
	}

	public static void staticParaToparaFlow3(int i, Object o, Data data, Data data2) {
		data.setValue(i);
		data.setData(o);
		data2.setData(o);
	}

	// mix tests
	public Data mixedFlow1(int i, Data data) {
		
		if (data.value > 43) {
			primitiveVariable = data.value;
		} else {
			staticPrimitiveVariable = 3;
		}
		data.value = i;
		return data;
	}

	public Data mixedFlow1small(int i, Data data) {
		data.value = i;
		return data;
	}

	public int intParaToReturn() {
		return primitiveVariable;
	}

	public int intInDataToReturn() {
		return nonPrimitiveVariable.value;
	}

	public int intInDataToReturn2() {
		return getNonPrimitiveVariable().value;
	}

	public int intInDataToReturn3() {
		return getNonPrimitiveVariable().getValue();
	}

	public Data dataFieldToReturn() {
		return nonPrimitiveVariable;
	}

	public Object objInDataFieldToReturn() {
		return nonPrimitiveVariable.getData();
	}

	public Data dataFieldToReturn2() {
		return getNonPrimitiveVariable();
	}

	public Data getNonPrimitive2Variable() {
		return nonPrimitive2Variable;
	}

	public void swap() {
		Data t = nonPrimitive2Variable;
		nonPrimitive2Variable = nonPrimitiveVariable;
		nonPrimitiveVariable = t;
	}

	public void swap2() {
		Data t = nonPrimitive2Variable;
		nonPrimitive2Variable.data = nonPrimitiveVariable.data;
		nonPrimitiveVariable.value = t.value;
	}

	public void data1ToDate2() {
		nonPrimitive2Variable = nonPrimitiveVariable;
	}

	public void fieldToPara(Data d) {
		d.value = nonPrimitiveVariable.value;
	}
	public void fieldToPara2(Data d) {
		d.data = nonPrimitiveVariable.data;
	}
	

	public int getPrimitiveVariable() {
		return primitiveVariable;
	}

	public void setPrimitiveVariable(int primitiveVariable) {
		this.primitiveVariable = primitiveVariable;
	}

	public String getaString() {
		return aString;
	}

	public void setaString(String aString) {
		this.aString = aString;
	}

	public Object getaObject() {
		return aObject;
	}

	public void setaObject(Object aObject) {
		this.aObject = aObject;
	}

	public int noThisFlow() {
		return 3;
	}

	public Object noThisFlow2(Data d) {
		d.value = nonPrimitiveVariable.value;
		return null;
	}

	public Object noThisFlow3() {
		Data t = nonPrimitive2Variable;
		nonPrimitive2Variable = nonPrimitiveVariable;
		nonPrimitiveVariable = t;
		return new Object();
	}

	public Object mutipleSources() {
		Data data = new Data();
		data.data = nonPrimitiveVariable;
		return new Pair<Object, Object>(data.data, nonPrimitiveVariable.data);
	}

	//TODO write test for the following methods
	public Pair<ApiClass, Object> thisToReturn() {
		Pair<ApiClass, Object> t = new Pair<ApiClass, Object>(this, new Object());
		return t;
	}
	
	public Pair<ApiClass, Object> thisAndFieldToReturn() {
		Pair<ApiClass, Object> t = new Pair<ApiClass, Object>(this, nonPrimitiveVariable);
		return t;
	}
	
	public Pair<Object, Pair<Object, ApiClass>> thisAndFieldToReturn1() {
		Pair<Object, Pair<Object, ApiClass>> t = new Pair<Object, Pair<Object, ApiClass>>(new Object(),
				new Pair<Object, ApiClass>(nonPrimitive2Variable.data, this));
		return t;
	}

	public Pair<Object, Pair<Object, ApiClass>> thisAndFieldToReturn2() {
		Pair<Object, Pair<Object, ApiClass>> t = new Pair<Object, Pair<Object, ApiClass>>(
				nonPrimitive2Variable.data, new Pair<Object, ApiClass>(new Object(), this));
		return t;
	}
	
	public void paraToSamePara(Data d){
		d.switchData();
	}
	public void paraToSamePara2(Data d){
		d.switchSwitch();
	}
	public void paraToParaT(Data d, Object o){
		d.d.d.d.data = o;
	}
	public void paraToParaT2(Data d, Data d2){
		d.d = d2;
	}
	public void paraToParaT3(Data d, Data d2){
		d.d.d = d2;
	}
	public void paraToParaT4(Data d, Object d2){
		d.d.data = d2;
	}
	public void paraToFieldT1(Data d){
		nonPrimitive2Variable.d = d;
	}
	public void paraToFieldT2(Data d){
		nonPrimitive2Variable.d.d = d;
	}
	public void paraToFieldT22(Data d){
		nonPrimitive2Variable.d = d.d;
	}
	public void paraToFieldT3(Data d){
		nonPrimitive2Variable.d.d.d = d;
	}
	public void paraToFieldT33(Data d){
		nonPrimitive2Variable.d = d.d.d;
	}
	public void paraToParaArray(Object []o ){
		o[3] = o[2];
	}
}
