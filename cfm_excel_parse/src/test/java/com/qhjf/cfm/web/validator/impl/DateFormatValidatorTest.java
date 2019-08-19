package com.qhjf.cfm.web.validator.impl;

import com.qhjf.cfm.excel.config.validator.impl.DateFormatValidator;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 日期校验器测试
 * @author CHT
 *
 */
public class DateFormatValidatorTest {
	
	@Test
	public void test1(){
		DateFormatValidator v = new DateFormatValidator();
		v.doValidat(new SimpleDateFormat("yyyy-MM-dd HH:ss:mm").format(new Date()));
		System.out.println(v.getErrorMessage());
	}
	@Test
	public void test2(){
		DateFormatValidator v = new DateFormatValidator();
		v.doValidat("1234-11-22");
		System.out.println(v.getErrorMessage());
	}
	@Test
	public void test3(){
		DateFormatValidator v = new DateFormatValidator();
		v.doValidat("99:11:33");
		System.out.println(v.getErrorMessage());
	}
	@Test
	public void test4(){
		DateFormatValidator v = new DateFormatValidator();
		v.doValidat("1234-11-22          11:11:33");
		System.out.println(v.getErrorMessage());
	}
	@Test
	public void test5(){
		String s = "1234-11-22          11:11:33";
		String r = "\\s+";
		s = s.replaceAll(r, " ");
		System.out.println(s);
	}
}
