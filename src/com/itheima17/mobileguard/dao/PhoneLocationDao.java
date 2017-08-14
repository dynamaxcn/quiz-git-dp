package com.itheima17.mobileguard.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.itheima17.mobileguard.domain.CommonBean;
import com.itheima17.mobileguard.domain.CommonTagBean;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class PhoneLocationDao {
	private static final String dbPath = "/data/data/com.itheima17.mobileguard/files/address.db";
	
	private static final String dbCommonPath = "/data/data/com.itheima17.mobileguard/files/commonnum.db";
	
	
	/**
	 * @param tableid
	 *       表名的后缀  如： table1  1就是后缀
	 *       
	 * @return
	 */
	public static List<CommonBean> getAllCommonBeans(int tableid){
	List<CommonBean> datas = new ArrayList<CommonBean>();
		
		SQLiteDatabase database = SQLiteDatabase.openDatabase(dbCommonPath, null, SQLiteDatabase.OPEN_READONLY);
		Cursor cursor = database.rawQuery("select name,number from table" + tableid, 
				null);
		
		while (cursor.moveToNext()) {
			CommonBean bean = new CommonBean();
			bean.setName(cursor.getString(0));
			bean.setNumber(cursor.getString(1));
			datas.add(bean);
			//location = cursor.getString(0);
		}
		cursor.close();
		database.close();
		return datas;
	}
	public static List<CommonTagBean> getAllCommons(){
		List<CommonTagBean> datas = new ArrayList<CommonTagBean>();
		
		SQLiteDatabase database = SQLiteDatabase.openDatabase(dbCommonPath, null, SQLiteDatabase.OPEN_READONLY);
		Cursor cursor = database.rawQuery("select name,idx from classlist", 
				null);
		
		while (cursor.moveToNext()) {
			CommonTagBean bean = new CommonTagBean();
			bean.setTypeName(cursor.getString(0));
			bean.setTable_id(cursor.getInt(1));
			datas.add(bean);
			//location = cursor.getString(0);
		}
		cursor.close();
		database.close();
		return datas;
	}
	
	/**
	 * @param number
	 *            号码 手机号 固定号码
	 * @return 归宿地信息
	 */
	public static String getLocation(String number) {
		String location = "";

		// 判断是否是移动号码
		Pattern p = Pattern.compile("1[35478]{1}[0-9]{9}");
		Matcher m = p.matcher(number);
		boolean b = m.matches();
		if (b) {
			// 手机号码
			location = getMobile(number);
		} else {
			// 固定号码
			location = getPhone(number);
		}
		return location;
	}

	/**
	 * @param number
	 *            移动号码
	 * @return 归属地信息
	 */
	private static String getMobile(String number) {
		//select location from data2 where id = (select outkey from data1 where id = 1343056);
		String location = "未知";
		//获取数据库
		SQLiteDatabase database = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READONLY);
		Cursor cursor = database.rawQuery("select location from data2 where id = (select outkey from data1 where id = ?)", 
				new String[]{number.substring(0, 7)});
		
		if (cursor.moveToNext()) {
			location = cursor.getString(0);
		}
		cursor.close();
		database.close();
		return location;
	}

	/**
	 * @param number
	 *            固定号码 
	 *            075588888888
	 *            02011111111
	 *            03915555555
	 *            01011111111
	 *            
	 *            1或2开头 2位区号
	 *            否则 3位区号
	 * @return 归属地信息
	 */
	private static String getPhone(String number) {
		
		if (number.charAt(1) == '1' || number.charAt(1) == '2') {
			//1或2开头 2位区号
			number = number.substring(1,3);
		} else {
			//3位区号
			number = number.substring(1,4);
		}
		
		
		String location = "未知";
		//获取数据库
		SQLiteDatabase database = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READONLY);
		Cursor cursor = database.rawQuery("select location from data2 where area = ? ", 
				new String[]{number});
		
		if (cursor.moveToNext()) {
			location = cursor.getString(0);
			location = location.substring(0,location.length() - 2);
		}
		
		cursor.close();
		database.close();
		
		return location;
		
	}

}
