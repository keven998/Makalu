package com.xuejian.client.lxp.module.my;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.TextView;

import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.PeachBaseActivity;
import com.xuejian.client.lxp.common.account.AccountManager;
import com.xuejian.client.lxp.db.User;

/**
 * Created by lxp_dqm07 on 2015/5/18.
 */
public class ModifyStatusOrSexActivity extends PeachBaseActivity implements View.OnClickListener {

    private User user;
    private String gender;
    private CheckedTextView ctv1;
    private CheckedTextView ctv2;
    private CheckedTextView ctv3;
    private CheckedTextView ctv4;
    private TextView tv_cancel;
    private TextView tv_confirm;
    private TextView tv_title_bar_title;
    private String type;
    private String[] sexs = {"美女", "帅锅", "一言难尽", "保密"};
    private String[] status = {"旅行灵感时期", "正在准备旅行", "旅行中", "不知道"};
    CheckedTextView[] checkBoxes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statusorsex);
        ctv1 = (CheckedTextView) findViewById(R.id.ctv_1);
        ctv2 = (CheckedTextView) findViewById(R.id.ctv_2);
        ctv3 = (CheckedTextView) findViewById(R.id.ctv_3);
        ctv4 = (CheckedTextView) findViewById(R.id.ctv_4);

        tv_cancel = (TextView) findViewById(R.id.tv_cancel);
        tv_confirm = (TextView) findViewById(R.id.tv_confirm);
        tv_title_bar_title = (TextView) findViewById(R.id.tv_title_bar_title);
        ctv1.setOnClickListener(this);
        ctv2.setOnClickListener(this);
        ctv3.setOnClickListener(this);
        ctv4.setOnClickListener(this);
        tv_cancel.setOnClickListener(this);
        tv_confirm.setOnClickListener(this);
        type = getIntent().getExtras().getString("type");
        user = AccountManager.getInstance().getLoginAccount(this);
        checkBoxes = new CheckedTextView[]{ctv1, ctv2, ctv3, ctv4};
        if (type.equals("sex")) {
            tv_title_bar_title.setText("性别设置");
            ctv1.setText(sexs[0]);
            ctv2.setText(sexs[1]);
            ctv3.setText(sexs[2]);
            ctv4.setText(sexs[3]);
            initSexData();
        } else if (type.equals("status")) {
            tv_title_bar_title.setText("状态");
            ctv1.setText(status[0]);
            ctv2.setText(status[1]);
            ctv3.setText(status[2]);
            ctv4.setText(status[3]);
        }
    }

    private void initSexData() {
        user = AccountManager.getInstance().getLoginAccount(this);
        gender = user.getGenderDesc();
        if (gender.equals(sexs[0])) {
            checkBoxes[0].setChecked(true);
        } else if (gender.equals(sexs[1])) {
            checkBoxes[1].setChecked(true);
        } else if (gender.equals(sexs[2])) {
            checkBoxes[2].setChecked(true);
        } else if (gender.equals(sexs[3])) {
            checkBoxes[3].setChecked(true);
        }
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
        switch (v.getId()) {
            case R.id.ctv_1:
                gender = sexs[0];
                changeStatus(0);
                checkBoxes[0].setChecked(true);
                break;

            case R.id.ctv_2:
                gender = sexs[1];
                changeStatus(1);
                checkBoxes[1].setChecked(true);
                break;

            case R.id.ctv_3:
                gender = sexs[2];
                changeStatus(2);
                checkBoxes[2].setChecked(true);
                break;

            case R.id.ctv_4:
                gender = sexs[3];
                changeStatus(3);
                checkBoxes[3].setChecked(true);
                break;
            case R.id.tv_cancel:
                finish();
                break;
            case R.id.tv_confirm:
                Intent fintent = new Intent();
                fintent.putExtra("result", gender);
                setResult(RESULT_OK, fintent);
                finish();
                overridePendingTransition(0, R.anim.fade_out);
                break;
        }
    }

    private void changeStatus(int i) {
        for (int j = 0; j < 4; j++) {
            if (i != j)
                checkBoxes[j].setChecked(false);
        }
    }

}
