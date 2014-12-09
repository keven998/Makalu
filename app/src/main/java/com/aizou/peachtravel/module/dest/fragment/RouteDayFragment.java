package com.aizou.peachtravel.module.dest.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.aizou.core.log.LogUtil;
import com.aizou.core.widget.section.BaseSectionAdapter;
import com.aizou.peachtravel.R;
import com.aizou.peachtravel.base.PeachBaseFragment;
import com.aizou.peachtravel.bean.LocBean;
import com.aizou.peachtravel.bean.PoiDetailBean;
import com.aizou.peachtravel.bean.StrategyBean;
import com.aizou.peachtravel.common.api.TravelApi;
import com.aizou.peachtravel.common.utils.UILUtils;
import com.aizou.peachtravel.common.widget.BlurDialogMenu.BlurDialogFragment;
import com.aizou.peachtravel.common.widget.BlurDialogMenu.SupportBlurDialogFragment;
import com.aizou.peachtravel.common.widget.dslv.DragSortController;
import com.aizou.peachtravel.common.widget.dslv.DragSortListView;
import com.aizou.peachtravel.module.dest.AddPoiActivity;
import com.aizou.peachtravel.module.dest.PoiDetailActivity;
import com.aizou.peachtravel.module.dest.SpotDetailActivity;
import com.lidroid.xutils.util.LogUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Rjm on 2014/11/24.
 */
public class RouteDayFragment extends PeachBaseFragment {
    public static final int ADD_POI_REQUEST_CODE=101;
    private ArrayList<StrategyBean.IndexPoi> itinerary;
    private int day;
    private ArrayList<ArrayList<PoiDetailBean>> routeDayMap;
    private ArrayList<LocBean> locList;
    @InjectView(R.id.edit_dslv)
    DragSortListView mEditDslv;
    RouteDayAdapter routeDayAdpater;
    @InjectView(R.id.edit_btn)
    CheckedTextView mEditBtn;
    View addDayFooter;
    View lineLl;
    Button addDayBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_route_guide, container, false);
        addDayFooter = View.inflate(getActivity(),R.layout.footer_route_day_add_day, null);
        addDayBtn = (Button) addDayFooter.findViewById(R.id.btn_add_day);
        lineLl = addDayFooter.findViewById(R.id.ll_line);
        ButterKnife.inject(this, rootView);
        mEditDslv.addFooterView(addDayFooter);
        initData();
        return rootView;
    }

    private void resizeData(ArrayList<StrategyBean.IndexPoi> itinerary){
        routeDayMap = new ArrayList<ArrayList<PoiDetailBean>>();
        for(int i=0;i< day;i++){
            routeDayMap.add(new ArrayList<PoiDetailBean>());
        }

        for(StrategyBean.IndexPoi indexPoi:itinerary){
            routeDayMap.get(indexPoi.dayIndex).add(indexPoi.poi);
        }

    }

    private void initData() {
        itinerary =getArguments().getParcelableArrayList("itinerary");
        locList = getArguments().getParcelableArrayList("locList");
        day = getArguments().getInt("day");
        resizeData(itinerary);
        routeDayAdpater = new RouteDayAdapter();
        mEditDslv.setDropListener(routeDayAdpater);

        // make and set controller on dslv
        SectionController c = new SectionController(mEditDslv, routeDayAdpater);
        mEditDslv.setFloatViewManager(c);
        mEditDslv.setOnTouchListener(c);
        mEditDslv.setAdapter(routeDayAdpater);
        mEditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                routeDayAdpater.isEditableMode =!routeDayAdpater.isEditableMode;
                if (routeDayAdpater.isEditableMode) {
                    mEditBtn.setChecked(true);
                    lineLl.setVisibility(View.GONE);
                    addDayFooter.setVisibility(View.VISIBLE);
                } else {
                    //todo:保存路线
                    mEditBtn.setChecked(false);
                    lineLl.setVisibility(View.GONE);
                    addDayFooter.setVisibility(View.INVISIBLE);
                }
                routeDayAdpater.notifyDataSetChanged();
            }
        });

        addDayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                routeDayMap.add(new ArrayList<PoiDetailBean>());
                routeDayAdpater.notifyDataSetChanged();
                mEditDslv.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mEditDslv.setSelection(routeDayAdpater.getCount() - 1);
                    }
                }, 50);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode== Activity.RESULT_OK){
            if(requestCode==ADD_POI_REQUEST_CODE){
               ArrayList<PoiDetailBean> poiList= data.getParcelableArrayListExtra("poiList");
               int dayIndex = data.getIntExtra("dayIndex",-1);
               routeDayMap.set(dayIndex, poiList);
               routeDayAdpater.notifyDataSetChanged();
            }
        }
    }

    private class SectionController extends DragSortController {
        private int mPos;
        private RouteDayAdapter mAdapter;
        DragSortListView mDslv;

        public SectionController(DragSortListView dslv, RouteDayAdapter adapter) {
            super(dslv, R.id.drag_handle, DragSortController.ON_DOWN, 0);
            setRemoveEnabled(false);
            mDslv = dslv;
            mAdapter = adapter;
        }


        @Override
        public View onCreateFloatView(int position) {
            mPos = position;

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
            View div = mDslv.getChildAt(0);
            if (mPos > 0) {
                // don't allow floating View to go above
                // section divider
                final int limit = div.getBottom() + lvDivHeight;
                if (floatPoint.y < limit) {
                    floatPoint.y = limit;
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
            DragSortListView.DropListener{
        public static final int SPOT=0;
        public static final int POI=1;
        public boolean isEditableMode;
        public RouteDayAdapter(){
            super();
        }

        @Override
        public int getContentItemViewType(int section, int position) {
            String type =routeDayMap.get(section).get(position).type;
            if(type.equals(TravelApi.PoiType.SPOT)){
                return SPOT ;
            }else{
                return POI ;
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
            return getGlobalPositionForItem(section,position);
        }

        @Override
        public View getItemView(final int section, int position, View convertView, ViewGroup parent) {
            int type = getContentItemViewType(section, position);
            LogUtils.d("item---section:"+section+"--postion:"+position+"--globle_postion"+getGlobalPositionForItem(section,position));
            ItemViewHolder holder = null;
            if (convertView == null) {
                holder = new ItemViewHolder();
                switch (type) {
                    case SPOT:
                        convertView = View.inflate(getActivity(), R.layout.row_routeday_spot, null);
                        holder.lineLl = (LinearLayout) convertView.findViewById(R.id.ll_line);
                        holder.deleteIv = (ImageView) convertView.findViewById(R.id.delete_iv);
                        holder.dragHandleIv = (ImageView) convertView.findViewById(R.id.drag_handle);
                        holder.nearByTv = (Button) convertView.findViewById(R.id.drag_nearby_btn);
                        holder.spotImageIv = (ImageView) convertView.findViewById(R.id.spot_image_iv);
                        holder.spotNameTv = (TextView) convertView.findViewById(R.id.spot_name_tv);
                        holder.spotCostTimeTv = (TextView) convertView.findViewById(R.id.spot_time_cost_tv);
                        break;
                    case POI:
                        convertView = View.inflate(getActivity(), R.layout.row_routeday_poi, null);
                        holder.lineLl = (LinearLayout) convertView.findViewById(R.id.ll_line);
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
            }
            else {
                holder = (ItemViewHolder) convertView.getTag();
            }
            final PoiDetailBean poiDetailBean = (PoiDetailBean) getItem(section,position);
            switch (type){
                case SPOT:
                    ImageLoader.getInstance().displayImage(poiDetailBean.images.get(0).url, holder.spotImageIv, UILUtils.getDefaultOption());
                    holder.spotNameTv.setText(poiDetailBean.zhName);
                    holder.spotCostTimeTv.setText(poiDetailBean.timeCostDesc);
                    if (isEditableMode) {
                        holder.deleteIv.setVisibility(View.VISIBLE);
                        holder.nearByTv.setVisibility(View.GONE);
                        holder.dragHandleIv.setVisibility(View.VISIBLE);
                        holder.deleteIv.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                new MaterialDialog.Builder(getActivity())

                                        .title(null)
                                        .content("确定删除")
                                        .theme(Theme.LIGHT)  // the default is light, so you don't need this line
                                        .positiveText("确定")
                                        .negativeText("取消")
                                        .callback(new MaterialDialog.Callback() {
                                            @Override
                                            public void onPositive(MaterialDialog dialog) {
                                                routeDayMap.get(section).remove(poiDetailBean);
                                                notifyDataSetChanged();
                                                dialog.dismiss();
                                            }

                                            @Override
                                            public void onNegative(MaterialDialog dialog) {
                                                dialog.dismiss();
                                            }
                                        })
                                        .show();
                            }
                        });
                    } else {
                        holder.deleteIv.setVisibility(View.GONE);
                        holder.nearByTv.setVisibility(View.VISIBLE);
                        holder.dragHandleIv.setVisibility(View.GONE);
                        holder.nearByTv.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                            }
                        });
                    }
                    convertView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getActivity(), SpotDetailActivity.class);
                            intent.putExtra("id",poiDetailBean.id);
                            startActivity(intent);
                        }
                    });

                    break;

                case POI:

                    ImageLoader.getInstance().displayImage(poiDetailBean.images.get(0).url, holder.poiImageIv, UILUtils.getDefaultOption());
                    holder.poiNameTv.setText(poiDetailBean.zhName);
                    holder.poiAddressTv.setText(poiDetailBean.address);
                    holder.poiRating.setRating(poiDetailBean.rating);

                    holder.poiPriceTv.setText(poiDetailBean.priceDesc);
                    if (isEditableMode) {
                        holder.deleteIv.setVisibility(View.VISIBLE);
                        holder.nearByTv.setVisibility(View.GONE);
                        holder.dragHandleIv.setVisibility(View.VISIBLE);
                        holder.deleteIv.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                new MaterialDialog.Builder(getActivity())
                                        .title(null)
                                        .content("确定删除")
                                        .theme(Theme.LIGHT)  // the default is light, so you don't need this line
                                        .positiveText("确定")
                                        .negativeText("取消")
                                        .callback(new MaterialDialog.Callback() {
                                            @Override
                                            public void onPositive(MaterialDialog dialog) {
                                                routeDayMap.get(section).remove(poiDetailBean);
                                                notifyDataSetChanged();
                                                dialog.dismiss();
                                            }

                                            @Override
                                            public void onNegative(MaterialDialog dialog) {
                                                dialog.dismiss();
                                            }
                                        })
                                        .show();
                            }
                        });
                    } else {
                        holder.deleteIv.setVisibility(View.GONE);
                        holder.nearByTv.setVisibility(View.VISIBLE);
                        holder.dragHandleIv.setVisibility(View.GONE);
                        holder.nearByTv.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        });
                    }
                    convertView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getActivity(), PoiDetailActivity.class);
                            intent.putExtra("id",poiDetailBean.id);
                            intent.putExtra("type",poiDetailBean.type);
                            startActivity(intent);
                        }
                    });
                    break;
            }
            if(isEditableMode){
                holder.lineLl.setVisibility(View.GONE);
            }else{
                holder.lineLl.setVisibility(View.VISIBLE);
            }
            return convertView;
        }

        @Override
        public View getHeaderView(final int section, View convertView, ViewGroup parent) {
            HeaderViewHolder holder=null;
            LogUtils.d("header---section:"+section+"--globle_postion"+getGlobalPositionForHeader(section));
            if(convertView==null){
                holder = new HeaderViewHolder();
                convertView = View.inflate(getActivity(), R.layout.row_drag_div, null);
                convertView.setTag(holder);
            }
            else{
                holder = (HeaderViewHolder) convertView.getTag();
            }
            holder.lineLl = (LinearLayout) convertView.findViewById(R.id.ll_line);
            holder.topLineVw = convertView.findViewById(R.id.line_top);
            holder.dayTv = (TextView) convertView.findViewById(R.id.tv_div);
            holder.menuIv = (ImageView) convertView.findViewById(R.id.iv_menu);
            holder.nullLl = (LinearLayout) convertView.findViewById(R.id.ll_null);
            if(isEditableMode){
                holder.lineLl.setVisibility(View.GONE);
            }else{
                holder.lineLl.setVisibility(View.VISIBLE);
            }
            if(section==0){
                holder.topLineVw.setVisibility(View.INVISIBLE);
            }else{
                holder.topLineVw.setVisibility(View.VISIBLE);
            }
            List<PoiDetailBean> poiList = routeDayMap.get(section);
            if(poiList.size()>0){
                holder.nullLl.setVisibility(View.GONE);
                LinkedHashSet<String> citySet = new LinkedHashSet<String>();
                for(PoiDetailBean detailBean:poiList){
                    if(detailBean.locList!=null&&detailBean.locList.size()>0){
                        citySet.add(detailBean.locList.get(detailBean.locList.size()-1).zhName);
                    }
                }

                holder.dayTv.setText("D" + (section+1) + "  " + citySet.toString());
            }else{
                holder.dayTv.setText("D" + (section+1) + "  未安排");
                if(isEditableMode){
                    holder.nullLl.setVisibility(View.GONE);
                }else{
                    holder.nullLl.setVisibility(View.VISIBLE);
                }

            }
            if(isEditableMode){
                holder.menuIv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        RouteDayMenu fragment = new RouteDayMenu();
                        fragment.setRouteDay(routeDayMap, routeDayAdpater);
                        Bundle args = new Bundle();
                        args.putInt(
                                SupportBlurDialogFragment.BUNDLE_KEY_BLUR_RADIUS,
                                4
                        );
                        args.putFloat(
                                SupportBlurDialogFragment.BUNDLE_KEY_DOWN_SCALE_FACTOR,
                                5
                        );
                        args.putInt("dayIndex", section);
                        args.putParcelableArrayList("locList", locList);

                        fragment.setArguments(args);
                        fragment.show(getActivity().getSupportFragmentManager(), "blur_menu");
                    }
                });
            }else{
                holder.menuIv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                });
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
            if(toPostion==-1){
                if(from>to){
                    toSection-=1;
                    toPostion = routeDayMap.get(toSection).size();
                }else{
                    toPostion+=1;
                }
            }
            List<PoiDetailBean> fromList = routeDayMap.get(fromSection);
            List<PoiDetailBean> toList = routeDayMap.get(toSection);
            PoiDetailBean bean = fromList.get(fromPostion);
            fromList.remove(bean);
            toList.add(toPostion,bean);

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
            public LinearLayout lineLl;
            public ImageView deleteIv, dragHandleIv;
            public Button nearByTv;
            public ImageView poiImageIv, spotImageIv;
            public TextView poiNameTv, spotNameTv;
            public TextView poiAddressTv, spotCostTimeTv;
            public TextView poiPriceTv;
            public RatingBar poiRating;
        }

        private class HeaderViewHolder {
            public LinearLayout lineLl;
            public LinearLayout nullLl;
            public View topLineVw;
            public TextView dayTv;
            public ImageView menuIv;
        }
    }

    public static class RouteDayMenu extends BlurDialogFragment {
        public int dayIndex;
        private ArrayList<LocBean> locList;
        private ArrayList<PoiDetailBean> poiList;
        private ArrayList< ArrayList<PoiDetailBean>> mRouteDayMap;
        private RouteDayAdapter mRouteDayAdapter;
        public RouteDayMenu(){
            super();
        }
        public void setRouteDay(ArrayList< ArrayList<PoiDetailBean>> RouteDayMap,RouteDayAdapter routeDayAdapter){
            mRouteDayMap =RouteDayMap;
            mRouteDayAdapter = routeDayAdapter;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            dayIndex = getArguments().getInt("dayIndex");
            locList = getArguments().getParcelableArrayList("locList");
            poiList= getArguments().getParcelableArrayList("poiList");
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
                    intent.putParcelableArrayListExtra("locList",locList);
                    intent.putExtra("dayIndex", dayIndex);
                    intent.putParcelableArrayListExtra("poiList", mRouteDayMap.get(dayIndex));
                    getActivity().startActivityForResult(intent, RouteDayFragment.ADD_POI_REQUEST_CODE);
                    dismiss();

                }
            });

            customView.findViewById(R.id.delete).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new MaterialDialog.Builder(getActivity())
                            .title(null)
                            .content("确定删除")
                            .theme(Theme.LIGHT)  // the default is light, so you don't need this line
                            .positiveText("确定")
                            .negativeText("取消")
                            .callback(new MaterialDialog.Callback() {
                                @Override
                                public void onPositive(MaterialDialog dialog) {
                                    mRouteDayMap.remove(dayIndex);
                                    mRouteDayAdapter.notifyDataSetChanged();
                                    dialog.dismiss();
                                }

                                @Override
                                public void onNegative(MaterialDialog dialog) {
                                    dialog.dismiss();
                                }
                            })
                            .show();
                    dismiss();
                }
            });
            return connectionDialog;
        }
    }


}

