package com.aizou.peachtravel.module.my;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.aizou.core.dialog.DialogManager;
import com.aizou.core.dialog.ToastUtil;
import com.aizou.core.log.LogUtil;
import com.aizou.peachtravel.R;
import com.aizou.peachtravel.base.PeachBaseActivity;


public class FeedbackActivity extends PeachBaseActivity {
//	private View mTitlebar;

	private EditText contentEt, emailEt, phoneEt;
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
		emailEt = (EditText) findViewById(R.id.et_email);
		phoneEt = (EditText) findViewById(R.id.et_phone);
		okBtn = (Button) findViewById(R.id.btn_ok);
		okBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(TextUtils.isEmpty(contentEt.getText())){
					ToastUtil.getInstance(mContext).showToast("请填写内容");
				}else{
					DialogManager.getInstance().showProgressDialog(mContext, "正在提交");
					feedback();
				}
				
			}
		});
	}

	private void initTitlebar() {
//		mTitlebar = findViewById(R.id.title_bar);

	}
	
	@Override
	public void finish() {
		// TODO Auto-generated method stub
		super.finish();
	}

	private void feedback() {
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("body", contentEt.getText().toString());
			jsonObject.put("title", "test");
			JSONObject contactJson = new JSONObject();
			contactJson.put("tel", phoneEt.getText().toString());
			contactJson.put("email", emailEt.getText().toString());
			jsonObject.put("contact", contactJson);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		LogUtil.d("put params", jsonObject.toString());
//		LxpRequest.feedback(mContext, jsonObject.toString(), new AsyncHttpResponseHandler(){
//
//
//			@Override
//			public void onSuccess(int statusCode, Header[] headers,
//					byte[] responseBody) {
//				closeProgressDialog();
//				FeedbackActivity.this.finish();
//
//			}
//
//			@Override
//			public void onFailure(int statusCode, Header[] headers,
//					byte[] responseBody, Throwable error) {
//				closeProgressDialog();
//				ToastUtil.getInstance(mContext).showToast("提交失败，请稍后重试");
//				error.printStackTrace();
//
//			}
//
//			@Override
//			public void setRequestHeaders(Header[] requestHeaders) {
//				for(Header header: requestHeaders){
//					LogUtil.d("header",header.getName()+"--"+header.getValue()+"");
//				}
//				super.setRequestHeaders(requestHeaders);
//			}
//
//
//
//
//
//		});

	}

}
