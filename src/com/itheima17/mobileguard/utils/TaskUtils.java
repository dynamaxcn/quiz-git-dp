package com.itheima17.mobileguard.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.itheima17.mobileguard.domain.AppBean;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

/**
 * @author Administrator
 * @date 2015-11-30
 * @pagename com.itheima17.mobileguard.utils
 * @desc 进程管理的工具类
 * 
 * @svn author $Author: goudan $
 * @svn date $Date: 2015-12-01 15:05:57 +0800 (周二, 01 十二月 2015) $
 * @Id $Id: TaskUtils.java 82 2015-12-01 07:05:57Z goudan $
 * @version $Rev: 82 $
 * @url $URL: https://188.188.2.100/svn/itheima17/trunk/MobileGuard/src/com/itheima17/mobileguard/utils/TaskUtils.java $
 * 
 */
public class TaskUtils {
	/**
	 * @return 总内存大小
	 */
	public static long getTotalMem(Context context) {
		long  totalSize = 0;
		File file = new File("/proc/meminfo");
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String memStr = reader.readLine();
			memStr = memStr.substring(memStr.indexOf(':') + 1, memStr.indexOf('k'));
			totalSize = Long.parseLong(memStr.trim());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return totalSize * 1024;
	}

	/**
	 * @return 剩余内存大小
	 */
	public static long getFreeMem(Context context) {
		// long size = 0;
		// 运行
		
		// 1 == 1.0
		// 1.0f == 1.0
		// new Float(1.0f) == new Double(1.0);
		// new Integer(1) == new Float(1.0f);
		//new Float(1.0f).equals(new Double(1.0))
		// if ( instanceof Float)
		//new Integer(1).equals(new Float(1.0f))
		
		// ActivityManager
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);

		MemoryInfo outInfo = new MemoryInfo();

		// 获取内存信息
		am.getMemoryInfo(outInfo);

		long availMem = outInfo.availMem;

		return availMem;
	}

	/**
	 * @return 所有运行中的进程信息
	 */
	public static List<AppBean> getAllRunningTaskInfos(Context context) {
		List<AppBean> mAppBeans = new ArrayList<AppBean>();
		PackageManager packageManager = context.getPackageManager();
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> runningAppProcesses = am.getRunningAppProcesses();
		
		for (RunningAppProcessInfo info:runningAppProcesses) {
			AppBean bean = new AppBean();
			bean.setPackName(info.processName);
			ApplicationInfo applicationInfo;
			try {
				applicationInfo = packageManager.getApplicationInfo(bean.getPackName(), 0);
				AppUtils.getBean(context.getPackageManager(), applicationInfo, bean);
				//内存大小
				//info.
				
				android.os.Debug.MemoryInfo[] memoryInfo = am.getProcessMemoryInfo(new int[]{info.pid});
				long totalPrivateDirty = memoryInfo[0].getTotalPrivateDirty();
				bean.setMemSize(totalPrivateDirty);//占用的内存
				
				//添加信息
				mAppBeans.add(bean);
			} catch (NameNotFoundException e) {
				// 没有名字的进程
				//e.printStackTrace();
			}
			
		}
		
		
		return mAppBeans;
	}
}
