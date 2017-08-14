package com.itheima17.mobileguard.activity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.itheima17.mobileguard.R;
import com.itheima17.mobileguard.domain.AppBean;
import com.itheima17.mobileguard.utils.AppUtils;

/**
 * @author Administrator
 * @date 2015-12-1
 * @pagename com.itheima17.mobileguard.activity
 * @desc 流量统计
 * 
 * @svn author $Author: goudan $
 * @svn date $Date: 2015-12-01 17:07:03 +0800 (周二, 01 十二月 2015) $
 * @Id $Id: ConnectivityActivity.java 84 2015-12-01 09:07:03Z goudan $
 * @version $Rev: 84 $
 * @url $URL: https://188.188.2.100/svn/itheima17/trunk/MobileGuard/src/com/itheima17/mobileguard/activity/ConnectivityActivity.java $
 * 
 */
public class ConnectivityActivity extends ListActivity {
	private MyAdapter mAdapter;
	private List<AppBean> mAllInstalledAppInfos;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = cm.getActiveNetworkInfo();
		String typeName = activeNetworkInfo.getTypeName();
		initView();
		initData();

	}

	private ProgressDialog mPd;

	private void initData() {
		new Thread() {
			

			public void run() {
				// 显示进度
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub

						mPd = ProgressDialog.show(ConnectivityActivity.this,
								"提醒", "正在玩命加载.....");
					}
				});

				mAllInstalledAppInfos = AppUtils
						.getAllInstalledAppInfos(getApplicationContext());
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub

						mPd.dismiss();
						Toast.makeText(getApplicationContext(), "大小：" + mAllInstalledAppInfos.size(), 1).show();
						mAdapter.notifyDataSetChanged();
					}
				});

			};
		}.start();

	}

	private void initView() {
		ListView lv = getListView();
		mAdapter = new MyAdapter();
		lv.setAdapter(mAdapter);
	}

	private class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			if (mAllInstalledAppInfos != null)
				return mAllInstalledAppInfos.size();
			return 0;
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
			
			ViewHolder holder = null;
			if (convertView == null) {
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
			
			//显示信息
			AppBean appBean = mAllInstalledAppInfos.get(position);
			int id = appBean.getPid();
			
			long sndSize = getFileTcpsize("proc/uid_stat/" + id + "/tcp_snd");
			long rcvSize = getFileTcpsize("proc/uid_stat/" + id +  "/tcp_rcv");
			
			//显示
			holder.iv_icon.setImageDrawable(appBean.getIcon());
			holder.tv_appName.setText(appBean.getAppName());
			holder.tv_location.setText("发送："+ sndSize);
			holder.tv_size.setText("接收："+ rcvSize);
			
			return convertView;
		}

	}
	
	private long  getFileTcpsize(String path){
		long len = 0;
		File file = new File(path);
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String size = reader.readLine();
			len = Long.parseLong(size);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return len;
		
	}
	
	private class ViewHolder {
		ImageView iv_icon;
		TextView tv_appName;
		TextView tv_location;
		TextView tv_size;
	}
}
