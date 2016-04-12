package com.jtv.locationwork.activity;

import java.util.Date;
import java.util.List;

import com.jtv.base.activity.BaseAty;
import com.jtv.hrb.locationwork.GlobalApplication;
import com.jtv.hrb.locationwork.R;
import com.jtv.locationwork.imp.PhotoTree;
import com.jtv.locationwork.listener.Trees;
import com.jtv.locationwork.tree.ParmterMutableTreeNode;
import com.jtv.locationwork.util.Constants;
import com.jtv.locationwork.util.CoustomKey;
import com.jtv.locationwork.util.DateUtil;
import com.jtv.locationwork.util.SpUtiles;
import com.jtv.locationwork.util.TextUtil;
import com.plutus.libraryui.dialog.DiaLogUtil;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

//弹出一个工单树，目前有两种一种是最近工单树，列外一种是
public class PhotoTreeAty extends BaseAty implements DialogInterface.OnClickListener {

	private ParmterMutableTreeNode[] mNodeTreeData;// 树的更节点

	private String mtype;// 当前的类型

	private String wonum;// 当前的工单号，如果传一个工单号过来代表是工区下的工单，不传代表最近工单

	private String type = "";// 自动选中type条目

	private AlertDialog showItemDiaLog;

	private String isimportant;

	@Override
	public void onClick(View v) {

	}

	public void getIntentParmter() {
		wonum = getIntent().getStringExtra(CoustomKey.WONUM);

		type = getIntent().getStringExtra("auto_type");

		isimportant = getIntent().getStringExtra("isimportant");// 是否是重点行车
	}

	@Override
	protected void onCreatInit(Bundle savedInstanceState) {
		requestWindowFeature(1);
		getIntentParmter();
		showDialogForLinkPhoto();
	}

	protected void showDialogForLinkPhoto() {
		Trees tableTree = null;
		String duanhao = GlobalApplication.siteid;
		boolean isAuto;

		if (!TextUtil.isEmpty(type)) {
			isAuto = true;
		} else {
			isAuto = false;
		}

		if (TextUtil.isEmpty(wonum)) {
			// 获取工单看是否选择
			try {
				org.json.JSONObject nearWonum = SpUtiles.NearWorkListInfo.getNearWonum();
				String start = nearWonum.getString(CoustomKey.NEAR_START_TIME);
				String end = nearWonum.getString(CoustomKey.NEAR_END_TIME);
				type = nearWonum.optString("type");

				long mStartTime = DateUtil.getTimesFotString(start);
				long mEndTime = DateUtil.getTimesFotString(end);
				long curr = new Date().getTime();

				boolean scrope = DateUtil.isScrope(mStartTime, mEndTime, curr, 1000 * 60 * 20);// 允许这个时间段的前20分钟范围

				if (scrope) {// 当前的工单是在这个范围

					isAuto = true;
					// woType = nearWonum.optString(CoustomKey.WONUM_TYPE);

				} else {// 当前工单不属于这个阶段的
					isAuto = false;
				}

			} catch (Exception e) {
				e.printStackTrace();
				// return;
			}
		}

		tableTree = PhotoTree.getInstance();

		if (tableTree == null) {
			finish();
		}

		ParmterMutableTreeNode mCreatTree = tableTree.creatTree(duanhao);

		List<ParmterMutableTreeNode> list = mCreatTree.getChilds();

		specailWonumAction(list);
		specailWonumAction1(list);

		String[] mNodeTitle = null;

		mNodeTitle = new String[list.size()];
		mNodeTreeData = new ParmterMutableTreeNode[list.size()];

		int index = 0;

		for (int j = 0; j < list.size(); j++) {
			ParmterMutableTreeNode parmterMutableTreeNode2 = list.get(j);

			String title = (String) parmterMutableTreeNode2.getUserObject();
			String mtype = (String) parmterMutableTreeNode2.getParmter()[1];

			if ("4".equals(type) || "6".equals(type) && !isAuto) {// 代表这个单子是大施工或者安控台行车10特殊处理

				if ("4".equals(mtype) || "6".equals(mtype)) {
					if (index > 2) {
						continue;
					}
					mNodeTreeData[index] = parmterMutableTreeNode2;
					mNodeTitle[index] = title;
					index++;
				}

			} else {
				mNodeTreeData[j] = parmterMutableTreeNode2;
				mNodeTitle[j] = title;
			}

			if (isAuto && !TextUtil.isEmpty(type)) {// 当工单属于当前时间段自动选择
				// 自动选择第一层
				boolean contains = type.equals(mtype);
				if (contains) {
					onClick(null, j);
					return;
				}
			}

		}
		if (showItemDiaLog != null && showItemDiaLog.isShowing()) {
			showItemDiaLog.dismiss();
		}
		showItemDiaLog = DiaLogUtil.showItemDiaLog(this, getString(R.string.dis_position), mNodeTitle, this);
	}

	// 处理安控台和施工台
	private void specailWonumAction1(List<ParmterMutableTreeNode> list) {

		if ("4".equals(type) || "6".equals(type)) {// 只需要1个
			if (list != null) {
				ParmterMutableTreeNode four = null;
				ParmterMutableTreeNode six = null;
				for (int i = 0; i < list.size(); i++) {
					ParmterMutableTreeNode parmterMutableTreeNode = list.get(i);
					String mtype = (String) parmterMutableTreeNode.getParmter()[1];
					if ("4".equals(mtype)) {// 行车台6确认
						four = parmterMutableTreeNode;
						if (four != null && six != null) {
							break;
						}
					} else if ("6".equals(mtype)) {// 安控台行车6确认
						six = parmterMutableTreeNode;
						if (four != null && six != null) {
							break;
						}
					}
				}

				if (four != null && six != null) {
					List<ParmterMutableTreeNode> childs = six.getChilds();
					if (childs != null)
						for (ParmterMutableTreeNode parmterMutableTreeNode : childs) {
							four.add(parmterMutableTreeNode);
						}
				}

				list.clear();
				list.add(four);
			}

		}

	}

	/**
	 * 【行车台6确认】工单录入页面增加字段【重点行车】,下拉选择值：是/否。当选择值为‘是’时，手机端增加【安控台行车6确认】的卡控项点。
	 * 
	 * @param list
	 */
	private void specailWonumAction(List<ParmterMutableTreeNode> list) {
		if ("1".equals(isimportant)) {// S01115 重点行车
			if (list != null) {
				ParmterMutableTreeNode isimportantNode = null;
				ParmterMutableTreeNode xingchetai6 = null;
				for (int i = 0; i < list.size(); i++) {
					ParmterMutableTreeNode parmterMutableTreeNode = list.get(i);
					String mtype = (String) parmterMutableTreeNode.getParmter()[1];
					if ("7".equals(mtype)) {// 行车台6确认
						xingchetai6 = parmterMutableTreeNode;
						if (isimportantNode != null && xingchetai6 != null) {
							break;
						}
					} else if ("8".equals(mtype)) {// 安控台行车6确认
						isimportantNode = parmterMutableTreeNode;
						if (isimportantNode != null && xingchetai6 != null) {
							break;
						}
					}
				}
				if (isimportantNode != null && xingchetai6 != null) {
					List<ParmterMutableTreeNode> childs = isimportantNode.getChilds();
					if (childs != null)
						for (ParmterMutableTreeNode parmterMutableTreeNode : childs) {
							xingchetai6.add(parmterMutableTreeNode);
						}
				}
			}
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		finish();
		return true;
	}

	private String filterID;
	private String mTable;

	private Object[] parmter;

	// dialog 的点击事件
	@Override
	public void onClick(DialogInterface dialog, int which) {
		if (dialog != null) {
			dialog.dismiss();
		}

		// 0 是否 1是是
		ParmterMutableTreeNode first = mNodeTreeData[which];
		ParmterMutableTreeNode isRoot = (ParmterMutableTreeNode) first.getParent();
		boolean root = isRoot.isRoot();
		if (root) {// 代表跟节点
			Object[] parmter = first.getParmter();
			mtype = (String) parmter[1];
			// mRootID = (String) parmter[1];
			mTable = (String) parmter[parmter.length - 1];
			// Enumeration<ParmterMutableTreeNode> children = first.children();// 获取到下一级数据
			List<ParmterMutableTreeNode> list = first.getChilds();
			String[] title = new String[list.size()];
			mNodeTreeData = new ParmterMutableTreeNode[list.size()];// 下一季

			for (int j = 0; j < list.size(); j++) {
				ParmterMutableTreeNode parmterMutableTreeNode2 = list.get(j);
				String name = (String) parmterMutableTreeNode2.getUserObject();
				title[j] = name;
				mNodeTreeData[j] = parmterMutableTreeNode2;
			}

			if (showItemDiaLog != null && showItemDiaLog.isShowing()) {
				showItemDiaLog.dismiss();
			}
			showItemDiaLog = DiaLogUtil.showItemDiaLog(this, "位置", title, this);
			setOndisMissListener(showItemDiaLog);

		} else {
			boolean isHas = false;
			List<ParmterMutableTreeNode> list = first.getChilds();
			String[] title = new String[list.size()];
			mNodeTreeData = new ParmterMutableTreeNode[list.size()];

			if (list != null)
				for (int j = 0; j < list.size(); j++) {
					ParmterMutableTreeNode parmterMutableTreeNode2 = list.get(j);
					String name = (String) parmterMutableTreeNode2.getUserObject();
					title[j] = name;
					mNodeTreeData[j] = parmterMutableTreeNode2;
					boolean leaf = parmterMutableTreeNode2.isLeaf();

					if (!leaf) {// 不是树枝节点，代表下面还有一层
						isHas = true;
					}
				}

			if (isHas) {
				if (showItemDiaLog != null && showItemDiaLog.isShowing()) {
					showItemDiaLog.dismiss();
				}
				showItemDiaLog = DiaLogUtil.showItemDiaLog(this, "位置", title, this);
				setOndisMissListener(showItemDiaLog);
			} else {// 打开界面了
				startActivity(first);
			}
		}

	}

	private void setOndisMissListener(AlertDialog showItemDiaLog) {
		this.showItemDiaLog.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface dialog) {
				if (PhotoTreeAty.this.showItemDiaLog != null && PhotoTreeAty.this.showItemDiaLog.isShowing()) {
					return;
				}
				PhotoTreeAty.this.finish();
			}
		});
	}

	private void startActivity(ParmterMutableTreeNode second) {
		parmter = second.getParmter();
		filterID = (String) parmter[0];
		Intent intent2 = new Intent(this, WoNumTakePhoto.class);

		// 当为空的时候代表是个人工单
		if (TextUtil.isEmpty(wonum)) {
			wonum = SpUtiles.NearWorkListInfo.getWonum();
		}

		intent2.putExtra(Constants.INTENT_PARMTER, mtype);
		intent2.putExtra(Constants.INTENT_TABLE, mTable);
		intent2.putExtra(Constants.INTENT_WONUM, wonum);
		intent2.putExtra(Constants.INTENT_SECONDID, filterID);
		intent2.putExtra(Constants.INTENT_FLAG_PATMTER, second);

		if (!TextUtil.isEmpty(wonum)) {
			startActivity(intent2);
		}
		finish();
	}
}
