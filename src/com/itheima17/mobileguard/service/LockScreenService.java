package com.itheima17.mobileguard.service;

import java.util.List;

import com.itheima17.mobileguard.domain.AppBean;
import com.itheima17.mobileguard.utils.PrintLog;
import com.itheima17.mobileguard.utils.TaskUtils;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

public class LockScreenService extends Service {

	private ScreenReceiver mScreenReceiver;
	private ActivityManager mAm;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	private class ScreenReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			//清理进程
			List<AppBean> allRunningTaskInfos = TaskUtils.getAllRunningTaskInfos(context);
			for (AppBean bean : allRunningTaskInfos){
				mAm.killBackgroundProcesses(bean.getPackName());
				
			}
			PrintLog.print("锁屏清理进程");
			
		}
		
	}
	
	@Override
	public void onCreate() {
		//注册锁屏的广播
		
		mAm = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		mScreenReceiver = new ScreenReceiver();
		IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
		
		registerReceiver(mScreenReceiver, filter);
		super.onCreate();
	}
	
	@Override
	public void onDestroy() {
		
		//取消注册
		unregisterReceiver(mScreenReceiver);
		super.onDestroy();
	}


	
	

}
