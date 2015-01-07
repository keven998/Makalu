package com.aizou.peachtravel.module.my;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
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
import com.aizou.peachtravel.common.dialog.CustomProgressDialog;
import com.aizou.peachtravel.common.dialog.DialogManager;
import com.aizou.peachtravel.common.dialog.PeachMessageDialog;
import com.aizou.peachtravel.common.gson.CommonJson;
import com.aizou.peachtravel.common.utils.CommonUtils;
import com.aizou.peachtravel.common.utils.PathUtils;
import com.aizou.peachtravel.common.utils.SelectPicUtils;
import com.aizou.peachtravel.common.widget.TitleHeaderBar;
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

    @ViewInject(R.id.tv_nickname)
    private TextView nickNameTv;
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
    private File cameraFile;
    private PeachUser user;
    DisplayImageOptions options;
    private TextView tvGender;

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
        tvGender = (TextView) findViewById(R.id.tv_gender);
        findViewById(R.id.ll_avatar).setOnClickListener(this);
        findViewById(R.id.ll_nickname).setOnClickListener(this);
        findViewById(R.id.ll_sign).setOnClickListener(this);
        findViewById(R.id.ll_gender).setOnClickListener(this);
        findViewById(R.id.ll_modify_pwd).setOnClickListener(this);
        findViewById(R.id.ll_bind_phone).setOnClickListener(this);
        logoutBtn.setOnClickListener(this);

        TitleHeaderBar titleBar = (TitleHeaderBar) findViewById(R.id.ly_header_bar_title_wrap);
        titleBar.getTitleTextView().setText("个人信息");
        titleBar.enableBackKey(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    private void bindView(PeachUser user){
        nickNameTv.setText(user.nickName);
        tvGender.setText(user.getGenderDesc());
        options = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.avatar_placeholder)
                .showImageOnFail(R.drawable.avatar_placeholder)
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisc(true)
                        // 设置下载的图片是否缓存在SD卡中
                .displayer(
                        new RoundedBitmapDisplayer(LocalDisplay.dp2px(
                                25))) // 设置成圆角图片
                .build();
        ImageLoader.getInstance().displayImage(user.avatar, avatarIv,
                options);
        idTv.setText(user.userId + "");
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
                Intent nickNameIntent = new Intent(mContext, ModifyNicknameActivity.class);
                startActivity(nickNameIntent);
                break;

            case R.id.ll_sign:
                Intent signIntent = new Intent(mContext, ModifySignActivity.class);
                startActivity(signIntent);
                break;

            case R.id.ll_gender:
                showSelectGenderDialog();
                break;

            case R.id.ll_avatar:
                showSelectPicDialog();
                break;

            case R.id.ll_modify_pwd:
                Intent modifyPwdIntent = new Intent(mContext, ModifyPwdActivity.class);
                startActivity(modifyPwdIntent);
                break;

            case R.id.ll_bind_phone:
                Intent bindPhoneIntent = new Intent(mContext, PhoneBindActivity.class);
                startActivity(bindPhoneIntent);
                break;

            case R.id.btn_logout:
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
        dialog.setTitleIcon(R.drawable.ic_dialog_tip);
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
                cameraFile = SelectPicUtils.getInstance().selectPicFromCamera(AccountActvity.this);
                dialog.dismiss();

            }
        });
        localBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                SelectPicUtils.getInstance().selectPicFromLocal(AccountActvity.this);
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
                    tvGender.setText(user.getGenderDesc());
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
        final AlertDialog dialog = new AlertDialog.Builder(this).create();
        View contentView = View.inflate(this, R.layout.dialog_select_gender, null);
        Button ladyBtn = (Button) contentView.findViewById(R.id.gender_lady);
        Button manBtn = (Button) contentView.findViewById(R.id.gender_man);
        Button unknown = (Button) contentView.findViewById(R.id.gender_unknown);
        Button cancel = (Button) contentView.findViewById(R.id.btn_cancle);
        ladyBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
                modifyGender("F");
            }
        });
        manBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
                modifyGender("M");

            }
        });
        unknown.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
                modifyGender("U");
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();

            }
        });
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SelectPicUtils.REQUEST_CODE_CAMERA) { // 发送照片
            if (cameraFile != null && cameraFile.exists()) {
                SelectPicUtils.getInstance().startPhotoZoom(this, Uri.fromFile(cameraFile));

            }
        } else if (requestCode == SelectPicUtils.REQUEST_CODE_LOCAL) {
            if (data != null) {
                Uri selectedImage = data.getData();
                if (selectedImage != null) {
                    File file = SelectPicUtils.getPicFormUri(this, selectedImage);
                    SelectPicUtils.getInstance().startPhotoZoom(this, Uri.fromFile(file));

                }
            }
        } else if (requestCode == SelectPicUtils.REQUEST_CODE_ZOOM) {
            if (data == null) return;
            final Uri zoomImage = data.getData();
            final File file = SelectPicUtils.getPicFormUri(this, zoomImage);
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
                                                user.avatar = imageUrl;
                                                AccountManager.getInstance().saveLoginAccount(mContext, user);
                                                ImageLoader.getInstance().displayImage(zoomImage.toString(), avatarIv, options);
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
    }

    @Override
    public void finish() {
        super.finish();
//        overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
    }
}
