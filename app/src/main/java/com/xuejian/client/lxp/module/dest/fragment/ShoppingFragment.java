package com.xuejian.client.lxp.module.dest.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aizou.core.dialog.ToastUtil;
import com.aizou.core.utils.LocalDisplay;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.PeachBaseFragment;
import com.xuejian.client.lxp.bean.PeachUser;
import com.xuejian.client.lxp.bean.PoiDetailBean;
import com.xuejian.client.lxp.bean.StrategyBean;
import com.xuejian.client.lxp.common.account.AccountManager;
import com.xuejian.client.lxp.common.api.TravelApi;
import com.xuejian.client.lxp.common.dialog.PeachMessageDialog;
import com.xuejian.client.lxp.common.imageloader.UILUtils;
import com.xuejian.client.lxp.common.utils.AnimationSimple;
import com.xuejian.client.lxp.common.utils.CommonUtils;
import com.xuejian.client.lxp.common.utils.IntentUtils;
import com.xuejian.client.lxp.common.widget.dslv.DragSortController;
import com.xuejian.client.lxp.common.widget.dslv.DragSortListView;
import com.xuejian.client.lxp.config.PeachApplication;
import com.xuejian.client.lxp.module.dest.OnStrategyModeChangeListener;
import com.xuejian.client.lxp.module.dest.PoiListActivity;
import com.xuejian.client.lxp.module.dest.StrategyActivity;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Rjm on 2014/11/29.
 */
public class ShoppingFragment extends PeachBaseFragment implements OnStrategyModeChangeListener {

    public final static int ADD_SHOPPING_REQUEST_CODE=103;
    private OnStrategyModeChangeListener mOnEditModeChangeListener;
    @InjectView(R.id.edit_dslv)
    DragSortListView mEditDslv;
    View addFooter;
    Button addBtn;
    ShoppingAdapter mShoppingAdapter;
    boolean isInEditMode;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_shopping_guide, container, false);
        addFooter = View.inflate(getActivity(), R.layout.footer_route_day_add_day, null);
        addBtn = (Button) addFooter.findViewById(R.id.btn_add_day);
        addBtn.setText("收集购物");
        ButterKnife.inject(this, rootView);
        mEditDslv.addFooterView(addFooter);
        initData();
        return rootView;
    }
    @Override
    public void onAttach(Activity activity) {
        try {
            mOnEditModeChangeListener = (OnStrategyModeChangeListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement On OnEditModeChangeListener");
        }
        super.onAttach(activity);
    }
    public boolean  isEditableMode(){
        if(mShoppingAdapter !=null){
            return mShoppingAdapter.isEditableMode;
        }
        return false;
    }

    private StrategyBean getStrategy() {
        return ((StrategyActivity) getActivity()).getStrategy();

    }
    private void setAddView(StrategyBean strategyBean){
        final PeachUser user = AccountManager.getInstance().getLoginAccount(PeachApplication.getContext());
        if(user == null){
            addFooter.setVisibility(View.GONE);
        }else {
            if (addFooter != null) {
                if (user.userId != strategyBean.userId) {
                    addFooter.setVisibility(View.GONE);
                } else {
                    addFooter.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    private void initData() {
        final StrategyBean strategyBean = getStrategy();
        setAddView(strategyBean);
        DragSortController controller = new DragSortController(mEditDslv);
        controller.setDragHandleId(R.id.drag_handle);
        controller.setBackgroundColor(Color.TRANSPARENT);
        controller.setRemoveEnabled(false);
        mShoppingAdapter = new ShoppingAdapter(isInEditMode);
        final DragSortListView listView = mEditDslv;
        View view = new View(getActivity());
        view.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, LocalDisplay.dp2px(0)));
        listView.addHeaderView(view);
        listView.setFloatViewManager(controller);
        listView.setOnTouchListener(controller);
        listView.setDropListener(mShoppingAdapter);
        listView.setAdapter(mShoppingAdapter);


        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MobclickAgent.onEvent(getActivity(),"event_add_shopping_schedule");
                if(mOnEditModeChangeListener!=null){
                    if(!isInEditMode){
                        isInEditMode = true;
                        mShoppingAdapter.setEditableMode(false);
                        mShoppingAdapter.notifyDataSetChanged();
                        mOnEditModeChangeListener.onEditModeChange(false);
                   }
                }
                Intent intent = new Intent(getActivity(), PoiListActivity.class);
                intent.putExtra("type", TravelApi.PeachType.SHOPPING);
                intent.putExtra("canAdd", true);
                intent.putParcelableArrayListExtra("locList", strategyBean.localities);
                intent.putParcelableArrayListExtra("poiList",strategyBean.shopping);
                getActivity().startActivityForResult(intent, ADD_SHOPPING_REQUEST_CODE);
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode== Activity.RESULT_OK){
            if(requestCode==ADD_SHOPPING_REQUEST_CODE){
                getStrategy().shopping = data.getParcelableArrayListExtra("poiList");
                mShoppingAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onEditModeChange(boolean isInEdit) {
        this.isInEditMode = isInEdit;
        if (mShoppingAdapter != null) {
            mShoppingAdapter.setEditableMode(isInEdit);
            mShoppingAdapter.notifyDataSetChanged();
        }

    }

    @Override
    public void onCopyStrategy() {
        setAddView(getStrategy());
    }

    public class ShoppingAdapter extends BaseAdapter implements
            DragSortListView.DropListener {
        public boolean isEditableMode;
        private DisplayImageOptions picOptions;
        private boolean isAnimationEnd=true;
        private StrategyBean strategy;
        public ShoppingAdapter(boolean isEditableMode) {
            this.isEditableMode = isEditableMode;
            picOptions = UILUtils.getDefaultOption();
            strategy = getStrategy();
        }
        public void setEditableMode(boolean mode){
            isAnimationEnd =false;
            isEditableMode = mode;
        }
        @Override
        public int getCount() {
            return  strategy.shopping.size();
        }

        @Override
        public Object getItem(int position) {
            return  strategy.shopping.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final PoiDetailBean poiDetailBean =  strategy.shopping.get(position);
            final ItemViewHolder holder;
            if (convertView == null) {
                holder = new ItemViewHolder();
                convertView = View.inflate(getActivity(), R.layout.row_poi_list, null);
                holder.contentRl = (RelativeLayout) convertView.findViewById(R.id.rl_content);
                holder.deleteIv = (ImageView) convertView.findViewById(R.id.poi_delete_iv);
               // holder.dragHandleIv = (ImageView) convertView.findViewById(R.id.poi_drag_handle);
                holder.nearByTv = (CheckedTextView) convertView.findViewById(R.id.btn_add);
                holder.poiImageIv = (ImageView) convertView.findViewById(R.id.poi_image_iv);
                holder.poiNameTv = (TextView) convertView.findViewById(R.id.tv_poi_name);
                holder.poiAddressTv = (TextView) convertView.findViewById(R.id.poi_address_tv);
                holder.poiPriceTv = (TextView) convertView.findViewById(R.id.poi_price_tv);
                holder.poiRating = (RatingBar) convertView.findViewById(R.id.poi_rating);
                holder.poiRankTv = (TextView) convertView.findViewById(R.id.poi_rank_tv);
                holder.costTimeDesc = (TextView) convertView.findViewById(R.id.poi_costtime_tv);
                convertView.setTag(holder);
            } else {
                holder = (ItemViewHolder) convertView.getTag();
            }
            if(poiDetailBean.images!=null&&poiDetailBean.images.size()>0){
                ImageLoader.getInstance().displayImage(poiDetailBean.images.get(0).url, holder.poiImageIv, picOptions);
            }else{
                holder.poiImageIv.setImageDrawable(null);
            }
            holder.nearByTv.setText("地图");
            holder.poiNameTv.setText(poiDetailBean.zhName);
            holder.costTimeDesc.setText(poiDetailBean.timeCostDesc);

            String locName="";
            if(poiDetailBean.locality!=null&&!TextUtils.isEmpty(poiDetailBean.locality.zhName)){
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
//                holder.poiRankTv.setText("热度排名 "+poiDetailBean.getFormatRank());
                holder.poiRankTv.setText(String.format("%s排名 %s", poiDetailBean.getPoiTypeName(), poiDetailBean.getFormatRank()));
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
                //animation = AnimationSimple.expand(holder.dragHandleIv);
                //holder.dragHandleIv.startAnimation(animation);

                holder.deleteIv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final PeachMessageDialog dialog = new PeachMessageDialog(getActivity());
                        dialog.setTitle("提示");
                        dialog.setMessage("确定删除");
                        dialog.setPositiveButton("确定", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                strategy.shopping.remove(poiDetailBean);
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
                //holder.dragHandleIv.setVisibility(View.VISIBLE);

                holder.deleteIv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final PeachMessageDialog dialog = new PeachMessageDialog(getActivity());
                        dialog.setTitle("提示");
                        dialog.setMessage("确定删除");
                        dialog.setPositiveButton("确定", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                strategy.shopping.remove(poiDetailBean);
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
                //holder.dragHandleIv.setVisibility(View.GONE);
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
                    IntentUtils.intentToDetail(getActivity(), poiDetailBean.type, poiDetailBean.id);
                }
            });
            return convertView;
        }

        @Override
        public void drop(int from, int to) {
            if (from != to) {
                PoiDetailBean item = (PoiDetailBean) getItem(from);
                strategy.shopping.remove(item);
                strategy.shopping.add(to, item);
                notifyDataSetChanged();
            }
        }

        private class ItemViewHolder {
            public RelativeLayout contentRl;
            public ImageView deleteIv;
            public CheckedTextView nearByTv;
            public ImageView poiImageIv;
            public TextView poiNameTv;
            public TextView poiAddressTv;
            public TextView poiPriceTv;
            public RatingBar poiRating;
            public TextView poiRankTv;
            public TextView costTimeDesc;
        }
    }
}
