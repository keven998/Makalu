package com.aizou.peachtravel.module.dest.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.aizou.core.dialog.ToastUtil;
import com.aizou.peachtravel.R;
import com.aizou.peachtravel.base.BaseActivity;
import com.aizou.peachtravel.bean.PoiDetailBean;
import com.aizou.peachtravel.common.api.TravelApi;
import com.aizou.peachtravel.common.imageloader.UILUtils;
import com.aizou.peachtravel.common.utils.IntentUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Rjm on 2014/11/27.
 */
public class PoiAdapter extends BaseAdapter {
    public final static int SPOT = 0;
    public final static int POI = 1;
    private Context mContext;
    private boolean mIsCanAdd;
    private List<PoiDetailBean> mPoiList = new ArrayList<PoiDetailBean>();
    private OnPoiActionListener mOnPoiActionListener;

    private DisplayImageOptions picOptions;
    private String mAddStr = "添加";

    public PoiAdapter(Context context, boolean isCanAdd) {
        mContext = context;
        mIsCanAdd = isCanAdd;
        picOptions = UILUtils.getRadiusOption();
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
                convertView = View.inflate(context, R.layout.row_spot_list, null);
                spotViewHolder = new SpotViewHolder(convertView);
                convertView.setTag(spotViewHolder);
                if (!mIsCanAdd) {
                    spotViewHolder.mBtnAdd.setBackgroundResource(R.drawable.theme_button_selctor);
                    spotViewHolder.mBtnAdd.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
                    spotViewHolder.mBtnAdd.setTextColor(context.getResources().getColor(R.color.base_color_white));
                    //spotViewHolder.mBtnAdd.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_poi_location, 0);
                }
            } else {
                convertView = View.inflate(context, R.layout.row_poi_list, null);
                poiViewHolder = new PoiViewHolder(convertView);
                convertView.setTag(poiViewHolder);
                if (!mIsCanAdd) {
                    poiViewHolder.mBtnAdd.setBackgroundResource(R.drawable.theme_button_selctor);
                    poiViewHolder.mBtnAdd.setTextColor(context.getResources().getColor(R.color.base_color_white));
                    //poiViewHolder.mBtnAdd.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_poi_location, 0);
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
            if (poiDetailBean.images != null && poiDetailBean.images.size() > 0) {
                ImageLoader.getInstance().displayImage(poiDetailBean.images.get(0).url, spotViewHolder.mSpotImageIv, picOptions);
            } else {
                spotViewHolder.mSpotImageIv.setImageDrawable(null);
            }
            spotViewHolder.mTvSpotName.setText(poiDetailBean.zhName);
            spotViewHolder.mSpotAddressTv.setText(poiDetailBean.address);
            if(TextUtils.isEmpty(poiDetailBean.timeCostDesc)){
                spotViewHolder.mSpotCosttimeTv.setText("");
            }else{
                spotViewHolder.mSpotCosttimeTv.setText("参考游玩 " + poiDetailBean.timeCostDesc);
            }

            //spotViewHolder.mSpotRating.setRating(poiDetailBean.getRating());
            if(!poiDetailBean.getFormatRank().equals("0")){
                spotViewHolder.mSpotRankTv.setText("景点排名 "+poiDetailBean.getFormatRank());
            }
            if (mIsCanAdd) {
                if (poiDetailBean.hasAdded) {
                    spotViewHolder.mBtnAdd.setText("已" + mAddStr);
                    spotViewHolder.mBtnAdd.setBackgroundResource(R.drawable.btn_has_add);
                } else {
                    spotViewHolder.mBtnAdd.setText(mAddStr);
                    spotViewHolder.mBtnAdd.setBackgroundResource(R.drawable.btn_theme_normal_shape_radius_4);
                }
                 spotViewHolder.mBtnAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (poiDetailBean.hasAdded) {
                            poiDetailBean.hasAdded = false;
                            if (mOnPoiActionListener != null) {
                                mOnPoiActionListener.onPoiRemoved(poiDetailBean);
                            }
                        } else {
                            MobclickAgent.onEvent(mContext,"event_add_desination_as_schedule");
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
                spotViewHolder.mBtnAdd.setText(/*poiDetailBean.distance*/"地图");     //TODO 添加距离
                spotViewHolder.mBtnAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mOnPoiActionListener != null) {
                            mOnPoiActionListener.onPoiNavi(poiDetailBean);
                        }
                    }
                });
            }
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    IntentUtils.intentToDetail((BaseActivity) context, TravelApi.PeachType.SPOT,poiDetailBean.id);
                }
            });
        } else {
            poiViewHolder.mTvPoiName.setText(poiDetailBean.zhName);
            if (mIsCanAdd) {
                if (poiDetailBean.hasAdded) {
                    poiViewHolder.mBtnAdd.setText("已" + mAddStr);
                    poiViewHolder.mBtnAdd.setBackgroundResource(R.drawable.btn_has_add);
                } else {
                    poiViewHolder.mBtnAdd.setText(mAddStr);
                    poiViewHolder.mBtnAdd.setBackgroundResource(R.drawable.btn_theme_normal_shape_radius_4);
                }
                poiViewHolder.mBtnAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (poiDetailBean.hasAdded) {
                            poiDetailBean.hasAdded = false;
                            if (mOnPoiActionListener != null) {
                                mOnPoiActionListener.onPoiRemoved(poiDetailBean);
                            }
                        } else {
                            MobclickAgent.onEvent(mContext,"event_add_desination_as_schedule");
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
                poiViewHolder.mBtnAdd.setText(/*poiDetailBean.distance*/"地图");     //TODO 添加距离 导航
                poiViewHolder.mBtnAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //TODO
                        if (mOnPoiActionListener != null) {
                            mOnPoiActionListener.onPoiNavi(poiDetailBean);
                        }
                    }
                });
            }
            poiViewHolder.mPoiPriceTv.setText(poiDetailBean.priceDesc);
            poiViewHolder.mPoiAddressTv.setText(poiDetailBean.address);
            if (poiDetailBean.images != null && poiDetailBean.images.size() > 0) {
                ImageLoader.getInstance().displayImage(poiDetailBean.images.get(0).url, poiViewHolder.mPoiImageIv, picOptions);
            } else {
                poiViewHolder.mPoiImageIv.setImageDrawable(null);
            }
            poiViewHolder.mPoiRating.setRating(poiDetailBean.getRating());
            if(!poiDetailBean.getFormatRank().equals("0")){
//                poiViewHolder.mPoiRankTv.setText("热度排名 "+poiDetailBean.getFormatRank());
                poiViewHolder.mPoiRankTv.setText(String.format("%s排名 %s", poiDetailBean.getPoiTypeName(), poiDetailBean.getFormatRank()));
            }
//            if (poiDetailBean.comments == null || poiDetailBean.comments.size() == 0) {
////                poiViewHolder.mRlComment.setVisibility(View.GONE);
//            } else {
////                poiViewHolder.mRlComment.setVisibility(View.VISIBLE);
//                CommentBean commentBean = poiDetailBean.comments.get(0);
//                poiViewHolder.mPoiCommentUsername.setText(commentBean.authorName);
////                poiViewHolder.mTvCommentNum.setText(String.valueOf(poiDetailBean.commentCnt));
//                poiViewHolder.mPoiCommentContent.setText(Html.fromHtml(commentBean.contents));
//            }
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    IntentUtils.intentToDetail((BaseActivity) context,poiDetailBean.type,poiDetailBean.id);
                }
            });

        }
        return convertView;
    }

    class SpotViewHolder {

        @InjectView(R.id.tv_spot_name)
        TextView mTvSpotName;
        @InjectView(R.id.btn_add)
        CheckedTextView mBtnAdd;
        @InjectView(R.id.spot_image_iv)
        ImageView mSpotImageIv;
        @InjectView(R.id.spot_address_tv)
        TextView mSpotAddressTv;
        @InjectView(R.id.spot_costtime_tv)
        TextView mSpotCosttimeTv;
        /*@InjectView(R.id.spot_rating)
        RatingBar mSpotRating;*/
        @InjectView(R.id.spot_rank_tv)
        TextView mSpotRankTv;

        public SpotViewHolder(View view) {
//            view = View.inflate(mContext, R.layout.row_spot_list, null);
            ButterKnife.inject(this, view);
        }

    }

    class PoiViewHolder {


        @InjectView(R.id.tv_poi_name)
        TextView mTvPoiName;
        @InjectView(R.id.btn_add)
        CheckedTextView mBtnAdd;
        @InjectView(R.id.poi_image_iv)
        ImageView mPoiImageIv;
        @InjectView(R.id.poi_address_tv)
        TextView mPoiAddressTv;
        @InjectView(R.id.poi_price_tv)
        TextView mPoiPriceTv;
        @InjectView(R.id.poi_rating)
        RatingBar mPoiRating;
        @InjectView(R.id.poi_rank_tv)
        TextView mPoiRankTv;
//        @InjectView(R.id.poi_comment_username)
//        TextView mPoiCommentUsername;
//        @InjectView(R.id.poi_comment_content)
//        TextView mPoiCommentContent;

        public PoiViewHolder(View view) {
//            view = View.inflate(mContext, R.layout.row_poi_list, null);
            ButterKnife.inject(this, view);
        }

    }

    public interface OnPoiActionListener {
        void onPoiAdded(PoiDetailBean poi);

        void onPoiRemoved(PoiDetailBean poi);

        void onPoiNavi(PoiDetailBean poi);

    }
}
