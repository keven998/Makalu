package com.aizou.peachtravel.module.dest.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.aizou.peachtravel.common.dialog.DialogManager;
import com.aizou.core.dialog.ToastUtil;
import com.aizou.core.http.HttpCallBack;
import com.aizou.peachtravel.R;
import com.aizou.peachtravel.base.PeachBaseFragment;
import com.aizou.peachtravel.bean.ModifyResult;
import com.aizou.peachtravel.bean.PoiDetailBean;
import com.aizou.peachtravel.bean.StrategyBean;
import com.aizou.peachtravel.common.account.StrategyManager;
import com.aizou.peachtravel.common.api.TravelApi;
import com.aizou.peachtravel.common.gson.CommonJson;
import com.aizou.peachtravel.common.imageloader.UILUtils;
import com.aizou.peachtravel.common.widget.dslv.DragSortController;
import com.aizou.peachtravel.common.widget.dslv.DragSortListView;
import com.aizou.peachtravel.module.dest.PoiDetailActivity;
import com.aizou.peachtravel.module.dest.PoiListActivity;
import com.aizou.peachtravel.module.dest.StrategyActivity;
import com.nostra13.universalimageloader.core.ImageLoader;

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
    CheckedTextView mEditBtn;
    View addFooter;
    View lineLl;
    Button addBtn;
    RestAdapter mRestAdapter;
    StrategyBean strategy;
    boolean canEdit;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_rest_guide, container, false);
        addFooter = View.inflate(getActivity(), R.layout.footer_route_day_add_day, null);
        addBtn = (Button) addFooter.findViewById(R.id.btn_add_day);
        addBtn.setText("收藏想尝的美食");
        lineLl = addFooter.findViewById(R.id.ll_line);
        ButterKnife.inject(this, rootView);
        mEditDslv.addFooterView(addFooter);
        initData();
        return rootView;
    }

    private void initData() {
        strategy = getArguments().getParcelable("strategy");
        canEdit = getArguments().getBoolean("canEdit");
        DragSortController controller = new DragSortController(mEditDslv);
        controller.setDragHandleId(R.id.drag_handle);
        controller.setBackgroundColor(Color.TRANSPARENT);
        controller.setRemoveEnabled(false);
        mRestAdapter = new RestAdapter();

        final DragSortListView listView = mEditDslv;
        View view = new View(getActivity());
        view.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, getResources().getDimensionPixelSize(R.dimen.panel_margin)));
        listView.addHeaderView(view);
        listView.setFloatViewManager(controller);
        listView.setOnTouchListener(controller);
        listView.setDropListener(mRestAdapter);
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
                if (i == SCROLL_STATE_IDLE) {
                    if (absListView.getFirstVisiblePosition() <= 1) {
                        ((StrategyActivity) getActivity()).setRVVisiable(true);
                    } else {
                        ((StrategyActivity) getActivity()).setRVVisiable(false);
                    }
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int i2, int i3) {
                if (firstVisibleItem <= 1) {
                    ((StrategyActivity)getActivity()).setRVVisiable(true);
                } else {
                    ((StrategyActivity)getActivity()).setRVVisiable(false);
                }
            }
        });
        listView.setAdapter(mRestAdapter);

        if (canEdit) {
            mEditBtn.setVisibility(View.VISIBLE);
            mEditBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mRestAdapter.isEditableMode =!mRestAdapter.isEditableMode;
                    if(mRestAdapter.isEditableMode){
                        mEditBtn.setChecked(true);
                        addFooter.setVisibility(View.VISIBLE);
                    }else{
                        //todo: need to 保存路线
                        DialogManager.getInstance().showLoadingDialog(getActivity());
                        TravelApi.saveGuide(strategy.id, StrategyManager.getSaveRestaurantJson(getActivity(),strategy),new HttpCallBack<String>() {
                            @Override
                            public void doSucess(String result, String method) {
                                DialogManager.getInstance().dissMissLoadingDialog();
                                CommonJson<ModifyResult> saveResult= CommonJson.fromJson(result,ModifyResult.class);
                                if(saveResult.code==0){
                                    ToastUtil.getInstance(getActivity()).showToast("保存成功");
                                    mEditBtn.setChecked(false);
                                    addFooter.setVisibility(View.INVISIBLE);
                                }
                            }

                            @Override
                            public void doFailure(Exception error, String msg, String method) {
                                DialogManager.getInstance().dissMissLoadingDialog();
//                                ToastUtil.getInstance(getActivity()).showToast("保存失败");
                                ToastUtil.getInstance(getActivity()).showToast(getResources().getString(R.string.request_network_failed));
                            }
                        });

                    }
                    mRestAdapter.notifyDataSetChanged();
                }
            });
            if (mRestAdapter.getCount() == 0) {
                mEditBtn.performClick();
            }
        } else {
            mEditBtn.setVisibility(View.GONE);
        }

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PoiListActivity.class);
                intent.putExtra("type", TravelApi.PeachType.RESTAURANTS);
                intent.putExtra("canAdd", true);
                intent.putParcelableArrayListExtra("locList", strategy.localities);
                intent.putParcelableArrayListExtra("poiList", strategy.restaurant);
                getActivity().startActivityForResult(intent, ADD_REST_REQUEST_CODE);
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode== Activity.RESULT_OK){
            if(requestCode==ADD_REST_REQUEST_CODE){
                strategy.restaurant = data.getParcelableArrayListExtra("poiList");
                mRestAdapter.notifyDataSetChanged();
            }
        }
    }

    public class RestAdapter extends BaseAdapter implements
            DragSortListView.DropListener {
        public boolean isEditableMode;

        @Override
        public int getCount() {
            return strategy.restaurant.size();
        }

        @Override
        public Object getItem(int position) {
            return strategy.restaurant.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final PoiDetailBean poiDetailBean = strategy.restaurant.get(position);
            ItemViewHolder holder;
            if (convertView == null) {
                holder = new ItemViewHolder();
                convertView = View.inflate(getActivity(), R.layout.row_list_poi, null);
                holder.contentRl = (RelativeLayout) convertView.findViewById(R.id.rl_content);
                holder.deleteIv = (ImageView) convertView.findViewById(R.id.delete_iv);
                holder.dragHandleIv = (ImageView) convertView.findViewById(R.id.drag_handle);
                holder.nearByTv = (ImageButton) convertView.findViewById(R.id.drag_nearby_tv);
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
                        new MaterialDialog.Builder(getActivity())

                                .title(null)
                                .content("确定删除")
                                .theme(Theme.LIGHT)  // the default is light, so you don't need this line
                                .positiveText("确定")
                                .negativeText("取消")
                                .callback(new MaterialDialog.Callback() {
                                    @Override
                                    public void onPositive(MaterialDialog dialog) {
                                        strategy.restaurant.remove(poiDetailBean);
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
            holder.contentRl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), PoiDetailActivity.class);
                    intent.putExtra("id",poiDetailBean.id);
                    intent.putExtra("type",poiDetailBean.type);
                    getActivity().startActivity(intent);
                }
            });
            return convertView;
        }

        @Override
        public void drop(int from, int to) {
            if (from != to) {
                PoiDetailBean item = (PoiDetailBean) getItem(from);
                strategy.restaurant.remove(item);
                strategy.restaurant.add(to, item);
                notifyDataSetChanged();
            }
        }

        private class ItemViewHolder {
            public RelativeLayout contentRl;
            public ImageView deleteIv, dragHandleIv;
            public ImageButton nearByTv;
            public ImageView poiImageIv;
            public TextView poiNameTv;
            public TextView poiAddressTv;
            public TextView poiPriceTv;
            public RatingBar poiRating;
        }
    }
}
