package com.jtv.locationwork.imp;

import com.jtv.locationwork.dao.DBFactory;
import com.jtv.locationwork.entity.FilterDateBaseTree;
import com.jtv.locationwork.listener.Trees;
import com.jtv.locationwork.tree.DateBaseTree;
import com.jtv.locationwork.tree.DateBaseTree.BuildTree;
import com.jtv.locationwork.tree.ParmterMutableTreeNode;

import android.database.sqlite.SQLiteDatabase;

public class PhotoTree implements Trees {

	static ParmterMutableTreeNode root;

	static PhotoTree instance;

	public static PhotoTree getInstance() {
		if (instance == null) {
			instance = new PhotoTree();
		}
		return instance;
	}

	@Override
	public ParmterMutableTreeNode creatTree(String siteid) {
		if (root != null && root.getChildCount() > 0) {
			// System.out.println("HELLO--:"+root.toStringPaths(root));
			return root;
		}
		String [] parmter = new String[]{"parmeter","tabletype"};
		SQLiteDatabase readableDatabase = DBFactory.getConnection().getWritableDatabase();
		BuildTree configurationTreeForDB = new DateBaseTree.BuildTree();
		configurationTreeForDB.setDb(readableDatabase).setDb_table("photo_tree").setRoot_node_id(0)
				.setParent_id_colum_name("Parent_id");
		configurationTreeForDB.setLevel_column_name("level").setNode_id_column_name("Node_id")
				.setParmter_column_name(parmter).setTitle_column_name("title");
		FilterDateBaseTree filterDateBaseTree = new FilterDateBaseTree(configurationTreeForDB, siteid);
		ParmterMutableTreeNode tree = filterDateBaseTree.getTree();

		root = tree;
		return root;
	}

}
