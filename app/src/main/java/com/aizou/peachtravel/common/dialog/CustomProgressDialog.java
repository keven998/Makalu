package com.aizou.peachtravel.common.dialog;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;

import com.aizou.peachtravel.R;
import com.aizou.peachtravel.common.widget.circularprogressbar.RateTextCircularProgressBar;

/**
 * 自定义圆形进度条对话框
 */
public class CustomProgressDialog extends ProgressDialog{

    private RateTextCircularProgressBar progressBar;
	public CustomProgressDialog(Context context) {
		super(context);
		setCanceledOnTouchOutside(false);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
		initData();
	}

	private void initData() {

	}

    public void setProgress(int progress){
        progressBar.setProgress(progress);
    }

    public void setMax(int max){
        progressBar.setMax(max);
    }

    public void setTextSize(int size){
        progressBar.setTextSize(size);
    }
    public void setTextColor(int color){
        progressBar.setTextColor(color);
    }


	private void initView() {
		setContentView(R.layout.view_custom_progress_dialog);
        progressBar = (RateTextCircularProgressBar) findViewById(R.id.pb_rate);
	}


}
