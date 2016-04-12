package com.jtv.locationwork.util;

import java.util.Date;

public class SpeedTest {
	private double start;
	private double end;
	private String method;

	public SpeedTest(String method) {
		this.method = method;
	}

	public void start() {
		start = new Date().getTime();
	}

	public void end() {
		end = new Date().getTime();
	}

	public double consumeTime() { 
		return end - start;
	}
	//返回多少秒
	@Override
	public String toString() {
		return "[ " + method + " --消耗时间-- " + (consumeTime()/1000) + " ]";
	}

	public void print() {
		System.out.println(toString());
	}

}
