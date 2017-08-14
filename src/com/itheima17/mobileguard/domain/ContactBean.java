package com.itheima17.mobileguard.domain;

/**
 * @author Administrator
 * @date 2015-11-22
 * @pagename com.itheima17.mobileguard.domain
 * @desc 联系人信息封装类

 * @svn author $Author: goudan $
 * @svn date $Date: 2015-11-29 16:58:13 +0800 (周日, 29 十一月 2015) $
 * @Id  $Id: ContactBean.java 65 2015-11-29 08:58:13Z goudan $
 * @version $Rev: 65 $
 * @url $URL: https://188.188.2.100/svn/itheima17/trunk/MobileGuard/src/com/itheima17/mobileguard/domain/ContactBean.java $ 
 * 
 */
public class ContactBean {
	public int date_sent;//短信的当前时间
	public String body;//短信内容
	public int type;//短信类型
	public int date;//短信的日期 
	
	public String phone;
	public String name;
	@Override
	public String toString() {
		return "ContactBean [phone=" + phone + ", name=" + name + "]";
	}
}
