package com.jtv.locationwork.tree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 
 * @author 财神
 * @email whatl@foxmail.com
 * @date 2015年8月18日
 */
public class ParmterMutableTreeNode extends DefaultMutableTreeNode {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Object[] parmter;

	public boolean isSelected=false;
	
	
	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}

	public ParmterMutableTreeNode(Object obj, boolean allowChild, Object... parmter) {
		super(obj, allowChild);
		this.parmter = parmter;
	}

	public ParmterMutableTreeNode(Object obj, String obj1, String obj2) {
		this(obj, true, obj1, obj2);
	}

	public ParmterMutableTreeNode(Object obj, Object parmter) {
		this(obj, true, parmter);
	}

	public ParmterMutableTreeNode(Object obj, boolean allowChild) {
		this(obj, allowChild, "");
	}

	public ParmterMutableTreeNode(Object obj) {
		this(obj, true, "");
	}

	public Object[] getParmter() {
		return parmter;
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

	public List<ParmterMutableTreeNode> getChilds() {
		int childCount = getChildCount();
		
		ArrayList<ParmterMutableTreeNode> arrayList = new ArrayList<ParmterMutableTreeNode>();
		
		for (int i = 0; i < childCount; i++) {
			ParmterMutableTreeNode childAt = (ParmterMutableTreeNode) getChildAt(i);
			arrayList.add(childAt);
		}
		
		return arrayList;
	}
}
