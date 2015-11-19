package com.xuejian.client.lxp.module.goods;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.PeachBaseActivity;
import com.xuejian.client.lxp.common.api.H5Url;
import com.xuejian.client.lxp.common.widget.NumberPicker;
import com.xuejian.client.lxp.module.PeachWebViewActivity;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by yibiao.qin on 2015/11/9.
 */
public class OrderCreateActivity extends PeachBaseActivity implements View.OnClickListener {

    @InjectView(R.id.tv_goods_name)
    TextView tvGoodsName;
    @InjectView(R.id.tv_date)
    TextView tvDate;
    @InjectView(R.id.tv_select_date)
    TextView tvSelectDate;
    @InjectView(R.id.select_num)
    NumberPicker selectNum;
    @InjectView(R.id.et_name)
    EditText etName;
    @InjectView(R.id.et_tel)
    EditText etTel;
    @InjectView(R.id.et_email)
    EditText etEmail;
    @InjectView(R.id.et_address)
    EditText etAddress;
    @InjectView(R.id.et_message)
    EditText etMessage;
    @InjectView(R.id.ctv_1)
    CheckedTextView ctvAgreement;
    @InjectView(R.id.tv_title_back)
    TextView tvTitleBack;
    @InjectView(R.id.strategy_title)
    TextView tvTitle;
    @InjectView(R.id.tv_address_book)
    TextView tv_address_book;
    @InjectView(R.id.tv_submit_order)
    TextView tvSubmitOrder;
    public static int SELECTED_DATE = 101;
    public static int SELECTED_USER = 102;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        ButterKnife.inject(this);
        tv_address_book.setOnClickListener(this);
        tvSubmitOrder.setOnClickListener(this);
        ctvAgreement.setOnClickListener(this);
        ListView packageList = (ListView) findViewById(R.id.lv_choose);
        packageList.setAdapter(new CommonAdapter(mContext, R.layout.item_package_info, true));
        setListViewHeightBasedOnChildren(packageList);

        ListView memberList = (ListView) findViewById(R.id.lv_members);
        memberList.setAdapter(new CommonAdapter(mContext, R.layout.item_member_info, false));
        View footView = View.inflate(this, R.layout.footer_add_member, null);
        memberList.addFooterView(footView);
        setListViewHeightBasedOnChildren(memberList);
        TextView addMember = (TextView) footView.findViewById(R.id.tv_show_all);
        tvSelectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OrderCreateActivity.this, DatePickActivity.class);
                startActivityForResult(intent, SELECTED_DATE);
            }
        });


        ctvAgreement.setChecked(true);
        SpannableString priceStr = new SpannableString("《旅行派条款》");
   //     priceStr.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.app_theme_color)), 0, priceStr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        priceStr.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Intent aboutIntent = new Intent(OrderCreateActivity.this, PeachWebViewActivity.class);
                aboutIntent.putExtra("url", H5Url.AGREEMENT);
                aboutIntent.putExtra("title", "注册协议");
                startActivity(aboutIntent);
            }
        }, 0, priceStr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        SpannableStringBuilder spb = new SpannableStringBuilder();
        spb.append("我已阅读并同意:").append(priceStr);
        ctvAgreement.setText(spb);
        ctvAgreement.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_address_book:
                Intent intent = new Intent(OrderCreateActivity.this, CommonUserInfoActivity.class);
                intent.putExtra("ListType", 1);
                startActivityForResult(intent, SELECTED_USER);
                break;
            case R.id.tv_submit_order:
                Intent intent1 = new Intent(OrderCreateActivity.this, OrderListActivity.class);
                startActivity(intent1);
                break;
            case R.id.ctv_1:
                ctvAgreement.setChecked(!ctvAgreement.isChecked());
                break;
        }
    }

    public class CommonAdapter extends BaseAdapter {

        private Context mContext;
        private int ResId;
        private ArrayList<Boolean> status;
        private int lastId;

        public CommonAdapter(Context c, int ResId, boolean selected) {
            mContext = c;
            this.ResId = ResId;
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
            ViewHolder holder;
            ViewHolder1 viewHolder1;
            if (ResId == R.layout.item_package_info) {

                if (convertView == null) {
                    convertView = View.inflate(mContext, ResId, null);

                    viewHolder1 = new ViewHolder1();
                    viewHolder1.content = (TextView) convertView.findViewById(R.id.tv_package);
                    convertView.setTag(viewHolder1);

                } else {
                    viewHolder1 = (ViewHolder1) convertView.getTag();
                }
                viewHolder1.content.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        status.set(lastId, false);
                        status.set(position, true);
                        lastId = position;
                        notifyDataSetChanged();
                    }
                });
                viewHolder1.content.setText("套餐A五星级酒店");
                if (position == lastId) {
                    viewHolder1.content.setBackgroundResource(R.drawable.icon_package_bg_selected);
                } else {
                    viewHolder1.content.setBackgroundResource(R.drawable.icon_package_bg_default);
                }
            } else if (ResId == R.layout.item_member_info) {

                if (convertView == null) {
                    convertView = View.inflate(mContext, ResId, null);

                    holder = new ViewHolder();
                    holder.content = (TextView) convertView.findViewById(R.id.tv_member);
                    convertView.setTag(holder);

                } else {
                    holder = (ViewHolder) convertView.getTag();
                }
                holder.content.setText("my angle");
            }
            return convertView;
        }

        class ViewHolder {
            private TextView content;
            private ImageView delete;
        }

        class ViewHolder1 {
            private TextView content;
        }
    }

    public void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0, len = listAdapter.getCount(); i < len; i++) { //listAdapter.getCount()返回数据项的数目
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0); //计算子项View 的宽高
            totalHeight += listItem.getMeasuredHeight(); //统计所有子项的总高度
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECTED_DATE) {
                String date = data.getStringExtra("date");
//                if (list.size()>0){
//                    StringBuilder sb = new StringBuilder();
//                    for (String s : list) {
//                        sb.append(s+"\n");
//                    }
                tvDate.setText(date);
                // Toast.makeText(mContext, date, Toast.LENGTH_SHORT).show();
            }else if (requestCode == SELECTED_USER){
                etName.setText("赵小琴");
                etTel.setText("13567683453");
                etEmail.setText("1234235365@gmail.com");
            }
        }
    }

}
