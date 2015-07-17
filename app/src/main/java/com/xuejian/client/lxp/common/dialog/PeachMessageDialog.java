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
 * Created by Rjm on 2014/12/16.
 */
public class PeachMessageDialog extends Dialog {

    private TextView mTitleTv;
    private TextView mContetTv;
    private Button mOkBtn;
    private Button mCancleBtn;
    private View midView;

    public PeachMessageDialog(Context context) {
        super(context, R.style.ComfirmDialog);
        initView();
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

    public void setTitleIcon(int res) {
        mTitleTv.setCompoundDrawablesWithIntrinsicBounds(res, 0, 0, 0);
    }

    private void initView() {
        setContentView(R.layout.view_peach_dialog);
        mTitleTv = (TextView) findViewById(R.id.tv_dialog_title);
        mTitleTv.setVisibility(View.GONE);
        mContetTv = (TextView) findViewById(R.id.tv_dialog_message);
        mOkBtn = (Button) findViewById(R.id.btn_ok);
        mCancleBtn = (Button) findViewById(R.id.btn_cancle);
        midView = findViewById(R.id.dialog_mid_line);
    }

    public void setPositiveButton(String str, View.OnClickListener listener) {

        mOkBtn.setText(str);
        mOkBtn.setOnClickListener(listener);
    }

    public void isCancle(boolean isCancle) {
        if (!isCancle) {
            mCancleBtn.setVisibility(View.GONE);
            midView.setVisibility(View.GONE);
        }
    }

    public void setNegativeButton(String str, View.OnClickListener listener) {
        mCancleBtn.setText(str);
        mCancleBtn.setVisibility(View.VISIBLE);
        mCancleBtn.setOnClickListener(listener);
    }
}
