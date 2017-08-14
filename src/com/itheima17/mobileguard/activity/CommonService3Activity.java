package com.itheima17.mobileguard.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.itheima17.mobileguard.R;
import com.itheima17.mobileguard.dao.PhoneLocationDao;
import com.itheima17.mobileguard.domain.CommonBean;
import com.itheima17.mobileguard.domain.CommonTagBean;

/**
 * @author Administrator
 * @date 2015-11-29
 * @pagename com.itheima17.mobileguard.activity
 * @desc 公共服务号码显示
 * 
 * @svn author $Author: goudan $
 * @svn date $Date: 2015-11-29 15:55:20 +0800 (周日, 29 十一月 2015) $
 * @Id $Id: CommonService3Activity.java 62 2015-11-29 07:55:20Z goudan $
 * @version $Rev: 62 $
 * @url $URL:
 *      https://188.188.2.100/svn/itheima17/trunk/MobileGuard/src/com/itheima17
 *      /mobileguard/activity/CommonServiceActivity.java $
 * 
 */
public class CommonService3Activity extends Activity {
	protected static final int LOADING = 1;
	protected static final int FINISH = 2;
	private ExpandableListView mElv_showDatas;
	private List<CommonTagBean> mAllCommons;

	private List<List<CommonBean>> mServiceNumberPerTypeDatas = new ArrayList<List<CommonBean>>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		initView();

		initData();

	}

	
	private LinearLayout mLl_loading;
	private MyAdapter mAdapter;

	private void initData() {
		// 初始化数据
		new Thread() {

			public void run() {
				// 1. 发送加载进度的消息
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						
						mLl_loading.setVisibility(View.VISIBLE);
						mElv_showDatas.setVisibility(View.GONE);
					}
				});
			
				

				// 2. 加载数据
				// 1. 服务类型
				mAllCommons = PhoneLocationDao.getAllCommons();
				// 2. 每种服务的服务号码
				for (CommonTagBean bean : mAllCommons) {
					// 获取具体服务的服务号码集
					List<CommonBean> allCommonBeans = PhoneLocationDao
							.getAllCommonBeans(bean.getTable_id());
					// 添加所有服务号码集
					mServiceNumberPerTypeDatas.add(allCommonBeans);
				}

				runOnUiThread(new  Runnable() {
					public void run() {
						mLl_loading.setVisibility(View.GONE);
						mElv_showDatas.setVisibility(View.VISIBLE);

						// /发送数据的更新事件
						mAdapter.notifyDataSetChanged();
					}
				});

			};
		}.start();

	}

	private class MyAdapter extends BaseExpandableListAdapter {

		@Override
		public int getGroupCount() {
			// 获取服务类型的数量
			if (mAllCommons != null)
				return mAllCommons.size();
			return 0;
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			if (mServiceNumberPerTypeDatas != null) {
				// 每种服务的服务号码数量
				return mServiceNumberPerTypeDatas.get(groupPosition).size();
			}
			return 0;
		}

		@Override
		public Object getGroup(int groupPosition) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Object getChild(int groupPosition, int childPosition) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getGroupId(int groupPosition) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public boolean hasStableIds() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			// 显示组的view
			// 界面缓存复用
			if (convertView == null) {
				convertView = getGenericView();// TextView
			}

			// 获取数据
			CommonTagBean commonTagBean = mAllCommons.get(groupPosition);
			convertView.setBackgroundColor(Color.BLUE);

			((TextView) convertView).setText(commonTagBean.getTypeName());
			return convertView;
		}

		private TextView getGenericView() {
			AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(
					ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);

			TextView textView = new TextView(CommonService3Activity.this);
			textView.setLayoutParams(layoutParams);

			textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
			textView.setTextSize(24);//像素
			textView.setPadding(40, 0, 0, 0);
			// textView.setText(string);
			return textView;
		}

		@Override
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {

			// 显示组的view
			// 界面缓存复用
			if (convertView == null) {
				convertView = getGenericView();// TextView
			}

			// 获取数据
			//CommonTagBean commonTagBean = mAllCommons.get(groupPosition);
			final CommonBean commonBean = mServiceNumberPerTypeDatas.get(groupPosition).get(childPosition);
			
			((TextView) convertView).setText(commonBean.getName() + "--" + commonBean.getNumber());
			convertView.setClickable(true);
			convertView.setBackgroundResource(R.drawable.settingcenter_item_middle_selector);
			
			//添加拨打电话的事件
			convertView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					//拨打电话
					Intent dial = new Intent(Intent.ACTION_CALL);
					//行 开发
					dial.setData(Uri.parse("tel:" + commonBean.getNumber()) );
					startActivity(dial);
					
				}
			});
			return convertView;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			// TODO Auto-generated method stub
			return true;
		}

	}

	private void initView() {
		setContentView(R.layout.activity_commonservice);

		mElv_showDatas = (ExpandableListView) findViewById(R.id.elv_commonservice_showdatas);
		mAdapter = new MyAdapter();
		mElv_showDatas.setAdapter(mAdapter);
		mLl_loading = (LinearLayout) findViewById(R.id.ll_black_loading);

	}
}
