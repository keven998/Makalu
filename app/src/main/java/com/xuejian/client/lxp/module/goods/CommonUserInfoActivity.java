package com.xuejian.client.lxp.module.goods;

import android.content.Context;
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

import java.util.ArrayList;

/**
 * Created by yibiao.qin on 2015/11/10.
 */
public class CommonUserInfoActivity extends PeachBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_user);
        ListView memberList = (ListView) findViewById(R.id.lv_userInfo);
        memberList.setAdapter(new UserAdapter(mContext, true));
        View footView = View.inflate(this, R.layout.footer_add_member_grey_line, null);
        memberList.addFooterView(footView);
    }

    public class UserAdapter extends BaseAdapter {

        private Context mContext;
        private ArrayList<Boolean> status;
        private int lastId;

        public UserAdapter(Context c, boolean selected) {
            mContext = c;
            if (selected) {
                status = new ArrayList<>();
                status.add(true);
                status.add(false);
                status.add(false);
            }

        }

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
                convertView = View.inflate(mContext, R.layout.item_common_userinfo, null);

                viewHolder1 = new ViewHolder1();
                viewHolder1.username = (CheckedTextView) convertView.findViewById(R.id.ctv_username);
                viewHolder1.id = (TextView) convertView.findViewById(R.id.tv_id);
                viewHolder1.edit = (ImageView) convertView.findViewById(R.id.iv_edit);
                convertView.setTag(viewHolder1);

            } else {
                viewHolder1 = (ViewHolder1) convertView.getTag();
            }
            viewHolder1.username.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    status.set(lastId, false);
                    status.set(position, true);
                    lastId = position;
                    notifyDataSetChanged();
                }
            });
            viewHolder1.username.setText("赵小琴zhaoxiaoqin");
            viewHolder1.username.setChecked(position == lastId);
            return convertView;
        }

        class ViewHolder1 {
            private CheckedTextView username;
            private TextView id;
            private ImageView edit;
        }
    }


}
