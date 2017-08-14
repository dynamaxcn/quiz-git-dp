package com.itheima17.mobileguard.db;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @author Administrator
 * @date 2015-11-25
 * @pagename com.itheima17.mobileguard.db
 * @desc 黑名单数据库的创建

 * @svn author $Author: goudan $
 * @svn date $Date: 2015-11-25 11:25:32 +0800 (周三, 25 十一月 2015) $
 * @Id  $Id: BlackDB.java 36 2015-11-25 03:25:32Z goudan $
 * @version $Rev: 36 $
 * @url $URL: https://188.188.2.100/svn/itheima17/trunk/MobileGuard/src/com/itheima17/mobileguard/db/BlackDB.java $ 
 * 
 */
public class BlackDB extends SQLiteOpenHelper {
	public static final int MODEL_SMS = 1 << 0;  // 01
	public static final int MODEL_PHONE = 1 << 1; // 10
	public static final int MODEL_ALL = MODEL_SMS | MODEL_PHONE;//11
	public static final int MODEL_NONE = 0;//00
	public static final String BLACK_TB = "black_tb";
	public static final String NUMBER = "number";
	public static final String MODEL= "model";
	
	private static int versionCode = 3;
	
	/**
	 * 构造函数
	 * @param context
	 * @param name
	 * @param factory
	 * @param version
	 * @param errorHandler
	 */
	public BlackDB(Context context) {
		
		super(context, "blackdb", null, versionCode);
		// TODO Auto-generated constructor stub
	}

	

	@Override
	public void onCreate(SQLiteDatabase db) {
		// 只执行一次
		// 创建表
		db.execSQL("create table black_tb(_id integer primary key autoincrement,number text,model integer)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		//版本号发生变化的回调
		db.execSQL("drop table black_tb");
		onCreate(db);
	}

}
