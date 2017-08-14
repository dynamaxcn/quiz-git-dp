package com.itheima17.mobileguard.domain;

import java.util.List;

public class SmsJsonData {
	public List<Smses> smses;
	public class Smses{
		public String body	;//{:,{}"{
		public int date;//	1377014200
		public String phone;//	123
		public int type;//	1
		public int date_sent;
	}
}
