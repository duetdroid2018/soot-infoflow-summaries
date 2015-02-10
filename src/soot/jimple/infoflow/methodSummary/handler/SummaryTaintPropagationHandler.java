package soot.jimple.infoflow.methodSummary.handler;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import soot.Scene;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.jimple.ReturnStmt;
import soot.jimple.Stmt;
import soot.jimple.infoflow.data.Abstraction;
import soot.jimple.infoflow.handlers.TaintPropagationHandler;
import soot.jimple.toolkits.ide.icfg.BiDiInterproceduralCFG;

/**
 * The SummaryTaintPropagationHandler collects all abstraction that reach the return statement of a specified method.
 * 
 */
public class SummaryTaintPropagationHandler implements TaintPropagationHandler {
	
	private final String methodSig;
	private final Set<String> excludedMethods;
	private SootMethod method = null;
	
	private Map<Abstraction, Stmt> result = new ConcurrentHashMap<>();
	
	public SummaryTaintPropagationHandler(String m) {
		this.methodSig = m;
		this.excludedMethods = Collections.emptySet();
	}
	
	public SummaryTaintPropagationHandler(String m, Set<String> excludedMethods) {
		this.methodSig = m;
		this.excludedMethods = excludedMethods;
	}
	
	@Override
	public void notifyFlowIn(Unit stmt,
			Abstraction result,
			BiDiInterproceduralCFG<Unit, SootMethod> cfg,
			FlowFunctionType type) {
		// Get the method for which we should create the summary
		if (method == null)
			method = Scene.v().getMethod(methodSig);
		
		// Get the method containing the current statement. If this does
		// not match the method for which we shall create a summary, we
		// ignore it.
		SootMethod m = cfg.getMethodOf(stmt);
		if (!method.equals(m))
			return;

		// Handle the flow function
		if (type.equals(TaintPropagationHandler.FlowFunctionType.ReturnFlowFunction))
			handleReturnFlow(stmt, result, cfg);
		else if (type.equals(TaintPropagationHandler.FlowFunctionType.CallToReturnFlowFunction))
			handleCallToReturnFlow(stmt, result, cfg);
	}
	
	private void handleReturnFlow(Unit stmt,
			Abstraction abs,
			BiDiInterproceduralCFG<Unit, SootMethod> cfg) {
		// Check whether we must register the abstraction for post-processing
		// We ignore inactive abstractions
		if (!abs.isAbstractionActive())
			return;
		
		// If the value is returned, we save it
		boolean isValidResult = false;
		if (stmt instanceof ReturnStmt) {
			ReturnStmt retStmt = (ReturnStmt) stmt;
			isValidResult |= (retStmt.getOp() == abs.getAccessPath().getPlainValue());
		}
		
		// If the value corresponds to a parameter, we save it
		if (!isValidResult)
			for (Value param : method.getActiveBody().getParameterLocals())
				if (abs.getAccessPath().getPlainValue() == param) {
					isValidResult = true;
					break;
				}
		
		// If the value is a field, we save it
		isValidResult |= (!method.isStatic()
				&& abs.getAccessPath().getPlainValue() == method.getActiveBody().getThisLocal());
		
		if (isValidResult)
			this.result.put(abs, (Stmt) stmt);
	}
	
	private void handleCallToReturnFlow(Unit stmt,
			Abstraction abs,
			BiDiInterproceduralCFG<Unit, SootMethod> cfg) {
		// If we have callees, we analyze them as usual
		Collection<SootMethod> callees = cfg.getCalleesOfCallAt(stmt);
		if (callees != null && !callees.isEmpty())
			return;
		
		// If we don't have any callees, we need to build a gap into our
		// summary. The taint wrapper takes care of continuing the analysis
		// after the gap.
		this.result.put(abs, (Stmt) stmt);
	}
	
	@Override
	public Set<Abstraction> notifyFlowOut(Unit u,
			Abstraction incoming,
			Set<Abstraction> outgoing,
			BiDiInterproceduralCFG<Unit, SootMethod> cfg,
			FlowFunctionType type) {
		// Do not propagate through excluded methods
//		SootMethod sm = cfg.getMethodOf(u);
//		if (excludedMethods.contains(sm.getSignature()))
//			return Collections.emptySet();
		
		/*
		if (outgoing == null || outgoing.isEmpty())
			return outgoing;
		
		// We only influence assignments from fields
		if (!(u instanceof AssignStmt))
			return outgoing;
		
		AssignStmt assignStmt = (AssignStmt) u;
		final Value leftOp = assignStmt.getLeftOp();
		final Value rightOp = assignStmt.getRightOp();

		if (!(leftOp instanceof Local) || !(rightOp instanceof FieldRef))
			return outgoing;
		
		// If this statement makes a source more concrete, we make it a source
		// of its own.
		Set<Abstraction> newOutgoing = new HashSet<>(outgoing.size());			
		for (Abstraction outgoingAbs : outgoing) {
			if (outgoingAbs.getAccessPath().getPlainValue() != leftOp)
				continue;
			
			// Extend the right side
			FieldRef rightRef = (FieldRef) rightOp;
			boolean matches = incoming.getAccessPath().getFieldCount() > 0
					&& incoming.getAccessPath().getFirstField() == rightRef.getField();
			if (rightRef instanceof InstanceFieldRef)
				matches = ((InstanceFieldRef) rightRef).getBase() == incoming.getAccessPath().getPlainValue();
			
			if (matches) {
				// Extend the left side with the right side
				AccessPath newAP = outgoingAbs.getAccessPath().copyWithNewValue(rightRef);
				
				// Connect to the previous source context
				if (incoming.getSourceContext() != null) {
					AccessPath sourceAP = incoming.getSourceContext().getAccessPath();
					newAP = sourceAP.appendFields(newAP.getFields(),
							newAP.getFieldTypes(), newAP.getTaintSubFields());
				}
				
				Abstraction newAbs = outgoingAbs.injectSourceContext(new SourceContext(newAP, null));
				newOutgoing.add(newAbs);
			}
			else
				newOutgoing.add(outgoingAbs);
		}
		return newOutgoing;
		*/
		
		
		return outgoing;
	}
	
	public Map<Abstraction, Stmt> getResult() {
		return result;
	}
	
}
