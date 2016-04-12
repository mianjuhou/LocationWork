package com.jtv.hrb.locationwork;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.HashMap;

/**
 * Created by fangdean on 2016/3/15.
 */
public class ViewDelegate {
    private HashMap<Integer, View> views = new HashMap<Integer, View>();
    private LinearLayout contentView;
    private FrameLayout containerView;

    public ViewDelegate(Context context, int layout) {
        contentView = (LinearLayout) View.inflate(context, R.layout.layout_viewdelegate, null);
        containerView = (FrameLayout) contentView.findViewById(R.id.parent_container);
        View detailContentView=View.inflate(context, layout, null);
        containerView.addView(detailContentView);
    }

    public ViewDelegate(LayoutInflater inflater, int layout) {
        contentView= (LinearLayout) inflater.inflate(R.layout.layout_viewdelegate,null);
        containerView = (FrameLayout) contentView.findViewById(R.id.parent_container);
        View detailContentView=inflater.inflate(layout,null);
        containerView.addView(detailContentView);
    }
    
    public View getContentView() {
        return contentView;
    }
    
    public void showTitle(boolean isShow){
    	if(isShow){
    		getView(R.id.title_container).setVisibility(View.VISIBLE);
    	}else{
    		getView(R.id.title_container).setVisibility(View.GONE);
    	}
    }
    
    public void setTitle(String title){
        getTextView(R.id.tv_title).setText(title);
    }
    
    public TextView getTextView(int resId) {
        return (TextView)getView(resId);
    }

    public EditText getEditText(int resId) {
        return (EditText)getView(resId);
    }

    public Button getButton(int resId) {
        return (Button)getView(resId);
    }

    public ListView getListView(int resId){
        return (ListView)getView(resId);
    }

    public View getView(int resId){
        View view=views.get(resId);
        if(view==null){
            view = contentView.findViewById(resId);
            views.put(resId,view);
        }
        return view;
    }
}