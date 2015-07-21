package com.xuejian.client.lxp.module.dest.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.TypedValue;
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
import com.xuejian.client.lxp.common.imageloader.UILUtils;
import com.xuejian.client.lxp.common.utils.IntentUtils;

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
        picOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true).bitmapConfig(Bitmap.Config.ARGB_8888)
                .resetViewBeforeLoading(true)
                .showImageOnFail(R.drawable.empty_photo)
                .showImageOnLoading(R.drawable.empty_photo)
                .showImageForEmptyUri(R.drawable.empty_photo)
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
                    spotViewHolder.mBtnAdd.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
                    spotViewHolder.mBtnAdd.setTextColor(context.getResources().getColor(R.color.base_color_white));
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
                    poiViewHolder.mBtnAdd.setTextColor(context.getResources().getColor(R.color.base_color_white));
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
                spotViewHolder.mSpotCosttimeTv.setText("参考游玩 " + poiDetailBean.timeCostDesc);
            }

            //spotViewHolder.mSpotRating.setRating(poiDetailBean.getRating());
            if (!poiDetailBean.getFormatRank().equals("0")) {
                spotViewHolder.mSpotRankTv.setText(poiDetailBean.getFormatRank());
            } else {
                spotViewHolder.mSpotRankTv.setText("N");
            }
            if (mIsCanAdd) {
                spotViewHolder.mBtnAdd.setVisibility(View.VISIBLE);
                if (poiDetailBean.hasAdded) {
                    spotViewHolder.mBtnAdd.setText("已" + mAddStr);
                    spotViewHolder.mBtnAdd.setTextColor(Color.rgb(150, 150, 150));
                    spotViewHolder.mBtnAdd.setBackgroundResource(R.drawable.cell_selecter_btn_disable);
                } else {
                    spotViewHolder.mBtnAdd.setText(mAddStr);
                    spotViewHolder.mBtnAdd.setTextColor(Color.rgb(153, 204, 102));
                    spotViewHolder.mBtnAdd.setBackgroundResource(R.drawable.cell_selecter_btn_normal);
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
                            MobclickAgent.onEvent(mContext, "event_add_desination_as_schedule");
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
//                spotViewHolder.mBtnAdd.setText(/*poiDetailBean.distance*/"地图");     //TODO 添加距离
//                spotViewHolder.mBtnAdd.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        if (mOnPoiActionListener != null) {
//                            mOnPoiActionListener.onPoiNavi(poiDetailBean);
//                        }
//                    }
//                });
            }
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    IntentUtils.intentToDetail((BaseActivity) context, TravelApi.PeachType.SPOT, poiDetailBean.id);
                }
            });
        } else {
            poiViewHolder.mTvPoiName.setText(poiDetailBean.zhName);
            if (mIsCanAdd) {
                if (poiDetailBean.hasAdded) {
                    poiViewHolder.mBtnAdd.setText("已" + mAddStr);
                    poiViewHolder.mBtnAdd.setTextColor(Color.rgb(150,150,150));
                    poiViewHolder.mBtnAdd.setBackgroundResource(R.drawable.cell_selecter_btn_disable);
                } else {
                    poiViewHolder.mBtnAdd.setText(mAddStr);
                    poiViewHolder.mBtnAdd.setTextColor(Color.rgb(153,204,102));
                    poiViewHolder.mBtnAdd.setBackgroundResource(R.drawable.cell_selecter_btn_normal);
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
                            MobclickAgent.onEvent(mContext, "event_add_desination_as_schedule");
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
//                poiViewHolder.mBtnAdd.setText("地图");     //TODO 添加距离 导航
//                poiViewHolder.mBtnAdd.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        //TODO
//                        if (mOnPoiActionListener != null) {
//                            mOnPoiActionListener.onPoiNavi(poiDetailBean);
//                        }
//                    }
//                });
            }
            poiViewHolder.mPoiPriceTv.setText(poiDetailBean.getPoiTypeName());
//            poiViewHolder.mPoiAddressTv.setText(poiDetailBean.address);
            if (poiDetailBean.images != null && poiDetailBean.images.size() > 0) {
                ImageLoader.getInstance().displayImage(poiDetailBean.images.get(0).url, poiViewHolder.mPoiImageIv, picOptions);
            } else {
                ImageLoader.getInstance().displayImage(null, poiViewHolder.mPoiImageIv, picOptions);
                //poiViewHolder.mPoiImageIv.setImageDrawable(null);
            }
//            poiViewHolder.mPoiRating.setRating(poiDetailBean.getRating());
            if (!poiDetailBean.getFormatRank().equals("0")) {
//                poiViewHolder.mPoiRankTv.setText("热度排名 "+poiDetailBean.getFormatRank());
                poiViewHolder.mPoiRankTv.setText(poiDetailBean.getFormatRank());
            } else {
                poiViewHolder.mPoiRankTv.setText("N");
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
                    IntentUtils.intentToDetail((BaseActivity) context, poiDetailBean.type, poiDetailBean.id);
                }
            });

        }
        return convertView;
    }

    class SpotViewHolder {

        @InjectView(R.id.tv_poi_title)
        TextView mTvSpotName;
        @InjectView(R.id.btn_send)
        CheckedTextView mBtnAdd;
        @InjectView(R.id.iv_poi_img)
        ImageView mSpotImageIv;
        //        @InjectView(R.id.spot_address_tv)
//        TextView mSpotAddressTv;
        @InjectView(R.id.tv_poi_time)
        TextView mSpotCosttimeTv;
        /*@InjectView(R.id.spot_rating)
        RatingBar mSpotRating;*/
        @InjectView(R.id.tv_poi_level)
        TextView mSpotRankTv;

        public SpotViewHolder(View view) {
//            view = View.inflate(mContext, R.layout.row_spot_list, null);
            ButterKnife.inject(this, view);
        }

    }

    class PoiViewHolder {

        @InjectView(R.id.tv_poi_title)
        TextView mTvPoiName;
        @InjectView(R.id.btn_send)
        CheckedTextView mBtnAdd;
        @InjectView(R.id.iv_poi_img)
        ImageView mPoiImageIv;
        //        @InjectView(R.id.poi_address_tv)
//        TextView mPoiAddressTv;
        @InjectView(R.id.tv_poi_time)
        TextView mPoiPriceTv;
        //        @InjectView(R.id.poi_rating)
//        RatingBar mPoiRating;
        @InjectView(R.id.tv_poi_level)
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
