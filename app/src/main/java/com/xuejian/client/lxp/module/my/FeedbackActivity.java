package com.xuejian.client.lxp.module.my;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.aizou.core.dialog.ToastUtil;
import com.aizou.core.http.HttpCallBack;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.PeachBaseActivity;
import com.xuejian.client.lxp.bean.ModifyResult;
import com.xuejian.client.lxp.common.api.OtherApi;
import com.xuejian.client.lxp.common.dialog.DialogManager;
import com.xuejian.client.lxp.common.gson.CommonJson;
import com.xuejian.client.lxp.common.widget.TitleHeaderBar;


public class FeedbackActivity extends PeachBaseActivity {
//	private View mTitlebar;

    private EditText contentEt;
    private TextView okBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    private void initView() {
        setContentView(R.layout.activity_feedback);
        initTitlebar();
        contentEt = (EditText) findViewById(R.id.et_content);
//		emailEt = (EditText) findViewById(R.id.et_email);
//		phoneEt = (EditText) findViewById(R.id.et_phone);
        okBtn = (TextView) findViewById(R.id.btn_ok);
        okBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(contentEt.getText())) {
                    ToastUtil.getInstance(mContext).showToast("说点什么吧");
                } else {
                    feedback();
                }

            }
        });
    }

    private void initTitlebar() {
//		mTitlebar = findViewById(R.id.title_bar);
        TitleHeaderBar titleBar = (TitleHeaderBar) findViewById(R.id.ly_header_bar_title_wrap);
        titleBar.getTitleTextView().setText("意见反馈");
        titleBar.enableBackKey(true);
    }

    @Override
    public void finish() {
        super.finish();
    }

    private void feedback() {
        DialogManager.getInstance().showLoadingDialog(mContext, "正在发送");
        OtherApi.feedback(contentEt.getText().toString().trim(), new HttpCallBack<String>() {
            @Override
            public void doSuccess(String result, String method) {
                DialogManager.getInstance().dissMissLoadingDialog();
                CommonJson<ModifyResult> feedbackResult = CommonJson.fromJson(result, ModifyResult.class);
                if (feedbackResult.code == 0) {
                    ToastUtil.getInstance(mContext).showToast("谢谢反馈，我们在努力做到更好");
                } else {
                    ToastUtil.getInstance(mContext).showToast("提交失败");
                }
            }

            @Override
            public void doFailure(Exception error, String msg, String method) {
                DialogManager.getInstance().dissMissLoadingDialog();
                if (!isFinishing())
                    ToastUtil.getInstance(FeedbackActivity.this).showToast(getResources().getString(R.string.request_network_failed));
            }

            @Override
            public void doFailure(Exception error, String msg, String method, int code) {

            }
        });
    }

}
