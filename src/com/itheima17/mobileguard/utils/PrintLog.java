package com.itheima17.mobileguard.utils;

public class PrintLog {
	private static boolean isPrint = true;
	public static void print(Object mess){
		if (isPrint)
			System.out.println(mess);
	}
}
