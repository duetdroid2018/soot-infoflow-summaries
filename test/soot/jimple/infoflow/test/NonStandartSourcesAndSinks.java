package soot.jimple.infoflow.test;

import java.util.List;

public class NonStandartSourcesAndSinks {
	int[] intArray = new int[100];
	
	public int intMultiTest3(int a, int b){
		return a + b;
	}
	
	public void listParameter5(List<Integer> list){
		list.add(intArray[2]);
	}
}
