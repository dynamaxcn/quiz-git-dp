package com.itheima17.mobileguard.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Utils {
	public static String md5encode(String md5) {
		
		StringBuilder mess = new StringBuilder();
		try {
			MessageDigest messageDigest = MessageDigest.getInstance("MD5");
			
			byte[] digest = messageDigest.digest(md5.getBytes());
			//转成十六进制数输出
			for (byte b: digest) {
				//byte 转换int
				int t = b & 0x000000ff;
				String s = Integer.toHexString(t);
				if (s.length() == 1) {
					s = "0" + s;
				}
				//System.out.println(s);
				mess.append(s);
			}
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return mess + "";
	}
	
	
	/**
	 * 计算出文件的MD5值
	 * @param path
	 *       文件的路径
	 * @return
	 *       文件的MD5值
	 */
	public static String getFile(String path){
		StringBuilder md5 = new StringBuilder();
		File file = new File(path);
		
		
		
		//用字节流读取文件内容
		try {
			MessageDigest md = MessageDigest.getInstance("md5");
			
			FileInputStream fis = new FileInputStream(file);
			byte[] buffer = new byte[1024*5];//一次读取5k的大小
			
			int len = fis.read(buffer);//默认每次从0开始写数据
			
			while (len != -1){
				//读到数据
				md.update(buffer, 0, len);//buffer 字节数组
				//继续读取文件
				len = fis.read(buffer);//默认每次从0开始写数据
				
			}
			
			fis.close();
			byte[] digest = md.digest();
			
			for (byte b: digest) {
				//byte 转换int
				int t = b & 0x000000ff;
				String s = Integer.toHexString(t);
				if (s.length() == 1) {
					s = "0" + s;
				}
				//System.out.println(s);
				md5.append(s);
			}
			
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return md5 + "";
		
	}

}
