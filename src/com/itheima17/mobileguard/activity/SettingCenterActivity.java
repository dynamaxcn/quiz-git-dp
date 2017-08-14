package com.itheima17.mobileguard.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.itheima17.mobileguard.R;
import com.itheima17.mobileguard.service.BlackService;
import com.itheima17.mobileguard.service.PhoneLocationService;
import com.itheima17.mobileguard.utils.MyContains;
import com.itheima17.mobileguard.utils.SPUtils;
import com.itheima17.mobileguard.utils.ServiceUtils;
import com.itheima17.mobileguard.view.MyDialog;
import com.itheima17.mobileguard.view.SettingCenterItemView;
import com.itheima17.mobileguard.view.SettingCenterItemView.OnToggleChangeListener;

/**
 * @author Administrator
 * @date 2015-11-25
 * @pagename com.itheima17.mobileguard.activity
 * @desc 设置中心
 * 
 * @svn author $Author: goudan $
 * @svn date $Date: 2015-11-30 14:23:06 +0800 (周一, 30 十一月 2015) $
 * @Id $Id: SettingCenterActivity.java 70 2015-11-30 06:23:06Z goudan $
 * @version $Rev: 70 $
 * @url $URL: https://188.188.2.100/svn/itheima17/trunk/MobileGuard/src/com/itheima17/mobileguard/activity/SettingCenterActivity.java $
 * 
 */
public class SettingCenterActivity extends Activity {
	private SettingCenterItemView mSciv_autoupdate;
	private SettingCenterItemView mSciv_blackservice;
	private SettingCenterItemView mSciv_locationStyle;
	private SettingCenterItemView mSciv_comingLocation;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		initView();
		
		initEvent();
		
		
	}
	
	@Override
	protected void onStart() {
		initData();
		super.onStart();
	}

	private void initData() {
		
		int index = (int) SPUtils.getFloat(getApplicationContext(), MyContains.LOCATIONSTYLEINDEX, 0);
		mSciv_locationStyle.setMessage("归属地样式(" + MyDialog.names[index] + ")");
		// 自定更新状态赋值
		mSciv_autoupdate.isToggleOn(SPUtils.getBoolean(getApplicationContext(), MyContains.AUTOUPDATE, false));
		//判断服务是否在运行
		if (ServiceUtils.isServiceRunning("com.itheima17.mobileguard.service.BlackService", this)){
			mSciv_blackservice.isToggleOn(true);
		} else {
			mSciv_blackservice.isToggleOn(false);
		}
		
		if (ServiceUtils.isServiceRunning("com.itheima17.mobileguard.service.PhoneLocationService", this)){
			mSciv_comingLocation.isToggleOn(true);
		} else {
			mSciv_comingLocation.isToggleOn(false);
		}
	}

	private void initEvent() {
		mSciv_locationStyle.setOnToggleChangeListener(new OnToggleChangeListener() {
			
			@Override
			public void onToggleChanged(SettingCenterItemView view, boolean isToggleOn) {
				//显示自定义对话框 显示几个背景的选择
				MyDialog m = new MyDialog(SettingCenterActivity.this,mSciv_locationStyle);
				//m.setTitle("标题");
				
				m.show();
			}
		});
		
		mSciv_comingLocation.setOnToggleChangeListener(new OnToggleChangeListener() {
			
			@Override
			public void onToggleChanged(SettingCenterItemView view, boolean isToggleOn) {
				if (isToggleOn) {
					//启动来电显示归属地服务
					Intent locationService = new Intent(SettingCenterActivity.this,PhoneLocationService.class);
					startService(locationService);
				} else {
					//关闭来电显示归属地服务
					Intent locationService = new Intent(SettingCenterActivity.this,PhoneLocationService.class);
					stopService(locationService);
				}
				
			}
		});
		
		// TODO Auto-generated method stub
		mSciv_autoupdate.setOnToggleChangeListener(new OnToggleChangeListener() {
			
			@Override
			public void onToggleChanged(SettingCenterItemView view, boolean isToggleOn) {
				//记录是否做自动更新的标记
				SPUtils.putBoolean(getApplicationContext(), MyContains.AUTOUPDATE, isToggleOn);
				/*if (isToggleOn) {
					Toast.makeText(getApplicationContext(), "自动更新打开", 0).show();
				} else {
					Toast.makeText(getApplicationContext(), "自动更新关闭", 0).show();
				}*/
				
			}
		});
		
		mSciv_blackservice.setOnToggleChangeListener(new OnToggleChangeListener() {
			
			@Override
			public void onToggleChanged(SettingCenterItemView view, boolean isToggleOn) {
				
				if (isToggleOn) {
					Intent service = new Intent(SettingCenterActivity.this, BlackService.class);
					startService(service);
				} else {
					Intent service = new Intent(SettingCenterActivity.this, BlackService.class);
					stopService(service);
				}
				
			}
		});
	}

	private void initView() {
		// TODO Auto-generated method stub
		setContentView(R.layout.activity_setttingcenter);
		mSciv_autoupdate = (SettingCenterItemView) findViewById(R.id.sciv_settingcenter_autoupdate);
		mSciv_blackservice = (SettingCenterItemView) findViewById(R.id.sciv_settingcenter_blackservice);
		
		mSciv_locationStyle = (SettingCenterItemView) findViewById(R.id.sciv_settingcenter_locationstyle);
		mSciv_comingLocation = (SettingCenterItemView) findViewById(R.id.sciv_settingcenter_cominglocation);
	}

	/*public void startBlackService(View v) {
		isBlackServiceStart = !isBlackServiceStart;
		// 启动或者关闭黑名单拦截服务
		if (isBlackServiceStart) {
			Intent service = new Intent(this, BlackService.class);
			startService(service);
		} else {
			Intent service = new Intent(this, BlackService.class);
			stopService(service);
		}

	}*/


}
