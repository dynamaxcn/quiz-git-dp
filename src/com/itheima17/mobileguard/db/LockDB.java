package com.itheima17.mobileguard.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @author Administrator
 * @date 2015-12-4
 * @pagename com.itheima17.mobileguard.db
 * @desc 程序锁数据库

 * @svn author $Author: goudan $
 * @svn date $Date: 2015-12-04 11:32:56 +0800 (周五, 04 十二月 2015) $
 * @Id  $Id: LockDB.java 95 2015-12-04 03:32:56Z goudan $
 * @version $Rev: 95 $
 * @url $URL: https://188.188.2.100/svn/itheima17/trunk/MobileGuard/src/com/itheima17/mobileguard/db/LockDB.java $ 
 * 
 */
public class LockDB extends SQLiteOpenHelper {

	public LockDB(Context context) {
		super(context, "lock.db", null, 2);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL("create table locktb(_id integer primary key autoincrement,packname text)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		db.execSQL("drop table locktb");
		onCreate(db);
	}

}
