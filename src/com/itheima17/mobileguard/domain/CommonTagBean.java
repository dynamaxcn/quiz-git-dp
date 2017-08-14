package com.itheima17.mobileguard.domain;

/**
 * @author Administrator
 * @date 2015-11-29
 * @pagename com.itheima17.mobileguard.domain
 * @desc 公共服务类型封装类

 * @svn author $Author: goudan $
 * @svn date $Date: 2015-11-29 09:31:41 +0800 (周日, 29 十一月 2015) $
 * @Id  $Id: CommonTagBean.java 56 2015-11-29 01:31:41Z goudan $
 * @version $Rev: 56 $
 * @url $URL: https://188.188.2.100/svn/itheima17/trunk/MobileGuard/src/com/itheima17/mobileguard/domain/CommonTagBean.java $ 
 * 
 */
public class CommonTagBean {
	
	@Override
	public String toString() {
		return "CommonTagBean [typeName=" + typeName + ", table_id=" + table_id
				+ "]";
	}
	private String typeName;//类型的名字
	public String getTypeName() {
		return typeName;
	}
	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}
	public int getTable_id() {
		return table_id;
	}
	public void setTable_id(int table_id) {
		this.table_id = table_id;
	}
	private int table_id;//表名后面的编号
}
