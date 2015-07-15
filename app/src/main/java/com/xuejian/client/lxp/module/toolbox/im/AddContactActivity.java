/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.xuejian.client.lxp.module.toolbox.im;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.aizou.core.dialog.ToastUtil;
import com.aizou.core.http.HttpCallBack;
import com.umeng.analytics.MobclickAgent;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.ChatBaseActivity;
import com.xuejian.client.lxp.common.account.AccountManager;
import com.xuejian.client.lxp.common.api.UserApi;
import com.xuejian.client.lxp.common.dialog.DialogManager;
import com.xuejian.client.lxp.common.gson.CommonJson4List;
import com.xuejian.client.lxp.common.utils.ShareUtils;
import com.xuejian.client.lxp.common.widget.TitleHeaderBar;
import com.xuejian.client.lxp.db.User;
import com.xuejian.client.lxp.module.toolbox.HisMainPageActivity;

public class AddContactActivity extends ChatBaseActivity implements View.OnClickListener {
    private EditText editText;
//    private LinearLayout searchedUserLayout;
    private TextView nameText;
//    private Button searchBtn;
//    private ImageView avatar;
//    private InputMethodManager inputMethodManager;
    private String toAddUsername;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        initTitleBar();

        editText = (EditText) findViewById(R.id.edit_note);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEARCH) {
                    searchContact();
                }
                return true;
            }
        });
//        searchedUserLayout = (LinearLayout) findViewById(R.id.ll_user);
//        nameText = (TextView) findViewById(R.id.name);
//        searchBtn = (Button) findViewById(R.id.search);
//        avatar = (ImageView) findViewById(R.id.avatar);
        findViewById(R.id.tv_phone_contact).setOnClickListener(this);
        findViewById(R.id.tv_weixin_contacts).setOnClickListener(this);
//        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        findViewById(R.id.search_contact).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchContact();
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
//        MobclickAgent.onPageStart("page_add_friend");
    }

    @Override
    protected void onPause() {
        super.onPause();
//        MobclickAgent.onPageEnd("page_add_friend");
    }
    private void initTitleBar(){
        final TitleHeaderBar titleHeaderBar = (TitleHeaderBar) findViewById(R.id.ly_header_bar_title_wrap);

//        titleHeaderBar.getRightTextView().setText(getString(R.string.button_search));
//        titleHeaderBar.setRightOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                searchContact();
//            }
//        });

        titleHeaderBar.getTitleTextView().setText("添加好友");
        titleHeaderBar.findViewById(R.id.ly_title_bar_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * 查找contact
     *
     */
    public void searchContact() {
        MobclickAgent.onEvent(mContext,"event_search_friend");
        final String name = editText.getText().toString();
//        String saveText = searchBtn.getText().toString();
//        if (getString(R.string.button_search).equals(saveText)) {
            toAddUsername = name;
            if (TextUtils.isEmpty(name)) {
//                startActivity(new Intent(this, IMAlertDialog.class).putExtra("msg", "请输入用户名"));
                ToastUtil.getInstance(mContext).showToast("你想找谁呢～");
                return;
            }
            // TODO 从服务器获取此contact,如果不存在提示不存在此用户
            //服务器存在此用户，显示此用户和添加按钮
//			searchedUserLayout.setVisibility(View.VISIBLE);
//			nameText.setText(toAddUsername);
            DialogManager.getInstance().showLoadingDialog(this);
            UserApi.seachContact(toAddUsername, new HttpCallBack<String>() {
                @Override
                public void doSuccess(String result, String method) {
                    DialogManager.getInstance().dissMissLoadingDialog();
                    CommonJson4List<User> seachResult = CommonJson4List.fromJson(result, User.class);
                    if (seachResult.code == 0) {
                        if (seachResult.result.size() > 0) {
                            User user = seachResult.result.get(0);
                            /**
                             * 测试中注销掉
                             */
//                            if (AccountManager.getInstance().getLoginAccount(mContext).getUserId() == (seachResult.result.get(0).getUserId())) {
//                                ToastUtil.getInstance(mContext).showToast("不能添加自己");
//                                return;
//                            }

                            if (AccountManager.getInstance().getContactList(mContext).containsKey(seachResult.result.get(0).getUserId())) {
                                ToastUtil.getInstance(mContext).showToast("该用户已是你的好友");
                               /* User imUser = AccountManager.getInstance().getContactList(mContext).get(user.easemobUser);
                                imUser.setNickName(user.nickName);
                                imUser.setAvatar(user.avatar);
                                imUser.setSignature(user.signature);
                                imUser.setMemo(user.memo);
                                imUser.setGender(user.gender);
                                IMUtils.setUserHead(imUser);
                                AccountManager.getInstance().getContactList(mContext).put(imUser.getUsername(), imUser);
                                IMUserRepository.saveContact(mContext, imUser);*/
                                Intent intent = new Intent(mContext, HisMainPageActivity.class);
                                intent.putExtra("userId", user.getUserId().intValue());
                                // intent.putExtra("userNick", user.nickName);
                                startActivity(intent);
                                return;
                            }
                            Intent intent = new Intent(mContext, HisMainPageActivity.class);
                            //intent.putExtra("isSeach",true);
                            intent.putExtra("userId", user.getUserId().intValue());
                            startActivity(intent);
                        } else {
                            ToastUtil.getInstance(mContext).showToast("查无此用户~");
                        }

                    } else if (!TextUtils.isEmpty(seachResult.err.message)) {
                        ToastUtil.getInstance(mContext).showToast(seachResult.err.message);
                    }

                }

                @Override
                public void doFailure(Exception error, String msg, String method) {
                    DialogManager.getInstance().dissMissLoadingDialog();
                    if (!isFinishing())
                        ToastUtil.getInstance(AddContactActivity.this).showToast(getResources().getString(R.string.request_network_failed));
                }
            });

//        }
    }

    public void back(View v) {
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_phone_contact:
                MobclickAgent.onEvent(mContext,"event_add_friend_from_contacts");
                Intent phoneIntent = new Intent(mContext, AddPhoneContactActivity.class);
                startActivity(phoneIntent);
                break;

            case R.id.tv_weixin_contacts:
                MobclickAgent.onEvent(mContext,"event_notify_weichat_friends");
                ShareUtils.shareAppToWx(this, String.format("我正在用旅行派，搜索: %s 加我", AccountManager.getInstance().getLoginAccount(this).getNickName()));
                break;
        }
    }
}
