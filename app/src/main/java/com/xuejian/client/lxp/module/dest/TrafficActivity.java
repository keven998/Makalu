package com.xuejian.client.lxp.module.dest;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.aizou.core.widget.pagerIndicator.indicator.FixedIndicatorView;
import com.aizou.core.widget.pagerIndicator.indicator.Indicator;
import com.jzxiang.pickerview.TimePickerDialog;
import com.jzxiang.pickerview.data.Type;
import com.jzxiang.pickerview.listener.OnDateSetListener;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.PeachBaseActivity;
import com.xuejian.client.lxp.bean.TrafficBean;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;

public class TrafficActivity extends PeachBaseActivity implements OnDateSetListener {
    @Bind(R.id.tv_title_back)
    TextView mTvTitleBack;
    @Bind(R.id.et_traffic_type)
    EditText mEtTrafficType;
    @Bind(R.id.et_start)
    EditText mEtStart;
    @Bind(R.id.et_end)
    EditText mEtEnd;
    @Bind(R.id.tv_time_setoff)
    TextView mTvTimeSetoff;
    @Bind(R.id.tv_time_arrive)
    TextView mTvTimeArrive;
    @Bind(R.id.indicator)
    FixedIndicatorView indicator;
    @Bind(R.id.tv_save)
    TextView tvSave;
    int timeType;
    int type;
    private String[] tabNames = new String[]{"    飞机","    火车","    其他"};
    TrafficBean mBean = new TrafficBean();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_traffic);
        ButterKnife.bind(this);
        mBean.category = "airline";
        mTvTitleBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        final TimePickerDialog mDialogHourMinute = new TimePickerDialog.Builder()
                .setType(Type.ALL)
                .setCallBack(this)
                .setThemeColor(ContextCompat.getColor(this, R.color.app_theme_color))
                .build();

         mTvTimeSetoff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timeType = 1;
                mDialogHourMinute.show(getSupportFragmentManager(), "dialog");
            }
        });
        mTvTimeArrive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timeType = 2;
                mDialogHourMinute.show(getSupportFragmentManager(), "dialog");
            }
        });
        indicator.setSplitMethod(FixedIndicatorView.SPLITMETHOD_WEIGHT);
        indicator.setAdapter(new Indicator.IndicatorAdapter() {
            @Override
            public int getCount() {
                return 3;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.tab_rectangle_select, parent, false);
                }
                TextView textView = (TextView) convertView.findViewById(R.id.desty_title);
                textView.setText(tabNames[position]);
                if (position == 0) {
                    textView.setBackgroundResource(R.drawable.in_out_indicator_textbg);
                } else if (position == 2) {
                    textView.setBackgroundResource(R.drawable.in_out_indicator_textbg_01);
                }else if (position ==1){
                    textView.setBackgroundResource(R.drawable.in_out_indicator_textbg_02);
                }
                return convertView;
            }
        });
        indicator.setOnItemSelectListener(new Indicator.OnItemSelectedListener() {
            @Override
            public void onItemSelected(View selectItemView, int select, int preSelect) {
                type = select;
                updateView();
            }
        });
        tvSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBean.start = mEtStart.getText().toString();
                mBean.end = mEtEnd.getText().toString();
                mBean.desc = mEtTrafficType.getText().toString();
                Intent intent = new Intent();
                intent.putExtra("traffic",mBean);
                setResult(RESULT_OK,intent);
                finish();
            }
        });
    }

    private void updateView() {
        switch (type){
            case 1:
                mEtTrafficType.setText("");
                mTvTimeSetoff.setText("未选择");
                mTvTimeArrive.setText("未选择");
                mEtTrafficType.setHint("火车车次");
                mBean.category = "trainRoute";
                break;
            case 2:
                mEtTrafficType.setText("");
                mTvTimeSetoff.setText("未选择");
                mTvTimeArrive.setText("未选择");
                mEtTrafficType.setHint("其他交通");
                mBean.category = "other";
                break;
            case 0:
                mEtTrafficType.setText("");
                mTvTimeSetoff.setText("未选择");
                mTvTimeArrive.setText("未选择");
                mEtTrafficType.setHint("飞机航班");
                mBean.category = "airline";
                break;
        }
    }

    @Override
    public void onDateSet(TimePickerDialog timePickerView, long millseconds) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 E kk点mm分", Locale.CHINA);
        if (timeType==1){
            mTvTimeSetoff.setText(simpleDateFormat.format(new Date(millseconds)));
            mBean.depTime = simpleDateFormat.format(new Date(millseconds));
        }else if (timeType ==2){
            mTvTimeArrive.setText(simpleDateFormat.format(new Date(millseconds)));
            mBean.arrTime = simpleDateFormat.format(new Date(millseconds));
        }
    }
}
