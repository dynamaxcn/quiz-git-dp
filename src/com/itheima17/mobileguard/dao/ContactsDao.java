package com.itheima17.mobileguard.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

import com.itheima17.mobileguard.domain.ContactBean;

/**
 * @author Administrator
 * @date 2015-11-22
 * @pagename com.itheima17.mobileguard.dao
 * @desc 获取联系人的信息

 * @svn author $Author: goudan $
 * @svn date $Date: 2015-11-26 10:32:29 +0800 (周四, 26 十一月 2015) $
 * @Id  $Id: ContactsDao.java 42 2015-11-26 02:32:29Z goudan $
 * @version $Rev: 42 $
 * @url $URL: https://188.188.2.100/svn/itheima17/trunk/MobileGuard/src/com/itheima17/mobileguard/dao/ContactsDao.java $ 
 * 
 */
public class ContactsDao {
	
	/**
	 * 从短信记录中找号码
	 * @param context
	 * @return
	 */
	public static List<ContactBean> getAllSms(Context context){
		List<ContactBean> beans = new ArrayList<ContactBean>();
		Uri uri = Uri.parse("content://sms");
		Cursor cursor = context.getContentResolver().query(uri, new String[]{"address"}, null, null, null);
		while(cursor.moveToNext()) {
			ContactBean bean = new ContactBean();
			bean.name = "sms";
			bean.phone = cursor.getString(0);
			//添加
			beans.add(bean);
		}
		return beans;
	}
	
	public static void deleteCallLog(Context context,String number){
		Uri uri = Uri.parse("content://call_log/calls");
		context.getContentResolver().delete(uri, "number=?", new String[]{number});
		
	}
	/**
	 * 从通话记录中找号码
	 * @param context
	 * @return
	 */
	public static List<ContactBean> getAllCalllog(Context context){
		List<ContactBean> beans = new ArrayList<ContactBean>();
		
		
		Uri uri = Uri.parse("content://call_log/calls");
		Cursor cursor = context.getContentResolver().query(uri, new String[]{"number"}, null, null, null);
		while(cursor.moveToNext()) {
			ContactBean bean = new ContactBean();
			bean.name = "phone";
			bean.phone = cursor.getString(0);
			//添加
			beans.add(bean);
		}
		
		return beans;
	}
	
	public static List<ContactBean> getAllBeans(Context context){
		List<ContactBean> beans = new ArrayList<ContactBean>();
		
		Uri contactUrl = Uri.parse("content://com.android.contacts/contacts");
		Uri dataUrl = Uri.parse("content://com.android.contacts/data");
		Cursor cursor = context.getContentResolver().query(contactUrl, new String[]{"name_raw_contact_id"}, null, null, null);
		
		while(cursor.moveToNext()) {
			ContactBean bean = new ContactBean();
			String name_raw_contact_id = cursor.getString(0);
			Cursor cursor2 = context.getContentResolver().query(dataUrl,new String[]{"data1","mimetype"},"raw_contact_id=?",new String[]{name_raw_contact_id},null);
			while (cursor2.moveToNext()) {
				
				String data = cursor2.getString(0);
				String type = cursor2.getString(1);
				if (type.equals("vnd.android.cursor.item/name")){
					bean.name = data;
				} else if (type.equals("vnd.android.cursor.item/phone_v2")) {
					bean.phone = data;
				}
			}
			cursor2.close();
			//添加
			beans.add(bean);
		}
		
		
		cursor.close();
		return beans;
	}
}
