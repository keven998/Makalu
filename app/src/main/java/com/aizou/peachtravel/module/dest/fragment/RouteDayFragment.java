package com.aizou.peachtravel.module.dest.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aizou.core.utils.LocalDisplay;
import com.aizou.peachtravel.base.BaseActivity;
import com.aizou.peachtravel.common.dialog.DialogManager;
import com.aizou.core.dialog.ToastUtil;
import com.aizou.core.http.HttpCallBack;
import com.aizou.core.log.LogUtil;
import com.aizou.core.widget.section.BaseSectionAdapter;
import com.aizou.peachtravel.R;
import com.aizou.peachtravel.base.PeachBaseFragment;
import com.aizou.peachtravel.bean.LocBean;
import com.aizou.peachtravel.bean.ModifyResult;
import com.aizou.peachtravel.bean.PoiDetailBean;
import com.aizou.peachtravel.bean.StrategyBean;
import com.aizou.peachtravel.common.account.StrategyManager;
import com.aizou.peachtravel.common.api.TravelApi;
import com.aizou.peachtravel.common.dialog.PeachMessageDialog;
import com.aizou.peachtravel.common.gson.CommonJson;
import com.aizou.peachtravel.common.imageloader.UILUtils;
import com.aizou.peachtravel.common.utils.IntentUtils;
import com.aizou.peachtravel.common.widget.BlurDialogMenu.BlurDialogFragment;
import com.aizou.peachtravel.common.widget.dslv.DragSortController;
import com.aizou.peachtravel.common.widget.dslv.DragSortListView;
import com.aizou.peachtravel.module.dest.AddPoiActivity;
import com.aizou.peachtravel.module.dest.OnDestActionListener;
import com.aizou.peachtravel.module.dest.OnEditModeChangeListener;
import com.aizou.peachtravel.module.dest.PoiDetailActivity;
import com.aizou.peachtravel.module.dest.SpotDetailActivity;
import com.aizou.peachtravel.module.dest.StrategyActivity;
import com.lidroid.xutils.util.LogUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Rjm on 2014/11/24.
 */
public class RouteDayFragment extends PeachBaseFragment implements OnEditModeChangeListener {
    public static final int ADD_POI_REQUEST_CODE = 101;
    private StrategyBean strategy;
    private ArrayList<ArrayList<PoiDetailBean>> routeDayMap;
    private OnEditModeChangeListener mOnEditModeChangeListener;
    @InjectView(R.id.edit_dslv)
    DragSortListView mEditDslv;
    RouteDayAdapter routeDayAdpater;
    @InjectView(R.id.edit_btn)
    CheckedTextView mEditBtn;
    View addDayFooter;
    Button addDayBtn;
    boolean isInEditMode;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_route_guide, container, false);
        addDayFooter = View.inflate(getActivity(), R.layout.footer_route_day_add_day, null);
        addDayBtn = (Button) addDayFooter.findViewById(R.id.btn_add_day);
        ButterKnife.inject(this, rootView);
        mEditDslv.addFooterView(addDayFooter);
        initData();
        return rootView;
    }
    @Override
    public void onAttach(Activity activity) {
        try {
            mOnEditModeChangeListener = (OnEditModeChangeListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement On OnDestActionListener");
        }
        super.onAttach(activity);
    }

    private void resizeData(ArrayList<StrategyBean.IndexPoi> itinerary) {
        routeDayMap = new ArrayList<ArrayList<PoiDetailBean>>();
        for (int i = 0; i < strategy.itineraryDays; i++) {
            routeDayMap.add(new ArrayList<PoiDetailBean>());
        }

        for (StrategyBean.IndexPoi indexPoi : itinerary) {
            routeDayMap.get(indexPoi.dayIndex).add(indexPoi.poi);
        }

    }

    public void resumeItinerary() {
        ArrayList<StrategyBean.IndexPoi> poiList = new ArrayList<>();
        int dayIndex = 0;
        StrategyBean.IndexPoi indexPoi;
        for (ArrayList<PoiDetailBean> poiDayList : routeDayMap) {
            for (PoiDetailBean poiDetailBean : poiDayList) {
                indexPoi = new StrategyBean.IndexPoi();
                indexPoi.dayIndex = dayIndex;
                indexPoi.poi = poiDetailBean;
                poiList.add(indexPoi);
            }
            dayIndex++;
        }
        strategy.itinerary = poiList;
    }

    public boolean isEditableMode() {
        if (routeDayAdpater != null) {
            return routeDayAdpater.isEditableMode;
        }
        return false;
    }

    public ArrayList<ArrayList<PoiDetailBean>> getRouteDayMap() {
        if (routeDayMap == null) {
            resizeData(strategy.itinerary);
        }
        return routeDayMap;
    }

    public StrategyBean getStrategy() {
        return strategy;
    }

    private void initData() {
        strategy = getArguments().getParcelable("strategy");
        isInEditMode = getArguments().getBoolean("isInEditMode");
        resizeData(strategy.itinerary);
        final RouteDayAdapter adapter = new RouteDayAdapter();
        routeDayAdpater = adapter;
        final DragSortListView listView = mEditDslv;
        routeDayAdpater.isEditableMode = isInEditMode;
        listView.setDropListener(adapter);
        // make and set controller on dslv
        SectionController c = new SectionController(listView, adapter);
        listView.setFloatViewManager(c);
        listView.setOnTouchListener(c);
        listView.setAdapter(adapter);

        mEditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!adapter.isEditableMode) {
                    mEditBtn.setChecked(true);
                    addDayFooter.setVisibility(View.VISIBLE);
                    adapter.isEditableMode = !adapter.isEditableMode;
                    adapter.notifyDataSetChanged();
                } else {
                    //todo:保存路线
                    DialogManager.getInstance().showLoadingDialog(getActivity());
                    JSONObject jsonObject = new JSONObject();
                    StrategyManager.putSaveGuideBaseInfo(jsonObject, getActivity(), strategy);
                    StrategyManager.putItineraryJson(getActivity(), jsonObject, strategy, routeDayMap);
                    TravelApi.saveGuide(strategy.id, jsonObject.toString(), new HttpCallBack<String>() {
                        @Override
                        public void doSucess(String result, String method) {
                            DialogManager.getInstance().dissMissLoadingDialog();
                            CommonJson<ModifyResult> saveResult = CommonJson.fromJson(result, ModifyResult.class);
                            if (saveResult.code == 0) {
//                                    ToastUtil.getInstance(getActivity()).showToast("保存成功");
                                mEditBtn.setChecked(false);
                                addDayFooter.setVisibility(View.INVISIBLE);
                                resumeItinerary();
                                adapter.isEditableMode = !adapter.isEditableMode;
                                adapter.notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void doFailure(Exception error, String msg, String method) {
                            DialogManager.getInstance().dissMissLoadingDialog();
//                                ToastUtil.getInstance(getActivity()).showToast("保存失败");
                            if (isAdded())
                                ToastUtil.getInstance(getActivity()).showToast(getResources().getString(R.string.request_network_failed));
                        }
                    });

                }

            }
        });
        int count = adapter.getCount();
        if (count == 0) {
            if (!mEditBtn.isChecked()) {
                mEditBtn.performClick();
            }
        } else {
            boolean ed = true;
            for (int i = 0; i < count; i++) {
                if (adapter.getCountInSection(i) > 0) {
                    ed = false;
                    break;
                }
            }
            if (ed) {
                if (!mEditBtn.isChecked()) {
                    mEditBtn.performClick();
                }
            }
        }


        addDayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                routeDayMap.add(new ArrayList<PoiDetailBean>());
                strategy.itineraryDays++;
                adapter.notifyDataSetChanged();
                listView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        listView.setSelection(adapter.getCount() - 1);
                    }
                }, 50);
                if(mOnEditModeChangeListener!=null){
                    if(!isInEditMode){
                        isInEditMode = true;
                        routeDayAdpater.isEditableMode=true;
                        routeDayAdpater.notifyDataSetChanged();
                        mOnEditModeChangeListener.onEditModeChange(true);
                    }
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == ADD_POI_REQUEST_CODE) {
                ArrayList<PoiDetailBean> poiList = data.getParcelableArrayListExtra("poiList");
                int dayIndex = data.getIntExtra("dayIndex", -1);
                routeDayMap.set(dayIndex, poiList);
                routeDayAdpater.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onEditModeChange(boolean isInEdit) {
        this.isInEditMode = isInEdit;
        if (routeDayAdpater != null) {
            routeDayAdpater.isEditableMode = isInEdit;
            routeDayAdpater.notifyDataSetChanged();
        }

    }

    private class SectionController extends DragSortController {
        private int mPos;
        private RouteDayAdapter mAdapter;
        DragSortListView mDslv;

        public SectionController(DragSortListView dslv, RouteDayAdapter adapter) {
            super(dslv, R.id.drag_handle, DragSortController.ON_DOWN, 0);
            setBackgroundColor(Color.TRANSPARENT);
            setRemoveEnabled(false);
            mDslv = dslv;
            mAdapter = adapter;
        }


        @Override
        public View onCreateFloatView(int position) {
            mPos = position;
            LogUtil.d("dslv", "pos=" + mPos);
            View v = mAdapter.getView(position, null, mDslv);
            // v.setBackgroundDrawable(getResources().getDrawable(
            // R.drawable.bg_handle_section1));
            // v.getBackground().setLevel(10000);
            return v;
        }

        private int origHeight = -1;

        @Override
        public void onDragFloatView(View floatView, Point floatPoint, Point touchPoint) {
            // final int first = mDslv.getFirstVisiblePosition();
            final int lvDivHeight = mDslv.getDividerHeight();

            if (origHeight == -1) {
                origHeight = floatView.getHeight();
            }
            //
            View div = mDslv.getChildAt(mDslv.getHeaderViewsCount());
            if (mPos > 0) {
                // don't allow floating View to go above
                // section divider
                if (mDslv.getFirstVisiblePosition() == 0) {
                    final int limit = div.getBottom() + lvDivHeight;
                    LogUtil.d("dslv", "limit=" + limit + "--y=" + floatPoint.y);
                    if (floatPoint.y < limit) {
                        floatPoint.y = limit;
                    }
                }

            } else {

            }
        }

        @Override
        public void onDestroyFloatView(View floatView) {
            // do nothing; block super from crashing
        }

    }

    public class RouteDayAdapter extends BaseSectionAdapter implements
            DragSortListView.DropListener {
        public static final int SPOT = 0;
        public static final int POI = 1;
        public boolean isEditableMode;
        private DisplayImageOptions options;

        public RouteDayAdapter() {
            super();
            options = UILUtils.getRadiusOption(LocalDisplay.dp2px(4));
        }

        @Override
        public int getContentItemViewType(int section, int position) {
            String type = routeDayMap.get(section).get(position).type;
            if (type.equals(TravelApi.PeachType.SPOT)) {
                return SPOT;
            } else {
                return POI;
            }

        }

        @Override
        public int getHeaderItemViewType(int section) {
            return 0;
        }

        @Override
        public int getItemViewTypeCount() {
            return 2;
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
        public View getItemView(final int section, int position, View convertView, ViewGroup parent) {
            int type = getContentItemViewType(section, position);
            LogUtils.d("item---section:" + section + "--postion:" + position + "--globle_postion" + getGlobalPositionForItem(section, position));
            ItemViewHolder holder = null;
            if (convertView == null) {
                holder = new ItemViewHolder();
                switch (type) {
                    case SPOT:
                        convertView = View.inflate(getActivity(), R.layout.row_routeday_spot, null);
                        holder.contentRl = (RelativeLayout) convertView.findViewById(R.id.rl_content);
                        holder.deleteIv = (ImageView) convertView.findViewById(R.id.delete_iv);
                        holder.dragHandleIv = (ImageView) convertView.findViewById(R.id.drag_handle);
                        holder.nearByTv = (Button) convertView.findViewById(R.id.drag_nearby_btn);
                        holder.spotImageIv = (ImageView) convertView.findViewById(R.id.spot_image_iv);
                        holder.spotNameTv = (TextView) convertView.findViewById(R.id.spot_name_tv);
                        holder.spotCostTimeTv = (TextView) convertView.findViewById(R.id.spot_time_cost_tv);
                        break;
                    case POI:
                        convertView = View.inflate(getActivity(), R.layout.row_routeday_poi, null);
                        holder.contentRl = (RelativeLayout) convertView.findViewById(R.id.rl_content);
                        holder.deleteIv = (ImageView) convertView.findViewById(R.id.delete_iv);
                        holder.dragHandleIv = (ImageView) convertView.findViewById(R.id.drag_handle);
                        holder.nearByTv = (Button) convertView.findViewById(R.id.drag_nearby_btn);
                        holder.poiImageIv = (ImageView) convertView.findViewById(R.id.poi_image_iv);
                        holder.poiNameTv = (TextView) convertView.findViewById(R.id.poi_name_tv);
                        holder.poiAddressTv = (TextView) convertView.findViewById(R.id.poi_address_tv);
                        holder.poiPriceTv = (TextView) convertView.findViewById(R.id.poi_price_tv);
                        holder.poiRating = (RatingBar) convertView.findViewById(R.id.poi_rating);
                        break;
                }
                convertView.setTag(holder);
            } else {
                holder = (ItemViewHolder) convertView.getTag();
            }
            final PoiDetailBean poiDetailBean = (PoiDetailBean) getItem(section, position);
            switch (type) {
                case SPOT:
                    if (poiDetailBean.images.size() > 0) {
                        ImageLoader.getInstance().displayImage(poiDetailBean.images.get(0).url, holder.spotImageIv, options);
                    } else {
                        holder.spotImageIv.setImageDrawable(null);
                    }

                    holder.spotNameTv.setText(poiDetailBean.zhName);
                    holder.spotCostTimeTv.setText("参考游玩  "+poiDetailBean.timeCostDesc);
                    if (isEditableMode) {
                        holder.deleteIv.setVisibility(View.VISIBLE);
//                        holder.nearByTv.setVisibility(View.GONE);
                        holder.dragHandleIv.setVisibility(View.VISIBLE);
                        holder.deleteIv.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                final PeachMessageDialog deleteDialog = new PeachMessageDialog(getActivity());
                                deleteDialog.setTitle("提示");
                                deleteDialog.setMessage("确定删除？");
                                deleteDialog.setPositiveButton("确定", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        routeDayMap.get(section).remove(poiDetailBean);
                                        notifyDataSetChanged();
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
                    } else {
                        holder.deleteIv.setVisibility(View.GONE);
//                        holder.nearByTv.setVisibility(View.VISIBLE);
                        holder.dragHandleIv.setVisibility(View.GONE);
//                        holder.nearByTv.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                            }
//                        });
                    }
                    holder.contentRl.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            IntentUtils.intentToDetail(getActivity(), TravelApi.PeachType.SPOT,poiDetailBean.id);
                        }
                    });

                    break;

                case POI:
                    if (poiDetailBean.images.size() > 0) {
                        ImageLoader.getInstance().displayImage(poiDetailBean.images.get(0).url, holder.poiImageIv, options);
                    } else {
                        holder.poiImageIv.setImageDrawable(null);
                    }

                    holder.poiNameTv.setText(poiDetailBean.zhName);
                    holder.poiAddressTv.setText(poiDetailBean.address);
                    holder.poiRating.setRating(poiDetailBean.getRating());

                    holder.poiPriceTv.setText(poiDetailBean.priceDesc);
                    if (isEditableMode) {
                        holder.deleteIv.setVisibility(View.VISIBLE);
//                        holder.nearByTv.setVisibility(View.GONE);
                        holder.dragHandleIv.setVisibility(View.VISIBLE);
                        holder.deleteIv.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                final PeachMessageDialog deleteDialog = new PeachMessageDialog(getActivity());
                                deleteDialog.setTitle("提示");
                                deleteDialog.setMessage("确定删除？");
                                deleteDialog.setPositiveButton("确定", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        routeDayMap.get(section).remove(poiDetailBean);
                                        notifyDataSetChanged();
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
                    } else {
                        holder.deleteIv.setVisibility(View.GONE);
//                        holder.nearByTv.setVisibility(View.VISIBLE);
                        holder.dragHandleIv.setVisibility(View.GONE);
//                        holder.nearByTv.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//
//                            }
//                        });
                    }
                    holder.contentRl.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            IntentUtils.intentToDetail(getActivity(), poiDetailBean.type, poiDetailBean.id);
                        }
                    });
                    break;
            }
            return convertView;
        }

        @Override
        public View getHeaderView(final int section, View convertView, ViewGroup parent) {
            HeaderViewHolder holder = null;
            if (convertView == null) {
                holder = new HeaderViewHolder();
                convertView = View.inflate(getActivity(), R.layout.row_drag_div, null);
                convertView.setTag(holder);
            } else {
                holder = (HeaderViewHolder) convertView.getTag();
            }
            holder.dayTv = (TextView) convertView.findViewById(R.id.tv_div);
            holder.addPoiIv = (Button) convertView.findViewById(R.id.iv_add_poi);
            holder.deleteDayIv = (Button) convertView.findViewById(R.id.iv_delete_day);
            holder.nullLl = (LinearLayout) convertView.findViewById(R.id.ll_null);
            holder.citysLl = (LinearLayout) convertView.findViewById(R.id.ll_citys);
            List<PoiDetailBean> poiList = routeDayMap.get(section);
            holder.citysLl.removeAllViews();
            if (poiList.size() > 0) {
                holder.nullLl.setVisibility(View.GONE);
                LinkedHashSet<String> citySet = new LinkedHashSet<String>();
                for (PoiDetailBean detailBean : poiList) {
                    if (detailBean.locality != null) {
                        citySet.add(detailBean.locality.zhName);
                    }
                }

                if (!citySet.isEmpty()) {
                    String des = "";
                    for (String str : citySet) {
                        View cityView = View.inflate(getActivity(), R.layout.item_route_day_loc, null);
                        holder.citysLl.addView(cityView);
                        TextView cityNameTv = (TextView) cityView.findViewById(R.id.tv_city_name);
                        cityNameTv.setText(str);
                    }
                    holder.dayTv.setText("第" + (section + 1) + "天  " + des);
                } else {
                    holder.dayTv.setText("第" + (section + 1) + "天");
                }
            } else {
                holder.dayTv.setText("第" + (section + 1) + "天  未安排");
                if (isEditableMode) {
                    holder.nullLl.setVisibility(View.GONE);
                } else {
                    holder.nullLl.setVisibility(View.GONE);
                }

            }
            if (isEditableMode) {
                holder.addPoiIv.setVisibility(View.VISIBLE);
                holder.deleteDayIv.setVisibility(View.VISIBLE);
                holder.addPoiIv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), AddPoiActivity.class);
                        intent.putParcelableArrayListExtra("locList", strategy.localities);
                        intent.putExtra("dayIndex", section);
                        intent.putParcelableArrayListExtra("poiList", routeDayMap.get(section));
                        getActivity().startActivityForResult(intent, RouteDayFragment.ADD_POI_REQUEST_CODE);
//                        RouteDayMenu fragment = new RouteDayMenu();
//                        fragment.setRouteDay(routeDayMap, routeDayAdpater);
//                        Bundle args = new Bundle();
//                        args.putInt(
//                                SupportBlurDialogFragment.BUNDLE_KEY_BLUR_RADIUS,
//                                4
//                        );
//                        args.putFloat(
//                                SupportBlurDialogFragment.BUNDLE_KEY_DOWN_SCALE_FACTOR,
//                                5
//                        );
//                        args.putInt("dayIndex", section);
//                        args.putParcelableArrayList("locList", locList);
//
//                        fragment.setArguments(args);
//                        fragment.show(getActivity().getSupportFragmentManager(), "blur_menu");
//                        showMenu();
                    }
                });

                holder.deleteDayIv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final PeachMessageDialog deleteDialog = new PeachMessageDialog(getActivity());
                        deleteDialog.setTitle("提示");
                        deleteDialog.setMessage("确定删除这一天吗？");
                        deleteDialog.setPositiveButton("确定", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                routeDayMap.remove(section);
                                strategy.itineraryDays--;
                                notifyDataSetChanged();
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

            } else {
//                holder.menuIv.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//
//                    }
//                });
                holder.addPoiIv.setVisibility(View.GONE);
                holder.deleteDayIv.setVisibility(View.GONE);
            }


            return convertView;
        }

//        private void showMenu() {
//            new MaterialDialog.Builder(getActivity())
//                    .title(null)
//                    .items(new String[]{"添加地点", "删除这天"})
//                    .itemsCallback(new MaterialDialog.ListCallback() {
//                        @Override
//                        public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
//
//                        }
//                    })
//                    .show();
//        }

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

            if (isHeader(position)) {
                return false;
            } else {
                return true;
            }
        }

        private class ItemViewHolder {
            public RelativeLayout contentRl;
            public ImageView deleteIv, dragHandleIv;
            public Button nearByTv;
            public ImageView poiImageIv, spotImageIv;
            public TextView poiNameTv, spotNameTv;
            public TextView poiAddressTv, spotCostTimeTv;
            public TextView poiPriceTv;
            public RatingBar poiRating;
        }

        private class HeaderViewHolder {
            public LinearLayout citysLl;
            public LinearLayout nullLl;
            public TextView dayTv;
            public Button deleteDayIv;
            public Button addPoiIv;
        }
    }

    public static class RouteDayMenu extends BlurDialogFragment {
        public int dayIndex;
        private ArrayList<LocBean> locList;
        private ArrayList<PoiDetailBean> poiList;
        private ArrayList<ArrayList<PoiDetailBean>> mRouteDayMap;
        private RouteDayAdapter mRouteDayAdapter;

        public RouteDayMenu() {
            super();
        }

        public void setRouteDay(ArrayList<ArrayList<PoiDetailBean>> RouteDayMap, RouteDayAdapter routeDayAdapter) {
            mRouteDayMap = RouteDayMap;
            mRouteDayAdapter = routeDayAdapter;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            dayIndex = getArguments().getInt("dayIndex");
            locList = getArguments().getParcelableArrayList("locList");
            poiList = getArguments().getParcelableArrayList("poiList");
            LogUtil.d(locList.toString());
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            Dialog connectionDialog = new Dialog(getActivity(), R.style.TransparentDialog);
            View customView = getActivity().getLayoutInflater().inflate(R.layout.menu_route_day, null);
            connectionDialog.setContentView(customView);
//            customView.findViewById(R.id.dialog_frame).setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    dismiss();
//                }
//            });
            customView.findViewById(R.id.add_view).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), AddPoiActivity.class);
                    intent.putParcelableArrayListExtra("locList", locList);
                    intent.putExtra("dayIndex", dayIndex);
                    intent.putParcelableArrayListExtra("poiList", mRouteDayMap.get(dayIndex));
                    getActivity().startActivityForResult(intent, RouteDayFragment.ADD_POI_REQUEST_CODE);
                    dismiss();

                }
            });

            customView.findViewById(R.id.delete).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    dismiss();
                }
            });
            return connectionDialog;
        }
    }


}

