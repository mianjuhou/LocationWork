package com.jtv.hrb.locationwork.domain;

import java.util.ArrayList;
import java.util.List;

public class StraightLineBean {
	private int _id;
	private String wonum;
	private String assetnum;
	private double startlc;
	private double endlc;
	private List<RailNumBean> railNumBeans = new ArrayList<RailNumBean>();

	public int get_id() {
		return _id;
	}

	public void set_id(int _id) {
		this._id = _id;
	}


	public String getWonum() {
		return wonum;
	}

	public void setWonum(String wonum) {
		this.wonum = wonum;
	}

	public String getAssetnum() {
		return assetnum;
	}

	public void setAssetnum(String assetnum) {
		this.assetnum = assetnum;
	}

	public double getStartlc() {
		return startlc;
	}

	public void setStartlc(double startlc) {
		this.startlc = startlc;
	}

	public double getEndlc() {
		return endlc;
	}

	public void setEndlc(double endlc) {
		this.endlc = endlc;
	}

	public List<RailNumBean> getRailNumBeans() {
		return railNumBeans;
	}

	public void setRailNumBeans(List<RailNumBean> railNumBeans) {
		this.railNumBeans = railNumBeans;
	}
}
