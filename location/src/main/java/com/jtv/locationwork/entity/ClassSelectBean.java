package com.jtv.locationwork.entity;

import java.util.ArrayList;
import java.util.List;

public class ClassSelectBean {
	public boolean isSelected;
	public String catalogName;
	public List<String> detailNames=new ArrayList<String>();
	public String getCatalogName() {
		return catalogName;
	}
	public void setCatalogName(String catalogName) {
		this.catalogName = catalogName;
	}
	public List<String> getDetailNames() {
		return detailNames;
	}
	public void setDetailNames(List<String> detailNames) {
		this.detailNames = detailNames;
	}
	public boolean isSelected() {
		return isSelected;
	}
	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}
	
}
