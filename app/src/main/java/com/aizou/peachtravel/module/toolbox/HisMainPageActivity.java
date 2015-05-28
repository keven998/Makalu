package com.aizou.peachtravel.module.toolbox;

import android.content.Intent;
import android.net.Uri;
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
import com.aizou.peachtravel.common.account.AccountManager;
import com.aizou.peachtravel.common.api.UserApi;
import com.aizou.peachtravel.common.dialog.DialogManager;
import com.aizou.peachtravel.common.dialog.PeachEditDialog;
import com.aizou.peachtravel.common.gson.CommonJson4List;
import com.aizou.peachtravel.common.imageloader.UILUtils;
import com.aizou.peachtravel.common.utils.IntentUtils;
import com.aizou.peachtravel.common.widget.FlowLayout;
import com.aizou.peachtravel.db.IMUser;
import com.aizou.peachtravel.db.respository.IMUserRepository;
import com.aizou.peachtravel.module.dest.StrategyMapActivity;
import com.aizou.peachtravel.module.my.LoginActivity;
import com.aizou.peachtravel.module.toolbox.im.ChatActivity;
import com.aizou.peachtravel.module.toolbox.im.ContactDetailActivity;
import com.aizou.peachtravel.module.toolbox.im.SeachContactDetailActivity;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
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
    /*@ViewInject(R.id.his_query)
    TextView query;*/
    @ViewInject(R.id.tv_his_brithday)
    TextView age;
    @ViewInject(R.id.tv_his_resident)
    TextView resident;
    @ViewInject(R.id.tv_his_sign)
    TextView sign;
    @ViewInject(R.id.tv_his_foot_print)
    TextView foot_print;
    @ViewInject(R.id.his_destination)
    TextView his_destinations;
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
    @ViewInject(R.id.ll_his_trip_plan)
    LinearLayout ll_his_trip_plan;
    @ViewInject(R.id.ll_foot_print)
    LinearLayout ll_foot_print;
    @ViewInject(R.id.tv_title_back)
    TextView tv_back;

    private int userId;
    private ImageView my_pics_cell;
    private ArrayList<LocBean> all_foot_print_list=new ArrayList<LocBean>();
    private ArrayList<String> all_pics=new ArrayList<String>();
    DisplayImageOptions options;
    PeachUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hismainpage);
        userId=getIntent().getExtras().getInt("userId");
        user= AccountManager.getInstance().getLoginAccount(HisMainPageActivity.this);
        options= new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.avatar_placeholder_round)
                .showImageOnFail(R.drawable.avatar_placeholder_round)
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisc(true)
                        // 设置下载的图片是否缓存在SD卡中
                .displayer(
                        new RoundedBitmapDisplayer(LocalDisplay.dp2px(
                                0))) // 设置成圆角图片
                .build();
        ViewUtils.inject(this);
        tv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ll_his_trip_plan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HisMainPageActivity.this, StrategyListActivity.class);
                intent.putExtra("userId", userId + "");
                intent.putExtra("isExpertPlan", true);
                startActivity(intent);
            }
        });
        initData(userId);
    }


    public void initData(int id){
        getUserInfo(id);
        if(user!=null&&!TextUtils.isEmpty(user.easemobUser)){
            initScrollView(id);
        }
    }

    public void refreshView(final List<ExpertBean> bean){
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
        if(bean.get(0).residence.equals("")||bean.get(0).residence==null){
            resident.setText("未设置");
        }else{
        resident.setText(bean.get(0).residence);
        }
        if(getAge(bean.get(0).birthday)==0){
            age.setText("未设置");
        }else{
        age.setText(getAge(bean.get(0).birthday)+"");
        }


        if(IMUserRepository.isMyFriend(HisMainPageActivity.this, bean.get(0).easemobUser)){
            add_friend.setText("咨询达人");
            add_friend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   /* Intent intent = new Intent(HisMainPageActivity.this, ContactDetailActivity.class);
                    intent.putExtra("userId", (long)bean.get(0).userId);
                    intent.putExtra("userNick", bean.get(0).nickName);
                    startActivity(intent);*/
                    if(user!=null&&!TextUtils.isEmpty(user.easemobUser)){
                        IMUser imUser = IMUserRepository.getContactByUserId(mContext, (long)bean.get(0).userId);
                        startActivity(new Intent(mContext, ChatActivity.class).putExtra("userId", imUser.getUsername()));
                        finish();
                    }else{
                        Intent intent=new Intent(HisMainPageActivity.this, LoginActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.push_bottom_in,0);
                        finish();
                    }
                }
            });

        }else{
            add_friend.setText("加为好友");
            add_friend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   /* PeachUser user = new PeachUser();
                    user.nickName = bean.get(0).nickName;
                    user.userId = bean.get(0).userId;
                    user.easemobUser = bean.get(0).easemobUser;
                    user.avatar = bean.get(0).avatar;
                    user.avatarSmall = bean.get(0).avatarSmall;
                    user.signature = bean.get(0).signature;
                    user.gender = bean.get(0).gender;
                    user.memo = bean.get(0).memo;
                    Intent intent = new Intent(HisMainPageActivity.this, SeachContactDetailActivity.class);
                    intent.putExtra("user", user);
                    startActivity(intent);
*/
                    if (user != null && !TextUtils.isEmpty(user.easemobUser)) {
                        final PeachEditDialog editDialog = new PeachEditDialog(mContext);
                        editDialog.setTitle("输入验证信息");
                        editDialog.setMessage(String.format("\"Hi, 我是%s\"", AccountManager.getInstance().getLoginAccount(HisMainPageActivity.this).nickName));
                        editDialog.setPositiveButton("确定", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                editDialog.dismiss();
                                DialogManager.getInstance().showLoadingDialog(HisMainPageActivity.this);
                                UserApi.requestAddContact(bean.get(0).userId + "", editDialog.getMessage(), new HttpCallBack() {
                                    @Override
                                    public void doSucess(Object result, String method) {
                                        DialogManager.getInstance().dissMissLoadingDialog();
//                                    Toast.makeText(getApplicationContext(), "发送请求成功,等待对方验证", Toast.LENGTH_SHORT).show();
                                        ToastUtil.getInstance(getApplicationContext()).showToast("请求已发送，等待对方验证");
                                        finish();
                                    }

                                    @Override
                                    public void doFailure(Exception error, String msg, String method) {
                                        DialogManager.getInstance().dissMissLoadingDialog();
//                                    Toast.makeText(getApplicationContext(), "请求添加桃友失败", Toast.LENGTH_SHORT).show();
                                        ToastUtil.getInstance(HisMainPageActivity.this).showToast(getResources().getString(R.string.request_network_failed));
                                    }
                                });
                            }
                        });

                        editDialog.show();
                    }else{
                        Intent intent=new Intent(HisMainPageActivity.this, LoginActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.push_bottom_in,0);
                        finish();
                    }
                }
            });
        }

        try {
            int countries=0;
            int citys;
            JSONObject jsonObject = new JSONObject(bean.get(0).tracks.toString());
            Iterator iterator=jsonObject.keys();
            while(iterator.hasNext()){
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
        ll_foot_print.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HisMainPageActivity.this, StrategyMapActivity.class);
                intent.putExtra("isExpertFootPrint", true);
                intent.putParcelableArrayListExtra("ExpertFootPrintBean",all_foot_print_list);
                startActivity(intent);
            }
        });
    }


    public int getAge(String birth){
        int age=0;
        String birthType=birth.substring(0,4).toString();
        int birthYear=Integer.parseInt(birthType);
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy");
        String date=sdf.format(new java.util.Date());
        age=Integer.parseInt(date)-birthYear;
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
                ToastUtil.getInstance(HisMainPageActivity.this).showToast("好像没有网络额~");
            }
        });
    }

    public void initFlDestion(List<LocBean> locBeans){
        String destinations="";
        if(locBeans.size()>0){
            for(int j=0;j<locBeans.size();j++){
               /* View contentView = View.inflate(HisMainPageActivity.this, R.layout.des_text_style2, null);
                final TextView cityNameTv = (TextView) contentView.findViewById(R.id.tv_cell_name);
                cityNameTv.setText(locBeans.get(j).zhName);
                his_destinations.addView(contentView);*/
                destinations+=(locBeans.get(j).zhName+"  ");
            }
            his_destinations.setText(destinations);
        }else{
           /* View contentView = View.inflate(HisMainPageActivity.this, R.layout.des_text_style2, null);
            final TextView cityNameTv = (TextView) contentView.findViewById(R.id.tv_cell_name);
            cityNameTv.setText("还没有我的足迹");*/
            his_destinations.setText("还没有我的足迹");
            //his_destinations.addView(contentView);
        }
    }

    public void initScrollView(int userId){
        UserApi.getUserPicAlbumn(String.valueOf(userId), new HttpCallBack<String>() {
            @Override
            public void doSucess(String result, String method) {
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(result);
                    if(jsonObject.getInt("code")==0) {
                        JSONArray object = jsonObject.getJSONArray("result");
                        for (int i = 0; i < object.length(); i++) {
                            JSONArray imgArray = object.getJSONObject(i).getJSONArray("image");
                            all_pics.add(imgArray.getJSONObject(0).getString("url"));
                        }
                        refreshUserPics(all_pics);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


            @Override
            public void doFailure(Exception error, String msg, String method) {
                ToastUtil.getInstance(HisMainPageActivity.this).showToast("好像没有网络额~");
            }
        });
    }

    public void refreshUserPics(final ArrayList<String> pics){
        his_pics_sv.removeAllViews();
        LinearLayout llPics=new LinearLayout(this);
        llPics.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        llPics.removeAllViews();
        for(int i=0;i<pics.size();i++){
            View view=View.inflate(HisMainPageActivity.this,R.layout.my_all_pics_cell,null);
            my_pics_cell=(ImageView)view.findViewById(R.id.my_pics_cell);
            ImageLoader.getInstance().displayImage(pics.get(i), my_pics_cell, options);
           /* if(i==pics.size()-1){
                my_pics_cell.setImageResource(R.drawable.smiley_add_btn);
                my_pics_cell.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ToastUtil.getInstance(HisMainPageActivity.this).showToast("添加图片");
                    }
                });
            }
            else*/
                my_pics_cell.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        IntentUtils.intentToPicGallery2(HisMainPageActivity.this, pics, 0);
                                                    }
                                                }
                );

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
