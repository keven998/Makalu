package com.aizou.peachtravel.module.dest;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.aizou.core.http.HttpCallBack;
import com.aizou.core.widget.listHelper.ListViewDataAdapter;
import com.aizou.core.widget.listHelper.ViewHolderBase;
import com.aizou.core.widget.listHelper.ViewHolderCreator;
import com.aizou.peachtravel.R;
import com.aizou.peachtravel.base.PeachBaseActivity;
import com.aizou.peachtravel.bean.LocBean;
import com.aizou.peachtravel.bean.TravelNoteBean;
import com.aizou.peachtravel.common.api.TravelApi;
import com.aizou.peachtravel.common.gson.CommonJson;
import com.aizou.peachtravel.common.utils.UILUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by Rjm on 2014/11/13.
 */
public class CityDetailActivity extends PeachBaseActivity {
    private ListView mTravelLv;
    private View headerView;
    private ImageView mCityIv;
    private TextView mPicNumTv;
    private TextView mCityNameTv;
    private ImageView mFavIv;
    private TextView mCityDescTv;
    private TextView mCostTimeTv;
    private TextView bestMonthTv;
    private View footerView;
    private ListViewDataAdapter travelAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_detail);
        initView();
        initData();

    }

    private void initData() {
        String id = getIntent().getStringExtra("id");
        id="53aa9a6410114e3fd47833bd";
        getCityDetailData(id);
    }

    private void initView(){
        mTravelLv = (ListView) findViewById(R.id.lv_city_detail);
        headerView = View.inflate(mContext,R.layout.view_city_detail_head,null);
        footerView = View.inflate(mContext,R.layout.view_city_detail_footer,null);
        mTravelLv.addHeaderView(headerView);
        mTravelLv.addFooterView(footerView);
        mCityIv = (ImageView) headerView.findViewById(R.id.iv_city_detail);
        mPicNumTv = (TextView) headerView.findViewById(R.id.tv_pic_num);
        mCityNameTv = (TextView) headerView.findViewById(R.id.tv_city_name);
        mCityDescTv = (TextView) headerView.findViewById(R.id.tv_city_desc);
        mCostTimeTv = (TextView) headerView.findViewById(R.id.tv_cost_time);
        bestMonthTv = (TextView) headerView.findViewById(R.id.tv_best_month);

        travelAdapter = new ListViewDataAdapter(new ViewHolderCreator() {
            @Override
            public ViewHolderBase createViewHolder() {
                return new TravelViewHolder();
            }
        });
        mTravelLv.setAdapter(travelAdapter);
    }

    private void getCityDetailData(String id){
        TravelApi.getCityDetail(id,new HttpCallBack<String>() {
            @Override
            public void doSucess(String result, String method) {
                CommonJson<LocBean> detailResult = CommonJson.fromJson(result,LocBean.class);
                if(detailResult.code==0){
                    bindView(detailResult.result);
                }

            }

            @Override
            public void doFailure(Exception error, String msg, String method) {

            }
        });
    }

    private void bindView(LocBean detailBean){
        if(detailBean.images!=null&&detailBean.images.size()>0)
        ImageLoader.getInstance().displayImage(detailBean.images.get(0).url,mCityIv,UILUtils.getDefaultOption());
        mCityIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext,CityPictureActivity.class);
                startActivity(intent);

            }
        });
        mPicNumTv.setText(detailBean.imageCount+"");
        mCityNameTv.setText(detailBean.zhName);
        mCityDescTv.setText(detailBean.desc);
        mCostTimeTv.setText(detailBean.timeCost+"天");
        bestMonthTv.setText(detailBean.travelMonth);
        travelAdapter.getDataList().addAll(detailBean.travelNote);
        travelAdapter.notifyDataSetChanged();

    }

    private class TravelViewHolder extends ViewHolderBase<TravelNoteBean>{
        ImageView mTravelIv;
        TextView mNoteNameTv;
        TextView mNoteDescTv;
        ImageView mAvatarIv;
        TextView mAuthorNameTv;
        TextView mFromTv;
        TextView mTimeTv;


        @Override
        public View createView(LayoutInflater layoutInflater) {
            View view = layoutInflater.inflate(R.layout.row_travels,null);
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
        public void showData(int position, TravelNoteBean itemData) {
            ImageLoader.getInstance().displayImage(itemData.cover,mTravelIv, UILUtils.getDefaultOption());
            mNoteNameTv.setText(itemData.title);
            mNoteDescTv.setText(itemData.desc);
            ImageLoader.getInstance().displayImage(itemData.authorAvatar,mAvatarIv,UILUtils.getDefaultOption());
            mAuthorNameTv.setText(itemData.authorName);
            mFromTv.setText(itemData.source);
            mTimeTv.setText(itemData.publishDate);

        }
    }

}
