package net.whydah.sso.util;

import java.io.UnsupportedEncodingException;

public class StringConv {

	public static String UTF8(byte[] bytes)
	{
		try {
			
			if(bytes!=null)
			{	
				return new String(bytes,"UTF8");
			
			} else {
				
				return "";
			}
			
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return "";
	}
	public static String UTF8(String str)
	{
		return new String(str);
	}	

	public static byte[] getBytes (String str)
	{
		try {
			
			return str.getBytes("UTF8");
		
		} catch (UnsupportedEncodingException e) {
		
			e.printStackTrace();
		}
		return null;		
	}
}