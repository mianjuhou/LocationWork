package com.plutus.libraryui.ppw;

public class LayoutInfo {
	private String title;
	private int drawable;
	private int id;

	public LayoutInfo(int id, int drawable, String title) {
		this.id = id;
		this.drawable = drawable;
		this.title = title;
		this.title = title;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @return the drawable
	 */
	public int getDrawable() {
		return drawable;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

}
