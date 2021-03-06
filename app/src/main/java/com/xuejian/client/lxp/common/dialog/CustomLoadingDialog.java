package com.xuejian.client.lxp.common.dialog;


import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.xuejian.client.lxp.R;

/**
 * 自定义圆形进度条对话框
 */
public class CustomLoadingDialog extends ProgressDialog {

    private String content;
    private TextView progress_dialog_content;
    private ImageView progressIv;

    public CustomLoadingDialog(Context context, String content) {
        super(context, R.style.ComfirmDialog);
        this.content = content;
        setCanceledOnTouchOutside(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
    }

    private void initData() {
        setContent(content);
    }

    public void setContent(String str) {
        if (TextUtils.isEmpty(str)) {
            progress_dialog_content.setVisibility(View.GONE);
        } else {
            progress_dialog_content.setVisibility(View.VISIBLE);
            progress_dialog_content.setText(str);
        }

    }

    private void initView() {
        try {
            setContentView(R.layout.view_custom_loading_dialog);
            progress_dialog_content = (TextView) findViewById(R.id.progress_dialog_content);
            progressIv = (ImageView) findViewById(R.id.pb_iv);
            ((AnimationDrawable) progressIv.getDrawable()).start();
        }catch (Exception e){
            dismiss();
        }

    }

    @Override
    public void dismiss() {
        super.dismiss();
    }
}
