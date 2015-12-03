package com.xuejian.client.lxp.module.goods;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;

import com.squareup.timessquare.CalendarCellDecorator;
import com.squareup.timessquare.CalendarCellView;
import com.xuejian.client.lxp.bean.PlanBean;
import com.xuejian.client.lxp.bean.PricingEntity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class SampleDecorator implements CalendarCellDecorator {
    ArrayList<PlanBean> list;
    public SampleDecorator( ArrayList<PlanBean> list){
        this.list = list;
    }

    @Override
    public void decorate(CalendarCellView cellView, Date date) {
        Date date1 = new Date();

        final Calendar lastYear = Calendar.getInstance();
        lastYear.add(Calendar.DAY_OF_YEAR, -1);
        if (date.before(lastYear.getTime())) {
            cellView.setText(Integer.toString(date.getDate()));
            cellView.setEnabled(false);
            return;
        }
        String dateString = Integer.toString(date.getDate());
        String priceString = "\n¥454";
        SpannableString string = new SpannableString(dateString + priceString);
        string.setSpan(new AbsoluteSizeSpan(13, true), 0, dateString.length(),
                Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        string.setSpan(new AbsoluteSizeSpan(11, true), dateString.length() + 1, dateString.length() + priceString.length(),
                Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        cellView.setText(string);
    }
    public boolean isShow(Date date){
        for (PlanBean bean : list) {
            for (PricingEntity entity : bean.getPricing()) {
                Date date1 = new Date(entity.getTimeRange().get(0));
                Date date2 = new Date(entity.getTimeRange().get(1));
                if (date.after(date1)&&date2.before(date)){
                    return true;
                }
            }
        }
        return false;
    }
}
