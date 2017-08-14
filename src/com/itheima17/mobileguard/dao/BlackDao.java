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
 * @svn date $Date: 2015-11-30 15:54:20 +0800 (周一, 30 十一月 2015) $
 * @Id $Id: BlackDao.java 74 2015-11-30 07:54:20Z goudan $
 * @version $Rev: 74 $
 * @url $URL:
 *      https://188.188.2.100/svn/itheima17/trunk/MobileGuard/src/com/itheima17
 *      /mobileguard/dao/BlackDao.java $
 * 
 */
public class BlackDao {
	private BlackDB mBlackDB;
	
	private int pageNumbers = 20;//每次加载多少条数据
	
	public int getCounts(){
		int counts = 0;
		SQLiteDatabase database = mBlackDB.getWritableDatabase();
		Cursor cursor = database.rawQuery("select count(1) from black_tb", null);
		cursor.moveToNext();
		counts = cursor.getInt(0);
		
		cursor.close();
		database.close();
		return counts;
		
	}
	public List<BlackBean> getMoreData(int startRow){
		List<BlackBean> res = new ArrayList<BlackBean>();
		SQLiteDatabase database = mBlackDB.getWritableDatabase();

		// 取所有数据
		Cursor cursor = database.rawQuery("select number,model from black_tb limit ?,?", 
				new String[]{startRow + "",pageNumbers + ""});
		BlackBean bean = null;
		while (cursor.moveToNext()) {// ite.hasNext() ite.next()
			bean = new BlackBean();
			// 获取数据
			bean.setNumber(cursor.getString(0));
			bean.setModel(cursor.getInt(1));

			// 添加数据
			res.add(bean);
		}

		cursor.close();
		database.close();

		return res;
	}

	public BlackDao(Context context) {
		mBlackDB = new BlackDB(context);
	}

	private void add1(BlackBean bean) {
		// insert
		SQLiteDatabase database = mBlackDB.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(BlackDB.NUMBER, bean.getNumber());
		values.put(BlackDB.MODEL, bean.getModel());
		database.insert(BlackDB.BLACK_TB, null, values);
		// 关闭数据库
		database.close();
	}

	/**
	 * @param number
	 *            号码
	 * @return 拦截模式
	 */
	public int getModel(String number) {
		int model = 0;
		SQLiteDatabase database = mBlackDB.getReadableDatabase();
		Cursor cursor = database.rawQuery("select " + BlackDB.MODEL + " from "
				+ BlackDB.BLACK_TB + " where " + BlackDB.NUMBER + "=?",
				new String[] { number });
		if (cursor.moveToNext()) {
			model = cursor.getInt(0);
		}

		cursor.close();
		return model;

	}

	/**
	 * 添加黑名单号码
	 * 
	 * @param bean
	 */
	public void add(BlackBean bean) {
		int model = getModel(bean.getNumber());
		if (model == -1) {
			// 判断黑名单号码是否存在
			add1(bean);
		} else {
			remove(bean);//先删除
			add1(bean);//再添加
			//update(bean);
		}
	}

	/**
	 * @param number
	 *            要删除的黑名单号码
	 */
	public void remove(String number) {
		SQLiteDatabase database = mBlackDB.getWritableDatabase();
		database.delete(BlackDB.BLACK_TB, BlackDB.NUMBER + "=?",
				new String[] { number });
		database.close();
	}

	public void remove(BlackBean bean) {
		remove(bean.getNumber());
	}

	/**
	 * @param bean
	 *            要更新的黑名单数据
	 */
	public void update(BlackBean bean) {
		SQLiteDatabase database = mBlackDB.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(BlackDB.MODEL, bean.getModel());
		database.update(BlackDB.BLACK_TB, values, BlackDB.NUMBER + "=?",
				new String[] { bean.getNumber() });
		database.close();
	}
	
	

	/**
	 * 
	 * @return
	 * 
	 *         所有的黑名单数据
	 */
	public List<BlackBean> getAllDatas() {
		List<BlackBean> res = new ArrayList<BlackBean>();
		SQLiteDatabase database = mBlackDB.getWritableDatabase();

		// 取所有数据
		Cursor cursor = database.query(BlackDB.BLACK_TB, new String[] {
				BlackDB.NUMBER, BlackDB.MODEL }, null, null, null, null,
				"_id desc");
		BlackBean bean = null;
		while (cursor.moveToNext()) {// ite.hasNext() ite.next()
			bean = new BlackBean();
			// 获取数据
			bean.setNumber(cursor.getString(0));
			bean.setModel(cursor.getInt(1));

			// 添加数据
			res.add(bean);
		}

		cursor.close();
		database.close();

		return res;

	}

}
