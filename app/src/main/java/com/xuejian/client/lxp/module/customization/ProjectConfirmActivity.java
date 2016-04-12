package com.xuejian.client.lxp.module.customization;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
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

import com.aizou.core.http.HttpCallBack;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.PeachBaseActivity;
import com.xuejian.client.lxp.bean.BountiesBean;
import com.xuejian.client.lxp.bean.OrderBean;
import com.xuejian.client.lxp.common.api.TravelApi;

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
                etPrice.setEnabled(false);
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
                etPrice.setEnabled(true);
                llPriceContainer.setVisibility(View.GONE);
            }
        });
        tvSubmitOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bean!=null) {
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

    private void createProject(BountiesBean bean) {
        TravelApi.createProject(bean, new HttpCallBack<String>() {
            @Override
            public void doSuccess(String result, String method) {
                showPayActionDialog(new OrderBean());
            }

            @Override
            public void doFailure(Exception error, String msg, String method) {

            }

            @Override
            public void doFailure(Exception error, String msg, String method, int code) {

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


    private void showPayActionDialog(final OrderBean currentOrder) {
        final Activity act = this;
        final AlertDialog dialog = new AlertDialog.Builder(act).create();
        View contentView = View.inflate(act, R.layout.dialog_select_payment, null);
        CheckedTextView alipay = (CheckedTextView) contentView.findViewById(R.id.ctv_alipay);
        CheckedTextView weixinpay = (CheckedTextView) contentView.findViewById(R.id.ctv_weixin);
        dialog.show();
        WindowManager windowManager = act.getWindowManager();
        Window window = dialog.getWindow();
        window.setContentView(contentView);
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = display.getWidth(); // 设置宽度
        lp.height = display.getHeight();
        window.setAttributes(lp);
        window.setGravity(Gravity.CENTER); // 此处可以设置dialog显示的位置
        window.setWindowAnimations(R.style.SelectPicDialog); // 添加动画
    }
}
