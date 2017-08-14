package com.itheima17.mobileguard.activity;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Toast;

import com.itheima17.mobileguard.R;
import com.itheima17.mobileguard.receiver.MyDeviceAdminReceiver;
import com.itheima17.mobileguard.service.LostFindService;
import com.itheima17.mobileguard.utils.MyContains;
import com.itheima17.mobileguard.utils.SPUtils;

/**
 * @author Administrator
 * @date 2015-11-22
 * @pagename com.itheima17.mobileguard.activity
 * @desc 手机防盗之第一个设置向导界面

 * @svn author $Author: goudan $
 * @svn date $Date: 2015-11-23 15:17:48 +0800 (周一, 23 十一月 2015) $
 * @Id  $Id: Setup4Activity.java 31 2015-11-23 07:17:48Z goudan $
 * @version $Rev: 31 $
 * @url $URL: https://188.188.2.100/svn/itheima17/trunk/MobileGuard/src/com/itheima17/mobileguard/activity/Setup4Activity.java $ 
 * 
 */
public class Setup4Activity extends BaseSetupActivity {
	private CheckBox mCb_isOpen;
	private TextView mTv_desc;
	private DevicePolicyManager mDpm;
	private ComponentName mComponentName;

	@Override
	public void initView() {
		setContentView(R.layout.activity_setup4);
		mCb_isOpen = (CheckBox) findViewById(R.id.cb_setup4_isopenlostfind);
		mTv_desc = (TextView) findViewById(R.id.tv_setup4_losfinddesc);
		super.initView();
	}
	
	@Override
	public void initEvent() {
		// 添加复选框的事件
		mCb_isOpen.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// 监听复选框的状态
				//记录当前的复选框状态
				SPUtils.putBoolean(getApplicationContext(), MyContains.ISOPENLOSTFIND, isChecked);
				if (isChecked) {
					activeAdminAdvice();
					//勾选
					mTv_desc.setText("防盗保护已经开启");
					//启动服务监控几个防盗功能
					Intent service = new Intent(Setup4Activity.this,LostFindService.class);
					startService(service);
				} else {
					//没选
					Intent service = new Intent(Setup4Activity.this,LostFindService.class);
					stopService(service);
					mTv_desc.setText("防盗保护已经关闭");
				}
			}
		});
		super.initEvent();
	}
	
	protected void activeAdminAdvice() {
		// TODO Auto-generated method stub
		mDpm = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);
		mComponentName = new ComponentName(this, MyDeviceAdminReceiver.class);
		
		//判断是否激活
		if (!mDpm.isAdminActive(mComponentName)) {
			//没有激活
			 Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
             intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mComponentName);
             intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                     "一键锁屏测试描述");
             //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//在广播或服务中启动activity需要此配置
             startActivity(intent);
		}
	}

	@Override
	public void initData() {
		//初始化复选框的状态
		mCb_isOpen.setChecked(SPUtils.getBoolean(getApplicationContext(), MyContains.ISOPENLOSTFIND, false));
		super.initData();
	}

	@Override
	public void next() {
		
		if (mCb_isOpen.isChecked()) {
			//保存设置完成的状态
			SPUtils.putBoolean(getApplicationContext(), MyContains.ISSETUPFINISH, true);
			startActivity(LostFindActivity.class);
		} else {
			Toast.makeText(getApplicationContext(), "请先开启防盗保护", 0).show();
		}
		
	}

	@Override
	public void prev() {
		// TODO Auto-generated method stub
		startActivity(Setup3Activity.class);
	}
}
