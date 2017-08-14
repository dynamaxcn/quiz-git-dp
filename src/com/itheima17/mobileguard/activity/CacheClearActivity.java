package com.itheima17.mobileguard.activity;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.os.SystemClock;
import android.text.format.Formatter;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.itheima17.mobileguard.R;
import com.itheima17.mobileguard.domain.AppBean;
import com.itheima17.mobileguard.utils.AppUtils;

/**
 * @author Administrator
 * @date 2015-12-3
 * @pagename com.itheima17.mobileguard.activity
 * @desc 清理缓存界面
 * 
 * @svn author $Author: goudan $
 * @svn date $Date: 2015-12-04 10:59:49 +0800 (周五, 04 十二月 2015) $
 * @Id $Id: CacheClearActivity.java 94 2015-12-04 02:59:49Z goudan $
 * @version $Rev: 94 $
 * @url $URL:
 *      https://188.188.2.100/svn/itheima17/trunk/MobileGuard/src/com/itheima17
 *      /mobileguard/activity/CacheClearActivity.java $
 * 
 */
public class CacheClearActivity extends Activity {
	protected static final int SCANING = 1;
	protected static final int FINISH = 2;
	protected static final int BEGIN = 3;
	
	private int counts = 0;//记录线程走完的个数

	private List<CacheBean> mAllCacheInfos = new ArrayList<CacheClearActivity.CacheBean>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mPm = getPackageManager();
		initView();
		initAnimation();

		initData();
		
		initEvent();

	}

	private void initEvent() {
		mIv_clean.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//清理所有缓存
				clearCache();
				
			}
		});
		
	}

	protected void clearCache() {
		
		// 反射调用PackageManager类的getPackageSizeInfo
				final IPackageDataObserver.Stub mStatsObserver = new IPackageDataObserver.Stub() {

					@Override
					public void onRemoveCompleted(String packageName,
							boolean succeeded) throws RemoteException {
						// TODO Auto-generated method stub
						//让程序员处理回调结果 
						runOnUiThread(new Runnable() {
							
							@Override
							public void run() {
								// TODO Auto-generated method stub
								Toast.makeText(getApplicationContext(), "清理完毕", 1).show();
								//清理缓存列表
								mLl_results.removeAllViews();
								
							}
						});
						
					}

				
				};
				try {
					// 1.class
					Class clazz = mPm.getClass();
					// 2.method
					Method method = clazz.getDeclaredMethod("freeStorageAndNotify",
							long.class, IPackageDataObserver.class);
					// 3. obj mPm
					// 4.invoke
					//Long.MAX_VALUE 清理的缓存大小 清理所有缓存
					method.invoke(mPm,Long.MAX_VALUE, mStatsObserver);
				} catch (Exception e) {
					e.printStackTrace();
				}
		
	}

	private void initAnimation() {
		mRa = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f,
				Animation.RELATIVE_TO_SELF, 0.5f);
		mRa.setDuration(2000);
		mRa.setRepeatCount(-1);
		mRa.setInterpolator(new Interpolator() {

			@Override
			public float getInterpolation(float input) {
				// 数学函数
				// y = 2 * x
				return 1 * input;
			}
		});

	}

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case BEGIN:
				// 开始扫描
				mIv_scan.startAnimation(mRa);// 开始动画
				break;
			case SCANING:
			{
				// 正在扫描
				AppBean bean = (AppBean) msg.obj;
				
				mTv_scanInfo.setText("扫描：" + bean.getAppName());
				mPb_progress.setMax(msg.arg1);//总进度
				mPb_progress.setProgress(msg.arg2);//当前进度
				
				View view = View.inflate(getApplicationContext(), R.layout.item_antivirus_ll, null);
				ImageView iv_icon = (ImageView) view.findViewById(R.id.iv_item_antivirus_ll_icon);
				TextView tv_name = (TextView) view.findViewById(R.id.tv_item_antivirus_ll_name);
				view.findViewById(R.id.iv_item_antivirus_ll_isVirus).setVisibility(View.GONE);
				
				iv_icon.setImageDrawable(bean.getIcon());
				tv_name.setText(bean.getAppName());
				
				//添加到LinearLayout中 
				mLl_results.addView(view,0);
				
				break;
			}
			case FINISH:
				mLl_results.removeAllViews();
				// 扫描完成
				mIv_scan.clearAnimation();// 清除动画
				
				//判断缓存信息的容器 是否有缓存信息
				if(mAllCacheInfos.size() == 0) {
					Toast.makeText(getApplicationContext(), "您的手机比脸都干净", 1).show();
				} else {
					//显示缓存信息
					for (CacheBean cachebean : mAllCacheInfos) {
						//取所有缓存信息
						View cacheview = View.inflate(getApplicationContext(), R.layout.item_cache_ll, null);
						ImageView iv_icon = (ImageView) cacheview.findViewById(R.id.iv_item_cache_ll_icon);
						TextView tv_name = (TextView) cacheview.findViewById(R.id.tv_item_cache_ll_name);
						TextView tv_cachesize = (TextView) cacheview.findViewById(R.id.tv_item_cache_ll_cachesize);
						
						iv_icon.setImageDrawable(cachebean.icon);
						tv_name.setText(cachebean.appName);
						tv_cachesize.setText(Formatter.formatFileSize(getApplicationContext(), cachebean.cacheSize));
						
						//添加
						mLl_results.addView(cacheview,0);
					}
				}
				break;
			default:
				break;
			}
		};
	};
	private RotateAnimation mRa;
	private ImageView mIv_scan;
	private PackageManager mPm;
	private TextView mTv_scanInfo;
	private ProgressBar mPb_progress;
	private LinearLayout mLl_results;
	private ImageView mIv_clean;

	private void initData() {
		new Thread() {
			public void run() {
				// 1. 开始扫描
				mHandler.obtainMessage(BEGIN).sendToTarget();

				// 2.耗时 获取每个app的缓存信息
				// a. 获取所有安装的app
				List<AppBean> allInstalledAppInfos = AppUtils
						.getAllInstalledAppInfos(getApplicationContext());
				int currentProgress = 0;
				for (AppBean bean : allInstalledAppInfos) {
					//或缓存大小 
					getCacheMessage(bean);
					
					//发送正在扫描信息
					Message msg = mHandler.obtainMessage(SCANING);
					msg.obj = bean;
					msg.arg1 = allInstalledAppInfos.size();//设置总进度
					msg.arg2 = ++currentProgress;
					
					mHandler.sendMessage(msg);
					
					SystemClock.sleep(20);//200毫秒扫描一次 
				}
				// 判断counts 值是否和allInstalledAppInfos.size一致
				while (counts != allInstalledAppInfos.size()) {
					//等待计算缓存大小的子线程是否走完
					SystemClock.sleep(1);
				}

				// 3. 完成
				mHandler.obtainMessage(FINISH).sendToTarget();

			};
		}.start();

	}

	private void getCacheMessage(final AppBean appBean) {
		// 反射调用PackageManager类的getPackageSizeInfo
		final IPackageStatsObserver.Stub mStatsObserver = new IPackageStatsObserver.Stub() {

			@Override
			public void onGetStatsCompleted(final PackageStats pStats,
					boolean succeeded) throws RemoteException {
				// 如果有缓存 保存到容器中

				if (pStats.cacheSize > 0) {
					// 有缓存
					CacheBean bean = new CacheBean();
					bean.cacheSize = pStats.cacheSize;
					bean.icon = appBean.getIcon();
					bean.appName = appBean.getAppName();
					mAllCacheInfos.add(bean);// 添加缓存信息
				}
				
				//线程走完
				synchronized (CacheClearActivity.this) {
					counts++;
				}
				
			}

		};
		try {
			// 1.class
			Class clazz = mPm.getClass();
			// 2.method
			Method method = clazz.getDeclaredMethod("getPackageSizeInfo",
					String.class, IPackageStatsObserver.class);
			// 3. obj mPm
			// 4.invoke
			method.invoke(mPm, appBean.getPackName(), mStatsObserver);
		} catch (Exception e) {

		}

	}

	private class CacheBean {
		Drawable icon;
		String appName;
		long cacheSize;
	}

	private void initView() {
		setContentView(R.layout.activity_cacheclear);
		mIv_scan = (ImageView) findViewById(R.id.iv_cacheclear_scan);
		
		mTv_scanInfo = (TextView) findViewById(R.id.tv_cacheclear_sanappinfo);
		mPb_progress = (ProgressBar) findViewById(R.id.pb_cacheclear_progress);
		
		mLl_results = (LinearLayout) findViewById(R.id.ll_cacheclear_result);
		
		mIv_clean = (ImageView) findViewById(R.id.iv_cacheclear_clear);

	}
}
