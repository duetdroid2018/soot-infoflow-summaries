package soot.jimple.infoflow.methodSummary.postProcessor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import soot.Local;
import soot.PrimType;
import soot.SootField;
import soot.SootMethod;
import soot.Type;
import soot.Unit;
import soot.Value;
import soot.jimple.AssignStmt;
import soot.jimple.Constant;
import soot.jimple.InstanceFieldRef;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.LengthExpr;
import soot.jimple.NewArrayExpr;
import soot.jimple.ReturnStmt;
import soot.jimple.Stmt;
import soot.jimple.infoflow.data.Abstraction;
import soot.jimple.infoflow.data.AccessPath;
import soot.jimple.infoflow.data.AccessPath.BasePair;
import soot.jimple.infoflow.data.SourceContextAndPath;
import soot.jimple.infoflow.solver.cfg.IInfoflowCFG;
import soot.jimple.infoflow.util.BaseSelector;

/**
 * Path tracking item adapted for reconstructing source access paths
 * 
 * @author Steven Arzt
 *
 */
class SummarySourceContextAndPath extends SourceContextAndPath {
	
	private final IInfoflowCFG cfg;
	private boolean isAlias;
	private AccessPath curAP;
	private final List<SootMethod> callees;
	
	public SummarySourceContextAndPath(IInfoflowCFG cfg,
			AccessPath value, Stmt stmt, boolean isAlias,
			AccessPath curAP, List<SootMethod> callees) {
		super(value, stmt);
		this.cfg = cfg;
		this.isAlias = isAlias;
		this.curAP = curAP;
		this.callees = callees;
	}
	
	public SummarySourceContextAndPath(IInfoflowCFG cfg,
			AccessPath value, Stmt stmt, AccessPath curAP, boolean isAlias,
			List<SootMethod> callees, Object userData) {
		super(value, stmt, userData);
		this.cfg = cfg;
		this.isAlias = isAlias;
		this.curAP = curAP;
		this.callees = callees;
	}
	
	@Override
	public SourceContextAndPath extendPath(Abstraction abs, boolean trackPath) {		
		// Do we have data at all?
		if (abs == null)
			return this;
		if (abs.getCurrentStmt() == null && abs.getCorrespondingCallSite() == null)
			return this;
		if (abs.getSourceContext() != null)
			return this;
		
		SummarySourceContextAndPath scap = clone();
		
		// Extend the call stack
		if (abs.getCorrespondingCallSite() != null
				&& abs.getCorrespondingCallSite() != abs.getCurrentStmt()) {
			if (scap.callStack == null)
				scap.callStack = new ArrayList<Stmt>();
			scap.callStack.add(0, abs.getCorrespondingCallSite());
		}
		
		// Compute the next access path
		final Stmt stmt = abs.getCurrentStmt();
		final Stmt callSite = abs.getCorrespondingCallSite();
		boolean matched = false;
		
		// If we have reached the source definition, we can directly take
		// the access path
		if (stmt == null && abs.getSourceContext() != null) {
			scap.curAP = abs.getSourceContext().getAccessPath();
			return scap;
		}
				
		// In case of a call-to-return edge, we have no information about
		// what happened in the callee, so we take the incoming access path
		if (stmt.containsInvokeExpr()) {
			if (callSite == stmt) {
				// only change base local
				Value newBase = abs.getPredecessor() != null
						? abs.getPredecessor().getAccessPath().getPlainValue()
						: abs.getAccessPath().getPlainValue();
				
				// If the incoming value is a primitive, we reset the field
				// list
				if (newBase.getType() instanceof PrimType)
					scap.curAP = new AccessPath(newBase, true);
				else
					scap.curAP = scap.curAP.copyWithNewValue(newBase);
				matched = true;
			}
		}
		
		if (matched)
			return scap;
		
		// Our call stack may run empty if we have a follow-returns-past-seeds case
		if (stmt.containsInvokeExpr()) {
			// Forward propagation, backwards reconstruction: We leave
			// methods when we reach the call site.
			Collection<SootMethod> curCallees = scap.callees.isEmpty() ? null
					: Collections.singleton(scap.callees.remove(0));
			if (curCallees == null)
				curCallees = cfg.getCalleesOfCallAt(stmt);
			
			// Match the access path from the caller back into the callee
			for (SootMethod callee : curCallees) {
				AccessPath newAP = mapAccessPathBackIntoCaller(scap.curAP, stmt, callee);
				if (newAP != null) {
					scap.curAP = newAP;
					matched = true;
					break;
				}
			}
			
			// If none of the possible callees worked, we're in trouble
			if (!matched)
				return null;
		}
		else if (callSite != null && callSite.containsInvokeExpr()) {
			// Forward propagation, backwards reconstruction: We enter
			// methods at the return site when we have a corresponding call site.
			SootMethod callee = cfg.getMethodOf(stmt);
			if (scap.callees.isEmpty() || callee != scap.callees.get(0))
				scap.callees.add(0, callee);
			
			// Map the access path into the scope of the callee
			AccessPath newAP = mapAccessPathIntoCallee(scap.curAP, stmt, callSite, callee,
					!abs.isAbstractionActive());
			if (newAP != null) {
				scap.curAP = newAP;
				matched = true;
			}
			else
				return null;
		}
		else if (stmt instanceof AssignStmt) {
			final AssignStmt assignStmt = (AssignStmt) stmt;
			final Value leftOp = BaseSelector.selectBase(assignStmt.getLeftOp(), false);
			final Value rightOp = BaseSelector.selectBase(assignStmt.getRightOp(), false);
			
			// If the access path must matches on the left side, we
			// continue with the value from the right side.
			if (leftOp instanceof Local
					&& leftOp == scap.curAP.getPlainValue()
					&& !assignStmt.containsInvokeExpr()) {
				// Get the next value from the right side of the assignment
				final Value[] rightOps = BaseSelector.selectBaseList(assignStmt.getRightOp(), false);
				Value rop = null;
				if (rightOps.length == 1)
					rop = rightOps[0];
				else if (abs.getPredecessor() != null) {
					Value base = abs.getPredecessor().getAccessPath().getPlainValue();
					for (Value rv : rightOps)
						if (base == rv) {
							rop = rv;
							break;
						}
				}
				
				if (assignStmt.getRightOp() instanceof NewArrayExpr)
					scap.curAP = new AccessPath(rop, false);
				else
					scap.curAP = scap.curAP.copyWithNewValue(rop, null, false, true);
				matched = true;
			}
			else if (assignStmt.getLeftOp() instanceof InstanceFieldRef) {
				InstanceFieldRef ifref = (InstanceFieldRef) assignStmt.getLeftOp();
				AccessPath matchedAP = matchAccessPath(scap.curAP, ifref.getBase(), ifref.getField());
				if (matchedAP != null) {
					scap.curAP = matchedAP.copyWithNewValue(assignStmt.getRightOp(),
							matchedAP.getFirstFieldType(), true, false);
					matched = true;
				}
			}
			
			if (matched || abs.isAbstractionActive())
				return scap;
			
			// For aliasing relationships, we also need to check the right
			// side
			if (rightOp instanceof Local
					&& rightOp == scap.curAP.getPlainValue()
					&& !assignStmt.containsInvokeExpr()
					&& !(assignStmt.getRightOp() instanceof LengthExpr)) {
				// Get the next value from the right side of the assignment
				final Value[] leftOps = BaseSelector.selectBaseList(assignStmt.getLeftOp(), false);
				Value lop = null;
				if (leftOps.length == 1)
					lop = leftOps[0];
				else if (abs.getPredecessor() != null) {
					Value base = abs.getPredecessor().getAccessPath().getPlainValue();
					for (Value rv : leftOps)
						if (base == rv) {
							lop = rv;
							break;
						}
				}
				
				scap.curAP = scap.curAP.copyWithNewValue(lop, null, false, false);
				matched = true;
			}
			else if (assignStmt.getRightOp() instanceof InstanceFieldRef) {
				InstanceFieldRef ifref = (InstanceFieldRef) assignStmt.getRightOp();
				AccessPath matchedAP = matchAccessPath(scap.curAP, ifref.getBase(), ifref.getField());
				if (matchedAP != null) {
					scap.curAP = matchedAP.copyWithNewValue(assignStmt.getLeftOp(),
							matchedAP.getFirstFieldType(), true, false);
					matched = true;
				}
			}
		}
		
		return matched ? scap : null;
	}
	
	private AccessPath matchAccessPath(AccessPath curAP, Value base, SootField field) {
		// The base object must match in any case
		if (curAP.getPlainValue() != base)
			return null;
				
		// If we have no field, we may have a taint-all flag
		if (curAP.isLocal()) {
			if (curAP.getTaintSubFields() || field == null)
				return new AccessPath(base, field, true);
		}
		
		// If we have a field, it must match
		if (curAP.isInstanceFieldRef()) {
			if (curAP.getFirstField() == field) {
				return curAP;
			}
			else {
				// Get the bases for this type
				final Collection<BasePair> bases =
						AccessPath.getBaseForType(base.getType());
				if (bases != null) {
					for (BasePair xbase : bases) {
						if (xbase.getFields()[0] == field) {
							// Build the access path against which we have
							// actually matched
							SootField[] cutFields = new SootField
									[curAP.getFieldCount() + xbase.getFields().length];
							Type[] cutFieldTypes = new Type[cutFields.length];
							
							System.arraycopy(xbase.getFields(), 0, cutFields, 0, xbase.getFields().length);
							System.arraycopy(curAP.getFields(), 0, cutFields,
									xbase.getFields().length, curAP.getFieldCount());
							
							System.arraycopy(xbase.getTypes(), 0, cutFieldTypes, 0, xbase.getTypes().length);
							System.arraycopy(curAP.getFieldTypes(), 0, cutFieldTypes,
									xbase.getFields().length, curAP.getFieldCount());

							return new AccessPath(curAP.getPlainValue(),
									cutFields, curAP.getBaseType(), cutFieldTypes,
									curAP.getTaintSubFields(), false, false, curAP.getArrayTaintType());
						}
					}
				}
				
			}
		}
		
		return null;
	}

	/**
	 * Maps an access path from a call site into the respective callee
	 * @param curAP The current access path in the scope of the caller
	 * @param stmt The statement entering the callee
	 * @param callSite The call site corresponding to the callee
	 * @param callee The callee to enter
	 * @return The new callee-side access path if it exists, otherwise null
	 * if the given access path has no corresponding AP in the scope of the
	 * callee.
	 */
	private AccessPath mapAccessPathIntoCallee(AccessPath curAP,
			final Stmt stmt, final Stmt callSite, SootMethod callee,
			boolean isBackwards) {
		// Map the return value into the scope of the callee
		if (stmt instanceof ReturnStmt) {
			ReturnStmt retStmt = (ReturnStmt) stmt;
			if (callSite instanceof AssignStmt
					&& ((AssignStmt) callSite).getLeftOp() == curAP.getPlainValue()) {
				return curAP.copyWithNewValue(retStmt.getOp());
			}
		}
		
		// Map the "this" fields into the callee
		if (!callee.isStatic() && callSite.getInvokeExpr() instanceof InstanceInvokeExpr) {
			InstanceInvokeExpr iiExpr = (InstanceInvokeExpr) callSite.getInvokeExpr();
			if (iiExpr.getBase() == curAP.getPlainValue()) {
				Local thisLocal = callee.getActiveBody().getThisLocal();
				return curAP.copyWithNewValue(thisLocal);
			}
		}
		
		// Map the parameters into the callee. Note that parameters as
		// such cannot return taints from methods, only fields reachable
		// through them. (nope, not true for alias propagation)
		if (!curAP.isLocal() || isBackwards)
			for (int i = 0; i < callSite.getInvokeExpr().getArgCount(); i++) {
				if (callSite.getInvokeExpr().getArg(i) == curAP.getPlainValue()) {
					Local paramLocal = callee.getActiveBody().getParameterLocal(i);
					return curAP.copyWithNewValue(paramLocal);
				}
			}
				
		// Map the parameters back to arguments when we are entering a method
		// during backwards propagation
		if (!curAP.isLocal() && !isBackwards) {
			SootMethod curMethod = cfg.getMethodOf(stmt);
			for (int i = 0; i < callSite.getInvokeExpr().getArgCount(); i++) {
				Local paramLocal = curMethod.getActiveBody().getParameterLocal(i);
				if (paramLocal == curAP.getPlainValue()) {
					return curAP.copyWithNewValue(callSite.getInvokeExpr().getArg(i),
							curMethod.getParameterType(i), false);
				}
			}
		}
		
		return null;
	}
	
	/**
	 * Matches an access path from the scope of the callee back into the scope
	 * of the caller
	 * @param curAP The access path to map
	 * @param stmt The call statement
	 * @param callee The callee from which we return
	 * @return The new access path in the scope of the caller if applicable,
	 * null if there is no corresponding access path in the caller.
	 */
	private AccessPath mapAccessPathBackIntoCaller(AccessPath curAP,
			final Stmt stmt, SootMethod callee) {
		boolean matched = false;
		
		// Static initializers do not modify access paths on call and return
		if (callee.isStaticInitializer())
			return null;
		
		// Special treatment for doPrivileged()
		if (stmt.getInvokeExpr().getMethod().getName().equals("doPrivileged")) {
			if (!callee.isStatic())
				if (curAP.getPlainValue() == callee.getActiveBody().getThisLocal())
					return curAP.copyWithNewValue(stmt.getInvokeExpr().getArg(0));
			
			return null;
		}
		
		// Make sure that we don't end up with a senseless callee
		if (!callee.getSubSignature().equals(stmt.getInvokeExpr().getMethod().getSubSignature())
				&& !isThreadCall(stmt.getInvokeExpr().getMethod(), callee))
			throw new RuntimeException(String.format("Invalid callee on stack. Caller "
					+ "was %s, callee was %s", stmt.getInvokeExpr().getMethod().getSubSignature(),
					callee));
		
		// Map the parameters back into the caller
		for (int i = 0; i < stmt.getInvokeExpr().getArgCount(); i++) {
			Local paramLocal = callee.getActiveBody().getParameterLocal(i);
			if (paramLocal == curAP.getPlainValue()) {
				// We cannot map back to a constant expression at the call site
				if (stmt.getInvokeExpr().getArg(i) instanceof Constant)
					return null;
				curAP = curAP.copyWithNewValue(stmt.getInvokeExpr().getArg(i));
				matched = true;
			}
		}
		
		// Map the "this" local back into the caller
		if (!callee.isStatic() && stmt.getInvokeExpr() instanceof InstanceInvokeExpr) {
			Local thisLocal = callee.getActiveBody().getThisLocal();
			if (thisLocal == curAP.getPlainValue()) {
				curAP = curAP.copyWithNewValue(((InstanceInvokeExpr)
						stmt.getInvokeExpr()).getBase());
				matched = true;
			}
		}
		
		if (matched)
			return curAP;
		
		// Map the return value into the scope of the caller. If we are inside
		// the aliasing part of the path, we might leave methods "the wrong way".
		if (stmt instanceof AssignStmt) {
			AssignStmt assign = (AssignStmt) stmt;
			for (Unit u : callee.getActiveBody().getUnits()) {
				if (u instanceof ReturnStmt) {
					ReturnStmt rStmt = (ReturnStmt) u;
					if (rStmt.getOp() == curAP.getPlainValue()) {
						curAP = curAP.copyWithNewValue(assign.getLeftOp());
						matched = true;
					}
				}
			}
		}
		
		return matched ? curAP : null;
	}
	
	/**
	 * Simplistic check to see whether the given formal callee and actual callee
	 * can be in a thread-start relationship
	 * @param callSite The method at the call site
	 * @param callee The actual callee
	 * @return True if this can be a thread-start call edge, otherwise false
	 */
	private boolean isThreadCall(SootMethod callSite, SootMethod callee) {
		return (callSite.getName().equals("start") && callee.getName().equals("run"));
	}
	
	@Override
	public synchronized SummarySourceContextAndPath clone() {
		final SummarySourceContextAndPath scap = new SummarySourceContextAndPath(
				cfg, getAccessPath(), getStmt(), curAP, true, new ArrayList<>(callees),
				getUserData());
		if (callStack != null)
			scap.callStack = new ArrayList<Stmt>(callStack);
		if (path != null)
			scap.path = new ArrayList<Abstraction>(path);
		return scap;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((callees == null) ? 0 : callees.hashCode());
		result = prime * result + ((cfg == null) ? 0 : cfg.hashCode());
		result = prime * result + ((curAP == null) ? 0 : curAP.hashCode());
		result = prime * result + (isAlias ? 1231 : 1237);
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		SummarySourceContextAndPath other = (SummarySourceContextAndPath) obj;
		if (callees == null) {
			if (other.callees != null)
				return false;
		} else if (!callees.equals(other.callees))
			return false;
		if (cfg == null) {
			if (other.cfg != null)
				return false;
		} else if (!cfg.equals(other.cfg))
			return false;
		if (curAP == null) {
			if (other.curAP != null)
				return false;
		} else if (!curAP.equals(other.curAP))
			return false;
		if (isAlias != other.isAlias)
			return false;
		return true;
	}
	
	public AccessPath getCurrentAccessPath() {
		return this.curAP;
	}
	
	public boolean getIsAlias() {
		return this.isAlias;
	}
		
}
