package com.xuejian.client.lxp.module.dest;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.PeachBaseActivity;
import com.xuejian.client.lxp.bean.DemoBean;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by yibiao.qin on 2016/6/15.
 */
public class MsgActivity extends PeachBaseActivity {

    @Bind(R.id.iv_nav_back)
    ImageView mIvNavBack;
    @Bind(R.id.tv_title_bar_title)
    TextView mTvTitleBarTitle;
    @Bind(R.id.iv_menu)
    ImageView mIvMenu;
    @Bind(R.id.et_msg)
    EditText mEtMsg;
    @Bind(R.id.tv_save)
    TextView mTvSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_msg);
        ButterKnife.bind(this);
        mIvNavBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mTvSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( TextUtils.isEmpty(mEtMsg.getText().toString())){
                    Toast.makeText(MsgActivity.this,"请输入内容",Toast.LENGTH_SHORT).show();
                }else {
                    DemoBean bean = new DemoBean();
                    bean.desc = mEtMsg.getText().toString();
                    Intent intent = new Intent();
                    intent.putExtra("demo",bean);
                    setResult(RESULT_OK,intent);
                    finish();
                }

            }
        });
    }
}
