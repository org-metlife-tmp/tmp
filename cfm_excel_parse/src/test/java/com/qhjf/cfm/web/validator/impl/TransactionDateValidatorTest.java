package com.qhjf.cfm.web.validator.impl;

import org.junit.Test;

import com.qhjf.cfm.excel.config.validator.impl.TransactionDateValidator;

public class TransactionDateValidatorTest {
	
	@Test
	public void test1(){
		TransactionDateValidator v = new TransactionDateValidator();
		v.doValidat("2018-09-26");
		System.out.println(v.getErrorMessage());
	}
	@Test
	public void test2(){
		TransactionDateValidator v = new TransactionDateValidator();
		v.doValidat("2018-09-25");
		System.out.println(v.getErrorMessage());
	}
	@Test
	public void test3(){
		TransactionDateValidator v = new TransactionDateValidator();
		v.doValidat("2018-04-30");
		System.out.println(v.getErrorMessage());
	}
	@Test
	public void test4(){
		TransactionDateValidator v = new TransactionDateValidator();
		v.doValidat("1998-04-29");
		System.out.println(v.getErrorMessage());
	}
}
