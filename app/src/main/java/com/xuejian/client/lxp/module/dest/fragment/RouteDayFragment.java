package com.xuejian.client.lxp.module.dest.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aizou.core.log.LogUtil;
import com.aizou.core.widget.section.BaseSectionAdapter;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.PeachBaseFragment;
import com.xuejian.client.lxp.bean.IndexPoi;
import com.xuejian.client.lxp.bean.PoiDetailBean;
import com.xuejian.client.lxp.bean.StrategyBean;
import com.xuejian.client.lxp.common.account.AccountManager;
import com.xuejian.client.lxp.common.api.TravelApi;
import com.xuejian.client.lxp.common.dialog.MoreDialog;
import com.xuejian.client.lxp.common.dialog.PeachMessageDialog;
import com.xuejian.client.lxp.common.imageloader.UILUtils;
import com.xuejian.client.lxp.common.utils.AnimationSimple;
import com.xuejian.client.lxp.common.utils.IntentUtils;
import com.xuejian.client.lxp.common.widget.dslv.DragSortController;
import com.xuejian.client.lxp.common.widget.dslv.DragSortListView;
import com.xuejian.client.lxp.db.User;
import com.xuejian.client.lxp.module.dest.AddPoiActivity;
import com.xuejian.client.lxp.module.dest.OnStrategyModeChangeListener;
import com.xuejian.client.lxp.module.dest.StrategyActivity;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.techery.properratingbar.ProperRatingBar;

/**
 * Created by Rjm on 2014/11/24.
 */
public class RouteDayFragment extends PeachBaseFragment implements OnStrategyModeChangeListener {
    public static final int ADD_POI_REQUEST_CODE = 101;
    private ArrayList<ArrayList<PoiDetailBean>> routeDayMap;
    private OnStrategyModeChangeListener mOnEditModeChangeListener;
    @Bind(R.id.edit_dslv)
    DragSortListView mEditDslv;
    RouteDayAdapter routeDayAdpater;
    View addDayFooter;
    StrategyBean strategy;
    public boolean isInEditMode;
    User user;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_route_guide, container, false);
        addDayFooter = View.inflate(getActivity(), R.layout.footer_route_day_bottom, null);
        ButterKnife.bind(this, rootView);
        user = AccountManager.getInstance().getLoginAccount(getActivity());
        mEditDslv.addFooterView(addDayFooter);
        initData();
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onAttach(Activity activity) {
        try {
            mOnEditModeChangeListener = (OnStrategyModeChangeListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement On OnDestActionListener");
        }
        super.onAttach(activity);
    }

    private void resizeData(ArrayList<IndexPoi> itinerary) {
        StrategyBean strategyBean = getStrategy();
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

    private StrategyBean getStrategy() {
        return ((StrategyActivity) getActivity()).getStrategy();

    }

    private void initData() {
        strategy = getStrategy();
        resizeData(strategy.itinerary);
        routeDayAdpater = new RouteDayAdapter(isInEditMode);
        mEditDslv.setDropListener(routeDayAdpater);
        // make and set controller on dslv
        SectionController c = new SectionController(mEditDslv, routeDayAdpater);
        mEditDslv.setFloatViewManager(c);
        mEditDslv.setOnTouchListener(c);
        mEditDslv.setAdapter(routeDayAdpater);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onEditModeChange(boolean isInEdit) {
        this.isInEditMode = isInEdit;
        if (routeDayAdpater != null) {
            routeDayAdpater.setEditableMode(isInEdit);
            routeDayAdpater.notifyDataSetChanged();
        }

    }

    @Override
    public void onCopyStrategy() {
        strategy = getStrategy();
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
            View v = mAdapter.getView(position, null, mDslv);
            v.setBackgroundResource(R.drawable.bg_move_floatview);
            return v;
        }

        private int origHeight = -1;

        @Override
        public void onDragFloatView(View floatView, Point floatPoint, Point touchPoint) {
            final int lvDivHeight = mDslv.getDividerHeight();

            if (origHeight == -1) {
                origHeight = floatView.getHeight();
            }

            View div = mDslv.getChildAt(mDslv.getHeaderViewsCount());
            if (mPos > 0) {
                // don't allow floating View to go above
                // section divider
                if (mDslv.getFirstVisiblePosition() == 0) {
                    final int limit = div.getBottom() + lvDivHeight;
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
        private boolean isEditableMode;
        private DisplayImageOptions options;
        public boolean isAnimationEnd = true;

        public RouteDayAdapter(boolean isEditableMode) {
            super();
            this.isEditableMode = isEditableMode;
            options = UILUtils.getDefaultOption();
        }

        @Override
        public int getContentItemViewType(int section, int position) {
            String type = routeDayMap.get(section).get(position).type;
            int a = section;
            int b = position;
            if (type.equals(TravelApi.PeachType.SPOT)) {
                return SPOT;
            } else {
                return POI;
            }

        }


        public void setEditableMode(boolean mode) {
            isEditableMode = mode;
            isAnimationEnd = false;
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
        public View getItemView(final int section, final int position, View convertView, ViewGroup parent) {
            int type = getContentItemViewType(section, position);
            ItemViewHolder holder = null;
            if (convertView == null) {
                holder = new ItemViewHolder();
                switch (type) {
                    case SPOT:
                        convertView = View.inflate(getActivity(), R.layout.row_routeday_spot, null);
                        holder.contentRl = (RelativeLayout) convertView.findViewById(R.id.rl_content);
                        holder.deleteIv = (ImageView) convertView.findViewById(R.id.delete_iv);
                        holder.dragHandleIv = (ImageView) convertView.findViewById(R.id.drag_handle);
                        holder.spotImageIv = (ImageView) convertView.findViewById(R.id.spot_image_iv);
                        holder.spotNameTv = (TextView) convertView.findViewById(R.id.spot_name_tv);
                        holder.spotCostTimeTv = (TextView) convertView.findViewById(R.id.spot_time_cost_tv);
                        holder.rankTv = (TextView) convertView.findViewById(R.id.spot_rank_tv);

                        convertView.setTag(holder);
                        break;
                    case POI:
                        convertView = View.inflate(getActivity(), R.layout.row_routeday_poi, null);
                        holder.contentRl = (RelativeLayout) convertView.findViewById(R.id.rl_content);
                        holder.deleteIv = (ImageView) convertView.findViewById(R.id.delete_iv);
                        holder.dragHandleIv = (ImageView) convertView.findViewById(R.id.drag_handle);
                        holder.poiImageIv = (ImageView) convertView.findViewById(R.id.poi_image_iv);
                        holder.poiNameTv = (TextView) convertView.findViewById(R.id.poi_name_tv);
                        holder.poiAddressTv = (TextView) convertView.findViewById(R.id.poi_address_tv);
                        holder.poiPriceTv = (TextView) convertView.findViewById(R.id.poi_price_tv);
                        holder.poiRating = (ProperRatingBar) convertView.findViewById(R.id.poi_rating);

                        convertView.setTag(holder);
                        break;
                }
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
                    holder.spotCostTimeTv.setText("参考游玩  " + poiDetailBean.timeCostDesc);
                    if (!poiDetailBean.getFormatRank().equals("0")) {
                        holder.rankTv.setText("景点排名 " + poiDetailBean.getFormatRank());
                    } else {
                        holder.rankTv.setText("暂无排名");
                    }

                    if (!isAnimationEnd && isEditableMode) {
                        Animation animation = AnimationSimple.expand(holder.deleteIv);
                        animation.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                isAnimationEnd = true;
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });
                        holder.deleteIv.startAnimation(animation);
                        animation = AnimationSimple.expand(holder.dragHandleIv);
                        holder.dragHandleIv.startAnimation(animation);
                        holder.deleteIv.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                final PeachMessageDialog deleteDialog = new PeachMessageDialog(getActivity());
                                deleteDialog.setTitle("提示");
                                deleteDialog.setMessage("确定删除");
                                deleteDialog.setPositiveButton("确定", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        LogUtil.d("执行");
                                        deleteDialog.dismiss();
                                        delectWithAnim(section, mEditDslv.getChildAt(getCurrentItemPosition(section - 1, position)), poiDetailBean);
                                        /*routeDayMap.get(section).remove(poiDetailBean);
                                        notifyDataSetChanged();
                                        deleteDialog.dismiss();*/
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
                    } else if (isEditableMode) {
                        holder.deleteIv.setVisibility(View.VISIBLE);
                        holder.dragHandleIv.setVisibility(View.VISIBLE);

                        holder.deleteIv.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                final PeachMessageDialog deleteDialog = new PeachMessageDialog(getActivity());
                                deleteDialog.setTitle("提示");
                                deleteDialog.setMessage("确定删除");
                                deleteDialog.setPositiveButton("确定", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        deleteDialog.dismiss();
                                        delectWithAnim(section, mEditDslv.getChildAt(getCurrentItemPosition(section - 1, position)), poiDetailBean);
                                        /*routeDayMap.get(section).remove(poiDetailBean);
                                        notifyDataSetChanged();
                                        deleteDialog.dismiss();*/
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
                        holder.dragHandleIv.setVisibility(View.GONE);
                    }

                    holder.contentRl.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            IntentUtils.intentToDetail(getActivity(), TravelApi.PeachType.SPOT, poiDetailBean.id);

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
                    holder.poiRating.setRating((int)poiDetailBean.getRating());
                    String typeName = "";
                    if (TravelApi.PeachType.RESTAURANTS.equals(poiDetailBean.type)) {
                        typeName = "美食";
                    } else if (TravelApi.PeachType.SHOPPING.equals(poiDetailBean.type)) {
                        typeName = "购物";
                    } else if (TravelApi.PeachType.HOTEL.equals(poiDetailBean.type)) {
                        typeName = "酒店";
                    }
                    if (!poiDetailBean.getFormatRank().equals("0")) {
                        holder.poiPriceTv.setText(typeName + "排名 " + poiDetailBean.getFormatRank());
                    } else {
                        holder.poiPriceTv.setText("");
                    }

                    if (!isAnimationEnd && isEditableMode) {
                        Animation animation = AnimationSimple.expand(holder.deleteIv);
                        animation.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                isAnimationEnd = true;
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });
                        holder.deleteIv.startAnimation(animation);
                        animation = AnimationSimple.expand(holder.dragHandleIv);
                        holder.dragHandleIv.startAnimation(animation);
                        holder.deleteIv.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                final PeachMessageDialog deleteDialog = new PeachMessageDialog(getActivity());
                                deleteDialog.setTitle("提示");
                                deleteDialog.setMessage("确定删除？");
                                deleteDialog.setPositiveButton("确定", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        deleteDialog.dismiss();
                                        delectWithAnim(section, mEditDslv.getChildAt(getCurrentItemPosition(section - 1, position)), poiDetailBean);
                                        /*routeDayMap.get(section).remove(poiDetailBean);
                                        notifyDataSetChanged();
                                        deleteDialog.dismiss();*/
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
                    } else if (isEditableMode) {
                        holder.deleteIv.setVisibility(View.VISIBLE);
                        holder.dragHandleIv.setVisibility(View.VISIBLE);
                    } else {
                        holder.deleteIv.setVisibility(View.GONE);
                        holder.dragHandleIv.setVisibility(View.GONE);
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
            holder.dayTv = (TextView) convertView.findViewById(R.id.tv_day_index);
           /* holder.addPoiIv = (Button) convertView.findViewById(R.id.iv_add_poi);
            holder.deleteDayIv = (ImageView) convertView.findViewById(R.id.iv_delete_day);*/
            holder.citysTv = (TextView) convertView.findViewById(R.id.tv_loc_list);
            holder.doMore = (ImageView) convertView.findViewById(R.id.day_location);

            if (user == null) {
                holder.doMore.setVisibility(View.GONE);
            } else {
                if (user.getUserId() != strategy.userId) {
                    holder.doMore.setVisibility(View.GONE);
                }
            }

            holder.dayTv.setText("第" + (section + 1) + "天");
            if (section == 0) {
                convertView.setPadding(0, 0, 0, 0);
            } else {
                convertView.setPadding(0, 20, 0, 0);
            }

            holder.doMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showMoreDialog(section);
                }
            });

            List<PoiDetailBean> poiList = routeDayMap.get(section);
            if (poiList.size() > 0) {
                LinkedHashSet<String> citySet = new LinkedHashSet<String>();
                for (PoiDetailBean detailBean : poiList) {
                    if (detailBean.locality != null) {
                        citySet.add(detailBean.locality.zhName);
                    }
                }

                if (!citySet.isEmpty()) {
                    StringBuffer des = new StringBuffer();
                    for (String str : citySet) {
                        des.append(" " + str);
                    }
                    holder.citysTv.setText(des);
                    holder.citysTv.setTextColor(getResources().getColor(R.color.second_font_color));
                } else {
                    holder.citysTv.setText("");
                }
            } else {
                holder.citysTv.setTextColor(getResources().getColor(R.color.second_font_color));
                holder.citysTv.setText("没有安排");
            }

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

        private class ItemViewHolder {
            public RelativeLayout contentRl;
            public ImageView deleteIv, dragHandleIv;
            public ImageView poiImageIv, spotImageIv;
            public TextView rankTv;
            public TextView poiNameTv, spotNameTv;
            public TextView poiAddressTv, spotCostTimeTv;
            public TextView poiPriceTv;
            public ProperRatingBar poiRating;
        }

        private class HeaderViewHolder {
            public TextView citysTv;
            public TextView dayTv;
            public ImageView deleteDayIv;
            public Button addPoiIv;
            public ImageView doMore;
        }
    }

    public void showMoreDialog(final int section) {
        String[] names = {"添加行程", "加一天在前面", "加一天在后面", "删除这一天"};
        final MoreDialog dialog = new MoreDialog(getActivity());
        dialog.setMoreStyle(true, 4, names);
        dialog.setTitle("第" + (section + 1) + "天");
        dialog.setMessage("请选择你要进行的操作");
        dialog.getTv4().setTextColor(getResources().getColor(R.color.base_color_red));
        //添加一天在前面
        dialog.getTv2().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                addNewDayRouter(section, true);
            }
        });

        //添加一天在后面
        dialog.getTv3().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                addNewDayRouter(section, false);
            }
        });

        //添加行程
        dialog.getTv1().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent intent = new Intent(getActivity(), AddPoiActivity.class);
                intent.putParcelableArrayListExtra("locList", strategy.localities);
                intent.putExtra("dayIndex", section);
                intent.putParcelableArrayListExtra("poiList", routeDayMap.get(section));
                getActivity().startActivityForResult(intent, RouteDayFragment.ADD_POI_REQUEST_CODE);
                if (mOnEditModeChangeListener != null) {
                    if (!isInEditMode) {
                        isInEditMode = true;
                        mOnEditModeChangeListener.onEditModeChange(false);
                        routeDayAdpater.setEditableMode(false);
                        routeDayAdpater.notifyDataSetChanged();
                    }
                }
            }
        });

        //删除这一天
        dialog.getTv4().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                final PeachMessageDialog deleteDialog = new PeachMessageDialog(getActivity());
                deleteDialog.setTitle("提示");
                deleteDialog.setMessage("删除这天安排");
                deleteDialog.setPositiveButton("确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        routeDayMap.remove(section);
                        strategy.itineraryDays--;
                        routeDayAdpater.notifyDataSetChanged();
                        if (mOnEditModeChangeListener != null) {
                            if (!isInEditMode) {
                                isInEditMode = true;
                                mOnEditModeChangeListener.onEditModeChange(false);
                                routeDayAdpater.setEditableMode(false);
                                routeDayAdpater.notifyDataSetChanged();
                            }
                        }
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
        dialog.show();
    }

    public int getCurrentItemPosition(int section, int position) {
        int sum = 0;
        for (int i = 0; i <= section; i++) {
            sum += (routeDayMap.get(i).size() + 1);
        }
        sum += (position + 1);
        sum -= mEditDslv.getFirstVisiblePosition();
        return sum;
    }


    public void delectWithAnim(final int section, final View mItem, final PoiDetailBean poiDetailBean) {

        Animation.AnimationListener al = new Animation.AnimationListener() {
            boolean flag = true;

            @Override
            public void onAnimationEnd(Animation arg0) {
                if (flag) {
                    routeDayMap.get(section).remove(poiDetailBean);
                    routeDayAdpater.notifyDataSetChanged();
                    flag = false;
                    arg0.cancel();
                }
                //
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationStart(Animation animation) {
            }
        };

        Animation Animation = AnimationUtils.loadAnimation(mItem.getContext(), R.anim.slide_out_to_right);
        if (al != null) {
            Animation.setAnimationListener(al);
        }
        mItem.startAnimation(Animation);
    }

    public void addNewDayRouter(int position, boolean isBefore) {
        final int sectionPos;
        if (isBefore) {
            routeDayMap.add(position, new ArrayList<PoiDetailBean>());
            sectionPos = position;
        } else {
            routeDayMap.add(position + 1, new ArrayList<PoiDetailBean>());
            sectionPos = position + 1;
        }
        strategy.itineraryDays++;
        routeDayAdpater.notifyDataSetChanged();
        if (mOnEditModeChangeListener != null) {
            if (!isInEditMode) {
                isInEditMode = true;
                mOnEditModeChangeListener.onEditModeChange(false);
                routeDayAdpater.setEditableMode(false);
                routeDayAdpater.notifyDataSetChanged();
            }
        }
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
}

