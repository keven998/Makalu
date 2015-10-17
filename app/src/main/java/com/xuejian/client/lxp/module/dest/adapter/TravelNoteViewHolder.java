package com.xuejian.client.lxp.module.dest.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aizou.core.utils.LocalDisplay;
import com.aizou.core.widget.listHelper.ViewHolderBase;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.bean.TravelNoteBean;
import com.xuejian.client.lxp.module.dest.TravelNoteDetailActivity;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Rjm on 2014/12/10.
 */
public class TravelNoteViewHolder extends ViewHolderBase<TravelNoteBean> {
    View view;
    RelativeLayout mSendRl;
    TextView mSendBtn;
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

    private DisplayImageOptions options;

    public TravelNoteViewHolder(Activity context, boolean isShowSend, boolean isShowMore) {
        mIsShowSend = isShowSend;
        mIsShowMore = isShowMore;
        activity = context;
        //   picOptions = UILUtils.getRadiusOption();
        options = new DisplayImageOptions.Builder()
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .showImageForEmptyUri(R.drawable.ic_home_more_avatar_unknown_round)
                .showImageOnFail(R.drawable.ic_home_more_avatar_unknown_round)
                .resetViewBeforeLoading(true)
                .cacheOnDisk(true)
                .displayer(new RoundedBitmapDisplayer(activity.getResources().getDimensionPixelSize(R.dimen.page_more_header_frame_height) - LocalDisplay.dp2px(20))) // 设置成圆角图片
                .build();
    }

    public void setOnSendClickListener(OnSendClickListener onSendClickListener) {
        mOnSendClickListener = onSendClickListener;
    }


    @Override
    public View createView(LayoutInflater layoutInflater) {
        view = layoutInflater.inflate(R.layout.row_travels, null);
        mSendRl = (RelativeLayout) view.findViewById(R.id.rl_send);
        mSendBtn = (TextView) view.findViewById(R.id.btn_send);
        mTravelIv = (ImageView) view.findViewById(R.id.iv_travels);
        mNoteNameTv = (TextView) view.findViewById(R.id.tv_note_title);
        mNoteDescTv = (TextView) view.findViewById(R.id.tv_travels_desc);
        mPropertyTv = (TextView) view.findViewById(R.id.tv_property);
        return view;
    }

    @Override
    public void showData(int position, final TravelNoteBean itemData) {
        if (mIsShowSend) {
            mSendRl.setVisibility(View.VISIBLE);
            mSendBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnSendClickListener != null) {
                        mOnSendClickListener.onSendClick(v, itemData);
                    }
                }
            });
        } else {
            mSendRl.setVisibility(View.GONE);
        }
        ImageLoader.getInstance().displayImage(itemData.authorAvatar, mTravelIv, options);
        // ImageLoader.getInstance().displayImage(itemData.authorAvatar,mTravelIv, picOptions);
        mNoteNameTv.setText(itemData.title);
        String[] strArray = itemData.summary.split("\n");
        String maxLengthStr = strArray[0];
        for (String str : strArray) {
            if (str.length() > maxLengthStr.length()) {
                maxLengthStr = str;
            }
        }
        mNoteDescTv.setText(maxLengthStr);
        mPropertyTv.setText(String.format("%s    %s", itemData.authorName, simpleDateFormat.format(new Date(itemData.publishTime))));
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, TravelNoteDetailActivity.class);
                intent.putExtra("travelNote", itemData);
                intent.putExtra("id", itemData.detailUrl);
                activity.startActivity(intent);
            }
        });

    }

    public interface OnSendClickListener {
        void onSendClick(View view, TravelNoteBean itemData);
    }

}
