package com.itheima17.mobileguard.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;

import com.itheima17.mobileguard.R;
import com.itheima17.mobileguard.utils.PrintLog;

/**
 * @author Administrator
 * @date 2015-11-22
 * @pagename com.itheima17.mobileguard.activity
 * @desc 4个设置向导界面的基类
 * 
 * @svn author $Author: goudan $
 * @svn date $Date: 2015-11-23 10:45:42 +0800 (周一, 23 十一月 2015) $
 * @Id $Id: BaseSetupActivity.java 27 2015-11-23 02:45:42Z goudan $
 * @version $Rev: 27 $
 * @url $URL: https://188.188.2.100/svn/itheima17/trunk/MobileGuard/src/com/itheima17/mobileguard/activity/BaseSetupActivity.java $
 * 
 */
public abstract class BaseSetupActivity extends Activity {
	private GestureDetector mGd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		//1. view
		initView();
		//2. controller
		initEvent();
		//3. model
		initData();
		
		
		initGesture();
	}

	private void initGesture() {
		mGd = new GestureDetector(new MyOnGestureListener(){
			/**
			 * e1 按下的点
			 * e2 滑动后结束的点
			 * velocityX  x轴方向的速度 单位：像素/秒
			 * velocityY  y轴方向的速度
			 */
			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
					float velocityY) {
				
				//判断滑动的方向和速度 velocityX
				if (Math.abs(velocityX) > 100) {
					//速度大于100像素每秒 滑动有效
					if (e2.getX() - e1.getX() > 50) {
						PrintLog.print("从左往右滑动");
						prePage(null);
						
					} else if (e2.getX() - e1.getX() < -50){
						
						PrintLog.print("从右往左滑动");
						nextPage(null);
						
					}
				}
				
				//true 自己消费事件
				return true;
			}
		});
		
	}
	

	/**
	 * 子类需要覆盖此方法完成界面的显示
	 */
	public void initView() {

	}

	/**
	 * 子类需要覆盖此方法完成数据的处理
	 */
	public void initData() {

	}

	/**
	 * 子类需要覆盖此方法完成事件的处理
	 */
	public void initEvent() {

	}

	public void startActivity(Class type) {
		Intent intent = new Intent(this, type);
		startActivity(intent);
		finish();
	}

	/*
	 * 下一个页面
	 */
	public abstract void next();

	/*
	 * 上一个页面
	 */
	public abstract void prev();

	/**
	 * 下一个按钮对应的事件
	 * 
	 * @param v
	 */
	public void nextPage(View v) {
		// activity的跳转
		next();
		// 动画的切换
		overridePendingTransition(R.anim.nextenteranim, R.anim.nextexitanim);
	}

	/**
	 * 上一个按钮对应的事件
	 * 
	 * @param v
	 */
	public void prePage(View v) {
		prev();
		// 动画的切换:有Activity之间的切换
		overridePendingTransition(R.anim.preventeranim, R.anim.prevexitanim);

	}
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		//绑定手势识别器
		mGd.onTouchEvent(event);
		return true;
	}
	
	private class MyOnGestureListener implements OnGestureListener{

		@Override
		public boolean onDown(MotionEvent e) {
			// TODO Auto-generated method stub
			return false;
		}

		

		@Override
		public void onLongPress(MotionEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void onShowPress(MotionEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			// TODO Auto-generated method stub
			return false;
		}



		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			// TODO Auto-generated method stub
			return false;
		}
		
	}
}
