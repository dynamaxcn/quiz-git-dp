package com.itheima17.mobileguard.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Environment;

import com.itheima17.mobileguard.domain.AppBean;

/**
 * @author Administrator
 * @date 2015-11-26
 * @pagename com.itheima17.mobileguard.utils
 * @desc 获取安装app的工具类

 * @svn author $Author: goudan $
 * @svn date $Date: 2015-12-01 17:07:03 +0800 (周二, 01 十二月 2015) $
 * @Id  $Id: AppUtils.java 84 2015-12-01 09:07:03Z goudan $
 * @version $Rev: 84 $
 * @url $URL: https://188.188.2.100/svn/itheima17/trunk/MobileGuard/src/com/itheima17/mobileguard/utils/AppUtils.java $ 
 * 
 */
public class AppUtils {
	/**
	 * @return
	 *    返回Rom卡的可用空间
	 */
	public static long getRomAvail(){
		File storageDirectory = Environment.getDataDirectory();
		long freeSpace = storageDirectory.getFreeSpace();
		return freeSpace;
	}
	
	/**
	 * @return
	 *     返回Rom卡的总大小
	 */
	public static long getRomTotal(){
		File storageDirectory = Environment.getDataDirectory();
		long totalSpace = storageDirectory.getTotalSpace();
		return totalSpace;
	}
	
	
	/**
	 * @return
	 *    返回sd卡的可用空间
	 */
	public static long getSDAvail(){
		File storageDirectory = Environment.getExternalStorageDirectory();
		long freeSpace = storageDirectory.getFreeSpace();
		return freeSpace;
	}
	
	/**
	 * @return
	 *     返回sd卡的总大小
	 */
	public static long getSDTotal(){
		File storageDirectory = Environment.getExternalStorageDirectory();
		long totalSpace = storageDirectory.getTotalSpace();
		return totalSpace;
	}
	
	public static List<AppBean> getAllInstalledAppInfos1(Context context){
		List<AppBean> datas = new ArrayList<AppBean>();
		
		//获取包管理器
		PackageManager packageManager = context.getPackageManager();
		
		List<PackageInfo> installedApplications = packageManager.getInstalledPackages(0);
		for (PackageInfo applicationInfo : installedApplications) {
			
			AppBean bean = new AppBean();
			bean.setPackName(applicationInfo.packageName);
			//添加
			datas.add(bean);
		}
		
		return datas;
	}
	/**
	 * @return
	 *    所有安装的app信息(用户，系统)
	 */
	public static List<AppBean> getAllInstalledAppInfos(Context context){
		List<AppBean> datas = new ArrayList<AppBean>();
		
		//获取包管理器
		PackageManager packageManager = context.getPackageManager();
		
		List<ApplicationInfo> installedApplications = packageManager.getInstalledApplications(0);
		for (ApplicationInfo applicationInfo : installedApplications) {
			
			AppBean bean = new AppBean();
			getBean(packageManager, applicationInfo, bean);
			
			//添加
			datas.add(bean);
		}
		
		return datas;
	}

	public static void getBean(PackageManager packageManager,
			ApplicationInfo applicationInfo, AppBean bean) {
		//获取app的图标
		Drawable icon = applicationInfo.loadIcon(packageManager);
		bean.setIcon(icon);
		//app的名字
		CharSequence appName = applicationInfo.loadLabel(packageManager) ;
		bean.setAppName(appName + "");
		//安装路径
		String sourceDir = applicationInfo.sourceDir;
		bean.setSourceDir(sourceDir);
		
		//包名
		String packageName = applicationInfo.packageName;
		bean.setPackName(packageName);
		
		bean.setPid(applicationInfo.uid);//设置进程id
		
		//app的类型 :安装位置，是否是系统app
		int flags = applicationInfo.flags;
		
		if ((flags & ApplicationInfo.FLAG_SYSTEM) != 0){
			//系统app
			bean.setSystem(true);
		} else {
			bean.setSystem(false);//用户app
		}
		
		if ((flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) != 0){
			//sd
			bean.setRom(false);
		} else {
			//rom
			bean.setRom(true);
		}
		
		//占用的大小
		File file = new File(sourceDir);
		long length = file.length();
		bean.setSize(length);
	}
}
