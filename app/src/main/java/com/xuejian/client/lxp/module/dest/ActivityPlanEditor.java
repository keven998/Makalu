package com.xuejian.client.lxp.module.dest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.AbsoluteSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.aizou.core.http.HttpCallBack;
import com.aizou.core.widget.section.BaseSectionAdapter;
import com.umeng.analytics.MobclickAgent;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.bean.LocBean;
import com.xuejian.client.lxp.bean.ModifyResult;
import com.xuejian.client.lxp.bean.PoiDetailBean;
import com.xuejian.client.lxp.bean.StrategyBean;
import com.xuejian.client.lxp.common.account.StrategyManager;
import com.xuejian.client.lxp.common.api.TravelApi;
import com.xuejian.client.lxp.common.dialog.DialogManager;
import com.xuejian.client.lxp.common.gson.CommonJson;
import com.xuejian.client.lxp.common.widget.dslv.DragSortController;
import com.xuejian.client.lxp.common.widget.dslv.DragSortListView;
import com.xuejian.client.lxp.module.dest.fragment.EditPlanFragment;
import com.xuejian.client.lxp.module.dest.fragment.RouteDayFragment;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created by luoyong on 15/6/15.
 */
public class ActivityPlanEditor extends FragmentActivity {

    private DragSortListView mDragListView;
    private DrawerLayout drawerLayout;
    private StrategyBean strategy;
    private ArrayList<ArrayList<PoiDetailBean>> routeDayMap;
    private List<Integer> sectionlist = new ArrayList<>();
    EditorAdapter editorAdapter;
    Fragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_editor_layout);

        findViewById(R.id.tv_title_bar_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        findViewById(R.id.tv_title_bar_right).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveStrategy();
            }
        });

        strategy = getIntent().getParcelableExtra("strategy");
        resizeData(strategy.itinerary);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerLayout.setEnabled(true);
        mDragListView = (DragSortListView) findViewById(R.id.listview_plan_editor);
        editorAdapter = new EditorAdapter(this);
        mDragListView.setDropListener(editorAdapter);
        SectionController c = new SectionController(mDragListView, editorAdapter);
        c.setSortEnabled(true);
        mDragListView.setFloatViewManager(c);
        mDragListView.setOnTouchListener(c);
        mDragListView.setAdapter(editorAdapter);

        findViewById(R.id.btn_edit_day).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawerLayout.isDrawerVisible(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);//关闭抽屉
                } else {
                    drawerLayout.openDrawer(GravityCompat.START);//打开抽屉
                }
            }
        });
        findViewById(R.id.btn_add_day).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewDayRouter(routeDayMap.size(), false);
            }
        });
        fragment = new EditPlanFragment();
        Bundle args = new Bundle();
        args.putParcelable("data", strategy);
        fragment.setArguments(args); // FragmentActivity将点击的菜单列表标题传递给Fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.add(fragment, "edit_menu");
        ft.replace(R.id.menu_frame, fragment).commit();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerVisible(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
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
        int temp = 0;
        for (ArrayList<PoiDetailBean> list : routeDayMap) {
            sectionlist.add(temp += list.size());
        }

    }

    private class SectionController extends DragSortController {
        private EditorAdapter mAdapter;
        private DragSortListView mDSlv;
        private int mPos;
        private int origHeight = -1;

        public SectionController(DragSortListView dslv, EditorAdapter adapter) {
            super(dslv, R.id.tv_plan_editor, DragSortController.ON_LONG_PRESS, DragSortController.CLICK_REMOVE);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 101) {
                ArrayList<PoiDetailBean> poiList = data.getParcelableArrayListExtra("poiList");
                int dayIndex = data.getIntExtra("dayIndex", -1);
                routeDayMap.set(dayIndex, poiList);
                editorAdapter.notifyDataSetChanged();
                EditPlanFragment editFragment = (EditPlanFragment) getSupportFragmentManager().findFragmentByTag("edit_menu");
                if (editFragment != null) {
                    editFragment.update(routeDayMap);
                }
            }
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
        public View getItemView(final int section, final int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.item_plan_editor, null);
                holder = new ViewHolder();
                holder.poiNameTextView = (TextView) convertView.findViewById(R.id.tv_plan_editor);
                holder.deleteIv = (ImageView) convertView.findViewById(R.id.iv_delete_item);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            PoiDetailBean poi = (PoiDetailBean) getItem(section, position);
            holder.poiNameTextView.setText(poi.zhName);
            holder.deleteIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    routeDayMap.get(section).remove(position);
                    notifyDataSetChanged();
                }
            });
            return convertView;
        }

        @Override
        public View getHeaderView(final int section, View convertView, ViewGroup parent) {
            a_ViewHolder holder;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.item_plan_editor_headerview, null);
                holder = new a_ViewHolder();
                holder.a_tv_day_index = (TextView) convertView.findViewById(R.id.tv_day_index);
                holder.a_tv_schedule_title = (TextView) convertView.findViewById(R.id.tv_schedule_title);
                holder.iv_add = (ImageView) convertView.findViewById(R.id.iv_add);
                convertView.setTag(holder);
            } else {
                holder = (a_ViewHolder) convertView.getTag();
            }

            List<PoiDetailBean> poiList = routeDayMap.get(section);

            SpannableString planStr = new SpannableString("Day");
            planStr.setSpan(new AbsoluteSizeSpan(11, true), 0, planStr.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            SpannableStringBuilder spb = new SpannableStringBuilder();
            if (section < 9) {
                spb.append(String.format("0%s.\n", (section + 1))).append(planStr);
            } else {
                spb.append(String.format("%s.\n", (section + 1))).append(planStr);
            }
            holder.a_tv_day_index.setText(spb);

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
            holder.a_tv_schedule_title.setText(descTitle);
            holder.iv_add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ActivityPlanEditor.this, AddPoiActivity.class);
                    intent.putParcelableArrayListExtra("locList", strategy.localities);
                    intent.putExtra("dayIndex", section);
                    intent.putParcelableArrayListExtra("poiList", routeDayMap.get(section));
                    startActivityForResult(intent, RouteDayFragment.ADD_POI_REQUEST_CODE);
                }
            });

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
            EditPlanFragment editFragment = (EditPlanFragment) getSupportFragmentManager().findFragmentByTag("edit_menu");
            if (editFragment != null) {
                editFragment.update(routeDayMap);
            }
        }

        @Override
        public boolean isEnabled(int position) {
            return !isHeader(position);
        }
    }

    private class ViewHolder {
        TextView poiNameTextView;
        ImageView deleteIv;
    }

    private class a_ViewHolder {
        TextView a_tv_schedule_title;
        TextView a_tv_day_index;
        ImageView iv_add;
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_stay, R.anim.push_bottom_out);
    }

    public void update(ArrayList<ArrayList<PoiDetailBean>> data) {
        routeDayMap.clear();
        routeDayMap.addAll(data);
        editorAdapter.notifyDataSetChanged();
    }

    private void saveStrategy() {
        final JSONObject jsonObject = new JSONObject();
        StrategyManager.putSaveGuideBaseInfo(jsonObject, ActivityPlanEditor.this, strategy);
        StrategyManager.putItineraryJson(ActivityPlanEditor.this, jsonObject, strategy, routeDayMap);

        ArrayList<LocBean> locs=new ArrayList<LocBean>();
        locs.addAll(strategy.localities);

        DialogManager.getInstance().showLoadingDialog(ActivityPlanEditor.this);
        TravelApi.saveGuide(strategy.id, jsonObject.toString(), new HttpCallBack() {
            @Override
            public void doSuccess(Object result, String method) {
                CommonJson<ModifyResult> saveResult = CommonJson.fromJson(result.toString(), ModifyResult.class);
                if (saveResult.code == 0) {
                    DialogManager.getInstance().dissMissLoadingDialog();
                    Intent intent = new Intent(ActivityPlanEditor.this, StrategyActivity.class);
                    intent.putExtra("id", strategy.id);
                    intent.putExtra("userId", String.valueOf(strategy.userId));
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            }

            @Override
            public void doFailure(Exception error, String msg, String method) {
                DialogManager.getInstance().dissMissLoadingDialog();
            }

            @Override
            public void doFailure(Exception error, String msg, String method, int code) {

            }
        });
    }

    public void addNewDayRouter(int position, boolean isBefore) {
        final int sectionPos;
        if (isBefore) {
            routeDayMap.add(position, new ArrayList<PoiDetailBean>());
            sectionPos = position;
        } else {
            routeDayMap.add(new ArrayList<PoiDetailBean>());
            sectionPos = position + 1;
        }
        strategy.itineraryDays++;
        editorAdapter.notifyDataSetChanged();
        EditPlanFragment editFragment = (EditPlanFragment) getSupportFragmentManager().findFragmentByTag("edit_menu");
        if (editFragment != null) {
            editFragment.update(routeDayMap);
        }
    }

}
