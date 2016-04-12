package com.jtv.locationwork.services;


public class ProgressBean {
	private int hashcode;
	private long id;
	private String path;
	private String personid;

	public int getHashcode() {
		return hashcode;
	}
	public long getID() {
		return id;
	}

	public String getPath() {
		return path;
	}
	public String getPersonid() {
		return personid;
	}
	/**
	 * 
	 * @param hashcode 下载路径的hashcode唯一标示
	 * @param ID 唯一的标示
	 * @param path 下载地址
	 * @param personid 下载的id由后台返回来标示当前是否下载过
	 */
	public ProgressBean(int hashcode, long id, String path,String personid) {
		this.hashcode = hashcode;
		this.id = id;
		this.path = path;
		this.personid=personid;
	}
	public void setId(long id) {
		this.id=id;
	}

}