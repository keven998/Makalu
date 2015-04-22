package com.aizou.peachtravel.module.toolbox.im;

import android.content.Context;
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
import com.aizou.core.http.HttpCallBack;
import com.aizou.core.utils.LocalDisplay;
import com.aizou.peachtravel.R;
import com.aizou.peachtravel.base.PeachBaseActivity;
import com.aizou.peachtravel.bean.ExpertBean;
import com.aizou.peachtravel.common.api.UserApi;
import com.aizou.peachtravel.common.dialog.CustomLoadingDialog;
import com.aizou.peachtravel.common.dialog.DialogManager;
import com.aizou.peachtravel.common.gson.CommonJson4List;
import com.aizou.peachtravel.common.imageloader.UILUtils;
import com.aizou.peachtravel.common.widget.DynamicBox;
import com.aizou.peachtravel.common.widget.freeflow.core.AbsLayoutContainer;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

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
    private TextView places_layout;
    private int layout_width;
    private DynamicBox box;
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
        box=new DynamicBox(this,listView);
        listView.setOnItemClickListener(new DarenClick());
        getExpertData();
    }

    public class DarenClick implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            ToastUtil.getInstance(ExpertListActivity.this).showToast("点击进入达人详情页或聊天页,第"+(position+1)+"个达人");
        }
    }

    public void getExpertData(){
        DialogManager.getInstance().showModelessLoadingDialog(mContext);
        UserApi.searchExpertContact("expert","roles",new HttpCallBack<String>() {
            @Override
            public void doSucess(String result, String method) {
                DialogManager.getInstance().dissMissModelessLoadingDialog();
                box.hideAll();
                CommonJson4List<ExpertBean> expertresult = CommonJson4List.fromJson(result , ExpertBean.class);
                if(expertresult.code==0){
                    adapter=new ExpertAdapter(ExpertListActivity.this,expertresult.result);
                    listView.setAdapter(adapter);
                }
            }

            @Override
            public void doFailure(Exception error, String msg, String method) {
                DialogManager.getInstance().dissMissModelessLoadingDialog();
                box.hideAll();
            }
        });
    }

    public class ExpertAdapter extends BaseAdapter{
        private Context context;
        private String[] status={"空","忙","阻"};
        public List<ExpertBean> expertBean;
        private DisplayImageOptions options;

        public ExpertAdapter(Context context,List<ExpertBean> expert){
            this.context=context;
            this.expertBean=expert;
            options = UILUtils.getRadiusOption(LocalDisplay.dp2px(4));
        }

        @Override
        public int getCount() {
            return expertBean.size();}

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView==null){
                inflater=LayoutInflater.from(this.context);
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
            places_layout=(TextView)convertView.findViewById(R.id.places_layout);
            user_msg=(TextView)convertView.findViewById(R.id.expert_msg);

            //获取接口数据进行加载
            ImageLoader.getInstance().displayImage(expertBean.get(position).avatarSmall, user_pic, options);
            user_name.setText(expertBean.get(position).nickName);
            user_msg.setText(expertBean.get(position).signature);
            user_level.setText("V"+expertBean.get(position).level);
            user_loc.setText("现住地 "+expertBean.get(position).residence);

            //控制达人的状态
            if(expertBean.get(position).travelStatus==null||expertBean.get(position).travelStatus.equals("")||expertBean.get(position).travelStatus.equals("null")){
                changeStatusBg(user_status_01,user_status_02,user_status_03,"空");
            }else/* if(status[position]=="忙"){
                changeStatusBg(user_status_02,user_status_01,user_status_03,status[position]);
            }else if(status[position]=="阻")*/{
                changeStatusBg(user_status_03,user_status_02,user_status_01,"阻");
            }

            places_layout.setText("已经去过21个国家和地区");
            //动态添加达人去过的地方
            /*places_layout.removeAllViews();
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
            }*/

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
