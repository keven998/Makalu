package com.aizou.peachtravel.module.toolbox.im;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import com.aizou.core.http.HttpCallBack;
import com.aizou.peachtravel.R;
import com.aizou.peachtravel.base.BaseChatActivity;
import com.aizou.peachtravel.bean.PeachUser;
import com.aizou.peachtravel.common.account.AccountManager;
import com.aizou.peachtravel.common.api.UserApi;
import com.aizou.peachtravel.common.gson.CommonJson;
import com.aizou.peachtravel.common.utils.IMUtils;
import com.aizou.peachtravel.common.utils.UILUtils;
import com.aizou.peachtravel.common.widget.BlurDialogMenu.FastBlurHelper;
import com.aizou.peachtravel.common.widget.TitleHeaderBar;
import com.aizou.peachtravel.db.IMUser;
import com.aizou.peachtravel.db.respository.IMUserRepository;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

/**
 * Created by Rjm on 2014/10/29.
 */
public class ContactDetailActivity extends BaseChatActivity {
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
    private long userId;
    private IMUser imUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_detail);
        initTitleBar();
        ViewUtils.inject(this);
        userId = getIntent().getLongExtra("userId", 0);
        imUser = IMUserRepository.getContactByUserId(mContext, userId);
        if (imUser != null) {
            bindView();
        }

        UserApi.getUserInfo(userId + "", new HttpCallBack<String>() {
            @Override
            public void doSucess(String result, String method) {
                CommonJson<PeachUser> userResult = CommonJson.fromJson(result, PeachUser.class);
                if (userResult.code == 0) {
                    PeachUser user = userResult.result;
                    imUser = AccountManager.getInstance().getContactList(mContext).get(user.easemobUser);
                    if(imUser!=null){
                        imUser.setNick(user.nickName);
                        imUser.setAvatar(user.avatar);
                        imUser.setSignature(user.signature);
                        imUser.setMemo(user.memo);
                        imUser.setGender(user.gender);
                        IMUtils.setUserHead(imUser);
                        AccountManager.getInstance().getContactList(mContext).put(imUser.getUsername(), imUser);
                        IMUserRepository.saveContact(mContext, imUser);
                        bindView();
                    }
                }
            }

            @Override
            public void doFailure(Exception error, String msg, String method) {

            }
        });
    }

    private void initTitleBar() {
        final TitleHeaderBar titleHeaderBar = (TitleHeaderBar) findViewById(R.id.ly_header_bar_title_wrap);
        titleHeaderBar.setRightViewImageRes(R.drawable.add);
        titleHeaderBar.getRightTextView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        titleHeaderBar.getTitleTextView().setText("好友信息");
        titleHeaderBar.enableBackKey(true);
    }

    private void bindView() {
        ImageLoader.getInstance().displayImage(imUser.getAvatar(), avatarIv, UILUtils.getDefaultOption(), new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String s, View view) {

            }

            @Override
            public void onLoadingFailed(String s, View view, FailReason failReason) {

            }

            @Override
            public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                final ImageView bigHeader = (ImageView)findViewById(R.id.big_avatar);
                bigHeader.setImageBitmap(bitmap);

                bigHeader.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        bigHeader.getViewTreeObserver().removeOnPreDrawListener(this);
                        bigHeader.buildDrawingCache();

                        Bitmap bmp = bigHeader.getDrawingCache();
                        blur(bmp, bigHeader);
                        return true;
                    }
                });
            }

            @Override
            public void onLoadingCancelled(String s, View view) {

            }
        });
        nickNameTv.setText("昵称：" + imUser.getNick());
        idTv.setText("桃号：" + imUser.getUserId());
        signTv.setText("签名：" + imUser.getSignature());
    }

    public void startChat(View view) {
        startActivity(new Intent(mContext, ChatActivity.class).putExtra("userId", imUser.getUsername()));

    }

    private void blur(Bitmap bkg, ImageView iv) {
        long startMs = System.currentTimeMillis();
        float scaleFactor = 1;
        float radius = 20;

        Bitmap overlay = FastBlurHelper.doBlur(bkg, (int) radius, true);
        iv.setImageBitmap(overlay);
    }
}
