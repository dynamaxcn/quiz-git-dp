package com.itheima17.mobileguard.view;

import com.itheima17.mobileguard.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * @author Administrator
 * @date 2015-12-4
 * @pagename com.itheima17.mobileguard.view
 * @desc 程序锁标签类的封装 

 * @svn author $Author: goudan $
 * @svn date $Date: 2015-12-04 14:59:15 +0800 (周五, 04 十二月 2015) $
 * @Id  $Id: AppLockTagView.java 96 2015-12-04 06:59:15Z goudan $
 * @version $Rev: 96 $
 * @url $URL: https://188.188.2.100/svn/itheima17/trunk/MobileGuard/src/com/itheima17/mobileguard/view/AppLockTagView.java $ 
 * 
 */
public class AppLockTagView extends RelativeLayout {

	private TextView mTv_locked;
	private TextView mTv_unlock;

	public AppLockTagView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		
		initView();
		
		initEvent();
		
		initdata();
	}
	
	
	private void initdata() {
		isLeftSelect = true;
		//未加锁
		/*mTv_locked.setEnabled(false);
		mTv_unlock.setEnabled(true);*/
		mTv_locked.setSelected(false);
		mTv_unlock.setSelected(true);
		
	}


	/**
	 * @author Administrator
	 * @date 2015-12-4
	 * @pagename com.itheima17.mobileguard.view
	 * @desc 点击标签变化的事件回调
	
	 * @svn author $Author: goudan $
	 * @svn date $Date: 2015-12-04 14:59:15 +0800 (周五, 04 十二月 2015) $
	 * @Id  $Id: AppLockTagView.java 96 2015-12-04 06:59:15Z goudan $
	 * @version $Rev: 96 $
	 * @url $URL: https://188.188.2.100/svn/itheima17/trunk/MobileGuard/src/com/itheima17/mobileguard/view/AppLockTagView.java $ 
	 * 
	 */
	public interface OnTagChangeListener{
		//回调方法  view  当前自定控件   左边的tag是否被选中
		public void tagChange(View view,boolean isLeftSelect);
	}
	
	private OnTagChangeListener listener;
	public void setOnTagChangeListener(OnTagChangeListener listener) {
		this.listener = listener;
	}
	
	private boolean isLeftSelect = true;
	private void initEvent() {
		OnClickListener listener = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.tv_applock_tag_locked://右边
					isLeftSelect = false;
					//加锁
					mTv_locked.setSelected(true);
					mTv_unlock.setSelected(false);
					break;
				case R.id.tv_applock_tag_unlock: //左边
					isLeftSelect = true;
					//未加锁
					/*mTv_locked.setEnabled(false);
					mTv_unlock.setEnabled(true);*/
					mTv_locked.setSelected(false);
					mTv_unlock.setSelected(true);
					break;
				default:
					break;
				}
				
				//做事件回调处理
				if (AppLockTagView.this.listener != null) {
					AppLockTagView.this.listener.tagChange(AppLockTagView.this, isLeftSelect);
				}
				
			}
		};
		
		mTv_locked.setOnClickListener(listener);
		mTv_unlock.setOnClickListener(listener);
	}

	private void initView() {
		//获取根布局 并且添加到父控件中 
		View rootView = View.inflate(getContext(), R.layout.lock_tab_tag_view, this);
		
		mTv_locked = (TextView) rootView.findViewById(R.id.tv_applock_tag_locked);
		mTv_unlock = (TextView) rootView.findViewById(R.id.tv_applock_tag_unlock);
		
	}

	public AppLockTagView(Context context) {
		this(context,null);
		// TODO Auto-generated constructor stub
	}

}
