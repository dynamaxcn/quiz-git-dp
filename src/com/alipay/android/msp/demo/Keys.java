/*
 * Copyright (C) 2010 The MobileSecurePay Project
 * All right reserved.
 * author: shiqun.shi@alipay.com
 * 
 *  提示：如何获取安全校验码和合作身份者id
 *  1.用您的签约支付宝账号登录支付宝网站(www.alipay.com)
 *  2.点击“商家服务”(https://b.alipay.com/order/myorder.htm)
 *  3.点击“查询合作者身份(pid)”、“查询安全校验码(key)”
 */

package com.alipay.android.msp.demo;

//
// 请参考 Android平台安全支付服务(msp)应用开发接口(4.2 RSA算法签名)部分，并使用压缩包中的openssl RSA密钥生成工具，生成一套RSA公私钥。
// 这里签名时，只需要使用生成的RSA私钥。
// Note: 为安全起见，使用RSA私钥进行签名的操作过程，应该尽量放到商家服务器端去进行。
public final class Keys {

	//合作身份者id，以2088开头的16位纯数字
	public static final String DEFAULT_PARTNER = "2088102174863400";
 
	//收款支付宝账号
	public static final String DEFAULT_SELLER = "ekmdlg6785@sandbox.com";

	//商户私钥，自助生成
	public static final String PRIVATE = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBANBDdC1j+dCaRL4BpE87TtF4xWLxxfwAAnQgzGzRZ7wm1XrT/vi2x01yCk6qmbGA4hyzmGx/Ouam5I248T7owCH/wm94lOEonmfwItnJIw6M/xkqW9Rdp9lyzei+O3XgKXGXO2sO3UWH7V0c0dnCDYo/3CrszKhQukboISVAoAUvAgMBAAECgYEAkeb/yCDnTc4V5KtPyjY5hKTAXA4XbvEuxt6HeKDHtNfb7T4BvYqHW+lN8UixfdpWDld/rFsCD77SFbuAP1td5fA10K/QLQ2H/XSHWN3pSn4lQhwK3QtemWKTzcL6E0brxFIxeltrN1BmHBXqfDqh5ye8ant+N4lu7d5IZiNN4ZkCQQDpJM7l+7rB9EVledhcCxXXNp/UVNReJym7kbkR7/AF2raE67iNubtDINr3peLQ87RCVGvorMTu70gpPw4IyUadAkEA5K46E1Ixj0zuOKtwoh+z7j0063U7ep+CtQxgs7HowAXtOcnxXFA0Fhl3ynZIfJbLpjoz6wYvDAvYWYlfVMQLOwJAIS8MA9AAlPqfpgHhubgaM7eiqCYq0/vfBMOJA/SSxUKMF/81FXcKIFPc1sLVtcf6MDaz5ToyDZAhRT2kUbvUQQJBAMRT1CZMVaIqsQbUISH/R9HMmIdhoYv4MBZRpx//KMzbCbyoZGAVt3IVsWOPw1G7gOGYbIRlTbTw4nCUYNT8bosCQCmpTxfxseYrJrslnYK/ERbbhEtqocU3JU7n2Ft6TFxsYmWOcVQS8ScSz/wuEIe7aNDcsvUgBiqEdIDTYEbTlyA=";

	public static final String PUBLIC = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCnxj/9qwVfgoUh/y2W89L6BkRAFljhNhgPdyPuBV64bfQNN1PjbCzkIM6qRdKBoLPXmKKMiFYnkd6rAoprih3/PrQEB/VsW8OoM8fxn67UDYuyBTqA23MML9q1+ilIZwBC2AQ2UBVOrFXfFl75p6/B5KsiNG9zpgmLCUYuLkxpLQIDAQAB";

}
