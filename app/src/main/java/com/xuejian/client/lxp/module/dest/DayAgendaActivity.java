package com.xuejian.client.lxp.module.dest;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Property;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.bean.PoiDetailBean;
import com.xuejian.client.lxp.bean.StrategyBean;
import com.xuejian.client.lxp.common.widget.AnimatedDoorLayout;
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
    private FrameLayout place_detail_panel;
    private AnimatedDoorLayout mAnimated;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day_agenda_layout);
        place_detail_panel = (FrameLayout)this.findViewById(R.id.place_detail_panel);
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
                beforeBack();
            }
        });
        mTitleView = (TextView) findViewById(R.id.tv_title);
        mSubTitleView = (TextView) findViewById(R.id.tv_subtitle);

        strategy = getIntent().getParcelableExtra("strategy");

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

        View parrent =place_detail_panel.getChildAt(0);
        mAnimated = new AnimatedDoorLayout(this);
        place_detail_panel.removeView(parrent);
        place_detail_panel.addView(mAnimated, parrent.getLayoutParams());
        mAnimated.addView(parrent);
        mAnimated.setDoorType(AnimatedDoorLayout.VERTICAL_DOOR);
        ObjectAnimator animator = ObjectAnimator.ofFloat(mAnimated,ANIMATED_DOOR_LAYOUT_FLOAT_PROPERTY,1).setDuration(500);
        animator.start();
    }


    private static final Property<AnimatedDoorLayout,Float>  ANIMATED_DOOR_LAYOUT_FLOAT_PROPERTY = new Property<AnimatedDoorLayout, Float>(Float.class, "ANIMATED_DOOR_LAYOUT_FLOAT_PROPERTY") {
        @Override
        public Float get(AnimatedDoorLayout layout) {
            return layout.getProgress();
        }

        @Override
        public void set(AnimatedDoorLayout layout, Float value) {
            layout.setProgress(value);
        }
    };

    @Override
    public void onBackPressed() {
        beforeBack();
    }

    public void beforeBack(){
        ObjectAnimator animator = ObjectAnimator.ofFloat(mAnimated, ANIMATED_DOOR_LAYOUT_FLOAT_PROPERTY,0).setDuration(600);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                finish();
            }
        });
        animator.start();
    }
    @Override
    protected void onResume() {
        super.onResume();
        //MobclickAgent.onPageStart("page_lxp_day_schedule_detail");
        //MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //MobclickAgent.onPageEnd("page_lxp_day_schedule_detail");
        // MobclickAgent.onPause(this);
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

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }

}
