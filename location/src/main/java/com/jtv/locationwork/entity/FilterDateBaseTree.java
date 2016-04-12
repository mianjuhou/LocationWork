package com.jtv.locationwork.entity;

import com.jtv.locationwork.tree.DateBaseTree;

import android.content.ContentValues;
import android.database.Cursor;

/**
 * 从数据库中加载树更具站点id加载不同的树数据
 * @author beyound
 *
 */
public class FilterDateBaseTree extends DateBaseTree {
	private String siteid;

	public FilterDateBaseTree(BuildTree buildTree, String filterSiteid) {
		super(buildTree);
		this.siteid = filterSiteid;
	}

	@Override
	protected Cursor getRootData(int rootid) {
		// 查询数据库的根节点
		Cursor query = db.query(db_table, null, node_id_column_name + "=" + "? ",
				new String[] { rootid + "" }, null, null, null);
		return query;

	}

	@Override
	protected Cursor getLevelData(int value) {
		// 查询当前父亲id是同一个等级的数据
		Cursor query = db.query(db_table, null, parent_id_colum_name + "=" + "? and siteid  = ?",
				new String[] { value + "" ,siteid}, null, null, null);
		return query;
	}

	@Override
	protected Cursor getLevelGroup() {
		Cursor query = db.query(db_table, null, "siteid  = ? ", new String[] { siteid }, "" + level_column_name, null,
				null);
		return query;
	}

	@Override
	protected Cursor getNextLevel(long i) {
		Cursor query = db.query(db_table, null, level_column_name + "=" + "?  and siteid  = ?",
				new String[] { i + "", siteid }, null, null, null);
		return query;
	}

	@Override
	protected void updatelevel(ContentValues contentValues, String node_id) {
		db.update(db_table, contentValues, node_id_column_name + " = ? and  siteid =?", new String[] { node_id,siteid });
	}
	
	@Override
	protected boolean isNeedInsertLevel() {
		Cursor query = db.query(db_table, new String[] { level_column_name }, "siteid  = ?", new String[]{siteid}, null, null, null);
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
}
