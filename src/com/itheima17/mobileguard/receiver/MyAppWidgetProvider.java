package com.itheima17.mobileguard.receiver;

import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;

import com.itheima17.mobileguard.service.WidgetService;

public class MyAppWidgetProvider extends AppWidgetProvider {

	@Override
	public void onEnabled(Context context) {
		// 只执行一次
		//开启监控的服务
		Intent service = new Intent(context,WidgetService.class);
		context.startService(service);
		super.onEnabled(context);
	}

	@Override
	public void onDisabled(Context context) {
		// 只执行一次
		//关闭监控服务
		Intent service = new Intent(context,WidgetService.class);
		context.stopService(service);
		super.onDisabled(context);
	}
	
}
