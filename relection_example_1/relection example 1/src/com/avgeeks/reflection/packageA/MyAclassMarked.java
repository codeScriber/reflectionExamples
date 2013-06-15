package com.avgeeks.reflection.packageA;

import com.avgeeks.reflection.MarkClass;

@MarkClass
public class MyAclassMarked {
	
	public MyAclassMarked(){
		System.out.println("created class instance of:" + this.getClass().getCanonicalName());
	}

}
