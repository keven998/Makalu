package com.aizou.peachtravel.module.dest.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aizou.core.utils.LocalDisplay;
import com.aizou.core.dialog.ToastUtil;
import com.aizou.peachtravel.R;
import com.aizou.peachtravel.base.PeachBaseFragment;
import com.aizou.peachtravel.bean.PoiDetailBean;
import com.aizou.peachtravel.bean.StrategyBean;
import com.aizou.peachtravel.common.api.TravelApi;
import com.aizou.peachtravel.common.dialog.PeachMessageDialog;
import com.aizou.peachtravel.common.imageloader.UILUtils;
import com.aizou.peachtravel.common.utils.AnimationSimple;
import com.aizou.peachtravel.common.utils.CommonUtils;
import com.aizou.peachtravel.common.utils.IntentUtils;
import com.aizou.peachtravel.common.widget.dslv.DragSortController;
import com.aizou.peachtravel.common.widget.dslv.DragSortListView;
import com.aizou.peachtravel.module.dest.OnEditModeChangeListener;
import com.aizou.peachtravel.module.dest.PoiListActivity;
import com.aizou.peachtravel.module.dest.StrategyActivity;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Rjm on 2014/11/29.
 */
public class RestaurantFragment extends PeachBaseFragment implements OnEditModeChangeListener {

    public final static int ADD_REST_REQUEST_CODE=102;
    private OnEditModeChangeListener mOnEditModeChangeListener;
    @InjectView(R.id.edit_dslv)
    DragSortListView mEditDslv;
    View addFooter;
    Button addBtn;
    RestAdapter mRestAdapter;
    boolean isInEditMode;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_rest_guide, container, false);
        addFooter = View.inflate(getActivity(), R.layout.footer_route_day_add_day, null);
        addBtn = (Button) addFooter.findViewById(R.id.btn_add_day);
        addBtn.setText("收集美食");
        ButterKnife.inject(this, rootView);
        mEditDslv.addFooterView(addFooter);
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

    public boolean  isEditableMode(){
        if(mRestAdapter!=null){
            return mRestAdapter.isEditableMode;
        }
        return false;
    }
    private StrategyBean getStrategy() {
        return ((StrategyActivity) getActivity()).getStrategy();

    }

    private void initData() {
        final StrategyBean strategyBean = getStrategy();
        DragSortController controller = new DragSortController(mEditDslv);
        controller.setDragHandleId(R.id.drag_handle);
        controller.setBackgroundColor(Color.TRANSPARENT);
        controller.setRemoveEnabled(false);
        mRestAdapter = new RestAdapter(isInEditMode);
        final DragSortListView listView = mEditDslv;
        View view = new View(getActivity());
        view.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, LocalDisplay.dp2px(10)));
        listView.addHeaderView(view);
        listView.setFloatViewManager(controller);
        listView.setOnTouchListener(controller);
        listView.setDropListener(mRestAdapter);
        listView.setAdapter(mRestAdapter);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mOnEditModeChangeListener!=null){
                    if(!isInEditMode){
                        isInEditMode = true;
                        mRestAdapter.setEditableMode(true);
                        mRestAdapter.notifyDataSetChanged();
                        mOnEditModeChangeListener.onEditModeChange(true);
                    }
                }
                Intent intent = new Intent(getActivity(), PoiListActivity.class);
                intent.putExtra("type", TravelApi.PeachType.RESTAURANTS);
                intent.putExtra("canAdd", true);
                intent.putParcelableArrayListExtra("locList", strategyBean.localities);
                intent.putParcelableArrayListExtra("poiList", strategyBean.restaurant);
                getActivity().startActivityForResult(intent, ADD_REST_REQUEST_CODE);
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode== Activity.RESULT_OK){
            if(requestCode==ADD_REST_REQUEST_CODE){
                StrategyBean strategyBean = getStrategy();
                strategyBean.restaurant = data.getParcelableArrayListExtra("poiList");
                mRestAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onEditModeChange(boolean isInEdit) {
        this.isInEditMode = isInEdit;
        if (mRestAdapter != null) {
            mRestAdapter.setEditableMode(isInEdit);
            mRestAdapter.notifyDataSetChanged();
        }

    }

    public class RestAdapter extends BaseAdapter implements
            DragSortListView.DropListener {
        private boolean isEditableMode;
        private DisplayImageOptions picOptions;
        private boolean isAnimationEnd=true;
        private StrategyBean strategy;

        public RestAdapter(boolean isEditableMode) {
            this.isEditableMode = isEditableMode;
            picOptions = UILUtils.getDefaultOption();
            strategy = getStrategy();
        }

        public void setEditableMode(boolean mode){
            isAnimationEnd = false;
            isEditableMode = mode;
        }

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
            final ItemViewHolder holder;
            if (convertView == null) {
                holder = new ItemViewHolder();
                convertView = View.inflate(getActivity(), R.layout.row_list_poi, null);
                holder.contentRl = (RelativeLayout) convertView.findViewById(R.id.rl_content);
                holder.deleteIv = (ImageView) convertView.findViewById(R.id.delete_iv);
                holder.dragHandleIv = (ImageView) convertView.findViewById(R.id.drag_handle);
                holder.nearByTv = (TextView) convertView.findViewById(R.id.drag_nearby_tv);
                holder.poiImageIv = (ImageView) convertView.findViewById(R.id.poi_image_iv);
                holder.poiNameTv = (TextView) convertView.findViewById(R.id.poi_name_tv);
                holder.poiAddressTv = (TextView) convertView.findViewById(R.id.poi_address_tv);
                holder.poiPriceTv = (TextView) convertView.findViewById(R.id.poi_price_tv);
                holder.poiRating = (RatingBar) convertView.findViewById(R.id.poi_rating);
                holder.poiRankTv = (TextView) convertView.findViewById(R.id.poi_rank_tv);
                convertView.setTag(holder);
            } else {
                holder = (ItemViewHolder) convertView.getTag();
            }
            if (poiDetailBean.images != null && poiDetailBean.images.size() > 0) {
                ImageLoader.getInstance().displayImage(poiDetailBean.images.get(0).url, holder.poiImageIv, picOptions);
            } else {
                holder.poiImageIv.setImageDrawable(null);
            }
            holder.poiNameTv.setText(poiDetailBean.zhName);
            String locName="";
            if(!TextUtils.isEmpty(poiDetailBean.locality.zhName)){
                locName="["+poiDetailBean.locality.zhName+"]";
            }
            SpannableString ss = new SpannableString(locName);
//            ss.setSpan(new StyleSpan(Typeface.BOLD), 0, locName.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            //设置字体前景色
            ss.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.app_theme_color)), 0, locName.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            holder.poiAddressTv.setText(ss);
            holder.poiAddressTv.append(poiDetailBean.address);
            holder.poiRating.setRating(poiDetailBean.getRating());
            holder.poiPriceTv.setText(poiDetailBean.priceDesc);
            if(!poiDetailBean.getFormatRank().equals("0")){
                holder.poiRankTv.setText("热度排名 "+poiDetailBean.getFormatRank());
            }

            if (!isAnimationEnd && isEditableMode) {
                final View view = holder.deleteIv;
                Animation animation = AnimationSimple.expand(view);
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
                view.startAnimation(animation);
                animation = AnimationSimple.expand(holder.dragHandleIv);
                holder.dragHandleIv.startAnimation(animation);

                holder.deleteIv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final PeachMessageDialog dialog = new PeachMessageDialog(getActivity());
                        dialog.setTitle("提示");
                        dialog.setMessage("确定删除");
                        dialog.setPositiveButton("确定", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                strategy.restaurant.remove(poiDetailBean);
                                notifyDataSetChanged();
                                dialog.dismiss();
                            }
                        });
                        dialog.setNegativeButton("取消",new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                        dialog.show();
                    }
                });
                holder.nearByTv.setVisibility(View.GONE);
            } else if (isEditableMode) {
                holder.deleteIv.setVisibility(View.VISIBLE);
                holder.nearByTv.setVisibility(View.GONE);
                holder.dragHandleIv.setVisibility(View.VISIBLE);

                holder.deleteIv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final PeachMessageDialog dialog = new PeachMessageDialog(getActivity());
                        dialog.setTitle("提示");
                        dialog.setMessage("确定删除");
                        dialog.setPositiveButton("确定", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                strategy.restaurant.remove(poiDetailBean);
                                notifyDataSetChanged();
                                dialog.dismiss();
                            }
                        });
                        dialog.setNegativeButton("取消",new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                        dialog.show();
                    }
                });
            } else {
                holder.deleteIv.setVisibility(View.GONE);
                holder.dragHandleIv.setVisibility(View.GONE);

                holder.nearByTv.setVisibility(View.VISIBLE);
                holder.nearByTv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (poiDetailBean.location != null && poiDetailBean.location.coordinates != null) {
                            Uri mUri = Uri.parse("geo:"+poiDetailBean.location.coordinates[1]+","+poiDetailBean.location.coordinates[0]+"?q="+poiDetailBean.zhName);
                            Intent mIntent = new Intent(Intent.ACTION_VIEW,mUri);
                            if (CommonUtils.checkIntent(getActivity(), mIntent)){
                                startActivity(mIntent);
                            }else{
                                ToastUtil.getInstance(getActivity()).showToast("没有找到地图应用");
                            }

                        }
                    }
                });
            }
            holder.contentRl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    IntentUtils.intentToDetail(getActivity(),poiDetailBean.type,poiDetailBean.id);
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
            public TextView nearByTv;
            public ImageView poiImageIv;
            public TextView poiNameTv;
            public TextView poiAddressTv;
            public TextView poiPriceTv;
            public TextView poiRankTv;
            public RatingBar poiRating;
        }


    }
}
