package com.aizou.peachtravel.module.dest.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aizou.core.utils.LocalDisplay;
import com.aizou.core.widget.listHelper.ViewHolderBase;
import com.aizou.peachtravel.R;
import com.aizou.peachtravel.bean.TravelNoteBean;
import com.aizou.peachtravel.common.imageloader.UILUtils;
import com.aizou.peachtravel.module.dest.TravelNoteDetailActivity;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Rjm on 2014/12/10.
 */
public class TravelNoteViewHolder extends ViewHolderBase<TravelNoteBean> {
    View view;
    RelativeLayout mMoreRl;
    TextView mMoreTv;
    Button mSendBtn;
    ImageView mTravelIv;
    TextView mNoteNameTv;
    TextView mNoteDescTv;
    ImageView mAvatarIv;
    TextView mAuthorNameTv;
    TextView mFromTv;
    TextView mTimeTv;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    OnMoreClickListener mOnMoreClickListener;
    Activity activity;

    private boolean mIsShowSend;
    private boolean mIsShowMore;

    public TravelNoteViewHolder(Activity context,boolean isShowSend,boolean isShowMore){
        mIsShowSend = isShowSend;
        mIsShowMore = isShowMore;
        activity= context;
    }

    public void setOnMoreClickListener(OnMoreClickListener onMoreClickListener){
        mOnMoreClickListener = onMoreClickListener;

    }


    @Override
    public View createView(LayoutInflater layoutInflater) {
        view = layoutInflater.inflate(R.layout.row_travels,null);
        mMoreRl = (RelativeLayout) view.findViewById(R.id.rl_more);
        mMoreTv = (TextView) view.findViewById(R.id.tv_more);
        mSendBtn = (Button) view.findViewById(R.id.btn_send);
        mTravelIv = (ImageView) view.findViewById(R.id.iv_travels);
        mNoteNameTv = (TextView) view.findViewById(R.id.tv_travels_name);
        mNoteDescTv = (TextView) view.findViewById(R.id.tv_travels_desc);
        mAvatarIv = (ImageView) view.findViewById(R.id.iv_avatar);
        mAuthorNameTv = (TextView) view.findViewById(R.id.tv_username);
        mFromTv = (TextView) view.findViewById(R.id.tv_from);
        mTimeTv = (TextView) view.findViewById(R.id.tv_time);
        return view;
    }

    @Override
    public void showData(int position, final TravelNoteBean itemData) {
        if(position==0&&mIsShowMore){
            mMoreRl.setVisibility(View.VISIBLE);
            mMoreTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mOnMoreClickListener!=null){
                        mOnMoreClickListener.onMoreClick(v);
                    }
                }
            });
        }else{
            mMoreRl.setVisibility(View.GONE);
        }
        if(mIsShowSend){
            mSendBtn.setVisibility(View.VISIBLE);
            mSendBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }else{
            mSendBtn.setVisibility(View.GONE);
        }
        ImageLoader.getInstance().displayImage(itemData.cover,mTravelIv, UILUtils.getRadiusOption());
        mNoteNameTv.setText(itemData.title);
        String[] strArray=itemData.summary.split("\n");
        String maxLengthStr=strArray[0];
        for(String str:strArray){
            if(str.length()>maxLengthStr.length()){
                maxLengthStr=str;
            }
        }
        mNoteDescTv.setText(maxLengthStr);
        ImageLoader.getInstance().displayImage(itemData.avatar, mAvatarIv, UILUtils.getRadiusOption(LocalDisplay.dp2px(18)));
        mAuthorNameTv.setText(itemData.author);
        mFromTv.setText("from:"+itemData.source);
        mFromTv.setTypeface(Typeface.MONOSPACE, Typeface.ITALIC);
        mTimeTv.setText(simpleDateFormat.format(new Date(itemData.publishDate)));
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity,TravelNoteDetailActivity.class);
                intent.putExtra("travelNote",itemData);
                intent.putExtra("id",itemData.id);
                activity.startActivity(intent);
            }
        });

    }

    public interface OnMoreClickListener{
        void onMoreClick(View view);

    }
}
