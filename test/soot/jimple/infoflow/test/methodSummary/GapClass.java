package soot.jimple.infoflow.test.methodSummary;

public class GapClass implements IGapClass {

	@Override
	public String callTheGap(String in) {
		return in;
	}

	@Override
	public void fillDataString(String in, Data d) {
		d.stringField = in;
	}

	@Override
	public Data dataThroughGap(Data d) {
		return d;
	}

}
