package com.jtv.locationwork.util;

import java.util.ArrayList;
import java.util.List;

public class Arrays {

	public static boolean isEmpty(List list) {
		if (list == null) {
			return true;
		}
		if (list.size() < 1) {
			return true;
		}
		return false;
	}

	public static <T> List<T> convert(T[] t) {
		if (t == null) {
			return null;
		}
		ArrayList<T> arrayList = new ArrayList<T>();
		arr2List(t, arrayList);
		return arrayList;
	}

	public static <T> void arr2List(T[] t, List<T> list) {
		if (list == null || t == null) {
			return;
		}
		for (int i = 0; i < t.length; i++) {
			list.add(t[i]);
		}
	}
	
	public static <T> int find(T[] t, T t2) {
		for (int i = 0; i < t.length; i++) {
			boolean equals = t[i].equals(t2);
			if (equals) {
				return i;
			}
		}
		return -1;
	}
}
