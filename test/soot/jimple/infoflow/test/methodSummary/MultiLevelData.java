package soot.jimple.infoflow.test.methodSummary;

public class MultiLevelData {
	public int level;
	public Object data;
	public MultiLevelData nextLevel;
	public static int maxlvl = 0;
	public MultiLevelData(int numLvls) {
		if(maxlvl == 0)
			maxlvl = numLvls;
		level = maxlvl - numLvls;
		if(numLvls <= 1){
			nextLevel = null;
		}else{
			nextLevel = new MultiLevelData(numLvls - 1); 
		}
	}
	
	Object getData(int lvl){
		if(lvl == level){
			return data;
		}else if(nextLevel == null){
			return null;
		}
		return nextLevel.getData(lvl);
	}
	
}
