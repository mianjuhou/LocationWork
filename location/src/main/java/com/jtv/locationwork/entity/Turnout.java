package com.jtv.locationwork.entity;

import java.util.List;

public class Turnout {
	private String assetattrid;
    private List<?> offsetcfg;

    public void setAssetattrid(String assetattrid) {
        this.assetattrid = assetattrid;
    }

    public void setOffsetcfg(List<?> offsetcfg) {
        this.offsetcfg = offsetcfg;
    }

    public String getAssetattrid() {
        return assetattrid;
    }

    public List<?> getOffsetcfg() {
        return offsetcfg;
    }
}