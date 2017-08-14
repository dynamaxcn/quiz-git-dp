package com.itheima17.mobileguard.activity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.Menu;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.itheima17.mobileguard.R;
import com.itheima17.mobileguard.domain.VersionBean;
import com.itheima17.mobileguard.utils.MyContains;
import com.itheima17.mobileguard.utils.PrintLog;
import com.itheima17.mobileguard.utils.SPUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

/**
 * @author Administrator
 * @date 2015-11-21
 * @pagename com.itheima17.mobileguard
 * @desc TODO
 * 
 * @svn author $Author: goudan $
 * @svn date $Date: 2015-12-03 09:34:59 +0800 (周四, 03 十二月 2015) $
 * @Id $Id: SplashActivity.java 85 2015-12-03 01:34:59Z goudan $
 * @version $Rev: 85 $
 * @url $URL:
 *      https://188.188.2.100/svn/itheima17/trunk/MobileGuard/src/com/itheima17
 *      /mobileguard/activity/SplashActivity.java $ alt + shift +　j
 */
public class SplashActivity extends Activity {

	protected static final int SUCCESS = 1;
	protected static final int ERROR = 2;
	private RelativeLayout mRl_root;
	private TextView mTv_versionCode;
	private TextView mTv_versionName;
	private AnimationSet mAs_anims;
	private int mVersionCode;
	private VersionBean mVersionBean;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();// 界面

		initData();// 数据

		startAnimation();// 开启动画

		initEvent();// 事件
		
		

	}

	/**
	 * 文件的拷贝
	 * @param string
	 */
	private void copyFile(String fileName) {
		//文件是否存在
		File file = new File("/data/data/" + getPackageName() + "/files/" + fileName);
		if (file.exists()) {// 文件存在
			PrintLog.print("文件已经存在");
			return;
		} else {
			PrintLog.print("文件拷贝");
		}
		
		 //把数据库文件从assert目录拷贝到/data/data/包名/files目录
		 AssetManager assetManager = getAssets();
		 try {
			InputStream inputStream = assetManager.open(fileName);
			
			FileOutputStream output = openFileOutput(fileName, MODE_PRIVATE);///data/data/包名/files
			
			
			byte[] buffer = new byte[1024 * 5];
			
			int len = inputStream.read(buffer);
			while (len != -1) {
				//有数据
				output.write(buffer, 0, len);
				len = inputStream.read(buffer);
			}
			
			output.close();
			inputStream.close();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void startAnimation() {
		// false 每种动画用自己的动画插入器
		mAs_anims = new AnimationSet(false);

		// 1. 创建Alpha动画

		AlphaAnimation aa = new AlphaAnimation(0.0f, 1.0f);
		// 动画时长
		aa.setDuration(2000);// 2秒时长
		aa.setFillAfter(true);// view停留在动画结束的位置

		// 添加alpha动画
		mAs_anims.addAnimation(aa);

		// 2. 旋转动画,锚点设置为中心点
		RotateAnimation ra = new RotateAnimation(0, 360,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		ra.setDuration(2000);// 2秒时长
		ra.setFillAfter(true);// view停留在动画结束的位置

		// 添加动画
		mAs_anims.addAnimation(ra);

		// 3. 比例动画
		ScaleAnimation sa = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		sa.setDuration(2000);// 2秒时长
		sa.setFillAfter(true);// view停留在动画结束的位置

		// 添加动画
		mAs_anims.addAnimation(sa);

		mRl_root.startAnimation(mAs_anims);

	}

	private void initData() {
		// 数据的获取和显示

		PackageManager pm = getPackageManager();
		try {
			// 获取数据
			PackageInfo packageInfo = pm.getPackageInfo(getPackageName(), 0);

			mVersionCode = packageInfo.versionCode;
			String versionName = packageInfo.versionName;

			// 显示
			mTv_versionCode.setText(mVersionCode + "");
			mTv_versionName.setText(versionName);
		} catch (NameNotFoundException e) {
			// can not reach
		}

	}

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {

			//
			switch (msg.what) {
			case SUCCESS:
				// 成功
				// 判断版本是否一致
				if (mVersionCode == mVersionBean.versionCode) {
					// 版本一致
					Toast.makeText(getApplicationContext(), "当前是最新版本", 0)
							.show();
					// 直接进入主界面
					loadMain();
				} else {
					// 有新版本
					showNewVersionDialog();
				}
				break;
			case ERROR:
				// 失败
				int error_code = msg.arg1;
				switch (error_code) {
				case 10081:
					// MalformedURLException
					Toast.makeText(getApplicationContext(), "10081:urlerror", 0)
							.show();
					break;
				case 10082:
					// IOException
					Toast.makeText(getApplicationContext(),
							"10082:url错误或者服务器没开启", 0).show();
					break;
				case 10083:
					// JSONException
					Toast.makeText(getApplicationContext(),
							"10081:jsonparseerror", 0).show();
					break;

				default:
					Toast.makeText(getApplicationContext(),
							"default:url错误或资源不存在", 0).show();
					break;
				} // end switch (error_code) {
					// 直接进入主界面
				loadMain();

				break;

			default:
				break;
			} // end switch (msg.what) {
		};
	};

	private void checkVersion() {
		new Thread() {

			public void run() {
				// 开始播放动画
				// 访问网络 获取版本号和版本名
				// 判断是否有新版本
				Message msg = mHandler.obtainMessage();
				msg.what = ERROR;
				// 访问网络之前记录当前时间
				long startTime = System.currentTimeMillis();
				try {
					URL url = new URL(getResources().getString(
							R.string.versionurl));
					HttpURLConnection con = (HttpURLConnection) url
							.openConnection();
					con.setConnectTimeout(5000);// 设置超时时间
					con.setRequestMethod("GET");// 设置请求方式
					int code = con.getResponseCode();// 200成功 404 找不到 500服务内部错误
					if (code == 200) {
						// 访问成功

						// 通过io获取信息json
						String jsonStr = stream2string(con.getInputStream());
						// 解析json
						mVersionBean = parseJson(jsonStr);

						// 判断是否有新版本

						// 发送消息
						msg.what = SUCCESS;

					} else {
						// 访问失败
						msg.arg1 = code;
					}
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					// e.printStackTrace();
					msg.arg1 = 10081;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					// e.printStackTrace();
					msg.arg1 = 10082;
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					// e.printStackTrace();
					msg.arg1 = 10083;
				} finally {
					long endTime = System.currentTimeMillis();
					// endTime - startTime 网络服务执行时间
					if (endTime - startTime < 2000) {
						// 延迟2秒发送消息
						SystemClock.sleep(2000 - (endTime - startTime));
					}
					// 发送消息
					mHandler.sendMessage(msg);
				}

			};
		}.start();
	}

	/**
	 * 显示下载新版本的对话框
	 */
	protected void showNewVersionDialog() {
		// TODO Auto-generated method stub
		// 对话框的上下文 必须是Activity类型
		AlertDialog.Builder ab = new AlertDialog.Builder(this);
		ab// .setCancelable(false)
		.setTitle("提醒").setMessage(mVersionBean.desc)
				.setPositiveButton("更新", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// 下载
						System.out.println("更新。。。。。");
						installLocalApk();

					}
				}).setNegativeButton("下次再说", new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// 进入主界面
						loadMain();

					}
				}).setOnCancelListener(new OnCancelListener() {

					@Override
					public void onCancel(DialogInterface dialog) {
						// 直接进入主界面
						loadMain();

					}
				}).show();

		// ab.show();//显示对话框

	}

	/**
	 * 下载新的apk
	 */
	protected void downLoadNewApk(String apkName) {

		// 第三方控件 下载 xutils
		HttpUtils httpUtils = new HttpUtils();
		httpUtils.configTimeout(5000);

		final ProgressDialog pd_showDownProgress = ProgressDialog.show(this,
				"提醒", "玩命下载中。。。。");
		// 封装了子线程访问数据，主线程处理结果
		httpUtils.download(mVersionBean.downloadUrl,
				Environment.getExternalStorageDirectory() + "/" + apkName,
				new RequestCallBack<File>() {

					@Override
					public void onSuccess(ResponseInfo<File> arg0) {
						// 下载成功
						showUpdateApkDialog();
						pd_showDownProgress.dismiss();
					}

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// 下载失败
						Toast.makeText(getApplicationContext(),
								"下载新版本失败" + arg0 + ":" + arg1, 0).show();
						// System.out.println(arg0 + ":" + arg1);
						PrintLog.print(arg0 + ":" + arg1);
						// 进入主界面
						loadMain();
						pd_showDownProgress.dismiss();

					}
				});
	}

	private void installLocalApk() {
		String apkName = mVersionBean.downloadUrl
				.substring(mVersionBean.downloadUrl.lastIndexOf('/') + 1);
		// 说明当前使用的版本不是最新版本
		File file = new File(Environment.getExternalStorageDirectory(), apkName);
		if (file.exists()) {

			// 直接安装
			showUpdateApkDialog();

		} else {
			// 下载的界面
			downLoadNewApk(apkName);
		}

	}

	/**
	 * 显示是否安装的对话框
	 */
	protected void showUpdateApkDialog() {
		AlertDialog.Builder ab = new AlertDialog.Builder(this);
		ab.setTitle("提醒").setMessage("现在安装？")
				.setPositiveButton("可以", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// 下载
						// apk的路径
						// 安装apk的意图
						/*
						 * <intent-filter> <action
						 * android:name="android.intent.action.VIEW" />
						 * <category
						 * android:name="android.intent.category.DEFAULT" />
						 * <data android:scheme="content" /> <data
						 * android:scheme="file" /> <data android:mimeType=
						 * "application/vnd.android.package-archive" />
						 * </intent-filter>
						 */
						Intent install = new Intent();
						install.setAction("android.intent.action.VIEW");
						install.addCategory("android.intent.category.DEFAULT");
						install.setDataAndType(Uri.fromFile(new File(
								Environment.getExternalStorageDirectory(),
								"sjws.apk")),
								"application/vnd.android.package-archive");

						startActivityForResult(install, 0);

					}
				}).setNegativeButton("下次再说", new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// 进入主界面
						loadMain();

					}
				}).setOnCancelListener(new OnCancelListener() {

					@Override
					public void onCancel(DialogInterface dialog) {
						// 直接进入主界面
						loadMain();

					}
				}).show();

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// 直接进入主界面
		loadMain();
		super.onActivityResult(requestCode, resultCode, data);
	}

	protected void loadMain() {
		// 进入主界面
		Intent home_activity = new Intent(this, HomeActivity.class);
		startActivity(home_activity);
		// 关闭自己
		finish();

	}

	/**
	 * @param jsonStr
	 *            解析json数据
	 * @return
	 * @throws JSONException
	 */
	protected VersionBean parseJson(String jsonStr) throws JSONException {
		// TODO Auto-generated method stub
		JSONObject jsonObject = new JSONObject(jsonStr);
		// 读取json数据
		VersionBean bean = new VersionBean();
		bean.desc = jsonObject.getString("desc");
		bean.downloadUrl = jsonObject.getString("apkdownloadurl");
		bean.versionCode = jsonObject.getInt("versioncode");
		bean.versionName = jsonObject.getString("versionname");
		return bean;
	}

	/**
	 * @param inputStream
	 *            要转换的输入流
	 * @return 字符串
	 */
	protected String stream2string(InputStream inputStream) {
		// TODO Auto-generated method stub
		// String res = "";
		StringBuilder res = new StringBuilder();// 线程非安全
		// 业务
		// 字节流转成字符流
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				inputStream));
		try {
			String line = reader.readLine();
			while (line != null) {
				// 有信息
				res.append(line);
				// 继续读取
				line = reader.readLine();
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			// 关闭流
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		return res + "";
	}

	private void initEvent() {
		// 添加动画开始和结束的事件
		mAs_anims.setAnimationListener(new MyAnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {

				//文件的拷贝
				new Thread(){
					public void run() {
						copyFile("address.db");
						copyFile("commonnum.db");
						copyFile("antivirus.db");
						
					};
				}.start();
				
				// 判断：如果要自动更新
				Boolean isUpdate = SPUtils.getBoolean(getApplicationContext(),
						MyContains.AUTOUPDATE, false);

				if (isUpdate) {
					// 开始访问服务器的线程
					checkVersion();
				} else {

				}
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				// 事件回调结果
				// 判断：如果要自动更新
				Boolean isUpdate = SPUtils.getBoolean(getApplicationContext(),
						MyContains.AUTOUPDATE, false);
				if (isUpdate) {
					// 啥也不做
				} else {
					// 直接进入主界面
					loadMain();
				}
				super.onAnimationEnd(animation);
			}
		});

	}

	private void initView() {
		setContentView(R.layout.activity_splash);

		// 获取根布局
		mRl_root = (RelativeLayout) findViewById(R.id.rl_splash_root);

		// 获取版本号
		mTv_versionCode = (TextView) findViewById(R.id.tv_splash_versioncode);

		// 获取版本名
		mTv_versionName = (TextView) findViewById(R.id.tv_splash_versionname);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.splash, menu);
		return true;
	}

	private class MyAnimationListener implements AnimationListener {

		@Override
		public void onAnimationStart(Animation animation) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onAnimationEnd(Animation animation) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onAnimationRepeat(Animation animation) {
			// TODO Auto-generated method stub

		}

	}
}
