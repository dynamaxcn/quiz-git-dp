package com.itheima17.mobileguard.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * @author Administrator
 * @date 2015-11-22
 * @pagename com.itheima17.mobileguard.utils
 * @desc SharedPreferences的工具类

 * @svn author $Author: goudan $
 * @svn date $Date: 2015-11-30 10:48:34 +0800 (周一, 30 十一月 2015) $
 * @Id  $Id: SPUtils.java 68 2015-11-30 02:48:34Z goudan $
 * @version $Rev: 68 $
 * @url $URL: https://188.188.2.100/svn/itheima17/trunk/MobileGuard/src/com/itheima17/mobileguard/utils/SPUtils.java $ 
 * 
 */
public class SPUtils {
	public static float getFloat(Context context,String key,float defValue){
		SharedPreferences preferences = context.getSharedPreferences(MyContains.SPFILENAME, Context.MODE_PRIVATE);
		return preferences.getFloat(key, defValue);
	}
	/**
	 * 保存字符串型数据到sp中
	 * @param context
	 * @param key
	 * @param value
	 */
	public static void putFloat(Context context,String key,float value){
		SharedPreferences preferences = context.getSharedPreferences(MyContains.SPFILENAME, Context.MODE_PRIVATE);
		preferences.edit().putFloat(key, value).commit();
	}
	/**
	 * 保存字符串型数据到sp中
	 * @param context
	 * @param key
	 * @param value
	 */
	public static void putString(Context context,String key,String value){
		SharedPreferences preferences = context.getSharedPreferences(MyContains.SPFILENAME, Context.MODE_PRIVATE);
		preferences.edit().putString(key, value).commit();
	}
	
	/**
	 * 从Sp中获取string型的数据
	 * @param context
	 * @param key
	 * @param defValue
	 * @return
	 */
	public static String getString(Context context,String key,String defValue){
		SharedPreferences preferences = context.getSharedPreferences(MyContains.SPFILENAME, Context.MODE_PRIVATE);
		return preferences.getString(key, defValue);
	}
	
	/**
	 * 保存字符串型数据到sp中
	 * @param context
	 * @param key
	 * @param value
	 */
	public static void putBoolean(Context context,String key,boolean value){
		SharedPreferences preferences = context.getSharedPreferences(MyContains.SPFILENAME, Context.MODE_PRIVATE);
		preferences.edit().putBoolean(key, value).commit();
	}
	
	/**
	 * 从Sp中获取boolean型的数据
	 * @param context
	 * @param key
	 * @param defValue
	 * @return
	 */
	public static Boolean getBoolean(Context context,String key,boolean defValue){
		SharedPreferences preferences = context.getSharedPreferences(MyContains.SPFILENAME, Context.MODE_PRIVATE);
		return preferences.getBoolean(key, defValue);
	}
	
	
}
