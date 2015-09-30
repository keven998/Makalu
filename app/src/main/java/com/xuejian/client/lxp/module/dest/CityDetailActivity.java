package com.xuejian.client.lxp.module.dest;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aizou.core.dialog.ToastUtil;
import com.aizou.core.http.HttpCallBack;
import com.aizou.core.utils.LocalDisplay;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.umeng.analytics.MobclickAgent;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.PeachBaseActivity;
import com.xuejian.client.lxp.bean.ExpertBean;
import com.xuejian.client.lxp.bean.LocBean;
import com.xuejian.client.lxp.bean.TravelNoteBean;
import com.xuejian.client.lxp.common.account.AccountManager;
import com.xuejian.client.lxp.common.api.OtherApi;
import com.xuejian.client.lxp.common.api.TravelApi;
import com.xuejian.client.lxp.common.api.UserApi;
import com.xuejian.client.lxp.common.dialog.DialogManager;
import com.xuejian.client.lxp.common.gson.CommonJson;
import com.xuejian.client.lxp.common.gson.CommonJson4List;
import com.xuejian.client.lxp.common.utils.IMUtils;
import com.xuejian.client.lxp.module.PeachWebViewActivity;
import com.xuejian.client.lxp.module.my.LoginActivity;
import com.xuejian.client.lxp.module.my.TravelExpertApplyActivity;
import com.xuejian.client.lxp.module.toolbox.HisMainPageActivity;
import com.xuejian.client.lxp.module.toolbox.im.GuilderListActivity;
import com.xuejian.client.lxp.module.toolbox.im.adapter.ExpertAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Rjm on 2014/11/13.
 */
public class CityDetailActivity extends PeachBaseActivity implements View.OnClickListener {
    @InjectView(R.id.recommend_plan)
    TextView recommend_plan;
    @InjectView(R.id.recommend_note)
    TextView recommend_note;
    @InjectView(R.id.tv_all_expert)
    RelativeLayout rl_all_expert;
    @InjectView(R.id.lv_city_detail)
    ListView expertListview;
    @InjectView(R.id.apply_expert)
    ImageView apply_expert;
    private ImageView mCityIv1;
    private ImageView mCityIv2;
    private ImageView mCityIv3;
    private ImageView mCityIv4;
    private ImageView mCityIv5;
    private ImageView mCityIv6;
    private TextView mCityNameTv;
    private TextView mCityDesc;
    private TextView mCostTimeTv;
    private TextView bestMonthTv;
    private ImageView iv_create;
    private ImageView iv_share;
    private ImageView foodTv, shoppingTv, spotsTv, travelTv;
    private LocBean locDetailBean;
    private String locId;
    private boolean isFromStrategy;
    ImageView[] imageViews;
    private CheckedTextView tv_traveled;
    private CheckedTextView tv_like;
    boolean isVote;
    boolean isTraveled;
    DisplayImageOptions options;
    ExpertAdapter adapter;
    ArrayList<ExpertBean> expertList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_detail);
        ButterKnife.inject(this);
        isFromStrategy = getIntent().getBooleanExtra("isFromStrategy", false);
        options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true).bitmapConfig(Bitmap.Config.ARGB_8888)
                .resetViewBeforeLoading(true)
                .showImageOnFail(R.drawable.ic_home_more_avatar_unknown_round)
                .showImageForEmptyUri(R.drawable.ic_home_more_avatar_unknown_round)
                .displayer(new RoundedBitmapDisplayer(getResources().getDimensionPixelSize(R.dimen.page_more_header_frame_height) - LocalDisplay.dp2px(20))) // 设置成圆角图片
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT).build();
        initView();
        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("page_city_detail");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("page_city_detail");
        MobclickAgent.onPause(this);
    }

    private void initData() {
        locId = getIntent().getStringExtra("id");
        getCityDetailData(locId);

    }

    private void initView() {
        WindowManager wm = (WindowManager) this
                .getSystemService(Context.WINDOW_SERVICE);

        int width = wm.getDefaultDisplay().getWidth();
        int ivWidth = (width - LocalDisplay.dp2px(84)) / 3;
        mCityIv1 = (ImageView) findViewById(R.id.iv_city_1);
        mCityIv2 = (ImageView) findViewById(R.id.iv_city_2);
        mCityIv3 = (ImageView) findViewById(R.id.iv_city_3);
        mCityIv4 = (ImageView) findViewById(R.id.iv_city_4);
        mCityIv5 = (ImageView) findViewById(R.id.iv_city_5);
        mCityIv6 = (ImageView) findViewById(R.id.iv_city_6);

        mCityIv1.getLayoutParams().width = ivWidth;
        mCityIv2.getLayoutParams().width = ivWidth;
        mCityIv3.getLayoutParams().width = ivWidth;
        mCityIv4.getLayoutParams().width = ivWidth;
        mCityIv5.getLayoutParams().width = ivWidth;
        mCityIv6.getLayoutParams().width = ivWidth;

        mCityIv1.getLayoutParams().height = ivWidth;
        mCityIv2.getLayoutParams().height = ivWidth;
        mCityIv3.getLayoutParams().height = ivWidth;
        mCityIv4.getLayoutParams().height = ivWidth;
        mCityIv5.getLayoutParams().height = ivWidth;
        mCityIv6.getLayoutParams().height = ivWidth;


        iv_create = (ImageView) findViewById(R.id.iv_create_plan);
        imageViews = new ImageView[]{mCityIv1, mCityIv2, mCityIv3, mCityIv4, mCityIv5, mCityIv6};
        mCityNameTv = (TextView) findViewById(R.id.tv_city_name);
        mCityDesc = (TextView) findViewById(R.id.tv_city_desc);
        mCostTimeTv = (TextView) findViewById(R.id.tv_cost_time);
        bestMonthTv = (TextView) findViewById(R.id.tv_best_month);
        travelTv = (ImageView) findViewById(R.id.tv_travel);
        spotsTv = (ImageView) findViewById(R.id.tv_spots);
        foodTv = (ImageView) findViewById(R.id.tv_restaurant);
        shoppingTv = (ImageView) findViewById(R.id.tv_shopping);
        iv_share = (ImageView) findViewById(R.id.iv_talk_share);
        iv_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MobclickAgent.onEvent(CityDetailActivity.this, "navigation_item_lxp_city_share");
                IMUtils.onClickImShare(CityDetailActivity.this);
            }
        });
        expertListview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_MOVE:
                        return true;
                    default:
                        break;
                }
                return false;
            }
        });

        tv_like = (CheckedTextView) findViewById(R.id.tv_like);
        tv_traveled = (CheckedTextView) findViewById(R.id.tv_hasGone);
        findViewById(R.id.iv_nav_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void getCityDetailData(final String id) {
        try {
            DialogManager.getInstance().showModelessLoadingDialog(this);
        } catch (Exception e) {
            DialogManager.getInstance().dissMissModelessLoadingDialog();
        }
        TravelApi.getCityDetail(id, (int) (LocalDisplay.SCREEN_WIDTH_PIXELS / 1.5), new HttpCallBack<String>() {
            @Override
            public void doSuccess(String result, String method) {
                CommonJson<LocBean> detailResult = CommonJson.fromJson(result, LocBean.class);
                if (detailResult.code == 0) {
                    bindView(detailResult.result);
                    // getTravelNotesbyKeyword(detailResult.result.zhName);
                    getLocalExpert(detailResult.result.zhName);
                    DialogManager.getInstance().dissMissModelessLoadingDialog();
                } else {
//                    ToastUtil.getInstance(CityDetailActivity.this).showToast(getResources().getString(R.string.request_server_failed));
                    DialogManager.getInstance().dissMissModelessLoadingDialog();
                }
            }

            @Override
            public void doFailure(Exception error, String msg, String method) {
                if (!isFinishing()) {
                    ToastUtil.getInstance(CityDetailActivity.this).showToast(getResources().getString(R.string.request_network_failed));
                }
                DialogManager.getInstance().dissMissModelessLoadingDialog();
            }

            @Override
            public void doFailure(Exception error, String msg, String method, int code) {

            }
        });
    }

    private void getLocalExpert(String zhName) {
        UserApi.searchExpert(zhName, new HttpCallBack<String>() {
            @Override
            public void doSuccess(String result, String method) {
                CommonJson4List<ExpertBean> expertresult = CommonJson4List.fromJson(result, ExpertBean.class);
                if (expertresult.code == 0) {
                    bindExpertView(expertresult.result);

                }
            }

            @Override
            public void doFailure(Exception error, String msg, String method) {

            }

            @Override
            public void doFailure(Exception error, String msg, String method, int code) {

            }
        });
    }

    private void bindExpertView(List<ExpertBean> result) {
        apply_expert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext, TravelExpertApplyActivity.class));
            }
        });
        if (result == null || result.size() == 0) {
            rl_all_expert.setVisibility(View.GONE);
            apply_expert.setVisibility(View.VISIBLE);
            return;
        }
        if (result.size() <=2) apply_expert.setVisibility(View.VISIBLE);
//        AbsListView.LayoutParams abp = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT);
//        abp.height = 400;
//        View footView = new View(mContext);
//        footView.setLayoutParams(abp);
//        expertListview.addFooterView(footView);
        expertListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (adapter.getCount() <= position) return;
                Intent intent = new Intent();
                intent.setClass(mContext, HisMainPageActivity.class);
                intent.putExtra("userId", (long) ((ExpertBean) adapter.getItem(position)).userId);
                startActivity(intent);
            }
        });
        adapter = new ExpertAdapter(mContext, 5);
        expertListview.setAdapter(adapter);
        adapter.getDataList().addAll(result);
        setListViewHeightBasedOnChildren(expertListview);
    }

    private void getTravelNotesbyKeyword(final String keyword) {
        OtherApi.getTravelNoteByKeyword(keyword, 0, 3, new HttpCallBack<String>() {
            @Override
            public void doSuccess(String result, String method) {
                DialogManager.getInstance().dissMissModelessLoadingDialog();
                CommonJson4List<TravelNoteBean> detailResult = CommonJson4List.fromJson(result, TravelNoteBean.class);
                if (detailResult.code == 0) {
                    //      travelAdapter.getDataList().clear();
                    //      travelAdapter.getDataList().addAll(detailResult.result);
                    if (detailResult.result.size() > 0) {
                        //全部游记
//                        findViewById(R.id.tv_all_note).setVisibility(View.VISIBLE);
//                        findViewById(R.id.tv_all_note).setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                Intent intent = new Intent(mContext, MoreTravelNoteActivity.class);
//                                intent.putExtra("keyword", keyword);
//                                intent.putExtra("id", locId);
//                                startActivity(intent);
//                            }
//                        });
                    }
                    // setListViewHeightBasedOnChildren(travelLv);
                } else {
//                    ToastUtil.getInstance(CityDetailActivity.this).showToast(getResources().getString(R.string.request_server_failed));
                }
            }

            @Override
            public void doFailure(Exception error, String msg, String method) {
                DialogManager.getInstance().dissMissModelessLoadingDialog();
//                ToastUtil.getInstance(CityDetailActivity.this).showToast(getResources().getString(R.string.request_network_failed));
            }

            @Override
            public void doFailure(Exception error, String msg, String method, int code) {

            }
        });
    }

    private void getTravelNotes(final String locId) {
        OtherApi.getTravelNoteByLocId(locId, 0, 3, new HttpCallBack<String>() {
            @Override
            public void doSuccess(String result, String method) {
                DialogManager.getInstance().dissMissModelessLoadingDialog();
                CommonJson4List<TravelNoteBean> detailResult = CommonJson4List.fromJson(result, TravelNoteBean.class);
                if (detailResult.code == 0) {
                    //         travelAdapter.getDataList().clear();
                    //        travelAdapter.getDataList().addAll(detailResult.result);
                    if (detailResult.result.size() > 0) {
                        //全部游记
//                        findViewById(R.id.tv_all_note).setVisibility(View.VISIBLE);
//                        findViewById(R.id.tv_all_note).setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                Intent intent = new Intent(mContext, MoreTravelNoteActivity.class);
//                                intent.putExtra("id", locId);
//                                startActivity(intent);
//                            }
//                        });
                    }
                    //       setListViewHeightBasedOnChildren(travelLv);
                } else {
//                    ToastUtil.getInstance(CityDetailActivity.this).showToast(getResources().getString(R.string.request_server_failed));
                }
            }

            @Override
            public void doFailure(Exception error, String msg, String method) {
                DialogManager.getInstance().dissMissModelessLoadingDialog();
//                ToastUtil.getInstance(CityDetailActivity.this).showToast(getResources().getString(R.string.request_network_failed));
            }

            @Override
            public void doFailure(Exception error, String msg, String method, int code) {

            }
        });
    }

    private void bindView(final LocBean detailBean) {

        if (AccountManager.getInstance().getLoginAccount(mContext) != null) {
            final String[] ids = new String[1];
            ids[0] = detailBean.id;
            isVote = detailBean.isVote;
            isTraveled = detailBean.traveled;
            tv_like.setChecked(detailBean.isVote);
            tv_like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (detailBean.id != null && AccountManager.getCurrentUserId() != null) {
                        if (!isVote) {

                            UserApi.vote(detailBean.id, new HttpCallBack() {
                                @Override
                                public void doSuccess(Object result, String method) {
                                    tv_like.setChecked(!isVote);
                                    isVote = !isVote;
                                }

                                @Override
                                public void doFailure(Exception error, String msg, String method) {

                                }

                                @Override
                                public void doFailure(Exception error, String msg, String method, int code) {

                                }
                            });
                        } else {
                            UserApi.unVote(detailBean.id, new HttpCallBack() {
                                @Override
                                public void doSuccess(Object result, String method) {
                                    tv_like.setChecked(!isVote);
                                    isVote = !isVote;
                                }

                                @Override
                                public void doFailure(Exception error, String msg, String method) {

                                }

                                @Override
                                public void doFailure(Exception error, String msg, String method, int code) {

                                }
                            });

                        }
                    }

                }
            });

            tv_traveled.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String footprintAction = "";
                    if (isTraveled) {
                        footprintAction = "del";
                    } else {
                        footprintAction = "add";
                    }
                    final String tac = footprintAction;
                    UserApi.updateUserFootPrint(AccountManager.getCurrentUserId(), tac, ids, new HttpCallBack() {
                        @Override
                        public void doSuccess(Object result, String method) {
                            tv_traveled.setChecked(!isTraveled);
                            isTraveled = !isTraveled;
                        }

                        @Override
                        public void doFailure(Exception error, String msg, String method) {

                        }

                        @Override
                        public void doFailure(Exception error, String msg, String method, int code) {

                        }
                    });
                }
            });
        }
        iv_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MobclickAgent.onEvent(CityDetailActivity.this, "navigation_item_lxp_city_create_plan");
                if (AccountManager.getInstance().getLoginAccount(mContext) == null) {
                    Intent logIntent = new Intent(mContext, LoginActivity.class);
                    startActivityWithNoAnim(logIntent);
                    overridePendingTransition(R.anim.push_bottom_in, R.anim.slide_stay);
                } else {
                    Intent intent = new Intent();
                    intent.setClass(CityDetailActivity.this, SelectDestActivity.class);
                    ArrayList<LocBean> locList = new ArrayList<LocBean>();
                    locList.add(locDetailBean);
                    intent.putExtra("locList", locList);
                    startActivity(intent);
                }


            }
        });
        recommend_plan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, StrategyActivity.class);
                intent.putExtra("locId", detailBean.id);
                intent.putExtra("recommend", true);
                startActivity(intent);
            }
        });
        recommend_note.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, MoreTravelNoteActivity.class);
                intent.putExtra("keyword", detailBean.zhName);
                intent.putExtra("id", locId);
                startActivity(intent);
            }
        });

        rl_all_expert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, GuilderListActivity.class);
                intent.putExtra("zone", detailBean.zhName);
                startActivity(intent);
            }
        });
        TextView titleTv = (TextView) findViewById(R.id.tv_title_bar_title);
        titleTv.setText(detailBean.zhName);

        locDetailBean = detailBean;
        if (isFromStrategy) {
            //  findViewById(R.id.tv_title_bar_right).setVisibility(View.GONE);
        }
//        findViewById(R.id.tv_title_bar_right).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                showActionDialog();
//            }
//        });
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .showImageForEmptyUri(R.drawable.empty_photo)
                .showImageOnFail(R.drawable.empty_photo)
                .resetViewBeforeLoading(true)
                .cacheOnDisk(true)
                .displayer(new RoundedBitmapDisplayer(10)) // 设置成圆角图片
                .build();
        if (detailBean.images != null && detailBean.images.size() > 0) {
            for (int i = 0; i < detailBean.images.size(); i++) {
                ImageLoader.getInstance().displayImage(detailBean.images.get(i).url, imageViews[i], options);
                if (i == 5) break;
            }
        }
        findViewById(R.id.ly1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MobclickAgent.onEvent(CityDetailActivity.this, "card_item_city_pictures");
                Intent intent = new Intent(mContext, CityPictureActivity.class);
                intent.putExtra("id", locDetailBean.id);
                intent.putExtra("title", locDetailBean.zhName);
                startActivityWithNoAnim(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        if (detailBean.imageCnt > 100) {
            detailBean.imageCnt = 100;
        }

        mCityNameTv.setText(detailBean.zhName);
        mCostTimeTv.setText(String.format("～推荐旅行 · %s～", detailBean.timeCostDesc));
        foodTv.setOnClickListener(this);
        shoppingTv.setOnClickListener(this);
        spotsTv.setOnClickListener(this);
        travelTv.setOnClickListener(this);

        final String bt = String.format("～最佳季节：%s", detailBean.travelMonth);
        bestMonthTv.setText(bt);

        ViewTreeObserver observer1 = bestMonthTv.getViewTreeObserver();
        observer1.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                TextView timeView = bestMonthTv;
                int nc = timeView.getLayout().getLineEnd(0);
                if (nc < bt.length() && nc < timeView.getText().length()) {
                    String text = bt.substring(0, nc - 8);
                    SpannableString planStr = new SpannableString("全文");
                    planStr.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.app_theme_color)), 0, planStr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    SpannableStringBuilder spb = new SpannableStringBuilder();
                    spb.append(String.format("%s... ", text)).append(planStr);
                    timeView.setText(spb);
                    bestMonthTv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent();
                            intent.putExtra("content", detailBean.travelMonth);
                            intent.putExtra("title", "最佳季节");
                            intent.setClass(CityDetailActivity.this, ReadMoreActivity.class);
                            startActivityWithNoAnim(intent);
                        }
                    });
                }
            }
        });

        final String desc = detailBean.desc;
        mCityDesc.setText(desc);
        ViewTreeObserver observer = mCityDesc.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                TextView descView = mCityDesc;
                if (descView.getLineCount() > 1) {
                    int numChars = descView.getLayout().getLineEnd(1);
                    if (descView.getText().length() > numChars) {
                        String text;
                        if (IMUtils.isEnglish(desc)) {
                            text = desc.substring(0, desc.substring(0, numChars - 4).lastIndexOf(" "));
                        } else {
                            text = desc.substring(0, numChars - 4);
                        }
                        SpannableString planStr = new SpannableString("全文");
                        planStr.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.app_theme_color)), 0, planStr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        SpannableStringBuilder spb = new SpannableStringBuilder();
                        spb.append(String.format("%s... ", text)).append(planStr);
                        descView.setText(spb);
                    }
                }
            }
        });
        mCityDesc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("content", desc);
                intent.putExtra("title", "城市简介");
                intent.setClass(CityDetailActivity.this, ReadMoreActivity.class);
                startActivityWithNoAnim(intent);
            }
        });

    }

    public void intentToTravel(View view) {
        if (locDetailBean != null) {
            Intent intent = new Intent(mContext, PeachWebViewActivity.class);
            intent.putExtra("url", locDetailBean.playGuide);
            intent.putExtra("enable_bottom_bar", true);
            intent.putExtra("title", "旅行指南");//String.format("玩转%s", mCityNameTv.getText()));
            startActivity(intent);
        }
    }

    public void intentToSpots(View view) {
        if (locDetailBean != null) {
            Intent intent = new Intent(mContext, SpotListActivity.class);
            ArrayList<LocBean> locList = new ArrayList<LocBean>();
            locList.add(locDetailBean);
            intent.putParcelableArrayListExtra("locList", locList);
            intent.putExtra("type", TravelApi.PeachType.SPOT);
            startActivity(intent);
        }
    }

    public void intentToFood(View view) {
        if (locDetailBean != null) {
            Intent intent = new Intent(mContext, PoiListActivity.class);
            ArrayList<LocBean> locList = new ArrayList<LocBean>();
            locList.add(locDetailBean);
            intent.putParcelableArrayListExtra("locList", locList);
            intent.putExtra("type", TravelApi.PeachType.RESTAURANTS);
            intent.putExtra("value", locDetailBean.diningTitles);
            intent.putExtra("isFromCityDetail", true);
            startActivity(intent);
        }
    }

    public void intentToShopping(View view) {
        if (locDetailBean != null) {
            Intent intent = new Intent(mContext, PoiListActivity.class);
            ArrayList<LocBean> locList = new ArrayList<LocBean>();
            locList.add(locDetailBean);
            intent.putParcelableArrayListExtra("locList", locList);
            intent.putExtra("type", TravelApi.PeachType.SHOPPING);
            intent.putExtra("value", locDetailBean.shoppingTitles);
            intent.putExtra("isFromCityDetail", true);
            startActivity(intent);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_travel:
                MobclickAgent.onEvent(CityDetailActivity.this, "button_item_city_travel_tips");
                intentToTravel(v);
                break;
            case R.id.tv_spots:
                MobclickAgent.onEvent(CityDetailActivity.this, "button_item_city_spots");
                intentToSpots(v);
                break;

            case R.id.tv_restaurant:
                MobclickAgent.onEvent(CityDetailActivity.this, "button_item_city_delicious");
                intentToFood(v);
                break;

            case R.id.tv_shopping:
                MobclickAgent.onEvent(CityDetailActivity.this, "button_item_city_shoppings");
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

    public void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0, len = listAdapter.getCount(); i < len; i++) { //listAdapter.getCount()返回数据项的数目
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0); //计算子项View 的宽高
            totalHeight += listItem.getMeasuredHeight(); //统计所有子项的总高度
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }
}
