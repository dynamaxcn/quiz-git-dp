package com.itheima17.mobileguard.view;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.ViewDebug.ExportedProperty;
import android.widget.TextView;

/**
 * @author Administrator
 * @date 2015-11-21
 * @pagename com.itheima17.mobileguard.view
 * @desc 获取焦点的TextView

 * @svn author $Author: goudan $
 * @svn date $Date: 2015-11-21 16:01:31 +0800 (周六, 21 十一月 2015) $
 * @Id  $Id: FocusTextView.java 16 2015-11-21 08:01:31Z goudan $
 * @version $Rev: 16 $
 * @url $URL: https://188.188.2.100/svn/itheima17/trunk/MobileGuard/src/com/itheima17/mobileguard/view/FocusTextView.java $ 
 * 
 */
public class FocusTextView extends TextView {

	public FocusTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		//布局文件调用
	}

	public FocusTextView(Context context) {
		super(context);
		// 代码中
	}
	
	@Override
	@ExportedProperty(category = "focus")
	public boolean isFocused() {
		// TODO Auto-generated method stub
		return true;
	}
	
	@Override
	protected void onFocusChanged(boolean focused, int direction,
			Rect previouslyFocusedRect) {
		// TODO Auto-generated method stub
		if (focused)
			super.onFocusChanged(focused, direction, previouslyFocusedRect);
		else {
			//啥也不干
		}
	}
	
	@Override
	public void onWindowFocusChanged(boolean hasWindowFocus) {
		// TODO Auto-generated method stub
		if (hasWindowFocus)
			super.onWindowFocusChanged(hasWindowFocus);
		else {
			//false 不做事情 焦点继续滚动
		}
			
	}

}
