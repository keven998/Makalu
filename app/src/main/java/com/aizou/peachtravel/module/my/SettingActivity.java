package com.aizou.peachtravel.module.my;

import java.util.HashMap;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aizou.core.dialog.DialogManager;
import com.aizou.core.log.LogUtil;
import com.aizou.peachtravel.R;
import com.aizou.peachtravel.base.PeachBaseActivity;
import com.aizou.peachtravel.bean.UpdateResult;
import com.aizou.peachtravel.common.widget.TitleHeaderBar;


public class SettingActivity extends PeachBaseActivity implements OnClickListener {
	// private View mTitlebar;
	private TextView versionUpdateLl, feedbackLl, xtLl;
	private UpdateResult mUpdateResult;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
	}

	private void initView() {
		setContentView(R.layout.activity_setting);
		initTitlebar();
		versionUpdateLl = (TextView) findViewById(R.id.ll_version_update);
		feedbackLl = (TextView) findViewById(R.id.ll_feedback);
		xtLl = (TextView) findViewById(R.id.ll_xt);
		versionUpdateLl.setOnClickListener(this);
		feedbackLl.setOnClickListener(this);
		xtLl.setOnClickListener(this);
	}

	private void initTitlebar() {
        TitleHeaderBar thb = (TitleHeaderBar)findViewById(R.id.ly_header_bar_title_wrap);
        thb.getTitleTextView().setText("设置");
	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub
		super.finish();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ll_version_update:
			DialogManager.getInstance().showProgressDialog(SettingActivity.this, "正在检查更新");
			update();
			break;

		case R.id.ll_feedback:
			Intent feedback = new Intent(mContext, FeedbackActivity.class);
			startActivity(feedback);
			break;

		case R.id.ll_xt:
			Intent pushIntent = new Intent(mContext, PushSettingActivity.class);
			startActivity(pushIntent);
			break;

		default:
			break;
		}
	}

	private void update() {
		HashMap<String, String> map = new HashMap<String, String>();
//		map.put("appVer", UpdateUtil.getVerName(mContext));
//		map.put("platformVer", "android " + android.os.Build.VERSION.RELEASE);
		LogUtil.d("platform", android.os.Build.MODEL + ","
                + android.os.Build.VERSION.SDK + ","
                + android.os.Build.VERSION.RELEASE);
//		LxpRequest.updateAfter(mContext, map, new AsyncHttpResponseHandler() {
//
//			@Override
//			public void onSuccess(int statusCode, Header[] headers,
//					byte[] responseBody) {
//				closeProgressDialog();
//				mUpdateResult = GsonTools.parseJsonToBean(new String(
//						responseBody), UpdateResult.class);
//				if (mUpdateResult.result.update) {
//					UpdateUtil.showUpdateDialog(mContext, "有新的版本!",
//							mUpdateResult.result.downloadUrl);
//				} else {
//					ToastUtil.getInstance(mContext).showToast("已是最新版本！");
//				}
//
//			}
//
//			@Override
//			public void onFailure(int statusCode, Header[] headers,
//					byte[] responseBody, Throwable error) {
//				DialogManager.getInstance().dissMissProgressDialog();
//
//			}
//
//		});
	}

}
