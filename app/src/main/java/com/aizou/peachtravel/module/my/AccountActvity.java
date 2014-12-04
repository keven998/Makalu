package com.aizou.peachtravel.module.my;

import android.accounts.Account;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.aizou.core.dialog.ToastUtil;
import com.aizou.core.http.HttpCallBack;
import com.aizou.core.log.LogUtil;
import com.aizou.core.utils.BitmapTools;
import com.aizou.core.utils.LocalDisplay;
import com.aizou.peachtravel.R;
import com.aizou.peachtravel.base.PeachBaseActivity;
import com.aizou.peachtravel.bean.PeachUser;
import com.aizou.peachtravel.bean.UploadTokenBean;
import com.aizou.peachtravel.common.account.AccountManager;
import com.aizou.peachtravel.common.api.OtherApi;
import com.aizou.peachtravel.common.gson.CommonJson;
import com.aizou.peachtravel.common.utils.PathUtils;
import com.aizou.peachtravel.common.utils.SelectPicUtils;
import com.aizou.peachtravel.common.widget.TitleHeaderBar;
import com.aizou.peachtravel.config.Constant;
import com.aizou.peachtravel.config.hxconfig.PeachHXSDKHelper;
import com.easemob.EMCallBack;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

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

    }

    private void initView() {
        setContentView(R.layout.activity_account);
        ViewUtils.inject(this);
        tvGender = (TextView)findViewById(R.id.tv_gender);
        findViewById(R.id.ll_avatar).setOnClickListener(this);
        findViewById(R.id.ll_nickname).setOnClickListener(this);
        findViewById(R.id.ll_sign).setOnClickListener(this);
        findViewById(R.id.ll_gender).setOnClickListener(this);
        findViewById(R.id.ll_modify_pwd).setOnClickListener(this);
        findViewById(R.id.ll_bind_phone).setOnClickListener(this);
        logoutBtn.setOnClickListener(this);

        TitleHeaderBar titleBar = (TitleHeaderBar)findViewById(R.id.ly_header_bar_title_wrap);
        titleBar.getTitleTextView().setText("个人信息");
        titleBar.enableBackKey(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    private void initData() {
        user = AccountManager.getInstance().getLoginAccount(this);
        nickNameTv.setText(user.nickName);
        tvGender.setText(user.gender);
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
        idTv.setText(user.userId+"");
        signTv.setText(user.signature);
        phoneTv.setText(user.tel);

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

    private void warnLogout() {
        new MaterialDialog.Builder(this)

                .title(null)
                .content("确定退出已登陆账号")
                .theme(Theme.LIGHT)  // the default is light, so you don't need this line
                .positiveText("确定")
                .negativeText("取消")
                .callback(new MaterialDialog.Callback() {
                    @Override
                    public void onPositive(final MaterialDialog dialog) {
                        View progressView = View.inflate(mContext,R.layout.view_progressbar,null);
                        dialog.setContentView(progressView);
                        AccountManager.getInstance().logout(mContext, new EMCallBack() {
                            @Override
                            public void onSuccess() {
                                dialog.dismiss();
                                finish();

                            }

                            @Override
                            public void onError(int i, String s) {
                                ToastUtil.getInstance(AccountActvity.this).showToast("退出失败，请重试");
                                dialog.dismiss();

                            }

                            @Override
                            public void onProgress(int i, String s) {

                            }
                        });

                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        dialog.dismiss();
                    }
                })
                .show();
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
                tvGender.setText(((Button)v).getText());
                dialog.dismiss();
            }
        });
        manBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                tvGender.setText(((Button)v).getText());
                dialog.dismiss();

            }
        });
        unknown.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                tvGender.setText(((Button)v).getText());
                dialog.dismiss();

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
            Bundle extras = data.getExtras();
            if (extras != null){
                Bitmap photo = extras.getParcelable("data");
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                photo.compress(Bitmap.CompressFormat.JPEG, 75, stream);
                String fileName = PathUtils.getInstance().getLocalImageCachePath()+"/"+user.userId+"_avatar.jpg";
                try {
                    final File imageFile = BitmapTools.saveBitmap(fileName,photo);
                    OtherApi.getAvatarUploadToken(new HttpCallBack<String>() {
                        @Override
                        public void doSucess(String result, String method) {
                            CommonJson<UploadTokenBean> tokenResult = CommonJson.fromJson(result,UploadTokenBean.class);
                            if(tokenResult.code==0){
                                String token = tokenResult.result.uploadToken;
                                String key = tokenResult.result.key;
                                UploadManager uploadManager = new UploadManager();
                                uploadManager.put(imageFile, key, token,
                                        new UpCompletionHandler() {
                                            @Override
                                            public void complete(String key, ResponseInfo info, JSONObject response) {
                                                if(info.isOK()){
                                                    LogUtil.d(response.toString());
                                                    try {
                                                        String imageUrl = response.getString("url");
                                                        user.avatar = imageUrl;
                                                        AccountManager.getInstance().saveLoginAccount(mContext,user);
                                                        ImageLoader.getInstance().displayImage(user.avatar,avatarIv, options);
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }

                                                }

                                            }
                                        }, null);
                            }
                        }

                        @Override
                        public void doFailure(Exception error, String msg, String method) {

                        }
                    });

                } catch (IOException e) {
                    e.printStackTrace();
                }
                Toast.makeText(mContext, "上传图片", Toast.LENGTH_SHORT).show();
            }

        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
    }
}
