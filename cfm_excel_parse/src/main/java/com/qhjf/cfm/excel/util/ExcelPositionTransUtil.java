package com.qhjf.cfm.excel.util;

/**
 * excel定位转换
 * @author CHT
 *
 */
public class ExcelPositionTransUtil {

	/**
	 * 
	 * @param position
	 * @return
	 */
	public static int positionTransToRow(String position){
		String row = position.substring(1);
		return Integer.parseInt(row) - 1;
	}
	/**
	 * 
	 * @param position
	 * @return
	 */
	public static int positionTransToColumn(String position){
		char ch = position.toUpperCase().charAt(0);
		return ch - 'A';
	}
	
	public static void main(String[] args) {
		String s = "B1";
		System.out.println(String.format("%s为第%s行", s, positionTransToRow(s)));
		System.out.println(String.format("%s为第%s列", s, positionTransToColumn(s)));
	}
}
