package com.itheima17.mobileguard.activity;

import java.util.List;

import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.itheima17.mobileguard.R;
import com.itheima17.mobileguard.dao.ContactsDao;
import com.itheima17.mobileguard.domain.ContactBean;
import com.itheima17.mobileguard.utils.MyContains;
import com.itheima17.mobileguard.utils.SPUtils;

/**
 * @author Administrator
 * @date 2015-11-22
 * @pagename com.itheima17.mobileguard.activity
 * @desc 手机防盗之第一个设置向导界面
 * 
 * @svn author $Author: goudan $
 * @svn date $Date: 2015-11-23 10:45:42 +0800 (周一, 23 十一月 2015) $
 * @Id $Id: Setup3Activity.java 27 2015-11-23 02:45:42Z goudan $
 * @version $Rev: 27 $
 * @url $URL:
 *      https://188.188.2.100/svn/itheima17/trunk/MobileGuard/src/com/itheima17
 *      /mobileguard/activity/Setup3Activity.java $
 * 
 */
public class Setup3Activity extends BaseSetupActivity {
	private EditText mEt_safeNumber;

	@Override
	public void initData() {
		// 初始化显示安全号码
		String number = SPUtils.getString(getApplicationContext(),
				MyContains.SAFENUMBER, "");
		mEt_safeNumber.setText(number);
		// 设置et的光标位置为文本的后面
		mEt_safeNumber.setSelection(number.length());
		super.initData();
	}

	@Override
	public void initView() {
		setContentView(R.layout.activity_setup3);
		mEt_safeNumber = (EditText) findViewById(R.id.et_setup3_safenumber);

		super.initView();
	}

	/**
	 * 选择安全号码
	 * 
	 * @param v
	 */
	public void selectSafeNumber(View v) {
		// 打开新界面显示所有好友信息
		// 选择好友 关闭好友界面 回到本界面
		Intent intent = new Intent(this, ContactsActivity.class);
		startActivityForResult(intent, 0);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (data != null) {
			String phone = data.getStringExtra("phone");
			mEt_safeNumber.setText(phone);// 显示安全号码
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void next() {
		String safeNumber = mEt_safeNumber.getText() + "";
		if (TextUtils.isEmpty(safeNumber)) {
			Toast.makeText(getApplicationContext(), "安全号码不能为空", 0).show();
			return;
		} else {
			// 有安全号码
			// 保存安全号码
			SPUtils.putString(getApplicationContext(), MyContains.SAFENUMBER,
					safeNumber);
			startActivity(Setup4Activity.class);
		}

	}

	@Override
	public void prev() {
		// TODO Auto-generated method stub
		startActivity(Setup2Activity.class);
	}
}
