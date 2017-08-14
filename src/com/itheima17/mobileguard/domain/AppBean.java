package com.itheima17.mobileguard.domain;

import android.graphics.drawable.Drawable;

/**
 * @author Administrator
 * @date 2015-11-26
 * @pagename com.itheima17.mobileguard.domain
 * @desc app的信息封装类

 * @svn author $Author: goudan $
 * @svn date $Date: 2015-12-04 16:56:07 +0800 (周五, 04 十二月 2015) $
 * @Id  $Id: AppBean.java 98 2015-12-04 08:56:07Z goudan $
 * @version $Rev: 98 $
 * @url $URL: https://188.188.2.100/svn/itheima17/trunk/MobileGuard/src/com/itheima17/mobileguard/domain/AppBean.java $ 
 * 
 */
public class AppBean implements Comparable<AppBean> {
	private int pid;//进程id
	public int getPid() {
		return pid;
	}
	public void setPid(int pid) {
		this.pid = pid;
	}
	private boolean isChecked;
	public boolean isChecked() {
		return isChecked;
	}
	public void setChecked(boolean isChecked) {
		this.isChecked = isChecked;
	}
	private long memSize;//占用内存大小
	public long getMemSize() {
		return memSize;
	}
	public void setMemSize(long memSize) {
		this.memSize = memSize;
	}
	private Drawable icon;//图标
	private String appName;//app名字
	private String sourceDir;//安装位置
	private boolean isRom;//安装位置： rom sd
	private long size;//大小
	private String packName;//包名
	private boolean isSystem;//app的标记 用户或系统
	public Drawable getIcon() {
		return icon;
	}
	public void setIcon(Drawable icon) {
		this.icon = icon;
	}
	public String getAppName() {
		return appName;
	}
	public void setAppName(String appName) {
		this.appName = appName;
	}
	public String getSourceDir() {
		return sourceDir;
	}
	public void setSourceDir(String sourceDir) {
		this.sourceDir = sourceDir;
	}
	public boolean isRom() {
		return isRom;
	}
	public void setRom(boolean isRom) {
		this.isRom = isRom;
	}
	public long getSize() {
		return size;
	}
	public void setSize(long size) {
		this.size = size;
	}
	public String getPackName() {
		return packName;
	}
	public void setPackName(String packName) {
		this.packName = packName;
	}
	public boolean isSystem() {
		return isSystem;
	}
	public void setSystem(boolean isSystem) {
		this.isSystem = isSystem;
	}
	@Override
	public String toString() {
		return "AppBean [appName=" + appName + ", packName=" + packName + "]";
	}
	@Override
	public int compareTo(AppBean another) {
		if (another.isSystem) {
			return -1;
		} else {
			return 3;
		}
		
	}
	

	
	
	
}
