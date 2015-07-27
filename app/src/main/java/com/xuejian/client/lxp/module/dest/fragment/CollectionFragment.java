package com.xuejian.client.lxp.module.dest.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.bean.LocBean;
import com.xuejian.client.lxp.bean.StrategyBean;
import com.xuejian.client.lxp.module.dest.PoiSaveActivity;
import com.xuejian.client.lxp.module.dest.StrategyActivity;

import java.util.ArrayList;

/**
 * Created by yibiao.qin on 2015/7/4.
 */
public class CollectionFragment extends Fragment {

    private ListView mListView;
    private StrategyBean strategy;
    private ArrayList<LocBean> destinations;
    private int FOR_FOOD_COLLECTION = 201;
    private int FOR_SHOP_COLLECTION = 202;
    private StrategySaveAdapter adapter;
    private boolean isOwner;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        strategy = getStrategy();
        destinations = getDestinations();
        isOwner = getArguments().getBoolean("isOwner");
        System.out.println("isOwner "+isOwner);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_plan_schedule_summary, container, false);
        mListView = (ListView) rootView.findViewById(R.id.listview_plan_schedule);
        adapter = new StrategySaveAdapter();
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    Intent foodIntent = new Intent(getActivity(), PoiSaveActivity.class);
                    foodIntent.putExtra("title", "美食");
                    foodIntent.putParcelableArrayListExtra("destinations", destinations);
                    foodIntent.putExtra("strategy", strategy);
                    foodIntent.putExtra("isOwner",isOwner);
                    getActivity().startActivityForResult(foodIntent, FOR_FOOD_COLLECTION);
                } else if (i == 1) {
                    Intent shopIntent = new Intent(getActivity(), PoiSaveActivity.class);
                    shopIntent.putExtra("title", "购物");
                    shopIntent.putParcelableArrayListExtra("destinations", destinations);
                    shopIntent.putExtra("strategy", strategy);
                    shopIntent.putExtra("isOwner", isOwner);
                    getActivity().startActivityForResult(shopIntent, FOR_SHOP_COLLECTION);
                }
            }
        });
        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == FOR_FOOD_COLLECTION) {
                strategy.restaurant = data.getParcelableArrayListExtra("newStrategy");
                adapter.notifyDataSetChanged();
            } else if (requestCode == FOR_SHOP_COLLECTION) {
                strategy.shopping = data.getParcelableArrayListExtra("newStrategy");
                adapter.notifyDataSetChanged();
            }
        }
    }

    private StrategyBean getStrategy() {
        return ((StrategyActivity) getActivity()).getStrategy();
    }

    private ArrayList<LocBean> getDestinations() {
        return ((StrategyActivity) getActivity()).getDestinations();
    }

    private class StrategySaveAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public Object getItem(int i) {
            return i;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder;
            if (view == null) {
                view = getActivity().getLayoutInflater().inflate(R.layout.strategy_save_list_cell, null);
                viewHolder = new ViewHolder();
                viewHolder.saveType = (ImageView) view.findViewById(R.id.iv_save_type);
                viewHolder.saveTypeTitle = (TextView) view.findViewById(R.id.tv_save_title);
                viewHolder.saveNumber = (TextView) view.findViewById(R.id.tv_save_summary);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }
            if (i == 0) {
                viewHolder.saveTypeTitle.setText("美食");
                viewHolder.saveType.setImageResource(R.drawable.collect_food);
                viewHolder.saveNumber.setText(strategy.restaurant.size() + "个收藏");
            } else if (i == 1) {
                viewHolder.saveTypeTitle.setText("购物");
                viewHolder.saveType.setImageResource(R.drawable.collect_shopping);
                viewHolder.saveNumber.setText(strategy.shopping.size() + "个收藏");
            }

            return view;
        }
    }

    private class ViewHolder {
        public ImageView saveType;
        public TextView saveTypeTitle;
        public TextView saveNumber;
    }

}
