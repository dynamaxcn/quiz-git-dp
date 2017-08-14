package com.itheima17.mobileguard.service;

import java.util.List;

import com.itheima17.mobileguard.activity.WatchDogPasswordActivity;
import com.itheima17.mobileguard.dao.LockDao;

import com.itheima17.mobileguard.utils.PrintLog;

import android.accessibilityservice.AccessibilityService;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.os.SystemClock;
import android.view.accessibility.AccessibilityEvent;

/**
 * @author Administrator
 * @date 2015-12-6
 * @pagename com.itheima17.mobileguard.service
 * @desc Accessibility来完成看门狗

 * @svn author $Author: goudan $
 * @svn date $Date: 2015-12-06 11:29:13 +0800 (周日, 06 十二月 2015) $
 * @Id  $Id: WatchDogAccessibility.java 101 2015-12-06 03:29:13Z goudan $
 * @version $Rev: 101 $
 * @url $URL: https://188.188.2.100/svn/itheima17/trunk/MobileGuard/src/com/itheima17/mobileguard/service/WatchDogAccessibility.java $ 
 * 
 */
public class WatchDogAccessibility extends AccessibilityService {
	private boolean isWatching = false;
	private ActivityManager mAm;
	private LockDao mLockDao;
	private ShurenReceiver mShurenReceiver;
	private String mShuren = "";
	private LockScreenReceiver mLockScreenReceiver;
	private List<String> mAllLockPacks;

	

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		PrintLog.print("看门狗启动");
		mAm = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		mLockDao = new LockDao(this);
		
		//获取所有加锁的app包名
		mAllLockPacks = mLockDao.getAllLockPacks();
		//启动看门狗
		

		//注册熟人的广播
		registShurenReceiver();
		//锁屏清理熟人的广播
		registLockScreen();
		
		//获取所有加锁app包名
		
		
		super.onCreate();
	}
	
	private class LockScreenReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			mShuren = "";//清理熟人
		}
		
	}

	private void registLockScreen() {
		// TODO Auto-generated method stub
		IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
		mLockScreenReceiver = new LockScreenReceiver();
		registerReceiver(mLockScreenReceiver, filter );
	}

	private class ShurenReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// 收到熟人的广播信息

			mShuren = intent.getStringExtra("shuren");
		}

	}

	private void registShurenReceiver() {
		mShurenReceiver = new ShurenReceiver();

		IntentFilter filter = new IntentFilter("itheima.watchdog17");
		registerReceiver(mShurenReceiver, filter);
	}

	

	protected void startEnterPassView(String packageName) {
		// 开启输入密码的界面
		Intent enterpass = new Intent(this, WatchDogPasswordActivity.class);
		enterpass.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		enterpass.putExtra("moshengren", packageName);
		startActivity(enterpass);

	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		PrintLog.print("看门狗关闭");
		unregisterReceiver(mShurenReceiver);// 取消熟人广播
		unregisterReceiver(mLockScreenReceiver);//取消锁屏的广播
		isWatching = false;
		super.onDestroy();
	}
	
	
	@Override
	public void onAccessibilityEvent(AccessibilityEvent event) {
		// 必须
		if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
			PrintLog.print(event.getPackageName() + "<<<>>>>");
			
			String packageName = event.getPackageName() + "";

			// 4. 通过lockdao判断是否加锁
			//性能优化
			if(mAllLockPacks.contains(packageName)){//if (mLockDao.isLock(packageName)) {// 缓存内存的判断
				// 1. 加锁
				PrintLog.print("拦截");
				if (!mShuren.equals(packageName)) {
					// 显示输入密码的界面
					startEnterPassView(packageName);
				} else {
					//熟人
					
				}
			} else {

				// 2. 放行
				PrintLog.print("放行 ");
			}
		}

	}

	@Override
	public void onInterrupt() {
		// TODO Auto-generated method stub

	}

}
