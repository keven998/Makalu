package com.xuejian.client.lxp.module.goods;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.aizou.core.http.HttpCallBack;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.PeachBaseActivity;
import com.xuejian.client.lxp.bean.TravellerBean;
import com.xuejian.client.lxp.bean.TravellerEntity;
import com.xuejian.client.lxp.common.account.AccountManager;
import com.xuejian.client.lxp.common.api.TravelApi;
import com.xuejian.client.lxp.common.gson.CommonJson4List;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by yibiao.qin on 2015/11/10.
 */
public class CommonUserInfoActivity extends PeachBaseActivity {

    private int EDIT_INFO = 103;
    TextView tvBack;
    TextView tv_confirm;
    private ArrayList<TravellerBean> passengerList = new ArrayList<>();
    private ArrayList<TravellerBean> selectedPassengerList = new ArrayList<>();
    private TravellerBean passenger;
    UserAdapter userAdapter;
    UserInfoAdapter userInfoAdapter;
    //编辑添加
    private static final int EDIT_LIST = 1;
    //展示
    private static final int SHOW_LIST = 2;

    int type;

    private HashMap<String,String> idType = new HashMap<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_user);
        type = getIntent().getIntExtra("ListType", -1);
        boolean multiple = getIntent().getBooleanExtra("multiple", true);
        idType.put("chineseID","身份证");
        idType.put("passport","护照");
        tvBack = (TextView) findViewById(R.id.tv_title_back);
        tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
//        for (int i = 0; i < 3; i++) {
//            TravellerBean bean = new TravellerBean();
//            TravellerEntity traveller =new TravellerEntity();
//            IdentityBean identityBean = new IdentityBean();
//            identityBean.setNumber("53452454253");
//            identityBean.setIdType("chineseID");
//            traveller.setGivenName("Xiaoqin");
//            traveller.setSurname("Zhao");
//            TelBean tel = new TelBean();
//            tel.setDialCode(86);
//            tel.setNumber(1384656365564l);
//            traveller.setTel(tel);
//            ArrayList<IdentityBean> identityBeanArrayList = new ArrayList<>();
//            identityBeanArrayList.add(identityBean);
//            traveller.setIdentities(identityBeanArrayList);
//            bean.setTraveller(traveller);
//            passengerList.add(bean);
//        }
        tv_confirm = (TextView) findViewById(R.id.tv_confirm);
        initView(type, multiple);
        if (type==EDIT_INFO||type==SHOW_LIST){
            getData();
        }

    }

    public void getData() {
        long userId = AccountManager.getInstance().getLoginAccount(mContext).getUserId();
        TravelApi.getTravellers(userId, new HttpCallBack<String>() {
            @Override
            public void doSuccess(String result, String method) {
                CommonJson4List<TravellerBean> list = CommonJson4List.fromJson(result, TravellerBean.class);
                passengerList.addAll(list.result);
                if (passengerList.size() > 0) {
                    passenger = passengerList.get(0);
                    if (type==EDIT_LIST){
                        userAdapter.notifyDataSetChanged();
                    }else if (type==SHOW_LIST){
                        userInfoAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void doFailure(Exception error, String msg, String method) {

            }

            @Override
            public void doFailure(Exception error, String msg, String method, int code) {

            }
        });
    }


    private void initView(int type, final boolean multiple) {
        TextView title = (TextView) findViewById(R.id.title);
        if (type == EDIT_LIST) {
            title.setText(R.string.common_user_info);
            ListView memberList = (ListView) findViewById(R.id.lv_userInfo);
            userAdapter = new UserAdapter(mContext, multiple);
            memberList.setAdapter(userAdapter);
            View footView = View.inflate(this, R.layout.footer_add_member_grey_line, null);
            if (multiple) {
                memberList.addFooterView(footView);
            }
            TextView addMember = (TextView) footView.findViewById(R.id.add_member);
            addMember.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(CommonUserInfoActivity.this, UserInfoEditActivity.class);
                    intent.putExtra("type", "create");
                    startActivityForResult(intent, EDIT_INFO);
                }
            });
            tv_confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (multiple) {
                        Intent intent = new Intent();
                        intent.putExtra("passenger", selectedPassengerList);
                        setResult(RESULT_OK, intent);
                        finish();
                    } else {
                        Intent intent = new Intent();
                        if (passenger == null) {
                      //      intent.putExtra("passenger", passenger);
                            setResult(RESULT_CANCELED, intent);
                        } else {
                            intent.putExtra("passenger", passenger);
                            setResult(RESULT_OK, intent);
                        }
                        finish();
                    }

                }
            });
        } else if (type == SHOW_LIST) {
            tv_confirm.setVisibility(View.GONE);
            title.setText(R.string.user_info);
            ListView memberList = (ListView) findViewById(R.id.lv_userInfo);
            userInfoAdapter = new UserInfoAdapter();
            memberList.setAdapter(userInfoAdapter);
        }else if (type == 3) {
            tv_confirm.setVisibility(View.GONE);
            title.setText(R.string.user_info);
            ArrayList<TravellerEntity>list = getIntent().getParcelableArrayListExtra("passengerList");
            for (TravellerEntity entity : list) {
                passengerList.add(new TravellerBean(entity));
            }
            ListView memberList = (ListView) findViewById(R.id.lv_userInfo);
            userInfoAdapter = new UserInfoAdapter();
            memberList.setAdapter(userInfoAdapter);
        }
    }

    public class UserAdapter extends BaseAdapter {

        private Context mContext;
        private int lastId;
        private boolean multiple;

        public UserAdapter(Context c, boolean multiple) {
            mContext = c;
            this.multiple = multiple;
        }

        @Override
        public int getCount() {
            return passengerList.size();
        }

        @Override
        public Object getItem(int position) {
            return passengerList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder1 viewHolder1;

            if (convertView == null) {
                convertView = View.inflate(mContext, R.layout.item_common_userinfo, null);
                viewHolder1 = new ViewHolder1();
                viewHolder1.username = (CheckedTextView) convertView.findViewById(R.id.ctv_username);
                viewHolder1.id = (TextView) convertView.findViewById(R.id.tv_id);
                viewHolder1.edit = (ImageView) convertView.findViewById(R.id.iv_edit);
                convertView.setTag(viewHolder1);

            } else {
                viewHolder1 = (ViewHolder1) convertView.getTag();
            }
            final TravellerBean bean = (TravellerBean) getItem(position);


            viewHolder1.username.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (multiple) {
                        viewHolder1.username.setChecked(!viewHolder1.username.isChecked());
                        if (viewHolder1.username.isChecked()) {
                            selectedPassengerList.add(bean);
                        } else {
                            selectedPassengerList.remove(bean);
                        }
                    } else {
                        lastId = position;
                        passenger = bean;
                        notifyDataSetChanged();
                    }
                }
            });
            if (multiple) {
                viewHolder1.edit.setVisibility(View.VISIBLE);
            } else {
                viewHolder1.edit.setVisibility(View.INVISIBLE);
            }
            viewHolder1.username.setText(bean.getTraveller().getSurname() + " " + bean.getTraveller().getGivenName());
            viewHolder1.id.setText(String.format("护照 %s", bean.getTraveller().getIdentities().get(0).getNumber()));
            viewHolder1.edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.putExtra("type", "edit");
                    intent.putExtra("passenger", bean);
                    intent.setClass(CommonUserInfoActivity.this, UserInfoEditActivity.class);
                    startActivity(intent);
                }
            });
            if (!multiple) {
                viewHolder1.username.setChecked(position == lastId);
            }

            return convertView;
        }

        class ViewHolder1 {
            private CheckedTextView username;
            private TextView id;
            private ImageView edit;
        }
    }

    public class UserInfoAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return passengerList.size();
        }

        @Override
        public Object getItem(int position) {
            return passengerList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder1 viewHolder1;

            if (convertView == null) {
                convertView = View.inflate(mContext, R.layout.item_order_users, null);

                viewHolder1 = new ViewHolder1();
                viewHolder1.username = (TextView) convertView.findViewById(R.id.tv_name);
                viewHolder1.id = (TextView) convertView.findViewById(R.id.tv_id);
                viewHolder1.tel = (TextView) convertView.findViewById(R.id.tv_tel);
                viewHolder1.title = (TextView) convertView.findViewById(R.id.tv_num);
                convertView.setTag(viewHolder1);

            } else {
                viewHolder1 = (ViewHolder1) convertView.getTag();
            }
            TravellerBean bean = (TravellerBean) getItem(position);
            viewHolder1.title.setText(String.format("旅客%d:", position + 1));
            viewHolder1.username.setText(bean.getTraveller().getSurname()+bean.getTraveller().getGivenName());
            viewHolder1.tel.setText(bean.getTraveller().getTel().getDialCode() + "-" + bean.getTraveller().getTel().getNumber());
            if (bean.getTraveller().getIdentities().size()>0){
                viewHolder1.id.setText(idType.get(bean.getTraveller().getIdentities().get(0).getIdType())+" "+bean.getTraveller().getIdentities().get(0).getNumber());
            }
            return convertView;
        }

        class ViewHolder1 {
            private TextView username;
            private TextView tel;
            private TextView id;
            private TextView title;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == EDIT_INFO) {
                TravellerBean bean = data.getParcelableExtra("passenger");
                passengerList.add(bean);
                userAdapter.notifyDataSetChanged();
            }
        }
    }
}
