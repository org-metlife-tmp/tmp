package com.qhjf.cfm.utils;

import java.util.UUID;

public class UUIDUtil {
	
	public static String getUUID(String name){

		UUID uuid=UUID.nameUUIDFromBytes(name.getBytes());
		String str = uuid.toString();
		String uuidStr=str.replace("-","");
		return uuidStr; 

    }
	
	public static String getUUIDWith(String name){
		UUID uuid=UUID.nameUUIDFromBytes(name.getBytes());
		String str = uuid.toString();
		return str;
	}

	public static void main(String[] args){
		String str = "2018-12-26";
		System.out.println(getUUID(str));
	}
}
