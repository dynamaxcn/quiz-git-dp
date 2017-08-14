package com.itheima17.mobileguard.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.itheima17.mobileguard.R;
import com.itheima17.mobileguard.utils.PrintLog;

/**
 * @author Administrator
 * @date 2015-12-6
 * @pagename com.itheima17.mobileguard.activity
 * @desc 锁拦截 输入密码的界面
 * 
 * @svn author $Author: goudan $
 * @svn date $Date: 2015-12-06 10:26:46 +0800 (周日, 06 十二月 2015) $
 * @Id $Id: WatchDogPasswordActivity.java 100 2015-12-06 02:26:46Z goudan $
 * @version $Rev: 100 $
 * @url $URL:
 *      https://188.188.2.100/svn/itheima17/trunk/MobileGuard/src/com/itheima17
 *      /mobileguard/activity/WatchDogPasswordActivity.java $
 * 
 */
public class WatchDogPasswordActivity extends Activity {
	private EditText mEt_password;
	private String mPackName;
	private BroadcastReceiver mReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_watchdogpassword);
		mEt_password = (EditText) findViewById(R.id.et_watchdogpassword_password);
		ImageView iv_icon = (ImageView) findViewById(R.id.iv_watchdogpassword_icon);
		mPackName = getIntent().getStringExtra("moshengren");

		// 获取图标
		try {
			Drawable icon = getPackageManager().getApplicationIcon(mPackName);
			iv_icon.setImageDrawable(icon);// 设置拦截app的图标
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		mReceiver = new HomeReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
		registerReceiver(mReceiver, filter);
	}
	
	private class HomeReceiver  extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			  String action = intent.getAction();  
			  if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
				  //home键处理
				  finish();//关闭自己
			  }

			
		}
		
	}

	@Override
	public void onBackPressed() {
		// 返回键的处理
		// 回到主界面
		/*
		 * <intent-filter> <action android:name="android.intent.action.MAIN" />
		 * <category android:name="android.intent.category.HOME" /> <category
		 * android:name="android.intent.category.DEFAULT" /> <category
		 * android:name="android.intent.category.MONKEY"/> </intent-filter>
		 */

		Intent main = new Intent("android.intent.action.MAIN");
		main.addCategory("android.intent.category.HOME");
		main.addCategory("android.intent.category.DEFAULT");
		main.addCategory("android.intent.category.MONKEY");

		startActivity(main);
		finish();// 退出任务栈
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		unregisterReceiver(mReceiver);
		super.onDestroy();
	}

	/**
	 * 输入密码按钮的事件处理
	 * 
	 * @param v
	 */
	public void enterPass(View v) {
		// 获取密码判断
		String pass = mEt_password.getText() + "";
		if (TextUtils.isEmpty(pass)) {
			Toast.makeText(getApplicationContext(), "密码不能为空", 0).show();
		} else if ("123456".equals(pass)) {
			// 放行
			PrintLog.print("密码正确，放行");

			// 告诉狗不要拦截
			Intent intent = new Intent("itheima.watchdog17");
			// 绑定信息 app包名
			intent.putExtra("shuren", mPackName);
			sendBroadcast(intent);

			Toast.makeText(getApplicationContext(), "密码正确，放行", 0).show();
			finish();// 关闭自己
		} else {
			// 密码不正确
			Toast.makeText(getApplicationContext(), "密码不正确", 0).show();
		}
	}
}
