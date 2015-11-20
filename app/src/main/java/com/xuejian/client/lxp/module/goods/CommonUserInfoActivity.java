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

import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.PeachBaseActivity;
import com.xuejian.client.lxp.bean.PassengerBean;

import java.util.ArrayList;

/**
 * Created by yibiao.qin on 2015/11/10.
 */
public class CommonUserInfoActivity extends PeachBaseActivity {

    private int EDIT_INFO = 103;
    TextView tvBack;
    TextView tv_confirm;
    private ArrayList<PassengerBean> passengerList = new ArrayList<>();
    private ArrayList<PassengerBean> selectedPassengerList = new ArrayList<>();
    private PassengerBean passenger;
    UserAdapter userAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_user);
        int type = getIntent().getIntExtra("ListType", -1);
        boolean multiple = getIntent().getBooleanExtra("multiple",true);
        tvBack = (TextView) findViewById(R.id.tv_title_back);
        tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        for (int i = 0; i < 3; i++) {
            PassengerBean bean = new PassengerBean();
            bean.id = "53452454253";
            bean.tel = "1384656365564";
            bean.lastName = "Zhao";
            bean.firstName = "Xiaoqin" + i;
            passengerList.add(bean);
        }
        passenger = passengerList.get(0);
        tv_confirm = (TextView) findViewById(R.id.tv_confirm);
        initView(type,multiple);

    }

    private void initView(int type, final boolean multiple) {
        TextView title = (TextView) findViewById(R.id.title);
        if (type == 1) {
            title.setText(R.string.common_user_info);
            ListView memberList = (ListView) findViewById(R.id.lv_userInfo);
            userAdapter = new UserAdapter(mContext, multiple);
            memberList.setAdapter(userAdapter);
            View footView = View.inflate(this, R.layout.footer_add_member_grey_line, null);
            if (multiple){
                memberList.addFooterView(footView);
            }
            TextView addMember = (TextView) footView.findViewById(R.id.add_member);
            addMember.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(CommonUserInfoActivity.this, UserInfoEditActivity.class);
                    startActivityForResult(intent, EDIT_INFO);
                }
            });
            tv_confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (multiple){
                        Intent intent = new Intent();
                        intent.putExtra("passenger", selectedPassengerList);
                        setResult(RESULT_OK, intent);
                        finish();
                    }else {
                        Intent intent = new Intent();
                        intent.putExtra("passenger", passenger);
                        setResult(RESULT_OK, intent);
                        finish();
                    }

                }
            });
        } else if (type == 2) {
            title.setText(R.string.user_info);
            ListView memberList = (ListView) findViewById(R.id.lv_userInfo);
            memberList.setAdapter(new UserInfoAdapter());
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
            final PassengerBean bean = (PassengerBean) getItem(position);


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
            viewHolder1.username.setText(bean.firstName + " " + bean.lastName);
            viewHolder1.id.setText(String.format("护照 %s", bean.id));

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
            return 3;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
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
            viewHolder1.title.setText(String.format("旅客%d:", position + 1));
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
                PassengerBean bean = data.getParcelableExtra("passenger");
                passengerList.add(bean);
                userAdapter.notifyDataSetChanged();
            }
        }
    }
}
