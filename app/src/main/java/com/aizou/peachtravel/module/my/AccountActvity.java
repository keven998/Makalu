package com.aizou.peachtravel.module.my;

import android.accounts.Account;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.DatePicker;
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
import com.aizou.peachtravel.bean.ModifyResult;
import com.aizou.peachtravel.bean.PeachUser;
import com.aizou.peachtravel.bean.UploadTokenBean;
import com.aizou.peachtravel.common.account.AccountManager;
import com.aizou.peachtravel.common.api.OtherApi;
import com.aizou.peachtravel.common.api.UserApi;
import com.aizou.peachtravel.common.dialog.CustomLoadingDialog;
import com.aizou.peachtravel.common.dialog.DialogManager;
import com.aizou.peachtravel.common.dialog.MoreDialog;
import com.aizou.peachtravel.common.dialog.PeachMessageDialog;
import com.aizou.peachtravel.common.gson.CommonJson;
import com.aizou.peachtravel.common.utils.CommonUtils;
import com.aizou.peachtravel.common.utils.SelectPicUtils;
import com.aizou.peachtravel.common.utils.video.Utils;
import com.aizou.peachtravel.common.widget.FlowLayout;
import com.aizou.peachtravel.common.widget.TitleHeaderBar;
import com.aizou.peachtravel.module.MainActivity;
import com.easemob.EMCallBack;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UpProgressHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.UploadOptions;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.net.utils.UResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

/**
 * Created by Rjm on 2014/10/11.
 */
public class AccountActvity extends PeachBaseActivity implements View.OnClickListener {


    @ViewInject(R.id.iv_avatar)
    private ImageView avatarIv;
    @ViewInject(R.id.iv_gender)
    private ImageView genderIv;

    @ViewInject(R.id.tv_gender)
    private TextView genderTv;
    @ViewInject(R.id.tv_resident)
    private TextView residentTv;
    @ViewInject(R.id.tv_brithday)
    private TextView brithdayTv;
    @ViewInject(R.id.tv_profession)
    private TextView professinoTv;
    @ViewInject(R.id.all_pics_sv)
    private HorizontalScrollView all_pics;
    @ViewInject(R.id.my_destination)
    private FlowLayout my_destination;

    @ViewInject(R.id.tv_nickname)
    private TextView nickNameTv;
    @ViewInject(R.id.tv_status)
    private TextView status;
    @ViewInject(R.id.tv_id)
    private TextView idTv;
    @ViewInject(R.id.tv_sign)
    private TextView signTv;
    @ViewInject(R.id.ll_modify_pwd)
    private TextView modifPwdLl;
    @ViewInject(R.id.btn_logout)
    private Button logoutBtn;
    @ViewInject(R.id.tv_bind_phone)
    private TextView bindPhoneTv;
    @ViewInject(R.id.tv_phone)
    private TextView phoneTv;
    private File tempImage;
    private PeachUser user;
    DisplayImageOptions options;
    private TextView tvGender;

    private int RESIDENT=1;
    private int STATUS=2;
    private int SEX=3;
    private ImageView my_pics_cell;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setAccountAbout(true);
        super.onCreate(savedInstanceState);
        initView();
        refreshUserInfo();

    }

    private void initView() {
        setContentView(R.layout.activity_account);
        ViewUtils.inject(this);
        //tvGender = (TextView) findViewById(R.id.tv_gender);
        //findViewById(R.id.ll_avatar).setOnClickListener(this);
        findViewById(R.id.ll_nickname).setOnClickListener(this);
        findViewById(R.id.ll_status).setOnClickListener(this);

        findViewById(R.id.ll_sign).setOnClickListener(this);
        findViewById(R.id.ll_gender).setOnClickListener(this);
        findViewById(R.id.ll_birthday).setOnClickListener(this);
        findViewById(R.id.ll_resident).setOnClickListener(this);
        findViewById(R.id.ll_profession).setOnClickListener(this);

        findViewById(R.id.ll_modify_pwd).setOnClickListener(this);
        findViewById(R.id.ll_bind_phone).setOnClickListener(this);
        findViewById(R.id.ll_foot_print).setOnClickListener(this);

        logoutBtn.setOnClickListener(this);

        initScrollView();
        initFlDestion();

        TitleHeaderBar titleBar = (TitleHeaderBar) findViewById(R.id.ly_header_bar_title_wrap);
        titleBar.getTitleTextView().setText("我");
        titleBar.enableBackKey(true);
    }

    public void initFlDestion(){
        my_destination.removeAllViews();
        String[] names={"美国","日本","澳大利亚","乌兹别克斯坦","墨西哥"};
        for(int j=0;j<5;j++){
            View contentView = View.inflate(AccountActvity.this, R.layout.des_text_style2, null);
            final TextView cityNameTv = (TextView) contentView.findViewById(R.id.tv_cell_name);
            cityNameTv.setText(names[j]);
            my_destination.addView(contentView);
        }
    }

    public void initScrollView(){
        all_pics.removeAllViews();
        LinearLayout llPics=new LinearLayout(this);
        llPics.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        llPics.removeAllViews();
        for(int i=0;i<3;i++){
            View view=View.inflate(AccountActvity.this,R.layout.my_all_pics_cell,null);
            my_pics_cell=(ImageView)view.findViewById(R.id.my_pics_cell);
            if(i==2){
                my_pics_cell.setImageResource(R.drawable.smiley_add_btn);
                my_pics_cell.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ToastUtil.getInstance(AccountActvity.this).showToast("添加图片");
                    }
                });
            }
            else{
                my_pics_cell.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ToastUtil.getInstance(AccountActvity.this).showToast("show pics");
                    }
                });

            }
            llPics.addView(view);
        }
        all_pics.addView(llPics);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
//        MobclickAgent.onPageStart("page_personal_profile");
    }


    @Override
    protected void onPause() {
        super.onPause();
//        MobclickAgent.onPageEnd("page_personal_profile");
    }
    private void bindView(PeachUser user){
        nickNameTv.setText(user.nickName);
        genderTv.setText(user.getGenderDesc());
        options = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.avatar_placeholder_round)
                .showImageOnFail(R.drawable.avatar_placeholder_round)
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisc(true)
                        // 设置下载的图片是否缓存在SD卡中
                .displayer(
                        new RoundedBitmapDisplayer(LocalDisplay.dp2px(
                                25))) // 设置成圆角图片
                .build();
       /* ImageLoader.getInstance().displayImage(user.avatarSmall, avatarIv,
                options);*/
       /* idTv.setText(user.userId + "");*/
        signTv.setText(user.signature);
        phoneTv.setText(user.tel);
    }

    private void initData() {
        user = AccountManager.getInstance().getLoginAccount(this);
        bindView(user);


//        if(TextUtils.isEmpty(user.tel)){
//            modifPwdLl.setVisibility(View.GONE);
//            bindPhoneTv.setText("绑定手机");
//        }else{
//            modifPwdLl.setVisibility(View.VISIBLE);
//            bindPhoneTv.setText("更改手机");
//            phoneTv.setText(user.tel);
//        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_nickname:
                MobclickAgent.onEvent(mContext,"event_update_nick");
                Intent nickNameIntent = new Intent(mContext, ModifyNicknameActivity.class);
                startActivity(nickNameIntent);
                break;

            case R.id.ll_sign:
                MobclickAgent.onEvent(mContext,"event_update_memo");
                Intent signIntent = new Intent(mContext, ModifySignActivity.class);
                startActivity(signIntent);
                break;

            case R.id.ll_gender:
                MobclickAgent.onEvent(mContext,"event_update_gender");
                //showSelectGenderDialog();
                Intent sexIntent = new Intent(mContext, ModifyStatusOrSexActivity.class);
                sexIntent.putExtra("type","sex");
                startActivityForResult(sexIntent, SEX);
                break;


            case R.id.ll_status:
                Intent statusIntent = new Intent(mContext, ModifyStatusOrSexActivity.class);
                statusIntent.putExtra("type","status");
                startActivityForResult(statusIntent, STATUS);
                break;

            /*case R.id.ll_avatar:
                MobclickAgent.onEvent(mContext,"event_update_avatar");
                showSelectPicDialog();
                break;*/

            case R.id.ll_foot_print:
                ToastUtil.getInstance(AccountActvity.this).showToast("旅行足迹");
                Intent intent=new Intent(AccountActvity.this,MyFootPrinterActivity.class);
                startActivity(intent);
                break;

            case R.id.ll_birthday:
                DatePickerDialog dialog = new DatePickerDialog(mContext,new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                          brithdayTv.setText(year+"-"+monthOfYear+"-"+dayOfMonth);
                    }
                },1990,0,0);
                dialog.setCancelable(true);
                dialog.show();
                //应该还差个上传动作
                break;


            case R.id.ll_resident:
                Intent residentIntent = new Intent(mContext, SelectResidentActivity.class);
                startActivityForResult(residentIntent,RESIDENT);
                overridePendingTransition(R.anim.fade_in,0);
                break;


            case R.id.ll_modify_pwd:
                MobclickAgent.onEvent(mContext,"event_update_password");
                Intent modifyPwdIntent = new Intent(mContext, ModifyPwdActivity.class);
                startActivity(modifyPwdIntent);
                break;

            case R.id.ll_bind_phone:
                MobclickAgent.onEvent(mContext,"event_update_phone");
                Intent bindPhoneIntent = new Intent(mContext, PhoneBindActivity.class);
                startActivity(bindPhoneIntent);
                break;

            case R.id.btn_logout:
                MobclickAgent.onEvent(mContext,"event_logout");
                warnLogout();
                break;
        }
    }


    private void refreshUserInfo(){
        PeachUser user = AccountManager.getInstance().getLoginAccount(this);
        if(user!=null){
            UserApi.getUserInfo(user.userId + "", new HttpCallBack<String>() {
                @Override
                public void doSucess(String result, String method) {
                    CommonJson<PeachUser> userResult = CommonJson.fromJson(result, PeachUser.class);
                    if (userResult.code == 0) {
                        AccountManager.getInstance().saveLoginAccount(mContext, userResult.result);
                        bindView(userResult.result);
                    }

                }

                @Override
                public void doFailure(Exception error, String msg, String method) {

                }
            });
        }
    }

    private void warnLogout() {
        final PeachMessageDialog dialog = new PeachMessageDialog(mContext);
        dialog.setTitle("提示");
       // dialog.setTitleIcon(R.drawable.ic_dialog_tip);
        dialog.setMessage("确定退出已登陆账号吗？");
        dialog.setPositiveButton("确定",new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                DialogManager.getInstance().showLoadingDialog(mContext,"正在登出");
                AccountManager.getInstance().logout(mContext, false, new EMCallBack() {
                    @Override
                    public void onSuccess() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                DialogManager.getInstance().dissMissLoadingDialog();
                                Intent intent =new Intent(mContext,MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                AccountActvity.this.finish();

                            }
                        });
                    }

                    @Override
                    public void onError(int i, String s) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtil.getInstance(AccountActvity.this).showToast("呃～网络好像找不到了");
                                dialog.dismiss();
                            }
                        });
                    }

                    @Override
                    public void onProgress(int i, String s) {

                    }
                });
            }
        });
        dialog.setNegativeButton("取消",new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();

    }


    private void showSelectPicDialog() {
        final AlertDialog dialog = new AlertDialog.Builder(this).create();
        View contentView = View.inflate(this,
                R.layout.dialog_select_pic, null);
        Button cameraBtn = (Button) contentView
                .findViewById(R.id.btn_camera);
        Button localBtn = (Button) contentView.findViewById(R.id.btn_local);
        Button cancleBtn = (Button) contentView.findViewById(R.id.btn_cancle);
        cameraBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                tempImage = SelectPicUtils.getInstance().selectPicFromCamera(AccountActvity.this);
                dialog.dismiss();

            }
        });
        localBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                tempImage= SelectPicUtils.getInstance().selectZoomPicFromLocal(AccountActvity.this);
                dialog.dismiss();

            }
        });
        cancleBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();

            }
        });
        // dialog.setView(contentView);
        // dialog.setContentView(contentView);
        dialog.show();
        WindowManager windowManager = getWindowManager();
        Window window = dialog.getWindow();
        window.setContentView(contentView);
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = (int) (display.getWidth()); // 设置宽度
        window.setAttributes(lp);
        window.setGravity(Gravity.BOTTOM); // 此处可以设置dialog显示的位置
        window.setWindowAnimations(R.style.SelectPicDialog); // 添加动画
    }

    private void modifyGender(final String gender){
        if(!CommonUtils.isNetWorkConnected(mContext)){
            ToastUtil.getInstance(mContext).showToast("无网络连接，请检查网络");
            return;
        }
        DialogManager.getInstance().showLoadingDialog(mContext, "请稍后");
        UserApi.editUserGender(user, gender, new HttpCallBack<String>() {


            @Override
            public void doSucess(String result, String method) {
                DialogManager.getInstance().dissMissLoadingDialog();
                CommonJson<ModifyResult> modifyResult = CommonJson.fromJson(result,ModifyResult.class);
                if(modifyResult.code==0){
                    user.gender = gender;
                    AccountManager.getInstance().saveLoginAccount(mContext,user);
                    genderTv.setText(user.getGenderDesc());
                    ToastUtil.getInstance(mContext).showToast("修改成功");
                }
            }

            @Override
            public void doFailure(Exception error, String msg, String method) {
                DialogManager.getInstance().dissMissLoadingDialog();
                if (!isFinishing())
                ToastUtil.getInstance(mContext).showToast(getResources().getString(R.string.request_network_failed));
            }

            @Override
            public void onStart() {
            }
        });
    }

    private void showSelectGenderDialog() {
        String[] names={"美女","帅锅","不告诉你"};
        final MoreDialog dialog=new MoreDialog(AccountActvity.this);
        dialog.setMoreStyle(false,3,names);

        /*final AlertDialog dialog = new AlertDialog.Builder(this).create();
        View contentView = View.inflate(this, R.layout.dialog_select_gender, null);
        Button ladyBtn = (Button) contentView.findViewById(R.id.gender_lady);
        Button manBtn = (Button) contentView.findViewById(R.id.gender_man);
        Button unknown = (Button) contentView.findViewById(R.id.gender_unknown);
        Button cancel = (Button) contentView.findViewById(R.id.btn_cancle);*/
        dialog.getTv2().setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
                modifyGender("F");
            }
        });
        dialog.getTv3().setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
                modifyGender("M");

            }
        });
        dialog.getTv4().setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
                modifyGender("U");
            }
        });
        dialog.show();

    }

    private void uploadAvatar(final File file){
        final CustomLoadingDialog progressDialog = DialogManager.getInstance().showLoadingDialog(mContext,"0%");
        OtherApi.getAvatarUploadToken(new HttpCallBack<String>() {
            @Override
            public void doSucess(String result, String method) {
                CommonJson<UploadTokenBean> tokenResult = CommonJson.fromJson(result, UploadTokenBean.class);
                if (tokenResult.code == 0) {
                    String token = tokenResult.result.uploadToken;
                    String key = tokenResult.result.key;
                    UploadManager uploadManager = new UploadManager();
                    uploadManager.put(file, key, token,
                            new UpCompletionHandler() {
                                @Override
                                public void complete(String key, ResponseInfo info, JSONObject response) {
                                    DialogManager.getInstance().dissMissLoadingDialog();
                                    if (info.isOK()) {
                                        LogUtil.d(response.toString());
                                        try {
                                            String imageUrl = response.getString("url");
                                            String urlSmall = response.getString("urlSmall");
                                            user.avatar = imageUrl;
                                            user.avatarSmall = urlSmall;
                                            AccountManager.getInstance().saveLoginAccount(mContext, user);
                                            ImageLoader.getInstance().displayImage(Uri.fromFile(file).toString(), avatarIv, options);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                }
                            }, new UploadOptions(null, null, false,
                                    new UpProgressHandler() {
                                        public void progress(String key, double percent) {
                                            progressDialog.setContent((int) (percent*100)+"%");
                                            LogUtil.d("progress",percent+"");
                                        }
                                    }, null));
                } else {
                    DialogManager.getInstance().dissMissLoadingDialog();
                }
            }

            @Override
            public void doFailure(Exception error, String msg, String method) {
                DialogManager.getInstance().dissMissLoadingDialog();
                if (!isFinishing())
                    ToastUtil.getInstance(AccountActvity.this).showToast(getResources().getString(R.string.request_network_failed));
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode!=RESULT_OK){
            return;
        }
        if (requestCode == SelectPicUtils.REQUEST_CODE_CAMERA) { // 发送照片
            if (tempImage != null && tempImage.exists()) {
                SelectPicUtils.getInstance().startPhotoZoom(this, Uri.fromFile(tempImage));

            }
        } else if (requestCode == SelectPicUtils.REQUEST_CODE_LOCAL_ZOOM) {
            if(tempImage!=null){
                uploadAvatar(tempImage);
            }
        } else if (requestCode == SelectPicUtils.REQUEST_CODE_ZOOM) {
            if(tempImage!=null){
               uploadAvatar(tempImage);

            }
        } else if (requestCode == RESIDENT){
            residentTv.setText(data.getExtras().getString("result"));
        } else if (requestCode == SEX){
            String sex=data.getExtras().getString("result");
            if(sex.equals("美女")){modifyGender("F");}
            else if(sex.equals("帅锅")){modifyGender("M");}
            else if(sex.equals("一言难尽")){modifyGender("U");}
            else if(sex.equals("保密")){modifyGender("S");}

        } else if (requestCode == STATUS){
            status.setText(data.getExtras().getString("result"));
        }
    }

    @Override
    public void finish() {
        super.finish();
//        overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
    }
}
