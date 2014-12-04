package com.aizou.peachtravel.module.toolbox.im;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.aizou.core.dialog.DialogManager;
import com.aizou.core.widget.listHelper.ListViewDataAdapter;
import com.aizou.core.widget.listHelper.ViewHolderBase;
import com.aizou.core.widget.listHelper.ViewHolderCreator;
import com.aizou.peachtravel.R;
import com.aizou.peachtravel.base.ChatBaseActivity;
import com.aizou.peachtravel.bean.AddressBookbean;
import com.aizou.peachtravel.common.utils.PhoneContactUtils;
import com.aizou.peachtravel.common.utils.SearchEngine;
import com.aizou.peachtravel.common.widget.TitleHeaderBar;

import java.util.ArrayList;

/**
 * Created by Rjm on 2014/10/28.
 */
public class AddPhoneContactActivity extends ChatBaseActivity {

    private ListView mListView;
    ArrayList<AddressBookbean> contactListInMobile ;
    ListViewDataAdapter<AddressBookbean> contactAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_phone_contact);
        mListView = (ListView) findViewById(R.id.lv_phone_contact);
        initTitleBar();

        initData();
    }
    private void initTitleBar(){
        final TitleHeaderBar titleHeaderBar = (TitleHeaderBar) findViewById(R.id.ly_header_bar_title_wrap);
//        titleHeaderBar.setRightViewImageRes(R.drawable.add);

        titleHeaderBar.getTitleTextView().setText("添加通讯录好友");
        titleHeaderBar.enableBackKey(true);
    }

    private void initData(){
        DialogManager.getInstance().showProgressDialog(this);
        // 初始化搜索引擎
        SearchEngine.prepare(this, new Runnable() {
            public void run() {
//                afterPrepare();
                contactListInMobile= PhoneContactUtils.getPhoneContact(mContext);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        contactAdapter = new ListViewDataAdapter<AddressBookbean>(new ViewHolderCreator<AddressBookbean>() {
                            @Override
                            public ViewHolderBase<AddressBookbean> createViewHolder() {
                                return new PhoneContactViewHolder();
                            }
                        });
                        mListView.setAdapter(contactAdapter);
                        contactAdapter.getDataList().addAll(contactListInMobile);
                        contactAdapter.notifyDataSetChanged();
                        DialogManager.getInstance().dissMissProgressDialog();
                    }
                });
            }
        });
    }

    private void uploadAddressBook(){

//        UserApi.searchByAddressBook()


    }


    public class PhoneContactViewHolder extends ViewHolderBase<AddressBookbean>{

        private TextView name,phone;
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
        public void showData(int position, AddressBookbean itemData) {
            name.setText(itemData.name);
            phone.setText(itemData.tel.get(0));
        }
    }
}
