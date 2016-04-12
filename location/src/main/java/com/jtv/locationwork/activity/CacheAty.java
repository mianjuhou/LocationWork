package com.jtv.locationwork.activity;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import com.jtv.hrb.locationwork.R;
import com.jtv.locationwork.adapter.BaseListAdapter;
import com.jtv.locationwork.adapter.CommonViewHolder;
import com.jtv.locationwork.util.Cache.Entry;
import com.jtv.locationwork.util.DiskBasedCache;
import com.jtv.locationwork.util.DiskCacheUtil;
import com.plutus.libraryui.dialog.LoadDataDialog;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * 查看人脸考勤
 *
 */
public class CacheAty extends BaseListAty implements OnClickListener {
	
	
	private ArrayList<WorkFacebean> list = null;
	
	private CacheAdapter mAdapter;
	
	private LoadDataDialog loadDataDialog;
	
	private String cache_key = "";//要查询的缓存key

	protected void onCreatInit(Bundle savedInstanceState) {
		super.onCreatInit(savedInstanceState);
		loadDataDialog = new LoadDataDialog(this);
		loadDataDialog.open();
		list = new ArrayList<WorkFacebean>();
		mAdapter = new CacheAdapter(this, list);

		listView.setAdapter(mAdapter);
		setBackOnClickFinish();
		getHeaderTitleTv().setText("查看");

		getIntentParmter();

		new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {

				DiskBasedCache cache = DiskCacheUtil.getCache(CacheAty.this);

				Entry entry = cache.get(cache_key);

				if (entry != null && !entry.isExpired()) {

					Map<String, String> responseHeadersentry = entry.responseHeaders;

					if (responseHeadersentry != null) {

						Set<String> keySet = responseHeadersentry.keySet();

						for (String key : keySet) {
							list.add(new WorkFacebean(key, ""));
						}

					}
				}

				return null;
			}

			protected void onPostExecute(Void result) {
				mAdapter.refershAdapter(list);
				loadDataDialog.close();
			};
		}.execute();
	}

	private void getIntentParmter() {
		cache_key = getIntent().getStringExtra("key");
	}

	@Override
	public void onClick(View v) {
	}

}

class WorkFacebean {
	private String name;
	private String type;

	public WorkFacebean(String name, String type) {
		this.name = name;
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}


class CacheAdapter extends BaseListAdapter<WorkFacebean>{

	public CacheAdapter(Context con, ArrayList<WorkFacebean> list) {
		super(con, list);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = View.inflate(con, R.layout.location_jtv_list_adapter_queryface, null);
		}
		TextView tv_name = CommonViewHolder.get(convertView, R.id.tv_name);
		TextView tv_type = CommonViewHolder.get(convertView, R.id.tv_type);
		WorkFacebean work = (WorkFacebean) list.get(position);
		String name = work.getName();
		String type = work.getType();
		tv_name.setText(name);
		tv_type.setText(type);
		return convertView;
	}
	
}
