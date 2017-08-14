package com.itheima17.mobileguard.view;

import com.itheima17.mobileguard.R;
import com.itheima17.mobileguard.utils.PrintLog;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * @author Administrator
 * @date 2015-11-26
 * @pagename com.itheima17.mobileguard.view
 * @desc 自定义组合空间（设置中心界面条目）

 * @svn author $Author: goudan $
 * @svn date $Date: 2015-11-30 14:23:06 +0800 (周一, 30 十一月 2015) $
 * @Id  $Id: SettingCenterItemView.java 70 2015-11-30 06:23:06Z goudan $
 * @version $Rev: 70 $
 * @url $URL: https://188.188.2.100/svn/itheima17/trunk/MobileGuard/src/com/itheima17/mobileguard/view/SettingCenterItemView.java $ 
 * 
 */
public class SettingCenterItemView extends RelativeLayout {

	private View mRootView;
	private TextView mTv_desc;
	private ImageView mIv_toggle;
	private boolean isToggleOn = false;//关闭
	
	private static final String nameSpace = "http://schemas.android.com/apk/res/com.itheima17.mobileguard";

	public SettingCenterItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// 布局文件中调用
		//attrs 获取布局文件中配置的属性
		initView();//初始化界面
		
		initData(attrs);//初始化数据
		
		initEvent();
	}
	
	private OnToggleChangeListener mOnToggleChangeListener;
	
	public void setOnToggleChangeListener(OnToggleChangeListener listener) {
		mOnToggleChangeListener = listener;
	}
	
	public void setMessage(String mess){
		mTv_desc.setText(mess);
	}
	
	//自定义接口回调
	public interface OnToggleChangeListener {
		public void onToggleChanged(SettingCenterItemView view, boolean isToggleOn); 
	}

	private void initEvent() {
		
		/*cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				
			}
		})*/
		// 给自定义控件的根布局添加点击事件
		mRootView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//控制开关的显示或关闭
				isToggleOn = !isToggleOn;
				
				showToggleAndEventCallback();
				
			}

			
		});
		
	}
	private void showToggleAndEventCallback() {
		if (isToggleOn) {
			mIv_toggle.setImageResource(R.drawable.on);
		} else {
			mIv_toggle.setImageResource(R.drawable.off);
		}
		
		//把开关的状态通过事件回调传递给用户
		if (mOnToggleChangeListener != null)
			//回调结果给调用者
			mOnToggleChangeListener.onToggleChanged(SettingCenterItemView.this, isToggleOn);
	}

	/**
	 * 设置开关的状态
	 * @param toggleOn
	 */
	public void isToggleOn(boolean toggleOn){
		this.isToggleOn = toggleOn;
		showToggleAndEventCallback();
		
	}
	private void initData(AttributeSet attrs) {
		// 获取配置文件中的属性值，来处理界面控件的显示
		
		//获取是否显示开关的属性
		boolean istoggleShow = attrs.getAttributeBooleanValue(nameSpace, "isToggleShow", true);
		if (!istoggleShow) {
			//不显示开关
			mIv_toggle.setVisibility(View.GONE);
		} else {
			//显示开关
		}
		
		//描述信息
		String desc = attrs.getAttributeValue(nameSpace, "desc");
		mTv_desc.setText(desc);
		
		//背景的样式
		int bgtype = attrs.getAttributeIntValue(nameSpace, "bgtype", 1);
		switch (bgtype) {
		case 1:
			// first
			mRootView.setBackgroundResource(R.drawable.settingcenter_item_first_selector);
			break;
		case 2:
			// middle
			mRootView.setBackgroundResource(R.drawable.settingcenter_item_middle_selector);
			break;
		case 3:
			// last
			mRootView.setBackgroundResource(R.drawable.settingcenter_item_last_selector);
			break;

		default:
			break;
		}
	}

	private void initView() {
		//显示的View
		//this 当前布局的父控件
		
		mRootView = View.inflate(getContext(), R.layout.settingcenter_item_view, this);
		
		//描述信息
		mTv_desc = (TextView) mRootView.findViewById(R.id.tv_settingcenter_item_title);
		
		//获取开关view
		mIv_toggle = (ImageView) mRootView.findViewById(R.id.iv_settingcenter_item_toggle);
		
		/**
		 * View rootView = View.inflate(getContext(), R.layout.settingcenter_item_view, null);
		 * addView(rootView);
		 */
		
	}

	public SettingCenterItemView(Context context) {
		this(context,null);
		// 代码中调用
		
	}
	
	

}
