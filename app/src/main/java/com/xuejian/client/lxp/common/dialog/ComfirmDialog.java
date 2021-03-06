package com.xuejian.client.lxp.common.dialog;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.xuejian.client.lxp.R;

/**
 * 自定义圆形进度条对话框
 */
public class ComfirmDialog extends Dialog {

    private TextView mTitleTv;
    private TextView mContetTv;
    private Button mOkBtn;
    private Button mCancleBtn;

    public ComfirmDialog(Context context) {
        super(context, R.style.ComfirmDialog);
        initView();
        setCanceledOnTouchOutside(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    public void setMessage(String str) {
        mContetTv.setText(str);
    }

    public void setTitle(String str) {
        if (!TextUtils.isEmpty(str)) {
            mTitleTv.setText(str);
            mTitleTv.setVisibility(View.VISIBLE);
        }


    }

    private void initView() {
        setContentView(R.layout.view_comfirm_dialog);
        mTitleTv = (TextView) findViewById(R.id.tv_dialog_title);
        mTitleTv.setVisibility(View.GONE);
        mContetTv = (TextView) findViewById(R.id.tv_dialog_message);
        mOkBtn = (Button) findViewById(R.id.btn_ok);
        mCancleBtn = (Button) findViewById(R.id.btn_cancle);
    }

    public void setPositiveButton(String str, View.OnClickListener listener) {
        mOkBtn.setText(str);
        mOkBtn.setOnClickListener(listener);
    }

    public void setNegativeButton(String str, View.OnClickListener listener) {
        mCancleBtn.setText(str);
        mCancleBtn.setOnClickListener(listener);
    }


}
