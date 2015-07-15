package com.xuejian.client.lxp.common.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xuejian.client.lxp.R;


/**
 * 自定义圆形进度条对话框
 */
public class MoreDialog extends Dialog {

    private TextView mTitleTv;
    private TextView mContetTv;
    private TextView tv1,tv2,tv3,tv4;
    private LinearLayout more_top,more_tv1,more_tv2,more_tv3,more_tv4;

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
        more_top.setVisibility(View.GONE);
    }

    public TextView getTv1(){return tv1;}
    public TextView getTv2(){return tv2;}
    public TextView getTv3(){return tv3;}
    public TextView getTv4(){return tv4;}

    public void setMoreStyle(boolean hasTop,int size,String[] names){
        if(hasTop){

        }else{
            hideInfoTitle();
        }
        switch (size){
            case 1:more_tv1.setVisibility(View.GONE);
                   more_tv2.setVisibility(View.GONE);
                   more_tv3.setVisibility(View.GONE);
                   tv4.setText(names[0]);
                   break;

            case 2:more_tv1.setVisibility(View.GONE);
                more_tv2.setVisibility(View.GONE);
                tv3.setText(names[0]);
                tv4.setText(names[1]);
                break;

            case 3:more_tv2.setVisibility(View.GONE);
                tv1.setText(names[0]);
                tv3.setText(names[1]);
                tv4.setText(names[2]);
                break;

            case 13:more_tv4.setVisibility(View.GONE);
                tv1.setText(names[0]);
                tv2.setText(names[1]);
                tv3.setText(names[2]);
                break;

            case 4:
                tv1.setText(names[0]);
                tv2.setText(names[1]);
                tv3.setText(names[2]);
                tv4.setText(names[3]);
                break;
        }
    }

    private void initView() {
        setContentView(R.layout.view_dialog_more_choice);
        mTitleTv = (TextView) findViewById(R.id.tv_dialog_title);
        //mTitleTv.setVisibility(View.GONE);
        mContetTv = (TextView) findViewById(R.id.tv_dialog_message);
        more_top=(LinearLayout)findViewById(R.id.more_top_box);
        more_tv1=(LinearLayout)findViewById(R.id.more_tv1);
        more_tv2=(LinearLayout)findViewById(R.id.more_tv2);
        more_tv3=(LinearLayout)findViewById(R.id.more_tv3);
        more_tv4=(LinearLayout)findViewById(R.id.more_tv4);
        tv1=(TextView)findViewById(R.id.dia_tv1);
        tv2=(TextView)findViewById(R.id.dia_tv2);
        tv3=(TextView)findViewById(R.id.dia_tv3);
        tv4=(TextView)findViewById(R.id.dia_tv4);
    }
}
