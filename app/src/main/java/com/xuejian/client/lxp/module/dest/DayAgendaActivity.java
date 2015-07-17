package com.xuejian.client.lxp.module.dest;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.bean.PoiDetailBean;
import com.xuejian.client.lxp.bean.StrategyBean;
import com.xuejian.client.lxp.module.dest.CommonViewUnit.POIAdapter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created by luoyong on 15/6/12.
 */
public class DayAgendaActivity extends FragmentActivity {
    final int RESULT_UPDATE_PLAN_DETAIL = 2;

    private StrategyBean strategy;
    private ArrayList<ArrayList<PoiDetailBean>> routeDayMap;
    private ListView mListView;

    private int currentDay;
    private TextView mTitleView;
    private TextView mSubTitleView;
    private TextView tv_editplan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day_agenda_layout);

        tv_editplan = (TextView) findViewById(R.id.tv_edit_schedule);
        tv_editplan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DayAgendaActivity.this, ActivityPlanEditor.class);
                intent.putExtra("strategy", strategy);
                startActivityForResult(intent, RESULT_UPDATE_PLAN_DETAIL);
                overridePendingTransition(R.anim.push_bottom_in, R.anim.slide_stay);
            }
        });
        findViewById(R.id.tv_title_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mTitleView = (TextView) findViewById(R.id.tv_title);
        mSubTitleView = (TextView) findViewById(R.id.tv_subtitle);

        strategy = getIntent().getParcelableExtra("strategy");
//        User user = AccountManager.getInstance().getLoginAccount(this);
//        if (user != null && user.getUserId() == strategy.userId) {
//            tv_editplan.setVisibility(View.VISIBLE);
//        }
        resizeData(strategy.itinerary);
        currentDay = getIntent().getIntExtra("current_day", 0);

        mListView = (ListView) findViewById(R.id.listview_common);
        mListView.setAdapter(new POIAdapter(this, routeDayMap.get(currentDay)));
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PoiDetailBean bean = routeDayMap.get(currentDay).get(position);
                Intent intent = new Intent();
                intent.putExtra("id", bean.id);
                intent.putExtra("type", bean.type);
                intent.setClass(DayAgendaActivity.this, PoiDetailActivity.class);
                startActivity(intent);
            }
        });

        setupTitle();
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
            if (routeDayMap.size() > indexPoi.dayIndex) {
                routeDayMap.get(indexPoi.dayIndex).add(indexPoi.poi);
            }
        }

    }

    private void setupTitle() {
        List<PoiDetailBean> poiList = routeDayMap.get(currentDay);
        String descTitle = "";
        HashSet<String> set = new HashSet<String>();
        PoiDetailBean pdb;
        int count = poiList.size();
        for (int i = 0; i < count; ++i) {
            pdb = poiList.get(i);
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

        if (!TextUtils.isEmpty(descTitle)) {
            mSubTitleView.setText(descTitle);
        } else {
            mSubTitleView.setText("无安排");
        }

        if (currentDay < 9) {
            mTitleView.setText(String.format("0%s.Day详情", currentDay + 1));
        } else {
            mTitleView.setText(String.format("%s.Day详情", currentDay + 1));
        }

    }

}
