package com.jtv.locationwork.util;


import android.support.v4.util.SparseArrayCompat;
import android.view.View;

public class CommonViewHolder {

	public static <T extends View> T get(View view, int id) {
        SparseArrayCompat<View> viewHolder = (SparseArrayCompat<View>) view.getTag();//类似hashmap优化
        if (viewHolder == null) {
            viewHolder = new SparseArrayCompat<View>();
            view.setTag(viewHolder);
        }
        View childView = viewHolder.get(id);
        if (childView == null) {
            childView = view.findViewById(id);
            viewHolder.put(id, childView);
        }
        return (T) childView;
    }
}
