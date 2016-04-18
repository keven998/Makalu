package com.xuejian.client.lxp.module.customization;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.aizou.core.dialog.ToastUtil;
import com.aizou.core.http.HttpCallBack;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.PeachBaseActivity;
import com.xuejian.client.lxp.bean.BountiesBean;
import com.xuejian.client.lxp.common.api.TravelApi;
import com.xuejian.client.lxp.common.dialog.PeachMessageDialog;
import com.xuejian.client.lxp.common.gson.CommonJson;
import com.xuejian.client.lxp.common.thirdpart.weixin.WeixinApi;
import com.xuejian.client.lxp.module.MainActivity;
import com.xuejian.client.lxp.module.pay.PaymentActivity;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by yibiao.qin on 2016/4/5.
 */
public class ProjectConfirmActivity extends PeachBaseActivity {

    @Bind(R.id.ctv_free)
    CheckedTextView ctvFree;
    @Bind(R.id.ctv_charge)
    CheckedTextView ctvCharge;
    @Bind(R.id.et_price)
    EditText etPrice;
    @Bind(R.id.tv_sign)
    TextView tvSign;
    @Bind(R.id.ll_price_container)
    LinearLayout llPriceContainer;
    @Bind(R.id.ll_bounty)
    LinearLayout llBounty;
    @Bind(R.id.tv_submit_order)
    TextView tvSubmitOrder;
    @Bind(R.id.tv_title_back)
    TextView tvTitleBack;
    @Bind(R.id.strategy_title)
    TextView strategyTitle;

    private static final String info = "1.悬赏定金能得到那些帮助？\n用户支付的定金越高，商家抢单的几率越大；\n支付定金不得低于50元，支付定金的用户保证在5个工作日内，至少有3个商家提供优秀方案；\n2.悬赏定金是否可退？\n定金支付后，若商家提供的方案用户都不满意，可申请退款；\n" +
            "平台会收取定金的5%作为违约费用；\n3.未设置悬赏定金和设置悬赏定金的区别？\n免费体验发布的需求，商家接单几率相对于支付定金的用户要少；\n在用户慎重考虑后，建议发布支付定金的需求，以便于能得到更优质的服务。";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_confirm);
        ButterKnife.bind(this);
        final BountiesBean bean = getIntent().getParcelableExtra("BountiesBean");
        tvTitleBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ctvCharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ctvFree.isChecked()){
                    ctvFree.setChecked(false);
                }
                ctvCharge.setChecked(true);
                etPrice.setEnabled(true);
                llPriceContainer.setVisibility(View.VISIBLE);
            }
        });
        ctvFree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ctvCharge.isChecked()){
                    ctvCharge.setChecked(false);
                }
                ctvFree.setChecked(true);
                etPrice.setEnabled(false);
                llPriceContainer.setVisibility(View.GONE);
            }
        });
        tvSubmitOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (bean!=null) {
                    if (ctvCharge.isChecked()){
                        double price = 0.00;
                        try {
                            price =  Double.parseDouble(etPrice.getText().toString().trim());
                        }catch (Exception e){
                            e.printStackTrace();
                            ToastUtil.getInstance(mContext).showToast("请输入正确的悬赏金额");
                            return;
                        }

                       bean.setBountyPrice(price);

                    }
                    createProject(bean);

                }
            }
        });
        llBounty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNotice();
            }
        });


    }

    private long id;
    private void createProject(BountiesBean bean) {
        Log.d("ProjectConfirmActivity", bean.toString());
        TravelApi.createProject(bean, new HttpCallBack<String>() {
            @Override
            public void doSuccess(String result, String method) {
                CommonJson<BountiesBean> beanCommonJson = CommonJson.fromJson(result,BountiesBean.class);
                id = beanCommonJson.result.getItemId();
                if (ctvFree.isChecked()){
                    showSuccess();
                }else {
                    showPayActionDialog(beanCommonJson.result.getItemId());
                }

            }

            @Override
            public void doFailure(Exception error, String msg, String method) {

            }

            @Override
            public void doFailure(Exception error, String msg, String method, int code) {
                ToastUtil.getInstance(ProjectConfirmActivity.this).showToast("提交失败");
            }
        });
    }

    private void showNotice() {
        View view = getLayoutInflater().inflate(R.layout.bounty_detail, null);
        TextView tvInfo = (TextView) view.findViewById(R.id.tv_info);
        FrameLayout frameLayout = (FrameLayout) view.findViewById(R.id.fl_container);
        tvInfo.setText(info);
        final PopupWindow popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        //   popupWindow.setAnimationStyle(R.style.PopAnimation1);
        frameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
        popupWindow.showAtLocation(ctvCharge, Gravity.CENTER, 0, 0);
    }


    private void showPayActionDialog(final long id) {
        final Activity act = this;
        final AlertDialog dialog = new AlertDialog.Builder(act).create();
        View contentView = View.inflate(act, R.layout.dialog_select_payment, null);
        CheckedTextView alipay = (CheckedTextView) contentView.findViewById(R.id.ctv_alipay);
        CheckedTextView weixinpay = (CheckedTextView) contentView.findViewById(R.id.ctv_weixin);

        alipay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent alipay = new Intent(ProjectConfirmActivity.this, PaymentActivity.class);
                alipay.putExtra("bounty", true);
                alipay.putExtra("bountyId", id);
                Log.d("ProjectConfirmActivity", "id:" + id);
                alipay.putExtra("type", "alipay");
                startActivity(alipay);
            }
        });
        weixinpay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (!WeixinApi.getInstance().isWXinstalled(ProjectConfirmActivity.this)) {
                    ToastUtil.getInstance(mContext).showToast("你还没有安装微信");
                    return;
                }
                Intent tv_pay = new Intent(ProjectConfirmActivity.this, PaymentActivity.class);
                tv_pay.putExtra("bounty", true);
                tv_pay.putExtra("bountyId", id);
                tv_pay.putExtra("type", "weixinpay");
                startActivity(tv_pay);
            }
        });
        contentView.findViewById(R.id.iv_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notice();
                //   dialog.dismiss();
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
    private void notice() {
        final PeachMessageDialog dialog = new PeachMessageDialog(mContext);
        dialog.setTitle("提示");
        dialog.setMessage("取消支付定金，发布的需求将失效，确认取消支付？");
        dialog.setPositiveButton("确认", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent intent = new Intent(ProjectConfirmActivity.this, ProjectDetailActivity.class);
                intent.putExtra("id",id);
                startActivity(intent);
                finish();
            }
        });
        dialog.setNegativeButton("取消", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();

    }
    private void showSuccess(){
        final PeachMessageDialog dialog =new PeachMessageDialog(this);
        dialog.setTitle("提示");
        dialog.setMessage("您的需求已提交，请耐心等待回复～");
        dialog.setCanceledOnTouchOutside(false);
        dialog.setPositiveButton("返回定制首页", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent intent = new Intent(ProjectConfirmActivity.this, MainActivity.class);
                intent.putExtra("custom",true);
                startActivity(intent);
            }
        });
        dialog.setNegativeButton("查看发布需求", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent intent = new Intent(ProjectConfirmActivity.this, ProjectDetailActivity.class);
                intent.putExtra("id",id);
                startActivity(intent);
            }
        });
        dialog.show();

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.getBooleanExtra("success",false)){
            showPaySuccess();
        }
    }

    private void showPaySuccess(){
        final PeachMessageDialog dialog =new PeachMessageDialog(this);
        dialog.setTitle("提示");
        dialog.setMessage("您的需求已成功支付定金，商家会火速为您服务，请静心等候");
        dialog.setCanceledOnTouchOutside(false);
        dialog.setPositiveButton("返回定制首页", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent intent = new Intent(ProjectConfirmActivity.this, MainActivity.class);
                intent.putExtra("custom",true);
                startActivity(intent);
            }
        });
        dialog.setNegativeButton("查看发布需求", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent intent = new Intent(ProjectConfirmActivity.this, ProjectDetailActivity.class);
                intent.putExtra("id",id);
                startActivity(intent);

            }
        });
        dialog.show();

    }
}
