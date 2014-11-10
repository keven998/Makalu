/*
 * Copyright (c) 2014. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.aizou.core.base;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.BaseAdapter;

import java.util.List;
import java.util.Map;

/**
 * Created by xyl on 2014/9/27.
 */
public abstract class CommBaseAdapter extends BaseAdapter {
    protected Context context;
    protected List<Map<String, String>> data;
    protected LayoutInflater inflater;

    protected CommBaseAdapter(Context context, List<Map<String, String>> data) {
        this.data = data;
        this.context = context;
        inflater = LayoutInflater.from(this.context);
    }

    @Override

    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

}
