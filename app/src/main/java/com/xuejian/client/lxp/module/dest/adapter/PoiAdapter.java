package com.xuejian.client.lxp.module.dest.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.TextView;

import com.aizou.core.dialog.ToastUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.umeng.analytics.MobclickAgent;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.BaseActivity;
import com.xuejian.client.lxp.bean.PoiDetailBean;
import com.xuejian.client.lxp.common.api.TravelApi;
import com.xuejian.client.lxp.common.utils.IntentUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.techery.properratingbar.ProperRatingBar;

/**
 * Created by Rjm on 2014/11/27.
 */
public class PoiAdapter extends BaseAdapter {
    public final static int SPOT = 0;
    public final static int POI = 1;
    private Context mContext;
    private boolean mIsCanAdd;
    private List<PoiDetailBean> mPoiList;
    private OnPoiActionListener mOnPoiActionListener;

    private DisplayImageOptions picOptions;
    private String mAddStr = "添加";

    public PoiAdapter(Context context, boolean isCanAdd) {
        mContext = context;
        mIsCanAdd = isCanAdd;
        mPoiList = new ArrayList<PoiDetailBean>();
        picOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true).bitmapConfig(Bitmap.Config.ARGB_8888)
                .resetViewBeforeLoading(true)
//                .showImageOnFail(R.drawable.empty_photo)
//                .showImageOnLoading(R.drawable.empty_photo)
//                .showImageForEmptyUri(R.drawable.empty_photo)
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT).build();
    }

    public void setAddStr(String addStr) {
        mAddStr = addStr;
    }

    public void reset() {
        mPoiList.clear();
        notifyDataSetChanged();
    }

    public void setData(List<PoiDetailBean> ds) {
        mPoiList.addAll(ds);
    }

    public void setOnPoiActionListener(OnPoiActionListener onPoiActionListener) {
        mOnPoiActionListener = onPoiActionListener;
    }

    public List<PoiDetailBean> getDataList() {
        return mPoiList;
    }

    @Override
    public int getCount() {
        return mPoiList.size();
    }

    @Override
    public Object getItem(int position) {
        return mPoiList.get(position);
    }

    public void cancleAdd(int pos){
        mPoiList.get(pos).hasAdded=false;
        notifyDataSetChanged();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        String type = mPoiList.get(position).type;
        if (TravelApi.PeachType.SPOT.equals(type)) {
            return SPOT;
        } else {
            return POI;
        }
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int type = getItemViewType(position);
        final PoiDetailBean poiDetailBean = (PoiDetailBean) getItem(position);
        SpotViewHolder spotViewHolder = null;
        PoiViewHolder poiViewHolder = null;
        final Context context = mContext;
        if (convertView == null) {
            if (type == SPOT) {
                convertView = View.inflate(context, R.layout.item_plan_day_detil, null);
                spotViewHolder = new SpotViewHolder(convertView);
                convertView.setTag(spotViewHolder);
                if (!mIsCanAdd) {
                    spotViewHolder.mBtnAdd.setBackgroundResource(R.drawable.cell_selecter_btn);
                    spotViewHolder.mBtnAdd.setTextColor(context.getResources().getColor(R.color.selector_white_text_color));
                    //spotViewHolder.mBtnAdd.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_poi_location, 0);
                } else {
                    spotViewHolder.mBtnAdd.setVisibility(View.VISIBLE);
                }
            } else {
                convertView = View.inflate(context, R.layout.item_plan_day_detil, null);
                poiViewHolder = new PoiViewHolder(convertView);
                convertView.setTag(poiViewHolder);
                if (!mIsCanAdd) {
                    poiViewHolder.mBtnAdd.setBackgroundResource(R.drawable.cell_selecter_btn);
                    poiViewHolder.mBtnAdd.setTextColor(context.getResources().getColor(R.color.selector_white_text_color));
                    //poiViewHolder.mBtnAdd.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_poi_location, 0);
                } else {
                    poiViewHolder.mBtnAdd.setVisibility(View.VISIBLE);
                }
            }
        } else {
            if (type == SPOT) {
                spotViewHolder = (SpotViewHolder) convertView.getTag();
            } else {
                poiViewHolder = (PoiViewHolder) convertView.getTag();
            }
        }

        if (type == SPOT) {

            if (position<=2){
                spotViewHolder.hot_tag.setVisibility(View.VISIBLE);
            }else {
                spotViewHolder.hot_tag.setVisibility(View.GONE);
            }
            if (poiDetailBean.images != null && poiDetailBean.images.size() > 0) {
                ImageLoader.getInstance().displayImage(poiDetailBean.images.get(0).url, spotViewHolder.mSpotImageIv, picOptions);
            } else {
                ImageLoader.getInstance().displayImage(null, spotViewHolder.mSpotImageIv, picOptions);
                //spotViewHolder.mSpotImageIv.setImageDrawable(null);
            }
            spotViewHolder.mTvSpotName.setText(poiDetailBean.zhName);
            // spotViewHolder.mSpotAddressTv.setText(poiDetailBean.address);
            if (TextUtils.isEmpty(poiDetailBean.timeCostDesc)) {
                spotViewHolder.mSpotCosttimeTv.setText("");
            } else {
                spotViewHolder.mSpotCosttimeTv.setText("建议游玩:" + poiDetailBean.timeCostDesc);
            }

            if (!poiDetailBean.getFormatRank().equals("0")) {
                spotViewHolder.mSpotRankTv.setText(poiDetailBean.getFormatRank());
            } else {
                spotViewHolder.mSpotRankTv.setText("N");
            }
            spotViewHolder.rb_poi.setRating((int)poiDetailBean.getRating());
            if (mIsCanAdd) {
                spotViewHolder.mBtnAdd.setVisibility(View.VISIBLE);
                if (poiDetailBean.hasAdded) {
                    spotViewHolder.mBtnAdd.setText("取消");
                    spotViewHolder.mBtnAdd.setChecked(true);
                } else {
                    spotViewHolder.mBtnAdd.setText(mAddStr);
                    spotViewHolder.mBtnAdd.setChecked(false);
                }

                spotViewHolder.mBtnAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MobclickAgent.onEvent(mContext,"button_item_pois_lxp_plan");
                        if (poiDetailBean.hasAdded) {
                            poiDetailBean.hasAdded = false;
                            if (mOnPoiActionListener != null) {
                                mOnPoiActionListener.onPoiRemoved(poiDetailBean);
                            }
                        } else {
                            poiDetailBean.hasAdded = true;
                            if (mOnPoiActionListener != null) {
                                mOnPoiActionListener.onPoiAdded(poiDetailBean);
                            }
                            ToastUtil.getInstance(mContext).showToast("已" + mAddStr);
                        }
                        notifyDataSetChanged();
                    }
                });
            } else {
                spotViewHolder.mBtnAdd.setVisibility(View.GONE);
            }
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    IntentUtils.intentToDetail((BaseActivity) context, TravelApi.PeachType.SPOT, poiDetailBean.id);
                }
            });
        } else {
            if (position<=2){
                poiViewHolder.hot_tag.setVisibility(View.VISIBLE);
            }else {
                poiViewHolder.hot_tag.setVisibility(View.GONE);
            }
            poiViewHolder.rb_poi.setRating((int)poiDetailBean.getRating());
            poiViewHolder.mTvPoiName.setText(poiDetailBean.zhName);
            if (mIsCanAdd) {
                if (poiDetailBean.hasAdded) {
                    poiViewHolder.mBtnAdd.setText("取消");
                    poiViewHolder.mBtnAdd.setChecked(true);
                } else {
                    poiViewHolder.mBtnAdd.setText(mAddStr);
                    poiViewHolder.mBtnAdd.setChecked(false);
                }
                poiViewHolder.mBtnAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MobclickAgent.onEvent(mContext,"button_item_pois_lxp_plan");
                        if (poiDetailBean.hasAdded) {
                            poiDetailBean.hasAdded = false;
                            if (mOnPoiActionListener != null) {
                                mOnPoiActionListener.onPoiRemoved(poiDetailBean);
                            }
                        } else {
                            poiDetailBean.hasAdded = true;
                            if (mOnPoiActionListener != null) {
                                mOnPoiActionListener.onPoiAdded(poiDetailBean);
                            }
                            ToastUtil.getInstance(mContext).showToast("已" + mAddStr);
                        }
                        notifyDataSetChanged();
                    }
                });
            } else {
                poiViewHolder.mBtnAdd.setVisibility(View.GONE);
            }


            if (poiDetailBean.style.size()>0)
            poiViewHolder.mPoiPriceTv.setText(poiDetailBean.style.get(0));
            if (poiDetailBean.images != null && poiDetailBean.images.size() > 0) {
                ImageLoader.getInstance().displayImage(poiDetailBean.images.get(0).url, poiViewHolder.mPoiImageIv, picOptions);
            } else {
                ImageLoader.getInstance().displayImage(null, poiViewHolder.mPoiImageIv, picOptions);
            }
            if (!poiDetailBean.getFormatRank().equals("0")) {
                poiViewHolder.mPoiRankTv.setText(poiDetailBean.getFormatRank());
            } else {
                poiViewHolder.mPoiRankTv.setText("N");
            }
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    IntentUtils.intentToDetail((BaseActivity) context, poiDetailBean.type, poiDetailBean.id);
                }
            });

        }
        return convertView;
    }

    class SpotViewHolder {

        @Bind(R.id.tv_poi_title)
        TextView mTvSpotName;
        @Bind(R.id.btn_send)
        CheckedTextView mBtnAdd;
        @Bind(R.id.iv_poi_img)
        ImageView mSpotImageIv;
        @Bind(R.id.tv_poi_time)
        TextView mSpotCosttimeTv;
        @Bind(R.id.tv_poi_level)
        TextView mSpotRankTv;
        @Bind(R.id.rb_poi)
        ProperRatingBar rb_poi;
        @Bind(R.id.hot_tag)
        TextView hot_tag;
        public SpotViewHolder(View view) {
            ButterKnife.bind(this, view);
        }

    }

    class PoiViewHolder {

        @Bind(R.id.tv_poi_title)
        TextView mTvPoiName;
        @Bind(R.id.btn_send)
        CheckedTextView mBtnAdd;
        @Bind(R.id.iv_poi_img)
        ImageView mPoiImageIv;
        @Bind(R.id.tv_poi_time)
        TextView mPoiPriceTv;
        @Bind(R.id.tv_poi_level)
        TextView mPoiRankTv;
        @Bind(R.id.rb_poi)
        ProperRatingBar rb_poi;
        @Bind(R.id.hot_tag)
        TextView hot_tag;
        public PoiViewHolder(View view) {
            ButterKnife.bind(this, view);
        }

    }

    public interface OnPoiActionListener {
        void onPoiAdded(PoiDetailBean poi);

        void onPoiRemoved(PoiDetailBean poi);

        void onPoiNavi(PoiDetailBean poi);

    }
}
