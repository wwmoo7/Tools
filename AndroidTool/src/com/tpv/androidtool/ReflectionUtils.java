package com.tpv.androidtool;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class ReflectionUtils {

	/**
	 * 
	 * @param classname
	 * @return Object
	 */

	public static Object getObjByClassName(String classname) {
		Object obj = null;
		if (classname != null) {
			Class<?> a;
			try {
				a = Class.forName(classname);
				obj = a.newInstance();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return obj;
	}

	public static Object getObjByClassNameAndParameter(String classname,
			Object[] parameter) {
		Object obj = null;
		if (classname != null) {
			try {
				Class<?> a = Class.forName(classname);
				Constructor<?> con = a
						.getConstructor(getParameterClass(parameter));
				obj = con.newInstance(parameter);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return obj;

	}

	/**
	 * @param className
	 * @param methodName
	 * 
	 * @return Object
	 */

	public static Object invokeMethod(String className, String methodName) {
		return invokeMethodWithObjHasParame(className,
				getObjByClassName(className), methodName, new Object[0]);
	}

	/**
	 * 用类名反射调用它的某个方法(一般针对工具类或者service)(有参数)
	 * 
	 * @param className
	 * @param methodName
	 * @param parameter
	 * @return Object
	 */

	public static Object invokeMethodHasParame(String className,
			String methodName, Object[] parameter) {
		return invokeMethodWithObjHasParame(className,
				getObjByClassName(className), methodName, parameter);

	}

	/**
	 * 用对象反射调用它的某个方法(没有参数的方法)
	 * 
	 * @param className
	 * @param obj
	 * @param methodName
	 * @return
	 */

	public static Object invokeMethodWithObj(String className, Object obj,
			String methodName) {
		return invokeMethodWithObjHasParame(className, obj, methodName,
				new Object[0]);
	}

	/**
	 * 用对象反射调用它的某个方法(有参数的方法)
	 * 
	 * @param className
	 * @param obj
	 * @param methodName
	 * @param parameter
	 * @return Object
	 */

	public static Object invokeMethodWithObjHasParame(String className,
			Object obj, String methodName,
			Object[] parameter) {
		return invokeMethodWithObjHasSpecialParame(className, obj, methodName,
				parameter, getParameterClass(parameter));
	}

	/**
	 * 获取参数列表的class对象
	 * 
	 * @param parameter
	 * @return Class[]
	 */

	private static Class<?>[] getParameterClass(Object[] parameter) {
		Class<?>[] methodParameters = null;
		if (parameter != null && parameter.length > 0) {
			methodParameters = new Class[parameter.length];
			for (int i = 0; i < parameter.length; i++) {
				methodParameters[i] = parameter[i].getClass();
			}
		}
		return methodParameters;

	}

	/**
	 * 用对象反射调用它的某个方法(指定参数类型的方法)
	 * @param className
	 * @param obj
	 * @param methodName
	 * @param parameter
	 * @param methodParameters
	 * @return Object
	 */

	public static Object invokeMethodWithObjHasSpecialParame(String className,
			Object obj, String methodName,
			Object[] parameter, Class<?>[] methodParameters) {

		Object object = null;
		try {
			Method method = Class.forName(className).getMethod(
					methodName.trim(), methodParameters);
			object = method.invoke(obj, parameter);
		}

		catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		catch (SecurityException e) {
			e.printStackTrace();
		}
		catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
		catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return object;

	}

	/**
	 * 反射获取一个类的方法信息 包括参数,方法名,返回类型 
	 * @param className
	 * @return List<String>
	 */

	public static List<String> getMethodMsg(String className) {
		List<String> retValue = new ArrayList<String>();
		try {
			// 通过getMethods得到类中包含的方法
			Class<?> myClass = Class.forName(className);
			Method m[] = myClass.getDeclaredMethods();
			for (int i = 0; i < m.length; i++) {
				String meth = m[i].toString();
				// 截取出所有的参数,参数以,形式分割
				meth = meth.substring(meth.indexOf("(") + 1, meth.indexOf(")"));
				// ret由3部分构成：参数;方法名;返回类型
				String ret = meth + ";" + m[i].getName() + ";"
						+ m[i].getReturnType();
				retValue.add(ret);
			}
			return retValue;
		}
		catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return retValue;
	}
}