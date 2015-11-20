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
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.PeachBaseActivity;
import com.xuejian.client.lxp.bean.PassengerBean;
import com.xuejian.client.lxp.module.dest.adapter.StringSpinnerAdapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_edit);
        ButterKnife.inject(this);
        tvTitleBack.setOnClickListener(this);
        tvConfirm.setOnClickListener(this);
        ivSelectBirthday.setOnClickListener(this);
        String [] mItems =new String[]{"护照","身份证"};
        StringSpinnerAdapter mTypeListAdapter = new StringSpinnerAdapter(mContext, Arrays.asList(mItems));
        spinner.setAdapter(mTypeListAdapter);
        spinner.setSelection(0, true);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
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
                Intent intent = new Intent();
                PassengerBean bean = new PassengerBean();
                if(TextUtils.isEmpty(etFirstName.getText().toString())){
                    Toast.makeText(mContext,"请填写完整旅客信息",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(etLastName.getText().toString())){
                    Toast.makeText(mContext,"请填写完整旅客信息",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(etTel.getText().toString())){
                    Toast.makeText(mContext,"请填写完整旅客信息",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(etId.getText().toString())){
                    Toast.makeText(mContext,"请填写完整旅客信息",Toast.LENGTH_SHORT).show();
                    return;
                }
                bean.lastName = etLastName.getText().toString();
                bean.firstName = etFirstName.getText().toString();
                bean.id = etId.getText().toString();
                bean.tel = etTel.getText().toString();
                intent.putExtra("passenger",bean);
                setResult(RESULT_OK, intent);
                finish();
                break;
        }
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
