package com.aizou.peachtravel.module.my;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.aizou.core.dialog.ToastUtil;
import com.aizou.core.http.HttpCallBack;
import com.aizou.peachtravel.R;
import com.aizou.peachtravel.base.PeachBaseActivity;
import com.aizou.peachtravel.common.api.UserApi;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

/**
 * Created by Rjm on 2014/10/13.
 */
public class VerifyPhoneActivity extends PeachBaseActivity implements View.OnClickListener {
    @ViewInject(R.id.et_sms)
    private EditText smsEt;
    @ViewInject(R.id.btn_time_down)
    private Button downTimeBtn;
    private CountDownTimer countDownTimer;
    private String tel,pwd,actionCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_phone);
        ViewUtils.inject(this);
        findViewById(R.id.btn_next).setOnClickListener(this);
        downTimeBtn.setOnClickListener(this);
        startCountDownTime();
        initData();


    }
    private void initData(){
        tel = getIntent().getStringExtra("tel");
        pwd = getIntent().getStringExtra("pwd");
        actionCode = getIntent().getStringExtra("actionCode");
    }



    private void startCountDownTime(){
        downTimeBtn.setClickable(false);
        countDownTimer= new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                downTimeBtn.setText("(" + (millisUntilFinished / 1000) + ")");
            }

            @Override
            public void onFinish() {
                downTimeBtn.setText("重新获取");
                downTimeBtn.setClickable(true);
            }
        }.start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_next:
                if(TextUtils.isEmpty(smsEt.getText().toString())){
                    ToastUtil.getInstance(mContext).showToast("请输入验证码");
                }else{
                    UserApi.signUp(tel,pwd,smsEt.getText().toString().trim(),new HttpCallBack<String>() {
                        @Override
                        public void doSucess(String result, String method) {

                        }

                        @Override
                        public void doFailure(Exception error, String msg, String method) {

                        }
                    });
                }
                break;
            case R.id.btn_time_down:
                startCountDownTime();
                break;
        }
    }
}
