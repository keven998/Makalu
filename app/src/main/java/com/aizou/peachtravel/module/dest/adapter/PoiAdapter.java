package com.aizou.peachtravel.module.dest.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
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
    private List<PoiDetailBean> mPoiList =new ArrayList<PoiDetailBean>();
    private OnPoiActionListener mOnPoiActionListener;

    public PoiAdapter(Context context,boolean isCanAdd) {
        mContext = context;
        mIsCanAdd = isCanAdd;
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
        if (type.equals(TravelApi.PoiType.SPOT)) {
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
        if (convertView == null) {
            if (type == SPOT) {
                convertView = View.inflate(mContext, R.layout.row_spot_list, null);
                spotViewHolder = new SpotViewHolder(convertView);
                convertView.setTag(spotViewHolder);
            } else if (type == POI) {
                convertView = View.inflate(mContext, R.layout.row_poi_list, null);
                poiViewHolder = new PoiViewHolder(convertView);
                convertView.setTag(poiViewHolder);
            }
        }else{
            if(type==SPOT){
               spotViewHolder = (SpotViewHolder) convertView.getTag();
            }else{
               poiViewHolder = (PoiViewHolder) convertView.getTag();
            }
        }

        if(type==SPOT){
            if(poiDetailBean.images!=null&&poiDetailBean.images.size()>0){
                ImageLoader.getInstance().displayImage(poiDetailBean.images.get(0).url,spotViewHolder.mSpotImageIv, UILUtils.getDefaultOption());
            }
            spotViewHolder.mSpotNameTv.setText(poiDetailBean.zhName);
            spotViewHolder.mSpotTimeCostTv.setText("参考游玩时间："+poiDetailBean.timeCostDesc);
            spotViewHolder.mSpotDescTv.setText(poiDetailBean.desc);
            if(mIsCanAdd){
                spotViewHolder.mBtnAdd.setVisibility(View.VISIBLE);
                if(poiDetailBean.hasAdded){
                    spotViewHolder.mBtnAdd.setText("已选择");

                }else{
                    spotViewHolder.mBtnAdd.setText("选择");
                }
                spotViewHolder.mBtnAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(poiDetailBean.hasAdded){
                            poiDetailBean.hasAdded=false;
                            if(mOnPoiActionListener!=null){
                                mOnPoiActionListener.onPoiRemoved(poiDetailBean);
                            }
                        }else{
                            poiDetailBean.hasAdded=true;
                            if(mOnPoiActionListener!=null){
                                mOnPoiActionListener.onPoiAdded(poiDetailBean);
                            }
                        }
                        notifyDataSetChanged();
                    }
                });
            }else{
                spotViewHolder.mBtnAdd.setVisibility(View.GONE);
            }
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, SpotDetailActivity.class);
                    intent.putExtra("id",poiDetailBean.id);
                    mContext.startActivity(intent);
                }
            });


        }else {
            poiViewHolder.mTvPoiName.setText(poiDetailBean.zhName);
            if(mIsCanAdd){
                poiViewHolder.mBtnAdd.setVisibility(View.VISIBLE);
                if(poiDetailBean.hasAdded){
                    poiViewHolder.mBtnAdd.setText("已选择");

                }else{
                    poiViewHolder.mBtnAdd.setText("选择");
                }
                poiViewHolder.mBtnAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(poiDetailBean.hasAdded){
                            poiDetailBean.hasAdded=false;
                            if(mOnPoiActionListener!=null){
                                mOnPoiActionListener.onPoiRemoved(poiDetailBean);
                            }
                        }else{
                            poiDetailBean.hasAdded=true;
                            if(mOnPoiActionListener!=null){
                                mOnPoiActionListener.onPoiAdded(poiDetailBean);
                            }
                        }
                        notifyDataSetChanged();
                    }
                });
            }else{
                poiViewHolder.mBtnAdd.setVisibility(View.GONE);
            }
            poiViewHolder.mTvPrice.setText(poiDetailBean.priceDesc);
            poiViewHolder.mTvAddr.setText(poiDetailBean.address);
            if(poiDetailBean.images!=null&&poiDetailBean.images.size()>0)
                ImageLoader.getInstance().displayImage(poiDetailBean.images.get(0).url,poiViewHolder.mIvPoiImage,UILUtils.getDefaultOption());
            poiViewHolder.mRatingBarPoi.setRating(poiDetailBean.rating);
            if(poiDetailBean.comments==null||poiDetailBean.comments.size()==0){
                poiViewHolder.mRlComment.setVisibility(View.GONE);
            }else{
                poiViewHolder.mRlComment.setVisibility(View.VISIBLE);
                CommentBean commentBean = poiDetailBean.comments.get(0);
                poiViewHolder.mTvCommentName.setText(commentBean.nickName);
                poiViewHolder.mTvCommentNum.setText(poiDetailBean.commentCnt+"");
                poiViewHolder.mTvCommentContent.setText(commentBean.commentDetails);
            }
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, PoiDetailActivity.class);
                    intent.putExtra("id",poiDetailBean.id);
                    intent.putExtra("type",poiDetailBean.type);
                    mContext.startActivity(intent);
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
        Button mBtnAdd;
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
        Button mBtnAdd;
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
