package com.avgeeks.reflection.packageB;

import com.avgeeks.reflection.MarkClass;

@MarkClass
public class MyBclassMarked {
	
	public MyBclassMarked(){
		System.out.println("created class instance of:" + this.getClass().getCanonicalName());
	}

}
