package com.xuejian.client.lxp.module.goods;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;

import com.squareup.timessquare.CalendarCellDecorator;
import com.squareup.timessquare.CalendarCellView;
import com.xuejian.client.lxp.bean.PlanBean;
import com.xuejian.client.lxp.bean.PricingEntity;

import java.util.Calendar;
import java.util.Date;

public class SampleDecorator implements CalendarCellDecorator {
    PlanBean bean;
    public SampleDecorator( PlanBean bean){
        this.bean = bean;
    }

    @Override
    public void decorate(CalendarCellView cellView, Date date) {

        final Calendar lastYear = Calendar.getInstance();
        lastYear.add(Calendar.DAY_OF_YEAR, -1);
        if (date.before(lastYear.getTime())) {
            cellView.setText(Integer.toString(date.getDate()));
            cellView.setEnabled(false);
            return;
        }

        int price = getPrice(bean, date);
        if (price>0){
            String dateString = Integer.toString(date.getDate());
            String priceString = "\n¥"+price;
            SpannableString string = new SpannableString(dateString + priceString);
            string.setSpan(new AbsoluteSizeSpan(13, true), 0, dateString.length(),
                    Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            string.setSpan(new AbsoluteSizeSpan(11, true), dateString.length() + 1, dateString.length() + priceString.length(),
                    Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            cellView.setText(string);
        }else {
            cellView.setText(Integer.toString(date.getDate()));
            cellView.setEnabled(false);
        }

    }
    public static int getPrice(PlanBean bean, Date date){
            for (PricingEntity entity : bean.getPricing()) {
                Date date1 = new Date(entity.getTimeRange().get(0));
                Date date2 = new Date(entity.getTimeRange().get(1));
                if (date.equals(date1)||date.equals(date2)||(date.after(date1)&&date.before(date2))){
                    return entity.getPrice();
                }
            }
            return -1;
    }
}
