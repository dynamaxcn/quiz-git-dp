package com.itheima17.mobileguard.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.itheima17.mobileguard.db.BlackDB;
import com.itheima17.mobileguard.domain.BlackBean;

/**
 * @author Administrator
 * @date 2015-11-25
 * @pagename com.itheima17.mobileguard.dao
 * @desc 黑名单数据库的业务类封装
 * 
 * @svn author $Author: goudan $
 * @svn date $Date: 2015-12-03 16:41:16 +0800 (周四, 03 十二月 2015) $
 * @Id $Id: AntiVirusDao.java 90 2015-12-03 08:41:16Z goudan $
 * @version $Rev: 90 $
 * @url $URL:
 *      https://188.188.2.100/svn/itheima17/trunk/MobileGuard/src/com/itheima17
 *      /mobileguard/dao/BlackDao.java $
 * 
 */
public class AntiVirusDao {
	private static final String dbPath = "/data/data/com.itheima17.mobileguard/files/antivirus.db";

	public static void updateVersion(String version) {
		SQLiteDatabase database = SQLiteDatabase.openDatabase(dbPath, null,
				SQLiteDatabase.OPEN_READWRITE);

		ContentValues values = new ContentValues();
		values.put("subcnt", Integer.parseInt(version));
		database.update("version", values, null, null);
		
		database.close();
	}

	/**
	 * @return 当前病毒库的版本
	 */
	public static String getVirusVersion() {
		String version = "";
		SQLiteDatabase database = SQLiteDatabase.openDatabase(dbPath, null,
				SQLiteDatabase.OPEN_READONLY);
		Cursor cursor = database.rawQuery("select subcnt from version", null);
		if (cursor.moveToNext()) {
			version = cursor.getInt(0) + "";
		}

		cursor.close();
		database.close();

		return version;
	}

	/**
	 * 给病毒库插入一条病毒
	 * 
	 * @param md5
	 * @param desc
	 */
	public static void insert(String md5, String desc) {
		SQLiteDatabase database = SQLiteDatabase.openDatabase(dbPath, null,
				SQLiteDatabase.OPEN_READWRITE);

		ContentValues values = new ContentValues();
		values.put("md5", md5);
		values.put("type", 6);
		values.put("name", "Android.Adware.AirAD.a");
		values.put("desc", desc);
		database.insert("datable", null, values);

		database.close();

	}

	/**
	 * 检测病毒
	 * 
	 * @param md5
	 *            文件的MD5值
	 * @return 是否是病毒
	 */
	public static boolean isVirus(String md5) {
		boolean res = false;
		SQLiteDatabase database = SQLiteDatabase.openDatabase(dbPath, null,
				SQLiteDatabase.OPEN_READONLY);
		Cursor cursor = database.rawQuery("select 1 from datable where md5=?",
				new String[] { md5 });
		if (cursor.moveToNext()) {
			res = true;
		}

		cursor.close();
		database.close();
		return res;

	}
}
