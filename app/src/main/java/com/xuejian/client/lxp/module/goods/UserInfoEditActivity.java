package com.xuejian.client.lxp.module.goods;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.aizou.core.dialog.ToastUtil;
import com.aizou.core.http.HttpCallBack;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.PeachBaseActivity;
import com.xuejian.client.lxp.bean.IdentityBean;
import com.xuejian.client.lxp.bean.TelBean;
import com.xuejian.client.lxp.bean.TravellerBean;
import com.xuejian.client.lxp.bean.TravellerEntity;
import com.xuejian.client.lxp.common.account.AccountManager;
import com.xuejian.client.lxp.common.api.TravelApi;
import com.xuejian.client.lxp.common.gson.CommonJson;
import com.xuejian.client.lxp.module.dest.adapter.StringSpinnerAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by yibiao.qin on 2015/11/10.
 */
public class UserInfoEditActivity extends PeachBaseActivity implements View.OnClickListener {

    @InjectView(R.id.tv_title_back)
    TextView tvTitleBack;
    @InjectView(R.id.tv_confirm)
    TextView tvConfirm;
    @InjectView(R.id.et_last_name)
    EditText etLastName;
    @InjectView(R.id.et_first_name)
    EditText etFirstName;
    @InjectView(R.id.tv_birthday)
    TextView tvBirthday;
    @InjectView(R.id.iv_select_birthday)
    ImageView ivSelectBirthday;
    @InjectView(R.id.et_tel)
    EditText etTel;
    @InjectView(R.id.et_id)
    EditText etId;
    @InjectView(R.id.type_spinner)
    Spinner spinner;
    String idType ="passport";
    String type = "";
    final String[] idTypeArray = new String[]{"passport", "chineseID"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_edit);
        ButterKnife.inject(this);
        type = getIntent().getStringExtra("type");
        tvTitleBack.setOnClickListener(this);
        tvConfirm.setOnClickListener(this);
        ivSelectBirthday.setOnClickListener(this);
        String[] mItems = new String[]{"护照", "身份证"};

        StringSpinnerAdapter mTypeListAdapter = new StringSpinnerAdapter(mContext, Arrays.asList(mItems));
        spinner.setAdapter(mTypeListAdapter);
        spinner.setSelection(0, true);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                idType = idTypeArray[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        if ("edit".equals(type)){
            TravellerBean bean1 = getIntent().getParcelableExtra("passenger");
            bindView(bean1);
        }
    }

    private void bindView(TravellerBean bean) {
        etLastName.setText(bean.getTraveller().getSurname());
        etFirstName.setText(bean.getTraveller().getGivenName());
        etTel.setText(String.valueOf(bean.getTraveller().getTel().getNumber()));
        etId.setText(String.valueOf(bean.getTraveller().getIdentities().get(0).getNumber()));
        String type = bean.getTraveller().getIdentities().get(0).getIdType();
        if(idTypeArray[0].equals(type)){
            spinner.setSelection(0);
        }else if (idTypeArray[1].equals(type)){
            spinner.setSelection(1);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_title_back:
                finish();
                break;
            case R.id.iv_select_birthday:
                SelectBirthday();
                break;
            case R.id.tv_confirm:

                if (TextUtils.isEmpty(etFirstName.getText().toString())) {
                    Toast.makeText(mContext, "请填写完整旅客信息", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(etLastName.getText().toString())) {
                    Toast.makeText(mContext, "请填写完整旅客信息", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(etTel.getText().toString())) {
                    Toast.makeText(mContext, "请填写完整旅客信息", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!TextUtils.isDigitsOnly(etTel.getText().toString())) {
                    Toast.makeText(mContext, "请填写正确的电话号码", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(etId.getText().toString())) {
                    Toast.makeText(mContext, "请填写完整旅客信息", Toast.LENGTH_SHORT).show();
                    return;
                }

                switch (type){
                    case "create":
                        TravellerBean bean = new TravellerBean();
                        TravellerEntity traveller = new TravellerEntity();
                        IdentityBean identityBean = new IdentityBean();
                        identityBean.setNumber(etId.getText().toString());
                        identityBean.setIdType(idType);
                        traveller.setGivenName(etFirstName.getText().toString());
                        traveller.setSurname(etLastName.getText().toString());
                        TelBean tel = new TelBean();
                        tel.setDialCode(86);
                        tel.setNumber(Long.parseLong(etTel.getText().toString()));
                        traveller.setTel(tel);
                        ArrayList<IdentityBean> identityBeanArrayList = new ArrayList<>();
                        identityBeanArrayList.add(identityBean);
                        traveller.setIdentities(identityBeanArrayList);
                        bean.setTraveller(traveller);
                        submitTraveller(bean);
                        break;
                    case "edit":
                        TravellerBean bean1 = getIntent().getParcelableExtra("passenger");
                        TravellerEntity traveller1 = new TravellerEntity();
                        IdentityBean identityBean1 = new IdentityBean();
                        identityBean1.setNumber(etId.getText().toString());
                        identityBean1.setIdType(idType);
                        traveller1.setGivenName(etFirstName.getText().toString());
                        traveller1.setSurname(etLastName.getText().toString());
                        TelBean tel1 = new TelBean();
                        tel1.setDialCode(86);
                        tel1.setNumber(Long.parseLong(etTel.getText().toString()));
                        traveller1.setTel(tel1);
                        ArrayList<IdentityBean> identityBeanArrayList1 = new ArrayList<>();
                        identityBeanArrayList1.add(identityBean1);
                        traveller1.setIdentities(identityBeanArrayList1);
                        bean1.setTraveller(traveller1);
                        editTraveller(bean1);
                        break;
                    default:
                        break;
                }

                break;
        }
    }

    public void editTraveller(final TravellerBean bean){
        JSONObject idProof = new JSONObject();
        JSONObject tel = new JSONObject();
        try {
            idProof.put("idType", bean.getTraveller().getIdentities().get(0).getIdType());
            idProof.put("number", bean.getTraveller().getIdentities().get(0).getNumber());
            tel.put("dialCode", bean.getTraveller().getTel().getDialCode());
            tel.put("number", bean.getTraveller().getTel().getNumber());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        long userId = AccountManager.getInstance().getLoginAccount(mContext).getUserId();
        TravelApi.editTraveller(userId,bean.getKey(), bean.getTraveller().getSurname(), bean.getTraveller().getGivenName(), "", 0, idProof, tel, "", new HttpCallBack<String>() {
            @Override
            public void doSuccess(String result, String method) {
                CommonJson<TravellerBean> traveller = CommonJson.fromJson(result,TravellerBean.class);
                Intent intent = new Intent();
                intent.putExtra("passenger", traveller.result);
                setResult(RESULT_OK, intent);
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

    private void submitTraveller(final TravellerBean bean) {
        JSONObject idProof = new JSONObject();
        JSONObject tel = new JSONObject();
        try {
            idProof.put("idType", bean.getTraveller().getIdentities().get(0).getIdType());
            idProof.put("number", bean.getTraveller().getIdentities().get(0).getNumber());
            tel.put("dialCode", bean.getTraveller().getTel().getDialCode());
            tel.put("number", bean.getTraveller().getTel().getNumber());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        long userId = AccountManager.getInstance().getLoginAccount(mContext).getUserId();
        TravelApi.createTraveller(userId, bean.getTraveller().getSurname(), bean.getTraveller().getGivenName(), "", 0, idProof, tel, "", new HttpCallBack<String>() {
            @Override
            public void doSuccess(String result, String method) {
                CommonJson<TravellerBean> traveller = CommonJson.fromJson(result,TravellerBean.class);
                Intent intent = new Intent();
                intent.putExtra("passenger", traveller.result);
                setResult(RESULT_OK, intent);
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

    private void SelectBirthday() {
//        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        DatePickerDialog dialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE));
        final DatePicker datePicker = dialog.getDatePicker();
        dialog.setButton(DialogInterface.BUTTON_POSITIVE, "确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                String dateString = datePicker.getYear() + "-" + (datePicker.getMonth() + 1) + "-" + datePicker.getDayOfMonth();
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                String submitStr = dateString;
                try {
                    Date date = format.parse(dateString);
                    submitStr = format.format(date);
                    if (date.after(new Date())) {
                        ToastUtil.getInstance(UserInfoEditActivity.this).showToast("无效的生日设置");
                    } else {
                        tvBirthday.setText(submitStr);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });
        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        dialog.setCancelable(true);
        dialog.show();
    }
}
