package com.qhjf.cfm.excel.analyze.validator;

import com.qhjf.cfm.excel.analyze.validator.impl.DefaultValidatorService;

/**
 * 校验器工厂
 * @author CHT
 *
 */
public class ValidatorServiceFactory {

	public static IValidatorService createValidatorService(String type){
		switch (type) {
		default:
			return new DefaultValidatorService();
		}
	}
}
