package soot.jimple.infoflow.test.methodSummary;

import java.util.LinkedList;
import java.util.List;

public class ParaToField {
	int intField = 1;
	Object obField;
	List<Object> listField;
	Object[] arrayField = new Object[100];
	int[] intArray = new int[100];
	Data dataField;

	public ParaToField() {
		dataField = new Data();
		listField = new LinkedList<Object>();
	}
	void intPara(int i) {
		intField = i;
		dataField.setValue(i);
		intArray[3] = i;
	}

	int intPara2(int i) {
		dataField.setValue(i);
		return dataField.value;
	}
	void intParaRec(int i, int count) {
		if (count == 3) {
			intField = i;
			dataField.value = i;
			intArray[3] = i;
		} else
			intParaRec(i, count - 1);
	}

	void objPara(Object o) {
		obField = o;
		dataField.data = o;
		arrayField[3] = o;
		listField.add(o);
	}

	void intAndObj(int i, Object o) {
		intField = i;
		dataField.value = i;
		intArray[3] = i;
		obField = o;
		dataField.data = o;
		arrayField[3] = o;
		listField.add(o);
	}

	void arrayParas(int[] i, Object[] o) {
		intField = i[3];
		dataField.value = i[2];
		intArray = i;
		obField = o;
		dataField.setO(o);
		arrayField[3] = o[5];
		listField.add(o[1]);
	}

	void dataAndList(Data d, List<Object> list) {
		intField = d.getValue();
		dataField.setValue(d.value);
		intArray[5] = d.value;
		obField = list.get(1);
		dataField.data = d.getO();
		arrayField[3] = d.data;
		listField.add(list.get(3));
	}
	void data(Data d) {
		intField = d.getValue();
	}
}
