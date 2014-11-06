package com.aizou.peachtravel.module.travel.im;

import android.os.Bundle;
import android.text.TextUtils;
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
import com.aizou.peachtravel.base.BaseChatActivity;
import com.aizou.peachtravel.bean.PhoneContactBean;
import com.aizou.peachtravel.common.utils.PhoneContactUtils;
import com.aizou.peachtravel.common.utils.SearchEngine;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Rjm on 2014/10/28.
 */
public class AddPhoneContactActivity extends BaseChatActivity {

    private ListView mListView;
    ArrayList<PhoneContactBean> contactListInMobile ;
    ListViewDataAdapter<PhoneContactBean> contactAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_phone_contact);
        mListView = (ListView) findViewById(R.id.lv_phone_contact);

        initData();
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
                        contactAdapter = new ListViewDataAdapter<PhoneContactBean>(new ViewHolderCreator<PhoneContactBean>() {
                            @Override
                            public ViewHolderBase<PhoneContactBean> createViewHolder() {
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




    public class PhoneContactViewHolder extends ViewHolderBase<PhoneContactBean>{

        private TextView name,phone;
        private Button actionButton;

        @Override
        public View createView(LayoutInflater layoutInflater) {
            View contentView = layoutInflater.inflate(R.layout.row_add_phone_contact, null);
            name = (TextView) contentView.findViewById(R.id.tv_name);
            phone = (TextView) contentView.findViewById(R.id.tv_contact);
            actionButton = (Button) contentView.findViewById(R.id.btn_add);
            return contentView;
        }

        @Override
        public void showData(int position, PhoneContactBean itemData) {
            name.setText(itemData.name);
            phone.setText(itemData.phone);
        }
    }
}
