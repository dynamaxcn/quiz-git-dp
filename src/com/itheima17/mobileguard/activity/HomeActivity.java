package com.itheima17.mobileguard.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.itheima17.mobileguard.R;
import com.itheima17.mobileguard.utils.MD5Utils;
import com.itheima17.mobileguard.utils.MyContains;
import com.itheima17.mobileguard.utils.SPUtils;
import com.nineoldandroids.animation.ObjectAnimator;
import com.startapp.android.publish.StartAppAd;
import com.startapp.android.publish.StartAppSDK;

/**
 * @author Administrator
 * @date 2015-11-21
 * @pagename com.itheima17.mobileguard.activity
 * @desc 手机卫士主界面
 * 
 * @svn author $Author: goudan $
 * @svn date $Date: 2015-12-06 14:14:38 +0800 (周日, 06 十二月 2015) $
 * @Id $Id: HomeActivity.java 102 2015-12-06 06:14:38Z goudan $
 * @version $Rev: 102 $
 * @url $URL:
 *      https://188.188.2.100/svn/itheima17/trunk/MobileGuard/src/com/itheima17
 *      /mobileguard/activity/HomeActivity.java $
 * 
 */
public class HomeActivity extends Activity {
	private StartAppAd startAppAd = new StartAppAd(this);
	// 标题
	private String[] titles = new String[] { "手机防盗", "通讯卫士", "软件管家", "进程管理",
			"流量统计", "病毒查杀", "缓存清理", "高级工具" };
	// 描述信息
	private String[] descs = new String[] { "手机防盗好找", "通讯卫士安全", "软件管家好用",
			"进程管理好快", "流量统计好准", "病毒查杀安全", "缓存清理好溜", "高级工具好爽" };
	// 图标
	private int[] icons = new int[] { R.drawable.sjfd, R.drawable.srlj,
			R.drawable.rjgj, R.drawable.jcgl, R.drawable.lltj, R.drawable.sjsd,
			R.drawable.hcql, R.drawable.cygj };
	private ImageView mIv_logo;
	private ImageView mIv_setting;
	private GridView mGv_tools;
	private AlertDialog mAlertDialog;

	@Override
	public void onResume() {
		super.onResume();
		startAppAd.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
		startAppAd.onPause();
	}
	
	@Override
	public void onBackPressed() {
	    startAppAd.onBackPressed();
	    super.onBackPressed();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		// "Your Developer Id" 广告平台的开发者账号 ： 点击率
		// "Your App ID" google市场id

		StartAppSDK.init(this, "102205247", "3333", true);
		initView();

		startAnimation();

		initData();

		initEvent();

	}

	private void initEvent() {
		// 给GridView添加item单击事件
		mGv_tools.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// 单击事件
				switch (position) {
				case 0:// 手机防盗
						// 判断下是否设置过密码
					String password = SPUtils.getString(
							getApplicationContext(), MyContains.PASSWORD, "");
					if (TextUtils.isEmpty(password)) {
						// 没设置过， 显示设置密码的对话框
						showSetPassDialog();
					} else {
						// 设置过，显示输入密码的对话框
						showEnterPassDialog();
					}

					break;
				case 1:// 通讯卫士
					startPage(BlackActivity.class);
					break;
				case 2:// 软件管家
					startPage(AppManagerActivity.class);
					break;
				case 3:// 进程管家
					startPage(TaskManagerActivity.class);
					break;
				case 4:// 流量统计
					startPage(ConnectivityActivity.class);
					break;
				case 5:// 病毒查杀
					startPage(AntiVirusActivity.class);
					break;
				case 6:// 缓存清理
					startPage(CacheClearActivity.class);
					break;
				case 7:// 高级工具
					startPage(AToolActivity.class);
					break;
				default:
					break;
				}

			}
		});

		// 添加设置中心的世界
		mIv_setting.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 跳转到设置中心界面
				Intent settingCenter = new Intent(HomeActivity.this,
						SettingCenterActivity.class);
				startActivity(settingCenter);
			}
		});

	}

	private void startPage(Class type) {
		Intent intent = new Intent(this, type);
		startActivity(intent);
	}

	protected void showEnterPassDialog() {
		// TODO Auto-generated method stub
		showEnterOrSetPassDialog(0);
	}

	/**
	 * 显示设置密码的对话框
	 */
	protected void showSetPassDialog() {
		showEnterOrSetPassDialog(1);
	}

	/**
	 * @param type
	 *            1 设置密码 0输入密码
	 */
	public void showEnterOrSetPassDialog(final int type) {
		AlertDialog.Builder ab = new AlertDialog.Builder(this);

		// 设置对话框显示的View
		View view = View.inflate(getApplicationContext(),
				R.layout.dialog_setpassword_view, null);
		ab.setView(view);

		// 获取子控件
		TextView tv_title = (TextView) view
				.findViewById(R.id.tv_dialog_setpass_title);
		final EditText et_passone = (EditText) view
				.findViewById(R.id.et_dialog_setpass_passone);
		final EditText et_passtwo = (EditText) view
				.findViewById(R.id.et_dialog_setpass_passtwo);

		Button bt_enter = (Button) view
				.findViewById(R.id.bt_dialog_setpass_enter);
		Button bt_cancel = (Button) view
				.findViewById(R.id.bt_dialog_setpass_cancel);

		if (type == 0) {
			// 输入密码
			// 隐藏第二个et
			et_passtwo.setVisibility(View.GONE);// gone
			tv_title.setText("输入密码");

		}
		View.OnClickListener listener = new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.bt_dialog_setpass_enter:
					// 确认
					// 获取密码
					String passone = et_passone.getText() + "";
					String passtwo = et_passtwo.getText() + "";
					if (type == 0) {
						// 输入密码
						passtwo = "aa";
					}

					if (TextUtils.isEmpty(passtwo)
							|| TextUtils.isEmpty(passone)) {
						Toast.makeText(getApplicationContext(), "密码不能为空", 0)
								.show();
						return;
					} else if (passone.equals(passtwo) && type == 1) {
						// 保存密码
						SPUtils.putString(getApplicationContext(),
								MyContains.PASSWORD,
								MD5Utils.md5encode(MD5Utils.md5encode(passone)));
						mAlertDialog.cancel();
					} else if (!passone.equals(passtwo) && type == 1) {

						Toast.makeText(getApplicationContext(), "两次密码不一致", 0)
								.show();
						return;
					} else if (type == 0) {
						// 输入密码 pass 2次MD5加密
						String pass = SPUtils.getString(
								getApplicationContext(), MyContains.PASSWORD,
								"");
						passone = MD5Utils.md5encode(MD5Utils
								.md5encode(passone));

						if (pass.equals(passone)) {
							Intent lostfind = new Intent(HomeActivity.this,
									LostFindActivity.class);
							startActivity(lostfind);
							mAlertDialog.cancel();
						} else {
							Toast.makeText(getApplicationContext(), "密码错误", 0)
									.show();
						}

					}

					break;
				case R.id.bt_dialog_setpass_cancel:
					// 取消
					// 关闭对话框
					mAlertDialog.cancel();
					break;
				default:
					break;
				}

			}
		};
		// 添加事件
		bt_enter.setOnClickListener(listener);
		bt_cancel.setOnClickListener(listener);

		mAlertDialog = ab.create();
		mAlertDialog.show();// 显示对话框
	}

	private void initData() {
		MyAdpater adapter = new MyAdpater();
		mGv_tools.setAdapter(adapter);

	}

	/**
	 * 对logo标签实现属性动画
	 */
	private void startAnimation() {
		// 属性动画
		// setLoactionX(0,20,40,60,90,110,130,150);
		// mIv_logo.setRotationX(30);

		// setRotationX
		ObjectAnimator oa = ObjectAnimator.ofFloat(mIv_logo, "rotationY", 0,
				30, 60, 90, 120, 180, 240, 300, 360);

		// 设置动画时长
		oa.setDuration(2000);// 一次动画时长2秒
		oa.setRepeatCount(ObjectAnimator.INFINITE);// 无限次数动画
		oa.setRepeatMode(ObjectAnimator.REVERSE);

		// 开始动画
		oa.start();

	}

	private void initView() {
		setContentView(R.layout.activity_home);
		// logo
		mIv_logo = (ImageView) findViewById(R.id.iv_home_logo);

		// 设置中心
		mIv_setting = (ImageView) findViewById(R.id.iv_home_settting);

		// 主界面
		mGv_tools = (GridView) findViewById(R.id.gv_home_tools);
		//
	}

	/**
	 * @author Administrator
	 * @date 2015-11-21
	 * @pagename com.itheima17.mobileguard.activity
	 * @desc GridView的适配器
	 */
	private class MyAdpater extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return titles.length;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			convertView = View.inflate(getApplicationContext(),
					R.layout.item_home_gv, null);

			// 获取子控件
			ImageView iv_item_icon = (ImageView) convertView
					.findViewById(R.id.iv_item_home_gv);
			TextView tv_title = (TextView) convertView
					.findViewById(R.id.tv_item_home_title);
			TextView tv_desc = (TextView) convertView
					.findViewById(R.id.tv_item_home_desc);

			// 赋值
			iv_item_icon.setImageResource(icons[position]);
			tv_desc.setText(descs[position]);
			tv_title.setText(titles[position]);
			return convertView;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

	}
}
