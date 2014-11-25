package com.aizou.peachtravel.module.dest;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aizou.core.http.HttpCallBack;
import com.aizou.core.widget.listHelper.ViewHolderBase;
import com.aizou.peachtravel.R;
import com.aizou.peachtravel.base.PeachBaseActivity;
import com.aizou.peachtravel.bean.CommentBean;
import com.aizou.peachtravel.bean.PoiDetailBean;
import com.aizou.peachtravel.bean.PoiListBean;
import com.aizou.peachtravel.common.api.TravelApi;
import com.aizou.peachtravel.common.gson.CommonJson;
import com.aizou.peachtravel.common.utils.UILUtils;
import com.aizou.peachtravel.common.widget.TitleHeaderBar;
import com.nostra13.universalimageloader.core.ImageLoader;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Rjm on 2014/11/24.
 */
public class PoiListActivity extends PeachBaseActivity {
    @InjectView(R.id.ly_header_bar_title_wrap)
    TitleHeaderBar mLyHeaderBarTitleWrap;
    @InjectView(R.id.iv_city_poi)
    ImageView mIvCityPoi;
    @InjectView(R.id.tv_city_name)
    TextView mTvCityName;
    @InjectView(R.id.tv_city_poi_desc)
    TextView mTvCityPoiDesc;
    private ListView mPoiListLv;
    private int page=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
    }

    private void initData() {

    }

    private void initView() {
        setContentView(R.layout.activity_poi_list);
        mPoiListLv = (ListView) findViewById(R.id.lv_poi_list);
        View headerView = View.inflate(mContext, R.layout.view_poi_list_header, null);
        mPoiListLv.addHeaderView(headerView);
        ButterKnife.inject(this);

    }

    private void getPoiListData(String type, String cityId) {
        if (PoiDetailBean.RESTAURANT.equals(type)) {
            TravelApi.getRESTList(cityId,page, new HttpCallBack<String>() {
                @Override
                public void doSucess(String result, String method) {
                    CommonJson<PoiListBean> poiListResult = CommonJson.fromJson(result, PoiListBean.class);
                    if (poiListResult.code == 0) {
                        bindView(poiListResult.result);
                        page++;
                    }

                }

                @Override
                public void doFailure(Exception error, String msg, String method) {

                }
            });
        }
    }

    private void bindView(PoiListBean result) {
        ImageLoader.getInstance().displayImage(result.images.get(0).url, mIvCityPoi, UILUtils.getDefaultOption());
        mTvCityName.setText(result.zhName);
        mTvCityPoiDesc.setText(result.desc);


    }

    public class PoiListViewHolder extends ViewHolderBase<PoiDetailBean> {

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

        @Override
        public View createView(LayoutInflater layoutInflater) {
            View view = View.inflate(mContext, R.layout.row_poi_list, null);
            ButterKnife.inject(this, view);
            return view;
        }

        @Override
        public void showData(int position, PoiDetailBean itemData) {
            mTvPoiName.setText(itemData.zhName);
            mBtnAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
            mTvPrice.setText(itemData.priceDesc);
            mTvAddr.setText(itemData.address);
            if(itemData.images!=null&&itemData.images.size()>0)
            ImageLoader.getInstance().displayImage(itemData.images.get(0).url,mIvPoiImage,UILUtils.getDefaultOption());
            mRatingBarPoi.setRating(itemData.rating);
            if(itemData.comments==null||itemData.comments.size()==0){
                mRlComment.setVisibility(View.GONE);
            }else{
                mRlComment.setVisibility(View.VISIBLE);
                CommentBean commentBean = itemData.comments.get(0);
                mTvCommentName.setText(commentBean.nickName);
                mTvCommentNum.setText(itemData.commentCnt+"");
                mTvCommentContent.setText(commentBean.commentDetails);
            }

        }
    }
}
