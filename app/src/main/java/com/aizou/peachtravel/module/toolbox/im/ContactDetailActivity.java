package com.aizou.peachtravel.module.toolbox.im;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import com.aizou.core.dialog.DialogManager;
import com.aizou.core.dialog.ToastUtil;
import com.aizou.core.http.HttpCallBack;
import com.aizou.core.utils.LocalDisplay;
import com.aizou.peachtravel.R;
import com.aizou.peachtravel.base.ChatBaseActivity;
import com.aizou.peachtravel.bean.ModifyResult;
import com.aizou.peachtravel.bean.PeachUser;
import com.aizou.peachtravel.common.account.AccountManager;
import com.aizou.peachtravel.common.api.UserApi;
import com.aizou.peachtravel.common.gson.CommonJson;
import com.aizou.peachtravel.common.utils.IMUtils;
import com.aizou.peachtravel.common.widget.BlurDialogMenu.FastBlurHelper;
import com.aizou.peachtravel.common.widget.BlurDialogMenu.SupportBlurDialogFragment;
import com.aizou.peachtravel.common.widget.TitleHeaderBar;
import com.aizou.peachtravel.db.IMUser;
import com.aizou.peachtravel.db.respository.IMUserRepository;
import com.aizou.peachtravel.db.respository.InviteMsgRepository;
import com.easemob.chat.EMChatManager;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

/**
 * Created by Rjm on 2014/10/29.
 */
public class ContactDetailActivity extends ChatBaseActivity {
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
        ViewUtils.inject(this);
        initTitleBar();
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
                ToastUtil.getInstance(ContactDetailActivity.this).showToast(getResources().getString(R.string.request_network_failed));
            }
        });
    }

    private void initTitleBar() {
        final TitleHeaderBar titleHeaderBar = (TitleHeaderBar) findViewById(R.id.ly_header_bar_title_wrap);
        titleHeaderBar.setRightViewImageRes(R.drawable.ic_more);
        titleHeaderBar.getRightTextView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ContactDetailMenu fragment = new ContactDetailMenu();
                Bundle args = new Bundle();
                args.putInt(SupportBlurDialogFragment.BUNDLE_KEY_BLUR_RADIUS, 2);
                args.putFloat(SupportBlurDialogFragment.BUNDLE_KEY_DOWN_SCALE_FACTOR, 3);
                args.putSerializable("imUser",imUser);
                fragment.setArguments(args);
                fragment.show(getSupportFragmentManager(), "contact_detail_menu");
            }
        });

        titleHeaderBar.getTitleTextView().setText("桃友信息");
        titleHeaderBar.enableBackKey(true);
    }

    private void bindView() {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .showImageForEmptyUri(R.drawable.avatar_placeholder)
                .showImageOnFail(R.drawable.avatar_placeholder)
                .cacheOnDisc(true)
                        // 设置下载的图片是否缓存在SD卡中
                .displayer(new RoundedBitmapDisplayer(LocalDisplay.dp2px(62))) // 设置成圆角图片
                .build();

        ImageLoader.getInstance().displayImage(imUser.getAvatar(), avatarIv, options, new ImageLoadingListener() {
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

        if (imUser.getGender().equalsIgnoreCase("M")) {
            genderIv.setImageResource(R.drawable.ic_gender_man);
        } else if (imUser.getGender().equalsIgnoreCase("F")) {
            genderIv.setImageResource(R.drawable.ic_gender_lady);
        } else {
            genderIv.setImageResource(R.drawable.avatar_placeholder);
        }

        nickNameTv.setText("昵称：" + imUser.getNick());
        idTv.setText("桃号：" + imUser.getUserId());
        signTv.setText("旅行签名：" + imUser.getSignature());
    }

    public void startChat(View view) {
        startActivity(new Intent(mContext, ChatActivity.class).putExtra("userId", imUser.getUsername()));
        overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
//        finish();
    }

    private void blur(Bitmap bkg, ImageView iv) {
        long startMs = System.currentTimeMillis();
        float scaleFactor = 1;
        float radius = 20;

        Bitmap overlay = FastBlurHelper.doBlur(bkg, (int) radius, true);
        iv.setImageBitmap(overlay);
    }

    public static class ContactDetailMenu extends SupportBlurDialogFragment {
        private IMUser mImUser;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            mImUser = (IMUser) getArguments().getSerializable("imUser");

        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            Dialog connectionDialog = new Dialog(getActivity(), R.style.TransparentDialog);
            View customView = getActivity().getLayoutInflater().inflate(R.layout.menu_contact_detail, null);
            connectionDialog.setContentView(customView);
            customView.findViewById(R.id.delete_contact);
            customView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    deleteContact(mImUser);

                }
            });

            return connectionDialog;
        }

        /**
         * 删除联系人
         *
         * @param tobeDeleteUser
         */
        public void deleteContact(final IMUser tobeDeleteUser) {
            DialogManager.getInstance().showProgressDialog(getActivity(),"正在删除...");
            UserApi.deleteContact(tobeDeleteUser.getUserId()+"",new HttpCallBack() {
                @Override
                public void doSucess(Object result, String method) {
                    DialogManager.getInstance().dissMissProgressDialog();
                    CommonJson<ModifyResult> deleteResult = CommonJson.fromJson((String) result, ModifyResult.class);
                    if(deleteResult.code==0){
                        IMUserRepository.deleteContact(getActivity(), tobeDeleteUser.getUsername());
                        // 删除此会话
                        EMChatManager.getInstance().deleteConversation(tobeDeleteUser.getUsername(),true);
                        AccountManager.getInstance().getContactList(getActivity()).remove(tobeDeleteUser.getUsername());
                        InviteMsgRepository.deleteInviteMsg(getActivity(),tobeDeleteUser.getUsername());
                        dismiss();
                        getActivity().finish();
                    }else if(!TextUtils.isEmpty(deleteResult.err.message)){
                        ToastUtil.getInstance(getActivity()).showToast(deleteResult.err.message);
                    }

                }

                @Override
                public void doFailure(Exception error, String msg, String method) {
                    DialogManager.getInstance().dissMissProgressDialog();
//                    ToastUtil.getInstance(getActivity()).showToast("删除失败");
                    ToastUtil.getInstance(getActivity()).showToast(getResources().getString(R.string.request_network_failed));
                }
            });

        }
    }


}
