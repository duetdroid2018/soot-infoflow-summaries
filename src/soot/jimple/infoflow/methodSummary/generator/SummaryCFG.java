package soot.jimple.infoflow.methodSummary.generator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.Unit;
import soot.jimple.InvokeExpr;
import soot.jimple.Stmt;
import soot.jimple.infoflow.solver.InfoflowCFG;
import soot.jimple.toolkits.ide.icfg.JimpleBasedInterproceduralCFG;

public class SummaryCFG extends InfoflowCFG {
	
	private static final Object methodLoadLock = new Object();

	@Override
	public Collection<SootMethod> getCalleesOfCallAt(Unit u) {
		Collection<SootMethod> callees = super.getCalleesOfCallAt(u);
		if (!callees.isEmpty())
			return callees;
		
		// We need an invoke expression
		Stmt s = (Stmt) u;
		if (!s.containsInvokeExpr())
			return Collections.emptySet();
		
		synchronized (methodLoadLock) {
			// The original algorithm did not find any callees, so we need to
			// perform a conservative over-approximation.
			InvokeExpr iexpr = s.getInvokeExpr();
			final SootClass declaringClass = iexpr.getMethod().getDeclaringClass();
	
			List<SootClass> workList = new ArrayList<>();
			if (declaringClass.isInterface()) {
				workList.addAll(Scene.v().getFastHierarchy()
						.getAllImplementersOfInterface(declaringClass));
			}
			else
				workList.add(declaringClass);
			
			// Get all subclasses of our seeds
			Set<SootMethod> receivers = new HashSet<>();
			while (!workList.isEmpty()) {
				SootClass sc = workList.remove(0);
				
				SootMethod scMethod = sc.getMethodUnsafe(iexpr.getMethod().getSubSignature());
				if (scMethod != null) {
					// Load the body for not running into issues later on
					delayLoadMethod(scMethod);
					receivers.add(scMethod);
					System.out.println("Delay-loaded " + sc);
				}
				
				workList.addAll(Scene.v().getFastHierarchy().getSubclassesOf(sc));
			}
			return receivers;
		}
	}
	
	private void delayLoadMethod(SootMethod scMethod) {
		if (!scMethod.isConcrete())
			return;

		final SootClass declaringClass = scMethod.getDeclaringClass();
		if (declaringClass.isPhantom())
			return;
		
		Scene.v().forceResolve(declaringClass.getName(), SootClass.BODIES);
		if (declaringClass.isPhantom())
			return;
			
		scMethod.retrieveActiveBody();
		if (delegate instanceof JimpleBasedInterproceduralCFG)
			((JimpleBasedInterproceduralCFG) delegate).initializeUnitToOwner(scMethod);
	}

}
