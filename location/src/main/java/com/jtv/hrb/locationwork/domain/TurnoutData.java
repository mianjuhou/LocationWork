package com.jtv.hrb.locationwork.domain;

import java.util.List;

public class TurnoutData {
	private String assetattrid;
	private List<String> offsetcfg;
	public String getAssetattrid() {
		return assetattrid;
	}
	public void setAssetattrid(String assetattrid) {
		this.assetattrid = assetattrid;
	}
	public List<String> getOffsetcfg() {
		return offsetcfg;
	}
	public void setOffsetcfg(List<String> offsetcfg) {
		this.offsetcfg = offsetcfg;
	}
}
