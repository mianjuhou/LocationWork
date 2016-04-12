package com.jtv.locationwork.entity;

import com.jtv.dbentity.annotation.Column;
import com.jtv.dbentity.annotation.Id;
import com.jtv.dbentity.annotation.Table;

@Table(name = "state")
public class FileState {
	@Id
	@Column(name = "id")
	private  int id;

	@Column(name = "state")
	private int state;

	@Column(name = "path")
	private String path;

	private Object obj;

	@Column(name = "message")
	protected String message;

	@Column(name = "message2")
	protected String message2;

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id=id;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public Object getObj() {
		return obj;
	}

	public void setObj(Object obj) {
		this.obj = obj;
	}

}
