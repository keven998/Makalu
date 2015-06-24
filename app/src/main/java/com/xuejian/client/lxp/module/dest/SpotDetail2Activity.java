package com.xuejian.client.lxp.module.dest;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.aizou.core.dialog.ToastUtil;
import com.aizou.core.http.HttpCallBack;
import com.aizou.core.utils.LocalDisplay;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.PeachBaseActivity;
import com.xuejian.client.lxp.bean.ModifyResult;
import com.xuejian.client.lxp.bean.SpotDetailBean;
import com.xuejian.client.lxp.common.account.AccountManager;
import com.xuejian.client.lxp.common.api.OtherApi;
import com.xuejian.client.lxp.common.api.TravelApi;
import com.xuejian.client.lxp.common.dialog.DialogManager;
import com.xuejian.client.lxp.common.gson.CommonJson;
import com.xuejian.client.lxp.common.imageloader.UILUtils;
import com.xuejian.client.lxp.common.utils.CommonUtils;
import com.xuejian.client.lxp.common.utils.IMUtils;
import com.xuejian.client.lxp.common.utils.IntentUtils;
import com.xuejian.client.lxp.db.userDB.User;
import com.xuejian.client.lxp.module.PeachWebViewActivity;
import com.xuejian.client.lxp.module.my.LoginActivity;

/**
 * Created by Rjm on 2014/11/17.
 */
public class SpotDetail2Activity extends PeachBaseActivity {
    private String mSpotId;
    private ImageView closeIv;
    private ImageView spotIv;
    private LinearLayout cardLl;
    private TextView mSpotNameTv, mPriceDescTv, mAddressTv, mCostTimeTv;
    private TextView tipsTv, travelGuideTv, trafficGuideTv;
    private ImageView favIv,shareIv;
    private SpotDetailBean spotDetailBean;
    private RatingBar ratingBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
    }

    @Override
    public void onBackPressed() {
        finishWithNoAnim();
        overridePendingTransition(0, android.R.anim.fade_out);
    }

    private void initView() {
        setContentView(R.layout.activity_spot_detail);
        WindowManager m = getWindowManager();
        Display d = m.getDefaultDisplay();  //为获取屏幕宽、高
        WindowManager.LayoutParams p = getWindow().getAttributes();  //获取对话框当前的参数值
        p.y = LocalDisplay.dp2px(5);
        p.height = (int) (d.getHeight() - LocalDisplay.dp2px(64));
        p.width = (int) (d.getWidth() - LocalDisplay.dp2px(28));
//        p.alpha = 1.0f;      //设置本身透明度
//        p.dimAmount = 0.0f;      //设置黑暗度
//        getWindow().setAttributes(p);
        spotIv = (ImageView) findViewById(R.id.iv_spot);
        closeIv = (ImageView) findViewById(R.id.iv_close);
        closeIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishWithNoAnim();
                SpotDetail2Activity.this.overridePendingTransition(0, android.R.anim.fade_out);
            }
        });
        favIv = (ImageView) findViewById(R.id.iv_fav);
        shareIv = (ImageView) findViewById(R.id.iv_share);
        ratingBar = (RatingBar) findViewById(R.id.ratingBar_spot);
        cardLl = (LinearLayout) findViewById(R.id.ll_card);
        mSpotNameTv = (TextView) findViewById(R.id.tv_spot_name);
        mPriceDescTv = (TextView) findViewById(R.id.tv_price_desc);
        mCostTimeTv = (TextView) findViewById(R.id.tv_cost_time);
        mAddressTv = (TextView) findViewById(R.id.tv_addr);
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
            public void doSuccess(String result, String method) {
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
                    ToastUtil.getInstance(SpotDetail2Activity.this).showToast(getResources().getString(R.string.request_network_failed));
                }

            }
        });
    }

    private void refreshFav(SpotDetailBean detailBean) {
        if (detailBean.isFavorite) {
            favIv.setImageResource(R.drawable.ic_favorite_selected);
        } else {
            favIv.setImageResource(R.drawable.ic_favorite_unselected);
        }
    }

    private void bindView(final SpotDetailBean result) {
        ImageLoader.getInstance().displayImage(result.images.size() > 0 ? result.images.get(0).url : "", spotIv, UILUtils.getDefaultOption());
        spotIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentUtils.intentToPicGallery(SpotDetail2Activity.this, result.images, 0);
            }
        });
        ratingBar.setRating(result.getRating());
        mSpotNameTv.setText(result.zhName);
        mPriceDescTv.setText("门票 "+result.priceDesc);
        mCostTimeTv.setText(String.format("建议游玩 %s\n开放时间 %s", result.timeCostDesc, result.openTime));
        mAddressTv.setText(result.address);
        mAddressTv.setOnClickListener(new View.OnClickListener() {
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
        shareIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showActionDialog();
            }
        });
        findViewById(R.id.fl_book).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        refreshFav(spotDetailBean);
        favIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User user = AccountManager.getInstance().getLoginAccount(SpotDetail2Activity.this);
                if (user == null ) { //|| TextUtils.isEmpty(user.easemobUser)
                    ToastUtil.getInstance(SpotDetail2Activity.this).showToast("请先登录");
                    Intent intent = new Intent(SpotDetail2Activity.this, LoginActivity.class);
                    startActivityForResult(intent, 11);
                    return;
                } else {
                    favorite(result.isFavorite);
                }
            }
        });
        cardLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, SpotIntroActivity.class);
                intent.putExtra("content", spotDetailBean.desc);
                intent.putExtra("spot", spotDetailBean.zhName);
                startActivity(intent);
            }
        });

        if (!TextUtils.isEmpty(result.tipsUrl)) {
            tipsTv.setEnabled(true);
            tipsTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, PeachWebViewActivity.class);
                    intent.putExtra("title", "实用信息");
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
                    Intent intent = new Intent(mContext, PeachWebViewActivity.class);
                    intent.putExtra("title", "交通指南");
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
                    Intent intent = new Intent(mContext, PeachWebViewActivity.class);
                    intent.putExtra("title", "亮点体验");
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
                public void doSuccess(String result, String method) {
                    CommonJson<ModifyResult> deleteResult = CommonJson.fromJson(result, ModifyResult.class);
                    if (deleteResult.code == 0) {
                        spotDetailBean.isFavorite = false;
                        refreshFav(spotDetailBean);
                    }
                }

                @Override
                public void doFailure(Exception error, String msg, String method) {
                    if (!isFinishing())
                        ToastUtil.getInstance(SpotDetail2Activity.this).showToast(getResources().getString(R.string.request_network_failed));
                }
            });
        } else {
            OtherApi.addFav(spotDetailBean.id, "vs", new HttpCallBack<String>() {
                @Override
                public void doSuccess(String result, String method) {
                    CommonJson<ModifyResult> deleteResult = CommonJson.fromJson(result, ModifyResult.class);
                    if (deleteResult.code == 0 || deleteResult.code == getResources().getInteger(R.integer.response_favorite_exist)) {
                        spotDetailBean.isFavorite = true;
                        refreshFav(spotDetailBean);
                        ToastUtil.getInstance(SpotDetail2Activity.this).showToast("已收藏");
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
                IMUtils.onClickImShare(SpotDetail2Activity.this);
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
