package com.itheima17.mobileguard.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.Formatter;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SlidingDrawer;
import android.widget.SlidingDrawer.OnDrawerCloseListener;
import android.widget.SlidingDrawer.OnDrawerOpenListener;
import android.widget.TextView;
import android.widget.Toast;

import com.itheima17.mobileguard.R;
import com.itheima17.mobileguard.domain.AppBean;
import com.itheima17.mobileguard.service.LockScreenService;
import com.itheima17.mobileguard.utils.AppUtils;
import com.itheima17.mobileguard.utils.MyContains;
import com.itheima17.mobileguard.utils.PrintLog;
import com.itheima17.mobileguard.utils.SPUtils;
import com.itheima17.mobileguard.utils.TaskUtils;
import com.itheima17.mobileguard.view.ProgressMessageView;
import com.itheima17.mobileguard.view.SettingCenterItemView;
import com.itheima17.mobileguard.view.SettingCenterItemView.OnToggleChangeListener;

/**
 * @author Administrator
 * @date 2015-12-1
 * @pagename com.itheima17.mobileguard.activity
 * @desc 进程管家的界面
 * 
 * @svn author $Author: goudan $
 * @svn date $Date: 2015-12-01 15:05:57 +0800 (周二, 01 十二月 2015) $
 * @Id $Id: TaskManagerActivity.java 82 2015-12-01 07:05:57Z goudan $
 * @version $Rev: 82 $
 * @url $URL:
 *      https://188.188.2.100/svn/itheima17/trunk/MobileGuard/src/com/itheima17
 *      /mobileguard/activity/TaskManagerActivity.java $
 * 
 */
public class TaskManagerActivity extends Activity {
	protected static final int LOADING = 1;
	protected static final int FINISH = 2;
	private ListView mLv_showDatas;
	private TextView mTv_tag;
	private LinearLayout mLl_loading;
	private SlidingDrawer mSd_chouti;
	private ImageView mIv_arrow1;
	private ImageView mIv_arrow2;
	private SettingCenterItemView mSciv_showsystem;
	private SettingCenterItemView mSciv_cleartask;
	private Button mBt_selectall;
	private Button mBt_fanselect;
	private long mTotalMem;
	private long mFreeMem;

	private int mTotalApps;
	private List<AppBean> mAllRunningTaskInfos;

	// 用户
	private List<AppBean> mUserApps = new Vector<AppBean>();
	// 系统
	private List<AppBean> mSystemApps = new Vector<AppBean>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		// 界面
		initView();

		// 事件
		initEvent();

		// 数据
		initData();

		// 初始化动画
		initAnimation();

		upArrow();// 初始化向上的箭头和动画

	}

	private void upArrow() {
		// 设置图片
		mIv_arrow1.setImageResource(R.drawable.drawer_arrow_up);
		mIv_arrow2.setImageResource(R.drawable.drawer_arrow_up);
		// 开始动画
		mIv_arrow1.startAnimation(mAa1);
		mIv_arrow2.startAnimation(mAa2);

	}

	private void downArrow() {
		// 清除动画
		mIv_arrow1.clearAnimation();
		mIv_arrow2.clearAnimation();

		// 修改箭头的方向
		mIv_arrow1.setImageResource(R.drawable.drawer_arrow_down);
		mIv_arrow2.setImageResource(R.drawable.drawer_arrow_down);
	}

	private void initAnimation() {
		mAa1 = new AlphaAnimation(0.5f, 1.0f);
		mAa1.setDuration(500);
		mAa1.setRepeatCount(-1);

		mAa2 = new AlphaAnimation(1.0f, 0.5f);
		mAa2.setDuration(500);
		mAa2.setRepeatCount(-1);

	}

	private void initEvent() {
		mSciv_cleartask.setOnToggleChangeListener(new OnToggleChangeListener() {
			
			@Override
			public void onToggleChanged(SettingCenterItemView view, boolean isToggleOn) {
				if (isToggleOn) {
					Intent service = new Intent(TaskManagerActivity.this,LockScreenService.class);
					startService(service);
				} else {
					Intent service = new Intent(TaskManagerActivity.this,LockScreenService.class);
					stopService(service);
				}
			}
		});

		// 清理进程
		mIv_clean.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 清理进程
				clearTask();

			}
		});
		// 是否显示系统进程的处理
		mSciv_showsystem
				.setOnToggleChangeListener(new OnToggleChangeListener() {

					@Override
					public void onToggleChanged(SettingCenterItemView view,
							boolean isToggleOn) {
						// 保存状态到sp中
						SPUtils.putBoolean(getApplicationContext(),
								MyContains.SHOWSYSTEM, isToggleOn);

						// 更新界面
						mAdapter.notifyDataSetChanged();

					}
				});
		// 抽屉打开或关闭的事件
		mSd_chouti.setOnDrawerCloseListener(new OnDrawerCloseListener() {

			@Override
			public void onDrawerClosed() {
				// TODO Auto-generated method stub
				upArrow();
			}
		});
		mSd_chouti.setOnDrawerOpenListener(new OnDrawerOpenListener() {

			@Override
			public void onDrawerOpened() {

				downArrow();
			}
		});
		OnClickListener listener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (v == mBt_selectall) {
					// 全选
					selectAll();
				} else if (v == mBt_fanselect) {
					// 反选
					fanSelect();
				}

			}
		};
		mBt_selectall.setOnClickListener(listener);
		mBt_fanselect.setOnClickListener(listener);
		// 添加lv的item点击事件
		mLv_showDatas.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {

				// 如果是标签 返回
				if (position == 0 || position == (mUserApps.size() + 1)) {
					return;
				}
				
				
				// 让点击条目的cb勾选
				// 取当前点击条目的信息
				AppBean clickBean = mAdapter.getItem(position);
				if (isSelf(clickBean)) {
					return;
				}
				
				clickBean.setChecked(!clickBean.isChecked());

				// 界面更新
				mAdapter.notifyDataSetChanged();

			}
		});
	}

	/**
	 * 清理进程
	 */
	protected void clearTask() {
		long cleanSize = 0;// 清理的内存大小
		long cleanNum = 0;// 清理的进程个数
		AppBean bean = null;
		for (int i = 0; i < mUserApps.size(); i++) {
			bean = mUserApps.get(i);
			if (bean.isChecked()) {
				cleanSize += bean.getMemSize();
				cleanNum++;
				// 清理
				mAm.killBackgroundProcesses(bean.getPackName());

				mUserApps.remove(i--);// 后面数据迁移

			}
		}
		if (SPUtils.getBoolean(getApplicationContext(), MyContains.SHOWSYSTEM,
				true)) {
			for (int i = 0; i < mSystemApps.size(); i++) {
				bean = mSystemApps.get(i);
				if (bean.isChecked()) {
					cleanSize += bean.getMemSize();
					cleanNum++;
					// 清理
					mAm.killBackgroundProcesses(bean.getPackName());

					mSystemApps.remove(i--);// 后面数据迁移

				}
			}
		}
		
		
		//清理完毕
		//更新界面
		//mAdapter.notifyDataSetChanged();
		setViewData();
		String cleanSizeStr = Formatter.formatFileSize(getApplicationContext(), cleanSize);
		Toast.makeText(getApplicationContext(), "清理了" + cleanNum + "个进程,释放了" + cleanSizeStr, 1).show();

	}

	protected void fanSelect() {
		// 反选
		if (SPUtils.getBoolean(this, MyContains.SHOWSYSTEM, true)) {
			// 显示系统进程
			for (AppBean bean : mUserApps) {
				if (isSelf(bean)){
					continue;
				}
				bean.setChecked(!bean.isChecked());
			}

			for (AppBean bean : mSystemApps) {
				bean.setChecked(!bean.isChecked());
			}
		} else {
			// 只显示用户
			for (AppBean bean : mUserApps) {
				if (isSelf(bean)){
					continue;
				}
				bean.setChecked(!bean.isChecked());
			}
		}

		// 更新界面
		mAdapter.notifyDataSetChanged();
	}

	/**
	 * 全选
	 */
	protected void selectAll() {
		if (SPUtils.getBoolean(this, MyContains.SHOWSYSTEM, true)) {
			// 显示系统进程
			for (AppBean bean : mUserApps) {
				if (isSelf(bean)){
					continue;
				}
				bean.setChecked(true);
			}

			for (AppBean bean : mSystemApps) {
				bean.setChecked(true);
			}
		} else {
			// 只显示用户
			for (AppBean bean : mUserApps) {
				if (isSelf(bean)){
					continue;
				}
				bean.setChecked(true);
			}
		}

		// 更新界面
		mAdapter.notifyDataSetChanged();

	}

	private void isViewShow(View v, boolean show) {
		if (show) {
			v.setVisibility(View.VISIBLE);
		} else {
			v.setVisibility(View.GONE);
		}
	}

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case LOADING:
				// 正在加载数据
				isViewShow(mLl_loading, true);
				// 三个隐藏
				isViewShow(mTv_tag, false);
				isViewShow(mLv_showDatas, false);
				isViewShow(mSd_chouti, false);
				break;
			case FINISH:
				// 加载数据完成

				// 隐藏
				isViewShow(mLl_loading, false);
				// 三个显示
				isViewShow(mTv_tag, true);
				isViewShow(mLv_showDatas, true);
				isViewShow(mSd_chouti, true);

				// 数据的处理
				setViewData();
				break;

			default:
				break;
			}
		}

		
	};
	private ProgressMessageView mPmv_showtasknumber;
	private ProgressMessageView mPmv_mem;
	private MyAdapter mAdapter;
	private AlphaAnimation mAa1;
	private AlphaAnimation mAa2;
	private ImageView mIv_clean;
	private ActivityManager mAm;
	private void setViewData() {
		// 设置组件的数据

		// 1. 进程个数的显示
		mPmv_showtasknumber.setMessage("运行中的进程:"
				+ mAllRunningTaskInfos.size());
		mPmv_showtasknumber.setProgress(mAllRunningTaskInfos.size(),
				mTotalApps);

		// 2. 显示内存信息
		String freeStr = Formatter.formatFileSize(getApplicationContext(),
				mFreeMem);
		String totalStr = Formatter.formatFileSize(getApplicationContext(),
				mTotalMem);
		mPmv_mem.setMessage("可用内存/总内存:" + freeStr + "/" + totalStr);
		mPmv_mem.setProgress(mTotalMem - mFreeMem, mTotalMem);

		// 3.进程的显示
		mAdapter.notifyDataSetChanged();

		// 4. 设置假标签的值
		mTv_tag.setText("用户软件(" + mUserApps.size() + ")");

	};
	
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		initData();
		super.onStart();
	}
	private void initData() {

		// 设置是否显示系统进程的标记
		mSciv_showsystem.isToggleOn(SPUtils.getBoolean(getApplicationContext(),
				MyContains.SHOWSYSTEM, true));

		new Thread() {

			public void run() {
				synchronized (mSystemApps) {
					// 1. 发送加载数据消息
					mHandler.obtainMessage(LOADING).sendToTarget();

					// 2. 获取数据

					mSystemApps.clear();
					mUserApps.clear();
				
					// 总app个数
					mTotalApps = AppUtils.getAllInstalledAppInfos(
							getApplicationContext()).size();
					mTotalMem = TaskUtils.getTotalMem(getApplicationContext());
					mFreeMem = TaskUtils.getFreeMem(getApplicationContext());
					mAllRunningTaskInfos = TaskUtils
							.getAllRunningTaskInfos(getApplicationContext());
					// 对信息的分类
					for (AppBean bean : mAllRunningTaskInfos) {
						if (bean.isSystem()) {
							mSystemApps.add(bean);
						} else {
							mUserApps.add(bean);
						}
					}


					// 3. 发送数据加载完成的消息
					mHandler.obtainMessage(FINISH).sendToTarget();
				}
				
			};
		}.start();
	}

	private class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// 所有app 只显示用户
			Boolean showSystem = SPUtils.getBoolean(getApplicationContext(),
					MyContains.SHOWSYSTEM, true);
			if (showSystem) {
				// 显示系统进程 view data
				return mUserApps.size() + mSystemApps.size() + 2;
			} else {
				return mUserApps.size() + 1;
			}
		}

		@Override
		public AppBean getItem(int position) {
			// TODO Auto-generated method stub
			if (position <= mUserApps.size()) {
				// 如果是用户软件 mUserApps
				return mUserApps.get(position - 1);// 0 -1
			} else {
				// 系统
				return mSystemApps.get(position - mUserApps.size() - 2);// 3 - 2
																		// - 2 =
																		// -1
			}
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			//PrintLog.print(convertView + "convertView");
			// TODO Auto-generated method stub
			// 显示标签
			if (position == 0) {
				// 用户app标签
				TextView tv_usertag = new TextView(getApplicationContext());
				tv_usertag.setText("用户软件(" + mUserApps.size() + ")");
				tv_usertag.setTextColor(Color.WHITE);
				tv_usertag.setBackgroundColor(Color.GRAY);
				return tv_usertag;
			}
			if (position == mUserApps.size() + 1) {
				// 系统app标签
				TextView tv_usertag = new TextView(getApplicationContext());
				tv_usertag.setText("系统软件(" + mSystemApps.size() + ")");
				tv_usertag.setTextColor(Color.WHITE);
				tv_usertag.setBackgroundColor(Color.GRAY);
				return tv_usertag;
			}
			ViewHolder viewHolder;// 目的减少findViewByid的次数 0.234秒
			// convertView 缓存的界面是TextView
			if (convertView == null || convertView instanceof TextView) {
				// 没有界面复用
				convertView = View.inflate(getApplicationContext(),
						R.layout.item_taskmanager_lv, null);

				viewHolder = new ViewHolder();

				viewHolder.iv_icon = (ImageView) convertView
						.findViewById(R.id.iv_item_taskmanager_lv_icon);
				viewHolder.tv_memsize = (TextView) convertView
						.findViewById(R.id.tv_item_taskmanager_lv_memsize);
				viewHolder.tv_name = (TextView) convertView
						.findViewById(R.id.tv_item_taskmanager_lv_taskname);
				viewHolder.cb_checked = (CheckBox) convertView
						.findViewById(R.id.cb_item_taskmanager_lv_checked);
				convertView.setTag(viewHolder);
			} else {
				// 界面复用
				viewHolder = (ViewHolder) convertView.getTag();
			}

			// 数据
			AppBean bean = getItem(position);

			// 赋值
			viewHolder.iv_icon.setImageDrawable(bean.getIcon());// 图标
			viewHolder.tv_name.setText(bean.getAppName());// 名字
			viewHolder.tv_memsize.setText(Formatter.formatFileSize(
					getApplicationContext(), bean.getMemSize()));

			//是否是自己
			//是  不显示checkbox
			if (isSelf(bean)) {
				isViewShow(viewHolder.cb_checked, false);
			} else {
				//不是
				isViewShow(viewHolder.cb_checked, true);
			}
			// 复选框的事件处理
			viewHolder.cb_checked.setChecked(bean.isChecked());

			return convertView;
		}

		

	}
	private boolean isSelf(AppBean bean) {
		return bean.getPackName().equals(getPackageName());
	}

	private class ViewHolder {
		ImageView iv_icon;
		TextView tv_name;
		TextView tv_memsize;
		CheckBox cb_checked;
	}

	private void initView() {
		// 界面
		setContentView(R.layout.acitivity_taskmanager);

		mLv_showDatas = (ListView) findViewById(R.id.lv_taskmanager_taskinfos);

		mAdapter = new MyAdapter();
		mLv_showDatas.setAdapter(mAdapter);
		mTv_tag = (TextView) findViewById(R.id.tv_taskmanager_tag);

		mLl_loading = (LinearLayout) findViewById(R.id.ll_black_loading);

		mSd_chouti = (SlidingDrawer) findViewById(R.id.sd_taskmanager);

		// 获取抽屉把手子控件

		mIv_arrow1 = (ImageView) findViewById(R.id.iv_taskmanager_arraw1);
		mIv_arrow2 = (ImageView) findViewById(R.id.iv_taskmanager_arraw2);

		// 获取抽屉内容的子控件

		mSciv_showsystem = (SettingCenterItemView) findViewById(R.id.sciv_taskmanager_showsystem);
		mSciv_cleartask = (SettingCenterItemView) findViewById(R.id.sciv_taskmanager_cleartask);

		// 获取全选和反选的按钮

		mBt_selectall = (Button) findViewById(R.id.bt_taskmanager_selectAll);
		mBt_fanselect = (Button) findViewById(R.id.bt_taskmanager_fanselect);

		// 两个显示进程信息的组件

		mPmv_showtasknumber = (ProgressMessageView) findViewById(R.id.pmv_taskmanager_tasknumber);
		mPmv_mem = (ProgressMessageView) findViewById(R.id.pmv_taskmanager_mem);

		// 清理进程的按钮

		mIv_clean = (ImageView) findViewById(R.id.iv_taskmanager_clean);

		mAm = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
	}
}
