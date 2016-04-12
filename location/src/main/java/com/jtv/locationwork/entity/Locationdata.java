package com.jtv.locationwork.entity;

import java.util.List;

import com.jtv.hrb.locationwork.domain.CurveLine;

/**
 * 线路检查的实体
 * <p>
 *
 * @author 更生
 * @version 2016年3月13日
 */
public class Locationdata {

	private String assetnum;

	private String classstructureid;

	private String description;

	// 直线检查
	private List<CurveLine> line;

	private String startmeasure;

	private String endmeasure;

	// 轨道检查
	private Turnout turnout;

	public void setAssetnum(String assetnum) {
		this.assetnum = assetnum;
	}

	public String getAssetnum() {
		return this.assetnum;
	}

	public void setTurnout(Turnout turnout) {
		this.turnout = turnout;
	}

	public Turnout getTurnout() {
		return this.turnout;
	}

	public void setClassstructureid(String classstructureid) {
		this.classstructureid = classstructureid;
	}

	public String getClassstructureid() {
		return this.classstructureid;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDescription() {
		return this.description;
	}


	public List<CurveLine> getLine() {
		return line;
	}

	public void setLine(List<CurveLine> line) {
		this.line = line;
	}

	public void setStartmeasure(String startmeasure) {
		this.startmeasure = startmeasure;
	}

	public String getStartmeasure() {
		return this.startmeasure;
	}

	public void setEndmeasure(String endmeasure) {
		this.endmeasure = endmeasure;
	}

	public String getEndmeasure() {
		return this.endmeasure;
	}

}
