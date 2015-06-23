package com.xuejian.client.lxp.module.toolbox.im;

import android.os.Bundle;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.aizou.core.dialog.ToastUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.PeachBaseActivity;
import com.xuejian.client.lxp.common.utils.CommonUtils;
import com.xuejian.client.lxp.common.widget.TitleHeaderBar;
import com.xuejian.client.lxp.db.userDB.User;
import com.xuejian.client.lxp.db.userDB.UserDBManager;
import com.xuejian.client.lxp.module.toolbox.im.group.CallBack;
import com.xuejian.client.lxp.module.toolbox.im.group.GroupManager;

public class ModifyGroupNameActivity extends PeachBaseActivity implements View.OnClickListener {
    @ViewInject(R.id.et_groupname)
    private EditText groupNameEt;
    @ViewInject(R.id.title_bar)
    private TitleHeaderBar titleHeaderBar;

    private User group;
    private String groupId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_groupname);
        ViewUtils.inject(this);
        titleHeaderBar.getTitleTextView().setText("设置群名称");
        titleHeaderBar.enableBackKey(true);
        findViewById(R.id.btn_save).setOnClickListener(new View.OnClickListener() {
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
                GroupManager.getGroupManager().editGroupName(groupId,groupNameEt.getText().toString().trim(), new CallBack() {
                    @Override
                    public void onSuccess() {
                        setResult(RESULT_OK);
                        finish();
                    }

                    @Override
                    public void onFailed() {
                        setResult(RESULT_OK);
                        finish();
                    }
                });
//                try {
//                    EMGroupManager.getInstance().changeGroupName(groupId,groupNameEt.getText().toString().trim());
//                    setResult(RESULT_OK);
//                    finish();
//                } catch (EaseMobException e) {
//                    e.printStackTrace();
//                }
            }
        });
        initData();

    }

    private void initData() {
        groupId = getIntent().getStringExtra("groupId");
        System.out.println("groupId "+groupId);
        group = UserDBManager.getInstance().getContactByUserId(Long.parseLong(groupId));
        groupNameEt.setText(group.getNickName());
        CharSequence text = groupNameEt.getText();
        //Debug.asserts(text instanceof Spannable);
        if (text instanceof Spannable) {
            Spannable spanText = (Spannable) text;
            Selection.setSelection(spanText, text.length());
        }
    }


    @Override
    public void onClick(View v) {
    }
}
