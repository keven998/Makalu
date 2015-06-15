package com.xuejian.client.lxp.module.dest;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.bean.PoiDetailBean;
import com.xuejian.client.lxp.bean.StrategyBean;
import com.xuejian.client.lxp.module.dest.CommonViewUnit.POIAdapter;

import java.util.ArrayList;

/**
 * Created by luoyong on 15/6/12.
 */
public class DayAgendaActivity extends FragmentActivity {

    private StrategyBean strategy;
    private ArrayList<ArrayList<PoiDetailBean>> routeDayMap;
    private ListView mListView;

    private int currentDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.base_listview_layout);

        strategy = getIntent().getParcelableExtra("strategy");
        resizeData(strategy.itinerary);
        currentDay = getIntent().getIntExtra("current_day", 0);

        mListView = (ListView) findViewById(R.id.listview_common);
        mListView.setAdapter(new POIAdapter(this, routeDayMap.get(currentDay)));
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void resizeData(ArrayList<StrategyBean.IndexPoi> itinerary) {
        StrategyBean strategyBean = strategy;
        routeDayMap = new ArrayList<ArrayList<PoiDetailBean>>();
        for (int i = 0; i < strategyBean.itineraryDays; i++) {
            routeDayMap.add(new ArrayList<PoiDetailBean>());
        }

        for (StrategyBean.IndexPoi indexPoi : itinerary) {
            if(routeDayMap.size()>indexPoi.dayIndex){
                routeDayMap.get(indexPoi.dayIndex).add(indexPoi.poi);
            }
        }

    }

}
