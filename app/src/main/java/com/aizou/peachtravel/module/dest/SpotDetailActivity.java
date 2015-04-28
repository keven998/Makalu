package com.aizou.peachtravel.module.dest;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aizou.core.utils.LocalDisplay;
import com.aizou.peachtravel.bean.PeachUser;
import com.aizou.peachtravel.common.account.AccountManager;
import com.aizou.peachtravel.common.dialog.DialogManager;
import com.aizou.core.dialog.ToastUtil;
import com.aizou.core.http.HttpCallBack;
import com.aizou.core.widget.DotView;
import com.aizou.core.widget.HackyViewPager;
import com.aizou.core.widget.autoscrollviewpager.AutoScrollViewPager;
import com.aizou.core.widget.expandabletextview.ExpandableTextView;
import com.aizou.core.widget.pagerIndicator.viewpager.RecyclingPagerAdapter;
import com.aizou.peachtravel.R;
import com.aizou.peachtravel.base.PeachBaseActivity;
import com.aizou.peachtravel.bean.ImageBean;
import com.aizou.peachtravel.bean.ModifyResult;
import com.aizou.peachtravel.bean.SpotDetailBean;
import com.aizou.peachtravel.common.api.OtherApi;
import com.aizou.peachtravel.common.api.TravelApi;
import com.aizou.peachtravel.common.gson.CommonJson;
import com.aizou.peachtravel.common.utils.CommonUtils;
import com.aizou.peachtravel.common.utils.IMUtils;
import com.aizou.peachtravel.common.utils.ImageZoomAnimator2;
import com.aizou.peachtravel.common.imageloader.UILUtils;
import com.aizou.peachtravel.common.utils.IntentUtils;
import com.aizou.peachtravel.common.utils.ShareUtils;
import com.aizou.peachtravel.common.widget.TitleHeaderBar;
import com.aizou.peachtravel.module.PeachWebViewActivity;
import com.aizou.peachtravel.module.my.LoginActivity;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;

/**
 * Created by Rjm on 2014/11/17.
 */
public class SpotDetailActivity extends PeachBaseActivity {
    private String mSpotId;
    private ImageView closeIv;
    private ImageView spotIv;
    private LinearLayout descLl,priceLl,timeLl;
    private RelativeLayout addressLl;
    private FrameLayout mBookFl;
    private TextView mSpotNameTv, descTv,mPriceDescTv, mAddressTv, mTimeTv;
    private TextView tipsTv, travelGuideTv, trafficGuideTv;
    private ImageView favIv,shareIv;
    private SpotDetailBean spotDetailBean;
    private RatingBar ratingBar;
    private TitleHeaderBar titleBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
    }

    @Override
    protected void onPause() {
        super.onPause();
//        MobclickAgent.onPageEnd("page_spot_detail");
    }

    @Override
    protected void onResume() {
        super.onResume();
//        MobclickAgent.onPageStart("page_spot_detail");
    }

    @Override
    public void onBackPressed() {
        finishWithNoAnim();
        overridePendingTransition(0, android.R.anim.fade_out);
    }

    private void initView() {
        setContentView(R.layout.activity_spot_detail);
        titleBar=(TitleHeaderBar)findViewById(R.id.spot_title_bar);
        titleBar.findViewById(R.id.ly_title_bar_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(0,R.anim.fade_out);
            }
        });
        WindowManager m = getWindowManager();
        Display d = m.getDefaultDisplay();  //为获取屏幕宽、高
        WindowManager.LayoutParams p = getWindow().getAttributes();  //获取对话框当前的参数值
        p.y = LocalDisplay.dp2px(5);
        p.height = (int) (d.getHeight());  /* - LocalDisplay.dp2px(64)*/
        p.width = (int) (d.getWidth() ); /*- LocalDisplay.dp2px(28)*/
//        p.alpha = 1.0f;      //设置本身透明度
//        p.dimAmount = 0.0f;      //设置黑暗度
//        getWindow().setAttributes(p);
        spotIv = (ImageView) findViewById(R.id.iv_spot);

        favIv = (ImageView) findViewById(R.id.iv_fav);
        shareIv = (ImageView) findViewById(R.id.iv_share);
        ratingBar = (RatingBar) findViewById(R.id.ratingBar_spot);
        descLl = (LinearLayout) findViewById(R.id.ll_desc);
        priceLl = (LinearLayout) findViewById(R.id.ll_price);
        timeLl = (LinearLayout) findViewById(R.id.ll_time);
        mSpotNameTv = (TextView) findViewById(R.id.tv_spot_name);
        descTv = (TextView) findViewById(R.id.tv_desc);
        mPriceDescTv = (TextView) findViewById(R.id.tv_price_desc);
        mTimeTv = (TextView) findViewById(R.id.tv_spot_time);
        addressLl = (RelativeLayout) findViewById(R.id.rl_address);
        mAddressTv = (TextView) findViewById(R.id.tv_addr);
        mBookFl = (FrameLayout) findViewById(R.id.fl_book);
        tipsTv = (TextView) findViewById(R.id.tv_tips);
        travelGuideTv = (TextView) findViewById(R.id.tv_travel_guide);
        trafficGuideTv = (TextView) findViewById(R.id.tv_traffic_guide);
    }

    private void initData() {
        mSpotId = getIntent().getStringExtra("id");
        DialogManager.getInstance().showModelessLoadingDialog(mContext);
        getSpotDetailData();
    }

    private void getSpotDetailData() {
        TravelApi.getSpotDetail(mSpotId, new HttpCallBack<String>() {
            @Override
            public void doSucess(String result, String method) {
                DialogManager.getInstance().dissMissModelessLoadingDialog();
                CommonJson<SpotDetailBean> detailResult = CommonJson.fromJson(result, SpotDetailBean.class);
                if (detailResult.code == 0) {
                    spotDetailBean = detailResult.result;
                    bindView(detailResult.result);
                }

            }

            @Override
            public void doFailure(Exception error, String msg, String method) {
                if (!isFinishing()) {
                    DialogManager.getInstance().dissMissLoadingDialog();
                    ToastUtil.getInstance(SpotDetailActivity.this).showToast(getResources().getString(R.string.request_network_failed));
                }

            }
        });
    }

    private void refreshFav(SpotDetailBean detailBean) {
        if (detailBean.isFavorite) {
            favIv.setImageResource(R.drawable.ic_poi_fav_selected);
        } else {
            favIv.setImageResource(R.drawable.ic_poi_fav_normal);
        }
    }

    private void bindView(final SpotDetailBean result) {
        ImageLoader.getInstance().displayImage(result.images.size() > 0 ? result.images.get(0).url : "", spotIv, UILUtils.getDefaultOption());
        spotIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentUtils.intentToPicGallery(SpotDetailActivity.this, result.images, 0);
            }
        });
        ratingBar.setRating(result.getRating());
        mSpotNameTv.setText(result.zhName);
        if(TextUtils.isEmpty(result.desc)){
            descLl.setVisibility(View.GONE);
        }else{
            descLl.setVisibility(View.VISIBLE);
            descTv.setText(result.desc);
        }
        mPriceDescTv.setText("门票 "+result.priceDesc);
        mTimeTv.setText(String.format("建议游玩 %s\n开放时间 %s", result.timeCostDesc, result.openTime));
        if(TextUtils.isEmpty(result.address)){
            mAddressTv.setText(result.zhName);
        }else{
            mAddressTv.setText(result.address);
        }
        addressLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (result.location != null && result.location.coordinates != null) {
                    Uri mUri = Uri.parse("geo:" + result.location.coordinates[1] + "," + result.location.coordinates[0] + "?q=" + result.zhName);
                    Intent mIntent = new Intent(Intent.ACTION_VIEW, mUri);
                    if (CommonUtils.checkIntent(mContext, mIntent)) {
                        startActivity(mIntent);
                    } else {
                        ToastUtil.getInstance(mContext).showToast("没找到地图");
                    }

                }

            }
        });
        if(!TextUtils.isEmpty(result.lyPoiUrl)){
            mBookFl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MobclickAgent.onEvent(mContext,"event_book_ticket");
                    Intent intent = new Intent(mContext,PeachWebViewActivity.class);
                    intent.putExtra("url",result.lyPoiUrl);
                    intent.putExtra("title",result.zhName);
                    startActivity(intent);
                }
            });

        } else {
            mBookFl.setVisibility(View.GONE);

        }
        shareIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showActionDialog();
            }
        });
        refreshFav(spotDetailBean);
        favIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PeachUser user = AccountManager.getInstance().getLoginAccount(SpotDetailActivity.this);
                if (user == null || TextUtils.isEmpty(user.easemobUser)) {
                    ToastUtil.getInstance(SpotDetailActivity.this).showToast("请先登录");
                    Intent intent = new Intent(SpotDetailActivity.this, LoginActivity.class);
                    startActivityForResult(intent, 11);
                    return;
                } else {
                    favorite(result.isFavorite);
                }
            }
        });
        descLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MobclickAgent.onEvent(mContext,"event_spot_information");
                Intent intent = new Intent(mContext, PeachWebViewActivity.class);
                intent.putExtra("title", result.zhName);
                intent.putExtra("url", result.descUrl);
                startActivity(intent);
            }
        });
        timeLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MobclickAgent.onEvent(mContext,"event_spot_information");
                Intent intent = new Intent(mContext, PeachWebViewActivity.class);
                intent.putExtra("title", result.zhName);
                intent.putExtra("url", result.descUrl);
                startActivity(intent);
            }
        });
        priceLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MobclickAgent.onEvent(mContext,"event_spot_information");
                Intent intent = new Intent(mContext, PeachWebViewActivity.class);
                intent.putExtra("title", result.zhName);
                intent.putExtra("url", result.descUrl);
                startActivity(intent);
            }
        });
        if (!TextUtils.isEmpty(result.tipsUrl)) {
            tipsTv.setEnabled(true);
            tipsTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MobclickAgent.onEvent(mContext,"event_spot_travel_tips");
                    Intent intent = new Intent(mContext, PeachWebViewActivity.class);
                    intent.putExtra("title", "游玩小贴士");
                    intent.putExtra("url", result.tipsUrl);
                    startActivity(intent);

                }
            });
        } else {
            tipsTv.setEnabled(false);
        }
        if (!TextUtils.isEmpty(result.trafficInfoUrl)) {
            trafficGuideTv.setEnabled(true);
            trafficGuideTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MobclickAgent.onEvent(mContext,"event_spot_traffic_summary");
                    Intent intent = new Intent(mContext, PeachWebViewActivity.class);
                    intent.putExtra("title", "景点交通");
                    intent.putExtra("url", result.trafficInfoUrl);
                    startActivity(intent);
                }
            });
        } else {
            trafficGuideTv.setEnabled(false);
        }
        if (!TextUtils.isEmpty(result.visitGuideUrl)) {
            travelGuideTv.setEnabled(true);
            travelGuideTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MobclickAgent.onEvent(mContext,"event_spot_travel_experience");
                    Intent intent = new Intent(mContext, PeachWebViewActivity.class);
                    intent.putExtra("title", "景点体验");
                    intent.putExtra("url", result.visitGuideUrl);
                    startActivity(intent);
                }
            });
        } else {
            travelGuideTv.setEnabled(false);
        }
    }

    private void favorite(boolean isFavorite) {
        if (isFavorite) {
            OtherApi.deleteFav(spotDetailBean.id, new HttpCallBack<String>() {
                @Override
                public void doSucess(String result, String method) {
                    CommonJson<ModifyResult> deleteResult = CommonJson.fromJson(result, ModifyResult.class);
                    if (deleteResult.code == 0) {
                        spotDetailBean.isFavorite = false;
                        refreshFav(spotDetailBean);
                    }
                }

                @Override
                public void doFailure(Exception error, String msg, String method) {
                    if (!isFinishing())
                        ToastUtil.getInstance(SpotDetailActivity.this).showToast(getResources().getString(R.string.request_network_failed));
                }
            });
        } else {
            MobclickAgent.onEvent(mContext,"event_spot_favorite");
            OtherApi.addFav(spotDetailBean.id, "vs", new HttpCallBack<String>() {
                @Override
                public void doSucess(String result, String method) {
                    CommonJson<ModifyResult> deleteResult = CommonJson.fromJson(result, ModifyResult.class);
                    if (deleteResult.code == 0  || deleteResult.code == getResources().getInteger(R.integer.response_favorite_exist)) {
                        spotDetailBean.isFavorite = true;
                        refreshFav(spotDetailBean);
                        ToastUtil.getInstance(SpotDetailActivity.this).showToast("已收藏");
                    }
                }

                @Override
                public void doFailure(Exception error, String msg, String method) {
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 11) {
                favorite(spotDetailBean.isFavorite);
            } else {
                IMUtils.onShareResult(mContext, spotDetailBean, requestCode, resultCode, data, null);
            }
        }
    }

    private void showActionDialog() {
        final Activity act = this;
        final AlertDialog dialog = new AlertDialog.Builder(act).create();
        View contentView = View.inflate(act, R.layout.share_to_talk_confirm_action, null);
        Button btn = (Button) contentView.findViewById(R.id.btn_go_plan);
        btn.setText("Talk分享");
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MobclickAgent.onEvent(mContext,"event_spot_share_to_talk");
                IMUtils.onClickImShare(SpotDetailActivity.this);
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
        window.setWindowAnimations(R.style.SelectPicDialog); // 添加动画
    }

}
