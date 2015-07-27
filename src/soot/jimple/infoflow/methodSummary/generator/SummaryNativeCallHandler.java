package soot.jimple.infoflow.methodSummary.generator;

import java.util.Collections;
import java.util.Set;

import soot.Value;
import soot.jimple.DefinitionStmt;
import soot.jimple.Stmt;
import soot.jimple.infoflow.data.Abstraction;
import soot.jimple.infoflow.data.AccessPath;
import soot.jimple.infoflow.nativ.NativeCallHandler;

/**
 * Handler for dealing with native calls during summary generation
 * 
 * @author Steven Arzt
 *
 */
public class SummaryNativeCallHandler extends NativeCallHandler {

	@Override
	public Set<Abstraction> getTaintedValues(Stmt call, Abstraction source,
			Value[] params) {
		// Check whether we have an incoming access path
		boolean found = false;
		for (Value val : call.getInvokeExpr().getArgs())
			if (val == source.getAccessPath().getPlainValue()) {
				found = true;
				break;
			}
		if (!found)
			return Collections.emptySet();
		
		// We over-approximate native method calls
		if (call instanceof DefinitionStmt) {
			DefinitionStmt defStmt = (DefinitionStmt) call;
			return Collections.singleton(source.deriveNewAbstraction(
					new AccessPath(defStmt.getLeftOp(), true), call));
		}
		
		return Collections.emptySet();
	}

}
