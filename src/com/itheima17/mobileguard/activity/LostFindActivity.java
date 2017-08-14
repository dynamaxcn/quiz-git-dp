package com.itheima17.mobileguard.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.itheima17.mobileguard.R;
import com.itheima17.mobileguard.utils.MyContains;
import com.itheima17.mobileguard.utils.SPUtils;

/**
 * @author Administrator
 * @date 2015-11-22
 * @pagename com.itheima17.mobileguard.activity
 * @desc 手机防盗界面

 * @svn author $Author: goudan $
 * @svn date $Date: 2015-11-23 10:45:42 +0800 (周一, 23 十一月 2015) $
 * @Id  $Id: LostFindActivity.java 27 2015-11-23 02:45:42Z goudan $
 * @version $Rev: 27 $
 * @url $URL: https://188.188.2.100/svn/itheima17/trunk/MobileGuard/src/com/itheima17/mobileguard/activity/LostFindActivity.java $ 
 * 
 */
public class LostFindActivity extends Activity {
	private TextView mTv_safeNumber;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		
		isSetupFinish();
	}
	
	private void isSetupFinish() {
		//判断是否设置向导完成
		Boolean isSetupFinish = SPUtils.getBoolean(getApplicationContext(), MyContains.ISSETUPFINISH, false);
		if (isSetupFinish) {
			// true 设置向导界面完成
			//显示自己的界面
			initView();
			initData();
		} else {
			//没有设置向导完成
			//进入第一个设置向导界面
			enterSetup1();
		}
		
	}

	private void initData() {
		// 初始化显示安全号码
		mTv_safeNumber.setText(SPUtils.getString(getApplicationContext(), MyContains.SAFENUMBER, ""));
		
	}

	private void enterSetup1() {
		Intent first = new Intent(this,Setup1Activity.class);
		startActivity(first);
		finish();
	}

	public void enterSetup(View v){
		enterSetup1();
	}

	private void initView() {
		// TODO Auto-generated method stub
		setContentView(R.layout.activity_lostfind);
		mTv_safeNumber = (TextView) findViewById(R.id.tv_lostfind_safenumber);
	}
}
