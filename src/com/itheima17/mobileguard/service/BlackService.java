package com.itheima17.mobileguard.service;

import static android.telephony.TelephonyManager.CALL_STATE_IDLE;
import static android.telephony.TelephonyManager.CALL_STATE_OFFHOOK;
import static android.telephony.TelephonyManager.CALL_STATE_RINGING;

import java.lang.reflect.Method;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;

import com.android.internal.telephony.ITelephony;
import com.itheima17.mobileguard.R;
import com.itheima17.mobileguard.dao.BlackDao;
import com.itheima17.mobileguard.dao.ContactsDao;
import com.itheima17.mobileguard.db.BlackDB;
import com.itheima17.mobileguard.utils.PrintLog;

/**
 * @author Administrator
 * @date 2015-11-25
 * @pagename com.itheima17.mobileguard.service
 * @desc 黑名单拦截的实现
 * 
 * @svn author $Author: goudan $
 * @svn date $Date: 2015-12-01 15:05:57 +0800 (周二, 01 十二月 2015) $
 * @Id $Id: BlackService.java 82 2015-12-01 07:05:57Z goudan $
 * @version $Rev: 82 $
 * @url $URL:
 *      https://188.188.2.100/svn/itheima17/trunk/MobileGuard/src/com/itheima17
 *      /mobileguard/service/BlackService.java $
 * 
 */
public class BlackService extends Service {

	private SmsInterceptReceiver mSmsInterceptReceiver;
	private BlackDao mBlackDao;
	private TelephonyManager mTm;
	private PhoneStateListener mListener;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	private class SmsInterceptReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {

			// 拦截短信的判断
			// 获取短信内容
			Bundle extras = intent.getExtras();
			Object[] object = (Object[]) extras.get("pdus");
			for (Object data : object) {
				SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) data);
				String number = smsMessage.getDisplayOriginatingAddress();
				PrintLog.print("number:" + number);
				// 判断黑名单是否存在
				int model = mBlackDao.getModel(number);
				if ((model & BlackDB.MODEL_SMS) != 0) {
					// 模式中有短信拦截 ： 短信 全部
					// 短信拦截
					abortBroadcast();
				}

			}

		}

	}

	/**
	 * 注册短信广播
	 */
	private void registSmsReceiver() {
		IntentFilter filter = new IntentFilter(
				"android.provider.Telephony.SMS_RECEIVED");
		// 设置广播优先级 优先级高的先运行 如果级别一样 代码注册优先清单文件，两个代码注册，谁先注册谁高
		filter.setPriority(Integer.MAX_VALUE);

		mSmsInterceptReceiver = new SmsInterceptReceiver();

		// 注册广播
		registerReceiver(mSmsInterceptReceiver, filter);

	}

	// 监听电话的状态
	private void registPhoneState() {
		mTm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);

		mListener = new PhoneStateListener() {

			/* *//** Device call state: No activity. */
			/*
			 * public static final int CALL_STATE_IDLE = 0; //空闲
			 *//**
			 * Device call state: Ringing. A new call arrived and is ringing
			 * or waiting. In the latter case, another call is already active.
			 */
			/*
			 * public static final int CALL_STATE_RINGING = 1;//响铃
			 *//**
			 * Device call state: Off-hook. At least one call exists that is
			 * dialing, active, or on hold, and no calls are ringing or waiting.
			 */
			/*
			 * public static final int CALL_STATE_OFFHOOK = 2;//通话
			 * incomingNumber 来电的号码
			 */
			@Override
			public void onCallStateChanged(int state, final String incomingNumber) {
				// 监听电话状态
				switch (state) {
				case CALL_STATE_IDLE:
					// 空闲
					PrintLog.print("空闲" + incomingNumber);

					break;
				case CALL_STATE_RINGING:
					// 响铃
					PrintLog.print("响铃" + incomingNumber);
					// 判断是否黑名单号码
					int model = mBlackDao.getModel(incomingNumber);
					if ((model & BlackDB.MODEL_PHONE) != 0) {
						// 11 10 10
						Uri uri = Uri.parse("content://call_log/calls");
						
						//注册内容观察者
						getContentResolver().registerContentObserver(uri, true, new ContentObserver(new Handler()){

							@Override
							public void onChange(boolean selfChange) {
								//删除日志
								super.onChange(selfChange);
								deleteCalllog(incomingNumber);
								//取消内容观察
								getContentResolver().unregisterContentObserver(this);
							}
							
						});
						// 挂断电话
						endCall(incomingNumber);
						//删除电话日志
						//
					}
					break;
				case CALL_STATE_OFFHOOK:
					// 通话
					PrintLog.print("通话" + incomingNumber);

					break;
				default:
					break;
				}
				super.onCallStateChanged(state, incomingNumber);
			}

		};
		mTm.listen(mListener, PhoneStateListener.LISTEN_CALL_STATE);
	}

	/**
	 * 删除电话日志 
	 */
	protected void deleteCalllog(final String number) {
		
		ContactsDao.deleteCallLog(getApplicationContext(), number);
		
		
	}

	/**
	 * 挂断电话
	 */
	protected void endCall(String incomingNumber) {
		PrintLog.print("挂断电话" + incomingNumber);
		try {
			// 1. class
			Class clazz = Class.forName("android.os.ServiceManager");
			// 2. method
			Method method = clazz.getDeclaredMethod("getService", String.class);
			// 3. object
			// 4. invoke
			IBinder ib = (IBinder) method.invoke(null,
					Context.TELEPHONY_SERVICE);

			ITelephony t = ITelephony.Stub.asInterface(ib);// 远程aidl获取该对象

			t.endCall();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}// 挂断电话
	}

	@Override
	public void onCreate() {
		setForeApp();
		
		// TODO Auto-generated method stub
		// 注册短信拦截广播
		registSmsReceiver();

		// 注册电话的状态
		registPhoneState();

		mBlackDao = new BlackDao(getApplicationContext());

		super.onCreate();
	};

	
	/**
	 * 设置成前台进程
	 */
	private void setForeApp() {
		Notification notification = new Notification(R.drawable.heima, "黑马卫士", System.currentTimeMillis());
		Intent intent = new Intent("itheima.home");
		PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent , 0);
		notification.setLatestEventInfo(getApplicationContext(), "黑马卫士", "打开黑马卫士主界面", contentIntent );
		startForeground(11, notification );
		
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		PrintLog.print("blackservice stop");
		unregisterReceiver(mSmsInterceptReceiver);

		// 取消电话监听
		mTm.listen(mListener, PhoneStateListener.LISTEN_NONE);// 取消
		super.onDestroy();
	}

}
