package com.aizou.peachtravel.module.toolbox.im;


import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.aizou.peachtravel.common.dialog.DialogManager;
import com.aizou.core.http.HttpCallBack;
import com.aizou.core.utils.LocalDisplay;
import com.aizou.peachtravel.R;
import com.aizou.peachtravel.base.ChatBaseActivity;
import com.aizou.peachtravel.bean.PeachUser;
import com.aizou.peachtravel.common.api.UserApi;
import com.aizou.peachtravel.common.gson.CommonJson4List;
import com.aizou.peachtravel.common.widget.TitleHeaderBar;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rjm on 2014/10/29.
 */
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
    private PeachUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seach_contact_detail);
        ViewUtils.inject(this);
        initTitleBar();
        boolean isSeach = getIntent().getBooleanExtra("isSeach", false);
        user = (PeachUser) getIntent().getSerializableExtra("user");
        if (isSeach) {
            bindView();
        } else {
            if (!TextUtils.isEmpty(user.nickName)) {
                bindView();
            }
            List<String> hxList = new ArrayList<String>();
            hxList.add(user.easemobUser);
            UserApi.getContactByHx(hxList, new HttpCallBack<String>() {
                @Override
                public void doSucess(String result, String method) {
                    CommonJson4List<PeachUser> userResult = CommonJson4List.fromJson(result, PeachUser.class);
                    if (userResult.code == 0) {
                        if (userResult.result.size() > 0) {
                            user = userResult.result.get(0);
                            bindView();
                        }
                    }
                }

                @Override
                public void doFailure(Exception error, String msg, String method) {

                }
            });
        }

        addContactBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialDialog.Builder builder= new MaterialDialog.Builder(mContext);
                builder.title("请输入验证信息");
                final EditText editText = new EditText(mContext);
                builder.customView(editText);
                builder.positiveText("确定");
                builder.negativeText("取消");
                builder.callback(new MaterialDialog.Callback() {
                    @Override
                    public void onNegative(MaterialDialog dialog) {

                    }

                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        DialogManager.getInstance().showLoadingDialog(SeachContactDetailActivity.this);
                        UserApi.requestAddContact(user.userId+"",editText.getText().toString().trim(),new HttpCallBack() {
                            @Override
                            public void doSucess(Object result, String method) {
                                DialogManager.getInstance().dissMissLoadingDialog();
                                    Toast.makeText(getApplicationContext(), "发送请求成功,等待对方验证", Toast.LENGTH_SHORT).show();
                                    finish();
                            }

                            @Override
                            public void doFailure(Exception error, String msg, String method) {
                                DialogManager.getInstance().dissMissLoadingDialog();
                                    Toast.makeText(getApplicationContext(), "请求添加桃友失败", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
                builder.show();
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
//                    jsonObject.put("attachMsg","加个桃友, 一起去旅行呗");
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
//                                    Toast.makeText(getApplicationContext(), "请求添加桃友失败:" + s, Toast.LENGTH_SHORT).show();
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
        thbar.getTitleTextView().setText("查找桃友");
        thbar.enableBackKey(true);
    }

    private void bindView(){
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .showImageForEmptyUri(R.drawable.avatar_placeholder)
                .showImageOnFail(R.drawable.avatar_placeholder)
                .cacheOnDisc(true)
                        // 设置下载的图片是否缓存在SD卡中
                .displayer(new RoundedBitmapDisplayer(LocalDisplay.dp2px(62))) // 设置成圆角图片
                .build();
        ImageLoader.getInstance().displayImage(user.avatar, avatarIv, options);
        nickNameTv.setText(user.nickName);
        signTv.setText(user.signature);
    }


}
