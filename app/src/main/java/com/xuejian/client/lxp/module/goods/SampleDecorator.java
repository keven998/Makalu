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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class SampleDecorator implements CalendarCellDecorator {
    PlanBean bean;
    private static long TIME = 60*1000;
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

        PriceBean price = getPrice(bean, date);
        if (price!=null&&price.getPrice()>0){
            String dateString = Integer.toString(date.getDate());
            String priceString = "\n¥"+ CommonUtils.getPriceString(price.getPrice());
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
    public static PriceBean getPrice(PlanBean bean, Date date){
            for (PricingEntity entity : bean.getPricing()) {
                Date date1 = new Date(entity.getTimeRange().get(0));
                Date date2 = new Date(entity.getTimeRange().get(1));
                long time1 = entity.getTimeRange().get(0);
                long time2 = entity.getTimeRange().get(1);
                if (time2>=time1){
                    if (date.getTime()>=time1&&date.getTime()<=time2){
                        String s = new SimpleDateFormat("yyyy-MM-dd").format(date);
                        PriceBean price = new PriceBean();
                        price.setDate(s);
                        price.setPrice(entity.getPrice());
                        return price;
                    }
                }else if (time2<time1){
                    if (date.getTime()<=time1&&date.getTime()>=time2){
                        String s = new SimpleDateFormat("yyyy-MM-dd").format(date);
                        PriceBean price = new PriceBean();
                        price.setDate(s);
                        price.setPrice(entity.getPrice());
                        return price;
                    }
                }
//                if (date.equals(date1)||date.equals(date2)||(date.after(date1)&&date.before(date2))){
//                    String s = new SimpleDateFormat("yyyy-MM-dd").format(date);
//                    PriceBean price = new PriceBean();
//                    price.setDate(s);
//                    price.setPrice(entity.getPrice());
//                    return price;
//                }
            }
            return null;
    }
}
