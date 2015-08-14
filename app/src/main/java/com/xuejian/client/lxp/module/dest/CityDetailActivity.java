package com.xuejian.client.lxp.module.dest;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.aizou.core.dialog.ToastUtil;
import com.aizou.core.http.HttpCallBack;
import com.aizou.core.utils.LocalDisplay;
import com.aizou.core.widget.listHelper.ListViewDataAdapter;
import com.aizou.core.widget.listHelper.ViewHolderBase;
import com.aizou.core.widget.listHelper.ViewHolderCreator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.umeng.analytics.MobclickAgent;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.PeachBaseActivity;
import com.xuejian.client.lxp.bean.CountryBean;
import com.xuejian.client.lxp.bean.LocBean;
import com.xuejian.client.lxp.bean.TravelNoteBean;
import com.xuejian.client.lxp.common.api.OtherApi;
import com.xuejian.client.lxp.common.api.TravelApi;
import com.xuejian.client.lxp.common.dialog.DialogManager;
import com.xuejian.client.lxp.common.gson.CommonJson;
import com.xuejian.client.lxp.common.gson.CommonJson4List;
import com.xuejian.client.lxp.common.utils.IMUtils;
import com.xuejian.client.lxp.common.utils.PreferenceUtils;
import com.xuejian.client.lxp.module.PeachWebViewActivity;
import com.xuejian.client.lxp.module.dest.adapter.TravelNoteViewHolder;

import java.util.ArrayList;

/**
 * Created by Rjm on 2014/11/13.
 */
public class CityDetailActivity extends PeachBaseActivity implements View.OnClickListener {
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
    private ListViewDataAdapter travelAdapter;
    private LocBean locDetailBean;
    private String locId;
    private boolean isFromStrategy;
    ImageView[] imageViews;
    ListView travelLv;
    PopupWindow mPop;

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
        int ivWidth = (width-LocalDisplay.dp2px(84))/3;

        travelLv = (ListView) findViewById(R.id.lv_city_detail);
        // mTravelLv = travelLv;
        mCityIv1 = (ImageView) findViewById(R.id.iv_city_1);
        mCityIv2 = (ImageView) findViewById(R.id.iv_city_2);
        mCityIv3 = (ImageView) findViewById(R.id.iv_city_3);
        mCityIv4 = (ImageView) findViewById(R.id.iv_city_4);
        mCityIv5 = (ImageView) findViewById(R.id.iv_city_5);
        mCityIv6 = (ImageView) findViewById(R.id.iv_city_6);

        mCityIv1.getLayoutParams().width=ivWidth;
        mCityIv2.getLayoutParams().width=ivWidth;
        mCityIv3.getLayoutParams().width=ivWidth;
        mCityIv4.getLayoutParams().width=ivWidth;
        mCityIv5.getLayoutParams().width=ivWidth;
        mCityIv6.getLayoutParams().width=ivWidth;

        mCityIv1.getLayoutParams().height=ivWidth;
        mCityIv2.getLayoutParams().height=ivWidth;
        mCityIv3.getLayoutParams().height=ivWidth;
        mCityIv4.getLayoutParams().height=ivWidth;
        mCityIv5.getLayoutParams().height=ivWidth;
        mCityIv6.getLayoutParams().height=ivWidth;


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
                MobclickAgent.onEvent(CityDetailActivity.this,"navigation_item_lxp_city_share");
                IMUtils.onClickImShare(CityDetailActivity.this);
            }
        });
        travelAdapter = new ListViewDataAdapter(new ViewHolderCreator() {
            @Override
            public ViewHolderBase createViewHolder() {
                TravelNoteViewHolder viewHolder = new TravelNoteViewHolder(CityDetailActivity.this, false, true);
                return viewHolder;
            }
        });
        travelLv.setAdapter(travelAdapter);
        travelLv.setOnTouchListener(new View.OnTouchListener() {
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

        findViewById(R.id.tv_all_note).setVisibility(View.GONE);

        findViewById(R.id.iv_nav_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        findViewById(R.id.tv_hasGone).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        findViewById(R.id.tv_like).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void getCityDetailData(final String id) {
        DialogManager.getInstance().showModelessLoadingDialog(this);
        TravelApi.getCityDetail(id, (int) (LocalDisplay.SCREEN_WIDTH_PIXELS / 1.5), new HttpCallBack<String>() {
            @Override
            public void doSuccess(String result, String method) {
                CommonJson<LocBean> detailResult = CommonJson.fromJson(result, LocBean.class);
                if (detailResult.code == 0) {
                    bindView(detailResult.result);
                  //  getTravelNotes(id);
                    getTravelNotesbyKeyword(detailResult.result.zhName);
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
    private void getTravelNotesbyKeyword(final String keyword) {
        OtherApi.getTravelNoteByKeyword(keyword, 0, 3, new HttpCallBack<String>() {
            @Override
            public void doSuccess(String result, String method) {
                DialogManager.getInstance().dissMissModelessLoadingDialog();
                CommonJson4List<TravelNoteBean> detailResult = CommonJson4List.fromJson(result, TravelNoteBean.class);
                if (detailResult.code == 0) {
                    travelAdapter.getDataList().clear();
                    travelAdapter.getDataList().addAll(detailResult.result);
                    if (detailResult.result.size() > 0) {
                        //全部游记
                        findViewById(R.id.tv_all_note).setVisibility(View.VISIBLE);
                        findViewById(R.id.tv_all_note).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(mContext, MoreTravelNoteActivity.class);
                                intent.putExtra("keyword",keyword);
                                intent.putExtra("id", locId);
                                startActivity(intent);
                            }
                        });
                    }
                    setListViewHeightBasedOnChildren(travelLv);
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
                    travelAdapter.getDataList().clear();
                    travelAdapter.getDataList().addAll(detailResult.result);
                    if(detailResult.result.size()>0){
                            //全部游记
                        findViewById(R.id.tv_all_note).setVisibility(View.VISIBLE);
                            findViewById(R.id.tv_all_note).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(mContext, MoreTravelNoteActivity.class);
                                    intent.putExtra("id", locId);
                                    startActivity(intent);
                                }
                            });
                    }
                    setListViewHeightBasedOnChildren(travelLv);
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
        iv_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MobclickAgent.onEvent(CityDetailActivity.this,"navigation_item_lxp_city_create_plan");
                Intent intent = new Intent( );
                intent.setClass(CityDetailActivity.this,SelectDestActivity.class);
                ArrayList<LocBean> locList = new ArrayList<LocBean>();
                locList.add(locDetailBean);
                intent.putExtra("locList", locList);
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
                MobclickAgent.onEvent(CityDetailActivity.this,"card_item_city_pictures");
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

        mCityNameTv.setText(getCityName(detailBean.zhName));
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
                            intent.putExtra("title","最佳季节");
                            intent.setClass(CityDetailActivity.this, ReadMoreActivity.class);
                            startActivityWithNoAnim(intent);
                        }
                    });
                }
            }
        });

        final String desc = detailBean.desc;
        mCityDesc.setText(desc);
//        mCityDesc.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
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
//                mPop.showAtLocation(findViewById(R.id.rl_spot_detail_list), Gravity.BOTTOM, 0, 0);
//                pop_dismiss.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        mPop.dismiss();
//                    }
//                });
//
//            }
//        });
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
                intent.putExtra("title","城市简介");
                intent.setClass(CityDetailActivity.this, ReadMoreActivity.class);
                startActivityWithNoAnim(intent);
            }
        });
    }

    private String getCityName(String name) {
        String outcountry = PreferenceUtils.getCacheData(CityDetailActivity.this, "destination_outcountry");
        CommonJson4List<CountryBean> countryListResult = CommonJson4List.fromJson(outcountry, CountryBean.class);
        if (countryListResult == null || countryListResult.result == null) return name;
        for (CountryBean countryBean : countryListResult.result) {
            for (LocBean kLocBean : countryBean.destinations) {
                if (kLocBean.zhName.equals(name)) {
                    return countryBean.zhName + " · " + name;
                }
            }
        }
        String inCountry = PreferenceUtils.getCacheData(CityDetailActivity.this, "destination_indest_group");
        CommonJson4List<CountryBean> groupListResult = CommonJson4List.fromJson(inCountry, CountryBean.class);
        for (CountryBean incountryBean : groupListResult.result) {
            for (LocBean kLocBean : incountryBean.destinations) {
                if (kLocBean.zhName.equals(name)) {
                    return "中国" + " · " + name;
                }
            }
        }
        return name;
    }

    public void intentToTravel(View view) {
        if (locDetailBean != null) {
            Intent intent = new Intent(mContext, PeachWebViewActivity.class);
            intent.putExtra("url", locDetailBean.playGuide);
            intent.putExtra("enable_bottom_bar", true);
            intent.putExtra("title", "旅行指南");//String.format("玩转%s", mCityNameTv.getText()));
            startActivity(intent);
        } else {
            Log.e("CLICK", "没有数据");
        }
    }

    public void intentToSpots(View view) {
        Intent intent = new Intent(mContext, SpotListActivity.class);
        ArrayList<LocBean> locList = new ArrayList<LocBean>();
        locList.add(locDetailBean);
        intent.putParcelableArrayListExtra("locList", locList);
        intent.putExtra("type", TravelApi.PeachType.SPOT);
        startActivity(intent);
    }

    public void intentToFood(View view) {
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
                MobclickAgent.onEvent(CityDetailActivity.this,"button_item_city_travel_tips");
                intentToTravel(v);
                break;
            case R.id.tv_spots:
                MobclickAgent.onEvent(CityDetailActivity.this,"button_item_city_spots");
                intentToSpots(v);
                break;

            case R.id.tv_restaurant:
                MobclickAgent.onEvent(CityDetailActivity.this,"button_item_city_delicious");
                intentToFood(v);
                break;

            case R.id.tv_shopping:
                MobclickAgent.onEvent(CityDetailActivity.this,"button_item_city_shoppings");
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
//获取ListView对应的Adapter
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
// pre-condition
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
//listView.getDividerHeight()获取子项间分隔符占用的高度
//params.height最后得到整个ListView完整显示需要的高度
        listView.setLayoutParams(params);
    }
}
