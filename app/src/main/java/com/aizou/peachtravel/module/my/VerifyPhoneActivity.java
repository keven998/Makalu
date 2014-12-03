package com.aizou.peachtravel.module.my;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.aizou.core.dialog.DialogManager;
import com.aizou.core.dialog.ToastUtil;
import com.aizou.core.http.HttpCallBack;
import com.aizou.peachtravel.R;
import com.aizou.peachtravel.base.PeachBaseActivity;
import com.aizou.peachtravel.bean.ContactListBean;
import com.aizou.peachtravel.bean.PeachUser;
import com.aizou.peachtravel.bean.ValidationBean;
import com.aizou.peachtravel.common.account.AccountManager;
import com.aizou.peachtravel.common.api.UserApi;
import com.aizou.peachtravel.common.gson.CommonJson;
import com.aizou.peachtravel.common.utils.CommonUtils;
import com.aizou.peachtravel.common.utils.IMUtils;
import com.aizou.peachtravel.common.widget.TitleHeaderBar;
import com.aizou.peachtravel.config.Constant;
import com.aizou.peachtravel.db.IMUser;
import com.aizou.peachtravel.db.respository.IMUserRepository;
import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMGroupManager;
import com.easemob.util.EMLog;
import com.easemob.util.HanziToPinyin;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Rjm on 2014/10/13.
 */
public class VerifyPhoneActivity extends PeachBaseActivity implements View.OnClickListener {
    @ViewInject(R.id.et_sms)
    private EditText smsEt;
    @ViewInject(R.id.btn_time_down)
    private Button downTimeBtn;
    private CountDownTimer countDownTimer;
    private String tel,pwd,actionCode;
    private int countDown;
    private PeachUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_phone);
        ViewUtils.inject(this);
        findViewById(R.id.btn_next).setOnClickListener(this);
        downTimeBtn.setOnClickListener(this);
        initData();
        startCountDownTime();

        TitleHeaderBar titleBar = (TitleHeaderBar)findViewById(R.id.ly_header_bar_title_wrap);
        titleBar.getTitleTextView().setText("注册验证");
        titleBar.enableBackKey(true);

        TextView tips = (TextView)findViewById(R.id.tips);
        tips.setText(String.format("已发送短信验证码至 %s\n网络有延迟,请稍后", tel));
    }
    private void initData(){
        tel = getIntent().getStringExtra("tel");
        pwd = getIntent().getStringExtra("pwd");
        countDown = getIntent().getIntExtra("countDown",60);
        actionCode = getIntent().getStringExtra("actionCode");
        user = AccountManager.getInstance().getLoginAccount(this);
    }

    private void startCountDownTime(){
        downTimeBtn.setClickable(false);
        countDownTimer= new CountDownTimer(countDown*1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                downTimeBtn.setText("(" + (millisUntilFinished / 1000) + ")");
            }

            @Override
            public void onFinish() {
                downTimeBtn.setText("重新获取");
                downTimeBtn.setClickable(true);
            }
        }.start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_next:
                if(TextUtils.isEmpty(smsEt.getText().toString())){
                    ToastUtil.getInstance(mContext).showToast("请输入验证码");
                }else{
                    if(!CommonUtils.isNetWorkConnected(mContext)){
                        ToastUtil.getInstance(this).showToast("无网络，请检查网络连接");
                        return;
                    }
                    if(actionCode.equals(UserApi.ValidationCode.REG_CODE)){
                        DialogManager.getInstance().showProgressDialog(VerifyPhoneActivity.this);
                        UserApi.signUp(tel,pwd,smsEt.getText().toString().trim(),new HttpCallBack<String>() {
                            @Override
                            public void doSucess(String result, String method) {
                                CommonJson<PeachUser> userResult = CommonJson.fromJson(result,PeachUser.class);
                                if(userResult.code==0){
                                    imLogin(userResult.result);
                                }else{
                                    ToastUtil.getInstance(mContext).showToast(userResult.err.message);
                                }

                            }

                            @Override
                            public void doFailure(Exception error, String msg, String method) {
                                DialogManager.getInstance().dissMissProgressDialog();
                            }
                        });
                    }else if(actionCode.equals(UserApi.ValidationCode.FIND_PWD)){

                    }

                }
                break;
            case R.id.btn_time_down:
                if(!CommonUtils.isNetWorkConnected(mContext)){
                    ToastUtil.getInstance(this).showToast("无网络，请检查网络连接");
                    return;
                }
                DialogManager.getInstance().showProgressDialog(VerifyPhoneActivity.this);

                String uid=null ;
                if(user!=null){
                    uid = user.userId+"";
                }
                UserApi.sendValidation(tel, actionCode,uid, new HttpCallBack<String>() {
                    @Override
                    public void doSucess(String result, String method) {
                        DialogManager.getInstance().dissMissProgressDialog();
                        CommonJson<ValidationBean> validationResult = CommonJson.fromJson(result, ValidationBean.class);
                        if (validationResult.code == 0) {
                            countDown = validationResult.result.coolDown;
                            startCountDownTime();
                        } else {
                            ToastUtil.getInstance(mContext).showToast(validationResult.err.message);
                        }

                    }

                    @Override
                    public void doFailure(Exception error, String msg, String method) {
                        DialogManager.getInstance().dissMissProgressDialog();
                    }
                });

                break;
        }
    }
    private void imLogin(final PeachUser user){
        EMChatManager.getInstance().login(user.easemobUser, user.easemobPwd, new EMCallBack() {

            @Override
            public void onSuccess() {
                // 登陆成功，保存用户名密码
                try {
                    // demo中简单的处理成每次登陆都去获取好友username，开发者自己根据情况而定
                    AccountManager.getInstance().saveLoginAccount(mContext, user);
//                    List<String> usernames = EMContactManager.getInstance().getContactUserNames();
                    final Map<String, IMUser> userlist = new HashMap<String, IMUser>();
                    // 添加user"申请与通知"
                    IMUser newFriends = new IMUser();
                    newFriends.setUsername(Constant.NEW_FRIENDS_USERNAME);
                    newFriends.setNick("申请与通知");
                    newFriends.setHeader("");
                    newFriends.setIsMyFriends(true);
                    userlist.put(Constant.NEW_FRIENDS_USERNAME, newFriends);
//                    // 添加"群聊"
//                    IMUser groupUser = new IMUser();
//                    groupUser.setUsername(Constant.GROUP_USERNAME);
//                    groupUser.setNick("群聊");
//                    groupUser.setHeader("");
//                    userlist.put(Constant.GROUP_USERNAME, groupUser);
                    UserApi.getContact(new HttpCallBack<String>() {
                        @Override
                        public void doSucess(String result, String method) {
                            CommonJson<ContactListBean> contactResult = CommonJson.fromJson(result, ContactListBean.class);
                            if (contactResult.code == 0) {
                                for (PeachUser peachUser : contactResult.result.contacts) {
                                    IMUser user = new IMUser();
                                    user.setUserId(peachUser.userId);
                                    user.setMemo(peachUser.memo);
                                    user.setNick(peachUser.nickName);
                                    user.setUsername(peachUser.easemobUser);
                                    user.setIsMyFriends(true);
                                    user.setAvatar(peachUser.avatar);
                                    user.setSignature(peachUser.signature);
                                    IMUtils.setUserHead(user);
                                    userlist.put(peachUser.easemobUser, user);
                                }
                                // 存入内存
                                AccountManager.getInstance().setContactList(userlist);
                                // 存入db
                                List<IMUser> users = new ArrayList<IMUser>(userlist.values());
                                IMUserRepository.saveContactList(mContext,users);
                            }

                        }

                        @Override
                        public void doFailure(Exception error, String msg, String method) {

                        }
                    });

                    // 存入内存
                    AccountManager.getInstance().setContactList(userlist);
                    // 存入db
                    List<IMUser> users = new ArrayList<IMUser>(userlist.values());
                    IMUserRepository.saveContactList(mContext,users);
                    // 获取群聊列表(群聊里只有groupid和groupname的简单信息),sdk会把群组存入到内存和db中
                    EMGroupManager.getInstance().getGroupsFromServer();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                DialogManager.getInstance().dissMissProgressDialog();
                boolean updatenick = EMChatManager.getInstance().updateCurrentUserNick(user.nickName);
                if (!updatenick) {
                    EMLog.e("LoginActivity", "update current user nick fail");
                }

                // 进入主页面
                setResult(RESULT_OK);
                finish();
            }

            @Override
            public void onProgress(int progress, String status) {

            }

            @Override
            public void onError(int code, final String message) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        DialogManager.getInstance().dissMissProgressDialog();
                        Toast.makeText(getApplicationContext(), "登录失败: " + message, Toast.LENGTH_SHORT).show();

                    }
                });
            }
        });


    }
}
