package com.avgeeks.reflection.packageA;


public class MyAclassUnMarked {
	
	public MyAclassUnMarked(){
		System.out.println("created class instance of:" + this.getClass().getCanonicalName());
	}

}
