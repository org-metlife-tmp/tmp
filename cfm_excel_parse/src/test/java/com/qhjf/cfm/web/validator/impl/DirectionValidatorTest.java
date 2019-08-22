package com.qhjf.cfm.web.validator.impl;

import com.qhjf.cfm.excel.config.validator.impl.DirectionValidator;
import org.junit.Test;

public class DirectionValidatorTest {

	@Test
	public void test1(){
		DirectionValidator v = new DirectionValidator();
		Object o = 1;
		v.doValidat(o);
		System.out.println(v.getErrorMessage());
	}
	@Test
	public void test2(){
		DirectionValidator v = new DirectionValidator();
		Object o = 2;
		v.doValidat(o);
		System.out.println(v.getErrorMessage());
	}
	@Test
	public void test3(){
		DirectionValidator v = new DirectionValidator();
		Object o = 3;
		v.doValidat(o);
		System.out.println(v.getErrorMessage());
	}
}
