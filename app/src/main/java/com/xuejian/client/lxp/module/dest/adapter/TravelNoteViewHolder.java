package com.xuejian.client.lxp.module.dest.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aizou.core.widget.listHelper.ViewHolderBase;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.bean.TravelNoteBean;
import com.xuejian.client.lxp.common.imageloader.UILUtils;
import com.xuejian.client.lxp.module.dest.TravelNoteDetailActivity;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Rjm on 2014/12/10.
 */
public class TravelNoteViewHolder extends ViewHolderBase<TravelNoteBean> {
    View view;
    RelativeLayout mSendRl;
    Button mSendBtn;
    ImageView mTravelIv;
    TextView mNoteNameTv;
    TextView mNoteDescTv;
    TextView mPropertyTv;
    //TextView mDays,mFee;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    OnSendClickListener mOnSendClickListener;
    Activity activity;

    private boolean mIsShowSend;
    private boolean mIsShowMore;

    private DisplayImageOptions picOptions;

    public TravelNoteViewHolder(Activity context, boolean isShowSend, boolean isShowMore){
        mIsShowSend = isShowSend;
        mIsShowMore = isShowMore;
        activity= context;
        picOptions = UILUtils.getRadiusOption();
    }

    public void setOnSendClickListener(OnSendClickListener onSendClickListener){
        mOnSendClickListener = onSendClickListener;
    }


    @Override
    public View createView(LayoutInflater layoutInflater) {
        view = layoutInflater.inflate(R.layout.row_travels, null);
        mSendRl = (RelativeLayout) view.findViewById(R.id.rl_send);
        mSendBtn = (Button) view.findViewById(R.id.btn_send);
        mTravelIv = (ImageView) view.findViewById(R.id.iv_travels);
        mNoteNameTv = (TextView) view.findViewById(R.id.tv_travels_name);
        mNoteDescTv = (TextView) view.findViewById(R.id.tv_travels_desc);
        mPropertyTv = (TextView) view.findViewById(R.id.tv_property);

       /* mDays = (TextView) view.findViewById(R.id.trip_days);
        mFee = (TextView) view.findViewById(R.id.trip_fee);*/
        return view;
    }

    @Override
    public void showData(int position, final TravelNoteBean itemData) {
        if(mIsShowSend){
            mSendRl.setVisibility(View.VISIBLE);
            mSendBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mOnSendClickListener!=null){
                        mOnSendClickListener.onSendClick(v,itemData);
                    }
                }
            });
        } else {
            mSendRl.setVisibility(View.GONE);
        }
        ImageLoader.getInstance().displayImage(itemData.getNoteImage(),mTravelIv, picOptions);
        mNoteNameTv.setText(itemData.title);
        String[] strArray=itemData.summary.split("\n");
        String maxLengthStr=strArray[0];
        for(String str:strArray){
            if(str.length()>maxLengthStr.length()){
                maxLengthStr=str;
            }
        }
        mNoteDescTv.setText(maxLengthStr);
        mPropertyTv.setText(String.format("%s  %s  %s", itemData.authorName, itemData.source, simpleDateFormat.format(new Date(itemData.publishTime))));
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MobclickAgent.onEvent(activity,"event_city_travel_note_item");
                Intent intent = new Intent(activity, TravelNoteDetailActivity.class);
                intent.putExtra("travelNote", itemData);
                intent.putExtra("id", itemData.id);
                activity.startActivity(intent);
            }
        });

    }

    public interface OnSendClickListener{
        void onSendClick(View view, TravelNoteBean itemData);
    }

}