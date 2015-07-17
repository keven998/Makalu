package com.xuejian.client.lxp.module.dest;

import com.xuejian.client.lxp.bean.LocBean;

/**
 * Created by Rjm on 2014/12/3.
 */
public interface OnDestActionListener {
    void onDestAdded(LocBean locBean, boolean isEdit, String type);

    void onDestRemoved(LocBean locBean, String type);
}
