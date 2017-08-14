package com.itheima17.mobileguard.activity;

import java.io.FileNotFoundException;

import org.apache.http.cookie.SM;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.itheima17.mobileguard.R;
import com.itheima17.mobileguard.service.WatchDogThread;
import com.itheima17.mobileguard.utils.ServiceUtils;
import com.itheima17.mobileguard.utils.SmsUtils;
import com.itheima17.mobileguard.utils.SmsUtils.SDNotExistException;
import com.itheima17.mobileguard.utils.SmsUtils.SDNotGouException;
import com.itheima17.mobileguard.view.SettingCenterItemView;
import com.itheima17.mobileguard.view.SettingCenterItemView.OnToggleChangeListener;

/**
 * @author Administrator
 * @date 2015-11-27
 * @pagename com.itheima17.mobileguard.activity
 * @desc 高级工具
 * 
 * @svn author $Author: goudan $
 * @svn date $Date: 2015-12-06 11:29:13 +0800 (周日, 06 十二月 2015) $
 * @Id $Id: AToolActivity.java 101 2015-12-06 03:29:13Z goudan $
 * @version $Rev: 101 $
 * @url $URL:
 *      https://188.188.2.100/svn/itheima17/trunk/MobileGuard/src/com/itheima17
 *      /mobileguard/activity/AToolActivity.java $
 * 
 */
public class AToolActivity extends Activity {
	private SettingCenterItemView mSciv_phonelocation;
	private SettingCenterItemView mSciv_serviceNumber;
	private SettingCenterItemView mSciv_smsBaike;
	private SettingCenterItemView mSciv_smsResumn;
	private ProgressBar mPb_progress;
	private ProgressDialog mPd;
	private SettingCenterItemView mSciv_applock;
	private SettingCenterItemView mSciv_watchdogthread;
	private SettingCenterItemView mSciv_watchdogAccessibility;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		initView();

		initEvent();
		
		initData();
	}

	private void initData() {
		boolean toggleOn = ServiceUtils.isServiceRunning("com.itheima17.mobileguard.service.WatchDogThread", this);
		//初始化看门狗服务是否启动
		mSciv_watchdogthread.isToggleOn(toggleOn );
		
		toggleOn = ServiceUtils.isServiceRunning("com.itheima17.mobileguard.service.WatchDogAccessibility", this);
		//初始化看门狗服务是否启动
		mSciv_watchdogAccessibility.isToggleOn(toggleOn );
		
	}

	private void initEvent() {
		OnToggleChangeListener listener = new OnToggleChangeListener() {

			@Override
			public void onToggleChanged(SettingCenterItemView view,
					boolean isToggleOn) {

				int id = view.getId();
				switch (id) {
				case R.id.siv_atool_phonelocation:
					startMyActivity(PhoneLocationActivity.class);
					break;
				case R.id.siv_atool_servicelocation:
					startMyActivity(CommonServiceActivity.class);
					break;
				case R.id.siv_atool_smsbaike:
					// 显示备份进度 ui

					try {
						// SmsUtils.smsBaike(AToolActivity.this, pd);
						SmsUtils.smsBaike(AToolActivity.this,
								new MyProgressListener());
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (SDNotExistException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (SDNotGouException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;
				case R.id.siv_atool_applock:
					Intent applock = new Intent(AToolActivity.this,
							AppLockActivity.class);
					startActivity(applock);
					break;
				case R.id.siv_atool_smsResume:
					// 短信还原
					try {
						SmsUtils.smsResumn(AToolActivity.this,
								new MyProgressListener());
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (SDNotExistException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;
				case R.id.siv_atool_watchdogthread:
					// 通过线程监控的看门狗服务
					if (isToggleOn) {
						Intent service = new Intent(AToolActivity.this,
								WatchDogThread.class);
						startService(service);
					} else {
						Intent service = new Intent(AToolActivity.this,
								WatchDogThread.class);
						stopService(service);
					}
					break;
					
				case R.id.siv_atool_watchdogaccessibility:
					
					/*  <intent-filter>
		                <action android:name="android.intent.action.MAIN" />
		                <action android:name="android.settings.ACCESSIBILITY_SETTINGS" />
		                <!-- Wtf...  this action is bogus!  Can we remove it? -->
		                <action android:name="ACCESSIBILITY_FEEDBACK_SETTINGS" />
		                <category android:name="android.intent.category.DEFAULT" />
		                <category android:name="android.intent.category.VOICE_LAUNCH" />
		            </intent-filter>*/
					//打开设置中心的Accessibility功能
					Intent intent = new Intent("android.settings.ACCESSIBILITY_SETTINGS");
					intent.addCategory("android.intent.category.DEFAULT");
					startActivity(intent);
					break;

				default:
					break;
				}
			}
		};

		// 添加事件
		mSciv_phonelocation.setOnToggleChangeListener(listener);
		mSciv_serviceNumber.setOnToggleChangeListener(listener);
		mSciv_smsBaike.setOnToggleChangeListener(listener);
		mSciv_smsResumn.setOnToggleChangeListener(listener);
		mSciv_applock.setOnToggleChangeListener(listener);
		mSciv_watchdogthread.setOnToggleChangeListener(listener);
		mSciv_watchdogAccessibility.setOnToggleChangeListener(listener);

	}

	private class MyProgressListener implements SmsUtils.OnSmsProgressListener {

		@Override
		public void setMax(int maxValue) {
			// TODO Auto-generated method stub
			mPd.setMax(maxValue);
			mPb_progress.setMax(maxValue);
		}

		@Override
		public void show() {
			// TODO Auto-generated method stub
			mPd.show();
			mPb_progress.setVisibility(View.VISIBLE);
		}

		@Override
		public void setProgress(int currentProgress) {
			// TODO Auto-generated method stub
			mPd.setProgress(currentProgress);
			mPb_progress.setProgress(currentProgress);
		}

		@Override
		public void close() {
			// TODO Auto-generated method stub
			mPd.dismiss();
			mPb_progress.setVisibility(View.GONE);
		}

	}

	private void initEvent1() {
		
		mSciv_phonelocation
				.setOnToggleChangeListener(new OnToggleChangeListener() {

					@Override
					public void onToggleChanged(SettingCenterItemView view,
							boolean isToggleOn) {
						// 跟开关没关系
						// 打开电话归属地查询的界面
						startMyActivity(PhoneLocationActivity.class);

					}

				});

		mSciv_serviceNumber
				.setOnToggleChangeListener(new OnToggleChangeListener() {

					@Override
					public void onToggleChanged(SettingCenterItemView view,
							boolean isToggleOn) {
						// 打开公共服务的界面
						startMyActivity(CommonServiceActivity.class);
					}
				});

		mSciv_smsBaike.setOnToggleChangeListener(new OnToggleChangeListener() {

			@Override
			public void onToggleChanged(SettingCenterItemView view,
					boolean isToggleOn) {
				Toast.makeText(getApplicationContext(), "短信备份", 0).show();

			}
		});
		mSciv_smsResumn.setOnToggleChangeListener(new OnToggleChangeListener() {

			@Override
			public void onToggleChanged(SettingCenterItemView view,
					boolean isToggleOn) {
				Toast.makeText(getApplicationContext(), "短信还原", 0).show();

			}
		});

	}

	private void startMyActivity(Class type) {
		Intent location = new Intent(AToolActivity.this, type);
		startActivity(location);
	}

	private void initView() {
		setContentView(R.layout.activity_atool);

		mSciv_phonelocation = (SettingCenterItemView) findViewById(R.id.siv_atool_phonelocation);
		// siv_atool_servicelocation

		mPb_progress = (ProgressBar) findViewById(R.id.pb_atool_progress);
		mSciv_serviceNumber = (SettingCenterItemView) findViewById(R.id.siv_atool_servicelocation);

		mSciv_smsBaike = (SettingCenterItemView) findViewById(R.id.siv_atool_smsbaike);
		mSciv_smsResumn = (SettingCenterItemView) findViewById(R.id.siv_atool_smsResume);

		mSciv_applock = (SettingCenterItemView) findViewById(R.id.siv_atool_applock);
		mPd = new ProgressDialog(AToolActivity.this);
		mPd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);// 设置进度条为水平方向

		mSciv_watchdogthread = (SettingCenterItemView) findViewById(R.id.siv_atool_watchdogthread);
		
		mSciv_watchdogAccessibility = (SettingCenterItemView) findViewById(R.id.siv_atool_watchdogaccessibility);
	}
}
