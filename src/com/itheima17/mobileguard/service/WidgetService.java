package com.itheima17.mobileguard.service;

import java.util.Timer;
import java.util.TimerTask;

import com.itheima17.mobileguard.R;
import com.itheima17.mobileguard.receiver.MyAppWidgetProvider;
import com.itheima17.mobileguard.utils.PrintLog;
import com.itheima17.mobileguard.utils.TaskUtils;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.IBinder;
import android.text.format.Formatter;
import android.widget.RemoteViews;

public class WidgetService extends Service {

	private Timer mTimer;
	private AppWidgetManager mAwm;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		mAwm = AppWidgetManager.getInstance(getApplicationContext());
		
		//监控
		
		mTimer = new Timer();
		TimerTask task = new TimerTask() {
			
			@Override
			public void run() {
				// 特殊线程
				PrintLog.print("监控");
				//更新widget的界面信息
				
				ComponentName provider = new ComponentName(getApplicationContext(), MyAppWidgetProvider.class);
				
				RemoteViews views = new RemoteViews(getPackageName(), R.layout.process_widget);
				//设置views子控件的值
				views.setTextViewText(R.id.tv_widget_count, "正在运行的软件个数:" + 
				TaskUtils.getAllRunningTaskInfos(getApplicationContext()).size());
				
				
				views.setTextViewText(R.id.tv_widget_memory, "可用内存:" + 
				Formatter.formatFileSize(getApplicationContext(), TaskUtils.getFreeMem(getApplicationContext())));
				
				
				Intent intent = new Intent("itheima.cleartask");
				//广播  清单文件中 
				PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent , 0);
				//添加按钮的事件
				views.setOnClickPendingIntent(R.id.btn_widget_clear, pendingIntent );
				mAwm.updateAppWidget(provider , views );
			}
		};
		mTimer.schedule(task , 0, 2000);//开启定时监控任务 
		super.onCreate();
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		mTimer.cancel();//取消监控
		mTimer = null;//方便回收
		super.onDestroy();
	}
}
