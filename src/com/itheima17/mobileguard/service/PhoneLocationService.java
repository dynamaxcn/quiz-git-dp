package com.itheima17.mobileguard.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import com.itheima17.mobileguard.dao.PhoneLocationDao;
import com.itheima17.mobileguard.utils.PrintLog;
import com.itheima17.mobileguard.view.MyToast;

/**
 * @author Administrator
 * @date 2015-11-30
 * @pagename com.itheima17.mobileguard.service
 * @desc  来电归属地显示的服务

 * @svn author $Author: goudan $
 * @svn date $Date: 2015-11-30 15:39:34 +0800 (周一, 30 十一月 2015) $
 * @Id  $Id: PhoneLocationService.java 73 2015-11-30 07:39:34Z goudan $
 * @version $Rev: 73 $
 * @url $URL: https://188.188.2.100/svn/itheima17/trunk/MobileGuard/src/com/itheima17/mobileguard/service/PhoneLocationService.java $ 
 * 
 */
public class PhoneLocationService extends Service {

	private TelephonyManager mTm;
	private PhoneStateListener mListener;
	private MyToast mToast;
	private OutCallReceiver mOutCallReceiver;
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	private void registPhoneState(){
		mTm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		mListener = new PhoneStateListener() {

			@Override
			public void onCallStateChanged(int state, String incomingNumber) {
				// 电话状态的改变
				switch (state) {
				case TelephonyManager.CALL_STATE_IDLE:
					//空闲
					mToast.hiden();
					break;
				case TelephonyManager.CALL_STATE_RINGING:
					//响铃
					//通过号码查询归属地信息 
					String location = PhoneLocationDao.getLocation(incomingNumber);
					
					//显示归属地
					showLocation(location);
					
					break;
				case TelephonyManager.CALL_STATE_OFFHOOK:
					//通话
					break;

				default:
					break;
				}
				super.onCallStateChanged(state, incomingNumber);
			}
			
		};
		
		//listener 状态的回调
		//PhoneStateListener.LISTEN_CALL_STATE 监听的类型 
		mTm.listen(mListener, PhoneStateListener.LISTEN_CALL_STATE);
	}
	
	/**
	 * 显示归属地信息
	 * @param location
	 */
	protected void showLocation(String location) {
		//自定义Toast 看Toast源码
		
		mToast.show(location);
		
		
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		PrintLog.print("来电显示归属地服务启动");
		mToast = new MyToast(getApplicationContext());
		
		//外拨电话注册
		registOutCall();
		//监听来电的状态
		registPhoneState();
		
		super.onCreate();
	}
	
	private class OutCallReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			PrintLog.print("外拨电话广播");
			//获取外拨电话的号码
			String number = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
			String location = PhoneLocationDao.getLocation(number);
			mToast.show(location);
		}
		
	}
	private void registOutCall() {
		
		mOutCallReceiver = new OutCallReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_NEW_OUTGOING_CALL);
		
		registerReceiver(mOutCallReceiver, filter);
	}

	@Override
	public void onDestroy() {
		
		//取消外拨电话的注册 
		unregisterReceiver(mOutCallReceiver);
		// TODO Auto-generated method stub
		PrintLog.print("来电显示归属地服务关闭");
		//取消电话监听
		mTm.listen(mListener, PhoneStateListener.LISTEN_NONE);
		super.onDestroy();
	}
}
