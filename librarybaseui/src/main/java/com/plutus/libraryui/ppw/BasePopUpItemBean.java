package com.plutus.libraryui.ppw;
public class BasePopUpItemBean implements PopUpItemBean {
	String text = null;

	int id = -1;

	private Object obj;

	private BasePopUpItemBean() {
	};

	public BasePopUpItemBean(String text, Object obj) {
		this.text = text;
		this.obj = obj;
	}

	public BasePopUpItemBean(String text, Object obj, int ID) {
		this.text = text;
		this.obj = obj;
		this.id = ID;
	}

	public BasePopUpItemBean(String text) {
		this(text, null);
	}

	@Override
	public String getText() {
		return text;
	}

	public Object getObj() {
		return obj;
	}

	@Override
	public int getID() {
		return id;
	}

}
