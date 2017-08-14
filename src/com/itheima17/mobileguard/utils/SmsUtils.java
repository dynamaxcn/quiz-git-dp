package com.itheima17.mobileguard.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.SystemClock;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.itheima17.mobileguard.domain.ContactBean;
import com.itheima17.mobileguard.domain.SmsJsonData;
import com.itheima17.mobileguard.domain.SmsJsonData.Smses;

/**
 * @author Administrator
 * @date 2015-11-29
 * @pagename com.itheima17.mobileguard.utils
 * @desc 短信备份和还原的操作

 * @svn author $Author: goudan $
 * @svn date $Date: 2015-11-29 16:58:13 +0800 (周日, 29 十一月 2015) $
 * @Id  $Id: SmsUtils.java 65 2015-11-29 08:58:13Z goudan $
 * @version $Rev: 65 $
 * @url $URL: https://188.188.2.100/svn/itheima17/trunk/MobileGuard/src/com/itheima17/mobileguard/utils/SmsUtils.java $ 
 * 
 */
public class SmsUtils {
	
	/**
	 * 写一条短信到数据库中 
	 * @param context
	 * @param smses
	 */
	private static void writeSms(Context context,Smses smses){
		//
		Uri uri = Uri.parse("content://sms");
		ContentValues values = new ContentValues();
		values.put("address", smses.phone);
		values.put("date", smses.date);
		values.put("body", smses.body);
		values.put("type", smses.type);
		values.put("date_sent", smses.date_sent);
		
		context.getContentResolver().insert(uri, values );
	}
	
	private static List<ContactBean> getAllSms(Context context){
		List<ContactBean> beans = new ArrayList<ContactBean>();
		Uri uri = Uri.parse("content://sms");
		Cursor cursor = context.getContentResolver().query(uri, new String[]{"address","date","body","type","date_sent"}, null, null, null);
		while(cursor.moveToNext()) {
			ContactBean bean = new ContactBean();
			//号码
			bean.phone = cursor.getString(0);
			
			bean.date = cursor.getInt(1);
			
			bean.body = cursor.getString(2);
			
			//body的解密
			bean.body = EncodeUtils.jiami(bean.body);
			
			bean.type = cursor.getInt(3);
			
			bean.date_sent = cursor.getInt(4);
			//添加
			beans.add(bean);
		}
		return beans;
	}
	
	/**
	 * @author Administrator
	 * @date 2015-11-29
	 * @pagename com.itheima17.mobileguard.utils
	 * @desc sd不能用
	
	 * @svn author $Author: goudan $
	 * @svn date $Date: 2015-11-29 16:58:13 +0800 (周日, 29 十一月 2015) $
	 * @Id  $Id: SmsUtils.java 65 2015-11-29 08:58:13Z goudan $
	 * @version $Rev: 65 $
	 * @url $URL: https://188.188.2.100/svn/itheima17/trunk/MobileGuard/src/com/itheima17/mobileguard/utils/SmsUtils.java $ 
	 * 
	 */
	public static class SDNotExistException extends Exception{
		
	}
	
	/**
	 * @author Administrator
	 * @date 2015-11-29
	 * @pagename com.itheima17.mobileguard.utils
	 * @desc sd卡空间不足
	
	 * @svn author $Author: goudan $
	 * @svn date $Date: 2015-11-29 16:58:13 +0800 (周日, 29 十一月 2015) $
	 * @Id  $Id: SmsUtils.java 65 2015-11-29 08:58:13Z goudan $
	 * @version $Rev: 65 $
	 * @url $URL: https://188.188.2.100/svn/itheima17/trunk/MobileGuard/src/com/itheima17/mobileguard/utils/SmsUtils.java $ 
	 * 
	 */
	public static class SDNotGouException extends Exception{
		
	}
	
	public interface OnSmsProgressListener {
		void setMax(int maxValue);//设置最大值
		void show();//显示进度条
		void setProgress(int currentProgress);
		void close();//关闭进度
		
	}
	
	
	public static void smsResumn(final Activity context,final OnSmsProgressListener listener) throws SDNotExistException, FileNotFoundException {
		//1. 取文件中的数据
		if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			//sd存在
			throw new SDNotExistException();
		}
		
		
		final File sdFile = Environment.getExternalStorageDirectory();
		File smsFile = new File(sdFile,"sms.json");
		if (!smsFile.exists()) {
			throw new FileNotFoundException();
		}
		
		
		//还原的逻辑
		//1. 解析json数据
		final List<Smses> parseJson = parseJson(smsFile);
		//设置回调的最大进度
		listener.setMax(parseJson.size());
		
		//显示
		listener.show();
		
		class Data{
			int progress  = 0;
		}
		final Data progressData = new Data();
		new Thread(){
			public void run() {
				for (Smses smses : parseJson) {
					//取一条 写一条到短信数据库
					writeSms(context,smses);
					progressData.progress++;
					
					//设置进度的回调
					context.runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							//设置当前进度
							listener.setProgress(progressData.progress);
							
						}
					});
				}
				//关闭
				context.runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						//设置当前进度
						listener.close();
						
					}
				});
			};
		}.start();
		
		
		
	
		
		
		
	}
	
	@SuppressWarnings("resource")
	private static  List<Smses> parseJson(File smsFile){
		//List<ContactBean> datas = new ArrayList<ContactBean>();
		
		//1. 创建gson对象
		Gson gson = new Gson();
		FileReader fileReader = null;
		try {
			fileReader = new FileReader(smsFile);
			SmsJsonData smsJsonData = gson.fromJson(fileReader, SmsJsonData.class);
			
			return smsJsonData.smses;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			try {
				fileReader.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return null;
		
	}
	
	
	//高级程序员
	public static void smsBaike(final Activity context,final OnSmsProgressListener listener) throws FileNotFoundException, SDNotExistException, SDNotGouException{
		//1. 获取所有短信的内容
		final List<ContactBean> allSms = getAllSms(context);
		
		listener.setMax(allSms.size());//设置最大值
		//2. 写到文件中(sd)
		    //是否sd卡 容量
		if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			//sd存在
			throw new SDNotExistException();
		}
		
		
		final File sdFile = Environment.getExternalStorageDirectory();
		
		if (sdFile.getFreeSpace() < 1024 * 1024 * 10) {
			//sd卡少于10m 
			throw new SDNotGouException();
		}
		
		
		
		//FileOutputStream fos = new FileOutputStream(smsFile);
		listener.show();//进度的显示
		class Data{
			int progress  = 0;
		}
		final Data progressData = new Data();
		
		new Thread(){
			public void run() {
				//sd正常
				File smsFile = new File(sdFile,"sms.json");
				//备份
				PrintStream out;
				try {
					out = new PrintStream(smsFile);
					out.println("{\"smses\": [");
					for (ContactBean bean : allSms) {
						//SystemClock.sleep(500);
						//取一条 往文件中写一条
						//{"":"","":""}, 非最后一条
						//{"":"","":""}]} 最后一条
						out.println("{");
						out.println("\"body\":\"" + format(EncodeUtils.jiami(bean.body)) + "\",");
						out.println("\"date\":\"" + bean.date + "\",");
						out.println("\"type\":\"" + bean.type + "\",");
						out.println("\"date_sent\":\"" + bean.date_sent + "\",");
						out.println("\"phone\":\"" + bean.phone+ "\"}");
						
						//判断是否是最后一条
						if (bean == allSms.get(allSms.size() - 1)) {
							out.println("]}");
						} else {
							out.println(",");
						}
						
						progressData.progress++;
						
						//设置进度
						context.runOnUiThread(new Runnable() {
							
							@Override
							public void run() {
								// 在主线程中设置进度条的进度
								listener.setProgress(progressData.progress);
							}
						});
					}
					
					//关闭
					out.close();
					//关闭进度条
					//设置进度
					context.runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							//关闭进度条
							listener.close();
						}
					});
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
			};
		}.start();
		
		
		
	}
	
	private static String format(String body){
		return body.replace("\"", "\\\"");
	
	}
}
