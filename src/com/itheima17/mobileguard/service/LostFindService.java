package com.itheima17.mobileguard.service;

import android.app.Service;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;

import com.itheima17.mobileguard.R;
import com.itheima17.mobileguard.receiver.MyDeviceAdminReceiver;
import com.itheima17.mobileguard.utils.MyContains;
import com.itheima17.mobileguard.utils.PrintLog;
import com.itheima17.mobileguard.utils.SPUtils;

/**
 * @author Administrator
 * @date 2015-11-23
 * @pagename com.itheima17.mobileguard.service
 * @desc 手机防盗service
 * 
 * @svn author $Author: goudan $
 * @svn date $Date: 2015-11-23 15:17:48 +0800 (周一, 23 十一月 2015) $
 * @Id $Id: LostFindService.java 31 2015-11-23 07:17:48Z goudan $
 * @version $Rev: 31 $
 * @url $URL:
 *      https://188.188.2.100/svn/itheima17/trunk/MobileGuard/src/com/itheima17
 *      /mobileguard/service/LostFindService.java $
 * 
 */
public class LostFindService extends Service {

	private SmsReceiver mSmsReceiver;
	private boolean isPlaying = false;
	private MediaPlayer mMp;
	private DevicePolicyManager mDpm;
	private ComponentName mComponentName;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 
	 * @author Administrator
	 * @date 2015-11-23
	 * @pagename com.itheima17.mobileguard.service
	 * @desc 短信的广播接受者
	 * 
	 * @svn author $Author: goudan $
	 * @svn date $Date: 2015-11-23 15:17:48 +0800 (周一, 23 十一月 2015) $
	 * @Id $Id: LostFindService.java 31 2015-11-23 07:17:48Z goudan $
	 * @version $Rev: 31 $
	 * @url $URL:
	 *      https://188.188.2.100/svn/itheima17/trunk/MobileGuard/src/com/itheima17
	 *      /mobileguard/service/LostFindService.java $
	 * 
	 */
	private class SmsReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			// 监控短信内容

			// 1. 获取短信的内容
			Bundle extras = intent.getExtras();
			Object[] object = (Object[]) extras.get("pdus");
			for (Object data : object) {
				SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) data);
				String messageBody = smsMessage.getDisplayMessageBody();
				if (messageBody.contains("#*music*#")) {

					music();
				} else if (messageBody.contains("#*gps*#")) {
					sendlocationSms();
				} else if (messageBody.contains("#*lockscreen*#")) {
					//重置密码
					mDpm.resetPassword("123456", 0);
					//锁屏
					mDpm.lockNow();
				} else if (messageBody.contains("#*wipedata")) {
					//清除sd卡
					mDpm.wipeData(DevicePolicyManager.WIPE_EXTERNAL_STORAGE);
				}
				// 2. 对内容判断
				// 1. music
				// 2. gps
				// 3. lockscreen
				// 4. wipedata
			}

		}
		public void sendlocationSms() {
PrintLog.print("gps 获取定位");			
			// 发送定位信息
			// 获取定位管理器
			final LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
			Criteria criteria = new Criteria();
			criteria.setAccuracy(Criteria.ACCURACY_FINE);// 获取精度高的定位方式
			criteria.setCostAllowed(true);// 付费

			String provider = lm.getBestProvider(criteria, true);
			
			// 拦截短信
			abortBroadcast();
			// 时间和位置动态监听
			lm.requestLocationUpdates(provider, 0, 0, new LocationListener() {

				@Override
				public void onStatusChanged(String provider, int status,
						Bundle extras) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onProviderEnabled(String provider) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onProviderDisabled(String provider) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onLocationChanged(Location location) {
					// 位置改变的监听
					float accuracy = location.getAccuracy();// 精确度
					double altitude = location.getAltitude();// 海拔
					double latitude = location.getLatitude();// 纬度
					double longitude = location.getLongitude();// 经度

					// 显示在tv中
					StringBuilder mess = new StringBuilder();
					mess.append("accuracy:" + accuracy + "\n");
					mess.append("altitude:" + altitude + "\n");
					mess.append("latitude:" + latitude + "\n");
					mess.append("longitude:" + longitude + "\n");

					
					//发送短信给安全号码
					String safeNumber = SPUtils.getString(getApplicationContext(), MyContains.SAFENUMBER, "");
					
PrintLog.print(mess + "<<<<<<<<<<<<<<<>>>>>>>>>>>>>>>>>>");					
					//发送短信
					SmsManager smsManager = SmsManager.getDefault();
					smsManager.sendTextMessage(safeNumber, null, mess + "", null, null);
					
					//停止监听位置
					
					lm.removeUpdates(this);
				}
			});

		}

		private void music() {
			if (isPlaying) {
				return;
			}

			PrintLog.print("播放音乐");
			isPlaying = true;
			// 播放报警音乐

			mMp = MediaPlayer.create(getApplicationContext(), R.raw.qqqg);
			mMp.start();
			// 监控音乐播放完毕的事件
			mMp.setOnCompletionListener(new OnCompletionListener() {

				@Override
				public void onCompletion(MediaPlayer mp) {
					// 音乐播放完成的回调
					isPlaying = false;
					mMp = null;
				}
			});
			// 拦截短信
			abortBroadcast();
		}

	}

	@Override
	public void onCreate() {
		// 第一个创建的时候执行
		PrintLog.print("lostfind service start");

		// 注册短信的广播接受者

		IntentFilter filter = new IntentFilter(
				"android.provider.Telephony.SMS_RECEIVED");
		// 设置广播优先级 优先级高的先运行 如果级别一样 代码注册优先清单文件，两个代码注册，谁先注册谁高
		filter.setPriority(Integer.MAX_VALUE);

		mSmsReceiver = new SmsReceiver();

		// 注册广播
		registerReceiver(mSmsReceiver, filter);
		
		mDpm = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);
		mComponentName = new ComponentName(this, MyDeviceAdminReceiver.class);
		
		super.onCreate();
	}

	

	@Override
	public void onDestroy() {
		// 服务销毁的时候执行
		PrintLog.print("lostfind service stop");
		// 取消注册
		unregisterReceiver(mSmsReceiver);
		if (mMp != null && mMp.isPlaying()) {
			mMp.stop();
			mMp.release();
			mMp = null;
		}
		super.onDestroy();
	}

}
