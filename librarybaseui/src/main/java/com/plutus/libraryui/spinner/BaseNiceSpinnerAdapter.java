package com.plutus.libraryui.spinner;

import com.plutus.libraryui.R;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * @author angelo.marchesin
 */

@SuppressLint("NewApi")
@SuppressWarnings("unused")
public abstract class BaseNiceSpinnerAdapter extends BaseAdapter {
	protected Context mContext;
	protected int mSelectedIndex;

	public BaseNiceSpinnerAdapter(Context context) {
		mContext = context;
	}

	@Override
	@SuppressWarnings("unchecked")
	public View getView(int position, View convertView, ViewGroup parent) {
		TextView textView;

		if (convertView == null) {
			convertView = View.inflate(mContext, R.layout.spinner_list_item, null);
			textView = (TextView) convertView.findViewById(R.id.tv_tinted_spinner);

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
				textView.setBackground(ContextCompat.getDrawable(mContext, R.drawable.spinner_selector));
			}

			convertView.setTag(new ViewHolder(textView));
		} else {
			textView = ((ViewHolder) convertView.getTag()).textView;
		}
		textView.setTextSize(17);
		textView.setText(getName(position).toString());

		return convertView;
	}

	public int getSelectedIndex() {
		return mSelectedIndex;
	}

	public void notifyItemSelected(int index) {
		mSelectedIndex = index;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public abstract Object getItem(int position);

	public abstract Object getName(int position);
	
	@Override
	public abstract int getCount();

	public abstract Object getItemInDataset(int position);

	protected static class ViewHolder {

		public TextView textView;

		public ViewHolder(TextView textView) {
			this.textView = textView;
		}
	}
}
