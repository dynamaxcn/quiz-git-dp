package com.itheima17.mobileguard.view;

import com.itheima17.mobileguard.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * @author Administrator
 * @date 2015-11-27
 * @pagename com.itheima17.mobileguard.view
 * @desc 进度+文本的显示

 * @svn author $Author: goudan $
 * @svn date $Date: 2015-11-27 10:39:07 +0800 (周五, 27 十一月 2015) $
 * @Id  $Id: ProgressMessageView.java 50 2015-11-27 02:39:07Z goudan $
 * @version $Rev: 50 $
 * @url $URL: https://188.188.2.100/svn/itheima17/trunk/MobileGuard/src/com/itheima17/mobileguard/view/ProgressMessageView.java $ 
 * 
 */
public class ProgressMessageView extends RelativeLayout {

	private View mRootView;
	private ProgressBar mPb_proProgressBar;
	private TextView mTv_mess;

	public ProgressMessageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		initView();
	}
	
	public void setMessage(String mess){
		mTv_mess.setText(mess);
	}
	
	public void setProgress(long romAvail,long romTotal) {
		mPb_proProgressBar.setMax(100);
		mPb_proProgressBar.setProgress((int) (romAvail * 1.0/romTotal * 100));
	}

	private void initView() {
		mRootView = View.inflate(getContext(), R.layout.progress_text_view, this);
		
		mPb_proProgressBar = (ProgressBar) mRootView.findViewById(R.id.pb_progress_text_view);
		
		mTv_mess = (TextView) mRootView.findViewById(R.id.tv_progress_text_view);
	}

	public ProgressMessageView(Context context) {
		this(context,null);
		// TODO Auto-generated constructor stub
	}

}
