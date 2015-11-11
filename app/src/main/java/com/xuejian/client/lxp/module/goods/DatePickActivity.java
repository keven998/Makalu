package com.xuejian.client.lxp.module.goods;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.squareup.timessquare.CalendarCellDecorator;
import com.squareup.timessquare.CalendarPickerView;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.PeachBaseActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by yibiao.qin on 2015/11/10.
 */
public class DatePickActivity extends PeachBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_pick);
        TextView back = (TextView) findViewById(R.id.tv_title_back);
        final CalendarPickerView calendar=(CalendarPickerView) findViewById(R.id.calendar_view);
        Calendar nextYear = Calendar.getInstance();
        nextYear.add(Calendar.YEAR, 1);
        Date today = new Date();
        calendar.setDecorators(Arrays.<CalendarCellDecorator>asList(new SampleDecorator()));
        calendar.init(today, nextYear.getTime())
                .inMode(CalendarPickerView.SelectionMode.MULTIPLE);
              //  .withSelectedDate(today);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                List<Date> dates = calendar.getSelectedDates();
                ArrayList<String> data = new ArrayList<String>();
                for (Date date : dates) {
                    data.add(date.toString());
                }
                intent.putExtra("date",data);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }
}
