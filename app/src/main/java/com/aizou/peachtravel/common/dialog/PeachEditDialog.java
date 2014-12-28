package com.aizou.peachtravel.common.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.aizou.peachtravel.R;

/**
 * Created by Rjm on 2014/12/16.
 */
public class PeachEditDialog extends Dialog {

    private TextView mTitleTv;
    private EditText mContetEt;
    private Button mOkBtn;
    private Button mCancleBtn;

    public PeachEditDialog(Context context) {
        super(context, R.style.ComfirmDialog);
        initView();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    public void setMessage(String str) {
        mContetEt.setText(str);
    }

    public void setTitle(String str) {
        if (!TextUtils.isEmpty(str)) {
            mTitleTv.setText(str);
            mTitleTv.setVisibility(View.VISIBLE);
        }
    }

    public String getMessage(){
        return mContetEt.getText().toString().trim();
    }

    private void initView() {
        setContentView(R.layout.dialog_edit);
        mTitleTv = (TextView) findViewById(R.id.tv_dialog_title);
        mTitleTv.setVisibility(View.GONE);
        mContetEt = (EditText) findViewById(R.id.et_dialog);
        mOkBtn = (Button) findViewById(R.id.btn_ok);
    }

    public void setPositiveButton(String str, View.OnClickListener listener) {
        mOkBtn.setText(str);
        mOkBtn.setOnClickListener(listener);
    }

}
