package com.aizou.peachtravel.module.dest.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.aizou.core.widget.section.BaseSectionAdapter;
import com.aizou.peachtravel.R;
import com.aizou.peachtravel.base.PeachBaseFragment;
import com.aizou.peachtravel.bean.PoiDetailBean;
import com.aizou.peachtravel.bean.RouteDayDragBean;
import com.aizou.peachtravel.bean.StrategyBean;
import com.aizou.peachtravel.common.api.TravelApi;
import com.aizou.peachtravel.common.utils.UILUtils;
import com.aizou.peachtravel.common.widget.BlurDialogMenu.BlurDialogFragment;
import com.aizou.peachtravel.common.widget.dslv.DragSortController;
import com.aizou.peachtravel.common.widget.dslv.DragSortListView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Rjm on 2014/11/24.
 */
public class RouteDayFragment extends PeachBaseFragment {
    private ArrayList<StrategyBean.IndexPoi> itinerary;
    private int day;
    private HashMap<Integer, List<PoiDetailBean>> routeDayMap;
    @InjectView(R.id.edit_dslv)
    DragSortListView mEditDslv;
    RouteDayAdapter mRouteDayAdpater;
    @InjectView(R.id.edit_btn)
    Button mEditBtn;
    View addDayFooter;
    View lineLl;
    Button addDayBtn;




    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_route_day, container, false);
        addDayFooter = View.inflate(getActivity(),R.layout.footer_route_day_add_day,null);
        addDayBtn = (Button) addDayFooter.findViewById(R.id.btn_add_day);
        lineLl = addDayFooter.findViewById(R.id.ll_line);
        ButterKnife.inject(this, rootView);
        mEditDslv.addFooterView(addDayFooter);
        initData();
        return rootView;

    }
    private void resizeData(ArrayList<StrategyBean.IndexPoi> itinerary){
        routeDayMap = new HashMap<Integer, List<PoiDetailBean>>();
        for(int i=0;i< day;i++){
            routeDayMap.put(i,new ArrayList<PoiDetailBean>());
        }

        for(StrategyBean.IndexPoi indexPoi:itinerary){
            routeDayMap.get(indexPoi.dayIndex).add(indexPoi.poi);
        }

    }

    private void initData() {
        itinerary =getArguments().getParcelableArrayList("itinerary");
        day = getArguments().getInt("day");
        resizeData(itinerary);
        mRouteDayAdpater = new RouteDayAdapter();
        mEditDslv.setDropListener(mRouteDayAdpater);

        // make and set controller on dslv
        SectionController c = new SectionController(mEditDslv, mRouteDayAdpater);
        mEditDslv.setFloatViewManager(c);
        mEditDslv.setOnTouchListener(c);
        mEditDslv.setAdapter(mRouteDayAdpater);
        mEditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRouteDayAdpater.isEditableMode =!mRouteDayAdpater.isEditableMode;
                if(mRouteDayAdpater.isEditableMode){
                    lineLl.setVisibility(View.GONE);
                    addDayFooter.setVisibility(View.VISIBLE);
                }else{
                    lineLl.setVisibility(View.GONE);
                    addDayFooter.setVisibility(View.INVISIBLE);
                }
                mRouteDayAdpater.notifyDataSetChanged();
            }
        });
        addDayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private class SectionController extends DragSortController {
        public boolean isEditableMode;
        private int mPos;
        private RouteDayAdapter mAdapter;
        DragSortListView mDslv;

        public SectionController(DragSortListView dslv,
                                 RouteDayAdapter adapter) {
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
        public void onDragFloatView(View floatView, Point floatPoint,
                                    Point touchPoint) {
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
        public static final int SPOT=1;
        public static final int POI=2;
        public boolean isEditableMode;
        public RouteDayAdapter(){
            super();
        }

        @Override
        public int getItemViewType(int section, int position) {
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
        public View getView(int section, int position, View convertView, ViewGroup parent) {
            int type = getItemViewType(section,position);
            ItemViewHolder holder = null;
            if (convertView == null) {
                holder = new ItemViewHolder();
                switch (type) {
                    case SPOT:
                        convertView = View.inflate(getActivity(), R.layout.row_routeday_spot, null);
                        holder.lineLl = (LinearLayout) convertView.findViewById(R.id.ll_line);
                        holder.deleteIv = (ImageView) convertView.findViewById(R.id.delete_iv);
                        holder.dragHandleIv = (ImageView) convertView.findViewById(R.id.drag_handle);
                        holder.nearByTv = (TextView) convertView.findViewById(R.id.drag_nearby_tv);
                        holder.spotImageIv = (ImageView) convertView.findViewById(R.id.spot_image_iv);
                        holder.spotNameTv = (TextView) convertView.findViewById(R.id.spot_name_tv);
                        holder.spotCostTimeTv = (TextView) convertView.findViewById(R.id.spot_time_cost_tv);
                        break;
                    case POI:
                        convertView = View.inflate(getActivity(), R.layout.row_routeday_poi, null);
                        holder.lineLl = (LinearLayout) convertView.findViewById(R.id.ll_line);
                        holder.deleteIv = (ImageView) convertView.findViewById(R.id.delete_iv);
                        holder.dragHandleIv = (ImageView) convertView.findViewById(R.id.drag_handle);
                        holder.nearByTv = (TextView) convertView.findViewById(R.id.drag_nearby_tv);
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
            PoiDetailBean poiDetailBean = (PoiDetailBean) getItem(section,position);
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


                    break;
                case RouteDayDragBean.POI:
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
                    break;
            }
            return convertView;
        }

        @Override
        public View getHeaderView(int section, View convertView, ViewGroup parent) {
            HeaderViewHolder holder=null;
            if(convertView==null){
                holder = new HeaderViewHolder();
                convertView = View.inflate(getActivity(), R.layout.row_drag_div, null);
                holder.lineLl = (LinearLayout) convertView.findViewById(R.id.ll_line);
                holder.topLineVw = convertView.findViewById(R.id.line_top);
                holder.dayTv = (TextView) convertView.findViewById(R.id.tv_div);
                holder.menuIv = (ImageView) convertView.findViewById(R.id.iv_menu);
                convertView.setTag(holder);
            }else{
                holder = (HeaderViewHolder) convertView.getTag();
            }
            if(section==0){
                holder.topLineVw.setVisibility(View.INVISIBLE);
            }else{
                holder.topLineVw.setVisibility(View.VISIBLE);
            }
            List<PoiDetailBean> poiList = routeDayMap.get(section);
            holder.dayTv.setText("");
            holder.menuIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

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
            if (headerIndex == 1) {
                return true;
            }
            return false;
        }

        @Override
        public void drop(int from, int to) {
            int fromSection = getSection(from);
            int fromPostion = getPositionInSection(from);
            int toSection = getSection(to);
            int toPostion = getPositionInSection(to);
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
            public TextView nearByTv;
            public ImageView poiImageIv, spotImageIv;
            public TextView poiNameTv, spotNameTv;
            public TextView poiAddressTv, spotCostTimeTv;
            public TextView poiPriceTv;
            public RatingBar poiRating;


        }
        private class HeaderViewHolder{
            public LinearLayout lineLl;
            public View topLineVw;
            public TextView dayTv;
            public ImageView menuIv;
        }
    }




    public class DragAdapter extends BaseAdapter implements
            DragSortListView.DropListener {
        public Context context;
        public List<RouteDayDragBean> dragBeanList;
        public boolean isEditableMode;

        public DragAdapter(Context context) {
            this.context = context;
            dragBeanList = new ArrayList<RouteDayDragBean>();
            resizeData();

        }

        public void resizeData() {
            for (int i = 0; i < 10; i++) {
                RouteDayDragBean bean;
                if (i == 0 || i == 5) {
                    bean = new RouteDayDragBean();
                    bean.type = RouteDayDragBean.DIV;
                    bean.day = "DAY---";
                } else if (i == 2 || i == 6) {
                    bean = new RouteDayDragBean();
                    bean.type = RouteDayDragBean.POI;
                    bean.address = "海淀图书城";
                    bean.name = "3W咖啡厅";
                    bean.city = "北京";
                    bean.price = "100/人";
                    bean.rating = 3;
                    bean.image = "http://img0.bdstatic.com/img/image/shouye/mnqz-12246357954.jpg";
                } else {
                    bean = new RouteDayDragBean();
                    bean.type = RouteDayDragBean.SPOT;
                    bean.address = "天安门";
                    bean.name = "故宫";
                    bean.city = "北京";
                    bean.costTime = "半天";
                    bean.image = "http://c.hiphotos.baidu.com/image/h%3D360/sign=52092c3e710e0cf3bff748fd3a46f23d/adaf2edda3cc7cd94f4bb5f53a01213fb80e91bc.jpg";
                }
                dragBeanList.add(bean);
            }

        }


        @Override
        public int getCount() {
            return dragBeanList.size();
        }

        @Override
        public Object getItem(int position) {
            return dragBeanList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getViewTypeCount() {
            return 3;
        }

        @Override
        public int getItemViewType(int position) {
            return dragBeanList.get(position).type;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            int type = getItemViewType(position);
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                switch (type) {
                    case RouteDayDragBean.DIV:
                        convertView = View.inflate(context, R.layout.row_drag_div, null);
                        holder.lineLl = (LinearLayout) convertView.findViewById(R.id.ll_line);
                        holder.topLineVw = convertView.findViewById(R.id.line_top);
                        holder.dayTv = (TextView) convertView.findViewById(R.id.tv_div);
                        holder.menuIv = (ImageView) convertView.findViewById(R.id.iv_menu);
                        break;
                    case RouteDayDragBean.SPOT:
                        convertView = View.inflate(context, R.layout.row_routeday_spot, null);
                        holder.lineLl = (LinearLayout) convertView.findViewById(R.id.ll_line);
                        holder.deleteIv = (ImageView) convertView.findViewById(R.id.delete_iv);
                        holder.dragHandleIv = (ImageView) convertView.findViewById(R.id.drag_handle);
                        holder.nearByTv = (TextView) convertView.findViewById(R.id.drag_nearby_tv);
                        holder.spotImageIv = (ImageView) convertView.findViewById(R.id.spot_image_iv);
                        holder.spotNameTv = (TextView) convertView.findViewById(R.id.spot_name_tv);
                        holder.spotCostTimeTv = (TextView) convertView.findViewById(R.id.spot_time_cost_tv);
                        break;
                    case RouteDayDragBean.POI:
                        convertView = View.inflate(context, R.layout.row_routeday_poi, null);
                        holder.lineLl = (LinearLayout) convertView.findViewById(R.id.ll_line);
                        holder.deleteIv = (ImageView) convertView.findViewById(R.id.delete_iv);
                        holder.dragHandleIv = (ImageView) convertView.findViewById(R.id.drag_handle);
                        holder.nearByTv = (TextView) convertView.findViewById(R.id.drag_nearby_tv);
                        holder.poiImageIv = (ImageView) convertView.findViewById(R.id.poi_image_iv);
                        holder.poiNameTv = (TextView) convertView.findViewById(R.id.poi_name_tv);
                        holder.poiAddressTv = (TextView) convertView.findViewById(R.id.poi_address_tv);
                        holder.poiPriceTv = (TextView) convertView.findViewById(R.id.poi_price_tv);
                        holder.poiRating = (RatingBar) convertView.findViewById(R.id.poi_rating);
                        break;
                }
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            RouteDayDragBean dragBean = dragBeanList.get(position);
            if(isEditableMode){
                holder.lineLl.setVisibility(View.GONE);
            }else{
                holder.lineLl.setVisibility(View.VISIBLE);
            }
            switch (dragBean.type) {
                case RouteDayDragBean.DIV:
                    if(position==0){
                        holder.topLineVw.setVisibility(View.INVISIBLE);
                    }else{
                        holder.topLineVw.setVisibility(View.VISIBLE);
                    }
                    holder.dayTv.setText(dragBean.day);
                    holder.menuIv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    });
                    break;
                case RouteDayDragBean.SPOT:
                    ImageLoader.getInstance().displayImage(dragBean.image, holder.spotImageIv, UILUtils.getDefaultOption());
                    holder.spotNameTv.setText(dragBean.name);
                    holder.spotCostTimeTv.setText(dragBean.costTime);
                    if (isEditableMode) {
                        holder.deleteIv.setVisibility(View.VISIBLE);
                        holder.nearByTv.setVisibility(View.GONE);
                        holder.dragHandleIv.setVisibility(View.VISIBLE);
                        holder.deleteIv.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

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


                    break;
                case RouteDayDragBean.POI:
                    ImageLoader.getInstance().displayImage(dragBean.image, holder.poiImageIv, UILUtils.getDefaultOption());
                    holder.poiNameTv.setText(dragBean.name);
                    holder.poiAddressTv.setText(dragBean.address);
                    holder.poiRating.setRating(dragBean.rating);
                    holder.poiPriceTv.setText(dragBean.price);
                    if (isEditableMode) {
                        holder.deleteIv.setVisibility(View.VISIBLE);
                        holder.nearByTv.setVisibility(View.GONE);
                        holder.dragHandleIv.setVisibility(View.VISIBLE);
                        holder.deleteIv.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

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
                    break;
            }


            return convertView;
        }

        private class ViewHolder {
            public LinearLayout lineLl;
            public View topLineVw;
            public ImageView deleteIv, dragHandleIv;
            public TextView nearByTv;
            public ImageView poiImageIv, spotImageIv;
            public TextView poiNameTv, spotNameTv;
            public TextView poiAddressTv, spotCostTimeTv;
            public TextView poiPriceTv;
            public RatingBar poiRating;
            public TextView dayTv;
            public ImageView menuIv;

        }

        @Override
        public void drop(int from, int to) {
            RouteDayDragBean bean = dragBeanList.get(from);
            dragBeanList.remove(bean);
            dragBeanList.add(to, bean);
            checkDayIsNull();
            resizeDay();
            notifyDataSetChanged();

        }

        public void resizeDay() {
            int size = dragBeanList.size();
            int index = 1;
            for (int i = 0; i < size; i++) {
                RouteDayDragBean bean = dragBeanList.get(i);
                if (bean.type == RouteDayDragBean.DIV) {
                    bean.dayIndex = index;
                    index++;
                }
            }
        }
        public void addDay() {
            RouteDayDragBean itemBean = new RouteDayDragBean();
            itemBean.type = RouteDayDragBean.DIV;
            // itemBean.date = bean.day;
            dragBeanList.add(itemBean);
            resizeDay();
            notifyDataSetChanged();
        }

        private boolean checkDayIsNull() {
            int size = dragBeanList.size();
            for (int i = 0; i < size; i++) {
                RouteDayDragBean bean = dragBeanList.get(i);
                if (bean.type == RouteDayDragBean.DIV) {
                    if (i == size - 1) {
                        bean.isCanRemove = false;
                    } else {
                        RouteDayDragBean nextBean = dragBeanList
                                .get(i + 1);
                        if (nextBean.type == RouteDayDragBean.DIV&&size!=1) {
                            bean.isCanRemove = true;
                        } else {
                            bean.isCanRemove = false;
                        }
                    }

                } else if (bean.type == RouteDayDragBean.SPOT||bean.type==RouteDayDragBean.POI) {
                }
            }
            return false;
        }


        @Override
        public boolean areAllItemsEnabled() {
            return false;
        }

        @Override
        public boolean isEnabled(int position) {
            RouteDayDragBean bean = dragBeanList.get(position);
            if (bean.type == RouteDayDragBean.DIV) {
                return false;
            } else {
                return true;
            }
        }
    }

    public static class RouteDayMenu extends BlurDialogFragment {
        public int dayIndex;
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
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
                }
            });

            customView.findViewById(R.id.delete).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                }
            });
            return connectionDialog;
        }
    }
}
