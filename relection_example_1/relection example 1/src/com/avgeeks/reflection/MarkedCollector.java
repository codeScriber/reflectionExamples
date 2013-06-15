package com.avgeeks.reflection;

import java.io.File;
import java.util.ArrayList;

public class MarkedCollector {
	
	public static void main(String[] args) throws Exception{
		File f = new File(System.getProperty("java.class.path", null));
		if( f != null && f.isDirectory()){
			ArrayList<Object> list = new ArrayList<Object>(10);
			getClassesInPathWithAnnotations(list, f.getPath(), "");
			System.out.println("path was:" + f.getPath() + ", " + list.size());
		}
	}
	
	public static void getClassesInPathWithAnnotations(ArrayList<Object> list, String path, String pkg){
		if( path != null ){
			File dir = new File(path);
			//if not it's actually a very miserable bug...
			if( dir.isDirectory() ){
				File[] files = dir.listFiles();
				for(File file : files){
					if(file.isDirectory() ){
						getClassesInPathWithAnnotations(list, file.getAbsolutePath(), pkg + file.getName() + ".");
					}else if( file.getName().endsWith(".class") ){
						try{
							System.out.println("trying to get class for name:" + pkg + "." + file.getName().replace(".class", ""));
							Class<?> clazz = Class.forName(pkg + file.getName().replace(".class", ""));
							if( clazz != null && clazz.getAnnotation(MarkClass.class) != null){
								Object instantiatedClass = clazz.newInstance();
								list.add(instantiatedClass);
							}
						}catch(Exception e){
							e.printStackTrace();
						}
					}
				}
			}
		}
	}
}
