package com.itheima17.mobileguard.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.Formatter;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

import com.itheima17.mobileguard.R;
import com.itheima17.mobileguard.domain.AppBean;
import com.itheima17.mobileguard.utils.AppUtils;
import com.itheima17.mobileguard.utils.PrintLog;
import com.itheima17.mobileguard.view.ProgressMessageView;
import com.stericson.RootTools.RootTools;
import com.stericson.RootTools.RootToolsException;

/**
 * @author Administrator
 * @date 2015-11-26
 * @pagename com.itheima17.mobileguard.activity
 * @desc 软件管家
 * 
 * @svn author $Author: goudan $
 * @svn date $Date: 2015-11-29 11:19:04 +0800 (周日, 29 十一月 2015) $
 * @Id $Id: AppManagerActivity.java 59 2015-11-29 03:19:04Z goudan $
 * @version $Rev: 59 $
 * @url $URL:
 *      https://188.188.2.100/svn/itheima17/trunk/MobileGuard/src/com/itheima17
 *      /mobileguard/activity/AppManagerActivity.java $
 * 
 */
public class AppManagerActivity extends Activity {
	protected static final int LOADING = 1;
	protected static final int FINISH = 2;
	private ListView mLv_datas;
	private LinearLayout mLl_loading;
	private List<AppBean> mAllInstalledAppInfos;
	private List<AppBean> mUserApps = new ArrayList<AppBean>();
	private List<AppBean> mSysApps = new ArrayList<AppBean>();

	private long mRomAvail;
	private long mRomTotal;
	private long mSdAvail;
	private long mSdTotal;
	private AppBean mClickBean;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		initView();

		initEvent();

		initData();

		initPopupWindow();
		
		registAppRemoveReceiver();
	}
	
	@Override
	protected void onDestroy() {
		// 取消app删除的广播监听
		unregisterReceiver(mUninstallAppReceiver);
		super.onDestroy();
	}
	
	private void registAppRemoveReceiver() {
		//注册app删除的广播监听
		
		mUninstallAppReceiver = new UninstallAppReceiver();
		
		//app移除的广播
		IntentFilter filter = new IntentFilter(Intent.ACTION_PACKAGE_REMOVED);
		
		filter.addDataScheme("package");
		registerReceiver(mUninstallAppReceiver, filter);
	
		
	}

	//监听app卸载的广播
	private class UninstallAppReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			PrintLog.print("删除app");
			initData();
		}
		
	}

	/**
	 * 初始化弹出窗体
	 */
	private void initPopupWindow() {
		// 获取弹出窗体的根布局
		View popupView = View.inflate(getApplicationContext(),
				R.layout.popup_appmanager_view, null);

		OnClickListener listener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.ll_popup_appmanager_share:
					// 分享到公众平台
					Toast.makeText(getApplicationContext(), "分享", 0).show();
					showShare();
					break;
				case R.id.ll_popup_appmanager_start:
					startApp();
					break;
				case R.id.ll_popup_appmanager_setting:
					//设置
					appDetail();
					break;
				case R.id.ll_popup_appmanager_uninstall:
					//卸载 
					uninstallApp();
					break;
				default:
					break;
				}
				// 关闭弹出窗体
				mPw.dismiss();
			}
		};

		// 获取四个功能组件
		LinearLayout ll_uninstall = (LinearLayout) popupView
				.findViewById(R.id.ll_popup_appmanager_uninstall);

		LinearLayout ll_share = (LinearLayout) popupView
				.findViewById(R.id.ll_popup_appmanager_share);

		LinearLayout ll_start = (LinearLayout) popupView
				.findViewById(R.id.ll_popup_appmanager_start);

		LinearLayout ll_setting = (LinearLayout) popupView
				.findViewById(R.id.ll_popup_appmanager_setting);

		ll_setting.setOnClickListener(listener);
		ll_share.setOnClickListener(listener);
		ll_start.setOnClickListener(listener);
		ll_uninstall.setOnClickListener(listener);
		// 创建弹出窗体

		mPw = new PopupWindow(popupView, -2, -2);

		// 设置弹出窗体的参数
		mPw.setTouchable(true);// 内容的获取焦点
		mPw.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));// 设置背景为透明：1.
																		// 外面点击生效，2.动画生效
		mPw.setOutsideTouchable(true);// 设置窗体外面点击关闭

		// 布局添加动画
		mPw.setAnimationStyle(R.style.popupAnimation);
	}

	/**
	 * 卸载app
	 */
	protected void uninstallApp() {
		
		PrintLog.print(mClickBean.getSourceDir() + "******************");
		if (mClickBean.isSystem()) {
			//2. 系统app
			try {
				RootTools.sendShell("mount -o remount rw /system", 5000);//设置权限
				RootTools.sendShell("rm " + mClickBean.getSourceDir(), 5000);//删除app的命令
				RootTools.sendShell("mount -o remount r /system", 5000);
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			//1.用户app
			
			//打开卸载app的界面
			/*
			 *   <intent-filter>
                <action android:name="android.intent.action.VIEW" />
                <action android:name="android.intent.action.DELETE" />
                <category android:name="android.intent.category.DEFAULT" />
                <data android:scheme="package" />
            </intent-filter>
			 */
			
			Intent uninstall = new Intent();
			uninstall.setAction("android.intent.action.DELETE");
			uninstall.addCategory("android.intent.category.DEFAULT");
			uninstall.setData(Uri.parse("package:" + mClickBean.getPackName()));
			
			startActivity(uninstall);
			
			
		}
		
		
		
	}

	/**
	 * 显示app的详细信息
	 */
	protected void appDetail() {
		/* {act=android.settings.APPLICATION_DETAILS_SETTINGS 
				 dat=package:com.itheima.mobilesafe13 
				 flg=0x10800000 
				 cmp=com.android.settings/.applications.InstalledAppDetails}*/
		Intent setting = new Intent();
		setting.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
		setting.addCategory("android.intent.category.DEFAULT");
		setting.setData(Uri.parse("package:" + mClickBean.getPackName()));
		startActivity(setting);
	}

	/**
	 * 打开app
	 */
	protected void startApp() {
		// intent
		PackageManager pm = getPackageManager();
		Intent launchIntentForPackage = pm.getLaunchIntentForPackage(mClickBean
				.getPackName());
		if (launchIntentForPackage != null)
			startActivity(launchIntentForPackage);
		else {
			Toast.makeText(this, "该app没有启动界面", 1).show();
		}

	}

	private void showShare() {
		ShareSDK.initSDK(this);
		OnekeyShare oks = new OnekeyShare();
		// 关闭sso授权
		oks.disableSSOWhenAuthorize();

		// 分享时Notification的图标和文字 2.5.9以后的版本不调用此方法
		// oks.setNotification(R.drawable.ic_launcher,
		// getString(R.string.app_name));
		// title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
		oks.setTitle("17期测试分享");
		// titleUrl是标题的网络链接，仅在人人网和QQ空间使用
		oks.setTitleUrl("http://sharesdk.cn");
		// text是分享文本，所有平台都需要这个字段
		oks.setText("17期手机卫士平台分享");
		// imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
		oks.setImagePath("/sdcard/test.jpg");// 确保SDcard下面存在此张图片
		// url仅在微信（包括好友和朋友圈）中使用
		oks.setUrl("http://sharesdk.cn");
		// comment是我对这条分享的评论，仅在人人网和QQ空间使用
		oks.setComment("我是测试评论文本");
		// site是分享此内容的网站名称，仅在QQ空间使用
		oks.setSite(getString(R.string.app_name));
		// siteUrl是分享此内容的网站地址，仅在QQ空间使用
		oks.setSiteUrl("http://sharesdk.cn");

		// 启动分享GUI
		oks.show(this);
	}

	private void initEvent() {

		// 给listview添加条目点击事件
		mLv_datas.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				// 先做点击位置判断
				if (position == mUserApps.size() + 1) {
					//点击的是两个标签
					return ;
				}
				
				// 条目点击事件

				// 2. 记录点击的条目信息
				
				//System.out.println("position:" + position);
				//mLv_datas.getSelectedItem();//adapter.getItem(selection);
				mClickBean = (AppBean) mAdapter.getItem(position);

				// 1. 弹出窗体 view当前条目的view
				if (mPw.isShowing()) {
					mPw.dismiss();
				} else {
					// 显示
					mPw.showAsDropDown(view, 50, -view.getHeight());// 50px
				}

			}
		});
		// 给listview添加滑动事件
		mLv_datas.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// 监听状态

			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// 监听滑动事件

				// 监听系统软件标签的位置，如果滑动中，显示在lv中的第一个位置，标签的替换

				int firstPosition = mLv_datas.getFirstVisiblePosition();
				// mUserApps.size() + 1 系统标签的位置
				// if ((mUserApps.size() + 1) <= firstPosition) {
				if (firstPosition >= (mUserApps.size() + 1)) {
					// 假标签的显示改成系统标签
					mTv_tag.setText("系统软件");
				} else {
					// 用户标签
					mTv_tag.setText("用户软件");
				}
			}
		});

	}

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case LOADING:
				mLl_loading.setVisibility(View.VISIBLE);
				mLv_datas.setVisibility(View.GONE);
				break;
			case FINISH:
				mLl_loading.setVisibility(View.GONE);
				mLv_datas.setVisibility(View.VISIBLE);

				// rom数据的显示
				mPmv_rom.setMessage("rom可用内存："
						+ Formatter.formatFileSize(getApplicationContext(),
								mRomAvail));
				mPmv_rom.setProgress(mRomTotal - mRomAvail, mRomTotal);
				// sd数据显示
				mPmv_sd.setMessage("sd可用内存："
						+ Formatter.formatFileSize(getApplicationContext(),
								mSdAvail));
				mPmv_sd.setProgress(mSdTotal - mSdAvail, mSdTotal);

				mAdapter.notifyDataSetChanged();
				break;

			default:
				break;
			}

		};
	};
	private MyAdapter mAdapter;
	private TextView mTv_tag;
	private ProgressMessageView mPmv_rom;
	private ProgressMessageView mPmv_sd;
	private PopupWindow mPw;
	private UninstallAppReceiver mUninstallAppReceiver;

	private void initData() {
		new Thread() {

			public void run() {
				// 1. 发送加载数据的消息
				mHandler.obtainMessage(LOADING).sendToTarget();

				//清空容器中的数据
				mSysApps.clear();
				mUserApps.clear();
				// 2.获取数据
				mAllInstalledAppInfos = AppUtils
						.getAllInstalledAppInfos(getApplicationContext());
				for (AppBean bean : mAllInstalledAppInfos) {
					// 判断app类型
					if (bean.isSystem()) {
						mSysApps.add(bean);
					} else {
						mUserApps.add(bean);
					}
				}

				// 取sd和rom的信息

				mRomAvail = AppUtils.getRomAvail();
				mRomTotal = AppUtils.getRomTotal();

				mSdAvail = AppUtils.getSDAvail();
				mSdTotal = AppUtils.getSDTotal();
				PrintLog.print(mSdAvail + ":" + mSdTotal);

				// 3. 发送加载数据完成的消息
				mHandler.obtainMessage(FINISH).sendToTarget();
			};
		}.start();

	}

	private class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			if (mAllInstalledAppInfos != null) {
				return mUserApps.size() + 1 + mSysApps.size() + 1;
			}
			return 0;
		}

		@Override
		public AppBean getItem(int position) {
System.out.println("postion:" + position);			
			// 判断position
			// 如果是用户软件 mUserApps
			// 如果是系统 mSysApps

			if (position <= mUserApps.size()) {
				// 如果是用户软件 mUserApps
				return mUserApps.get(position - 1);// 0 -1
			} else {
				// 系统
				return mSysApps.get(position - mUserApps.size() - 2);// 3 - 2 - 2 = -1
			}

		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// 显示标签
			if (position == 0) {
				// 用户app标签
				TextView tv_usertag = new TextView(getApplicationContext());
				tv_usertag.setText("用户软件");
				tv_usertag.setTextColor(Color.WHITE);
				tv_usertag.setBackgroundColor(Color.GRAY);
				return tv_usertag;
			}
			if (position == mUserApps.size() + 1) {
				// 系统app标签
				TextView tv_usertag = new TextView(getApplicationContext());
				tv_usertag.setText("系统软件");
				tv_usertag.setTextColor(Color.WHITE);
				tv_usertag.setBackgroundColor(Color.GRAY);
				return tv_usertag;
			}

			ViewHolder holder = null;
			if (convertView == null || convertView instanceof TextView) {
				convertView = View.inflate(getApplicationContext(),
						R.layout.item_appmanager_lv, null);

				holder = new ViewHolder();
				holder.iv_icon = (ImageView) convertView
						.findViewById(R.id.iv_item_appmanager_lv_icon);
				holder.tv_appName = (TextView) convertView
						.findViewById(R.id.tv_item_appmanager_lv_appname);
				holder.tv_location = (TextView) convertView
						.findViewById(R.id.tv_item_appmanager_lv_location);
				holder.tv_size = (TextView) convertView
						.findViewById(R.id.tv_item_appmanager_lv_size);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			// 获取数据
			AppBean appBean = getItem(position);

			holder.tv_appName.setText(appBean.getAppName());
			if (appBean.isRom()) {
				holder.tv_location.setText("rom");
			} else {
				holder.tv_location.setText("sd");
			}

			long size = appBean.getSize();

			// 格式化大小
			String sizeStr = Formatter.formatFileSize(getApplicationContext(),
					size);
			holder.tv_size.setText(sizeStr);

			holder.iv_icon.setImageDrawable(appBean.getIcon());

			return convertView;
		}

	}

	private class ViewHolder {
		ImageView iv_icon;
		TextView tv_appName;
		TextView tv_location;
		TextView tv_size;
	}

	private void initView() {
		setContentView(R.layout.acitivity_appmanager);
		mLv_datas = (ListView) findViewById(R.id.lv_appmanager_appinfos);
		mLl_loading = (LinearLayout) findViewById(R.id.ll_black_loading);

		mPmv_rom = (ProgressMessageView) findViewById(R.id.pmv_appmanager_rom);
		mPmv_sd = (ProgressMessageView) findViewById(R.id.pmv_appmanager_sd);
		mTv_tag = (TextView) findViewById(R.id.tv_appmanager_tag);
		mAdapter = new MyAdapter();
		mLv_datas.setAdapter(mAdapter);

	}
}
