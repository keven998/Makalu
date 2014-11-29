package com.aizou.peachtravel.module.dest.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.aizou.peachtravel.R;
import com.aizou.peachtravel.base.PeachBaseFragment;
import com.aizou.peachtravel.bean.LocBean;
import com.aizou.peachtravel.bean.PoiDetailBean;
import com.aizou.peachtravel.common.api.TravelApi;
import com.aizou.peachtravel.common.utils.UILUtils;
import com.aizou.peachtravel.common.widget.dslv.DragSortController;
import com.aizou.peachtravel.common.widget.dslv.DragSortListView;
import com.aizou.peachtravel.module.dest.AddPoiActivity;
import com.aizou.peachtravel.module.dest.PoiListActivity;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Rjm on 2014/11/29.
 */
public class RestaurantFragment extends PeachBaseFragment {

    public final static int ADD_REST_REQUEST_CODE=102;

    @InjectView(R.id.edit_dslv)
    DragSortListView mEditDslv;
    @InjectView(R.id.edit_btn)
    Button mEditBtn;
    View addFooter;
    View lineLl;
    Button addBtn;
    RestAdapter mRestAdapter;
    ArrayList<PoiDetailBean> restaurantList;
    ArrayList<LocBean> locList;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_route_guide, container, false);
        addFooter = View.inflate(getActivity(), R.layout.footer_route_day_add_day, null);
        addBtn = (Button) addFooter.findViewById(R.id.btn_add_day);
        lineLl = addFooter.findViewById(R.id.ll_line);
        ButterKnife.inject(this, rootView);
        mEditDslv.addFooterView(addFooter);
        initData();
        return rootView;
    }

    private void initData() {
        restaurantList = getArguments().getParcelableArrayList("restaurant");
        locList = getArguments().getParcelableArrayList("locList");
        DragSortController controller = new DragSortController(mEditDslv);
        controller.setDragHandleId(R.id.drag_handle);
        controller.setRemoveEnabled(false);
        mRestAdapter = new RestAdapter();
        mEditDslv.setFloatViewManager(controller);
        mEditDslv.setOnTouchListener(controller);
        mEditDslv.setDropListener(mRestAdapter);
        mEditDslv.setAdapter(mRestAdapter);
        mEditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRestAdapter.isEditableMode =!mRestAdapter.isEditableMode;
                if(mRestAdapter.isEditableMode){
                    addFooter.setVisibility(View.VISIBLE);
                }else{
                    addFooter.setVisibility(View.INVISIBLE);
                }
                mRestAdapter.notifyDataSetChanged();
            }
        });
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PoiListActivity.class);
                intent.putExtra("type", TravelApi.PoiType.RESTAURANTS);
                intent.putParcelableArrayListExtra("locList", locList);
                intent.putParcelableArrayListExtra("poiList",restaurantList);
                getActivity().startActivityForResult(intent, ADD_REST_REQUEST_CODE);
            }
        });



    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode== Activity.RESULT_OK){
            if(requestCode==ADD_REST_REQUEST_CODE){

            }
        }
    }

    public class RestAdapter extends BaseAdapter implements
            DragSortListView.DropListener {
        public boolean isEditableMode;

        @Override
        public int getCount() {
            return restaurantList.size();
        }

        @Override
        public Object getItem(int position) {
            return restaurantList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final PoiDetailBean poiDetailBean = restaurantList.get(position);
            ItemViewHolder holder;
            if (convertView == null) {
                holder = new ItemViewHolder();
                convertView = View.inflate(getActivity(), R.layout.row_list_poi, null);
                holder.deleteIv = (ImageView) convertView.findViewById(R.id.delete_iv);
                holder.dragHandleIv = (ImageView) convertView.findViewById(R.id.drag_handle);
                holder.nearByTv = (TextView) convertView.findViewById(R.id.drag_nearby_tv);
                holder.poiImageIv = (ImageView) convertView.findViewById(R.id.poi_image_iv);
                holder.poiNameTv = (TextView) convertView.findViewById(R.id.poi_name_tv);
                holder.poiAddressTv = (TextView) convertView.findViewById(R.id.poi_address_tv);
                holder.poiPriceTv = (TextView) convertView.findViewById(R.id.poi_price_tv);
                holder.poiRating = (RatingBar) convertView.findViewById(R.id.poi_rating);
                convertView.setTag(holder);
            }else{
                holder = (ItemViewHolder) convertView.getTag();
            }
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
                        restaurantList.remove(poiDetailBean);
                        notifyDataSetChanged();
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
            return convertView;
        }

        @Override
        public void drop(int from, int to) {
            if (from != to) {
                PoiDetailBean item = (PoiDetailBean) getItem(from);
                restaurantList.remove(item);
                restaurantList.add(to, item);
                notifyDataSetChanged();
            }
        }

        private class ItemViewHolder {
            public ImageView deleteIv, dragHandleIv;
            public TextView nearByTv;
            public ImageView poiImageIv;
            public TextView poiNameTv;
            public TextView poiAddressTv;
            public TextView poiPriceTv;
            public RatingBar poiRating;
        }
    }
}
