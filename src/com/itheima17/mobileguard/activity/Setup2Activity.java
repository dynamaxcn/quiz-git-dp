package com.itheima17.mobileguard.activity;

import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.itheima17.mobileguard.R;
import com.itheima17.mobileguard.utils.MyContains;
import com.itheima17.mobileguard.utils.PrintLog;
import com.itheima17.mobileguard.utils.SPUtils;

/**
 * @author Administrator
 * @date 2015-11-22
 * @pagename com.itheima17.mobileguard.activity
 * @desc 手机防盗之第一个设置向导界面
 * 
 * @svn author $Author: goudan $
 * @svn date $Date: 2015-11-22 16:40:00 +0800 (周日, 22 十一月 2015) $
 * @Id $Id: Setup2Activity.java 25 2015-11-22 08:40:00Z goudan $
 * @version $Rev: 25 $
 * @url $URL:
 *      https://188.188.2.100/svn/itheima17/trunk/MobileGuard/src/com/itheima17
 *      /mobileguard/activity/Setup2Activity.java $
 * 
 */
public class Setup2Activity extends BaseSetupActivity {
	private ImageView mIv_isbind;
	
	@Override
	public void initData() {
		//初始化绑定sim图标的状态
		String simNumber = SPUtils.getString(getApplicationContext(),
				MyContains.SIMSERIALNUMBER, null);
		if (TextUtils.isEmpty(simNumber)) {
			mIv_isbind.setImageResource(R.drawable.unlock);
		} else {
			mIv_isbind.setImageResource(R.drawable.lock);
		}
			
		super.initData();
	}

	@Override
	public void initView() {
		setContentView(R.layout.activity_setup2);

		mIv_isbind = (ImageView) findViewById(R.id.iv_setup2_isbindsim);
		super.initView();
	}

	@Override
	public void next() {
		// 是否绑定sim卡
		String simNumber = SPUtils.getString(getApplicationContext(),
				MyContains.SIMSERIALNUMBER, null);
		if (TextUtils.isEmpty(simNumber)) {
			// 啥也不做
			//不存在activity之间的切换
			Toast.makeText(getApplicationContext(), "请先绑定sim卡", 0).show();
		} else {
			startActivity(Setup3Activity.class);
		}

	}

	@Override
	public void prev() {
		//
		startActivity(Setup1Activity.class);
	}

	/**
	 * 绑定解绑sim卡
	 * 
	 * @param v
	 */
	public void bindSim(View v) {
		// 绑定sim卡 :保存sim卡的序列号
		TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		// 获取sim卡的序列号
		String simSerialNumber = tm.getSimSerialNumber();
		String simNumber = SPUtils.getString(getApplicationContext(),
				MyContains.SIMSERIALNUMBER, null);
		if (TextUtils.isEmpty(simNumber)) {
			// 绑定
			SPUtils.putString(getApplicationContext(),
					MyContains.SIMSERIALNUMBER, simSerialNumber);
			// 改变图标
			mIv_isbind.setImageResource(R.drawable.lock);
		} else {
			// 解绑

			SPUtils.putString(getApplicationContext(),
					MyContains.SIMSERIALNUMBER, "");
			// 改变图标
			mIv_isbind.setImageResource(R.drawable.unlock);
		}

	}
}
