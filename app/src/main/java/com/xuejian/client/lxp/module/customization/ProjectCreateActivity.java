package com.xuejian.client.lxp.module.customization;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.TextView;

import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.PeachBaseActivity;
import com.xuejian.client.lxp.common.widget.ListViewForScrollView;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by yibiao.qin on 2016/3/30.
 */
public class ProjectCreateActivity extends PeachBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_project);
        chooseCloseReason(1);
    }


    private void chooseCloseReason(int type) {
        final AlertDialog dialog = new AlertDialog.Builder(this).create();
        View contentView = View.inflate(this, R.layout.dialog_project_select, null);
        ListViewForScrollView listView = (ListViewForScrollView) contentView.findViewById(R.id.lv);
        TextView title = (TextView) contentView.findViewById(R.id.tv_title);
        final String[] theme = new String[]{"蜜月度假", "家庭亲子", "美食购物", "人文探索", "户外体验"};
        final String[] service = new String[]{"机票酒店", "美食门票", "导游接机", "行程设计", "全套服务"};

        ThemeAdapter adapter ;
        if (type==1){
            title.setText("主题偏向");
            adapter = new ThemeAdapter(new ArrayList<>(Arrays.asList(theme)) );
        }else {
            title.setText("服务包含");
            adapter = new ThemeAdapter(new ArrayList<>(Arrays.asList(service)));
        }
        listView.setAdapter(adapter);
        contentView.findViewById(R.id.iv_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                dialog.dismiss();
            }
        });
        dialog.show();
        WindowManager windowManager = getWindowManager();
        Window window = dialog.getWindow();
        window.setContentView(contentView);
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = display.getWidth(); // 设置宽度
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
        window.setGravity(Gravity.CENTER); // 此处可以设置dialog显示的位置
        window.setWindowAnimations(R.style.SelectPicDialog); // 添加动画
    }

    public class ThemeAdapter extends BaseAdapter{

        ArrayList<String> list;
        ArrayList<Integer>  selected;
        public ThemeAdapter( ArrayList<String> list){
            this.list = list;
            selected = new ArrayList<>();
        }
        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public String getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        public boolean isSelected(Integer pos){
            return selected.contains(pos);
        }

        public void  setSelected(Integer pos){
            if (!selected.contains(pos)){
                selected.add(pos);
            }else {
                selected.remove(pos);
            }
        }

        public ArrayList<Integer> getSelected() {
            return selected;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_project_theme,null);
            TextView textView = (TextView) convertView.findViewById(R.id.tv_content);
            final CheckedTextView checkedTextView = (CheckedTextView) convertView.findViewById(R.id.ctv);
            textView.setText(getItem(position));
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setSelected(position);
                    checkedTextView.setChecked(isSelected(position));
                }
            });


            return convertView;
        }
    }
}
