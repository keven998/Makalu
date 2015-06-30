package com.xuejian.client.lxp.module.dest;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aizou.core.dialog.ToastUtil;
import com.aizou.core.http.HttpCallBack;
import com.aizou.core.utils.LocalDisplay;
import com.aizou.core.widget.listHelper.ListViewDataAdapter;
import com.aizou.core.widget.listHelper.ViewHolderBase;
import com.aizou.core.widget.listHelper.ViewHolderCreator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.PeachBaseActivity;
import com.xuejian.client.lxp.bean.LocBean;
import com.xuejian.client.lxp.bean.TravelNoteBean;
import com.xuejian.client.lxp.common.api.OtherApi;
import com.xuejian.client.lxp.common.api.TravelApi;
import com.xuejian.client.lxp.common.dialog.DialogManager;
import com.xuejian.client.lxp.common.gson.CommonJson;
import com.xuejian.client.lxp.common.gson.CommonJson4List;
import com.xuejian.client.lxp.common.imageloader.UILUtils;
import com.xuejian.client.lxp.common.utils.IMUtils;
import com.xuejian.client.lxp.common.widget.TitleHeaderBar;
import com.xuejian.client.lxp.common.widget.pulltozoomview.PullToZoomListViewEx;
import com.xuejian.client.lxp.module.PeachWebViewActivity;
import com.xuejian.client.lxp.module.dest.adapter.TravelNoteViewHolder;

import java.util.ArrayList;

/**
 * Created by Rjm on 2014/11/13.
 */
public class CityDetailActivity extends PeachBaseActivity implements View.OnClickListener {
    private PullToZoomListViewEx mTravelLv;
    private RelativeLayout titleBar;
    private View bottom_line;
    private ImageView mCityIv1;
    private ImageView mCityIv2;
    private ImageView mCityIv3;
    private ImageView mCityIv4;
    private ImageView mCityIv5;
    private ImageView mCityIv6;
    private ImageView mCityIv;
    private TextView mCityNameTv;
    private TextView mCityNameEn;
    private TextView mTTview;
    private TextView mCostTimeTv;
    private TextView bestMonthTv;
    private ImageView foodTv, shoppingTv, spotsTv, travelTv;
    private ListViewDataAdapter travelAdapter;
    private LocBean locDetailBean;
    private String locId;
    private ImageView shareToChat;
    private PopupWindow mPop;
    private boolean isFromStrategy;
    ImageView [] imageViews;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_detail);
        isFromStrategy = getIntent().getBooleanExtra("isFromStrategy", false);
        initView();
        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        MobclickAgent.onPageStart("page_city_detail");
    }

    @Override
    protected void onPause() {
        super.onPause();
//        MobclickAgent.onPageEnd("page_city_detail");
    }

    private void initData() {
        locId = getIntent().getStringExtra("id");
        getCityDetailData(locId);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mPop != null && mPop.isShowing()) {
                mPop.dismiss();
            } else {
                finish();
            }
        }
        return false;
    }

    private void initView() {
     //   PullToZoomListViewEx travelLv = (PullToZoomListViewEx) findViewById(R.id.lv_city_detail);
        ListView travelLv = (ListView) findViewById(R.id.lv_city_detail);
       // mTravelLv = travelLv;
        mCityIv1 = (ImageView) findViewById(R.id.iv_city_1);
        mCityIv2 = (ImageView) findViewById(R.id.iv_city_2);
        mCityIv3 = (ImageView) findViewById(R.id.iv_city_3);
        mCityIv4 = (ImageView) findViewById(R.id.iv_city_4);
        mCityIv5 = (ImageView) findViewById(R.id.iv_city_5);
        mCityIv6 = (ImageView) findViewById(R.id.iv_city_6);
        imageViews=new ImageView[]{mCityIv1,mCityIv2,mCityIv3,mCityIv4,mCityIv5,mCityIv6};
        TitleHeaderBar titleBar = (TitleHeaderBar) findViewById(R.id.title_bar);
        titleBar.getTitleTextView().setText("");
        titleBar.enableBackKey(true);
       // titleBar = (RelativeLayout) findViewById(R.id.title_bar);
//        bottom_line = findViewById(R.id.title_bottom_line);
//        shareToChat = (ImageView) findViewById(R.id.city_detail_chat);
//        shareToChat.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                MobclickAgent.onEvent(mContext, "event_city_share_to_talk");
//                IMUtils.onClickImShare(CityDetailActivity.this);
//            }
//        });
        findViewById(R.id.tv_title_bar_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

      //  View hv;
      //  hv = View.inflate(mContext, R.layout.view_city_detail_head, null);
      //  travelLv.setHeaderView(hv);
        // travelLv.getRootView().addFooterView(getLayoutInflater().inflate(R.layout.no_more_action_list_footerview, null));
      //  View zoomView = hv.findViewById(R.id.ly1);
      //  travelLv.setZoomView(zoomView);
        mTTview = (TextView)findViewById(R.id.travel_title);
        mCityNameTv = (TextView) findViewById(R.id.tv_city_name);
        mCityNameEn = (TextView) findViewById(R.id.tv_city_name_en);
        mCostTimeTv = (TextView) findViewById(R.id.tv_cost_time);
        bestMonthTv = (TextView) findViewById(R.id.tv_best_month);
        travelTv = (ImageView) findViewById(R.id.tv_travel);
        spotsTv = (ImageView) findViewById(R.id.tv_spots);
        foodTv = (ImageView)findViewById(R.id.tv_restaurant);
        shoppingTv = (ImageView)findViewById(R.id.tv_shopping);


        travelAdapter = new ListViewDataAdapter(new ViewHolderCreator() {
            @Override
            public ViewHolderBase createViewHolder() {
                TravelNoteViewHolder viewHolder = new TravelNoteViewHolder(CityDetailActivity.this, false, true);
                return viewHolder;
            }
        });
       travelLv.setAdapter(travelAdapter);
      //  travelLv.setParallax(false);


        //用来点击查看更多
       findViewById(R.id.tv_more).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MobclickAgent.onEvent(mContext, "event_more_city_travel_notes");
                Intent intent = new Intent(mContext, MoreTravelNoteActivity.class);
                intent.putExtra("id", locId);
                startActivity(intent);
            }
        });

        /*final int max = LocalDisplay.dp2px(170);
        final int min = LocalDisplay.dp2px(80);
        travelLv.setOnScrollYListener(new PullToZoomBase.OnScrollYListener() {
            @Override
            public void onScrollY(float scrollY) {
                float height = min;
                if (scrollY > max) {
                    height = max - min;
                } else {
                    if (scrollY < min) {
                        height = 0;
                    } else {
                        height = scrollY - min;
                    }
                }
                    int alpha = (int) (height * 255 / (max - min));
                    setTitleAlpha(alpha);

            }
        });*/
        // setTitleAlpha(10);
    }

    private void setTitleAlpha(int alpha) {
//        if (alpha <= 1) {
        titleBar.getBackground().setAlpha(alpha);
        bottom_line.getBackground().setAlpha(alpha);
        //   mTitleTv.setTextColor(mTitleTv.getTextColors().withAlpha(alpha));
//        }
    }

    private void getCityDetailData(final String id) {
        DialogManager.getInstance().showModelessLoadingDialog(mContext);
        TravelApi.getCityDetail(id, (int) (LocalDisplay.SCREEN_WIDTH_PIXELS / 1.5), new HttpCallBack<String>() {
            @Override
            public void doSuccess(String result, String method) {
                CommonJson<LocBean> detailResult = CommonJson.fromJson(result, LocBean.class);
                if (detailResult.code == 0) {
                    bindView(detailResult.result);
                    getTravelNotes(id);
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

    private void getTravelNotes(String locId) {
        OtherApi.getTravelNoteByLocId(locId, 0, 3, new HttpCallBack<String>() {
            @Override
            public void doSuccess(String result, String method) {
                DialogManager.getInstance().dissMissModelessLoadingDialog();
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
                DialogManager.getInstance().dissMissModelessLoadingDialog();
//                ToastUtil.getInstance(CityDetailActivity.this).showToast(getResources().getString(R.string.request_network_failed));
            }
        });
    }

    private void bindView(final LocBean detailBean) {
        locDetailBean = detailBean;
        if (isFromStrategy) {
            findViewById(R.id.tv_title_bar_right).setVisibility(View.GONE);
        }
        findViewById(R.id.tv_title_bar_right).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showActionDialog();
            }
        });
//        if (detailBean.images != null && detailBean.images.size() > 0) {
//
//            ImageLoader.getInstance().displayImage(detailBean.images.get(0).url, mCityIv, UILUtils.getDefaultOption());
//        }
        if (detailBean.images != null && detailBean.images.size() > 0) {
            for (int i = 0; i < detailBean.images.size(); i++) {
                System.out.println(detailBean.images.get(i).url);
                ImageLoader.getInstance().displayImage(detailBean.images.get(i).url, imageViews[i], UILUtils.getDefaultOption());
                if (i==5)break;
            }
        }
//        mCityIv.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                MobclickAgent.onEvent(mContext, "event_city_photoes");
//                Intent intent = new Intent(mContext, CityPictureActivity.class);
//                intent.putExtra("id", locDetailBean.id);
//                intent.putExtra("title", locDetailBean.zhName);
//                startActivityWithNoAnim(intent);
//                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
//            }
//        });

        if (detailBean.imageCnt > 100) {
            detailBean.imageCnt = 100;
        }
        System.out.println(detailBean.zhName + detailBean.timeCostDesc + detailBean.travelMonth);
        mCityNameTv.setText(detailBean.zhName);
        mCostTimeTv.setText(String.format("～参考游玩时间·%s～", detailBean.timeCostDesc));
        bestMonthTv.setText(String.format("～推荐游玩时间：%s～",  detailBean.travelMonth.substring(0,detailBean.travelMonth.indexOf("。"))));
        foodTv.setOnClickListener(this);
        shoppingTv.setOnClickListener(this);
        spotsTv.setOnClickListener(this);
        travelTv.setOnClickListener(this);
        if (detailBean.enName.equals("") || detailBean.enName == null) {
            mCityNameEn.setText(detailBean.desc);
            String spannable=mCityNameEn.getText().toString();
            System.out.println(spannable.length());
            mCityNameEn.setText(spannable.substring(0,spannable.length()-5));

        } else {
            mCityNameEn.setText(detailBean.enName);
        }
//        mCityNameEn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                LayoutInflater mLayoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
////自定义布局
//                ViewGroup menuView = (ViewGroup) mLayoutInflater.inflate(
//                        R.layout.text_diaplay, null, true);
//                TextView pop_dismiss = (TextView) menuView.findViewById(R.id.pop_dismiss);
//
//                TextView tv = (TextView) menuView.findViewById(R.id.msg);
//                tv.setText(detailBean.desc);
//                mPop = new PopupWindow(menuView, FlowLayout.LayoutParams.MATCH_PARENT,
//                        FlowLayout.LayoutParams.MATCH_PARENT, true);
//                mPop.setContentView(menuView);//设置包含视图
//                mPop.setWidth(FlowLayout.LayoutParams.MATCH_PARENT);
//                mPop.setHeight(FlowLayout.LayoutParams.MATCH_PARENT);
//                mPop.setAnimationStyle(R.style.PopAnimation);
//                mPop.showAtLocation(findViewById(R.id.city_parent), Gravity.BOTTOM, 0, 0);
//                pop_dismiss.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        mPop.dismiss();
//                    }
//                });
//            }
//        });
//        mTTview.setText(String.format("玩转%s", detailBean.zhName));

        // mTitleTv.setText(detailBean.zhName);
    }

    public void intentToTravel(View view) {
        if (locDetailBean != null) {
            MobclickAgent.onEvent(mContext, "event_city_information");
            Intent intent = new Intent(mContext, PeachWebViewActivity.class);
            intent.putExtra("url", locDetailBean.playGuide);
            intent.putExtra("enable_bottom_bar", true);
            intent.putExtra("title", "旅游指南");//String.format("玩转%s", mCityNameTv.getText()));
            startActivity(intent);
        } else {
            Log.e("CLICK", "没有数据");
        }
    }

    public void intentToSpots(View view) {
        MobclickAgent.onEvent(mContext, "event_city_spots");
        Intent intent = new Intent(mContext, SpotListActivity.class);
        ArrayList<LocBean> locList = new ArrayList<LocBean>();
        locList.add(locDetailBean);
        intent.putParcelableArrayListExtra("locList", locList);
        intent.putExtra("type", TravelApi.PeachType.SPOT);
        startActivity(intent);
    }

    public void intentToFood(View view) {
        MobclickAgent.onEvent(mContext, "event_city_delicacy");
        Intent intent = new Intent(mContext, PoiListActivity.class);
        ArrayList<LocBean> locList = new ArrayList<LocBean>();
        locList.add(locDetailBean);
        intent.putParcelableArrayListExtra("locList", locList);
        intent.putExtra("type", TravelApi.PeachType.RESTAURANTS);
        intent.putExtra("value", locDetailBean.diningTitles);
        intent.putExtra("isFromCityDetail", true);
        startActivity(intent);
    }

    public void intentToShopping(View view) {
        MobclickAgent.onEvent(mContext, "event_city_shopping");
        Intent intent = new Intent(mContext, PoiListActivity.class);
        ArrayList<LocBean> locList = new ArrayList<LocBean>();
        locList.add(locDetailBean);
        intent.putParcelableArrayListExtra("locList", locList);
        intent.putExtra("type", TravelApi.PeachType.SHOPPING);
        intent.putExtra("value", locDetailBean.shoppingTitles);
        intent.putExtra("isFromCityDetail", true);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_travel:
                Log.e("CLICK", "点击生效");
                intentToTravel(v);
                break;
            case R.id.tv_spots:
                intentToSpots(v);
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
        IMUtils.onShareResult(mContext, locDetailBean, requestCode, resultCode, data, null);
    }

    private void showActionDialog() {
        /*final Activity act = this;
        final AlertDialog dialog = new AlertDialog.Builder(act).create();
        View contentView = View.inflate(act, R.layout.dialog_city_detail_action, null);
        contentView.findViewById(R.id.btn_go_plan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {*/
        MobclickAgent.onEvent(mContext, "event_create_new_trip_plan_city");
        Intent intent = new Intent(this, SelectDestActivity.class);
        ArrayList<LocBean> locList = new ArrayList<LocBean>();
        locList.add(locDetailBean);
        intent.putExtra("locList", locList);
        startActivity(intent);
          /*      dialog.dismiss();
            }
        });
        contentView.findViewById(R.id.btn_go_share).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MobclickAgent.onEvent(mContext,"event_city_share_to_talk");
                IMUtils.onClickImShare(act);
                dialog.dismiss();
            }
        });
        contentView.findViewById(R.id.btn_cancle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
        WindowManager windowManager = act.getWindowManager();
        Window window = dialog.getWindow();
        window.setContentView(contentView);
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = (int) (display.getWidth()); // 设置宽度
        window.setAttributes(lp);
        window.setGravity(Gravity.BOTTOM); // 此处可以设置dialog显示的位置
        window.setWindowAnimations(R.style.SelectPicDialog); // 添加动画*/
    }

}
