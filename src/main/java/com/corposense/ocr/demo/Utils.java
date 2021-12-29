package com.corposense.ocr.demo;

import java.util.Locale;


public class Utils {

	public static String getOsName(){
		//by using ROOT we don't specify the language/country.
		return System.getProperty("os.name", "unknown").toLowerCase(Locale.ROOT);
	}
	public static boolean isLinux(){
		return Utils.getOsName().contains("nux");
	}
	public static boolean isWindows(){
		return Utils.getOsName().contains("win");
	}
}
