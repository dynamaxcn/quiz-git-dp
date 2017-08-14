package com.itheima17.mobileguard.activity;

import java.util.List;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.itheima17.mobileguard.dao.ContactsDao;
import com.itheima17.mobileguard.domain.ContactBean;
import com.itheima17.mobileguard.utils.PrintLog;

/**
 * @author Administrator
 * @date 2015-11-22
 * @pagename com.itheima17.mobileguard.activity
 * @desc 显示好友信息的activity listView

 * @svn author $Author: goudan $
 * @svn date $Date: 2015-11-25 15:43:02 +0800 (周三, 25 十一月 2015) $
 * @Id  $Id: ContactsActivity.java 37 2015-11-25 07:43:02Z goudan $
 * @version $Rev: 37 $
 * @url $URL: https://188.188.2.100/svn/itheima17/trunk/MobileGuard/src/com/itheima17/mobileguard/activity/ContactsActivity.java $ 
 * 
 */
public class ContactsActivity extends BaseContactsCallogSmsActivity {

	@Override
	public List<ContactBean> getDatas() {
		// 获取好友
		return ContactsDao.getAllBeans(getApplicationContext());
	}
	
}
