package com.xuejian.client.lxp.module.dest.adapter;

import android.app.Activity;
import android.content.Context;
import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.xuejian.client.lxp.R;

import java.util.List;

/**
 * Created by Rjm on 2014/12/1.
 */
public class StringSpinnerAdapter implements SpinnerAdapter {
    private List<String> mStrList;
    private Context mContext;
    public StringSpinnerAdapter(Context context,List<String> strList){
        mStrList = strList;
        mContext = context;
    }
    @Override
    public View getDropDownView(int position, View view, ViewGroup parent) {
        if (view == null || !view.getTag().toString().equals("DROPDOWN")) {
            view = ((Activity)mContext).getLayoutInflater().inflate(R.layout.spinner_item_dropdown, parent, false);
            view.setTag("DROPDOWN");
        }
        System.out.print("hhhh  "+getItem(position).toString());
            TextView textView= (TextView) view.findViewById(R.id.tv_title);
        textView .setText(getItem(position).toString());
        return view;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        if (view == null || !view.getTag().toString().equals("NON_DROPDOWN")) {
            view = ((Activity)mContext).getLayoutInflater().inflate(R.layout.spinner_item_actionbar, parent, false);
            view.setTag("NON_DROPDOWN");
        }
        ((TextView) view).setText(getItem(position).toString());
        return view;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver dataSetObserver) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {

    }

    @Override
    public int getCount() {
        return mStrList.size();
    }

    @Override
    public Object getItem(int i) {
        return mStrList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public int getItemViewType(int i) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }
}
