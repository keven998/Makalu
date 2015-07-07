package com.xuejian.client.lxp.module.dest.CommonViewUnit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.bean.PoiDetailBean;
import com.xuejian.client.lxp.common.imageloader.UILUtils;

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
            convertView = inflater.inflate(R.layout.item_plan_day_detil, null);
            holder = new ViewHolder();
            holder.tv_poi_title = (TextView) convertView.findViewById(R.id.tv_poi_title);
            holder.tv_poi_level = (TextView) convertView.findViewById(R.id.tv_poi_level);
            holder.tv_poi_time = (TextView) convertView.findViewById(R.id.tv_poi_time);
            holder.iv_poi = (ImageView) convertView.findViewById(R.id.iv_poi_img);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        PoiDetailBean poi = mPOIs.get(position);
        ImageLoader.getInstance().displayImage(poi.images.get(0).url, holder.iv_poi, UILUtils.getDefaultOption());
        holder.tv_poi_time.setText(String.format("建议游玩 %s", poi.timeCostDesc));
        holder.tv_poi_level.setText(String.valueOf(poi.commentCnt));
        holder.tv_poi_title.setText(poi.zhName);
        return convertView;
    }

    private class ViewHolder {
        public TextView tv_poi_title;
        public TextView tv_poi_time;
        public TextView tv_poi_level;
        public ImageView iv_poi;
    }
}
