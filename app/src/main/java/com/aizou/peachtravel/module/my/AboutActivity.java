package com.aizou.peachtravel.module.my;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.aizou.peachtravel.R;
import com.aizou.peachtravel.base.PeachBaseActivity;
import com.aizou.peachtravel.common.utils.UpdateUtil;


public class AboutActivity extends PeachBaseActivity {
//	private View mTitlebar;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
	}
	
	private void initView(){
		setContentView(R.layout.activity_about);
		initTitlebar();
		TextView verTv = (TextView) findViewById(R.id.tv_ver);
		verTv.setText("version "+ UpdateUtil.getVerName(mContext));
	}
	private void initTitlebar() {

	}
	
	@Override
		public void finish() {
			// TODO Auto-generated method stub
			super.finish();
		}

}
