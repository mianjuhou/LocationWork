package com.jtv.locationwork.tree;

import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

/**
 * 数据库树形结构设计，可以用来做层级关系和文件管理。。。。
 * 
 * 条件1:数据库树形表格设计要满足当前类
 * 
 * 表格不可缺少列名字 1:（int）当前条目唯一标识 2:（int）当前条目父节点标识 3:（int） 层级关系不需要手动设置只需要一个空列就可以
 * 
 * 
 * @author beyound
 *
 */
public class DateBaseTree {

	// 列名 节点id 唯一 int类型
	protected String node_id_column_name = "";

	// 列名 数据库的父id int类型
	protected String parent_id_colum_name = "";

	// 树形根节点的值 方便算出
	protected int root_node_id = 0;

	// 需要访问的数据库表名字
	protected String db_table = "";

	// 列名 int 类型 层级关系(代码中自动算出层级)
	protected String level_column_name = "";

	// 数据库的操作可写
	protected SQLiteDatabase db;

	// 列名 标题
	protected String title_column_name = "";

	// 需要在树结构对象获取到这个数据库列的参数，会在原来的基础上加一个用来存储节点id
	protected String[] parmter_column_name = new String[] { "parmter", "table" };

	// 构建数据
	public static class BuildTree {

		// // 列名 节点id 唯一 int类型
		private String node_id_column_name = "";

		// 列名 数据库的父id int类型
		private String parent_id_colum_name = "";

		// 树形根节点的值
		private int root_node_id = 0;

		// 需要访问的数据库表名字
		private String db_table = "";

		// 列名 int 类型 层级关系(代码中自动算出层级)
		private String level_column_name = "";

		// 数据库的操作可写
		private SQLiteDatabase db;

		// 列名 标题
		private String title_column_name = "";

		// 列名 参数
		private String[] parmter_column_name = new String[] {};

		public String getNode_id_column_name() {
			return node_id_column_name;
		}

		public BuildTree setNode_id_column_name(String node_id_column_name) {
			this.node_id_column_name = node_id_column_name;
			return this;
		}

		public String getParent_id_colum_name() {
			return parent_id_colum_name;
		}

		public void setParent_id_colum_name(String parent_id_colum_name) {
			this.parent_id_colum_name = parent_id_colum_name;
		}

		public int getRoot_node_id() {
			return root_node_id;
		}

		public BuildTree setRoot_node_id(int root_node_id) {
			this.root_node_id = root_node_id;
			return this;
		}

		public String getDb_table() {
			return db_table;
		}

		public BuildTree setDb_table(String db_table) {
			this.db_table = db_table;
			return this;
		}

		public String getLevel_column_name() {
			return level_column_name;
		}

		public BuildTree setLevel_column_name(String level_column_name) {
			this.level_column_name = level_column_name;
			return this;
		}

		public SQLiteDatabase getDb() {
			return db;
		}

		public BuildTree setDb(SQLiteDatabase db) {
			this.db = db;
			return this;
		}

		public String getTitle_column_name() {
			return title_column_name;
		}

		public BuildTree setTitle_column_name(String title_column_name) {
			this.title_column_name = title_column_name;
			return this;
		}

		public String[] getParmter_column_name() {
			return parmter_column_name;
		}

		public BuildTree setParmter_column_name(String[] parmter_column_name) {

			this.parmter_column_name = parmter_column_name;

			return this;
		}

		public DateBaseTree getInstance() {
			return new DateBaseTree(this);
		}

		public DateBaseTree getInstance(SQLiteDatabase db) {
			this.db = db;
			return new DateBaseTree(this);
		}

	}

	// private DateBaseTree(SQLiteDatabase db) {
	// this.db = db;
	// }

	private DateBaseTree() {
	}

	protected DateBaseTree(BuildTree buildTree) {
		if (parmter_column_name == null) {
			parmter_column_name = new String[0];
		}
		if (buildTree != null) {

			db = buildTree.getDb();
			root_node_id = buildTree.getRoot_node_id();
			initLevel = root_node_id;
			level = root_node_id;

			String temp_Table = buildTree.getDb_table();
			if (!TextUtils.isEmpty(temp_Table)) {
				db_table = temp_Table;
			}

			String temp_Level = buildTree.getLevel_column_name();
			if (!TextUtils.isEmpty(temp_Level)) {
				level_column_name = temp_Level;
			}

			String temp_NodeidName = buildTree.getNode_id_column_name();
			if (!TextUtils.isEmpty(temp_NodeidName)) {
				node_id_column_name = temp_NodeidName;
			}

			String temp_ParentNodeid = buildTree.getParent_id_colum_name();
			if (!TextUtils.isEmpty(temp_ParentNodeid)) {
				parent_id_colum_name = temp_ParentNodeid;
			}

			String temp_Title = buildTree.getTitle_column_name();
			if (!TextUtils.isEmpty(temp_Title)) {
				title_column_name = temp_Title;
			}

			String[] temp_Parmter = buildTree.getParmter_column_name();
			if (temp_Parmter != null && temp_Parmter.length > 0) {
				parmter_column_name = temp_Parmter;
			}

		}
	}

	protected Cursor getRootData(int rootid) {
		// 查询数据库的根节点
		Cursor query = db.query(db_table, null, node_id_column_name + "=" + "?", new String[] { rootid + "" }, null,
				null, null);
		return query;
	}

	/**
	 * 获取到父id是同一个等级的cursor
	 * 
	 * @param value
	 *            父id
	 * @return
	 */
	protected Cursor getLevelData(int value) {
		// 查询当前父亲id是同一个等级的数据
		Cursor query = db.query(db_table, null, parent_id_colum_name + "=" + "?", new String[] { value + "" }, null,
				null, null);
		return query;
	}

	/**
	 * 层级分组的数据
	 * 
	 * @return
	 */
	protected Cursor getLevelGroup() {
		Cursor query = db.query(db_table, null, null, null, "" + level_column_name, null, null);
		return query;
	}

	protected boolean isNeedInsertLevel() {
		Cursor query = db.query(db_table, new String[] { level_column_name }, null, null, null, null, null);
		if (query != null) {
			int count = 0;
			while (query.moveToNext() && count < 2) {
				count++;
				int i = query.getInt(query.getColumnIndex(level_column_name));
				if (i > 0) {
					query.close();
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * 获取到下一个层级的数据
	 * 
	 * @param i
	 *            层级值
	 * @return
	 */
	protected Cursor getNextLevel(long i) {
		Cursor query = db.query(db_table, null, level_column_name + "=" + "?", new String[] { i + "" }, null, null,
				null);
		return query;
	}

	/**
	 * 更新某条数据的等级
	 * 
	 * @param contentValues
	 *            等级
	 * @param node_id
	 *            具体修改的id
	 */
	protected void updatelevel(ContentValues contentValues, String node_id) {
		db.update(db_table, contentValues, node_id_column_name + " = ?", new String[] { node_id });
	}

	// 获取根节点
	private ParmterMutableTreeNode getRootTree(int rootid) {

		Cursor query = getRootData(rootid);
		if (query != null) {
			while (query.moveToNext()) {

				// 获取参数
				String mtitle = query.getString(query.getColumnIndex(title_column_name));

				String[] parmterValue = new String[parmter_column_name.length + 1];
				parmterValue[0] = rootid + "";
				for (int i = 0; i < parmter_column_name.length; i++) {

					String index = parmter_column_name[i];

					String mParmter = query.getString(query.getColumnIndex(index));

					parmterValue[i] = mParmter;
				}

				// 创建根节点对象
				ParmterMutableTreeNode root = new ParmterMutableTreeNode(mtitle, true, parmterValue);

				return root;
			}

			query.close();
		}
		return null;
	}

	private int initLevel = 0;
	// 当前插入数据库的等级
	private int level = 0;

	/**
	 * 给数据库的tree结构插入层级关系,这个层级关系代表当前他们所在那一层，并且用来加载整个树
	 * 
	 * @param nodeid_id
	 *            树根节点id这个集合的代表同一级
	 * 
	 * @param levelreset
	 *            恢复默认等级,初始化时候使用
	 */
	public void insertLevelTreeNode(ArrayList<Integer> root_node_id, boolean levelreset) {

		if (levelreset) {// 防止多次添加等级错误
			level = initLevel;
		}

		level++;

		// 存储同一个等级的node_id
		ArrayList<Integer> sameLevel = new ArrayList<Integer>();

		for (int i = 0; i < root_node_id.size(); i++) {
			int value = root_node_id.get(i);

			// 给根节点插入等级
			if (value == this.root_node_id) {
				ContentValues contentValues = new ContentValues();
				contentValues.put(level_column_name, this.root_node_id);
				// db.update(db_table, contentValues, node_id_column_name + " =
				// ?",
				// new String[] { this.root_node_id + "" });
				updatelevel(contentValues, this.root_node_id + "");
			}

			// 查询当前父亲id是同一个等级的数据
			Cursor query = getLevelData(value);

			if (query != null) {

				while (query.moveToNext()) {

					String nodei_id = query.getString(query.getColumnIndex(node_id_column_name));

					// 更新同一个等级的level数据
					ContentValues contentValues = new ContentValues();
					contentValues.put(level_column_name, level);

					updatelevel(contentValues, nodei_id);

					Integer valueOf = Integer.valueOf(nodei_id);
					sameLevel.add(valueOf);
				}

				query.close();

			}

		}

		// 用递归的方式修改同一个等级
		if (sameLevel != null && sameLevel.size() > 0) {
			insertLevelTreeNode(sameLevel, false);
		}

	}

	/**
	 * 获取整个树结构关系 从0层开始遍历一直到n层结束 通过nodeid 和parentid 来确定关系
	 * 
	 * @return 树
	 */
	public ParmterMutableTreeNode getTree() {
		// start = new Date().getTime();
		ArrayList<ParmterMutableTreeNode> mParentPath = new ArrayList<ParmterMutableTreeNode>();

		// 用来减少遍历父路径的次数
		ArrayList<Integer> needDeleteIndex = new ArrayList<Integer>();

		// 当前准备加入树
		ParmterMutableTreeNode joinTree = null;

		ArrayList<Integer> arr = new ArrayList<Integer>();
		arr.add(root_node_id);

		if (isNeedInsertLevel()) {
			// 需要通过层级关系来加载树结构
			insertLevelTreeNode(arr, true);
		}

		// 获取根目录树结构
		ParmterMutableTreeNode rootId = getRootTree(root_node_id);

		// 层级关系分组得到数量
		Cursor mLevelCursor = getLevelGroup();

		// 层级关系总个数
		int levelCount = 0;

		if (mLevelCursor != null) {
			levelCount = mLevelCursor.getCount();
			mLevelCursor.close();
		}

		// 不需要根目录的层级所以＋＋
		root_node_id++;

		// 遍历每个层级关系
		for (long i = root_node_id; i <= levelCount; i++) {// 获取到同一层等级的
			// 获取到下一层
			Cursor query = getNextLevel(i);

			while (query != null && query.moveToNext()) {

				// 标题
				String mTitle = query.getString(query.getColumnIndex(title_column_name));

				// 配置参数
				// String mParmter =
				// query.getString(query.getColumnIndex(parmter_column_name));

				String[] parmterValue = new String[parmter_column_name.length + 1];

				for (int j = 0; j < parmter_column_name.length; j++) {

					String index = parmter_column_name[j];

					String mParmter = query.getString(query.getColumnIndex(index));

					parmterValue[j + 1] = mParmter;
				}

				// 当前节点id
				String nodei_id = query.getString(query.getColumnIndex(node_id_column_name));

				// 父亲id
				String parent_id = query.getString(query.getColumnIndex(parent_id_colum_name));

				parmterValue[0] = nodei_id;

				// 创建加入树
				joinTree = new ParmterMutableTreeNode(mTitle, true, parmterValue);

				if (joinTree != null && i == root_node_id) {// 代表当前只有根节点没有其他节点

					rootId.add(joinTree);
					mParentPath.add(joinTree);

				} // else if (joinTree != null && old != null) {
				else {
					boolean isAdd = false;

					for (int j = 0; j < mParentPath.size(); j++) {
						ParmterMutableTreeNode childAt = mParentPath.get(j);
						// 找到父亲id并且校验是否是父亲
						if (childAt.getParmter()[0].equals(parent_id + "")) {

							childAt.add(joinTree);// 找到父亲节点
							isAdd = true;

							if (!needDeleteIndex.contains(j)) {
								needDeleteIndex.add(j);
							}

							break;
						}
					}

					if (isAdd) {
						mParentPath.add(joinTree);
					}

					// TreeNode parent = old.getParent();// 获取到上一层的父类
					//
					// int childCount = parent.getChildCount();//
					// 获取到当前同等级的所有子孩子个数
					//
					// for (int j = 0; j < childCount; j++) {// 寻找父亲3 层没问题
					// 第四层有问题
					//
					// ParmterMutableTreeNode childAt = (ParmterMutableTreeNode)
					// parent.getChildAt(j);
					//
					//
					// // 找到父亲id并且校验是否是父亲
					// if (childAt.getParmter()[0].equals(parent_id + "")) {
					//
					// childAt.add(joinTree);// 找到父亲节点
					//
					// break;
					// }
					//
					// }

				}

			}

			query.close();
			// 简单优化 ,去除不需要的遍历
			for (int j = 0; j < needDeleteIndex.size(); j++) {
				Integer integer = needDeleteIndex.get(j);
				// String string = mParentPath.get(integer).toString();
				// System.out.println("移除数据－－" + string);
				mParentPath.remove(integer);
			}
			needDeleteIndex.clear();
			// System.out.println("移除数据＝＝＝＝＝＝＝");
		}
		// end = new Date().getTime();
		// System.out.println("耗时－－－" + (end - start) / 60);
		// 整个树形结构加载完毕
		return rootId;

	}

}
