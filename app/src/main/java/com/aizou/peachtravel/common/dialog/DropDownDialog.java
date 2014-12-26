package com.aizou.peachtravel.common.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import com.aizou.peachtravel.R;

/**
 * Created by Rjm on 2014/12/10.
 */
public class DropDownDialog extends Dialog {


    public DropDownDialog(Context context) {
        super(context, R.style.DropDownDialog);
//        initView();
        setCanceledOnTouchOutside(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


//    public void setMessage(String str){
//        mContetTv.setText(str);
//    }
//    public void setTitle(String str){
//        if(!TextUtils.isEmpty(str)){
//            mTitleTv.setText(str);
//            mTitleTv.setVisibility(View.VISIBLE);
//        }
//

//    }

//    private void initView() {
//        setContentView(R.layout.view_comfirm_dialog);
//        mTitleTv = (TextView) findViewById(R.id.tv_dialog_title);
//        mTitleTv.setVisibility(View.GONE);
//        mContetTv = (TextView) findViewById(R.id.tv_dialog_message);
//        mOkBtn = (Button) findViewById(R.id.btn_ok);
//        mCancleBtn = (Button) findViewById(R.id.btn_cancle);
//    }
//
//    public void setPositiveButton(String str,View.OnClickListener listener){
//        mOkBtn.setText(str);
//        mOkBtn.setOnClickListener(listener);
//    }
//
//    public void setNegativeButton(String str,View.OnClickListener listener){
//        mCancleBtn.setText(str);
//        mCancleBtn.setOnClickListener(listener);
//    }
}
