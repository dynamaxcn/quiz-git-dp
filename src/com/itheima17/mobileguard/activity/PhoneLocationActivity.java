package com.itheima17.mobileguard.activity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.itheima17.mobileguard.R;
import com.itheima17.mobileguard.dao.PhoneLocationDao;

/**
 * @author Administrator
 * @date 2015-11-27
 * @pagename com.itheima17.mobileguard.activity
 * @desc 电话归属地查询的界面
 * 
 * @svn author $Author: goudan $
 * @svn date $Date: 2015-11-29 11:19:04 +0800 (周日, 29 十一月 2015) $
 * @Id $Id: PhoneLocationActivity.java 59 2015-11-29 03:19:04Z goudan $
 * @version $Rev: 59 $
 * @url $URL: https://188.188.2.100/svn/itheima17/trunk/MobileGuard/src/com/itheima17/mobileguard/activity/PhoneLocationActivity.java $
 * 
 */
public class PhoneLocationActivity extends Activity {
	private TextView mTv_locationMess;
	private Button mBt_query;
	private EditText mEt_number;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		initView();

		initEvent();
	}

	private void initEvent() {
		//监听文本变化
		mEt_number.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// 文本变化的回调
				//获取文本
				locationShow();
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// 文本变化之前的回调
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// 文本变化之后的回调 
				
			}
		});
		
		
		mBt_query.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				locationShow();
			}

			
		});
	}

	/**
	 * @param number
	 *            号码
	 * @return 号码格式是否正确
	 */
	private boolean isFormater(String number) {
		boolean res = true;
		Pattern p = Pattern.compile("1[35478]{1}[0-9]{9}");
		Matcher m = p.matcher(number);
		if (m.matches()) {
			// 手机格式错误

			return res;
		}

		if (number.charAt(0) == '0') {
			return res;
		}

		res = false;
		return res;
	}
	private void locationShow() {
		// 获取输入的号码
		String number = mEt_number.getText() + "";
		if (TextUtils.isEmpty(number)) {
			// Toast.makeText(getApplicationContext(), "号码不能为空",
			// 0).show();
			Animation shake = AnimationUtils.loadAnimation(PhoneLocationActivity.this,R.anim.shake);
			mEt_number.startAnimation(shake);
			
			//震动效果
			Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
			vibrator.vibrate(new long[]{200,100,300,200,200,100,100,300,220,100}, 5);
			return;
		} else if (!isFormater(number)) {
			Toast.makeText(getApplicationContext(), "格式错误", 0).show();
			return;
		} else {
			// 查询数据库
			String location = "";
			try {
				location = PhoneLocationDao.getLocation(number);
			} catch (Exception e) {
				location = "查不到";
			}
			
			// 显示
			mTv_locationMess.setText("归属地:" + location);
		}
	}

	private void initView() {
		setContentView(R.layout.activity_phonelocation);

		mTv_locationMess = (TextView) findViewById(R.id.tv_phonelocation_locationmess);
		mBt_query = (Button) findViewById(R.id.bt_phonelocation_query);
		mEt_number = (EditText) findViewById(R.id.et_phonelocation_number);
	}
}
