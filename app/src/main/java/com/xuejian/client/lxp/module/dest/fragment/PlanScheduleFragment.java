package com.xuejian.client.lxp.module.dest.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.bean.PoiDetailBean;
import com.xuejian.client.lxp.bean.StrategyBean;
import com.xuejian.client.lxp.module.dest.DayAgendaActivity;
import com.xuejian.client.lxp.module.dest.StrategyActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by luoyong on 15/6/12.
 */
public class PlanScheduleFragment extends Fragment {

    private StrategyBean strategy;
    private ArrayList<ArrayList<PoiDetailBean>> routeDayMap;
    private ListView mListView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        strategy = getStrategy();
        if (strategy!=null)resizeData(strategy.itinerary,strategy);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_plan_schedule_summary, container, false);
        mListView = (ListView) rootView.findViewById(R.id.listview_plan_schedule);
        mListView.setAdapter(new DSAdapter(routeDayMap));
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), DayAgendaActivity.class);
                intent.putExtra("strategy", strategy);
                intent.putExtra("current_day", position);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
            }
        });
        return rootView;
    }

    private StrategyBean getStrategy() {
        return ((StrategyActivity) getActivity()).getStrategy();
    }

    private void resizeData(ArrayList<StrategyBean.IndexPoi> itinerary,StrategyBean str) {
        StrategyBean strategyBean = getStrategy();
        routeDayMap = new ArrayList<ArrayList<PoiDetailBean>>();
        for (int i = 0; i < strategyBean.itineraryDays; i++) {
            routeDayMap.add(new ArrayList<PoiDetailBean>());
        }
//strategyBean.localities
        for (StrategyBean.IndexPoi indexPoi : itinerary) {
            if(routeDayMap.size()>indexPoi.dayIndex){
                routeDayMap.get(indexPoi.dayIndex).add(indexPoi.poi);
            }
        }

    }

    class DSAdapter extends BaseAdapter {
        private ArrayList<ArrayList<PoiDetailBean>> routeDayMap;

        public DSAdapter(ArrayList<ArrayList<PoiDetailBean>> data) {
            routeDayMap = data;
        }

        @Override
        public int getCount() {
            return routeDayMap.size();
        }

        @Override
        public Object getItem(int position) {
            return routeDayMap.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater().inflate(R.layout.item_plan_day_schedule_summary, null);
                holder = new ViewHolder();
                holder.summaryTextView = (TextView) convertView.findViewById(R.id.tv_schedule_summary);
                holder.tv_day_index = (TextView) convertView.findViewById(R.id.tv_day_index);
                holder.tv_schedule_title= (TextView) convertView.findViewById(R.id.tv_schedule_title);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            List<PoiDetailBean> poiList = routeDayMap.get(position);
            holder.tv_day_index.setText(String.valueOf(position+1));
           // holder.tv_schedule_title.setText(routeDayMap.s);
            int count = poiList.size();
            String desc ="";
            PoiDetailBean pdb;
            for (int i = 0; i < count; ++i) {
                pdb = poiList.get(i);
                if (i == 0) {
                    desc= String.format("%s", pdb.zhName);
                } else {
                    desc = String.format("%s → %s", desc, pdb.zhName);
                }
            }
            holder.summaryTextView.setText(desc);
            return convertView;
        }
    }

    private class ViewHolder {
        public TextView summaryTextView;
        public TextView tv_day_index;
        public TextView tv_schedule_title;
    }

}
