package com.xuejian.client.lxp.module.goods;

import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.PeachBaseActivity;
import com.xuejian.client.lxp.bean.ContactBean;
import com.xuejian.client.lxp.bean.CouponBean;
import com.xuejian.client.lxp.bean.OrderBean;
import com.xuejian.client.lxp.bean.PlanBean;
import com.xuejian.client.lxp.bean.PriceBean;
import com.xuejian.client.lxp.bean.SimpleCommodityBean;
import com.xuejian.client.lxp.bean.TelBean;
import com.xuejian.client.lxp.bean.TravellerBean;
import com.xuejian.client.lxp.common.api.H5Url;
import com.xuejian.client.lxp.common.dialog.PeachMessageDialog;
import com.xuejian.client.lxp.common.utils.CommonUtils;
import com.xuejian.client.lxp.common.widget.NumberPicker;
import com.xuejian.client.lxp.module.PeachWebViewActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by yibiao.qin on 2015/11/9.
 */
public class OrderCreateActivity extends PeachBaseActivity implements View.OnClickListener {

    @Bind(R.id.tv_goods_name)
    TextView tvGoodsName;
    @Bind(R.id.tv_date)
    TextView tvDate;
    @Bind(R.id.tv_select_date)
    TextView tvSelectDate;
    @Bind(R.id.select_num)
    NumberPicker selectNum;
    @Bind(R.id.et_first_name)
    EditText etFirstName;
    @Bind(R.id.et_tel)
    EditText etTel;
    @Bind(R.id.et_last_name)
    EditText etLastName;
    @Bind(R.id.et_message)
    EditText etMessage;
    @Bind(R.id.ctv_1)
    CheckedTextView ctvAgreement;
    @Bind(R.id.tv_title_back)
    TextView tvTitleBack;
    @Bind(R.id.strategy_title)
    TextView tvTitle;
    @Bind(R.id.tv_address_book)
    TextView tv_address_book;
    @Bind(R.id.tv_submit_order)
    TextView tvSubmitOrder;
    @Bind(R.id.tv_edit_user)
    TextView tvEditUser;
    @Bind(R.id.tv_add_user)
    TextView tvAddUser;
    @Bind(R.id.tv_total_price)
    TextView tvTotalPrice;
    @Bind(R.id.tv_dialCode)
    TextView tvDialCode;
    @Bind(R.id.tv_unit_price)
    TextView tvUnitPrice;
    @Bind(R.id.iv_arrow)
    ImageView arrow;
    @Bind(R.id.ll_action_bar)
    LinearLayout ll_action_bar;
    @Bind(R.id.rl_coupon)
    RelativeLayout rl_coupon;
    @Bind(R.id.tv_coupon_price)
    TextView tv_coupon_price;
    public final static int SELECTED_DATE = 101;
    public final static int SELECTED_USER = 102;
    public final static int EDIT_USER_LIST = 103;
    public final static int SELECTED_CODE = 104;
    public final static int SUBMIT_ORDER_CODE = 105;
    public final static int SELECT_COUPON = 106;
    private ArrayList<TravellerBean> passengerList = new ArrayList<>();
    CommonAdapter memberAdapter;
    ListView memberList;
    private int goodsNum = 1;
    private String commodityId;
    private PlanBean currentPlanBean;
    private PriceBean priceBean;
    private int currenrDialCode = 86;
    private String name;
    private boolean isShow;
    AlertDialog priceInfoDialog;
    private CouponBean currentCoupon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        ButterKnife.bind(this);
        final ArrayList<PlanBean> data = getIntent().getParcelableArrayListExtra("planList");
        currentPlanBean = data.get(0);
        commodityId = getIntent().getStringExtra("commodityId");
        name = getIntent().getStringExtra("name");
        tvGoodsName.setText(name);
        tv_address_book.setOnClickListener(this);
        tvSubmitOrder.setOnClickListener(this);
        ctvAgreement.setOnClickListener(this);
        tvAddUser.setOnClickListener(this);
        tvEditUser.setOnClickListener(this);
        tvTitleBack.setOnClickListener(this);
        tvDialCode.setOnClickListener(this);
        rl_coupon.setOnClickListener(this);
        ListView packageList = (ListView) findViewById(R.id.lv_choose);
        CommonAdapter commonAdapter = new CommonAdapter(mContext, R.layout.item_package_info, true, data);
        packageList.setAdapter(commonAdapter);
        setListViewHeightBasedOnChildren(packageList);
        commonAdapter.setOnSelectedListener(new OnSelectedListener() {
            @Override
            public void OnSelected(int pos) {
                currentPlanBean = data.get(pos);

                if (!TextUtils.isEmpty(tvDate.getText().toString())) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    try {
                        Date date = sdf.parse(tvDate.getText().toString());
                        priceBean = SampleDecorator.getPrice(currentPlanBean, date);
                        tvTotalPrice.setText(String.format("¥%s", CommonUtils.getPriceString(priceBean.getPrice() * goodsNum)));
                        tvUnitPrice.setText(String.format("¥%s", CommonUtils.getPriceString(priceBean.getPrice())));

                    } catch (ParseException e) {
                        e.printStackTrace();
                        tvDate.setText("");
                        priceBean = null;
                    }
                }
            }
        });

        memberList = (ListView) findViewById(R.id.lv_members);
        memberAdapter = new CommonAdapter(mContext, R.layout.item_member_info, false, null);
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
                intent.putExtra("planList", currentPlanBean);
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
                aboutIntent.putExtra("title", "旅行派条款");
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
                if (priceBean != null) {
                    tvTotalPrice.setText(String.format("¥%s", CommonUtils.getPriceString(priceBean.getPrice() * value)));
                    tvUnitPrice.setText(String.format("¥%s", CommonUtils.getPriceString(priceBean.getPrice())));
                }
            }
        });

        arrow.setVisibility(View.INVISIBLE);
        tvTotalPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentCoupon != null) {
                    arrow.setVisibility(View.VISIBLE);
                    if (isShow) {
                        hideVoucher();
                    } else {
                        showVoucher();
                    }
                }
            }
        });
    }

    public void showVoucher() {

        View view = View.inflate(this, R.layout.price_detail, null);
        TextView tvpackage = (TextView) view.findViewById(R.id.tv_package);
        TextView tvprice = (TextView) view.findViewById(R.id.tv_package_price);
        TextView coupon = (TextView) view.findViewById(R.id.tv_coupon_price);
        if (currentCoupon != null && priceBean != null && currentPlanBean != null) {
            tvpackage.setText(currentPlanBean.getTitle());
            tvprice.setText(String.format("¥%s*%d", CommonUtils.getPriceString(priceBean.price), selectNum.getCurrentValue()));
            coupon.setText(String.format("-¥%s", CommonUtils.getPriceString(currentCoupon.getDiscount())));
        }
        PopupWindow popupWindow = new PopupWindow(view);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setHeight(CommonUtils.getScreenHeight(this) - CommonUtils.dip2px(this, 62f));
        popupWindow.setWidth(CommonUtils.getScreenWidth(this));
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                hideVoucher();
            }
        });
        int[] location = new int[2];
        ll_action_bar.getLocationOnScreen(location);
        popupWindow.setAnimationStyle(R.style.PopAnimation1);
        popupWindow.showAtLocation(ll_action_bar, Gravity.NO_GRAVITY, location[0], location[1] - popupWindow.getHeight());
        ObjectAnimator animator = ObjectAnimator.ofFloat(arrow, "rotation", 0f, 180f).setDuration(300);
        animator.start();
    }

    public void hideVoucher() {
        ObjectAnimator animator = ObjectAnimator.ofFloat(arrow, "rotation", 180f, 360f).setDuration(300);
        animator.start();
        if (priceInfoDialog != null) {
            priceInfoDialog.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        passengerList = null;
        memberAdapter = null;
        memberList = null;
        commodityId = null;
        currentPlanBean = null;
        priceBean = null;
        name = null;
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
                MobclickAgent.onEvent(OrderCreateActivity.this, "event_confirmOrder");
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
                tv_edit_user.putExtra("selected", passengerList);
                startActivityForResult(tv_edit_user, EDIT_USER_LIST);
                break;
            case R.id.tv_title_back:
                notice();
                break;
            case R.id.tv_dialCode:
                Intent tv_dialCode = new Intent(OrderCreateActivity.this, CountryPickActivity.class);
                startActivityForResult(tv_dialCode, SELECTED_CODE);
                break;
            case R.id.rl_coupon:
                if (priceBean == null) {
                    Toast.makeText(mContext, "请先选择套餐", Toast.LENGTH_LONG).show();
                } else {
                    Intent rl_coupon = new Intent(OrderCreateActivity.this, CouponListActivity.class);
                    rl_coupon.putExtra("createOrder", true);
                    rl_coupon.putExtra("price", priceBean.getPrice() * (double) selectNum.getCurrentValue());
                    if (currentCoupon != null) rl_coupon.putExtra("id", currentCoupon.getId());
                    startActivityForResult(rl_coupon, SELECT_COUPON);
                }

                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        // super.onBackPressed();
        notice();
    }

    public void submitOrder() {

        String sDt = tvDate.getText().toString();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date dt2 = null;
        try {
            dt2 = sdf.parse(sDt);
        } catch (ParseException e) {
            e.printStackTrace();
            tvDate.setText("");
            Toast.makeText(mContext, "请选择出行日期", Toast.LENGTH_SHORT).show();
            return;
        }
        String tel = etTel.getText().toString();
        long telNumber = 0;
        if (TextUtils.isDigitsOnly(tel)) {
            telNumber = Long.parseLong(tel);
        } else {
            Toast.makeText(mContext, "请输入正确的电话号码", Toast.LENGTH_LONG).show();
            return;
        }

        OrderBean orderBean = new OrderBean();
        orderBean.setComment(etMessage.getText().toString());
        orderBean.setPlanId(currentPlanBean.getPlanId());
        SimpleCommodityBean simpleCommodityBean = new SimpleCommodityBean();
        simpleCommodityBean.setCommodityId(Long.parseLong(commodityId));
        ArrayList<PlanBean> planBeans = new ArrayList<>();
        planBeans.add(currentPlanBean);
        simpleCommodityBean.setPlans(planBeans);
        simpleCommodityBean.setTitle(name);
        orderBean.setCommodity(simpleCommodityBean);
        orderBean.setRendezvousTime(new SimpleDateFormat("yyyy-MM-dd").format(dt2));
        orderBean.setQuantity(goodsNum);
        orderBean.setTotalPrice(priceBean.price * goodsNum);
//        ArrayList<TravellerEntity> list = new ArrayList<>();
//        for (TravellerBean bean : passengerList) {
//            list.add(bean.getTraveller());
//        }
//        orderBean.setTravellers(list);
        orderBean.couponBean = currentCoupon;
        ContactBean contactBean = new ContactBean();
        TelBean telBean = new TelBean();
        telBean.setDialCode(currenrDialCode);
        telBean.setNumber(telNumber);
        contactBean.setTel(telBean);
        contactBean.setGivenName(etFirstName.getText().toString());
        contactBean.setSurname(etLastName.getText().toString());
        orderBean.setContact(contactBean);
        orderBean.setStatus("pending");

        Intent intent = new Intent(OrderCreateActivity.this, OrderConfirmActivity.class);
        intent.putExtra("type", "pendingOrder");
        intent.putExtra("order", orderBean);
        intent.putExtra("passengerList", passengerList);
        startActivityForResult(intent, SUBMIT_ORDER_CODE);


//        TravelApi.createOrder(Long.parseLong(commodityId), currentPlanBean.getPlanId(), dt2.getTime(), goodsNum, currenrDialCode
//                , telNumber, "", etLastName.getText().toString(), etFirstName.getText().toString(),
//                etMessage.getText().toString(), passengerList, new HttpCallBack<String>() {
//                    @Override
//                    public void doSuccess(String result, String method) {
//                        CommonJson<OrderBean> bean = CommonJson.fromJson(result, OrderBean.class);
//                        Intent intent = new Intent(OrderCreateActivity.this, OrderConfirmActivity.class);
//                        intent.putExtra("type", "pendingOrder");
//                        intent.putExtra("order", bean.result);
//                        intent.putExtra("orderId", bean.result.getOrderId());
//                        startActivity(intent);
//                        finish();
//                    }
//
//                    @Override
//                    public void doFailure(Exception error, String msg, String method) {
//                        Toast.makeText(mContext, "订单创建失败", Toast.LENGTH_LONG).show();
//                    }
//
//                    @Override
//                    public void doFailure(Exception error, String msg, String method, int code) {
//
//                    }
//                });
    }


    private boolean checkOrder() {
        if (TextUtils.isEmpty(tvDate.getText().toString())) {
            Toast.makeText(mContext, "请选择出行日期", Toast.LENGTH_SHORT).show();
            return true;
        }
        if (passengerList == null || passengerList.size() == 0) {
            Toast.makeText(mContext, "请填写旅客信息", Toast.LENGTH_SHORT).show();
            return true;
        }
        if (TextUtils.isEmpty(etFirstName.getText().toString())) {
            Toast.makeText(mContext, "请填写联系人信息", Toast.LENGTH_SHORT).show();
            return true;
        }
        if (TextUtils.isEmpty(etLastName.getText().toString())) {
            Toast.makeText(mContext, "请填写联系人信息", Toast.LENGTH_SHORT).show();
            return true;
        }
        if (TextUtils.isEmpty(etTel.getText().toString())) {
            Toast.makeText(mContext, "请填写联系人信息", Toast.LENGTH_SHORT).show();
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
        OnSelectedListener listener;
        private ArrayList<PlanBean> packageList;

        public CommonAdapter(Context c, int ResId, boolean selected, ArrayList<PlanBean> list) {
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

        public void setOnSelectedListener(OnSelectedListener listener) {
            this.listener = listener;
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
                        if (listener != null && lastId != position) {
                            listener.OnSelected(position);
                        }
                        lastId = position;
                        notifyDataSetChanged();

                    }
                });
                PlanBean bean = (PlanBean) getItem(position);
                viewHolder1.packageName.setText(bean.getTitle());
                viewHolder1.packagePrice.setText(String.format("¥%s起", CommonUtils.getPriceString(bean.getPrice())));
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

    private interface OnSelectedListener {
        void OnSelected(int pos);

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
                PriceBean bean = data.getParcelableExtra("date_price");
                tvDate.setText(bean.date);
                tvTotalPrice.setText(String.format("¥%s", CommonUtils.getPriceString(bean.getPrice() * selectNum.getCurrentValue())));
                tvUnitPrice.setText(String.format("¥%s", CommonUtils.getPriceString(bean.getPrice())));
                priceBean = bean;

            } else if (requestCode == SELECTED_USER) {
                TravellerBean bean = data.getParcelableExtra("passenger");
                if (bean != null) {
                    etFirstName.setText(bean.getTraveller().getGivenName());
                    etLastName.setText(bean.getTraveller().getSurname());
                    etTel.setText(String.valueOf(bean.getTraveller().getTel().getNumber()));
                    currenrDialCode = bean.getTraveller().getTel().getDialCode();
                    tvDialCode.setText("+" + currenrDialCode);
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
            } else if (requestCode == SELECTED_CODE) {
                currenrDialCode = data.getIntExtra("dialCode", 0);
                tvDialCode.setText("+" + currenrDialCode);
            } else if (requestCode == SUBMIT_ORDER_CODE) {
                finish();
            } else if (requestCode == SELECT_COUPON) {
                arrow.setVisibility(View.VISIBLE);
                currentCoupon = data.getParcelableExtra("coupon");
                tv_coupon_price.setText("-" + currentCoupon.getDiscount());
                if (priceBean.getPrice() * (double) selectNum.getCurrentValue() - (double) currentCoupon.getDiscount() <= 0) {
                    tvTotalPrice.setText("¥0");
                } else {
                    tvTotalPrice.setText(String.format("¥%s", CommonUtils.getPriceString(priceBean.getPrice() * (double) selectNum.getCurrentValue() - (double) currentCoupon.getDiscount())));
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
