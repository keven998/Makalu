package com.aizou.peachtravel.module.dest.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.Html;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aizou.peachtravel.R;
import com.aizou.peachtravel.bean.CommentBean;
import com.aizou.peachtravel.bean.PoiDetailBean;
import com.aizou.peachtravel.common.api.TravelApi;
import com.aizou.peachtravel.common.utils.UILUtils;
import com.aizou.peachtravel.module.dest.PoiDetailActivity;
import com.aizou.peachtravel.module.dest.SpotDetailActivity;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

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

    public PoiAdapter(Context context, boolean isCanAdd) {
        mContext = context;
        mIsCanAdd = isCanAdd;
        picOptions = UILUtils.getRadiusOption();
    }

    public void reset() {
        mPoiList.clear();
        notifyDataSetChanged();
    }

    public void setData(List<PoiDetailBean> ds) {
        mPoiList.addAll(ds);
    }

    public void setOnPoiActionListener(OnPoiActionListener onPoiActionListener){
        mOnPoiActionListener = onPoiActionListener;
    }

    public List<PoiDetailBean> getDataList(){
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
        if (type.equals(TravelApi.PeachType.SPOT)) {
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
                    spotViewHolder.mBtnAdd.setBackgroundColor(Color.WHITE);
                    spotViewHolder.mBtnAdd.setTextSize(TypedValue.COMPLEX_UNIT_SP,11);
                    spotViewHolder.mBtnAdd.setTextColor(context.getResources().getColor(R.color.base_text_color_subtitle));
                    spotViewHolder.mBtnAdd.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.selector_ic_navigation, 0);
                }
            } else {
                convertView = View.inflate(context, R.layout.row_poi_list, null);
                poiViewHolder = new PoiViewHolder(convertView);
                convertView.setTag(poiViewHolder);
                if (!mIsCanAdd) {
                    poiViewHolder.mBtnAdd.setBackgroundColor(Color.WHITE);
                    poiViewHolder.mBtnAdd.setTextColor(context.getResources().getColor(R.color.base_text_color_subtitle));
                    poiViewHolder.mBtnAdd.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.selector_ic_navigation, 0);
                }
            }
        } else {
            if(type == SPOT){
               spotViewHolder = (SpotViewHolder) convertView.getTag();
            } else {
               poiViewHolder = (PoiViewHolder) convertView.getTag();
            }
        }

        if (type == SPOT) {
            if (poiDetailBean.images != null && poiDetailBean.images.size() > 0) {
                ImageLoader.getInstance().displayImage(poiDetailBean.images.get(0).url, spotViewHolder.mSpotImageIv, picOptions);
            } else {
                spotViewHolder.mSpotImageIv.setImageResource(R.drawable.default_image);
            }
            spotViewHolder.mSpotNameTv.setText(poiDetailBean.zhName);
            spotViewHolder.mSpotTimeCostTv.setText("参考游玩时间："+poiDetailBean.timeCostDesc);
            spotViewHolder.mSpotDescTv.setText(poiDetailBean.desc);
            if(mIsCanAdd) {
                if(poiDetailBean.hasAdded) {
                    spotViewHolder.mBtnAdd.setText("已选择");
                } else {
                    spotViewHolder.mBtnAdd.setText("选择");
                }
                ((ViewGroup)spotViewHolder.mBtnAdd.getParent()).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(poiDetailBean.hasAdded) {
                            poiDetailBean.hasAdded = false;
                            if (mOnPoiActionListener != null) {
                                mOnPoiActionListener.onPoiRemoved(poiDetailBean);
                            }
                        } else {
                            poiDetailBean.hasAdded = true;
                            if(mOnPoiActionListener != null) {
                                mOnPoiActionListener.onPoiAdded(poiDetailBean);
                            }
                        }
                        notifyDataSetChanged();
                    }
                });
            } else {
                spotViewHolder.mBtnAdd.setText(poiDetailBean.distance);     //TODO 添加距离
                ((ViewGroup)spotViewHolder.mBtnAdd.getParent()).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //TODO
                    }
                });
            }
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Activity act = (Activity) context;
                    Intent intent = new Intent(act, SpotDetailActivity.class);
                    intent.putExtra("id",poiDetailBean.id);
                    act.startActivity(intent);
                }
            });
        } else {
//            poiViewHolder.mTvPoiName.setText("hellohellohellohellohellohellohello");
//            poiViewHolder.mTvPrice.setText("129/人");
//            poiViewHolder.mTvAddr.setText("hellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohello");
//            poiViewHolder.mIvPoiImage.setImageResource(R.drawable.guide_1);
//            poiViewHolder.mRatingBarPoi.setRating(2.7f);
//            poiViewHolder.mTvCommentName.setText("hellohello");
//            poiViewHolder.mTvCommentNum.setText(String.valueOf(9999));
//            poiViewHolder.mTvCommentContent.setText("hellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohello");


            poiViewHolder.mTvPoiName.setText(poiDetailBean.zhName);
            if (mIsCanAdd) {
                if (poiDetailBean.hasAdded) {
                    poiViewHolder.mBtnAdd.setText("已选择");

                } else {
                    poiViewHolder.mBtnAdd.setText("选择");
                }
                poiViewHolder.mBtnAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(poiDetailBean.hasAdded) {
                            poiDetailBean.hasAdded=false;
                            if(mOnPoiActionListener!=null) {
                                mOnPoiActionListener.onPoiRemoved(poiDetailBean);
                            }
                        } else {
                            poiDetailBean.hasAdded=true;
                            if(mOnPoiActionListener!=null) {
                                mOnPoiActionListener.onPoiAdded(poiDetailBean);
                            }
                        }
                        notifyDataSetChanged();
                    }
                });
            } else {
                poiViewHolder.mBtnAdd.setText(poiDetailBean.distance);     //TODO 添加距离 导航
                poiViewHolder.mBtnAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //TODO
                    }
                });
            }
            poiViewHolder.mTvPrice.setText(poiDetailBean.priceDesc);
            poiViewHolder.mTvAddr.setText(poiDetailBean.address);
            if(poiDetailBean.images != null&&poiDetailBean.images.size() > 0) {
                ImageLoader.getInstance().displayImage(poiDetailBean.images.get(0).url, poiViewHolder.mIvPoiImage, picOptions);
            } else {
                poiViewHolder.mIvPoiImage.setImageResource(R.drawable.default_image);
            }
            poiViewHolder.mRatingBarPoi.setRating(poiDetailBean.rating);
            if(poiDetailBean.comments == null || poiDetailBean.comments.size() == 0) {
                poiViewHolder.mRlComment.setVisibility(View.GONE);
            } else {
                poiViewHolder.mRlComment.setVisibility(View.VISIBLE);
                CommentBean commentBean = poiDetailBean.comments.get(0);
                poiViewHolder.mTvCommentName.setText(commentBean.userName);
                poiViewHolder.mTvCommentNum.setText(String.valueOf(poiDetailBean.commentCnt));
                poiViewHolder.mTvCommentContent.setText(Html.fromHtml(commentBean.contents));
            }
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Activity act = (Activity) context;
                    Intent intent = new Intent(act, PoiDetailActivity.class);
                    intent.putExtra("id", poiDetailBean.id);
                    intent.putExtra("type", poiDetailBean.type);
                    act.startActivity(intent);
                }
            });

        }
        return convertView;
    }

    class SpotViewHolder {
        @InjectView(R.id.spot_image_iv)
        ImageView mSpotImageIv;
        @InjectView(R.id.spot_name_tv)
        TextView mSpotNameTv;
        @InjectView(R.id.spot_time_cost_tv)
        TextView mSpotTimeCostTv;
        @InjectView(R.id.btn_add)
        CheckedTextView mBtnAdd;
        @InjectView(R.id.spot_desc_tv)
        TextView mSpotDescTv;

        public SpotViewHolder(View view) {
//            view = View.inflate(mContext, R.layout.row_poi_list, null);
            ButterKnife.inject(this, view);
        }

    }

    class PoiViewHolder {

        @InjectView(R.id.tv_poi_name)
        TextView mTvPoiName;
        @InjectView(R.id.btn_add)
        CheckedTextView mBtnAdd;
        @InjectView(R.id.tv_price)
        TextView mTvPrice;
        @InjectView(R.id.tv_addr)
        TextView mTvAddr;
        @InjectView(R.id.iv_poi_image)
        ImageView mIvPoiImage;
        @InjectView(R.id.ratingBar_poi)
        RatingBar mRatingBarPoi;
        @InjectView(R.id.tv_comment_name)
        TextView mTvCommentName;
        @InjectView(R.id.tv_comment_num)
        TextView mTvCommentNum;
        @InjectView(R.id.tv_comment_content)
        TextView mTvCommentContent;
        @InjectView(R.id.rl_comment)
        RelativeLayout mRlComment;

        public PoiViewHolder(View view) {
//            view = View.inflate(mContext, R.layout.row_poi_list, null);
            ButterKnife.inject(this, view);
        }

    }

    public interface  OnPoiActionListener{
        void onPoiAdded(PoiDetailBean poi);
        void onPoiRemoved(PoiDetailBean poi);

    }
}
