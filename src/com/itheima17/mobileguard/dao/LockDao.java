package com.itheima17.mobileguard.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.itheima17.mobileguard.db.LockDB;

/**
 * @author Administrator
 * @date 2015-12-4
 * @pagename com.itheima17.mobileguard.dao
 * @desc 程序锁的dao层

 * @svn author $Author: goudan $
 * @svn date $Date: 2015-12-04 16:56:07 +0800 (周五, 04 十二月 2015) $
 * @Id  $Id: LockDao.java 98 2015-12-04 08:56:07Z goudan $
 * @version $Rev: 98 $
 * @url $URL: https://188.188.2.100/svn/itheima17/trunk/MobileGuard/src/com/itheima17/mobileguard/dao/LockDao.java $ 
 * 
 */
public class LockDao {
	
	public static final Uri uri = Uri.parse("content://itheima17.locktb");
	private LockDB mLockDB;
	private Context context ;
	public LockDao(Context context){
		this.context = context;
		mLockDB = new LockDB(context);
	}
	
	/**
	 * 加锁
	 * @param packName
	 *    app的包名
	 */
	public void addLock(String packName){
		SQLiteDatabase database = mLockDB.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("packname", packName);
		database.insert("locktb", null, values);
		database.close();
		
		//通知内容观察者
		context.getContentResolver().notifyChange(uri, null);
		
	}
	
	/**
	 * 解锁
	 * @param packName
	 */
	public void removeLock(String packName){
		SQLiteDatabase database = mLockDB.getWritableDatabase();
		database.delete("locktb", "packname=?", new String[]{packName});
		database.close();
		//通知内容观察者
		context.getContentResolver().notifyChange(uri, null);
	}
	
	public boolean isLock(String packName){
		boolean isLock = false;
		SQLiteDatabase database = mLockDB.getReadableDatabase();
		Cursor cursor = database.rawQuery("select 1 from locktb where packname=?", new String[]{packName});
		if (cursor.moveToNext()) {
			isLock = true;
		}
		
		cursor.close();
		database.close();
		
		return isLock;
	}
	
	public List<String> getAllLockPacks(){
		List<String> datas = new ArrayList<String>();
		SQLiteDatabase database = mLockDB.getReadableDatabase();
		Cursor cursor = database.rawQuery("select packname from locktb", null);
		
		while(cursor.moveToNext()) {
			datas.add(cursor.getString(0));
		}
		
		cursor.close();
		database.close();
		return datas;
	}
}
