package com.itheima17.mobileguard.view;

import com.itheima17.mobileguard.R;
import com.itheima17.mobileguard.utils.MyContains;
import com.itheima17.mobileguard.utils.PrintLog;
import com.itheima17.mobileguard.utils.SPUtils;

import android.content.Context;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.TextView;

public class MyToast implements OnTouchListener{
	
	private Context mContext;
	private LayoutParams mLayoutParams;
	private WindowManager mWm;
	private View mToastView;
	private float mDownX;
	private float mDownY;
	public MyToast(Context context) {
        mContext = context;
        mWm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        mLayoutParams = new LayoutParams();
        
        //获取土司显示的View
        
        
        
        mLayoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mLayoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mLayoutParams.format = PixelFormat.TRANSLUCENT;
       // mLayoutParams.windowAnimations = com.android.internal.R.style.Animation_Toast;
        mLayoutParams.type = WindowManager.LayoutParams.TYPE_PRIORITY_PHONE;//土司的类型
        mLayoutParams.setTitle("Toast");
        mLayoutParams.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | 
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
                //| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
        mLayoutParams.gravity = Gravity.TOP | Gravity.LEFT;//设置土司的左上角对齐
        
        //获取初始位置 
        mLayoutParams.x = Math.round(SPUtils.getFloat(mContext, MyContains.TOASTX, 0));
        mLayoutParams.y = Math.round(SPUtils.getFloat(mContext, MyContains.TOASTY, 0));
        
	}
	
	/**
	 * 土司的显示
	 */
	public void show(String location){
		hiden();//先隐藏
		
		mToastView = View.inflate(mContext, R.layout.sys_toast, null);
		//设置样式
		int index = (int) SPUtils.getFloat(mContext, MyContains.LOCATIONSTYLEINDEX, 0);
		mToastView.setBackgroundResource(MyDialog.icons[index]);
		TextView tv_mess = (TextView) mToastView.findViewById(R.id.tv_location_title);
		tv_mess.setText(location);
		mToastView.setOnTouchListener(this);
		PrintLog.print(mWm + ":" + mToastView + ":" + mLayoutParams);
		//显示
		mWm.addView(mToastView, mLayoutParams);
	}
	
	/**
	 * 土司的关闭
	 */
	public void hiden(){
		 if (mToastView != null) {//土司view是否初始化
             // note: checking parent() just to make sure the view has
             // been added...  i have seen cases where we get here when
             // the view isn't yet added, so let's try not to crash.
             if (mToastView.getParent() != null) {//土司是否已经添加到窗体中
                
					//从窗体中移除土司
                 mWm.removeView(mToastView);
             }

             mToastView = null;
         }
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		//给自定义土司添加触摸事件
		PrintLog.print("摸我了");
		
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			//按下
			//获取按下点的坐标(屏幕)
			
			mDownX = event.getRawX();
			mDownY = event.getRawY();
			break;
		case MotionEvent.ACTION_MOVE:
			//事件的拖动
			
			float moveX = event.getRawX();//移动的位置坐标x
			float moveY = event.getRawY();
			
			//算出坐标的相对值
			float dx = moveX - mDownX;
			float dy = moveY - mDownY;
			
			//设置坐标的位置 
			mLayoutParams.x += dx;
			mLayoutParams.y += dy;
			
			
			//判断位置越界
			//左
			if (mLayoutParams.x < 0) {
				mLayoutParams.x = 0;
			}
			//右
			//屏幕的坐标
			int screenWidth = mWm.getDefaultDisplay().getWidth();
			int screenHeight = mWm.getDefaultDisplay().getHeight();
			
			int toastWidth = mToastView.getWidth();
			int toastHeight = mToastView.getHeight();
			
			//右
			if (mLayoutParams.x > (screenWidth - toastWidth)){
				mLayoutParams.x = screenWidth - toastWidth;
			}
			
			//上
			if (mLayoutParams.y < 0){
				mLayoutParams.y = 0;
			}
			//下
			if (mLayoutParams.y > (screenHeight - toastHeight)) {
				mLayoutParams.y = (screenHeight - toastHeight);
			}
			
			//设置坐标
			mWm.updateViewLayout(mToastView, mLayoutParams);
			
			//下次起始位置
			mDownX = moveX;
			mDownY = moveY;
			
		case MotionEvent.ACTION_UP:
			//松开
			
			//记录土司的坐标
			SPUtils.putFloat(mContext, MyContains.TOASTX, mLayoutParams.x);
			SPUtils.putFloat(mContext, MyContains.TOASTY, mLayoutParams.y);
			
		default:
			break;
		}
		return true;//自己处理事件 事件不回传
	}
}
