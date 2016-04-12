package com.jtv.locationwork.util;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.alibaba.fastjson.serializer.AfterFilter;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.PropertyPreFilter;
import com.alibaba.fastjson.serializer.SerializeConfig;

/**
 * 解决过滤fastjson多余的字段toJsonString
 * @author Administrator
 *
 */
public class SimplePropertyPreFilter implements PropertyPreFilter {

	private final Class<?> clazz;
	private Set<String> includes = new HashSet<String>();
	private Set<String> excludes = new HashSet<String>();

	public SimplePropertyPreFilter(String... properties) {
		this(null, properties);
	}

	public SimplePropertyPreFilter(Class<?> clazz, String... properties) {
		super();
		this.clazz = clazz;
		for (String item : properties) {
			if (item != null) {
				this.includes.add(item);
			}
		}
	}

	public Class<?> getClazz() {
		return clazz;
	}

	public boolean apply(JSONSerializer serializer, Object source, String name) {
//		serializer.getWriter().writeString("hea");
		if (source == null) {
			return true;
		}
		
		if (clazz != null && !clazz.isInstance(source)) {
			return true;
		}

		if (this.excludes.contains(name)) {
			return false;
		}

		if (includes.size() == 0 || includes.contains(name)) {
			return true;
		}

		return false;
	}

	public Set<String> getIncludes() {
		return includes;
	}

	public Set<String> getExcludes() {
		return excludes;
	}

	public void setIncludes(Set<String> includes) {
		this.includes = includes;
	}

	public void setExcludes(Set<String> excludes) {
		this.excludes = excludes;
	}

}