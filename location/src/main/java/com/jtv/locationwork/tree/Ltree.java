package com.jtv.locationwork.tree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Ltree<T> extends DefaultMutableTreeNode {
	private static final long serialVersionUID = 1L;
	public T t;

	public void setT(T t) {
		this.t = t;
	}

	public T getT() {
		return t;
	}

	private int i = 1;
	private List<T> d = (List<T>) new ArrayList<Ltree<T>>();

	public void removeChilds(T t) {
		i++;
		getAll(i, this, d);
		TreeNode re = null;
		for (int i = 0; i < d.size(); i++) {
			T t2 = d.get(i);
			if (t.equals(t2)) {
				re = (TreeNode) d.get(i);
				return;
			}
		}

	}

	public String getPaths() {
		TreeNode[] path = getPath();
		String paths = "";
		if (path != null) {
			for (TreeNode treeNode : path) {
				String string = treeNode.toString();
				paths += "/" + string;
			}
		}
		return paths;
	}

	private static StringBuilder sb = new StringBuilder();

	public String toStringPaths(ParmterMutableTreeNode tree) {
		if (tree == null) {
			return null;
		}
		int childCount = tree.getChildCount();
		if (childCount < 1) {
			TreeNode[] path = tree.getPath();
			String mPath = Arrays.toString(path);
			sb.append(mPath);
			sb.append("\r\n");
			// sb.toString();
			return sb.toString();

		} else {
			for (int i = 0; i < childCount; i++) {

				ParmterMutableTreeNode childAt = (ParmterMutableTreeNode) tree.getChildAt(i);
				// int size = childAt.getChildCount();
				if (childAt != null) {

					toStringPaths(childAt);
				}

			}
		}
		return sb.toString();
	}

	public Ltree<T> has(String name) {
		List<Ltree<T>> childs = getChilds();
		if (childs != null) {
			for (Ltree<T> parmterMutableTreeNode : childs) {
				Object userObject2 = parmterMutableTreeNode.getUserObject();
				if (userObject2 != null && userObject2.toString().equals(name)) {
					return parmterMutableTreeNode;
				}
			}
		}
		return null;

	}

	public int isAdd = 1;// 标记

	/**
	 * 从根节点获取所有的子孩子，包括文件夹和文件
	 * 
	 * @param tree
	 *            根节点
	 * @return 跟节点下面所有的子孩子
	 */
	public static <T> void getAll(int flag, Ltree<T> tree, List<T> all) {

		if (tree == null) {
			return;
		}
		int childCount = tree.getChildCount();
		if (childCount < 1) {// 代表没有子孩子

			if (tree.isAdd != flag) {
				tree.isAdd = flag;
				all.add(tree.getT());
			}

		} else {// 代表有子孩子，文件夹

			if (tree.isAdd != flag) {
				tree.isAdd = flag;
				all.add(tree.getT());
			}

			for (int i = 0; i < childCount; i++) {

				Ltree<T> childAt = (Ltree<T>) tree.getChildAt(i);
				if (childAt != null) {
					T t2 = childAt.getT();
				}
				if (childAt != null) {
					getAll(flag, childAt, all);
				}
			}

		}
	}

	public List<Ltree<T>> getChilds() {
		int childCount = getChildCount();

		ArrayList<Ltree<T>> arrayList = new ArrayList<Ltree<T>>();

		for (int i = 0; i < childCount; i++) {
			Ltree<T> childAt = (Ltree<T>) getChildAt(i);
			arrayList.add(childAt);
		}

		return arrayList;
	}
}
