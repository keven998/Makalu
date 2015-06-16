package com.xuejian.client.lxp.module.dest;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.bean.PoiDetailBean;
import com.xuejian.client.lxp.bean.StrategyBean;
import com.xuejian.client.lxp.common.widget.TitleHeaderBar;
import com.xuejian.client.lxp.module.dest.CommonViewUnit.POIAdapter;

import java.util.ArrayList;

/**
 * Created by luoyong on 15/6/12.
 */
public class DayAgendaActivity extends FragmentActivity {
    final int RESULT_UPDATE_PLAN_DETAIL = 2;

    private StrategyBean strategy;
    private ArrayList<ArrayList<PoiDetailBean>> routeDayMap;
    private ListView mListView;

    private int currentDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day_agenda_layout);

        TitleHeaderBar titleHeaderBar = (TitleHeaderBar)findViewById(R.id.ly_header_bar_title_wrap);
        titleHeaderBar.getTitleTextView().setText("日程详情");

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

        findViewById(R.id.edit_plan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DayAgendaActivity.this, ActivityPlanEditor.class);
                intent.putExtra("strategy", strategy);
                startActivityForResult(intent, RESULT_UPDATE_PLAN_DETAIL);
                overridePendingTransition(R.anim.push_bottom_in, R.anim.slide_stay);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
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
