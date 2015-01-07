package com.aizou.peachtravel.module.my;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.aizou.core.http.HttpCallBack;
import com.aizou.peachtravel.bean.ModifyResult;
import com.aizou.peachtravel.common.api.OtherApi;
import com.aizou.peachtravel.common.dialog.DialogManager;
import com.aizou.core.dialog.ToastUtil;
import com.aizou.core.log.LogUtil;
import com.aizou.peachtravel.R;
import com.aizou.peachtravel.base.PeachBaseActivity;
import com.aizou.peachtravel.common.gson.CommonJson;
import com.aizou.peachtravel.common.widget.TitleHeaderBar;


public class FeedbackActivity extends PeachBaseActivity {
//	private View mTitlebar;

    private EditText contentEt;
	private Button okBtn;

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
		okBtn = (Button) findViewById(R.id.btn_ok);
		okBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(TextUtils.isEmpty(contentEt.getText())){
					ToastUtil.getInstance(mContext).showToast("你的吐槽呢，我读书少不要骗我");
				} else {
					feedback();
				}
				
			}
		});
	}

	private void initTitlebar() {
//		mTitlebar = findViewById(R.id.title_bar);
        TitleHeaderBar titleBar = (TitleHeaderBar)findViewById(R.id.ly_header_bar_title_wrap);
        titleBar.getTitleTextView().setText("意见与吐槽");
        titleBar.enableBackKey(true);
	}
	
	@Override
	public void finish() {
		super.finish();
	}

	private void feedback() {
        DialogManager.getInstance().showLoadingDialog(mContext,"正在提交");
        OtherApi.feedback(contentEt.getText().toString().trim(),new HttpCallBack<String>() {
            @Override
            public void doSucess(String result, String method) {
                DialogManager.getInstance().dissMissLoadingDialog();
                CommonJson<ModifyResult> feedbackResult = CommonJson.fromJson(result,ModifyResult.class);
                if(feedbackResult.code==0){
                    ToastUtil.getInstance(mContext).showToast("提交成功");
                }else {
                    ToastUtil.getInstance(mContext).showToast("提交失败");
                }
            }

            @Override
            public void doFailure(Exception error, String msg, String method) {
                DialogManager.getInstance().dissMissLoadingDialog();
                if (!isFinishing())
                ToastUtil.getInstance(FeedbackActivity.this).showToast(getResources().getString(R.string.request_network_failed));
            }
        });
	}

}
