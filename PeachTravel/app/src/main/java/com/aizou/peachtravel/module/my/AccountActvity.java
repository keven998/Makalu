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
import android.widget.Toast;

import com.aizou.core.utils.LocalDisplay;
import com.aizou.peachtravel.R;
import com.aizou.peachtravel.base.PeachBaseActivity;
import com.aizou.peachtravel.bean.PeachUser;
import com.aizou.peachtravel.common.account.AccountManager;
import com.aizou.peachtravel.common.utils.SelectPicUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

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
    @ViewInject(R.id.btn_logout)
    private Button logoutBtn;
    private File cameraFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        initView();
        initData();
    }

    private void initView() {
        setContentView(R.layout.activity_account);
        ViewUtils.inject(this);
        findViewById(R.id.ll_avatar).setOnClickListener(this);
        findViewById(R.id.ll_nickname).setOnClickListener(this);
        findViewById(R.id.ll_sign).setOnClickListener(this);
        findViewById(R.id.ll_gender).setOnClickListener(this);
        findViewById(R.id.ll_modify_pwd).setOnClickListener(this);
        findViewById(R.id.ll_bind_phone).setOnClickListener(this);
        logoutBtn.setOnClickListener(this);

    }

    private void initData() {
        PeachUser user = AccountManager.getInstance().getLoginAccountFromPref(this);
        nickNameTv.setText(user.nickName);
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisc(true)
                        // 设置下载的图片是否缓存在SD卡中
                .displayer(
                        new RoundedBitmapDisplayer(LocalDisplay.dp2px(
                                25))) // 设置成圆角图片
                .build();
        ImageLoader.getInstance().displayImage(user.avatar, avatarIv,
                options);
        idTv.setText(user.userId);
        signTv.setText(user.signature);
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
                Intent genderIntent = new Intent(mContext, ModifyGenderActivity.class);
                startActivity(genderIntent);
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

            case R.id.btn_logout:
                AccountManager.getInstance().logout(this);
                finish();
                break;
        }
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
                cameraFile = SelectPicUtils.getInstance().selectPicFromCamera(mContext);
                dialog.dismiss();

            }
        });
        localBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                SelectPicUtils.getInstance().selectPicFromLocal(mContext);
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
            Toast.makeText(mContext, "上传图片", Toast.LENGTH_SHORT).show();
        }
    }
}
