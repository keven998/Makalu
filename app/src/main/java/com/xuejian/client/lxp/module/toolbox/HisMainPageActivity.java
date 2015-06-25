package com.xuejian.client.lxp.module.toolbox;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aizou.core.dialog.ToastUtil;
import com.aizou.core.http.HttpCallBack;
import com.aizou.core.utils.LocalDisplay;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.umeng.analytics.MobclickAgent;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.PeachBaseActivity;
import com.xuejian.client.lxp.bean.LocBean;
import com.xuejian.client.lxp.bean.ModifyResult;
import com.xuejian.client.lxp.common.account.AccountManager;
import com.xuejian.client.lxp.common.api.UserApi;
import com.xuejian.client.lxp.common.dialog.DialogManager;
import com.xuejian.client.lxp.common.dialog.PeachEditDialog;
import com.xuejian.client.lxp.common.dialog.PeachMessageDialog;
import com.xuejian.client.lxp.common.gson.CommonJson;
import com.xuejian.client.lxp.common.imageloader.UILUtils;
import com.xuejian.client.lxp.common.utils.IntentUtils;
import com.xuejian.client.lxp.db.User;
import com.xuejian.client.lxp.db.UserDBManager;
import com.xuejian.client.lxp.module.dest.StrategyMapActivity;
import com.xuejian.client.lxp.module.my.LoginActivity;
import com.xuejian.client.lxp.module.toolbox.im.ChatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
    @ViewInject(R.id.tv_title_del)
    TextView tv_del;

    private int userId;
    private ImageView my_pics_cell;
    private ArrayList<LocBean> all_foot_print_list=new ArrayList<LocBean>();
    private ArrayList<String> all_pics=new ArrayList<String>();
    DisplayImageOptions options;
    User user;
    private User imUser;
    private int EXPERT_INT=2;
    /*PeachUser user;
    PeachUser hisBean;
    private IMUser imUser;*/


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hismainpage);
        userId=getIntent().getExtras().getInt("userId");
        user= AccountManager.getInstance().getLoginAccount(HisMainPageActivity.this);
        if(user!=null) {
            imUser = UserDBManager.getInstance().getContactByUserId(userId);
        }
        options= new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.messages_bg_useravatar)
                .showImageOnFail(R.drawable.messages_bg_useravatar)
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
        if(user!=null) {
            if (userId != 10000 && UserDBManager.getInstance().isMyFriend((long) userId)) {
                tv_del.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showActionDialog();
                    }
                });
            } else {
                tv_del.setVisibility(View.GONE);
            }
        }else{
            tv_del.setVisibility(View.GONE);
        }

        ll_his_trip_plan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HisMainPageActivity.this, StrategyListActivity.class);
                intent.putExtra("userId", String.valueOf(userId));
                intent.putExtra("user_name", user.getNickName());
                startActivity(intent);
            }
        });
        initData(userId);
    }


    public void initData(int id){
        getUserInfo(id);
        if(user!=null){ //&&!TextUtils.isEmpty(user.easemobUser)
            initScrollView(id);
        }
    }

    private void showActionDialog() {
        final Activity act = this;
        final AlertDialog dialog = new AlertDialog.Builder(act).create();
        View contentView = View.inflate(act, R.layout.dialog_home_confirm_action, null);
        Button btn = (Button) contentView.findViewById(R.id.btn_go_plan);
        btn.setTextColor(getResources().getColor(R.color.app_theme_color));
        btn.setText("删除");
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MobclickAgent.onEvent(mContext, "event_delete_it");
                final PeachMessageDialog deleteDialog = new PeachMessageDialog(act);
                deleteDialog.setTitle("提示");
                deleteDialog.setMessage("删除确认");
                deleteDialog.setPositiveButton("确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteContact(imUser);
                        deleteDialog.dismiss();
                    }
                });
                deleteDialog.setNegativeButton("取消", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteDialog.dismiss();
                    }
                });
                deleteDialog.show();

                dialog.dismiss();
            }
        });
        contentView.findViewById(R.id.btn_cancle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
        WindowManager windowManager = act.getWindowManager();
        Window window = dialog.getWindow();
        window.setContentView(contentView);
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = (int) (display.getWidth()); // 设置宽度
        window.setAttributes(lp);
        window.setGravity(Gravity.BOTTOM); // 此处可以设置dialog显示的位置
        window.setWindowAnimations(R.style.SelectPicDialog); // 添加动画
    }

    private void deleteContact(final User tobeDeleteUser) {
        DialogManager.getInstance().showLoadingDialog(this, "正在删除...");
        UserApi.deleteContact(String.valueOf(tobeDeleteUser.getUserId()), new HttpCallBack() {
            @Override
            public void doSuccess(Object result, String method) {
                DialogManager.getInstance().dissMissLoadingDialog();
                CommonJson<ModifyResult> deleteResult = CommonJson.fromJson((String) result, ModifyResult.class);
                if (deleteResult.code == 0) {
                     UserDBManager.getInstance().deleteContact(tobeDeleteUser.getUserId());
                    AccountManager.getInstance().getContactList(HisMainPageActivity.this).remove(tobeDeleteUser.getUserId());
                    finish();
                } else if (!TextUtils.isEmpty(deleteResult.err.message)) {
                    ToastUtil.getInstance(HisMainPageActivity.this).showToast(deleteResult.err.message);
                }

            }

            @Override
            public void doFailure(Exception error, String msg, String method) {
                DialogManager.getInstance().dissMissLoadingDialog();
                ToastUtil.getInstance(HisMainPageActivity.this).showToast("删除失败");
            }
        });

    }

    public void refreshView(final User bean){
        if(user!=null) {
            int type;
            User user=UserDBManager.getInstance().getContactByUserId(bean.getUserId());
            if(user!=null){
                type=user.getType();
                bean.setType(type | EXPERT_INT);
            }else{
                bean.setType(EXPERT_INT);
            }
            UserDBManager.getInstance().saveContact(bean);
        }
        DisplayImageOptions options = UILUtils.getRadiusOption(LocalDisplay.dp2px(4));
        title_name.setText(bean.getNickName());
        his_name.setText(bean.getNickName());
        ImageLoader.getInstance().displayImage(bean.getAvatarSmall(), his_avatar, options);
        his_level.setText("V" + bean.getLevel());
        if(bean.getGender().equals("F")){
            his_gender.setImageResource(R.drawable.girl);
        }else if(bean.getGender().equals("F")){
            his_gender.setImageResource(R.drawable.boy);
        }
        xingzuo.setText(bean.getZodiac());
        his_id.setText(String.valueOf(bean.getUserId()));
        if(!TextUtils.isEmpty(bean.getTravelStatus())){
            his_status.setText(bean.getTravelStatus());
        }
        sign.setText(bean.getSignature());
        his_trip_plan.setText("共"+bean.getGuideCnt()+"篇旅行计划");
        if(bean.getResidence().equals("")||bean.getResidence()==null){
            resident.setText("未设置");
        }else{
        resident.setText(bean.getResidence());
        }
        if(bean.getBirthday()==null){
            age.setText("未设置");
        }else{
        age.setText(getAge(bean.getBirthday())+"");
        }

        if(user!=null) {
            if (userId != 10000 && UserDBManager.getInstance().isMyFriend(bean.getUserId())) {
                tv_del.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showActionDialog();
                    }
                });
            } else {
                tv_del.setVisibility(View.GONE);
            }


            if (UserDBManager.getInstance().isMyFriend(bean.getUserId())) {
                add_friend.setText("开始聊天");
                add_friend.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                   /* Intent intent = new Intent(HisMainPageActivity.this, ContactDetailActivity.class);
                    intent.putExtra("userId", (long)bean.get(0).userId);
                    intent.putExtra("userNick", bean.get(0).nickName);
                    startActivity(intent);*/

                        if (user != null) { //&&!TextUtils.isEmpty(user.easemobUser)
                            User imUser = UserDBManager.getInstance().getContactByUserId(bean.getUserId());
                            Intent intent = new Intent(mContext, ChatActivity.class);
                            intent.putExtra("friend_id", String.valueOf(imUser.getUserId()));
                            intent.putExtra("chatType", "single");
                            startActivity(intent);
                            finish();
                        } else {
                            Intent intent = new Intent(HisMainPageActivity.this, LoginActivity.class);
                            startActivity(intent);
                            overridePendingTransition(R.anim.push_bottom_in, 0);
                            finish();
                        }
                    }
                });

            } else {
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
                        if (user != null) { // && !TextUtils.isEmpty(user.easemobUser)
                            final PeachEditDialog editDialog = new PeachEditDialog(mContext);
                            editDialog.setTitle("输入验证信息");
                            editDialog.setMessage(String.format("\"Hi, 我是%s\"", AccountManager.getInstance().getLoginAccount(HisMainPageActivity.this).getNickName()));
                            editDialog.setPositiveButton("确定", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    editDialog.dismiss();
                                    DialogManager.getInstance().showLoadingDialog(HisMainPageActivity.this);
                                    UserApi.requestAddContact(bean.getUserId() + "", editDialog.getMessage(), new HttpCallBack() {
                                        @Override
                                        public void doSuccess(Object result, String method) {
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
                        } else {
                            Intent intent = new Intent(HisMainPageActivity.this, LoginActivity.class);
                            startActivity(intent);
                            overridePendingTransition(R.anim.push_bottom_in, 0);
                            finish();
                        }
                    }
                });
            }
        }else{
            tv_del.setVisibility(View.GONE);
            add_friend.setText("加为好友");
            add_friend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        Intent intent = new Intent(HisMainPageActivity.this, LoginActivity.class);
                    startActivity(intent);
                        overridePendingTransition(R.anim.push_bottom_in, 0);
                    finish();
                }
            });
        }

        try {
            int countries=0;
            int citys;
            JSONObject jsonObject = new JSONObject(bean.getTracks().toString());
            Iterator iterator=jsonObject.keys();
            while(iterator.hasNext()){
                countries++;
                String key=(String)iterator.next();
                for(int i=0;i<bean.getTracks().get(key).size();i++){
                    all_foot_print_list.add(bean.getTracks().get(key).get(i));
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
            String birthType = birth.substring(0, 4).toString();
            int birthYear = Integer.parseInt(birthType);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
            String date = sdf.format(new java.util.Date());
            age = Integer.parseInt(date) - birthYear;
        return age;
    }

    public void getUserInfo(int userid){
        DialogManager.getInstance().showModelessLoadingDialog(mContext);
        UserApi.getUserInfo(String.valueOf(userid), new HttpCallBack<String>() {
            @Override
            public void doSuccess(String result, String method) {
                DialogManager.getInstance().dissMissModelessLoadingDialog();
                CommonJson<User> expertInfo = CommonJson.fromJson(result, User.class);
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
            public void doSuccess(String result, String method) {
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(result);
                    if (jsonObject.getInt("code") == 0) {
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
