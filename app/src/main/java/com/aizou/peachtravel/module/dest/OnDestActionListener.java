package com.aizou.peachtravel.module.dest;

import com.aizou.peachtravel.bean.LocBean;

/**
 * Created by Rjm on 2014/12/3.
 */
public interface OnDestActionListener{
    void onDestAdded(LocBean locBean);
    void onDestRemoved(LocBean locBean);
}
