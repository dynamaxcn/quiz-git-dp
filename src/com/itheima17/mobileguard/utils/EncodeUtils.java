package com.itheima17.mobileguard.utils;

import java.io.UnsupportedEncodingException;

/**
 * @author Administrator
 * @date 2015-11-29
 * @pagename com.itheima17.mobileguard.utils
 * @desc 自定义加密算法

 * @svn author $Author: goudan $
 * @svn date $Date: 2015-11-29 16:58:13 +0800 (周日, 29 十一月 2015) $
 * @Id  $Id: EncodeUtils.java 65 2015-11-29 08:58:13Z goudan $
 * @version $Rev: 65 $
 * @url $URL: https://188.188.2.100/svn/itheima17/trunk/MobileGuard/src/com/itheima17/mobileguard/utils/EncodeUtils.java $ 
 * 
 */
public class EncodeUtils {
	/**
	 * @param str
	 *     原文
	 * @return
	 *    密文
	 */
	public static String jiami(String str) {
		// 01 换算
		
		try {
			byte[] bytes = str.getBytes("gbk");
			
			//加密
			for (int i = 0; i < bytes.length ; i++) {
				 bytes[i] ^= MyContains.MUSICNUMBER;
			}
			
			return new String(bytes,"gbk");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
