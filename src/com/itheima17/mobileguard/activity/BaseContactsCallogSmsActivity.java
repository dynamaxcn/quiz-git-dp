package com.itheima17.mobileguard.activity;

import java.util.List;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.itheima17.mobileguard.dao.ContactsDao;
import com.itheima17.mobileguard.domain.ContactBean;
import com.itheima17.mobileguard.utils.PrintLog;

/**
 * @author Administrator
 * @date 2015-11-22
 * @pagename com.itheima17.mobileguard.activity
 * @desc 显示好友信息的activity listView

 * @svn author $Author: goudan $
 * @svn date $Date: 2015-11-25 15:43:02 +0800 (周三, 25 十一月 2015) $
 * @Id  $Id: BaseContactsCallogSmsActivity.java 37 2015-11-25 07:43:02Z goudan $
 * @version $Rev: 37 $
 * @url $URL: https://188.188.2.100/svn/itheima17/trunk/MobileGuard/src/com/itheima17/mobileguard/activity/BaseContactsCallogSmsActivity.java $ 
 * 
 */
public abstract class BaseContactsCallogSmsActivity extends ListActivity {
	protected static final int LOADING = 1;
	protected static final int FINISH = 2;
	private ListView mLv_datas;
	private List<ContactBean> mMContactBeans;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
PrintLog.print("black add.....");		
		mLv_datas = getListView();
		
		mMAdapter = new MyAdapter();
		mLv_datas.setAdapter(mMAdapter);
		
		initData();
		
		initEvent();
	}
	
	private void initEvent() {
		mLv_datas.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				ContactBean bean = mMContactBeans.get(position);
				Intent intent = new Intent();
				intent.putExtra("phone", bean.phone);
				setResult(0, intent);
				finish();//关闭当前的联系人界面
				
			}
		});
		
	}

	private Handler mHandler = new Handler(){
		private ProgressDialog mPd;

		public void handleMessage(android.os.Message msg) {
			
			switch (msg.what) {
			case LOADING:
				mPd = ProgressDialog.show(BaseContactsCallogSmsActivity.this, "提醒", "正在玩命加载好友信息");
				break;
			case FINISH:
				mPd.dismiss();
				
				//更新数据
				mMAdapter.notifyDataSetChanged();
			default:
				break;
			}
		};
	};
	
	@Override
	public void onBackPressed() {
		
		//返回键的处理
		//增加自己的功能
		/*Intent data = new Intent();
		setResult(0, data);*/
		super.onBackPressed();
	};
	private MyAdapter mMAdapter;
	
	private void initData() {
		new Thread(){
			
			public void run() {
				//取数据之前发的加载数据的消息
				mHandler.obtainMessage(LOADING).sendToTarget();
				
				//加载数据
				mMContactBeans = getDatas();//ContactsDao.getAllBeans(getApplicationContext());
				SystemClock.sleep(2000);
				
				
				//发送加载数据完成的消息
				mHandler.obtainMessage(FINISH).sendToTarget();
			};
		}.start();
		
	}

	public abstract List<ContactBean> getDatas() ;

	private class MyAdapter extends BaseAdapter{

		@Override
		public int getCount() {
PrintLog.print("getCount>");		
			// TODO Auto-generated method stub
			if (mMContactBeans != null) {
				return mMContactBeans.size();
			}
			return 0;
		}

		@Override
		public Object getItem(int position) {
			PrintLog.print("getItem>");
			return null;
		}

		@Override
		public long getItemId(int position) {
			PrintLog.print("getItemId>");
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			PrintLog.print("getView" + position);
			TextView tv = new TextView(getApplicationContext());
			ContactBean bean = mMContactBeans.get(position);
			tv.setText(bean.name + "<>" + bean.phone);
			tv.setTextSize(25);
			
			return tv;
		}
		
	}
}
