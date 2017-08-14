package com.itheima17.mobileguard.activity;

import com.itheima17.mobileguard.R;

import android.app.Activity;
import android.os.Bundle;

/**
 * @author Administrator
 * @date 2015-11-22
 * @pagename com.itheima17.mobileguard.activity
 * @desc 手机防盗之第一个设置向导界面

 * @svn author $Author: goudan $
 * @svn date $Date: 2015-11-22 15:54:46 +0800 (周日, 22 十一月 2015) $
 * @Id  $Id: Setup1Activity.java 23 2015-11-22 07:54:46Z goudan $
 * @version $Rev: 23 $
 * @url $URL: https://188.188.2.100/svn/itheima17/trunk/MobileGuard/src/com/itheima17/mobileguard/activity/Setup1Activity.java $ 
 * 
 */
public class Setup1Activity extends BaseSetupActivity {
	@Override
	public void initView() {
		setContentView(R.layout.activity_setup1);
		super.initView();
	}

	@Override
	public void next() {
		startActivity(Setup2Activity.class);
	}

	@Override
	public void prev() {
		// TODO Auto-generated method stub
		
	}
}
