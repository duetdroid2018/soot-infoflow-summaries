package soot.jimple.infoflow.methodSummary.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

public class ClassFileInformation {
	/**
	 * 
	 * @return all method signatures of class clazz in a soot processable format
	 */
	public static List<String> getMethodSignature(Class<?> clazz, Class<?> startClazz) {
		List<String> res = new LinkedList<String>();
		for (Class<?> c : clazz.getDeclaredClasses()) {
			res.addAll(getMethodSignature(c, startClazz));
		}
		Method[] methods = clazz.getDeclaredMethods();

		//String className = clazz.getName(); // clazz.getCanonicalName();
		for (Method m : methods) {

			res.add(getMethodSig(m, clazz));
		}

		Constructor<?>[] constructors = clazz.getConstructors();
		for (Constructor<?> c : constructors) {
			res.add(getConstructorSig(c));
		}
		return res;
	}

	public static String getMethodSig(Method m) {
		return getMethodSig(m, m.getDeclaringClass());
	}

	public static String getMethodSig(Method m, Class<?> c) {

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

	public static String getType(Class<?> para) {
		String res = "";
		if (para.isPrimitive())
			res = para.getSimpleName();
		else if (para.isArray()) {
			//quick hack
			if(para.getName().replaceAll(";|L|\\[", "").length() < 2)
				res = para.getSimpleName();
			else
				res = para.getName().replaceAll(";|L|\\[", "") + "[]";
		} else {
			res = para.getName();
		}
		return res;
	}

	public static String getConstructorSig(Constructor<?> c) {
		String para = "";
		for (int i = 0; i < c.getParameterTypes().length; i++) {
			if (i < c.getParameterTypes().length - 1) {
				para = para + c.getParameterTypes()[i].getName() + ",";
			} else {
				para = para + c.getParameterTypes()[i].getName();
			}
		}
		String method = "<init>(" + para + ")";
		return "<" + c.getDeclaringClass().getName() + ": void " + method + ">";
	}

	/**
	 * 
	 * @return all global vars of class clazz
	 */
	public static List<String> getGlobalVars(Class<?> clazz) {
		List<String> res = new LinkedList<String>();
		Field[] vars = clazz.getDeclaredFields();
		for (Field f : vars) {
			res.add("<" + clazz.getCanonicalName() + ": " + f.getType().getCanonicalName() + " " + f.getName() + ">");
		}
		return res;
	}

}
