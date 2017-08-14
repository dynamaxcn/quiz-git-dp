package com.itheima17.mobileguard.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.itheima17.mobileguard.R;
import com.itheima17.mobileguard.utils.MyContains;
import com.itheima17.mobileguard.utils.SPUtils;

public class MyDialog extends Dialog {

	public static  String names[] = new String[] { "半透明", "活力橙", "金属灰", "苹果绿", "卫士蓝" };
	public static final int icons[] = new int[] { R.drawable.call_locate_white,
			R.drawable.call_locate_orange, R.drawable.call_locate_gray,
			R.drawable.call_locate_green, R.drawable.call_locate_blue };
	private View mMRootView;
	private ListView mLv_showdatas;
	private MyAdapter mAdapter;

	public MyDialog(Context context, int theme) {
		super(context, theme);
		// 添加自己代码

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		mMRootView = View.inflate(getContext(),
				R.layout.dialog_locaitonstyle_view, null);

		mLv_showdatas = (ListView) mMRootView
				.findViewById(R.id.lv_dialog_locaitonstyle_styles);
		mAdapter = new MyAdapter();
		mLv_showdatas.setAdapter(mAdapter);

		initEvent();

		// 设置对话框的显示view
		setContentView(mMRootView);
		// 参数设置 默认对话显示在屏幕中间
		// 让对话框显示在屏幕的下方
		Window window = getWindow();
		LayoutParams attributes = window.getAttributes();
		attributes.gravity = Gravity.BOTTOM;// 设置对话框底部对齐

		// 设置新参数
		window.setAttributes(attributes);

	}

	private void initEvent() {
		mLv_showdatas.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// 保存样式的位置 到sp中
				SPUtils.putFloat(getContext(), MyContains.LOCATIONSTYLEINDEX,
						position);

				// 设置文本的值
				mSciv_locationStyle
						.setMessage("归属地样式(" + names[position] + ")");
				// 关闭对话框
				dismiss();// 关闭对话框

			}
		});
	}

	private class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return names.length;
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
			if (convertView == null) {
				convertView = View.inflate(getContext(),
						R.layout.item_dialog_lv, null);
			}

			ImageView iv_style = (ImageView) convertView
					.findViewById(R.id.iv_item_dialog_lv_style);

			TextView tv_name = (TextView) convertView
					.findViewById(R.id.tv_item_dialog_lv_name);

			ImageView iv_isselect = (ImageView) convertView
					.findViewById(R.id.iv_item_dialog_lv_select);
			iv_style.setBackgroundResource(icons[position]);
			tv_name.setText(names[position]);

			if (position == SPUtils.getFloat(getContext(),
					MyContains.LOCATIONSTYLEINDEX, 0)) {
				iv_isselect.setVisibility(View.VISIBLE);

			} else {
				iv_isselect.setVisibility(View.GONE);
			}
			return convertView;
		}

	}

	private SettingCenterItemView mSciv_locationStyle;

	public MyDialog(Context context, SettingCenterItemView mSciv_locationStyle) {
		this(context, R.style.locaitondialogstyle);
		this.mSciv_locationStyle = mSciv_locationStyle;
		// TODO Auto-generated constructor stub
	}

}
