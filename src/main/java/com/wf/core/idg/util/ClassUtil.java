package com.wf.core.idg.util;

import java.io.InputStream;

/**
 * 类工具。提供与Class或ClassLoader相关的实现
 * @author Fe
 * @version 2015-10-26 <strong>1.2</strong>增强对desc（描述符）的支持
 * @version 2014-06-17 <strong>1.1</strong>增加二进制名称的转换
 * @version 2014-02-22 <strong><s>1.0</s></strong>
 */
public class ClassUtil {
	/**
	 * 从指定的路径读取一个资源文件。可以读取到jar包中的资源文件。
	 * @param classLoaderClass 读取时提供加载器的类
	 * @param path 路径
	 * @return 一个InputStream流
	 */
	public static InputStream getResourceAsStream(Class<?> classLoaderClass, String path) {
		return classLoaderClass.getClassLoader().getResourceAsStream(path);
	}

	/**
	 * 从指定的路径读取一个资源文件(使用当前类所在包的路径)。可以读取到jar包中的资源文件。
	 * 
	 * @param classLoaderClass 读取时提供加载器的类
	 * @param path 增加了包路径的路径
	 * @return 一个InputStream流
	 */
	public static InputStream getResourceAsStreamUseRelativeClassPath(
            Class<?> classLoaderClass, String path) {
		String path0 = addRelativeClassPath(classLoaderClass, path);
		return getResourceAsStream(classLoaderClass, path0);
	}

	/**
	 * 对path增加类所在包路径
	 */
	public static String addRelativeClassPath(Class<?> clazz, String path) {
		String packageName = clazz.getPackage().getName();
		packageName = packageName.replace(".", "/");
		if (!path.startsWith("/"))
			packageName = packageName + "/" + path;
		else
			packageName = packageName + path;
		return packageName;
	}
}
