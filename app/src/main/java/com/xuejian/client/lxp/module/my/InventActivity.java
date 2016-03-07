package com.xuejian.client.lxp.module.my;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.PeachBaseActivity;
import com.xuejian.client.lxp.common.account.AccountManager;
import com.xuejian.client.lxp.common.utils.ShareUtils;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by yibiao.qin on 2016/3/4.
 */
public class InventActivity extends PeachBaseActivity {

    @Bind(R.id.tv_title_back)
    TextView tvTitleBack;
    @Bind(R.id.tv_title)
    TextView tvTitle;
    @Bind(R.id.invent_code)
    TextView inventCode;
    @Bind(R.id.tv_copy)
    TextView tvCopy;
    @Bind(R.id.tv_invent)
    TextView tv_invent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invent);
        ButterKnife.bind(this);
        tvTitleBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        String code = AccountManager.getInstance().getLoginAccountInfo().getPromotionCode();
        inventCode.setText(code);
        final ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        tvCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clipboardManager.setPrimaryClip(ClipData.newPlainText(null,inventCode.getText()));
                Toast.makeText(mContext,"已复制",Toast.LENGTH_SHORT).show();
            }
        });

        tv_invent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareUtils.shareAppToWx(InventActivity.this, null);
            }
        });

    }
}
