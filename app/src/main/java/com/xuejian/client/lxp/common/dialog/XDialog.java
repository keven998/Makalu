package com.xuejian.client.lxp.common.dialog;

import android.app.Dialog;
import android.content.Context;
import android.text.Layout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.xuejian.client.lxp.R;

import java.util.List;
import java.util.zip.Inflater;

/**
 * Created by lxp_dqm07 on 2015/7/17.
 */
public class XDialog extends Dialog {
    private ListView listView;

    public XDialog(Context context){
        super(context, R.style.ComfirmDialog);
        initView();
        setCanceledOnTouchOutside(true);
    }

    public void initView(){
        setContentView(R.layout.map_day_select);
        listView=(ListView)findViewById(R.id.map_days_list);
    }



    public ListView getListView(){return listView;}
}
