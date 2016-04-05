package com.xuejian.client.lxp.module.customization;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.PeachBaseActivity;
import com.xuejian.client.lxp.bean.BountiesBean;
import com.xuejian.client.lxp.bean.ContactBean;
import com.xuejian.client.lxp.bean.LocBean;
import com.xuejian.client.lxp.bean.TelBean;
import com.xuejian.client.lxp.bean.TravellerBean;
import com.xuejian.client.lxp.common.widget.ListViewForScrollView;
import com.xuejian.client.lxp.common.widget.NumberPicker;
import com.xuejian.client.lxp.module.goods.CommonUserInfoActivity;
import com.xuejian.client.lxp.module.goods.CountryPickActivity;
import com.xuejian.client.lxp.module.goods.DatePickActivity;
import com.xuejian.client.lxp.module.my.SelectResidentActivity;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by yibiao.qin on 2016/3/30.
 */
public class ProjectCreateActivity extends PeachBaseActivity {

    @Bind(R.id.tv_address_book)
    TextView tvAddressBook;
    @Bind(R.id.et_last_name)
    EditText etLastName;
    @Bind(R.id.et_first_name)
    EditText etFirstName;
    @Bind(R.id.tv_dialCode)
    TextView tvDialCode;
    @Bind(R.id.et_tel)
    EditText etTel;
    @Bind(R.id.tv_setOff_city)
    TextView tvSetOffCity;
    @Bind(R.id.date_title)
    TextView dateTitle;
    @Bind(R.id.tv_date)
    TextView tvDate;
    @Bind(R.id.tv_select_date)
    TextView tvSelectDate;
    @Bind(R.id.select_day_num)
    NumberPicker selectDayNum;
    @Bind(R.id.select_traveller_num)
    NumberPicker selectTravellerNum;
    @Bind(R.id.unit_price)
    TextView unitPrice;
    @Bind(R.id.tv_total_price)
    EditText tvTotalPrice;
    @Bind(R.id.tv_target_city)
    TextView tvTargetCity;
    @Bind(R.id.tv_service)
    TextView tvService;
    @Bind(R.id.tv_theme)
    TextView tvTheme;
    @Bind(R.id.et_message)
    EditText etMessage;
    @Bind(R.id.tv_submit_order)
    TextView tvSubmitOrder;
    @Bind(R.id.ll_action_bar)
    LinearLayout llActionBar;
    @Bind(R.id.tv_title_back)
    TextView tvTitleBack;
    @Bind(R.id.top_title)
    TextView topTitle;
    @Bind(R.id.ctv_child)
    CheckedTextView ctv_child;
    @Bind(R.id.ctv_elder)
    CheckedTextView ctv_elder;


    public final static int RESIDENT = 1;
    public final static int SELECTED_DATE = 101;
    public final static int SELECTED_USER = 102;
    public final static int SELECTED_CODE = 104;
    public final static int SELECTED_TARGET = 105;
    @Bind(R.id.rl_setoff)
    RelativeLayout rlSetoff;
    @Bind(R.id.rl_target)
    RelativeLayout rlTarget;
    @Bind(R.id.rl_service)
    RelativeLayout rlService;
    @Bind(R.id.rl_theme)
    RelativeLayout rlTheme;
    private int currentDialCode = 86;
    private String currentCityId;
    private ArrayList<LocBean> selectedCity = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_project);
        ButterKnife.bind(this);
        bindView();
    }

    private void bindView() {
        tvTitleBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tvDialCode.setText("+" + currentDialCode);
        tvDialCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent tv_dialCode = new Intent(ProjectCreateActivity.this, CountryPickActivity.class);
                startActivityForResult(tv_dialCode, SELECTED_CODE);
            }
        });
        tvAddressBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProjectCreateActivity.this, CommonUserInfoActivity.class);
                intent.putExtra("ListType", 1);
                intent.putExtra("multiple", false);
                startActivityForResult(intent, SELECTED_USER);
            }
        });
        tvSelectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProjectCreateActivity.this, DatePickActivity.class);
                intent.putExtra("justDate", true);
                startActivityForResult(intent, SELECTED_DATE);
            }
        });

        rlService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseCloseReason(2);
            }
        });
        rlTheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseCloseReason(1);
            }
        });

        tvSubmitOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createProject();
            }
        });
        ctv_child.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ctv_child.setChecked(!ctv_child.isChecked());
            }
        });
        ctv_elder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ctv_elder.setChecked(!ctv_elder.isChecked());
            }
        });

        rlSetoff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent residentIntent = new Intent(mContext, SelectResidentActivity.class);
                startActivityForResult(residentIntent, RESIDENT);
            }
        });

        rlTarget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent residentIntent = new Intent(mContext, DestMenuActivity.class);
                startActivityForResult(residentIntent, SELECTED_TARGET);
            }
        });
    }

    private void createProject() {

        if (checkOrder()) return;
        BountiesBean bean = new BountiesBean();
        ContactBean contactBean = new ContactBean();
        TelBean telBean = new TelBean();
        telBean.setDialCode(currentDialCode);
        telBean.setNumber(Long.parseLong(etTel.getText().toString().trim()));
        contactBean.setTel(telBean);
        contactBean.setGivenName(etFirstName.getText().toString());
        contactBean.setSurname(etLastName.getText().toString());
        ArrayList<ContactBean> contactBeans = new ArrayList<>();
        contactBeans.add(contactBean);
        bean.setContact(contactBeans);
        bean.setDepartureDate(tvDate.getText().toString().trim());
        bean.setTimeCost(selectDayNum.getCurrentValue());
        bean.setParticipantCnt(selectTravellerNum.getCurrentValue());
        bean.setBudget(Double.parseDouble(tvTotalPrice.getText().toString().trim()));
        bean.setService(tvService.getText().toString().trim());
        bean.setTopic(tvTheme.getText().toString().trim());
        bean.setMemo(etMessage.getText().toString().trim());

        ArrayList<String> participants = new ArrayList<>();
        if (ctv_child.isChecked()) participants.add("children");
        if (ctv_elder.isChecked()) participants.add("oldman");
        bean.setParticipants(participants);

        ArrayList<LocBean> setOffCities = new ArrayList<>();
        LocBean locBean = new LocBean();
        locBean.zhName = tvSetOffCity.getText().toString();
        locBean.id = currentCityId;
        setOffCities.add(locBean);
        bean.setDeparture(setOffCities);
        bean.setDestination(selectedCity);

        Intent intent = new Intent();
        intent.putExtra("BountiesBean", bean);
        intent.setClass(this, ProjectConfirmActivity.class);
        startActivity(intent);
    }


    private boolean checkOrder() {

        if (TextUtils.isEmpty(etFirstName.getText()) || TextUtils.isEmpty(etLastName.getText())) {
            Toast.makeText(mContext, "请填写联系人信息", Toast.LENGTH_SHORT).show();
            return true;
        }
        if (TextUtils.isEmpty(etTel.getText())) {
            Toast.makeText(mContext, "请填写联系人信息", Toast.LENGTH_SHORT).show();
            return true;
        }

        try {
            Long.parseLong(etTel.getText().toString().trim());
        } catch (Exception e) {
            Toast.makeText(mContext, "请填写正确的电话号码", Toast.LENGTH_SHORT).show();
        }

        if (TextUtils.isEmpty(tvDate.getText().toString())) {
            Toast.makeText(mContext, "请选择出发日期", Toast.LENGTH_SHORT).show();
            return true;
        }
        if (TextUtils.isEmpty(tvSetOffCity.getText().toString().trim())) {
            Toast.makeText(mContext, "请选择出发城市", Toast.LENGTH_SHORT).show();
            return true;
        }
        if (selectDayNum.getCurrentValue() <= 0 || selectTravellerNum.getCurrentValue() <= 0) {
            Toast.makeText(mContext, "请填写出行天数与人数", Toast.LENGTH_SHORT).show();
            return true;
        }
        if (TextUtils.isEmpty(tvTotalPrice.getText().toString().trim())) {
            Toast.makeText(mContext, "请填写总预算金额", Toast.LENGTH_SHORT).show();
            return true;
        }
        try {
            Double.parseDouble(tvTotalPrice.getText().toString().trim());
        } catch (Exception e) {
            Toast.makeText(mContext, "请填写正确的预算金额", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(tvTargetCity.getText().toString())) {
            Toast.makeText(mContext, "请选择旅行城市", Toast.LENGTH_SHORT).show();
            return true;
        }
        if (TextUtils.isEmpty(tvService.getText().toString())) {
            Toast.makeText(mContext, "请选择服务项目", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECTED_USER) {
                TravellerBean bean = data.getParcelableExtra("passenger");
                if (bean != null) {
                    etFirstName.setText(bean.getTraveller().getGivenName());
                    etLastName.setText(bean.getTraveller().getSurname());
                    etTel.setText(String.valueOf(bean.getTraveller().getTel().getNumber()));
                    currentDialCode = bean.getTraveller().getTel().getDialCode();
                    tvDialCode.setText("+" + currentDialCode);
                }
            } else if (requestCode == SELECTED_CODE) {
                currentDialCode = data.getIntExtra("dialCode", 0);
                tvDialCode.setText("+" + currentDialCode);
            } else if (requestCode == SELECTED_DATE) {
                String s = data.getStringExtra("date");
                tvDate.setText(s);
            } else if (requestCode == RESIDENT) {
                String s = data.getStringExtra("result");
                currentCityId = data.getStringExtra("resultId");
                tvSetOffCity.setText(s);
            } else if (requestCode == SELECTED_TARGET) {
                selectedCity.clear();
                ArrayList<LocBean> locBeans = data.getParcelableArrayListExtra("selected");
                selectedCity.addAll(locBeans);
                StringBuilder stringBuilder = new StringBuilder();
                for (int i = 0; i < selectedCity.size(); i++) {
                    if (i != 0) stringBuilder.append("、");
                    stringBuilder.append(selectedCity.get(i).zhName);
                }
                tvTargetCity.setText(stringBuilder);
            }

        }
    }

    private void chooseCloseReason(final int type) {
        final AlertDialog dialog = new AlertDialog.Builder(this).create();
        View contentView = View.inflate(this, R.layout.dialog_project_select, null);
        final ListViewForScrollView listView = (ListViewForScrollView) contentView.findViewById(R.id.lv);
        TextView title = (TextView) contentView.findViewById(R.id.tv_title);
        final String[] theme = new String[]{"蜜月度假", "家庭亲子", "美食购物", "人文探索", "户外体验"};
        final String[] service = new String[]{"机票酒店", "美食门票", "导游接机", "行程设计", "全套服务"};

        final ArrayList<String> themeList = new ArrayList<String>(Arrays.asList(theme));
        final ArrayList<String> serviceList = new ArrayList<String>(Arrays.asList(service));
        final ThemeAdapter adapter;
        if (type == 1) {
            title.setText("主题偏向");
            adapter = new ThemeAdapter(themeList);
        } else {
            title.setText("服务包含");
            adapter = new ThemeAdapter(serviceList);
        }
        listView.setAdapter(adapter);
        contentView.findViewById(R.id.iv_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (type == 1) {
                    tvTheme.setText(getSelectedString(themeList, adapter.getSelected()));
                } else {
                    tvService.setText(getSelectedString(serviceList, adapter.getSelected()));
                }
                dialog.dismiss();
            }
        });
        dialog.show();
        WindowManager windowManager = getWindowManager();
        Window window = dialog.getWindow();
        window.setContentView(contentView);
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = display.getWidth(); // 设置宽度
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
        window.setGravity(Gravity.CENTER); // 此处可以设置dialog显示的位置
        window.setWindowAnimations(R.style.SelectPicDialog); // 添加动画
    }

    private String getSelectedString(ArrayList<String> list, ArrayList<Integer> selected) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Integer integer : selected) {
            if (!TextUtils.isEmpty(stringBuilder)) stringBuilder.append("、");
            stringBuilder.append(list.get(integer));
        }
        return stringBuilder.toString();
    }

    public class ThemeAdapter extends BaseAdapter {

        ArrayList<String> list;
        ArrayList<Integer> selected;

        public ThemeAdapter(ArrayList<String> list) {
            this.list = list;
            selected = new ArrayList<>();
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public String getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public boolean isSelected(Integer pos) {
            return selected.contains(pos);
        }

        public void setSelected(Integer pos) {
            if (!selected.contains(pos)) {
                selected.add(pos);
            } else {
                selected.remove(pos);
            }
        }

        public ArrayList<Integer> getSelected() {
            return selected;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_project_theme, null);
            TextView textView = (TextView) convertView.findViewById(R.id.tv_content);
            final CheckedTextView checkedTextView = (CheckedTextView) convertView.findViewById(R.id.ctv);
            textView.setText(getItem(position));
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setSelected(position);
                    checkedTextView.setChecked(isSelected(position));
                }
            });


            return convertView;
        }
    }
}
