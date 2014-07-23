package soot.jimple.infoflow.methodSummary.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

/**
 * Helper class that extract soot fields and methods signatures from
 * java.lang.class.
 * 
 * @author meD
 * 
 */
public class ClassFileInformation {
	/**
	 * 
	 * @return all method signatures of class clazz in a soot processable format
	 */
	public static List<String> getMethodSignatures(Class<?> clazz, boolean includingPrentClasses) {
		List<String> res = new LinkedList<String>();
		if (includingPrentClasses) {
//			for (Class<?> c : clazz.getDeclaredClasses()) {
//				res.addAll(getMethodSignatures(c, includingPrentClasses));
//			}
			if(clazz.getSuperclass() != null && !clazz.getSuperclass().getName().contains("java.lang.Object"))
				res.addAll(getMethodSignatures(clazz.getSuperclass(), includingPrentClasses));
		}
		Method[] methods = clazz.getDeclaredMethods();

		//String className = clazz.getName(); // clazz.getCanonicalName();
		for (Method m : methods) {
			res.add(getMethodSignatures(m, clazz));
		}

		Constructor<?>[] constructors = clazz.getConstructors();
		for (Constructor<?> c : constructors) {
			res.add(getConstructorSig(c));
		}
		return res;
	}

	public static String getMethodSignature(Method m) {
		return getMethodSignatures(m, m.getDeclaringClass());
	}

	public static String getMethodSignatures(Method m, Class<?> c) {
		String className = c.getName(); // clazz.getCanonicalName();

		String para = "";
		for (int i = 0; i < m.getParameterTypes().length; i++) {
			if (i < m.getParameterTypes().length - 1) {
				para = para + getType(m.getParameterTypes()[i]) + ",";

			} else {
				para = para + getType(m.getParameterTypes()[i]);
			}
		}

		String method = getType(m.getReturnType()) + " " + m.getName() + "(" + para + ")";
		return "<" + className + ": " + method + ">";
	}

	public static String getConstructorSignature(Constructor<?> cons) {
		return getConstructorSignature(cons, cons.getDeclaringClass());
	}

	public static String getConstructorSignature(Constructor<?> cons, Class<?> c) {
		String className = c.getName(); // clazz.getCanonicalName();

		String para = "";
		for (int i = 0; i < cons.getParameterTypes().length; i++) {
			if (i < cons.getParameterTypes().length - 1) {
				para = para + getType(cons.getParameterTypes()[i]) + ",";

			} else {
				para = para + getType(cons.getParameterTypes()[i]);
			}
		}

		String method = "void <init>(" + para + ")";
		return "<" + className + ": " + method + ">";
	}

	public static String getType(Class<?> parameter) {
		String res = "";
		if (parameter.isPrimitive())
			res = parameter.getSimpleName();
		else if (parameter.isArray()) {
			//quick hack
			if (parameter.getName().replaceAll(";|L|\\[", "").length() < 2)
				res = parameter.getSimpleName();
			else
				res = parameter.getName().replaceAll(";|L|\\[", "") + "[]";
		} else {
			res = parameter.getName();
		}
		return res;
	}

	public static String getConstructorSig(Constructor<?> c) {
		String para = "";
		for (int i = 0; i < c.getParameterTypes().length; i++) {
			if (i < c.getParameterTypes().length - 1) {
				para = para + getType(c.getParameterTypes()[i]) + ",";
			} else {
				para = para + getType(c.getParameterTypes()[i]);
			}
		}
		String method = "<init>(" + para + ")";
		return "<" + c.getDeclaringClass().getName() + ": void " + method + ">";
	}

	/**
	 * 
	 * @return The field signature of all Fields in a clazz.
	 */
	public static List<String> getFields(Class<?> clazz) {
		List<String> res = new LinkedList<String>();
		Field[] vars = clazz.getDeclaredFields();
		for (Field f : vars) {
			res.add("<" + clazz.getCanonicalName() + ": " + f.getType().getCanonicalName() + " " + f.getName() + ">");
		}
		return res;
	}

}
