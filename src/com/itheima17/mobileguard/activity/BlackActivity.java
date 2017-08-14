package com.itheima17.mobileguard.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.itheima17.mobileguard.R;
import com.itheima17.mobileguard.dao.BlackDao;
import com.itheima17.mobileguard.db.BlackDB;
import com.itheima17.mobileguard.domain.BlackBean;
import com.itheima17.mobileguard.utils.PrintLog;

/**
 * @author Administrator
 * @date 2015-11-25
 * @pagename com.itheima17.mobileguard.activity
 * @desc 黑名单数据显示界面
 * 
 * @svn author $Author: goudan $
 * @svn date $Date: 2015-11-30 15:54:20 +0800 (周一, 30 十一月 2015) $
 * @Id $Id: BlackActivity.java 74 2015-11-30 07:54:20Z goudan $
 * @version $Rev: 74 $
 * @url $URL:
 *      https://188.188.2.100/svn/itheima17/trunk/MobileGuard/src/com/itheima17
 *      /mobileguard/activity/BlackActivity.java $
 * 
 */
public class BlackActivity extends Activity {
	protected static final int LOADING = 1;
	protected static final int FINISH = 2;
	private ImageView mIv_addBlack;
	private List<BlackBean> mMoreData;
	private View mPopupView;
	private PopupWindow mPw;
	private ScaleAnimation mSa;
	private List<BlackBean> mDatas = new ArrayList<BlackBean>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		initView();

		// 初始化弹出窗体
		initPopupWindow();

		// 初始化弹出窗体显示的动画
		initPopupWindowAnimation();

		// 初始化对话框（添加黑名单）
		initDialog();

		initEvent();

		// 初始化数据
		initData();
	}

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			// 运行在主线程中
			switch (msg.what) {
			case LOADING:
				// 正在加载数据
				// 处理界面的显示
				// 显示加载数据进度
				mLl_loading.setVisibility(View.VISIBLE);
				// 隐藏listview
				mLv_showDatas.setVisibility(View.GONE);

				// 隐藏imageview
				mIv_nodata.setVisibility(View.GONE);
				break;
			case FINISH:
				// 数据加载完成
				// 隐藏加载数据进度
				mLl_loading.setVisibility(View.GONE);
				// 判断是否有数据
				if (mDatas.isEmpty()) {
					// 没有数据
					// 隐藏listview
					mLv_showDatas.setVisibility(View.GONE);
					// 显示imageview
					mIv_nodata.setVisibility(View.VISIBLE);

				} else {
					// 有数据
					// 显示listview
					mLv_showDatas.setVisibility(View.VISIBLE);
					// 隐藏imageview
					mIv_nodata.setVisibility(View.GONE);

					// 数据的更新
					mAdapter.notifyDataSetChanged();

					// 判断mMoreData 是否为空
					if (mMoreData.size() == 0) {
						Toast.makeText(getApplicationContext(), "没有更多数据", 0)
								.show();
					}
				}
				break;
			default:
				break;
			}
		};
	};
	private BlackDao mBlackDao;
	private ListView mLv_showDatas;
	private LinearLayout mLl_loading;
	private ImageView mIv_nodata;
	private MyAdapter mAdapter;
	private AlertDialog mAlertDialog;
	private EditText mEt_number;

	private void initData() {
		// 子线程获取数据
		new Thread() {

			public void run() {
				// 1. 发送数据正在加载的消息
				mHandler.obtainMessage(LOADING).sendToTarget();

				// 2.获取数据
				// mDatas = mBlackDao.getAllDatas();

				mMoreData = mBlackDao.getMoreData(mDatas.size());
				// 添加到mDatas中
				mDatas.addAll(mMoreData);

				//SystemClock.sleep(1000);

				// 3. 数据加载完成
				mHandler.obtainMessage(FINISH).sendToTarget();

			};
		}.start();
	}

	// listView适配器
	private class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			if (mDatas != null)
				return mDatas.size();

			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// 界面的缓存复用
			ViewHolder viewHolder = null;
			if (convertView == null) {
				// 没有界面复用
				convertView = View.inflate(getApplicationContext(),
						R.layout.item_black_listview, null);
				viewHolder = new ViewHolder();// 存放自控键的复用
				viewHolder.tv_number = (TextView) convertView
						.findViewById(R.id.tv_item_black_lv_number);
				viewHolder.tv_model = (TextView) convertView
						.findViewById(R.id.tv_item_black_lv_model);
				viewHolder.iv_delete = (ImageView) convertView
						.findViewById(R.id.iv_item_black_lv_delete);

				// 绑定到convertView组件
				convertView.setTag(viewHolder);
			} else {
				// 有界面复用
				viewHolder = (ViewHolder) convertView.getTag();
			}

			// 不管是否有缓存 都要findViewByid
			// 获取子控件

			// 设置数据
			final BlackBean bean = mDatas.get(position);

			viewHolder.tv_number.setText(bean.getNumber());
			String model = "";
			switch (bean.getModel()) {
			case BlackDB.MODEL_SMS:
				// 短信拦截
				model = "短信拦截";
				break;
			case BlackDB.MODEL_PHONE:
				// 电话拦截
				model = "电话拦截";
				break;
			case BlackDB.MODEL_ALL:
				// 全部拦截
				model = "全部拦截";
				break;
			case BlackDB.MODEL_NONE:
				// 没有拦截
				model = "没有拦截";
				break;

			default:
				break;
			}
			viewHolder.tv_model.setText(model);
			// 添加删除事件

			viewHolder.iv_delete.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// 删除当前数据

					// 从数据中删除
					mBlackDao.remove(bean);

					// 界面更新
					// initData();
					if (mDatas.size() < 20 && mBlackDao.getCounts() >= 20) {
						initData();
					} else {
						// 删除容器中的数据
						mDatas.remove(bean);
					}
					/*
					 * //更新界面 mAdapter.notifyDataSetChanged();
					 */

					// 数据删除没有
					// 显示卡通 表示没有数据
					// 发送消息
					mHandler.obtainMessage(FINISH).sendToTarget();

				}
			});
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

	private class ViewHolder {
		ImageView iv_delete;
		TextView tv_number;
		TextView tv_model;
	}

	private void initPopupWindowAnimation() {
		// 缩放动画

		mSa = new ScaleAnimation(1.0f, 1.0f, 0, 1.0f,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0);

		mSa.setDuration(500);
		mSa.setFillAfter(true);

	}

	private void initDialog() {
		View dialogView = View.inflate(getApplicationContext(),
				R.layout.dialog_addblack_view, null);
		mEt_number = (EditText) dialogView
				.findViewById(R.id.et_dialog_black_number);

		final CheckBox cb_sms = (CheckBox) dialogView
				.findViewById(R.id.cb_dialog_black_sms);
		final CheckBox cb_phone = (CheckBox) dialogView
				.findViewById(R.id.cb_dialog_black_phone);

		Button bt_add = (Button) dialogView
				.findViewById(R.id.bt_dialog_black_add);
		Button bt_cancle = (Button) dialogView
				.findViewById(R.id.bt_dialog_black_cancel);

		OnClickListener listener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.bt_dialog_black_add:
					// 添加
					// 获取黑名单号码
					String number = mEt_number.getText() + "";
					if (TextUtils.isEmpty(number)) {
						Toast.makeText(getApplicationContext(), "号码不能为空", 0)
								.show();
						return;
					} else if (!cb_phone.isChecked() && !cb_sms.isChecked()) {
						Toast.makeText(getApplicationContext(), "至少选折一种拦截模式", 0)
								.show();
						return;
					} else {

						int model = 0;
						if (cb_phone.isChecked()) {
							model |= BlackDB.MODEL_PHONE;
						}

						if (cb_sms.isChecked()) {
							model |= BlackDB.MODEL_SMS;
						}
						// 添加黑名单数据
						BlackBean bean = new BlackBean();
						bean.setNumber(number);
						bean.setModel(model);

						// 添加到数据库
						mBlackDao.add(bean);

						// 界面显示
						// initData(); //List
						mDatas.remove(bean);//

						mDatas.add(0, bean);
						// mLv_showDatas.smoothScrollToPosition(0);//回滚到0位置显示
						// mLv_showDatas.setSelection(0);//回滚到0位置显示
						// mAdapter.notifyDataSetChanged();//位置不变
						mLv_showDatas.setAdapter(mAdapter);

						// 切换界面 mLv_showDatas.setVisibility(View.VISIBLE);
						// 隐藏imageview
						mIv_nodata.setVisibility(View.GONE);

						// 取消对话框
						mAlertDialog.dismiss();
					}

					break;
				case R.id.bt_dialog_black_cancel:

					// 取消
					mAlertDialog.dismiss();
					break;

				default:
					break;
				}

			}
		};

		bt_add.setOnClickListener(listener);
		bt_cancle.setOnClickListener(listener);

		// 创建Dialog
		AlertDialog.Builder ab = new AlertDialog.Builder(this);
		ab.setView(dialogView);
		mAlertDialog = ab.create();

	}

	private void setNumber(String number) {
		mEt_number.setText(number);
	}

	private void initPopupWindow() {
		mPopupView = View.inflate(getApplicationContext(),
				R.layout.popup_addblack, null);

		// 获取四个添加方式
		TextView tv_hand = (TextView) mPopupView
				.findViewById(R.id.tv_popup_black_hand);
		TextView tv_sms = (TextView) mPopupView
				.findViewById(R.id.tv_popup_black_sms);
		TextView tv_phone = (TextView) mPopupView
				.findViewById(R.id.tv_popup_black_call);
		TextView tv_friends = (TextView) mPopupView
				.findViewById(R.id.tv_popup_black_friends);

		mPw = new PopupWindow(mPopupView, -2, -2);// -2包裹内容

		OnClickListener listener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 关闭弹出窗体
				if (mPw.isShowing()) {
					mPw.dismiss();
				}

				switch (v.getId()) {
				case R.id.tv_popup_black_hand:
					// 手动添加
					mAlertDialog.show();
					break;
				case R.id.tv_popup_black_sms:
					// 添加
					Intent sms = new Intent(BlackActivity.this,
							SmsActivity.class);
					startActivityForResult(sms, 0);
					PrintLog.print("sms...............");
					break;
				case R.id.tv_popup_black_call:
					// 手动添加
					// 跳转到CallogActivity
					Intent calllog = new Intent(BlackActivity.this,
							CalllogActivity.class);
					startActivityForResult(calllog, 0);
					break;
				case R.id.tv_popup_black_friends:
					// 好友添加
					Intent friends = new Intent(BlackActivity.this,
							ContactsActivity.class);
					startActivityForResult(friends, 0);
					break;

				default:
					break;
				}

			}
		};
		tv_hand.setOnClickListener(listener);
		tv_sms.setOnClickListener(listener);
		tv_friends.setOnClickListener(listener);
		tv_phone.setOnClickListener(listener);

		// 设置pw的参数
		mPw.setFocusable(true);// 获取焦点
		mPw.setOutsideTouchable(true);// 组件外面点击消失
		mPw.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));// 透明的背景:
																		// 组件外面点击消失,动画效果生效

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// 1.
		if (data != null) {
			String number = data.getStringExtra("phone");
			setNumber(number);
			mAlertDialog.show();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void initEvent() {
		// 添加黑名单数据的事件
		mIv_addBlack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 显示弹出窗体
				if (mPw != null && mPw.isShowing()) {
					// 关闭
					mPw.dismiss();
				} else {
					mPw.showAsDropDown(mIv_addBlack);// 显示在mIv_addBlack组件的下方
					// 动画效果
					mPopupView.startAnimation(mSa);
				}
				// 状态切换

			}
		});

		// 给listView添加滑动事件
		mLv_showDatas.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// 状态改变
				// SCROLL_STATE_FLING 惯性滑动2
				// SCROLL_STATE_TOUCH_SCROLL 按住滑动1
				// SCROLL_STATE_IDLE 空闲0
				// PrintLog.print(scrollState + "<<<<<<<<<<<<<<<<<");
				if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
					// 空闲状态
					// 判断显示的位置是否是最后一条，如果是最后一条 加载更多
					int lastVisiblePosition = mLv_showDatas
							.getLastVisiblePosition();
					if (lastVisiblePosition == mDatas.size() - 1) {
						// 最后一条数据显示
						PrintLog.print("最后一条数据.....");
						//
						initData();
					}
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// 按住拖动
				// PrintLog.print("onScroll");

			}
		});
	}

	private void initView() {
		setContentView(R.layout.activity_black);

		mIv_addBlack = (ImageView) findViewById(R.id.iv_black_addblack);

		mLv_showDatas = (ListView) findViewById(R.id.lv_black_showdatas);

		// 绑定适配器
		mAdapter = new MyAdapter();
		mLv_showDatas.setAdapter(mAdapter);

		mLl_loading = (LinearLayout) findViewById(R.id.ll_black_loading);
		mIv_nodata = (ImageView) findViewById(R.id.iv_black_empty);

		// 初始化BlackDao

		mBlackDao = new BlackDao(this);
	}
}
