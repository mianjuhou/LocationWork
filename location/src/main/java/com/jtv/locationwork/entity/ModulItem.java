package com.jtv.locationwork.entity;

import com.jtv.locationwork.listener.ModulItemListener;

public class ModulItem {
	private int position;
	private String title;
	private int icon;
	private int stateIcon;
	protected ModulItemListener back;
	public int getPosition() {
		return position;
	}
	public void setPosition(int position) {
		this.position = position;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public int getIcon() {
		return icon;
	}
	public void setIcon(int icon) {
		this.icon = icon;
	}
	public ModulItemListener getBack() {
		return back;
	}
	public void setBack(ModulItemListener back) {
		this.back = back;
	}
	public int getStateIcon() {
		return stateIcon;
	}
	public void setStateIcon(int stateIcon) {
		this.stateIcon = stateIcon;
	}
	
}
