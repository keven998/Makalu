package com.xuejian.client.lxp.module.dest.adapter;

/**
 * Created by lxp_dqm07 on 2015/5/4.
 */

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.aizou.core.widget.listHelper.ViewHolderBase;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.bean.TravelNoteBean;
import com.xuejian.client.lxp.common.imageloader.UILUtils;

public class SpotDpViewHolder extends ViewHolderBase<TravelNoteBean> {
    View view;
    ImageView dp_pic;
    TextView dp_name, dp_time, dp_msg;
    OnSendClickListener mOnSendClickListener;
    Activity activity;

    private boolean mIsShowSend;
    private boolean mIsShowMore;

    private DisplayImageOptions picOptions;

    public SpotDpViewHolder(Activity context) {
        activity = context;
        picOptions = UILUtils.getRadiusOption(15);
    }

    public void setOnSendClickListener(OnSendClickListener onSendClickListener) {
        mOnSendClickListener = onSendClickListener;
    }


    @Override
    public View createView(LayoutInflater layoutInflater) {
        view = layoutInflater.inflate(R.layout.spot_detail_view_cell, null);

        dp_pic = (ImageView) view.findViewById(R.id.spot_detail_dp_pic);
        dp_name = (TextView) view.findViewById(R.id.spot_detail_dp_name);
        dp_time = (TextView) view.findViewById(R.id.spot_detail_dp_time);
        dp_msg = (TextView) view.findViewById(R.id.spot_detail_dp_msg);


        return view;
    }


    @Override
    public void showData(int position, final TravelNoteBean itemData) {
        ImageLoader.getInstance().displayImage(itemData.getNoteImage(), dp_pic, picOptions);
        //添加字段值
        dp_name.setText(itemData.authorName);
        dp_time.setText(itemData.publishTime + "");
        dp_msg.setText(itemData.summary);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* MobclickAgent.onEvent(activity, "event_city_travel_note_item");
                Intent intent = new Intent(activity, TravelNoteDetailActivity.class);
                intent.putExtra("travelNote", itemData);
                intent.putExtra("id", itemData.id);
                activity.startActivity(intent);*/
                //ToastUtil.getInstance(activity).showToast("详情页");
            }
        });

    }

    public interface OnSendClickListener {
        void onSendClick(View view, TravelNoteBean itemData);
    }

}
