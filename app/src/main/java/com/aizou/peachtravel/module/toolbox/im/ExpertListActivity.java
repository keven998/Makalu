package com.aizou.peachtravel.module.toolbox.im;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.aizou.core.dialog.ToastUtil;
import com.aizou.core.utils.LocalDisplay;
import com.aizou.peachtravel.R;
import com.aizou.peachtravel.base.PeachBaseActivity;
import com.aizou.peachtravel.common.widget.freeflow.core.AbsLayoutContainer;

/**
 * Created by lxp_dqm07 on 2015/4/14.
 */
public class ExpertListActivity extends PeachBaseActivity {

    private ListView listView;
    private ImageView user_pic;
    private EditText search_expert;
    private TextView user_name;
    private TextView user_status_01,user_status_02,user_status_03;
    private TextView user_level;
    private TextView user_loc;
    private TextView user_place;
    private TextView user_msg;
    private ExpertAdapter adapter;
    private LayoutInflater inflater;
    private LinearLayout places_layout;
    private int layout_width;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expert);
        search_expert=(EditText)findViewById(R.id.search_expert_name);
        findViewById(R.id.expert_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        inflater=LayoutInflater.from(this);
        initList();
    }

    private void initList(){
        listView=(ListView)findViewById(R.id.expert_list);
        listView.setOnItemClickListener(new DarenClick());
        adapter=new ExpertAdapter();
        listView.setAdapter(adapter);
    }

    public class DarenClick implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            ToastUtil.getInstance(ExpertListActivity.this).showToast("点击进入达人详情页或聊天页,第"+(position+1)+"个达人");
        }
    }

    public class ExpertAdapter extends BaseAdapter{
        private String[] status={"空","忙","阻"};
        private String[] places={"美国","澳大利亚","乌兹别克斯坦","日本"};
        @Override
        public int getCount() {
            return 3;//这个当然是数据的length，暂时是3假数据
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView==null){
                convertView=inflater.inflate(R.layout.expert_list_cont,null);
            }

            //初始化控件
            user_pic=(ImageView)convertView.findViewById(R.id.expert_pic);
            user_name=(TextView)convertView.findViewById(R.id.expert_name);
            user_status_01=(TextView)convertView.findViewById(R.id.expert_status_01);
            user_status_02=(TextView)convertView.findViewById(R.id.expert_status_02);
            user_status_03=(TextView)convertView.findViewById(R.id.expert_status_03);
            user_level=(TextView)convertView.findViewById(R.id.expert_level);
            user_loc=(TextView)convertView.findViewById(R.id.expert_location);
            places_layout=(LinearLayout)convertView.findViewById(R.id.places_layout);
            user_msg=(TextView)convertView.findViewById(R.id.expert_msg);

            //获取接口数据进行加载

            //控制达人的状态
            if(status[position]=="空"){
                changeStatusBg(user_status_01,user_status_02,user_status_03,status[position]);
            }else if(status[position]=="忙"){
                changeStatusBg(user_status_02,user_status_01,user_status_03,status[position]);
            }else if(status[position]=="阻"){
                changeStatusBg(user_status_03,user_status_02,user_status_01,status[position]);
            }else{
                //如果没有的话就不显示
            }

            //动态添加达人去过的地方
            places_layout.removeAllViews();
            layout_width=places_layout.getMeasuredWidth();
            int all_views_width=0;
            for(int i=0;i<places.length;i++){
                View places_view=inflater.inflate(R.layout.expert_place_view,places_layout,false);
                user_place=(TextView)places_view.findViewById(R.id.expert_places);
                user_place.setText(places[i]);
                int tv_width=user_place.getMeasuredWidth()+ LocalDisplay.dp2px(7);
                all_views_width+=tv_width;
                if(all_views_width<=layout_width){
                    places_layout.addView(user_place);
                }else{break;}
            }

            return convertView;
        }
    }

    private void changeStatusBg(TextView v1,TextView v2,TextView v3,String value){
        v1.setVisibility(View.VISIBLE);
        v2.setVisibility(View.GONE);
        v3.setVisibility(View.GONE);
        v1.setText(value);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
