package com.itheima17.mobileguard.receiver;

import java.util.List;

import com.itheima17.mobileguard.domain.AppBean;
import com.itheima17.mobileguard.utils.PrintLog;
import com.itheima17.mobileguard.utils.TaskUtils;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ClearTaskReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		ActivityManager mAm = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		//清理进程
		List<AppBean> allRunningTaskInfos = TaskUtils.getAllRunningTaskInfos(context);
		for (AppBean bean : allRunningTaskInfos){
			mAm.killBackgroundProcesses(bean.getPackName());
			
		}
		PrintLog.print("widget清理进程 ");
	}

}
