package com.xuejian.client.lxp.module.dest.fragment;

import android.graphics.Color;
import android.graphics.Point;
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
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.bean.PoiDetailBean;
import com.xuejian.client.lxp.bean.StrategyBean;
import com.xuejian.client.lxp.common.dialog.PeachMessageDialog;
import com.xuejian.client.lxp.common.widget.dslv.DragSortController;
import com.xuejian.client.lxp.common.widget.dslv.DragSortListView;
import com.xuejian.client.lxp.module.dest.ActivityPlanEditor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created by yibiao.qin on 2015/7/9.
 */
public class EditPlanFragment extends Fragment {
    private DragSortListView mDragListView;
    private PlanEditAdapter adapter;
    private ArrayList<ArrayList<PoiDetailBean>> routeDayMap;
    private StrategyBean strategy;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_plan_edit_menu, container, false);
    }
    private DragSortListView.DropListener onDrop =
            new DragSortListView.DropListener() {
                @Override
                public void drop(int from, int to) {//from to 分别表示 被拖动控件原位置 和目标位置
                    if (from != to) {
                        ArrayList<PoiDetailBean> item = (ArrayList<PoiDetailBean>)adapter.getItem(from);//得到listview的适配器
                        adapter.remove(from);//在适配器中”原位置“的数据。
                        adapter.insert(item, to);//在目标位置中插入被拖动的控件。
                        ActivityPlanEditor activityPlanEditor=(ActivityPlanEditor)getActivity();
                        activityPlanEditor.update(routeDayMap);
                    }
                }
            };
    //删除监听器，点击左边差号就触发。删除item操作。
    private DragSortListView.RemoveListener onRemove =
            new DragSortListView.RemoveListener() {
                @Override
                public void remove(int which) {
                    adapter.remove(which);
                }
            };
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mDragListView = (DragSortListView) getView().findViewById(R.id.listview_day_plan_editor);
        //得到滑动listview并且设置监听器。
        strategy = getArguments().getParcelable("data");
        resizeData(strategy.itinerary);
        mDragListView.setDropListener(onDrop);
        mDragListView.setRemoveListener(onRemove);
        adapter = new PlanEditAdapter( );
        mDragListView.setDragEnabled(true);
        SectionController c = new SectionController(mDragListView, adapter);
        c.setSortEnabled(true);
        mDragListView.setFloatViewManager(c);
        mDragListView.setOnTouchListener(c);
        mDragListView.setAdapter(adapter);
    }
    private class SectionController extends DragSortController {
        private PlanEditAdapter mAdapter;
        private DragSortListView mDSlv;
        private int mPos;
        private int origHeight = -1;

        public SectionController(DragSortListView dslv, PlanEditAdapter adapter) {
            super(dslv, R.id.rl_drag, DragSortController.ON_LONG_PRESS, DragSortController.CLICK_REMOVE);
            setBackgroundColor(Color.TRANSPARENT);
            setRemoveEnabled(false);
            setRemoveMode(DragSortController.CLICK_REMOVE);
            setClickRemoveId(R.id.delete);
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
    public void update(ArrayList<ArrayList<PoiDetailBean>> data){
        routeDayMap.clear();
        routeDayMap.addAll(data);
        adapter.notifyDataSetChanged();
    }

    public class PlanEditAdapter extends BaseAdapter {

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

        public void remove(int arg0) {//删除指定位置的item
            routeDayMap.remove(arg0);
            this.notifyDataSetChanged();//不要忘记更改适配器对象的数据源
        }

        public void insert(ArrayList<PoiDetailBean> item, int arg0) {
            routeDayMap.add(arg0, item);
            this.notifyDataSetChanged();
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater().inflate(R.layout.item_plan_edit_day, null);
                viewHolder = new ViewHolder();
                convertView.findViewById(R.id.rl_drag);
                viewHolder.delete = (ImageView) convertView.findViewById(R.id.delete);
                viewHolder.summaryTextView = (TextView) convertView.findViewById(R.id.tv_schedule_summary);
                viewHolder.tv_day_index = (TextView) convertView.findViewById(R.id.tv_day_index);
                viewHolder.tv_schedule_title = (TextView) convertView.findViewById(R.id.tv_schedule_title);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
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
            viewHolder.tv_day_index.setText(spb);

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
            if (TextUtils.isEmpty(desc)&&TextUtils.isEmpty(descTitle)){
                viewHolder.tv_schedule_title.setText("没有安排");
                viewHolder.summaryTextView.setVisibility(View.INVISIBLE);
            }else {
                viewHolder.summaryTextView.setVisibility(View.VISIBLE);
                viewHolder.summaryTextView.setText(desc);
                viewHolder.tv_schedule_title.setText(descTitle);
            }

          //  viewHolder.ivCountryLogo.setImageResource(item.src);
            viewHolder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MobclickAgent.onEvent(getActivity(), "event_delete_day_agenda");
                    final PeachMessageDialog deleteDialog = new PeachMessageDialog(getActivity());
                    deleteDialog.setTitle("提示");
                    deleteDialog.setMessage("删除这天安排");
                    deleteDialog.setPositiveButton("确定", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            routeDayMap.remove(position);
                            strategy.itineraryDays--;
                            adapter.notifyDataSetChanged();
//                            if (mOnEditModeChangeListener != null) {
//                                if (!isInEditMode) {
//                                    isInEditMode = true;
//                                    mOnEditModeChangeListener.onEditModeChange(false);
//                                    routeDayAdpater.setEditableMode(false);
//                                    routeDayAdpater.notifyDataSetChanged();
//                                }
//                            }
                            ActivityPlanEditor activityPlanEditor=(ActivityPlanEditor)getActivity();
                            activityPlanEditor.update(routeDayMap);
                            deleteDialog.dismiss();
                        }
                    });
                    deleteDialog.setNegativeButton("取消", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            deleteDialog.dismiss();
                        }
                    });
                    deleteDialog.show();
                }
            });

            return convertView;
        }

        class ViewHolder {
            ImageView delete;
            TextView summaryTextView;
            TextView tv_schedule_title;
            TextView tv_day_index;
        }
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
}
