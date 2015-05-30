package com.xuejian.client.lxp.module.my;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.PeachBaseActivity;
import com.xuejian.client.lxp.common.widget.TitleHeaderBar;

/**
 * Created by lxp_dqm07 on 2015/5/18.
 */
public class ModifyStatusOrSexActivity extends PeachBaseActivity implements View.OnClickListener {

    private TextView first,second,third,fourth;
    private TitleHeaderBar titleHeaderBar;
    private String type;
    private String[] sexs={"美女","帅锅","一言难尽","保密"};
    private String[] status={"旅行灵感时期","正在准备旅行","旅行中","不知道"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statusorsex);
        titleHeaderBar=(TitleHeaderBar)findViewById(R.id.ss_title_bar);
        first=(TextView)findViewById(R.id.ss_tv_first);
        second=(TextView)findViewById(R.id.ss_tv_second);
        third=(TextView)findViewById(R.id.ss_tv_third);
        fourth=(TextView)findViewById(R.id.ss_tv_fourth);
        first.setOnClickListener(this);
        second.setOnClickListener(this);
        third.setOnClickListener(this);
        fourth.setOnClickListener(this);
        type=getIntent().getExtras().getString("type");
        if(type.equals("sex")){
            titleHeaderBar.getTitleTextView().setText("性别");
            first.setText(sexs[0]);
            second.setText(sexs[1]);
            third.setText(sexs[2]);
            fourth.setText(sexs[3]);
        }else if(type.equals("status")){
            titleHeaderBar.getTitleTextView().setText("状态");
            first.setText(status[0]);
            second.setText(status[1]);
            third.setText(status[2]);
            fourth.setText(status[3]);
        }
        titleHeaderBar.enableBackKey(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void finish() {
        super.finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ss_tv_first:
                Intent intent=new Intent();
                intent.putExtra("result",first.getText());
                setResult(RESULT_OK,intent);
                finish();
                overridePendingTransition(0,R.anim.fade_out);
                break;

            case R.id.ss_tv_second:
                Intent sintent=new Intent();
                sintent.putExtra("result",second.getText());
                setResult(RESULT_OK,sintent);
                finish();
                overridePendingTransition(0,R.anim.fade_out);
                break;

            case R.id.ss_tv_third:
                Intent tintent=new Intent();
                tintent.putExtra("result",third.getText());
                setResult(RESULT_OK,tintent);
                finish();
                overridePendingTransition(0,R.anim.fade_out);
                break;

            case R.id.ss_tv_fourth:
                Intent fintent=new Intent();
                fintent.putExtra("result",fourth.getText());
                setResult(RESULT_OK,fintent);
                finish();
                overridePendingTransition(0,R.anim.fade_out);
                break;
        }
    }
}
