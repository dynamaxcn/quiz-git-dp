package com.itheima17.mobileguard.activity;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.ProgressDialog;
import android.database.ContentObserver;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.FrameLayout;

import com.itheima17.mobileguard.R;
import com.itheima17.mobileguard.dao.LockDao;
import com.itheima17.mobileguard.domain.AppBean;
import com.itheima17.mobileguard.utils.AppUtils;
import com.itheima17.mobileguard.view.AppLockTagView;
import com.itheima17.mobileguard.view.AppLockTagView.OnTagChangeListener;
import com.itheima17.mobileguard.view.LockedFragment;
import com.itheima17.mobileguard.view.UnlockedFragment;

/**
 * @author Administrator
 * @date 2015-12-4
 * @pagename com.itheima17.mobileguard.activity
 * @desc 程序锁的界面

 * @svn author $Author: goudan $
 * @svn date $Date: 2015-12-04 16:56:07 +0800 (周五, 04 十二月 2015) $
 * @Id  $Id: AppLockActivity.java 98 2015-12-04 08:56:07Z goudan $
 * @version $Rev: 98 $
 * @url $URL: https://188.188.2.100/svn/itheima17/trunk/MobileGuard/src/com/itheima17/mobileguard/activity/AppLockActivity.java $ 
 * 
 */
public class AppLockActivity extends FragmentActivity {
	protected static final int LOADING = 1;
	protected static final int FINISH = 33;
	private AppLockTagView mAtv_tag;
	private FrameLayout mFl_content;
	private LockedFragment mLockedFragment;
	private UnlockedFragment mUnlockedFragment;
	private List<AppBean> mAllInstalledAppInfos;
	private LockDao mLockDao = null;
	private List<String> mAllLockPacks;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		
		initView();
		
		initEvent();
		
		initData();
		
		registContenResolver();
		
	}
	
	private void registContenResolver() {
		getContentResolver().registerContentObserver(LockDao.uri, true, new ContentObserver(new Handler()) {

			@Override
			public void onChange(boolean selfChange) {
				//数据变化的通知
				mAllLockPacks.clear();
				
				List<String> allLockPacks = mLockDao.getAllLockPacks();
				mAllLockPacks.addAll(allLockPacks);//加载新的数据
				super.onChange(selfChange);
			}
			
		});
		
	}

	private Handler mHandler = new Handler(){
		private ProgressDialog mPd;

		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case LOADING:
				mPd = ProgressDialog.show(AppLockActivity.this, "注意", "正在玩命加载数据");
				break;
			case FINISH:
				mPd.dismiss();//关闭对话
				//有数据 
				mLockedFragment.setAllInstalledAppInfos(mAllInstalledAppInfos);
				mUnlockedFragment.setAllInstalledAppInfos(mAllInstalledAppInfos);
				
				mLockedFragment.setLockedPacks(mAllLockPacks);
				mUnlockedFragment.setLockedPacks(mAllLockPacks);
				//初始化fragment的显示
				selectView(true);//左边选中（默认）
			default:
				break;
			}
		};
	};

	private void initData() {
		
		mLockDao = new LockDao(getApplicationContext());
		//子线程获取所有安装的app信息
		new Thread(){
			

			

			public void run() {
				//1. 显示加载数据进度的消息
				mHandler.obtainMessage(LOADING).sendToTarget();
				
				//2.获取所有安装的app信息
				
				mAllInstalledAppInfos = AppUtils.getAllInstalledAppInfos(getApplicationContext());
				//排序
				/*Collections.sort(mAllInstalledAppInfos, new Comparator<>() {
				})*/
				Collections.sort(mAllInstalledAppInfos);
				mAllLockPacks = mLockDao.getAllLockPacks();
				
				
				//3. 发送数据加载完成的消息
				mHandler.obtainMessage(FINISH).sendToTarget();
				
			};
		}.start();
		
	}

	private void initEvent() {
		mAtv_tag.setOnTagChangeListener(new OnTagChangeListener() {
			
			@Override
			public void tagChange(View view, boolean isLeftSelect) {
				selectView(isLeftSelect);
				
			}
		});
		
	}
	
	private void selectView(boolean isLeftSelect){
		
		//fragment的替换
		//1. fragment管理器
		FragmentManager fragmentManager = getSupportFragmentManager();
		//2. 开启事物
		FragmentTransaction transaction = fragmentManager.beginTransaction();
		//3. 替换
		if (isLeftSelect)
			transaction.replace(R.id.fl_applock_content, mUnlockedFragment);
		else 
			transaction.replace(R.id.fl_applock_content, mLockedFragment);
		//4. 提交
		transaction.commit();
	}
	
	

	private void initView() {
		// TODO Auto-generated method stub
		setContentView(R.layout.activity_applock);
		mAtv_tag = (AppLockTagView) findViewById(R.id.apv_lockOrUnlock);
		
		mFl_content = (FrameLayout) findViewById(R.id.fl_applock_content);
		
		mLockedFragment = new LockedFragment();//获取所有安装的app信息
		mUnlockedFragment = new UnlockedFragment();
	}
}
