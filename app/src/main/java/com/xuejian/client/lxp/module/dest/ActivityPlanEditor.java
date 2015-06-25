package com.xuejian.client.lxp.module.dest;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aizou.core.widget.section.BaseSectionAdapter;
import com.umeng.analytics.MobclickAgent;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.bean.PoiDetailBean;
import com.xuejian.client.lxp.bean.StrategyBean;
import com.xuejian.client.lxp.common.widget.TitleHeaderBar;
import com.xuejian.client.lxp.common.widget.dslv.DragSortController;
import com.xuejian.client.lxp.common.widget.dslv.DragSortListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by luoyong on 15/6/15.
 */
public class ActivityPlanEditor extends FragmentActivity {

    private DragSortListView mDragListView;

    private StrategyBean strategy;
    private ArrayList<ArrayList<PoiDetailBean>> routeDayMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_editor_layout);
        TitleHeaderBar titleHeaderBar = (TitleHeaderBar)findViewById(R.id.ly_header_bar_title_wrap);
        titleHeaderBar.getTitleTextView().setText("修改行程");
        strategy = getIntent().getParcelableExtra("strategy");
        resizeData(strategy.itinerary);

        mDragListView = (DragSortListView) findViewById(R.id.listview_plan_editor);
        EditorAdapter editorAdapter = new EditorAdapter(this);
        mDragListView.setDropListener(editorAdapter);
        SectionController c = new SectionController(mDragListView, editorAdapter);
        mDragListView.setFloatViewManager(c);
        mDragListView.setOnTouchListener(c);
        mDragListView.setAdapter(editorAdapter);
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

    private class SectionController extends DragSortController {
        private EditorAdapter mAdapter;
        private DragSortListView mDSlv;
        private int mPos;
        private int origHeight = -1;

        public SectionController(DragSortListView dslv, EditorAdapter adapter) {
            super(dslv, R.id.tv_plan_editor, DragSortController.ON_LONG_PRESS, 0);
            setBackgroundColor(Color.TRANSPARENT);
            setRemoveEnabled(false);
            mDSlv = dslv;
            mAdapter = adapter;
        }

        @Override
        public View onCreateFloatView(int position) {
            mPos = position;
            View v = mAdapter.getView(position, null, mDSlv);
            return v;
        }

        @Override
        public void onDragFloatView(View floatView, Point position, Point touch) {

            final int lvDivHeight = mDSlv.getDividerHeight();

            if (origHeight == -1) {
                origHeight = floatView.getHeight();
            }
            //
            View div = mDSlv.getChildAt(mDSlv.getHeaderViewsCount());
            if (mPos > 0) {
                // don't allow floating View to go above
                // section divider
                if (mDSlv.getFirstVisiblePosition() == 0) {
                    final int limit = div.getBottom() + lvDivHeight;
                    if (position.y < limit) {
                        position.y = limit;
                    }
                }
            }
        }

        @Override
        public void onDestroyFloatView(View floatView) {
//            super.onDestroyFloatView(floatView);
        }
    }

    private class EditorAdapter extends BaseSectionAdapter implements DragSortListView.DropListener {
        private LayoutInflater inflater;

        public EditorAdapter(Context context) {
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getContentItemViewType(int section, int position) {
            return 0;
        }

        @Override
        public int getHeaderItemViewType(int section) {
            return 0;
        }

        @Override
        public int getItemViewTypeCount() {
            return 1;
        }

        @Override
        public int getHeaderViewTypeCount() {
            return 1;
        }

        @Override
        public Object getItem(int section, int position) {
            return routeDayMap.get(section).get(position);
        }

        @Override
        public long getItemId(int section, int position) {
            return getGlobalPositionForItem(section, position);
        }

        @Override
        public View getItemView(int section, int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.item_plan_editor, null);
                holder = new ViewHolder();
                holder.poiNameTextView = (TextView) convertView.findViewById(R.id.tv_plan_editor);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            PoiDetailBean poi = (PoiDetailBean) getItem(section, position);
            holder.poiNameTextView.setText(poi.zhName);
            return convertView;
        }

        @Override
        public View getHeaderView(int section, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.item_plan_editor_headerview, null);
                holder = new ViewHolder();
                holder.poiNameTextView = (TextView) convertView.findViewById(R.id.tv_day_index);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.poiNameTextView.setText(String.format("DAY%d", section));
            return convertView;
        }

        @Override
        public int getSectionCount() {
            return routeDayMap.size();
        }

        @Override
        public int getCountInSection(int section) {
            return routeDayMap.get(section).size();
        }

        @Override
        public boolean doesSectionHaveHeader(int section) {
            return true;
        }

        @Override
        public boolean shouldListHeaderFloat(int headerIndex) {
            return false;
        }

        @Override
        public void drop(int from, int to) {
            MobclickAgent.onEvent(ActivityPlanEditor.this, "event_reorder_items");
            int fromSection = getSection(from);
            int fromPostion = getPositionInSection(from);
            int toSection = getSection(to);
            int toPostion = getPositionInSection(to);
            if (toPostion == -1) {
                if (from > to) {
                    toSection -= 1;
                    toPostion = routeDayMap.get(toSection).size();
                } else {
                    toPostion += 1;
                }
            }
            List<PoiDetailBean> fromList = routeDayMap.get(fromSection);
            List<PoiDetailBean> toList = routeDayMap.get(toSection);
            PoiDetailBean bean = fromList.get(fromPostion);
            fromList.remove(bean);
            toList.add(toPostion, bean);

            notifyDataSetChanged();
        }

        @Override
        public boolean isEnabled(int position) {
            return !isHeader(position);
        }
    }

    private class ViewHolder {
        public TextView poiNameTextView;
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_stay, R.anim.push_bottom_out);
    }
}
