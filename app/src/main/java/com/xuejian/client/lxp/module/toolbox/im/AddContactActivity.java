/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.xuejian.client.lxp.module.toolbox.im;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aizou.core.dialog.ToastUtil;
import com.aizou.core.http.HttpCallBack;
import com.aizou.core.utils.LocalDisplay;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.umeng.analytics.MobclickAgent;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.ChatBaseActivity;
import com.xuejian.client.lxp.common.account.AccountManager;
import com.xuejian.client.lxp.common.api.UserApi;
import com.xuejian.client.lxp.common.dialog.DialogManager;
import com.xuejian.client.lxp.common.gson.CommonJson4List;
import com.xuejian.client.lxp.common.utils.CommonUtils;
import com.xuejian.client.lxp.common.utils.ShareUtils;
import com.xuejian.client.lxp.common.widget.TitleHeaderBar;
import com.xuejian.client.lxp.db.User;
import com.xuejian.client.lxp.module.toolbox.HisMainPageActivity;

import java.util.List;

public class AddContactActivity extends ChatBaseActivity implements View.OnClickListener {
    private EditText editText;
    private TextView nameText;
    private String toAddUsername;
    private ProgressDialog progressDialog;
    private ListView listView;
    private DisplayImageOptions picOptions;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);
        picOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true).bitmapConfig(Bitmap.Config.RGB_565)
                .resetViewBeforeLoading(true)
                .showImageOnFail(R.drawable.ic_home_more_avatar_unknown_round)
                .showImageForEmptyUri(R.drawable.ic_home_more_avatar_unknown_round)
                .displayer(new RoundedBitmapDisplayer(LocalDisplay.dp2px(20)))
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT).build();
        initTitleBar();
        listView = (ListView) findViewById(R.id.listView);
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
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(s))listView.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        findViewById(R.id.tv_phone_contact).setOnClickListener(this);
        findViewById(R.id.tv_weixin_contacts).setOnClickListener(this);

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
        MobclickAgent.onPageStart("page_add_friend");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("page_add_friend");
        MobclickAgent.onPause(this);
    }

    private void initTitleBar() {
        final TitleHeaderBar titleHeaderBar = (TitleHeaderBar) findViewById(R.id.ly_header_bar_title_wrap);


        titleHeaderBar.getTitleTextView().setText("添加朋友");
        titleHeaderBar.findViewById(R.id.ly_title_bar_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * 查找contact
     */
    public void searchContact() {
        final String name = editText.getText().toString().trim();
        toAddUsername = name;
        if (TextUtils.isEmpty(name) || name.length() == 0) {
            ToastUtil.getInstance(mContext).showToast("查无此用户");
            return;
        }
        try {
            DialogManager.getInstance().showLoadingDialog(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        UserApi.seachContact(toAddUsername, new HttpCallBack<String>() {
            @Override
            public void doSuccess(String result, String method) {
                DialogManager.getInstance().dissMissLoadingDialog();
                CommonJson4List<User> seachResult = CommonJson4List.fromJson(result, User.class);

                if (seachResult.code == 0) {
                    if (seachResult.result.size() > 0) {

                        listView.setVisibility(View.VISIBLE);
                        listView.setAdapter(new Adapter(seachResult.result));

//                        if (AccountManager.getInstance().getContactList(mContext).containsKey(seachResult.result.get(0).getUserId())) {
//                            ToastUtil.getInstance(mContext).showToast("该用户已是你的朋友");
//                            Intent intent = new Intent(mContext, HisMainPageActivity.class);
//                            intent.putExtra("userId", user.getUserId());
//                            startActivity(intent);
//                            return;
//                        }
//                        Intent intent = new Intent(mContext, HisMainPageActivity.class);
//                        intent.putExtra("userId", user.getUserId());
//                        startActivity(intent);
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

            @Override
            public void doFailure(Exception error, String msg, String method, int code) {

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
                if (CommonUtils.checkOp(AddContactActivity.this, 4) == 1) {
                    Toast.makeText(AddContactActivity.this, "通讯录权限被禁止，请打开权限后重试", Toast.LENGTH_SHORT).show();
                } else {
                    MobclickAgent.onEvent(AddContactActivity.this, "cell_item_add_lxp_friends_from_contacts");
                    Intent phoneIntent = new Intent(mContext, AddPhoneContactActivity.class);
                    startActivity(phoneIntent);
                }

                break;

            case R.id.tv_weixin_contacts:
                MobclickAgent.onEvent(AddContactActivity.this, "cell_item_add_lxp_friends_from_weichat");
                ShareUtils.shareAppToWx(this, String.format("我正在用旅行派，搜索: %s 加我", AccountManager.getInstance().getLoginAccount(this).getNickName()));
                break;
        }
    }

    public class Adapter extends BaseAdapter {

        List<User> list;

        public Adapter(List<User> list) {
            this.list = list;

        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder1 vh;
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_contact,null);
                vh = new ViewHolder1();
                vh.avatarView = (ImageView) convertView.findViewById(R.id.avatar);
                vh.nickView = (TextView) convertView.findViewById(R.id.name);
                vh.sectionHeader = (TextView) convertView.findViewById(R.id.header);
                vh.unreadMsgView = (TextView) convertView.findViewById(R.id.non_accept_number);
                vh.dividerView = convertView.findViewById(R.id.vw_divider);
                vh.content = (RelativeLayout)convertView. findViewById(R.id.content);
                convertView.setTag(vh);
            } else {
                vh = (ViewHolder1) convertView.getTag();
            }
            vh.dividerView.setVisibility(View.GONE);
            vh.unreadMsgView.setVisibility(View.INVISIBLE);
            final User user = (User) getItem(position);
            vh.nickView.setText(user.getNickName());
            ImageLoader.getInstance().displayImage(user.getAvatar(), vh.avatarView, picOptions);

            vh.content.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, HisMainPageActivity.class);
                    intent.putExtra("userId", user.getUserId());
                    startActivity(intent);
                }
            });

            return convertView;
        }
    }

    class ViewHolder1 {
        public TextView sectionHeader;
        public ImageView avatarView;
        public View dividerView;
        public TextView nickView;
        public RelativeLayout content;
        public TextView unreadMsgView;
    }
}
