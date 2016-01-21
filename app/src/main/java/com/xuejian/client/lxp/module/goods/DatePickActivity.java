package com.xuejian.client.lxp.module.goods;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.squareup.timessquare.CalendarCellDecorator;
import com.squareup.timessquare.CalendarPickerView;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.PeachBaseActivity;
import com.xuejian.client.lxp.bean.PlanBean;
import com.xuejian.client.lxp.bean.PriceBean;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by yibiao.qin on 2015/11/10.
 */
public class DatePickActivity extends PeachBaseActivity {

     PlanBean planData;
    CalendarPickerView calendar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_pick);
        planData = getIntent().getParcelableExtra("planList");
        final TextView back = (TextView) findViewById(R.id.tv_title_back);
        calendar = (CalendarPickerView) findViewById(R.id.calendar_view);
        Calendar nextYear = Calendar.getInstance();
        nextYear.add(Calendar.YEAR, 1);
        Date today = new Date();
        calendar.setDecorators(Arrays.<CalendarCellDecorator>asList(new SampleDecorator(planData)));
        calendar.init(today, nextYear.getTime())
                .inMode(CalendarPickerView.SelectionMode.SINGLE);
        //  .withSelectedDate(today);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Date date = calendar.getSelectedDate();
                if (date!=null){
                    try {
                        PriceBean bean = SampleDecorator.getPrice(planData, date);
                        Intent intent = new Intent();
                        if (bean!=null){
                            String s = new SimpleDateFormat("yyyy-MM-dd").format(date);
                            //   String s = DateFormat.getDateInstance(DateFormat.FULL).format(date);
                            //   PriceBean bean = new PriceBean();
                            bean.setDate(s);
                            intent.putExtra("date_price", bean);
                            setResult(RESULT_OK, intent);
                        }else {
                            setResult(RESULT_CANCELED, intent);
                        }
                        finish();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else {
                    setResult(RESULT_CANCELED);
                    finish();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        Date date = calendar.getSelectedDate();
        if (date!=null){
            try {
                PriceBean bean = SampleDecorator.getPrice(planData, date);
                Intent intent = new Intent();
                if (bean!=null){
                    String s = new SimpleDateFormat("yyyy-MM-dd").format(date);
                    //   String s = DateFormat.getDateInstance(DateFormat.FULL).format(date);
                    //   PriceBean bean = new PriceBean();
                    bean.setDate(s);
                    intent.putExtra("date_price", bean);
                    setResult(RESULT_OK, intent);
                }else {
                    setResult(RESULT_CANCELED, intent);
                }
                finish();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else {
            setResult(RESULT_CANCELED);
            finish();
        }

        super.onBackPressed();
    }
}
