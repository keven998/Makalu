package com.lv.im;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yibiao.qin on 2015/6/17.
 */
public class CountFrequency {
    private long lastSendTime;
    private List<Long> times;
    private long time;
    public CountFrequency(){
        times=new ArrayList<>();
    }
    public void addMessage(){
        time=System.currentTimeMillis();
        if (lastSendTime==0)lastSendTime=time;
        else {
            times.add(time-lastSendTime);
        }
    }
    public long getFrequency(){
        long sum = 0;
        if(!times.isEmpty()) {
            for (Long mark : times) {
                sum += mark;
            }
            return sum / times.size();
        }
        return sum;
    }
}
