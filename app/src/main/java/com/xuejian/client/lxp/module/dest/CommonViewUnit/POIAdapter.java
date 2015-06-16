package com.xuejian.client.lxp.module.dest.CommonViewUnit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.bean.PoiDetailBean;

import java.util.ArrayList;

/**
 * Created by luoyong on 15/6/12.
 */
public class POIAdapter extends BaseAdapter {
    private ArrayList<PoiDetailBean> mPOIs;
    private LayoutInflater inflater;

    public POIAdapter(Context context, ArrayList<PoiDetailBean> datas) {
        mPOIs = datas;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mPOIs.size();
    }

    @Override
    public Object getItem(int position) {
        return mPOIs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_plan_day_schedule_summary, null);
            holder = new ViewHolder();
            holder.poiNameTextView = (TextView) convertView.findViewById(R.id.tv_schedule_summary);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        PoiDetailBean poi = mPOIs.get(position);
        holder.poiNameTextView.setText(poi.zhName);
        return convertView;
    }

    private class ViewHolder {
        public TextView poiNameTextView;
    }
}
