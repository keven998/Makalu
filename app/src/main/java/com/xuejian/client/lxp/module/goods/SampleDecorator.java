package com.xuejian.client.lxp.module.goods;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;

import com.squareup.timessquare.CalendarCellDecorator;
import com.squareup.timessquare.CalendarCellView;
import com.xuejian.client.lxp.bean.PlanBean;
import com.xuejian.client.lxp.bean.PriceBean;
import com.xuejian.client.lxp.bean.PricingEntity;
import com.xuejian.client.lxp.common.utils.CommonUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class SampleDecorator implements CalendarCellDecorator {
    PlanBean bean;
    private static long TIME = 60 * 1000;

    public SampleDecorator(PlanBean bean) {
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

        PriceBean price = getPrice(bean, date);
        if (price != null && price.getPrice() > 0) {
            String dateString = Integer.toString(date.getDate());
            String priceString = "\n¥" + CommonUtils.getPriceString(price.getPrice());
            SpannableString string = new SpannableString(dateString + priceString);
            string.setSpan(new AbsoluteSizeSpan(13, true), 0, dateString.length(),
                    Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            string.setSpan(new AbsoluteSizeSpan(11, true), dateString.length() + 1, dateString.length() + priceString.length(),
                    Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            cellView.setText(string);
        } else {
            cellView.setText(Integer.toString(date.getDate()));
            cellView.setEnabled(false);
        }

    }

    public static PriceBean getPrice(PlanBean bean, Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZ");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        for (PricingEntity entity : bean.getPricing()) {
            String dateString1 = entity.getTimeRange().get(0);
            String dateString2 = entity.getTimeRange().get(1);
            try {
                Date date1 = sdf.parse(dateString1);
                Date date2 = sdf.parse(dateString2);

                if (checkDate(date,date1)){
                    String s = simpleDateFormat.format(date);
                    PriceBean price = new PriceBean();
                    price.setDate(s);
                    price.setPrice(entity.getPrice());
                    return price;
                }else if (checkDate(date,date2)){
                    String s = simpleDateFormat.format(date);
                    PriceBean price = new PriceBean();
                    price.setDate(s);
                    price.setPrice(entity.getPrice());
                    return price;
                }else if ((date.after(date1) && date.before(date2))) {
                    String s = simpleDateFormat.format(date);
                    PriceBean price = new PriceBean();
                    price.setDate(s);
                    price.setPrice(entity.getPrice());
                    return price;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static boolean checkDate(Date date1, Date date2) {
        int year1 = date1.getYear() + 1900;
        int month1 = date1.getMonth() + 1;
        int d1 = date1.getDate();
        int year2 = date2.getYear() + 1900;
        int month2 = date2.getMonth() + 1;
        int d2 = date2.getDate();

        if (year1 == year2 && month1 == month2 && d1 == d2) {
            return true;
        }
        return false;
    }

}
