package com.jtv.locationwork.activity;

import java.util.ArrayList;

import com.jtv.base.activity.SwipePulltoRefershAty;
import com.jtv.dbentity.dao.BaseDaoImpl;
import com.jtv.hrb.locationwork.R;
import com.jtv.locationwork.adapter.CheckCacheAdapter;
import com.jtv.locationwork.dao.DBFactory;
import com.jtv.locationwork.entity.CheckEntity;
import com.jtv.locationwork.util.DateUtil;

import android.os.Bundle;
import android.view.View;

public class CheckCacheAty extends SwipePulltoRefershAty {
	private CheckCacheAdapter adapter;
	private ArrayList<CheckEntity> list = new ArrayList<CheckEntity>();
	private BaseDaoImpl<CheckEntity> checkQuestionDao;

	@Override
	public void onClick(View v) {

	}

	@Override
	public void onRefresh() {
		long currTime = DateUtil.getCurrTime();
		long Past = currTime - 1000 * 60 * 60 * 24;// 24小时前的数据
		list = (ArrayList<CheckEntity>) checkQuestionDao.find(null, "time > ?", new String[] { Past + "" }, null, null,
				"time desc", null);// 倒序排
		if (adapter != null) {
			adapter.refershAdapter(list);
		} else {
			adapter = new CheckCacheAdapter(this, list);
			mList.setAdapter(adapter);
		}
		mSwipeRefreshLayout.setRefreshing(false);
	}

	@Override
	protected void init(Bundle savedInstanceState) {
		setHeaderTitleText(getString(R.string.title_question_line));
		setBackOnClickFinish();

		checkQuestionDao = DBFactory.getCheckQuestionDao(this);

		onRefresh();
	}

	@Override
	protected View addHeadView() {
		return null;
	}

}
