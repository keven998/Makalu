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
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
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
import com.xuejian.client.lxp.bean.SpotDetailBean;
import com.xuejian.client.lxp.common.api.TravelApi;
import com.xuejian.client.lxp.common.dialog.DialogManager;
import com.xuejian.client.lxp.common.gson.CommonJson;
import com.xuejian.client.lxp.common.imageloader.UILUtils;
import com.xuejian.client.lxp.common.utils.CommonUtils;
import com.xuejian.client.lxp.common.utils.IMUtils;
import com.xuejian.client.lxp.common.utils.IntentUtils;
import com.xuejian.client.lxp.module.PeachWebViewActivity;
import com.xuejian.client.lxp.module.dest.adapter.SpotDpViewHolder;

/**
 * Created by Rjm on 2014/11/17.
 */
public class SpotDetailActivity extends PeachBaseActivity {
    private String mSpotId;
    private ImageView closeIv;
    private ImageView spotIv;
    private RelativeLayout descLl,priceLl,timeLl;
    private RelativeLayout addressLl;
    private RelativeLayout mBookFl;
    private TextView mSpotNameTv, mTimeTv, mAllEvaluation;
    private TextView tipsTv, travelGuideTv, trafficGuideTv;
    private ImageView chatIv;
    private SpotDetailBean spotDetailBean;
    private RatingBar ratingBar;
    private TextView ic_back,poi_rank_sm;
    private ListViewDataAdapter adapter;
    private BaseAdapter  bAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.spot_detail_list);
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
        ListView spotLv=(ListView) findViewById(R.id.spot_detail_list);
        View hv;
        hv=View.inflate(mContext,R.layout.activity_spot_detail,null);
        spotLv.addHeaderView(hv);
        ic_back=(TextView)findViewById(R.id.poi_det_back);
        ic_back.setOnClickListener(new View.OnClickListener() {
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
        p.height = d.getHeight();  /* - LocalDisplay.dp2px(64)*/
        p.width = d.getWidth(); /*- LocalDisplay.dp2px(28)*/
        spotIv = (ImageView) hv.findViewById(R.id.iv_spot);
        chatIv = (ImageView) findViewById(R.id.iv_chat);
        ratingBar = (RatingBar) hv.findViewById(R.id.ratingBar_spot);
        mSpotNameTv = (TextView) hv.findViewById(R.id.tv_spot_name);
        mTimeTv = (TextView) hv.findViewById(R.id.tv_spot_time);
        poi_rank_sm = (TextView) hv.findViewById(R.id.poi_rank_sm);
        descLl = (RelativeLayout) hv.findViewById(R.id.ll_desc);
        priceLl = (RelativeLayout) hv.findViewById(R.id.ll_price);
        timeLl = (RelativeLayout) hv.findViewById(R.id.ll_time);
        addressLl = (RelativeLayout) hv.findViewById(R.id.rl_address);
        mBookFl = (RelativeLayout) hv.findViewById(R.id.fl_book);
        tipsTv = (TextView) hv.findViewById(R.id.tv_tips);
        travelGuideTv = (TextView) hv.findViewById(R.id.tv_travel_guide);
        trafficGuideTv = (TextView) hv.findViewById(R.id.tv_traffic_guide);
        mAllEvaluation = (TextView) hv.findViewById(R.id.all_evaluation);

        //这个是备着以后读接口
        adapter = new ListViewDataAdapter(new ViewHolderCreator() {
            @Override
            public ViewHolderBase createViewHolder() {
                SpotDpViewHolder viewHolder = new SpotDpViewHolder(SpotDetailActivity.this);
                return viewHolder;
            }
        });
        /*bAdapter=new BaseAdapter() {
            @Override
            public int getCount() {
                return 3;
            }

            @Override
            public Object getItem(int position) {
                return null;
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if(convertView==null){
                    convertView=View.inflate(SpotDetailActivity.this,R.layout.spot_detail_view_cell,null);
                }
                return convertView;
            }
        };*/
        spotLv.setAdapter(adapter);
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
                    getDpView(detailResult.result.id);
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

    private void getDpView(String id){
        //读取接口数据
    }

    private void bindView(final SpotDetailBean result) {
        ImageLoader.getInstance().displayImage(result.images.size() > 0 ? result.images.get(0).url : "", spotIv, UILUtils.getDefaultOption());
        spotIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentUtils.intentToPicGallery(SpotDetailActivity.this, result.images, 0);
            }
        });

        mAllEvaluation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* Intent intent = new Intent(mContext, PeachWebViewActivity.class);
                intent.putExtra("title", "更多评论");
                intent.putExtra("url", result.lyPoiUrl);
                startActivity(intent);*/
            }
        });

        ratingBar.setRating(result.getRating());
        mSpotNameTv.setText(result.zhName);
        if(result.getRank().equals("0")) {
            poi_rank_sm.setText("");
        }else{
            poi_rank_sm.setText(result.zhName + " 景点排名" + result.getRank());
        }
        if(TextUtils.isEmpty(result.desc)){
            descLl.setVisibility(View.GONE);
        }else{
            descLl.setVisibility(View.VISIBLE);
            //descTv.setText(result.desc);
        }
        //mPriceDescTv.setText("门票 "+result.priceDesc);
        mTimeTv.setText("开放时间"+result.timeCostDesc);    /*String.format("建议游玩 %s\n开放时间 %s", result.timeCostDesc, result.openTime)*/
       /* if(TextUtils.isEmpty(result.address)){
            mAddressTv.setText(result.zhName);
        }else{
            mAddressTv.setText(result.address);
        }*/
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
                    intent.putExtra("enable_bottom_bar", true);
                    startActivity(intent);
                }
            });

        } else {
            mBookFl.setVisibility(View.GONE);

        }
        chatIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MobclickAgent.onEvent(mContext,"event_spot_share_to_talk");
                IMUtils.onClickImShare(SpotDetailActivity.this);
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
            //tipsTv.setTextColor(getResources().getColor(R.color.third_font_color));
            tipsTv.setCompoundDrawablesWithIntrinsicBounds(null,SpotDetailActivity.this.getResources().getDrawable(R.drawable.ic_little_disabled),null,null);
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
            //trafficGuideTv.setTextColor(getResources().getColor(R.color.third_font_color));
            trafficGuideTv.setCompoundDrawablesWithIntrinsicBounds(null,SpotDetailActivity.this.getResources().getDrawable(R.drawable.ic_travle_disabled),null,null);
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
            //travelGuideTv.setTextColor(getResources().getColor(R.color.third_font_color));
            travelGuideTv.setCompoundDrawablesWithIntrinsicBounds(null,SpotDetailActivity.this.getResources().getDrawable(R.drawable.ic_spots_disabled),null,null);
            travelGuideTv.setEnabled(false);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            IMUtils.onShareResult(mContext, spotDetailBean, requestCode, resultCode, data, null);
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
        lp.width = display.getWidth(); // 设置宽度
        window.setAttributes(lp);
        window.setGravity(Gravity.BOTTOM); // 此处可以设置dialog显示的位置
        window.setWindowAnimations(R.style.SelectPicDialog); // 添加动画
    }

}
