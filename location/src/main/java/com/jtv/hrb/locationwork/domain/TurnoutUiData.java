package com.jtv.hrb.locationwork.domain;

import java.util.ArrayList;

public class TurnoutUiData {
	public int partOrder;
	public int rowNum;
	public String partName;
	public ArrayList<ArrayList<Boolean>> editMatrix=new ArrayList<ArrayList<Boolean>>();
	public ArrayList<String> rowNames=new ArrayList<String>();
	public ArrayList<String> rowNums=new ArrayList<String>();
	
	public int getPartOrder() {
		return partOrder;
	}
	public void setPartOrder(int partOrder) {
		this.partOrder = partOrder;
	}
	public int getRowNum() {
		return rowNum;
	}
	public void setRowNum(int rowNum) {
		this.rowNum = rowNum;
	}
	public String getPartName() {
		return partName;
	}
	public void setPartName(String partName) {
		this.partName = partName;
	}
	
	public ArrayList<ArrayList<Boolean>> getEditMatrix() {
		return editMatrix;
	}
	public void setEditMatrix(ArrayList<ArrayList<Boolean>> editMatrix) {
		this.editMatrix = editMatrix;
	}
	public ArrayList<String> getRowNames() {
		return rowNames;
	}
	public void setRowNames(ArrayList<String> rowNames) {
		this.rowNames = rowNames;
	}
	public ArrayList<String> getRowNums() {
		return rowNums;
	}
	public void setRowNums(ArrayList<String> rowNums) {
		this.rowNums = rowNums;
	}
	
}
