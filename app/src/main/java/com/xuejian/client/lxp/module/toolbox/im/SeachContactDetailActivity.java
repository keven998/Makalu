package com.xuejian.client.lxp.module.toolbox.im;


import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.aizou.core.dialog.ToastUtil;
import com.aizou.core.http.HttpCallBack;
import com.aizou.core.utils.LocalDisplay;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.ChatBaseActivity;
import com.xuejian.client.lxp.common.account.AccountManager;
import com.xuejian.client.lxp.common.api.UserApi;
import com.xuejian.client.lxp.common.dialog.DialogManager;
import com.xuejian.client.lxp.common.dialog.PeachEditDialog;
import com.xuejian.client.lxp.common.gson.CommonJson4List;
import com.xuejian.client.lxp.common.widget.TitleHeaderBar;
import com.xuejian.client.lxp.db.userDB.User;
import com.xuejian.client.lxp.db.userDB.UserDBManager;

import java.util.ArrayList;
import java.util.List;

public class SeachContactDetailActivity extends ChatBaseActivity {
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
    @ViewInject(R.id.btn_add_contact)
    private Button addContactBtn;
    private User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seach_contact_detail);
        ViewUtils.inject(this);
        boolean isSearch = getIntent().getBooleanExtra("isSeach", false);
        user = (User) getIntent().getSerializableExtra("user");
        initTitleBar();
        if (isSearch) {
            bindView();
        } else {
            if (!TextUtils.isEmpty(user.getNickName())) {
                bindView();
            }
            List<String> hxList = new ArrayList<String>();
            hxList.add(user.getUserId()+"");
            UserApi.getContactByHx(hxList, new HttpCallBack<String>() {
                @Override
                public void doSucess(String result, String method) {
                    CommonJson4List<User> userResult = CommonJson4List.fromJson(result, User.class);
                    if (userResult.code == 0) {
                        if (userResult.result.size() > 0) {
                            user = userResult.result.get(0);
                            User cuser = UserDBManager.getInstance().getContactByUserId(user.getUserId());
                            //IMUser imUser = IMUserRepository.getContactByUserId(mContext, user.userId);
                            if (cuser == null) {
                                cuser = new User();
                            }
                            cuser.setNickName(user.getNickName());
                            cuser.setAvatar(user.getAvatar());
                            cuser.setAvatarSmall(user.getAvatarSmall());
                            cuser.setSignature(user.getSignature());
                            cuser.setMemo(user.getMemo());
                            cuser.setGender(user.getGender());
                            //IMUtils.setUserHead(imUser);
                            //IMUserRepository.saveContact(mContext, imUser);
                            UserDBManager.getInstance().saveContact(user);
                            bindView();
                        }
                    }
                }

                @Override
                public void doFailure(Exception error, String msg, String method) {
                    ToastUtil.getInstance(SeachContactDetailActivity.this).showToast(getResources().getString(R.string.request_network_failed));
                }
            });
        }

        addContactBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //todo:修改攻略名称
                final PeachEditDialog editDialog = new PeachEditDialog(mContext);
                editDialog.setTitle("输入验证信息");
                editDialog.setMessage(String.format("\"Hi, 我是%s\"", AccountManager.getInstance().getLoginAccount(SeachContactDetailActivity.this).getNickName()));
                editDialog.setPositiveButton("确定",new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        editDialog.dismiss();
                        DialogManager.getInstance().showLoadingDialog(SeachContactDetailActivity.this);
                        UserApi.requestAddContact(user.getUserId() + "", editDialog.getMessage(), new HttpCallBack() {
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
//                                    Toast.makeText(getApplicationContext(), "请求添加好友失败", Toast.LENGTH_SHORT).show();
                                ToastUtil.getInstance(SeachContactDetailActivity.this).showToast(getResources().getString(R.string.request_network_failed));
                            }
                        });
                    }
                });

                editDialog.show();
//                EMMessage cmdMsg = EMMessage.createSendMessage(EMMessage.Type.CMD);
//                //目前只支持单聊
//                String action="taozi_cmd";//action可以自定义，在广播接收时可以收到
//                CmdMessageBody cmdBody=new CmdMessageBody(action);
//                String toUsername=user.easemobUser;//发送给某个人
//                cmdMsg.setReceipt(toUsername);
//                cmdMsg.addBody(cmdBody);
//                cmdMsg.setAttribute("CMDType", 1);//支持自定义扩展
//                PeachUser myUser = AccountManager.getInstance().getLoginAccount(mContext);
//                JSONObject jsonObject = new JSONObject();
//                try {
//                    jsonObject.put("userId",myUser.userId);
//                    jsonObject.put("nickName",myUser.nickName);
//                    jsonObject.put("avatar",myUser.avatar);
//                    jsonObject.put("gender",myUser.gender);
//                    jsonObject.put("easemobUser",myUser.easemobUser);
//                    jsonObject.put("attachMsg","加个好友, 分享旅行");
//                    cmdMsg.setAttribute("content",jsonObject.toString());
//                    DialogManager.getInstance().showLoadingDialog(SeachContactDetailActivity.this);
//                    EMChatManager.getInstance().sendMessage(cmdMsg, new EMCallBack() {
//                        @Override
//                        public void onSuccess() {
//                            runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    DialogManager.getInstance().dissMissLoadingDialog();
//                                    Toast.makeText(getApplicationContext(), "发送请求成功,等待对方验证", Toast.LENGTH_SHORT).show();
//                                    finish();
//                                }
//                            });
//
//                        }
//
//                        @Override
//                        public void onError(int i, final String s) {
//                            runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    DialogManager.getInstance().dissMissLoadingDialog();
//                                    Toast.makeText(getApplicationContext(), "请求添加好友失败:" + s, Toast.LENGTH_SHORT).show();
//                                }
//                            });
//
//                        }
//
//                        @Override
//                        public void onProgress(int i, String s) {
//
//                        }
//                    });
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }

            }
        });

    }

    private void initTitleBar(){
        TitleHeaderBar thbar = (TitleHeaderBar)findViewById(R.id.ly_header_bar_title_wrap);
        thbar.getTitleTextView().setText(user.getNickName());
        thbar.enableBackKey(true);
    }

    private void bindView(){
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .showImageForEmptyUri(R.drawable.avatar_placeholder_round)
                .showImageOnFail(R.drawable.avatar_placeholder_round)
                .cacheOnDisc(true)
                        // 设置下载的图片是否缓存在SD卡中
                .displayer(new RoundedBitmapDisplayer(LocalDisplay.dp2px(32))) // 设置成圆角图片
                .build();
        ImageLoader.getInstance().displayImage(user.getAvatarSmall(), avatarIv, options);
        if ("M".equalsIgnoreCase(user.getGender())) {
            genderIv.setImageResource(R.drawable.ic_gender_man);
        } else if ("F".equalsIgnoreCase(user.getGender())) {
            genderIv.setImageResource(R.drawable.ic_gender_lady);
        } else {
            genderIv.setImageDrawable(null);
        }

        nickNameTv.setText(user.getNickName());
        signTv.setText(user.getSignature());
    }


}
