package com.xuejian.client.lxp.common.dialog;

import android.app.Dialog;
import android.content.Context;
import android.widget.ListView;

import com.xuejian.client.lxp.R;

/**
 * Created by lxp_dqm07 on 2015/7/17.
 */
public class XDialog extends Dialog {
    private ListView listView;
    int resId;

    public XDialog(Context context, int resId) {
        super(context, R.style.ComfirmDialog);
        this.resId = resId;
        initView();
        setCanceledOnTouchOutside(true);
    }

    public XDialog(Context context, int resId, int style) {
        super(context, style);
        this.resId = resId;
        initView();
        setCanceledOnTouchOutside(true);
    }

    public void initView() {
        setContentView(resId);
        listView = (ListView) findViewById(R.id.loc_select_list);
    }

    public ListView getListView() {
        return listView;
    }
}
