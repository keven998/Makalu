package com.aizou.peachtravel.module.dest;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.aizou.core.utils.LocalDisplay;
import com.aizou.peachtravel.common.dialog.DialogManager;
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
import com.aizou.peachtravel.common.utils.IMUtils;
import com.aizou.peachtravel.common.imageloader.UILUtils;
import com.aizou.peachtravel.common.widget.DrawableCenterTextView;
import com.aizou.peachtravel.common.widget.TitleHeaderBar;
import com.aizou.peachtravel.module.PeachWebViewActivity;
import com.aizou.peachtravel.module.dest.adapter.TravelNoteViewHolder;
import com.aizou.peachtravel.module.my.LoginActivity;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

/**
 * Created by Rjm on 2014/11/13.
 */
public class CityDetailActivity extends PeachBaseActivity implements View.OnClickListener {
    private ListView mTravelLv;
    private View headerView;
    private ImageView mCityIv;
    private TextView mPicNumTv;
    private TextView mCityNameTv;
    private CheckBox mFavCb;
    private ExpandableTextView mCityDescTv;
    private TextView mCostTimeTv;
    private ExpandableTextView bestMonthTv;
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
//        locId="5473ccd7b8ce043a64108c46";
        getCityDetailData(locId);
        getTravelNotes(locId);
    }

    private void initView(){
        mTravelLv = (ListView) findViewById(R.id.lv_city_detail);
        titleHeaderBar = (TitleHeaderBar) findViewById(R.id.ly_header_bar_title_wrap);
        titleHeaderBar.setRightViewImageRes(R.drawable.ic_share);
        titleHeaderBar.enableBackKey(true);
        View hv;
        hv = View.inflate(mContext,R.layout.view_city_detail_head, null);
        headerView = hv;
        mTravelLv.addHeaderView(hv);
        mCityIv = (ImageView) hv.findViewById(R.id.iv_city_detail);
        mPicNumTv = (TextView) hv.findViewById(R.id.tv_pic_num);
        mCityNameTv = (TextView) hv.findViewById(R.id.tv_city_name);
        mCityDescTv = (ExpandableTextView) hv.findViewById(R.id.tv_city_desc);
        mCostTimeTv = (TextView) hv.findViewById(R.id.tv_cost_time);
        bestMonthTv = (ExpandableTextView) hv.findViewById(R.id.tv_best_month);
        mFavCb = (CheckBox) hv.findViewById(R.id.iv_fav);
        travelTv = (DrawableCenterTextView) hv.findViewById(R.id.tv_travel);
        foodTv = (DrawableCenterTextView) hv.findViewById(R.id.tv_restaurant);
        shoppingTv = (DrawableCenterTextView) hv.findViewById(R.id.tv_shopping);
        travelAdapter = new ListViewDataAdapter(new ViewHolderCreator() {
            @Override
            public ViewHolderBase createViewHolder() {
                TravelNoteViewHolder viewHolder = new TravelNoteViewHolder(CityDetailActivity.this, false, true);
                return viewHolder;
            }
        });
        mTravelLv.setAdapter(travelAdapter);

        hv.findViewById(R.id.tv_more).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, MoreTravelNoteActivity.class);
                intent.putExtra("id", locId);
                startActivity(intent);
            }
        });
    }

    private void getCityDetailData(String id){
        TravelApi.getCityDetail(id, (int) (LocalDisplay.SCREEN_WIDTH_PIXELS/1.5),new HttpCallBack<String>() {
            @Override
            public void doSucess(String result, String method) {
                CommonJson<LocBean> detailResult = CommonJson.fromJson(result, LocBean.class);
                if(detailResult.code==0){
                    bindView(detailResult.result);
                } else {
//                    ToastUtil.getInstance(CityDetailActivity.this).showToast(getResources().getString(R.string.request_server_failed));
                }
            }

            @Override
            public void doFailure(Exception error, String msg, String method) {
                if (!isFinishing()) {
                    ToastUtil.getInstance(CityDetailActivity.this).showToast(getResources().getString(R.string.request_network_failed));
                }
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
                } else {
//                    ToastUtil.getInstance(CityDetailActivity.this).showToast(getResources().getString(R.string.request_server_failed));
                }
            }

            @Override
            public void doFailure(Exception error, String msg, String method) {
//                ToastUtil.getInstance(CityDetailActivity.this).showToast(getResources().getString(R.string.request_network_failed));
            }
        });
    }

    private void bindView(final LocBean detailBean){
        locDetailBean = detailBean;
        if(detailBean.images!=null&&detailBean.images.size()>0)
        ImageLoader.getInstance().displayImage(detailBean.images.get(0).url, mCityIv, UILUtils.getDefaultOption());
        mCityIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, CityPictureActivity.class);
                intent.putExtra("id", locDetailBean.id);
                intent.putExtra("title", locDetailBean.zhName);
                startActivity(intent);

            }
        });
        titleHeaderBar.setRightOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IMUtils.onClickImShare(mContext);

            }
        });

        mFavCb.setChecked(locDetailBean.isFavorite);
        mFavCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, final boolean b) {
                PeachUser user = AccountManager.getInstance().getLoginAccount(mContext);
                if (user == null) {
                    ToastUtil.getInstance(mContext).showToast("请先登录");
                    Intent intent = new Intent(mContext, LoginActivity.class);
                    startActivity(intent);
                    mFavCb.setChecked(!b);
                    return;
                }
                if (b) {
                    OtherApi.deleteFav(detailBean.id, new HttpCallBack<String>() {
                        @Override
                        public void doSucess(String result, String method) {
//                            DialogManager.getInstance().dissMissLoadingDialog();
                            CommonJson<ModifyResult> deleteResult = CommonJson.fromJson(result, ModifyResult.class);
                            if (deleteResult.code == 0 || deleteResult.code == getResources().getInteger(R.integer.response_favorite_exist)) {

                            } else {
                                mFavCb.setChecked(!b);
                            }
                        }

                        @Override
                        public void doFailure(Exception error, String msg, String method) {
                            mFavCb.setChecked(!b);
                        }
                    });
                } else {
                    OtherApi.addFav(detailBean.id, "locality", new HttpCallBack<String>() {
                        @Override
                        public void doSucess(String result, String method) {
                            CommonJson<ModifyResult> deleteResult = CommonJson.fromJson(result, ModifyResult.class);
                            if (deleteResult.code == 0) {

                            } else {
                                mFavCb.setChecked(!b);
                            }
                        }

                        @Override
                        public void doFailure(Exception error, String msg, String method) {
                            mFavCb.setChecked(!b);
                        }
                    });
                }
            }
        });

        titleHeaderBar.getTitleTextView().setText(detailBean.zhName);
        if(detailBean.imageCnt>100){
            detailBean.imageCnt=100;
        }
        mCityNameTv.setText(detailBean.zhName);
        mCityDescTv.setText(detailBean.desc);
        mCostTimeTv.setText(detailBean.timeCostDesc);
        bestMonthTv.setText(detailBean.travelMonth);
        travelTv.setOnClickListener(this);
        foodTv.setOnClickListener(this);
        shoppingTv.setOnClickListener(this);
    }

    public void intentToTravel(View view){
//        Intent intent = new Intent(mContext,SpotDetailActivity.class);
//        startActivity(intent);
        Intent intent = new Intent(mContext, PeachWebViewActivity.class);
        intent.putExtra("url", H5Url.LOC_TRAVEL + locId);
        intent.putExtra("title", String.format("玩转%s", mCityNameTv.getText()));
        startActivity(intent);
    }

    public void intentToFood(View view){
        Intent intent = new Intent(mContext, PoiListActivity.class);
        ArrayList<LocBean> locList = new ArrayList<LocBean>();
        locList.add(locDetailBean);
        intent.putParcelableArrayListExtra("locList", locList);
        intent.putExtra("type", TravelApi.PeachType.RESTAURANTS);
        startActivity(intent);
//          Intent intent = new Intent(mContext,PoiDetailActivity.class);
//          startActivity(intent);
    }

    public void intentToShopping(View view){
        Intent intent = new Intent(mContext, PoiListActivity.class);
        ArrayList<LocBean> locList = new ArrayList<LocBean>();
        locList.add(locDetailBean);
        intent.putParcelableArrayListExtra("locList", locList);
        intent.putExtra("type", TravelApi.PeachType.SHOPPING);
        startActivity(intent);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IMUtils.onShareResult(mContext,locDetailBean,requestCode,resultCode,data,null);
    }
}
