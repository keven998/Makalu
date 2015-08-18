package com.xuejian.client.lxp.module.toolbox.im;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.aizou.core.dialog.ToastUtil;
import com.aizou.core.http.HttpCallBack;
import com.aizou.core.widget.listHelper.ListViewDataAdapter;
import com.aizou.core.widget.listHelper.ViewHolderBase;
import com.aizou.core.widget.listHelper.ViewHolderCreator;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.ChatBaseActivity;
import com.xuejian.client.lxp.bean.AddressBookbean;
import com.xuejian.client.lxp.common.account.AccountManager;
import com.xuejian.client.lxp.common.api.UserApi;
import com.xuejian.client.lxp.common.dialog.DialogManager;
import com.xuejian.client.lxp.common.gson.CommonJson;
import com.xuejian.client.lxp.common.gson.CommonJson4List;
import com.xuejian.client.lxp.common.utils.PhoneContactUtils;
import com.xuejian.client.lxp.common.widget.TitleHeaderBar;
import com.xuejian.client.lxp.db.User;
import com.xuejian.client.lxp.module.toolbox.HisMainPageActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AddPhoneContactActivity extends ChatBaseActivity {

    private ListView mListView;
    List<AddressBookbean> contactListInMobile;
    ListViewDataAdapter<AddressBookbean> contactAdapter;
    private EditText edit_note;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_phone_contact);
        mListView = (ListView) findViewById(R.id.lv_phone_contact);
        edit_note = (EditText)this.findViewById(R.id.edit_note);
        findViewById(R.id.search_contact).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String requestStr = edit_note.getText().toString();
                refreshData(requestStr);
            }
        });
        edit_note.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
//        edit_note.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
//                if(actionId == EditorInfo.IME_ACTION_DONE){
//                    InputMethodManager imm = (InputMethodManager)textView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
//                    if(imm.isActive()){
//                        imm.hideSoftInputFromInputMethod(textView.getApplicationWindowToken(),0);
//                    }
//                    final String requestStr = edit_note.getText().toString();
//                    refreshData(requestStr);
//                }
//
//                return false;
//            }
//        });

        initTitleBar();
        initData();
    }

    private void initTitleBar() {
        final TitleHeaderBar titleHeaderBar = (TitleHeaderBar) findViewById(R.id.ly_header_bar_title_wrap);
//        titleHeaderBar.setRightViewImageRes(R.drawable.add);

        titleHeaderBar.getTitleTextView().setText("添加通讯录朋友");
        titleHeaderBar.enableBackKey(true);
    }

    private void refreshData(String keyword){
        try {
            DialogManager.getInstance().showLoadingDialog(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(contactListInMobile==null || contactListInMobile.size()==0){
            contactListInMobile = PhoneContactUtils.getPhoneContact(mContext);
        }
        final ArrayList<AddressBookbean> keyWordResult= PhoneContactUtils.getPhoneContactByKeyWord(mContext,contactListInMobile,keyword);
        if (keyWordResult.size()==0){
            DialogManager.getInstance().dissMissLoadingDialog();
            ToastUtil.getInstance(AddPhoneContactActivity.this).showToast("没有找到匹配的联系人");
            return;
        }
        UserApi.searchByAddressBook(keyWordResult, new HttpCallBack<String>(){
            @Override
            public void doSuccess(String result, String method) {
                DialogManager.getInstance().dissMissLoadingDialog();
                CommonJson4List<AddressBookbean> contactResult = CommonJson4List.fromJson(result, AddressBookbean.class);
                if (contactResult.code == 0) {
                    int size = keyWordResult.size();
                    for (int i = 0; i < size; i++) {
                        AddressBookbean bookbean = keyWordResult.get(i);
                        AddressBookbean bookResult = contactResult.result.get(i);
                        bookbean.isUser = bookResult.isUser;
                        bookbean.isContact = bookResult.isContact;
                        bookbean.userId = bookResult.userId;
                    }
//                    keyWordResult = contactResult.result;
                    contactAdapter = new ListViewDataAdapter<AddressBookbean>(new ViewHolderCreator<AddressBookbean>() {
                        @Override
                        public ViewHolderBase<AddressBookbean> createViewHolder() {
                            return new PhoneContactViewHolder();
                        }
                    });
                    Collections.sort(keyWordResult, new Comparator<AddressBookbean>() {
                        @Override
                        public int compare(AddressBookbean lhs, AddressBookbean rhs) {
                            if (lhs == null || rhs == null) {
                                return 0;
                            } else {
                                return rhs.getSort() - lhs.getSort();
                            }
                        }
                    });
                    mListView.setAdapter(contactAdapter);
                    contactAdapter.getDataList().addAll(keyWordResult);
                    contactAdapter.notifyDataSetChanged();
                }

            }

            @Override
            public void doFailure(Exception error, String msg, String method) {
                DialogManager.getInstance().dissMissLoadingDialog();
                ToastUtil.getInstance(AddPhoneContactActivity.this).showToast(getResources().getString(R.string.request_network_failed));
            }

            @Override
            public void doFailure(Exception error, String msg, String method, int code) {

            }
        });
    }
    private void initData() {
        try {
            DialogManager.getInstance().showLoadingDialog(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        contactListInMobile = PhoneContactUtils.getPhoneContact(mContext);
        UserApi.searchByAddressBook(contactListInMobile, new HttpCallBack<String>() {
            @Override
            public void doSuccess(String result, String method) {
                DialogManager.getInstance().dissMissLoadingDialog();
                CommonJson4List<AddressBookbean> contactResult = CommonJson4List.fromJson(result, AddressBookbean.class);
                if (contactResult.code == 0) {
                    int size = contactListInMobile.size();
                    for (int i = 0; i < size; i++) {
                        AddressBookbean bookbean = contactListInMobile.get(i);
                        AddressBookbean bookResult = contactResult.result.get(i);
                        bookbean.isUser = bookResult.isUser;
                        bookbean.isContact = bookResult.isContact;
                        bookbean.userId = bookResult.userId;
                    }
//                    contactListInMobile = contactResult.result;
                    contactAdapter = new ListViewDataAdapter<AddressBookbean>(new ViewHolderCreator<AddressBookbean>() {
                        @Override
                        public ViewHolderBase<AddressBookbean> createViewHolder() {
                            return new PhoneContactViewHolder();
                        }
                    });
                    Collections.sort(contactListInMobile, new Comparator<AddressBookbean>() {
                        @Override
                        public int compare(AddressBookbean lhs, AddressBookbean rhs) {
                            if (lhs == null || rhs == null) {
                                return 0;
                            } else {
                                return rhs.getSort() - lhs.getSort();
                            }
                        }
                    });
                    mListView.setAdapter(contactAdapter);
                    contactAdapter.getDataList().addAll(contactListInMobile);
                    contactAdapter.notifyDataSetChanged();
                }

            }

            @Override
            public void doFailure(Exception error, String msg, String method) {
                DialogManager.getInstance().dissMissLoadingDialog();
                ToastUtil.getInstance(AddPhoneContactActivity.this).showToast(getResources().getString(R.string.request_network_failed));
            }

            @Override
            public void doFailure(Exception error, String msg, String method, int code) {

            }
        });

//        // 初始化搜索引擎
//        SearchEngine.prepare(this, new Runnable() {
//            public void run() {
////                afterPrepare();
//                contactListInMobile= PhoneContactUtils.getPhoneContact(mContext);
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        contactAdapter = new ListViewDataAdapter<AddressBookbean>(new ViewHolderCreator<AddressBookbean>() {
//                            @Override
//                            public ViewHolderBase<AddressBookbean> createViewHolder() {
//                                return new PhoneContactViewHolder();
//                            }
//                        });
//                        mListView.setAdapter(contactAdapter);
//                        contactAdapter.getDataList().addAll(contactListInMobile);
//                        contactAdapter.notifyDataSetChanged();
//                        DialogManager.getInstance().dissMissLoadingDialog();
//                    }
//                });
//            }
//        });
    }

    private void uploadAddressBook() {
//        UserApi.searchByAddressBook()

    }


    public class PhoneContactViewHolder extends ViewHolderBase<AddressBookbean> {

        private TextView name, phone;
        private TextView actionButton;

        @Override
        public View createView(LayoutInflater layoutInflater) {
            View contentView = layoutInflater.inflate(R.layout.row_add_phone_contact, null);
            name = (TextView) contentView.findViewById(R.id.tv_name);
            phone = (TextView) contentView.findViewById(R.id.tv_contact);
            actionButton = (TextView) contentView.findViewById(R.id.btn_add);
            return contentView;
        }

        @Override
        public void showData(int position, final AddressBookbean itemData) {
            name.setText(itemData.name);
            phone.setText(itemData.tel);
            if (itemData.isContact) {
                actionButton.setText("");
                actionButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_gray_complete, 0);
                actionButton.setOnClickListener(null);
            } else if (itemData.isUser) {
                actionButton.setText("");
                actionButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_add_phone_contact, 0);
                actionButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (itemData.userId == AccountManager.getInstance().getLoginAccount(mContext).getUserId()) {
                            ToastUtil.getInstance(mContext).showToast("那是自己");
                            return;
                        }
                        try {
                            DialogManager.getInstance().showLoadingDialog(AddPhoneContactActivity.this);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        UserApi.getUserInfo(String.valueOf(itemData.userId), new HttpCallBack<String>() {
                            @Override
                            public void doSuccess(String result, String method) {
                                DialogManager.getInstance().dissMissLoadingDialog();
                                CommonJson<User> userResult = CommonJson.fromJson(result, User.class);
                                if (userResult.code == 0) {
                                    Intent intent = new Intent(AddPhoneContactActivity.this, HisMainPageActivity.class);
                                    intent.putExtra("userId", userResult.result.getUserId());
                                    startActivity(intent);
                                } else {

                                }
                            }

                            @Override
                            public void doFailure(Exception error, String msg, String method) {
                                DialogManager.getInstance().dissMissLoadingDialog();
                                if (!isFinishing())
                                    ToastUtil.getInstance(mContext).showToast("获取用户信息失败");
                            }

                            @Override
                            public void doFailure(Exception error, String msg, String method, int code) {

                            }
                        });
                    }
                });
            } else {
                actionButton.setText("邀请");
                actionButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_arrow_right, 0);
                actionButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_SENDTO);
                        //需要发短息的号码
                        intent.setData(Uri.parse("smsto:" + itemData.tel));
                        intent.putExtra("sms_body", String.format("我正在用旅行派，可以和达人互动的旅行应用。搜索：%s 加我", AccountManager.getInstance().getLoginAccount(AddPhoneContactActivity.this).getNickName()));
                        startActivity(intent);
                    }
                });
            }
        }
    }
}
