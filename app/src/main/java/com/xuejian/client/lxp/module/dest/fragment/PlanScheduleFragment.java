package com.xuejian.client.lxp.module.dest.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.bean.IndexPoi;
import com.xuejian.client.lxp.bean.PoiDetailBean;
import com.xuejian.client.lxp.bean.StrategyBean;
import com.xuejian.client.lxp.module.dest.DayAgendaActivity;
import com.xuejian.client.lxp.module.dest.StrategyActivity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created by luoyong on 15/6/12.
 */
public class PlanScheduleFragment extends Fragment {

    private StrategyBean strategy;
    private ArrayList<ArrayList<PoiDetailBean>> routeDayMap;
    private ListView mListView;

    private PopupWindow popupWindow;
    private LayoutInflater inflater;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        strategy = getStrategy();
        if (strategy != null) resizeData(strategy.itinerary, strategy);
    }

    private DSAdapter mAdapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_plan_schedule_summary, container, false);
        mListView = (ListView) rootView.findViewById(R.id.listview_plan_schedule);
        mAdapter = new DSAdapter(routeDayMap);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Intent intent = new Intent(getActivity(), DayAgendaActivity.class);
                intent.putExtra("strategy", strategy);
                intent.putExtra("current_day", position);
                startActivityForResult(intent,111);
                getActivity().overridePendingTransition(0,0);
            }
        });
        return rootView;
    }

    public void addDay(){
        if (mAdapter!=null){
            mAdapter.addDay();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == -1){
            ArrayList<PoiDetailBean> list = data.getParcelableArrayListExtra("poiList");
            int index = data.getIntExtra("dayIndex",0);
            if (list!=null&&list.size()>0)routeDayMap.set(index,list);
            mAdapter.notifyDataSetChanged();
        }
        super.onActivityResult(requestCode, resultCode, data);

    }

    private StrategyBean getStrategy() {
        return ((StrategyActivity) getActivity()).getStrategy();
    }

    private void resizeData(ArrayList<IndexPoi> itinerary, StrategyBean str) {
        StrategyBean strategyBean = getStrategy();
        routeDayMap = new ArrayList<ArrayList<PoiDetailBean>>();
        for (int i = 0; i < strategyBean.itineraryDays; i++) {
            routeDayMap.add(new ArrayList<PoiDetailBean>());
        }
        for (IndexPoi indexPoi : itinerary) {
            if (routeDayMap.size() > indexPoi.dayIndex) {
                routeDayMap.get(indexPoi.dayIndex).add(indexPoi.poi);
            }
        }

    }

    private class DSAdapter extends BaseAdapter {
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

        public void addDay(){
            routeDayMap.add(new ArrayList<PoiDetailBean>());
            notifyDataSetChanged();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater().inflate(R.layout.item_plan_day_schedule_summary, null);
                holder = new ViewHolder();
                holder.summaryTextView = (TextView) convertView.findViewById(R.id.tv_schedule_summary);
                holder.tv_day_index = (TextView) convertView.findViewById(R.id.tv_day_index);
                holder.tv_schedule_title = (TextView) convertView.findViewById(R.id.tv_schedule_title);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            List<PoiDetailBean> poiList = routeDayMap.get(position);

            SpannableString planStr = new SpannableString("Day");
            planStr.setSpan(new AbsoluteSizeSpan(11, true), 0, planStr.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            SpannableStringBuilder spb = new SpannableStringBuilder();
            if (position < 9) {
                spb.append(String.format("0%s.\n", (position + 1))).append(planStr);
            } else {
                spb.append(String.format("%s.\n", (position + 1))).append(planStr);
            }
            holder.tv_day_index.setText(spb);

            int count = poiList.size();
            String desc = "";
            String descTitle = "";
            HashSet<String> set = new HashSet<String>();
            PoiDetailBean pdb;
            for (int i = 0; i < count; ++i) {
                pdb = poiList.get(i);
                if (i == 0) {
                    desc = String.format("%s", pdb.zhName);
                } else {
                    desc = String.format("%s → %s", desc, pdb.zhName);
                }
                if (pdb.locality != null) {
                    set.add(pdb.locality.zhName);
                }
            }
            for (String desName : set) {
                if (descTitle.equals("")) {
                    descTitle = desName;
                } else {
                    descTitle = String.format("%s > %s", descTitle, desName);
                }
            }
            if (TextUtils.isEmpty(desc)) {
                holder.tv_schedule_title.setText("无安排");
                holder.summaryTextView.setText("");
            } else {
                holder.summaryTextView.setText(desc);
                holder.tv_schedule_title.setText(descTitle);
            }
            return convertView;
        }
    }

    private class ViewHolder {
        public TextView summaryTextView;
        public TextView tv_day_index;
        public TextView tv_schedule_title;
    }

}
