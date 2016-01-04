package com.xuejian.client.lxp.module.pay;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.aizou.core.http.HttpCallBack;
import com.alipay.sdk.app.PayTask;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.xuejian.client.lxp.BuildConfig;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.PeachBaseActivity;
import com.xuejian.client.lxp.bean.PrePayRespBean;
import com.xuejian.client.lxp.common.alipay.PayResult;
import com.xuejian.client.lxp.common.api.TravelApi;
import com.xuejian.client.lxp.common.dialog.DialogManager;
import com.xuejian.client.lxp.common.gson.CommonJson;
import com.xuejian.client.lxp.common.utils.ShareUtils;
import com.xuejian.client.lxp.module.MainActivity;
import com.xuejian.client.lxp.module.goods.OrderDetailActivity;
import com.xuejian.client.lxp.module.goods.OrderListActivity;

import java.lang.ref.WeakReference;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by yibiao.qin on 2015/11/18.
 */
public class PaymentActivity extends PeachBaseActivity implements View.OnClickListener {

    @Bind(R.id.tv_title_back)
    TextView tvTitleBack;
    @Bind(R.id.tv_title)
    TextView tvTitle;
    @Bind(R.id.tv_pay_state)
    TextView tvState;
    @Bind(R.id.tv_main)
    TextView tvMain;
    @Bind(R.id.tv_order_detail)
    TextView tvOrderDetail;
    private static final int ALI_PAY = 1001;
    private static final int ALI_PAY_CHECK = 1002;

    final IWXAPI msgApi = WXAPIFactory.createWXAPI(this, null);

    private final String Tag = "PaymentActivity";

    private final PayHandler handler = new PayHandler(this);

    private static class PayHandler extends Handler {
        private final WeakReference<PaymentActivity> mActivity;

        public PayHandler(PaymentActivity activity) {
            mActivity = new WeakReference<PaymentActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case ALI_PAY: {
                    Intent intent = new Intent(mActivity.get(), OrderListActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    PayResult payResult = new PayResult((String) msg.obj);

                    // 支付宝返回此次支付结果及加签，建议对支付宝签名信息拿签约时支付宝提供的公钥做验签
                    String resultStatus = payResult.getResultStatus();
                    System.out.println("resultInfo " + payResult.toString());
                    // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                    if (TextUtils.equals(resultStatus, "9000")) {

                        if (mActivity.get()!=null){
                            Toast.makeText(mActivity.get(), "支付成功",
                                    Toast.LENGTH_SHORT).show();
                            mActivity.get().tvOrderDetail.setVisibility(View.VISIBLE);
                            mActivity.get().tvMain.setVisibility(View.VISIBLE);
                            mActivity.get().tvState.setText("订单已支付\n请等待卖家确认");
                        }
                    } else {
                        // 判断resultStatus 为非“9000”则代表可能支付失败
                        // “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                        if (TextUtils.equals(resultStatus, "8000")) {
                            Toast.makeText(mActivity.get(), "支付结果确认中",
                                    Toast.LENGTH_SHORT).show();

                        }else if (TextUtils.equals(resultStatus, "6001")){
                            Toast.makeText(mActivity.get(), "支付取消 " ,
                                    Toast.LENGTH_SHORT).show();
                        }else {
                            // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                            Toast.makeText(mActivity.get(), "支付失败 " + resultStatus,
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                    if (mActivity.get()!=null){
                        mActivity.get().startActivity(intent);
                        mActivity.get().finish();
                    }

                    break;
                }
                case ALI_PAY_CHECK: {
                    Toast.makeText(mActivity.get(), "检查结果为：" + msg.obj,
                            Toast.LENGTH_SHORT).show();
                    break;
                }
                default:
                    break;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_state);
        ButterKnife.bind(this);
        tvTitleBack.setOnClickListener(this);
        DialogManager.getInstance().showLoadingDialog(this);
        tvOrderDetail.setVisibility(View.GONE);
        tvMain.setVisibility(View.GONE);
//        SpannableString priceStr = new SpannableString("¥35353");
//        priceStr.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.price_color)), 0, priceStr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//        priceStr.setSpan(new AbsoluteSizeSpan(15, true), 0, priceStr.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
//        SpannableStringBuilder spb = new SpannableStringBuilder();
//        spb.append("总价:").append(priceStr);
//        tvPrice.setText(spb);


        String type = getIntent().getStringExtra("type");
        final long orderId = getIntent().getLongExtra("orderId", 0);

        switch (type) {
            case "weixinpay":
                getPrePayInfo(orderId, "wechat");
                break;
            case "alipay":
                getPrePayInfo(orderId, "alipay");
                break;
            default:
                break;
        }
        tvMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PaymentActivity.this, MainActivity.class);
                intent.putExtra("back",true);
                startActivity(intent);
            }
        });
        tvOrderDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PaymentActivity.this, OrderDetailActivity.class);
                intent.putExtra("orderDetail",true);
                intent.putExtra("orderId",orderId);
                startActivity(intent);
            }
        });

    }

    private void getPrePayInfo(long orderId, final String vendor) {
        TravelApi.getPrePayInfo(orderId, vendor, new HttpCallBack<String>() {

            @Override
            public void doSuccess(String result, String method) {
                if (BuildConfig.DEBUG){
                    System.out.println(result);
                }
                DialogManager.getInstance().dissMissLoadingDialog();
                CommonJson<PrePayRespBean> bean = CommonJson.fromJson(result, PrePayRespBean.class);
                if (bean.code==0){
                    switch (vendor){
                        case "wechat":
                            startWeixinPay(bean.result);
                            break;
                        case "alipay":
                            startAliPay(bean.result);
                            break;
                        default:
                            break;
                    }
                }else {
                    Toast.makeText(PaymentActivity.this, "支付失败！", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void doFailure(Exception error, String msg, String method) {
                DialogManager.getInstance().dissMissLoadingDialog();
                Toast.makeText(PaymentActivity.this, "支付失败！", Toast.LENGTH_LONG).show();
            }

            @Override
            public void doFailure(Exception error, String msg, String method, int code) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_title_back:
                finish();
                break;
            default:
                break;
        }
    }

    public void startAliPay(final PrePayRespBean payInfo) {
        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                // 构造PayTask 对象
                PayTask alipay = new PayTask(PaymentActivity.this);
                // 调用支付接口，获取支付结果
                String result = alipay.pay(payInfo.getRequestString());

                Message msg = new Message();
                msg.what = ALI_PAY;
                msg.obj = result;
                handler.sendMessage(msg);
            }
        };
        // 必须异步调用
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

    public void startWeixinPay(final PrePayRespBean payInfo) {

        PayReq payReq = new PayReq();
        payReq.appId = payInfo.getAppid();
        payReq.partnerId = payInfo.getPartnerid();
        payReq.prepayId = payInfo.getPrepayid();
        payReq.packageValue = payInfo.getPackageX();
        payReq.nonceStr = payInfo.getNoncestr();
        payReq.timeStamp = payInfo.getTimestamp();
        payReq.sign = payInfo.getSign();
        msgApi.registerApp(ShareUtils.PlatfromSetting.WX_APPID);
        msgApi.sendReq(payReq);
        finish();
//        PayReq payReq = new PayReq();
//            payReq.appId = ShareUtils.PlatfromSetting.WX_APPID;
//            payReq.partnerId = "1278401701";
//            payReq.prepayId = "wx201512161630147fb297eaf80597523823";
//            payReq.packageValue = "Sign=WXPay";
//            payReq.nonceStr = "89082c23-de24-4b4e-8c5a-5f713892";
//            payReq.timeStamp = "1450254622";
//            payReq.sign = "00A752DC9D26DAF84BB90E13BA0DC482";
//            msgApi.registerApp(ShareUtils.PlatfromSetting.WX_APPID);
//            msgApi.sendReq(payReq);
    }
}
