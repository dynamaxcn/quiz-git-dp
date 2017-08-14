package com.itheima17.mobileguard.domain;

public class BlackBean {
	private String number;//号码
	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	public int getModel() {
		return model;
	}
	public void setModel(int model) {
		this.model = model;
	}
	private int model;//模式
	@Override
	public String toString() {
		return "BlackBean [number=" + number + ", model=" + model + "]";
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof BlackBean) {
			BlackBean b = (BlackBean)o;
			return this.number.equals(b.getNumber());
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return number.hashCode();//string 实现hashCode 
	}
	
}
