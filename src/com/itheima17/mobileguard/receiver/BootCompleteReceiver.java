package com.itheima17.mobileguard.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;

import com.itheima17.mobileguard.service.LostFindService;
import com.itheima17.mobileguard.utils.MyContains;
import com.itheima17.mobileguard.utils.SPUtils;

/**
 * @author Administrator
 * @date 2015-11-23
 * @pagename com.itheima17.mobileguard.receiver
 * @desc 开机广播

 * @svn author $Author: goudan $
 * @svn date $Date: 2015-11-23 15:31:36 +0800 (周一, 23 十一月 2015) $
 * @Id  $Id: BootCompleteReceiver.java 33 2015-11-23 07:31:36Z goudan $
 * @version $Rev: 33 $
 * @url $URL: https://188.188.2.100/svn/itheima17/trunk/MobileGuard/src/com/itheima17/mobileguard/receiver/BootCompleteReceiver.java $ 
 * 
 */
public class BootCompleteReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// 检测sim卡是否变化
		String simBind = SPUtils.getString(context, MyContains.SIMSERIALNUMBER, "") + "1";
		//获取当前手机的sim卡
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		String currentSim = tm.getSimSerialNumber();
		
		//判断
		if (!currentSim.equals(simBind)) {
			//sim卡不一致
			//发送报警短信给安全号码
			String safeNumber = SPUtils.getString(context, MyContains.SAFENUMBER, "");
			String mess = "i am xiaotou";
			
			//发送短信   
			SmsManager smsManager = SmsManager.getDefault();
			smsManager.sendTextMessage(safeNumber, null, mess, null, null);
		}
		
		//是否需要启动防盗服务
		if (SPUtils.getBoolean(context, MyContains.ISOPENLOSTFIND, false)) {
			//开启防盗保护
			Intent service = new Intent(context,LostFindService.class);
			context.startService(service);
		}

	}

}
