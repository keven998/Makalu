package com.xuejian.client.lxp.module.goods;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.aizou.core.http.HttpCallBack;
import com.jakewharton.scalpel.ScalpelFrameLayout;
import com.xuejian.client.lxp.BuildConfig;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.PeachBaseActivity;
import com.xuejian.client.lxp.bean.OrderBean;
import com.xuejian.client.lxp.bean.PlanBean;
import com.xuejian.client.lxp.bean.TravellerBean;
import com.xuejian.client.lxp.common.api.H5Url;
import com.xuejian.client.lxp.common.api.TravelApi;
import com.xuejian.client.lxp.common.dialog.PeachMessageDialog;
import com.xuejian.client.lxp.common.gson.CommonJson;
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
    @InjectView(R.id.et_first_name)
    EditText etFirstName;
    @InjectView(R.id.et_tel)
    EditText etTel;
    @InjectView(R.id.et_last_name)
    EditText etLastName;
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
    @InjectView(R.id.tv_edit_user)
    TextView tvEditUser;
    @InjectView(R.id.tv_add_user)
    TextView tvAddUser;
    public static int SELECTED_DATE = 101;
    public static int SELECTED_USER = 102;
    public static int EDIT_USER_LIST = 103;
    private ArrayList<TravellerBean> passengerList = new ArrayList<>();
    CommonAdapter memberAdapter;
    ListView memberList;
    private int goodsNum = 1;
    String commodityId;
    ArrayList<PlanBean> planList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (BuildConfig.DEBUG){
            View view = getLayoutInflater().inflate(R.layout.activity_order,null);
            ScalpelFrameLayout sView  = new ScalpelFrameLayout(this);
            //sView.setLayerInteractionEnabled(true);
            sView.addView(view);
            setContentView(sView);
        }else {
            setContentView(R.layout.activity_order);
        }
        ButterKnife.inject(this);
        final ArrayList<PlanBean> data = getIntent().getParcelableArrayListExtra("planList");
        planList = data;
        commodityId = getIntent().getStringExtra("commodityId");
        tv_address_book.setOnClickListener(this);
        tvSubmitOrder.setOnClickListener(this);
        ctvAgreement.setOnClickListener(this);
        tvAddUser.setOnClickListener(this);
        tvEditUser.setOnClickListener(this);
        tvTitleBack.setOnClickListener(this);

        ListView packageList = (ListView) findViewById(R.id.lv_choose);
        packageList.setAdapter(new CommonAdapter(mContext, R.layout.item_package_info, true, data));
        setListViewHeightBasedOnChildren(packageList);


        memberList = (ListView) findViewById(R.id.lv_members);
        memberAdapter = new CommonAdapter(mContext, R.layout.item_member_info, false,null);
        memberList.setAdapter(memberAdapter);
        setListViewHeightBasedOnChildren(memberList);
        if (memberAdapter.getCount() > 0) {
            tvEditUser.setVisibility(View.VISIBLE);
            tvAddUser.setVisibility(View.GONE);
        } else {
            tvEditUser.setVisibility(View.GONE);
            tvAddUser.setVisibility(View.VISIBLE);
        }


        tvSelectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OrderCreateActivity.this, DatePickActivity.class);
                intent.putExtra("planList",data);
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

        NumberPicker numberPicker = (NumberPicker) findViewById(R.id.select_num);
        numberPicker.setListenr(new NumberPicker.OnButtonClick() {
            @Override
            public void OnValueChange(int value) {
                goodsNum = value;
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_address_book:
                Intent intent = new Intent(OrderCreateActivity.this, CommonUserInfoActivity.class);
                intent.putExtra("ListType", 1);
                intent.putExtra("multiple", false);
                startActivityForResult(intent, SELECTED_USER);
                break;
            case R.id.tv_submit_order:
                if (checkOrder()) return;
                submitOrder();
                break;
            case R.id.ctv_1:
                ctvAgreement.setChecked(!ctvAgreement.isChecked());
                break;
            case R.id.tv_add_user:
                Intent tv_add_user = new Intent(OrderCreateActivity.this, CommonUserInfoActivity.class);
                tv_add_user.putExtra("ListType", 1);
                startActivityForResult(tv_add_user, EDIT_USER_LIST);
                break;
            case R.id.tv_edit_user:
                Intent tv_edit_user = new Intent(OrderCreateActivity.this, CommonUserInfoActivity.class);
                tv_edit_user.putExtra("ListType", 1);
                startActivityForResult(tv_edit_user, EDIT_USER_LIST);
                break;
            case R.id.tv_title_back:
                notice();
                break;
        }
    }

    @Override
    public void onBackPressed() {
       // super.onBackPressed();
        notice();
    }
    public void submitOrder() {
        TravelApi.createOrder(Long.parseLong(commodityId), planList.get(0).getPlanId(), tvDate.getText().toString(), goodsNum, etTel.getText().toString(), "", etFirstName.getText().toString(), etLastName.getText().toString(), "",passengerList ,new HttpCallBack<String>() {
            @Override
            public void doSuccess(String result, String method) {
                CommonJson<OrderBean> bean = CommonJson.fromJson(result, OrderBean.class);
                Intent intent = new Intent(OrderCreateActivity.this, OrderDetailActivity.class);
                intent.putExtra("type","pendingOrder");
                intent.putExtra("order",bean.result);
                startActivity(intent);
                finish();
            }

            @Override
            public void doFailure(Exception error, String msg, String method) {

            }

            @Override
            public void doFailure(Exception error, String msg, String method, int code) {

            }
        });
    }

    private boolean checkOrder() {
        if (TextUtils.isEmpty(tvDate.getText().toString())) {
            Toast.makeText(mContext, "请选择出行日期", Toast.LENGTH_SHORT).show();
            return true;
        }
        if (TextUtils.isEmpty(etFirstName.getText().toString())) {
            Toast.makeText(mContext, "请填写旅客信息", Toast.LENGTH_SHORT).show();
            return true;
        }
        if (TextUtils.isEmpty(etLastName.getText().toString())) {
            Toast.makeText(mContext, "请填写旅客信息", Toast.LENGTH_SHORT).show();
            return true;
        }
        if (TextUtils.isEmpty(etTel.getText().toString())) {
            Toast.makeText(mContext, "请填写旅客信息", Toast.LENGTH_SHORT).show();
            return true;
        }
        if (!ctvAgreement.isChecked()) {
            Toast.makeText(mContext, "请确认《旅行派条款》", Toast.LENGTH_SHORT).show();
            return true;
        }
        if (goodsNum <= 0) {
            Toast.makeText(mContext, "请至少选择一件商品", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

    public class CommonAdapter extends BaseAdapter {

        private Context mContext;
        private int ResId;
        private int lastId;
        private ArrayList<PlanBean> packageList;
        public CommonAdapter(Context c, int ResId, boolean selected,ArrayList<PlanBean>list) {
            packageList = list;
            mContext = c;
            this.ResId = ResId;

        }

        @Override
        public int getCount() {
            if (ResId == R.layout.item_package_info) {
                return packageList.size();
            } else if (ResId == R.layout.item_member_info) {
                return passengerList.size();
            }
            return 0;
        }

        @Override
        public Object getItem(int position) {
            if (ResId == R.layout.item_package_info) {
                return packageList.get(position);
            } else if (ResId == R.layout.item_member_info) {
                return passengerList.get(position);
            }
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            ViewHolder1 viewHolder1;
            if (ResId == R.layout.item_package_info) {
                if (convertView == null) {
                    convertView = View.inflate(mContext, ResId, null);
                    viewHolder1 = new ViewHolder1();
                    viewHolder1.packageName = (TextView) convertView.findViewById(R.id.tv_package);
                    viewHolder1.packagePrice = (TextView) convertView.findViewById(R.id.tv_package_price);
                    viewHolder1.bg = (LinearLayout) convertView.findViewById(R.id.ll_bg);
                    convertView.setTag(viewHolder1);
                } else {
                    viewHolder1 = (ViewHolder1) convertView.getTag();
                }
                viewHolder1.bg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        lastId = position;
                        notifyDataSetChanged();
                    }
                });
                PlanBean bean= (PlanBean) getItem(position);
                viewHolder1.packageName.setText(bean.getTitle());
                viewHolder1.packagePrice.setText(String.format("¥%d起",bean.getPrice()));
                if (position == lastId) {
                    viewHolder1.bg.setBackgroundResource(R.drawable.icon_package_bg_selected);
                    //  viewHolder1.content.setPadding(10,0,0,0);
                } else {
                    viewHolder1.bg.setBackgroundResource(R.drawable.icon_package_bg_default);
                    //  viewHolder1.content.setPadding(10,0,0,0);
                }
            } else if (ResId == R.layout.item_member_info) {
                TravellerBean bean = (TravellerBean) getItem(position);
                if (convertView == null) {
                    convertView = View.inflate(mContext, ResId, null);

                    holder = new ViewHolder();
                    holder.content = (TextView) convertView.findViewById(R.id.tv_member);
                    convertView.setTag(holder);

                } else {
                    holder = (ViewHolder) convertView.getTag();
                }
                holder.content.setText(bean.getTraveller().getSurname() + " " + bean.getTraveller().getGivenName());
            }
            return convertView;
        }

        class ViewHolder {
            private TextView content;
        }

        class ViewHolder1 {
            private TextView packageName;
            private TextView packagePrice;
            private LinearLayout bg;
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
                tvDate.setText(date);
            } else if (requestCode == SELECTED_USER) {
                TravellerBean bean = data.getParcelableExtra("passenger");
                if (bean != null) {
                    etFirstName.setText(bean.getTraveller().getGivenName());
                    etLastName.setText(bean.getTraveller().getSurname());
                    etTel.setText(bean.getTraveller().getTel().getNumber()+"");
                }
            } else if (requestCode == EDIT_USER_LIST) {
                ArrayList<TravellerBean> list = data.getParcelableArrayListExtra("passenger");
                passengerList.clear();
                passengerList.addAll(list);
                memberAdapter.notifyDataSetChanged();
                setListViewHeightBasedOnChildren(memberList);
                if (memberAdapter.getCount() > 0) {
                    tvEditUser.setVisibility(View.VISIBLE);
                    tvAddUser.setVisibility(View.GONE);
                } else {
                    tvEditUser.setVisibility(View.GONE);
                    tvAddUser.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    private void notice() {
        final PeachMessageDialog dialog = new PeachMessageDialog(mContext);
        dialog.setTitle("提示");
        dialog.setMessage("离开页面将清除填写内容，确定离开吗？");
        dialog.setPositiveButton("离开", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                finish();
            }
        });
        dialog.setNegativeButton("取消", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();

    }
}