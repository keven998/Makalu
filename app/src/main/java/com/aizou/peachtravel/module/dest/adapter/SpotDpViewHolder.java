package com.aizou.peachtravel.module.dest.adapter;

/**
 * Created by lxp_dqm07 on 2015/5/4.
 */

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aizou.core.dialog.ToastUtil;
import com.aizou.core.widget.listHelper.ViewHolderBase;
import com.aizou.peachtravel.R;
import com.aizou.peachtravel.bean.TravelNoteBean;
import com.aizou.peachtravel.common.imageloader.UILUtils;
import com.aizou.peachtravel.module.dest.TravelNoteDetailActivity;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SpotDpViewHolder extends ViewHolderBase<TravelNoteBean> {
    View view;
    ImageView dp_pic;
    TextView dp_name,dp_time,dp_msg;
    OnSendClickListener mOnSendClickListener;
    Activity activity;

    private boolean mIsShowSend;
    private boolean mIsShowMore;

    private DisplayImageOptions picOptions;

    public SpotDpViewHolder(Activity context){
        activity= context;
        picOptions = UILUtils.getRadiusOption(15);
    }

    public void setOnSendClickListener(OnSendClickListener onSendClickListener){
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


        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* MobclickAgent.onEvent(activity, "event_city_travel_note_item");
                Intent intent = new Intent(activity, TravelNoteDetailActivity.class);
                intent.putExtra("travelNote", itemData);
                intent.putExtra("id", itemData.id);
                activity.startActivity(intent);*/
                ToastUtil.getInstance(activity).showToast("详情页");
            }
        });

    }

    public interface OnSendClickListener{
        void onSendClick(View view,TravelNoteBean itemData);
    }

}
