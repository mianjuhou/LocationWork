package com.jtv.locationwork.entity;

import com.jtv.dbentity.annotation.Column;
import com.jtv.dbentity.annotation.Id;
import com.jtv.dbentity.annotation.Table;

@Table(name = "local_cache")
public class LocalCache {

	@Id
	@Column(name = "cache_id")
	private int cache_id;
	@Column(name = "ip")
	private String ip;
	@Column(name = "port")
	private String port;
	@Column(name = "description")
	private String description;
	@Column(name = "data")
	private String data;
	@Column(name = "classpath")
	private String classpath;
	@Column(name = "method")
	private String method;
	@Column(name = "state")
	private int state;
	@Column(name = "key")
	private String key;
	@Column(name = "value")
	private String value;
	@Column(name = "key1")
	private String key1;
	@Column(name = "value1")
	private String value1;
	@Column(name = "key2")
	private String key2;
	@Column(name = "value2")
	private String value2;
	@Column(name = "key3")
	private String key3;
	@Column(name = "value3")
	private String value3;
	@Column(name = "key4")
	private String key4;

	@Column(name = "value4")
	private String value4;

	@Column(name = "key5")
	private String key5;

	@Column(name = "key6")
	private String key6;

	@Column(name = "value6")
	private String value6;

	@Column(name = "value5")
	private String value5;

	@Column(name = "key7")
	private String key7;

	@Column(name = "value7")
	private String value7;

	@Column(name = " key8")
	private String key8;

	@Column(name = "value8")
	private String value8;

	public int getCache_id() {
		return cache_id;
	}

	public void setCache_id(int cache_id) {
		this.cache_id = cache_id;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public String getClasspath() {
		return classpath;
	}

	public void setClasspath(String classpath) {

		try {
			if(classpath.startsWith("class")){
				String path =classpath;
				String trim = path.trim();
				trim = trim.replaceFirst("class", "");
				trim = trim.trim();
				classpath = trim;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		this.classpath = classpath;
	}

	public void setClasspath(Class cla) {
		setClasspath(cla.getName());

	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getKey1() {
		return key1;
	}

	public void setKey1(String key1) {
		this.key1 = key1;
	}

	public String getValue1() {
		return value1;
	}

	public void setValue1(String value1) {
		this.value1 = value1;
	}

	public String getKey2() {
		return key2;
	}

	public void setKey2(String key2) {
		this.key2 = key2;
	}

	public String getValue2() {
		return value2;
	}

	public void setValue2(String value2) {
		this.value2 = value2;
	}

	public String getKey3() {
		return key3;
	}

	public void setKey3(String key3) {
		this.key3 = key3;
	}

	public String getValue3() {
		return value3;
	}

	public void setValue3(String value3) {
		this.value3 = value3;
	}

	public String getKey4() {
		return key4;
	}

	public void setKey4(String key4) {
		this.key4 = key4;
	}

	public String getValue4() {
		return value4;
	}

	public void setValue4(String value4) {
		this.value4 = value4;
	}

	public String getKey5() {
		return key5;
	}

	public void setKey5(String key5) {
		this.key5 = key5;
	}

	public String getValue5() {
		return value5;
	}

	public void setValue5(String value5) {
		this.value5 = value5;
	}

	public String getKey6() {
		return key6;
	}

	public void setKey6(String key6) {
		this.key6 = key6;
	}

	public String getValue6() {
		return value6;
	}

	public void setValue6(String value6) {
		this.value6 = value6;
	}

	public String getKey7() {
		return key7;
	}

	public void setKey7(String key7) {
		this.key7 = key7;
	}

	public String getValue7() {
		return value7;
	}

	public void setValue7(String value7) {
		this.value7 = value7;
	}

	public String getKey8() {
		return key8;
	}

	public void setKey8(String key8) {
		this.key8 = key8;
	}

	public String getValue8() {
		return value8;
	}

	public void setValue8(String value8) {
		this.value8 = value8;
	}

}
