package com.xuejian.client.lxp.module.dest;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Property;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.aizou.core.http.HttpCallBack;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.bean.DemoBean;
import com.xuejian.client.lxp.bean.IndexPoi;
import com.xuejian.client.lxp.bean.LocBean;
import com.xuejian.client.lxp.bean.PoiDetailBean;
import com.xuejian.client.lxp.bean.StrategyBean;
import com.xuejian.client.lxp.bean.TrafficBean;
import com.xuejian.client.lxp.common.account.StrategyManager;
import com.xuejian.client.lxp.common.api.TravelApi;
import com.xuejian.client.lxp.common.utils.CommonUtils;
import com.xuejian.client.lxp.common.widget.AnimatedDoorLayout;
import com.xuejian.client.lxp.module.dest.CommonViewUnit.POIAdapter;
import com.xuejian.client.lxp.module.dest.fragment.RouteDayFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by luoyong on 15/6/12.
 */
public class DayAgendaActivity extends FragmentActivity {
    final int RESULT_UPDATE_PLAN_DETAIL = 2;
    private StrategyBean strategy;
    private ArrayList<ArrayList<PoiDetailBean>> routeDayMap;
    private ListView mListView;
    private ListView lvOther;
    private int currentDay;
    private TextView mTitleView;
    private TextView mSubTitleView;
    private ImageView tv_editplan;
    private ImageView ivPanel;
    private FrameLayout place_detail_panel;
    private AnimatedDoorLayout mAnimated;
    int MsgActivity = 10001;
    int TrafficActivity = 10002;
    OtherAdapter mOtherAdapter;
    POIAdapter mPOIAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day_agenda_layout);
        place_detail_panel = (FrameLayout) this.findViewById(R.id.place_detail_panel);
        tv_editplan = (ImageView) findViewById(R.id.tv_edit_schedule);
        ivPanel = (ImageView) findViewById(R.id.iv_panel);
        ivPanel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPanel();
            }
        });
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
        lvOther = (ListView) findViewById(R.id.lv_other);
        mOtherAdapter = new OtherAdapter();
        lvOther.setAdapter(mOtherAdapter);

        currentDay = getIntent().getIntExtra("current_day", 0);
        resizeData(strategy.itinerary);
        mListView = (ListView) findViewById(R.id.listview_common);
        mPOIAdapter = new POIAdapter(this, routeDayMap.get(currentDay));
        mListView.setAdapter(mPOIAdapter);
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

        View parrent = place_detail_panel.getChildAt(0);
        mAnimated = new AnimatedDoorLayout(this);
        place_detail_panel.removeView(parrent);
        place_detail_panel.addView(mAnimated, parrent.getLayoutParams());
        mAnimated.addView(parrent);
        mAnimated.setDoorType(AnimatedDoorLayout.VERTICAL_DOOR);
        ObjectAnimator animator = ObjectAnimator.ofFloat(mAnimated, ANIMATED_DOOR_LAYOUT_FLOAT_PROPERTY, 1).setDuration(500);
        animator.start();
    }
    private PopupWindow mPopupWindow;
    public void showPanel() {

        View view = View.inflate(this, R.layout.dialog_plan_panel, null);
        view.findViewById(R.id.btn_poi).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DayAgendaActivity.this, AddPoiActivity.class);
                intent.putParcelableArrayListExtra("locList", strategy.localities);
                intent.putExtra("dayIndex", currentDay);
                intent.putExtra("type", "vs");
                intent.putParcelableArrayListExtra("poiList", routeDayMap.get(currentDay));
                startActivityForResult(intent, RouteDayFragment.ADD_POI_REQUEST_CODE);
            }
        });
        view.findViewById(R.id.btn_traffic).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(DayAgendaActivity.this, TrafficActivity.class), TrafficActivity);
            }
        });
        view.findViewById(R.id.btn_hotel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DayAgendaActivity.this, AddPoiActivity.class);
                intent.putParcelableArrayListExtra("locList", strategy.localities);
                intent.putExtra("dayIndex", currentDay);
                intent.putExtra("type", "hotels");
                intent.putParcelableArrayListExtra("poiList", routeDayMap.get(currentDay));
                startActivityForResult(intent, RouteDayFragment.ADD_POI_REQUEST_CODE);
            }
        });
        view.findViewById(R.id.btn_msg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DayAgendaActivity.this, MsgActivity.class);
                startActivityForResult(intent, MsgActivity);
            }
        });
        final PopupWindow popupWindow = new PopupWindow(view);
        mPopupWindow = popupWindow;
        view.findViewById(R.id.ll_container).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setWidth(CommonUtils.getScreenWidth(this));
        popupWindow.setHeight(CommonUtils.getScreenHeight(this) - mTitleView.getHeight() - 150);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {

            }
        });
        int[] location = new int[2];
        mTitleView.getLocationOnScreen(location);
        popupWindow.setAnimationStyle(R.style.PopAnimation1);
        final int[] f = location;
        popupWindow.showAtLocation(mTitleView, Gravity.NO_GRAVITY,
                CommonUtils.getScreenWidth(DayAgendaActivity.this) / 2, f[1] + 100);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mPopupWindow!=null){
            mPopupWindow.dismiss();
        }
    }

    private static final Property<AnimatedDoorLayout, Float> ANIMATED_DOOR_LAYOUT_FLOAT_PROPERTY = new Property<AnimatedDoorLayout, Float>(Float.class, "ANIMATED_DOOR_LAYOUT_FLOAT_PROPERTY") {
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

    public void beforeBack() {
        ObjectAnimator animator = ObjectAnimator.ofFloat(mAnimated, ANIMATED_DOOR_LAYOUT_FLOAT_PROPERTY, 0).setDuration(600);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                Intent intent = new Intent();
                intent.putExtra("dayIndex",currentDay);
                intent.putExtra("poiList", routeDayMap.get(currentDay));
                setResult(RESULT_OK,intent);
                finish();
            }
        });
        animator.start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == RouteDayFragment.ADD_POI_REQUEST_CODE) {
                ArrayList<PoiDetailBean> list = data.getParcelableArrayListExtra("poiList");
                int index = data.getIntExtra("dayIndex",0);
                saveStrategy(index,list);
                mPOIAdapter = new POIAdapter(this, routeDayMap.get(currentDay));
                mListView.setAdapter(mPOIAdapter);
            } else if (requestCode == MsgActivity) {
                DemoBean demoBean = data.getParcelableExtra("demo");
                saveDemo(demoBean);
            } else if (requestCode == TrafficActivity) {
                TrafficBean demoBean = data.getParcelableExtra("traffic");
                saveTraffic(demoBean);
            }
        }
    }

    private void saveTraffic(final TrafficBean demoBean) {
        demoBean.dayIndex = currentDay;
        final JSONObject jsonObject = new JSONObject();
        StrategyManager.putSaveGuideBaseInfo(jsonObject, DayAgendaActivity.this, strategy);
        StrategyManager.putItineraryJson(DayAgendaActivity.this, jsonObject, strategy, routeDayMap);
        JSONArray array = new JSONArray();
        for (TrafficBean bean : mTrafficBeanArrayList) {
            JSONObject object = new JSONObject();
            try {
                object.put("dayIndex", bean.dayIndex);
                object.put("arrTime", bean.arrTime);
                object.put("category", bean.category);
                object.put("depTime", bean.depTime);
                object.put("end", bean.end);
                object.put("start", bean.start);
                array.put(object);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        JSONObject object = new JSONObject();
        try {
            object.put("dayIndex", demoBean.dayIndex);
            object.put("arrTime", demoBean.arrTime);
            object.put("category", demoBean.category);
            object.put("depTime", demoBean.depTime);
            object.put("end", demoBean.end);
            object.put("desc", demoBean.desc);
            object.put("start", demoBean.start);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        array.put(object);
        try {
            jsonObject.put("trafficItems", array);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        TravelApi.saveGuide(strategy.id, jsonObject.toString(), new HttpCallBack() {
            @Override
            public void doSuccess(Object result, String method) {
                mOtherAdapter.getList().add(demoBean);
                mOtherAdapter.notifyDataSetChanged();
            }

            @Override
            public void doFailure(Exception error, String msg, String method) {
            }

            @Override
            public void doFailure(Exception error, String msg, String method, int code) {
            }
        });
    }

    ArrayList<TrafficBean> mTrafficBeanArrayList = new ArrayList<>();
    ArrayList<DemoBean> mDemoBeanArrayList = new ArrayList<>();

    private void saveDemo(final DemoBean demoBean) {
        demoBean.dayIndex = currentDay;
        final JSONObject jsonObject = new JSONObject();
        StrategyManager.putSaveGuideBaseInfo(jsonObject, DayAgendaActivity.this, strategy);
        StrategyManager.putItineraryJson(DayAgendaActivity.this, jsonObject, strategy, routeDayMap);
        JSONArray array = new JSONArray();
        for (DemoBean bean : mDemoBeanArrayList) {
            JSONObject object = new JSONObject();
            try {
                object.put("dayIndex", bean.dayIndex);
                object.put("desc", bean.desc);
                array.put(object);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        JSONObject object = new JSONObject();
        try {
            object.put("dayIndex",demoBean.dayIndex);
            object.put("desc",demoBean.desc);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        array.put(object);
        try {
            jsonObject.put("demoItems", array);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        TravelApi.saveGuide(strategy.id, jsonObject.toString(), new HttpCallBack() {
            @Override
            public void doSuccess(Object result, String method) {
                mOtherAdapter.getList().add(demoBean);
                mOtherAdapter.notifyDataSetChanged();
            }

            @Override
            public void doFailure(Exception error, String msg, String method) {
            }

            @Override
            public void doFailure(Exception error, String msg, String method, int code) {
            }
        });
    }

    private void saveStrategy(int day, ArrayList<PoiDetailBean>list) {
        routeDayMap.set(day, list);
        final JSONObject jsonObject = new JSONObject();
        StrategyManager.putSaveGuideBaseInfo(jsonObject, DayAgendaActivity.this, strategy);
        StrategyManager.putItineraryJson(DayAgendaActivity.this, jsonObject, strategy, routeDayMap);

        ArrayList<LocBean> locs = new ArrayList<LocBean>();
        locs.addAll(strategy.localities);
        TravelApi.saveGuide(strategy.id, jsonObject.toString(), new HttpCallBack() {
            @Override
            public void doSuccess(Object result, String method) {
            }

            @Override
            public void doFailure(Exception error, String msg, String method) {
            }

            @Override
            public void doFailure(Exception error, String msg, String method, int code) {
            }
        });
    }

    private void resizeData(ArrayList<IndexPoi> itinerary) {
        mDemoBeanArrayList.addAll(strategy.demoItems);
        mTrafficBeanArrayList.addAll(strategy.trafficItems);
        StrategyBean strategyBean = strategy;
        for (DemoBean demoItem : strategyBean.demoItems) {
            if (demoItem.dayIndex ==currentDay){
                mOtherAdapter.getList().add(demoItem);
            }
        }
        for (TrafficBean trafficItem : strategyBean.trafficItems) {
            if (trafficItem.dayIndex ==currentDay){
                mOtherAdapter.getList().add(trafficItem);
            }
        }

//        mOtherAdapter.getList().addAll(strategyBean.demoItems);
//        mOtherAdapter.getList().addAll(strategyBean.trafficItems);
        mOtherAdapter.notifyDataSetChanged();
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

    public class OtherAdapter extends BaseAdapter {
        public ArrayList<Object> mList = new ArrayList<>();

        public ArrayList<Object> getList() {
            return mList;
        }

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public Object getItem(int position) {
            return mList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_other, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            }else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            Object object = getItem(position);
            if (object instanceof DemoBean){
                viewHolder.mIvPoiImg.setImageResource(R.drawable.icon_note);
                viewHolder.mTvMsg.setVisibility(View.VISIBLE);
                viewHolder.ll_traffic.setVisibility(View.GONE);
                viewHolder.mTvMsg.setText(((DemoBean)object).desc);
            }else if (object instanceof TrafficBean){
                viewHolder.mIvPoiImg.setImageResource(R.drawable.icon_traffic);
                viewHolder.ll_traffic.setVisibility(View.VISIBLE);
                viewHolder.mTvMsg.setVisibility(View.GONE);
                viewHolder.mTvName.setText(((TrafficBean)object).desc);
                viewHolder.mTvDestination.setText(String.format("%s->%s",((TrafficBean)object).start,((TrafficBean)object).end));
                viewHolder.mTvTime.setText(String.format("%s->%s",((TrafficBean)object).depTime,((TrafficBean)object).arrTime));
            }
            return convertView;
        }

        class ViewHolder {
            @Bind(R.id.iv_poi_img)
            ImageView mIvPoiImg;
            @Bind(R.id.tv_name)
            TextView mTvName;
            @Bind(R.id.tv_destination)
            TextView mTvDestination;
            @Bind(R.id.tv_time)
            TextView mTvTime;
            @Bind(R.id.tv_msg)
            TextView mTvMsg;
            @Bind(R.id.ll_traffic)
            LinearLayout ll_traffic;

            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }
        }
    }
}
