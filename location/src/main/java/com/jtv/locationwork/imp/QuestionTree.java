package com.jtv.locationwork.imp;

import com.jtv.locationwork.dao.DBFactory;
import com.jtv.locationwork.listener.Trees;
import com.jtv.locationwork.tree.DateBaseTree;
import com.jtv.locationwork.tree.DateBaseTree.BuildTree;
import com.jtv.locationwork.tree.ParmterMutableTreeNode;

import android.database.sqlite.SQLiteDatabase;

public class QuestionTree implements Trees{
	static ParmterMutableTreeNode root;

	static QuestionTree instance;

	public static QuestionTree getInstance() {
		if (instance == null) {
			instance = new QuestionTree();
		}
		return instance;
	}
	
	@Override
	public ParmterMutableTreeNode creatTree(String siteid) {
		if (root != null && root.getChildCount() > 0) {
			return root;
		}
		
		String [] parmter = new String[]{"parmter"};
		
		SQLiteDatabase readableDatabase = DBFactory.getConnection().getWritableDatabase();
		
		BuildTree configurationTreeForDB = new DateBaseTree.BuildTree();
		configurationTreeForDB.setDb(readableDatabase).setDb_table("question_tree").setRoot_node_id(0)
				.setParent_id_colum_name("Parent_id");
		configurationTreeForDB.setLevel_column_name("level").setNode_id_column_name("Node_id")
				.setParmter_column_name(parmter).setTitle_column_name("title");
		DateBaseTree instance2 = configurationTreeForDB.getInstance();
		ParmterMutableTreeNode tree = instance2.getTree();
		root = tree;
		return root;
	}

}
