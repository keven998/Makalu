package com.aizou.peachtravel.module.toolbox;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aizou.core.dialog.ToastUtil;
import com.aizou.core.http.HttpCallBack;
import com.aizou.core.log.LogUtil;
import com.aizou.core.utils.LocalDisplay;
import com.aizou.peachtravel.R;
import com.aizou.peachtravel.base.PeachBaseActivity;
import com.aizou.peachtravel.bean.ExpertBean;
import com.aizou.peachtravel.bean.LocBean;
import com.aizou.peachtravel.bean.PeachUser;
import com.aizou.peachtravel.common.api.UserApi;
import com.aizou.peachtravel.common.dialog.DialogManager;
import com.aizou.peachtravel.common.gson.CommonJson4List;
import com.aizou.peachtravel.common.imageloader.UILUtils;
import com.aizou.peachtravel.common.widget.FlowLayout;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by lxp_dqm07 on 2015/5/18.
 */
public class HisMainPageActivity extends PeachBaseActivity implements View.OnClickListener {

    @ViewInject(R.id.his_add_friend)
    TextView add_friend;
    @ViewInject(R.id.his_query)
    TextView query;
    @ViewInject(R.id.tv_his_brithday)
    TextView age;
    @ViewInject(R.id.tv_his_resident)
    TextView resident;
    @ViewInject(R.id.tv_his_sign)
    TextView sign;
    @ViewInject(R.id.tv_his_foot_print)
    TextView foot_print;
    @ViewInject(R.id.his_destination)
    FlowLayout his_destinations;
    @ViewInject(R.id.tv_his_plan)
    TextView his_trip_plan;
    @ViewInject(R.id.tv_his_status)
    TextView his_status;
    @ViewInject(R.id.tv_his_id)
    TextView his_id;
    @ViewInject(R.id.user_his_xingzuo)
    TextView xingzuo;
    @ViewInject(R.id.iv_his_gender)
    ImageView his_gender;
    @ViewInject(R.id.user_his_level)
    TextView his_level;
    @ViewInject(R.id.tv_his_nickname)
    TextView his_name;
    @ViewInject(R.id.iv_his_avatar)
    ImageView his_avatar;
    @ViewInject(R.id.tv_his_title_bar_title)
    TextView title_name;
    @ViewInject(R.id.all_his_pics_sv)
    HorizontalScrollView his_pics_sv;

    private int userId;
    private ImageView my_pics_cell;
    private ArrayList<LocBean> all_foot_print_list=new ArrayList<LocBean>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hismainpage);
        userId=getIntent().getExtras().getInt("userId");
        ViewUtils.inject(this);
        add_friend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        query.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        initData(userId);
    }


    public void initData(int id){
        getUserInfo(id);
        initScrollView();
    }

    public void refreshView(List<ExpertBean> bean){
        DisplayImageOptions options = UILUtils.getRadiusOption(LocalDisplay.dp2px(4));
        title_name.setText(bean.get(0).nickName);
        his_name.setText(bean.get(0).nickName);
        ImageLoader.getInstance().displayImage(bean.get(0).avatarSmall, his_avatar, options);
        his_level.setText("V" + bean.get(0).level);
        if(bean.get(0).gender.equals("F")){
            his_gender.setImageResource(R.drawable.girl);
        }else if(bean.get(0).gender.equals("F")){
            his_gender.setImageResource(R.drawable.boy);
        }
        xingzuo.setText(bean.get(0).zodiac);LogUtil.d(bean.get(0).zodiac);
        his_id.setText(String.valueOf(bean.get(0).userId));
        if(!TextUtils.isEmpty(bean.get(0).travelStatus)){
            his_status.setText(bean.get(0).travelStatus);
        }
        sign.setText(bean.get(0).signature);
        resident.setText(bean.get(0).residence);
        age.setText(/*getAge(bean.get(0).birthday)*/"25");

        try {
            int countries=0;
            int citys;
            JSONObject jsonObject = new JSONObject(bean.get(0).tracks.toString());
            Iterator iterator=jsonObject.keys();
            if(iterator.hasNext()){
                countries++;
                String key=(String)iterator.next();
                for(int i=0;i<bean.get(0).tracks.get(key).size();i++){
                    all_foot_print_list.add(bean.get(0).tracks.get(key).get(i));
                }
            }
            citys=all_foot_print_list.size();
            foot_print.setText("已经去过"+countries+"个国家， "+citys+"个城市");
        } catch (JSONException e) {
            e.printStackTrace();
        }


        initFlDestion(all_foot_print_list);
    }


    public int getAge(String birth){
        int age=0;
        String[] birthType=new String[3];
        birthType=birth.split("-");
        int birthYear=Integer.getInteger(birthType[0]);
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy");
        String date=sdf.format(new java.util.Date());
        age=Integer.getInteger(date)-birthYear;
        return age;
    }

    public void getUserInfo(int userid){
        DialogManager.getInstance().showModelessLoadingDialog(mContext);
        UserApi.seachContact(String.valueOf(userid), new HttpCallBack<String>() {
            @Override
            public void doSucess(String result, String method) {
                DialogManager.getInstance().dissMissModelessLoadingDialog();
                CommonJson4List<ExpertBean> expertInfo = CommonJson4List.fromJson(result, ExpertBean.class);
                if (expertInfo.code == 0) {
                      refreshView(expertInfo.result);
                }
            }


            @Override
            public void doFailure(Exception error, String msg, String method) {
                DialogManager.getInstance().dissMissModelessLoadingDialog();
            }
        });
    }

    public void initFlDestion(List<LocBean> locBeans){
        his_destinations.removeAllViews();
        for(int j=0;j<locBeans.size();j++){
            View contentView = View.inflate(HisMainPageActivity.this, R.layout.des_text_style2, null);
            final TextView cityNameTv = (TextView) contentView.findViewById(R.id.tv_cell_name);
            cityNameTv.setText(locBeans.get(j).zhName);
            his_destinations.addView(contentView);
        }
    }

    public void initScrollView(){
        his_pics_sv.removeAllViews();
        LinearLayout llPics=new LinearLayout(this);
        llPics.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        llPics.removeAllViews();
        for(int i=0;i<3;i++){
            View view=View.inflate(HisMainPageActivity.this,R.layout.my_all_pics_cell,null);
            my_pics_cell=(ImageView)view.findViewById(R.id.my_pics_cell);
            if(i==2){
                my_pics_cell.setImageResource(R.drawable.smiley_add_btn);
                my_pics_cell.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ToastUtil.getInstance(HisMainPageActivity.this).showToast("添加图片");
                    }
                });
            }
            else{
                my_pics_cell.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ToastUtil.getInstance(HisMainPageActivity.this).showToast("show pics");
                    }
                });

            }
            llPics.addView(view);
        }
        his_pics_sv.addView(llPics);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
        public void finish() {
            super.finish();
//        overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
        }

    @Override
    public void onClick(View v) {

    }
}
