package com.aizou.peachtravel.module.dest;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.aizou.core.dialog.DialogManager;
import com.aizou.core.dialog.ToastUtil;
import com.aizou.core.http.HttpCallBack;
import com.aizou.core.widget.expandabletextview.ExpandableTextView;
import com.aizou.core.widget.listHelper.ListViewDataAdapter;
import com.aizou.core.widget.listHelper.ViewHolderBase;
import com.aizou.core.widget.listHelper.ViewHolderCreator;
import com.aizou.peachtravel.R;
import com.aizou.peachtravel.base.PeachBaseActivity;
import com.aizou.peachtravel.bean.LocBean;
import com.aizou.peachtravel.bean.ModifyResult;
import com.aizou.peachtravel.bean.PeachUser;
import com.aizou.peachtravel.bean.TravelNoteBean;
import com.aizou.peachtravel.common.account.AccountManager;
import com.aizou.peachtravel.common.api.H5Url;
import com.aizou.peachtravel.common.api.OtherApi;
import com.aizou.peachtravel.common.api.TravelApi;
import com.aizou.peachtravel.common.gson.CommonJson;
import com.aizou.peachtravel.common.gson.CommonJson4List;
import com.aizou.peachtravel.common.utils.UILUtils;
import com.aizou.peachtravel.common.widget.DrawableCenterTextView;
import com.aizou.peachtravel.common.widget.TitleHeaderBar;
import com.aizou.peachtravel.module.PeachWebViewActivity;
import com.aizou.peachtravel.module.dest.adapter.TravelNoteViewHolder;
import com.aizou.peachtravel.module.my.LoginActivity;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by Rjm on 2014/11/13.
 */
public class CityDetailActivity extends PeachBaseActivity implements View.OnClickListener {
    private ListView mTravelLv;
    private View headerView;
    private ImageView mCityIv;
    private TextView mPicNumTv;
    private TextView mCityNameTv;
    private ImageView mFavIv;
    private ExpandableTextView mCityDescTv;
    private TextView mCostTimeTv;
    private TextView bestMonthTv;
    private DrawableCenterTextView travelTv,foodTv,shoppingTv;
    private ListViewDataAdapter travelAdapter;
    private TitleHeaderBar titleHeaderBar;
    private LocBean locDetailBean;
    private String locId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_detail);
        initView();
        initData();

    }

    private void initData() {
        locId = getIntent().getStringExtra("id");
        locId="5473ccd7b8ce043a64108c46";
        getCityDetailData(locId);
        getTravelNotes(locId);
    }

    private void initView(){
        mTravelLv = (ListView) findViewById(R.id.lv_city_detail);
        titleHeaderBar = (TitleHeaderBar) findViewById(R.id.ly_header_bar_title_wrap);
        titleHeaderBar.setRightViewImageRes(R.drawable.ic_launcher);
        titleHeaderBar.enableBackKey(true);
        headerView = View.inflate(mContext,R.layout.view_city_detail_head,null);
        mTravelLv.addHeaderView(headerView);
        mCityIv = (ImageView) headerView.findViewById(R.id.iv_city_detail);
        mPicNumTv = (TextView) headerView.findViewById(R.id.tv_pic_num);
        mCityNameTv = (TextView) headerView.findViewById(R.id.tv_city_name);
        mCityDescTv = (ExpandableTextView) headerView.findViewById(R.id.tv_city_desc);
        mCostTimeTv = (TextView) headerView.findViewById(R.id.tv_cost_time);
        bestMonthTv = (TextView) headerView.findViewById(R.id.tv_best_month);
        mFavIv = (ImageView) headerView.findViewById(R.id.iv_fav);
        travelTv = (DrawableCenterTextView) headerView.findViewById(R.id.tv_travel);
        foodTv = (DrawableCenterTextView) headerView.findViewById(R.id.tv_restaurant);
        shoppingTv = (DrawableCenterTextView) headerView.findViewById(R.id.tv_shopping);
        travelAdapter = new ListViewDataAdapter(new ViewHolderCreator() {
            @Override
            public ViewHolderBase createViewHolder() {
                TravelNoteViewHolder viewHolder = new TravelNoteViewHolder(false,true);
                viewHolder.setOnMoreClickListener(new TravelNoteViewHolder.OnMoreClickListener() {
                    @Override
                    public void onMoreClick(View view) {
                        Intent intent = new Intent(mContext,MoreTravelNoteActivity.class);
                        intent.putExtra("id",locId);
                        startActivity(intent);
                    }
                });
                return viewHolder;
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

    private void getTravelNotes(String locId){
        OtherApi.getTravelNoteByLocId(locId, 0, 3, new HttpCallBack<String>() {
            @Override
            public void doSucess(String result, String method) {
                CommonJson4List<TravelNoteBean> detailResult = CommonJson4List.fromJson(result, TravelNoteBean.class);
                if (detailResult.code == 0) {
                    travelAdapter.getDataList().clear();
                    travelAdapter.getDataList().addAll(detailResult.result);
                    travelAdapter.notifyDataSetChanged();
                }

            }

            @Override
            public void doFailure(Exception error, String msg, String method) {

            }
        });
    }
    private void refreshFav(LocBean detailBean){
        if(detailBean.isFavorite){
            mFavIv.setImageResource(R.drawable.ic_fav);
        }else{
            mFavIv.setImageResource(R.drawable.ic_unfav);
        }
    }

    private void bindView(final LocBean detailBean){
        locDetailBean = detailBean;
        if(detailBean.images!=null&&detailBean.images.size()>0)
        ImageLoader.getInstance().displayImage(detailBean.images.get(0).url,mCityIv,UILUtils.getDefaultOption());
        mCityIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext,CityPictureActivity.class);
                intent.putExtra("id",locDetailBean.id);
                startActivity(intent);

            }
        });
        refreshFav(locDetailBean);

        mFavIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PeachUser user=AccountManager.getInstance().getLoginAccount(mContext);
                if(user==null){
                    ToastUtil.getInstance(mContext).showToast("请先登录");
                    Intent intent = new Intent(mContext, LoginActivity.class);
                    startActivity(intent);
                    return;
                }
                DialogManager.getInstance().showProgressDialog(CityDetailActivity.this);
                if(detailBean.isFavorite){
                    OtherApi.deleteFav(detailBean.id,new HttpCallBack<String>() {
                        @Override
                        public void doSucess(String result, String method) {
                            DialogManager.getInstance().dissMissProgressDialog();
                            CommonJson<ModifyResult> deleteResult = CommonJson.fromJson(result,ModifyResult.class);
                            if(deleteResult.code==0){
                                detailBean.isFavorite =false;
                                refreshFav(detailBean);
                            }

                        }

                        @Override
                        public void doFailure(Exception error, String msg, String method) {
                            DialogManager.getInstance().dissMissProgressDialog();
                        }
                    });
                }else{
                    OtherApi.addFav(detailBean.id, "locality", new HttpCallBack<String>() {
                        @Override
                        public void doSucess(String result, String method) {
                            DialogManager.getInstance().dissMissProgressDialog();
                            CommonJson<ModifyResult> deleteResult = CommonJson.fromJson(result,ModifyResult.class);
                            if(deleteResult.code==0){
                                detailBean.isFavorite =true;
                                refreshFav(detailBean);
                            }

                        }

                        @Override
                        public void doFailure(Exception error, String msg, String method) {
                            DialogManager.getInstance().dissMissProgressDialog();
                        }
                    });
                }
            }
        });

        titleHeaderBar.getTitleTextView().setText(detailBean.zhName);
        mPicNumTv.setText(detailBean.imageCnt+"");
        mCityNameTv.setText(detailBean.zhName);
        mCityDescTv.setText(detailBean.desc);
        mCostTimeTv.setText(detailBean.timeCost+"天");
        bestMonthTv.setText(detailBean.travelMonth);
        travelTv.setOnClickListener(this);
        foodTv.setOnClickListener(this);
        shoppingTv.setOnClickListener(this);

    }

    public void intentToTravel(View view){
//        Intent intent = new Intent(mContext,SpotDetailActivity.class);
//        startActivity(intent);
        Intent intent = new Intent(mContext,PeachWebViewActivity.class);
        intent.putExtra("url", H5Url.LOC_TRAVEL);
        startActivity(intent);
        //todo:跳转html
    }
    public void intentToFood(View view){
//        Intent intent = new Intent(mContext,PoiListActivity.class);
//        ArrayList<LocBean> locList =new ArrayList<LocBean>();
//        locList.add(locDetailBean);
//        intent.putParcelableArrayListExtra("locList", locList);
//        intent.putExtra("type", TravelApi.PoiType.RESTAURANTS);
//        startActivity(intent);
          Intent intent = new Intent(mContext,PoiDetailActivity.class);
          startActivity(intent);
        //todo:跳转美食
    }
    public void intentToShopping(View view){
        //todo:跳转购物
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_travel:
                intentToTravel(v);
                break;
            case R.id.tv_restaurant:
                intentToFood(v);
                break;
            case R.id.tv_shopping:
                intentToShopping(v);
                break;
        }
    }
}
