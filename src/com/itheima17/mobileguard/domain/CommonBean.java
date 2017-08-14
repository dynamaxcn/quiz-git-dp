package com.itheima17.mobileguard.domain;

/**
 * @author Administrator
 * @date 2015-11-29
 * @pagename com.itheima17.mobileguard.domain
 * @desc 公共服务号码封装

 * @svn author $Author: goudan $
 * @svn date $Date: 2015-11-29 10:04:27 +0800 (周日, 29 十一月 2015) $
 * @Id  $Id: CommonBean.java 57 2015-11-29 02:04:27Z goudan $
 * @version $Rev: 57 $
 * @url $URL: https://188.188.2.100/svn/itheima17/trunk/MobileGuard/src/com/itheima17/mobileguard/domain/CommonBean.java $ 
 * 
 */
public class CommonBean {
	
	@Override
	public String toString() {
		return "CommonBean [name=" + name + ", number=" + number + "]";
	}
	private String name;//服务名字
	private String number;//服务号码
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}
}
