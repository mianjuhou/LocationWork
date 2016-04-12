package com.jtv.locationwork.entity;

import java.util.List;

/**
 * 线路检查的实体
 * <p>
 *
 * @author 更生
 * @version 2016年3月13日
 */
public class Line {

	private double xxqc;

	private List<Planvalue> planvalue;

	private int zhxc;

	private int qhxc;

	private int qxbj;

	public void setXxqc(double xxqc) {
		this.xxqc = xxqc;
	}

	public double getXxqc() {
		return this.xxqc;
	}

	public void setPlanvalue(List<Planvalue> planvalue) {
		this.planvalue = planvalue;
	}

	public List<Planvalue> getPlanvalue() {
		return this.planvalue;
	}

	public void setZhxc(int zhxc) {
		this.zhxc = zhxc;
	}

	public int getZhxc() {
		return this.zhxc;
	}

	public void setQhxc(int qhxc) {
		this.qhxc = qhxc;
	}

	public int getQhxc() {
		return this.qhxc;
	}

	public void setQxbj(int qxbj) {
		this.qxbj = qxbj;
	}

	public int getQxbj() {
		return this.qxbj;
	}

}
