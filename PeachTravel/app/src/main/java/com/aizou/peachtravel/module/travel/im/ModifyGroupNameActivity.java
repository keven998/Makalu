package com.aizou.peachtravel.module.travel.im;

import android.os.Bundle;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.aizou.core.dialog.DialogManager;
import com.aizou.core.dialog.ToastUtil;
import com.aizou.core.http.HttpCallBack;
import com.aizou.core.utils.RegexUtils;
import com.aizou.peachtravel.R;
import com.aizou.peachtravel.base.PeachBaseActivity;
import com.aizou.peachtravel.bean.ModifyResult;
import com.aizou.peachtravel.bean.PeachUser;
import com.aizou.peachtravel.common.account.AccountManager;
import com.aizou.peachtravel.common.api.UserApi;
import com.aizou.peachtravel.common.gson.CommonJson;
import com.aizou.peachtravel.common.utils.CommonUtils;
import com.aizou.peachtravel.common.utils.InputCheckUtils;
import com.aizou.peachtravel.common.widget.TitleHeaderBar;
import com.easemob.chat.EMGroup;
import com.easemob.chat.EMGroupManager;
import com.easemob.exceptions.EaseMobException;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

/**
 * Created by Rjm on 2014/10/11.
 */
public class ModifyGroupNameActivity extends PeachBaseActivity implements View.OnClickListener {
    @ViewInject(R.id.et_groupname)
    private EditText groupNameEt;
    @ViewInject(R.id.iv_delete)
    private ImageView deleteIv;
    @ViewInject(R.id.title_bar)
    private TitleHeaderBar titleHeaderBar;

    private EMGroup group;
    private String groupId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_groupname);
        ViewUtils.inject(this);
        deleteIv.setOnClickListener(this);
        titleHeaderBar.getRightTextView().setText("保存");
        titleHeaderBar.setRightOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!CommonUtils.isNetWorkConnected(mContext)){
                    ToastUtil.getInstance(mContext).showToast("无网络连接，请检查网络");
                    return;
                }
                if(TextUtils.isEmpty(groupNameEt.getText())){
                    ToastUtil.getInstance(mContext).showToast("请输入群名称");
                    return;
                }
                try {
                    EMGroupManager.getInstance().changeGroupName(groupId,groupNameEt.getText().toString().trim());
                    setResult(RESULT_OK);
                    finish();
                } catch (EaseMobException e) {
                    e.printStackTrace();
                }

            }
        });
        initData();

    }

    private void initData() {
        groupId = getIntent().getStringExtra("groupId");
        group = EMGroupManager.getInstance().getGroup(groupId);
        groupNameEt.setText(group.getGroupName());
        CharSequence text = groupNameEt.getText();
        //Debug.asserts(text instanceof Spannable);
        if (text instanceof Spannable) {
            Spannable spanText = (Spannable) text;
            Selection.setSelection(spanText, text.length());
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_delete:
                groupNameEt.setText("");
                break;
        }
    }
}
