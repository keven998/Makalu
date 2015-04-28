package com.aizou.peachtravel.common.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.aizou.peachtravel.R;



/**
 * 自定义圆形进度条对话框
 */
public class MoreDialog extends Dialog {

    private TextView mTitleTv;
    private TextView mContetTv;
    private TextView tv1,tv2,tv3;

    public MoreDialog(Context context) {
        super(context, R.style.ComfirmDialog);
        initView();
        setCanceledOnTouchOutside(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    public void setMessage(String str){
        mContetTv.setText(str);
    }


    public void setTitle(String str){
        if(!TextUtils.isEmpty(str)){
            mTitleTv.setText(str);
            mTitleTv.setVisibility(View.VISIBLE);
        }
    }

    public void hideInfoTitle(){
        mTitleTv.setVisibility(View.GONE);
        mContetTv.setVisibility(View.GONE);
    }

    public TextView getTv1(){return tv1;}
    public TextView getTv2(){return tv2;}
    public TextView getTv3(){return tv3;}

    private void initView() {
        setContentView(R.layout.view_dialog_more_choice);
        mTitleTv = (TextView) findViewById(R.id.tv_dialog_title);
        //mTitleTv.setVisibility(View.GONE);
        mContetTv = (TextView) findViewById(R.id.tv_dialog_message);
        tv1=(TextView)findViewById(R.id.dia_tv1);
        tv2=(TextView)findViewById(R.id.dia_tv2);
        tv3=(TextView)findViewById(R.id.dia_tv3);
    }
}
