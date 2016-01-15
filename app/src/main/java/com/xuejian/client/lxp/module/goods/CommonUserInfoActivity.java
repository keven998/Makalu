package com.xuejian.client.lxp.module.goods;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
    private int EDIT_USER = 106;
    TextView tvBack;
    TextView tv_confirm;
    TextView tv_add;
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

    private HashMap<String, String> idType = new HashMap<>();
    ListView memberList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_user);

//        Uri uri = getIntent().getData();
//        System.out.println(uri.getHost());
//        System.out.println(uri.getPath());
//        System.out.println(uri.getScheme());
//        System.out.println(uri.getQueryParameter("id"));

        type = getIntent().getIntExtra("ListType", -1);
        boolean multiple = getIntent().getBooleanExtra("multiple", true);
        idType.put("chineseID", "身份证");
        idType.put("passport", "护照");
        idType.put("HMPermit", "港澳通行证");
        idType.put("TWPermit", "台湾通行证");
        memberList = (ListView) findViewById(R.id.lv_userInfo);
        tvBack = (TextView) findViewById(R.id.tv_title_back);
        tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tv_confirm = (TextView) findViewById(R.id.tv_confirm);
        tv_add = (TextView) findViewById(R.id.tv_add);
        initView(type, multiple);
        if (type == EDIT_LIST || type == SHOW_LIST) {
            getData();
        }

    }

    public void getData() {
        long userId = AccountManager.getInstance().getLoginAccount(mContext).getUserId();
        TravelApi.getTravellers(userId, new HttpCallBack<String>() {
            @Override
            public void doSuccess(String result, String method) {
                CommonJson4List<TravellerBean> list = CommonJson4List.fromJson(result, TravellerBean.class);
                passengerList.clear();
                passengerList.addAll(list.result);
                if (passengerList.size() > 0) {
                    passenger = passengerList.get(0);
                }
                if (type == EDIT_LIST) {
                    userAdapter.notifyDataSetChanged();
                } else if (type == SHOW_LIST) {
                    userInfoAdapter.notifyDataSetChanged();
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
        selectedPassengerList.clear();
        TextView title = (TextView) findViewById(R.id.title);
        if (type == EDIT_LIST) {
            title.setText(R.string.common_user_info);
            userAdapter = new UserAdapter(mContext, multiple);
            View footView = View.inflate(this, R.layout.footer_add_member_grey_line, null);
            if (multiple) {
                memberList.addFooterView(footView);
            }
            memberList.setAdapter(userAdapter);
            ArrayList<TravellerBean> list = getIntent().getParcelableArrayListExtra("selected");
            if (list != null) {
                selectedPassengerList.addAll(list);
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
            memberList.setDividerHeight(0);
            memberList.setDivider(null);
            tv_confirm.setVisibility(View.GONE);
            tv_add.setVisibility(View.VISIBLE);
            tv_add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(CommonUserInfoActivity.this, UserInfoEditActivity.class);
                    intent.putExtra("type", "create");
                    startActivityForResult(intent, EDIT_INFO);
                }
            });
            title.setText(R.string.user_info);
            userInfoAdapter = new UserInfoAdapter();
            memberList.setAdapter(userInfoAdapter);
        } else if (type == 3) {
            memberList.setDividerHeight(0);
            memberList.setDivider(null);
            tv_confirm.setVisibility(View.GONE);
            title.setText(R.string.user_info);
            ArrayList<TravellerEntity> list = getIntent().getParcelableArrayListExtra("passengerList");
            for (TravellerEntity entity : list) {
                passengerList.add(new TravellerBean(entity));
            }
            userInfoAdapter = new UserInfoAdapter();
            memberList.setAdapter(userInfoAdapter);
        }
    }

    public class UserAdapter extends BaseAdapter {

        private Context mContext;
        private int lastId = -1;
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
                viewHolder1.tel = (TextView) convertView.findViewById(R.id.tv_tel);
                viewHolder1.name = (TextView) convertView.findViewById(R.id.tv_name);
                viewHolder1.edit = (ImageView) convertView.findViewById(R.id.iv_edit);
                viewHolder1.llContainer = (LinearLayout) convertView.findViewById(R.id.ll_container);
                convertView.setTag(viewHolder1);

            } else {
                viewHolder1 = (ViewHolder1) convertView.getTag();
            }
            final TravellerBean bean = (TravellerBean) getItem(position);

            if (multiple) {
                viewHolder1.username.setChecked(false);
                for (TravellerBean travellerBean : selectedPassengerList) {
                    if (bean.getKey().equals(travellerBean.getKey())) {
                        viewHolder1.username.setChecked(true);
                        break;
                    }
                }
            }

            viewHolder1.llContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (multiple) {
                        viewHolder1.username.setChecked(!viewHolder1.username.isChecked());
                        if (viewHolder1.username.isChecked()) {
                            // selectedPassengerList.add(bean);
                            add(bean);
                        } else {
                            remove(bean);
                            //  selectedPassengerList.remove(bean);
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
            viewHolder1.name.setText(bean.getTraveller().getSurname() + " " + bean.getTraveller().getGivenName());
            viewHolder1.tel.setText(bean.getTraveller().getTel().getDialCode() + "-" + bean.getTraveller().getTel().getNumber());
            if (bean.getTraveller().getIdentities().size() > 0) {
                viewHolder1.id.setText(idType.get(bean.getTraveller().getIdentities().get(0).getIdType()) + " " + bean.getTraveller().getIdentities().get(0).getNumber());
            }

            viewHolder1.edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.putExtra("type", "edit");
                    intent.putExtra("passenger", bean);
                    intent.setClass(CommonUserInfoActivity.this, UserInfoEditActivity.class);
                    startActivityForResult(intent, EDIT_USER);
                }
            });
            if (!multiple) {
                viewHolder1.username.setChecked(position == lastId);
            }

            return convertView;
        }

        public void remove(TravellerBean bean) {
            ArrayList<TravellerBean> del = new ArrayList<>();
            for (TravellerBean travellerBean : selectedPassengerList) {
                if (travellerBean.getKey().equals(bean.getKey())) {
                    del.add(travellerBean);
                    break;
                }
            }
            selectedPassengerList.removeAll(del);
            del = null;
        }

        public void add(TravellerBean bean) {
            boolean flag = false;
            for (TravellerBean travellerBean : selectedPassengerList) {
                if (travellerBean.getKey().equals(bean.getKey())) {
                    flag = true;
                    break;
                }
            }
            if (!flag) selectedPassengerList.add(bean);
        }

        class ViewHolder1 {
            private CheckedTextView username;
            private TextView id;
            private TextView name;
            private TextView tel;
            private ImageView edit;
            private LinearLayout llContainer;
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
            viewHolder1.username.setText(bean.getTraveller().getSurname() + bean.getTraveller().getGivenName());
            viewHolder1.tel.setText(bean.getTraveller().getTel().getDialCode() + "-" + bean.getTraveller().getTel().getNumber());
            if (bean.getTraveller().getIdentities().size() > 0) {
                viewHolder1.id.setText(idType.get(bean.getTraveller().getIdentities().get(0).getIdType()) + " " + bean.getTraveller().getIdentities().get(0).getNumber());
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
                if (bean != null) passengerList.add(bean);
                if (type == SHOW_LIST) {
                    userInfoAdapter.notifyDataSetChanged();
                } else {
                    userAdapter.notifyDataSetChanged();
                }

            } else if (requestCode == EDIT_USER) {
                TravellerBean bean = data.getParcelableExtra("passenger");
                if (bean != null) {
                    for (int i = 0; i < passengerList.size(); i++) {
                        if (passengerList.get(i).getKey().equals(bean.getKey())) {
                            passengerList.set(i, bean);
                        }
                    }
                }
                getData();
            }
        }
    }
}
