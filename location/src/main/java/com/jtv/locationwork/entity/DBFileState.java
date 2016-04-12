package com.jtv.locationwork.entity;

import com.jtv.dbentity.annotation.Table;

@Table(name ="state")
public class DBFileState extends FileState {
	
	private Object obj2  = null;
	
	public Object getObj2() {
		return obj2;
	}

	public void setObj2(Object obj2) {
		this.obj2 = obj2;
	}

	public String getMessage() {
		return message;
	}
   //存储的是过滤的id
	public void setMessage(String message) {
		this.message = message;
	}
	//暂时用来存储的是三层的标题
	public String getMessage2() {
		return message2;
	}

	public void setMessage2(String message2) {
		this.message2 = message2;
	}
}
