package com.tcn.vending.springdemo.presentation;

public class MdbInfoBean {
	private int iSingleValue = -1;    //单个面值
	private int iCount = -1;    //个数

	public MdbInfoBean(int singleValue, int count) {
		this.iSingleValue = singleValue;
		this.iCount = count;
	}

	public void setSingleValue(int value)
	{
		this.iSingleValue = value;
	}

	public int getSingleValue()
	{
		return this.iSingleValue;
	}


	public void setCount(int count)
	{
		this.iCount = count;
	}

	public int getCount()
	{
		return this.iCount;
	}
}
