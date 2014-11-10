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
package com.aizou.peachtravel.module.travel.im;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aizou.core.dialog.DialogManager;
import com.aizou.core.dialog.ToastUtil;
import com.aizou.core.http.HttpCallBack;
import com.aizou.core.utils.StringUtil;
import com.aizou.peachtravel.R;
import com.aizou.peachtravel.base.BaseChatActivity;
import com.aizou.peachtravel.bean.PeachUser;
import com.aizou.peachtravel.common.account.AccountManager;
import com.aizou.peachtravel.common.api.UserApi;
import com.aizou.peachtravel.common.gson.CommonJson;
import com.aizou.peachtravel.common.gson.CommonJson4List;
import com.aizou.peachtravel.common.utils.IMUtils;
import com.aizou.peachtravel.config.PeachApplication;
import com.aizou.peachtravel.db.IMUser;
import com.aizou.peachtravel.db.respository.IMUserRepository;
import com.easemob.chat.EMContactManager;

public class AddContactActivity extends BaseChatActivity implements View.OnClickListener {
    private EditText editText;
    private LinearLayout searchedUserLayout;
    private TextView nameText;
    private Button searchBtn;
    private ImageView avatar;
    private InputMethodManager inputMethodManager;
    private String toAddUsername;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        editText = (EditText) findViewById(R.id.edit_note);
        searchedUserLayout = (LinearLayout) findViewById(R.id.ll_user);
        nameText = (TextView) findViewById(R.id.name);
        searchBtn = (Button) findViewById(R.id.search);
        avatar = (ImageView) findViewById(R.id.avatar);
        findViewById(R.id.ll_phone_contact).setOnClickListener(this);
        findViewById(R.id.ll_weixin).setOnClickListener(this);
        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
    }


    /**
     * 查找contact
     *
     * @param v
     */
    public void searchContact(View v) {
        final String name = editText.getText().toString();
        String saveText = searchBtn.getText().toString();

        if (getString(R.string.button_search).equals(saveText)) {
            toAddUsername = name;
            if (TextUtils.isEmpty(name)) {
                startActivity(new Intent(this, AlertDialog.class).putExtra("msg", "请输入用户名"));
                return;
            }
            // TODO 从服务器获取此contact,如果不存在提示不存在此用户
            //服务器存在此用户，显示此用户和添加按钮
//			searchedUserLayout.setVisibility(View.VISIBLE);
//			nameText.setText(toAddUsername);
            DialogManager.getInstance().showProgressDialog(this);
            UserApi.seachContact(toAddUsername, new HttpCallBack<String>() {
                @Override
                public void doSucess(String result, String method) {
                    DialogManager.getInstance().dissMissProgressDialog();
                    CommonJson4List<PeachUser> seachResult = CommonJson4List.fromJson(result, PeachUser.class);
                    if (seachResult.code == 0) {
                        if (seachResult.result.size() > 0) {
                            PeachUser user = seachResult.result.get(0);
                            if (AccountManager.getInstance().getLoginAccount(mContext).userId==(seachResult.result.get(0).userId)) {
                                ToastUtil.getInstance(mContext).showToast("不能添加自己");
                                return;
                            }

                            if (AccountManager.getInstance().getContactList(mContext).containsKey(seachResult.result.get(0).easemobUser)) {
                                ToastUtil.getInstance(mContext).showToast("此用户已是你的好友");
                                IMUser imUser = AccountManager.getInstance().getContactList(mContext).get(user.easemobUser);
                                imUser.setNick(user.nickName);
                                imUser.setAvatar(user.avatar);
                                imUser.setSignature(user.signature);
                                imUser.setMemo(user.memo);
                                imUser.setGender(user.gender);
                                IMUtils.setUserHead(imUser);
                                AccountManager.getInstance().getContactList(mContext).put(imUser.getUsername(),imUser);
                                IMUserRepository.saveContact(mContext,imUser);
                                Intent intent = new Intent(mContext, ContactDetailActivity.class);
                                intent.putExtra("userId", user.userId);
                                startActivity(intent);
                                return;
                            }
                            Intent intent = new Intent(mContext, SeachContactDetailActivity.class);
                            intent.putExtra("isSeach",true);
                            intent.putExtra("user", seachResult.result.get(0));
                            startActivity(intent);
                        }


                    } else if (!TextUtils.isEmpty(seachResult.err.message)) {
                        ToastUtil.getInstance(mContext).showToast(seachResult.err.message);
                    }

                }

                @Override
                public void doFailure(Exception error, String msg, String method) {
                    DialogManager.getInstance().dissMissProgressDialog();

                }
            });

        }
    }

    public void back(View v) {
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_phone_contact:
                Intent phoneIntent = new Intent(mContext, AddPhoneContactActivity.class);
                startActivity(phoneIntent);
                break;
            case R.id.ll_weixin:
                break;
        }
    }
}
