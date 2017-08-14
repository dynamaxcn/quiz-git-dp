package com.itheima17.mobileguard.view;

import java.util.ArrayList;
import java.util.List;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.itheima17.mobileguard.R;
import com.itheima17.mobileguard.dao.LockDao;
import com.itheima17.mobileguard.domain.AppBean;
import com.itheima17.mobileguard.utils.PrintLog;

/**
 * @author Administrator
 * @date 2015-12-4
 * @pagename com.itheima17.mobileguard.view
 * @desc 已加锁和未加锁的基类
 * 
 * @svn author $Author: goudan $
 * @svn date $Date: 2015-12-04 16:56:07 +0800 (周五, 04 十二月 2015) $
 * @Id $Id: BaseFragment.java 98 2015-12-04 08:56:07Z goudan $
 * @version $Rev: 98 $
 * @url $URL:
 *      https://188.188.2.100/svn/itheima17/trunk/MobileGuard/src/com/itheima17
 *      /mobileguard/view/BaseFragment.java $
 * 
 */
public class BaseFragment extends Fragment {

	private List<AppBean> mAllInstalledAppInfos;

	private LockDao mLockDao = null;
	private List<String> mAllLockPacks;

	private List<AppBean> mLockedOrUnlockedAppInfos = new ArrayList<AppBean>();// 如果子类是已加锁，容器存放已加锁的数据

	// 设置所有加锁的app包名容器
	public void setLockedPacks(List<String> mAllLockPacks) {
		this.mAllLockPacks = mAllLockPacks;
	}

	// 获取所有安装的app的引用
	public void setAllInstalledAppInfos(List<AppBean> mAllInstalledAppInfos) {
		this.mAllInstalledAppInfos = mAllInstalledAppInfos;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// 只初始化一次

		super.onActivityCreated(savedInstanceState);
		PrintLog.print("onActivityCreated" + this.getClass().getName());

	}

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		PrintLog.print("onAttach" + this.getClass().getName());
	}

	private Context context;
	private MyAdapter mAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		context = getActivity();// 获取上下文
		mLockDao = new LockDao(getActivity());
		;
		mLockedOrUnlockedAppInfos.clear();
		// 数据分类
		for (AppBean bean : mAllInstalledAppInfos) {
			if (mAllLockPacks.contains(bean.getPackName())
					&& this instanceof LockedFragment) {// if
														// (mLockDao.isLock(bean.getPackName())
														// && this instanceof
														// LockedFragment) {
				// 已加锁
				mLockedOrUnlockedAppInfos.add(bean);
			} else if (!mAllLockPacks.contains(bean.getPackName())
					&& this instanceof UnlockedFragment) {
				mLockedOrUnlockedAppInfos.add(bean);
			}
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		PrintLog.print("onCreateView" + this.getClass().getName());

		StickyListHeadersListView lv = new StickyListHeadersListView(
				getActivity());

		// 设置适配器
		if (mAdapter == null) {
			mAdapter = new MyAdapter();
		}

		lv.setAdapter(mAdapter);// 设置适配器
		return lv;
	}

	private class MyAdapter extends BaseAdapter implements StickyListHeadersAdapter  {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mLockedOrUnlockedAppInfos.size();
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

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub

			ViewHolder viewHolder = null;
			if (convertView == null) {
				convertView = View.inflate(getActivity(),
						R.layout.item_applock_lv, null);

				viewHolder = new ViewHolder();
				viewHolder.iv_icon = (ImageView) convertView
						.findViewById(R.id.iv_item_applock_ll_icon);
				viewHolder.iv_isLock = (ImageView) convertView
						.findViewById(R.id.iv_item_applock_ll_islock);
				viewHolder.tv_name = (TextView) convertView
						.findViewById(R.id.tv_item_applock_ll_name);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}

			// 赋值
			final AppBean bean = mLockedOrUnlockedAppInfos.get(position);

			viewHolder.iv_icon.setImageDrawable(bean.getIcon());
			viewHolder.tv_name.setText(bean.getAppName());

			if (BaseFragment.this instanceof UnlockedFragment) {
				viewHolder.iv_isLock.setImageResource(R.drawable.lock);
			} else {
				viewHolder.iv_isLock.setImageResource(R.drawable.unlock);
			}

			final View rootView = convertView;
			viewHolder.iv_isLock.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// 判断
					mLockedOrUnlockedAppInfos.remove(bean);// 从当前容器中移除
					TranslateAnimation ta = null;
					// 加锁 还是 解锁
					if (BaseFragment.this instanceof UnlockedFragment) {
						// 当前是未加锁
						// 加锁的操作
						mLockDao.addLock(bean.getPackName());
						ta = (TranslateAnimation) AnimationUtils.loadAnimation(
								getActivity(), R.anim.lock_animation);
					} else {
						// 解锁的操作
						mLockDao.removeLock(bean.getPackName());
						ta = (TranslateAnimation) AnimationUtils.loadAnimation(
								getActivity(), R.anim.unlock_animation);
					}

					// 移除的动画
					rootView.startAnimation(ta);

					new Thread() {
						public void run() {
							SystemClock.sleep(500);
							getActivity().runOnUiThread(new Runnable() {

								@Override
								public void run() {
									// TODO Auto-generated method stub
									mAdapter.notifyDataSetChanged();// 刷新界面
								}
							});
						};
					}.start();

				}
			});
			return convertView;
		}

		@Override
		public View getHeaderView(int position, View convertView,
				ViewGroup parent) {
			//标签
			TextView tv_tag = new TextView(getActivity());
			tv_tag.setBackgroundColor(Color.GRAY);
			tv_tag.setTextColor(Color.WHITE);
			tv_tag.setTextSize(20);
			
			AppBean bean = mLockedOrUnlockedAppInfos.get(position);
			if (bean.isSystem()) {
				tv_tag.setText("系统软件");
			} else {
				tv_tag.setText("用户软件");
			}
			
			return tv_tag;
		}

		@Override
		public long getHeaderId(int position) {
			// 标签位置的tag 
			AppBean bean = mLockedOrUnlockedAppInfos.get(position);
			if (bean.isSystem()) {
				return 1;
			} else {
				return 3;
			}
			
		}

	}

	private class ViewHolder {
		ImageView iv_icon;
		TextView tv_name;
		ImageView iv_isLock;
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		PrintLog.print("onDestroy" + this.getClass().getName());
	}

	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
		PrintLog.print("onDestroyView" + this.getClass().getName());
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		PrintLog.print("onPause" + this.getClass().getName());
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		PrintLog.print("onResume" + this.getClass().getName());
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		PrintLog.print("onStart" + this.getClass().getName());
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		PrintLog.print("onStop" + this.getClass().getName());
	}

}
