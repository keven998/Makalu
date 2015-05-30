package com.xuejian.client.lxp.module.my;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.aizou.core.dialog.ToastUtil;
import com.aizou.core.http.HttpCallBack;
import com.aizou.core.log.LogUtil;
import com.aizou.core.utils.LocalDisplay;
import com.easemob.EMCallBack;
import com.easemob.EMValueCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMGroup;
import com.easemob.chat.EMGroupManager;
import com.easemob.util.EMLog;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.umeng.analytics.MobclickAgent;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.PeachBaseFragment;
import com.xuejian.client.lxp.bean.ContactListBean;
import com.xuejian.client.lxp.bean.PeachUser;
import com.xuejian.client.lxp.common.account.AccountManager;
import com.xuejian.client.lxp.common.api.H5Url;
import com.xuejian.client.lxp.common.api.UserApi;
import com.xuejian.client.lxp.common.dialog.DialogManager;
import com.xuejian.client.lxp.common.gson.CommonJson;
import com.xuejian.client.lxp.common.utils.IMUtils;
import com.xuejian.client.lxp.common.utils.ShareUtils;
import com.xuejian.client.lxp.config.Constant;
import com.xuejian.client.lxp.db.IMUser;
import com.xuejian.client.lxp.db.respository.IMUserRepository;
import com.xuejian.client.lxp.module.PeachWebViewActivity;
import com.xuejian.client.lxp.module.toolbox.FavListActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Rjm on 2014/10/9.
 */
public class MyFragment extends PeachBaseFragment implements View.OnClickListener {
    public final static int CODE_FAVORITE = 102;

    @ViewInject(R.id.iv_avatar)
    private ImageView avatarIv;
    @ViewInject(R.id.iv_gender)
    private ImageView genderIv;

    @ViewInject(R.id.tv_nickname)
    private TextView nickNameTv;
    @ViewInject(R.id.tv_id)
    private TextView idTv;
    @ViewInject(R.id.tv_status)
    private TextView statusTv;
    private View rootView,unRootView;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_my, null);
        ViewUtils.inject(this, rootView);
        rootView.findViewById(R.id.ll_share_account).setOnClickListener(this);
        rootView.findViewById(R.id.ll_about).setOnClickListener(this);
        rootView.findViewById(R.id.ll_setting).setOnClickListener(this);
        rootView.findViewById(R.id.ll_message_center).setOnClickListener(this);
        rootView.findViewById(R.id.ll_push_friends).setOnClickListener(this);
        rootView.findViewById(R.id.login_frame).setOnClickListener(this);
       // rootView.findViewById(R.id.guide_favour).setOnClickListener(this);  //新添的指路达人
        return rootView;
    }

    public void refresh(){
        PeachUser user = AccountManager.getInstance().getLoginAccount(getActivity());
        if(user == null) {
            View view=getView();
            genderIv.setVisibility(View.GONE);
            view.findViewById(R.id.indicator).setVisibility(View.GONE);
            avatarIv.setImageResource(R.drawable.avatar_placeholder_round);
            nickNameTv.setText("未登录");
            idTv.setText("点击登录旅行派，享受更多旅行服务");
            statusTv.setText("");
        } else {
            genderIv.setVisibility(View.VISIBLE);
            View view=getView();
            view.findViewById(R.id.indicator).setVisibility(View.VISIBLE);
            if (user.gender.equalsIgnoreCase("M")) {
                genderIv.setImageResource(R.drawable.ic_gender_man);
            } else if (user.gender.equalsIgnoreCase("F")) {
                genderIv.setImageResource(R.drawable.ic_gender_lady);
            } else {
                genderIv.setImageDrawable(null);
            }
            nickNameTv.setText(user.nickName);
            DisplayImageOptions options = new DisplayImageOptions.Builder()
                    .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                    .showImageForEmptyUri(R.drawable.avatar_placeholder_round)
                    .showImageOnFail(R.drawable.avatar_placeholder_round)
                    .resetViewBeforeLoading(true)
                    .cacheOnDisc(true)
                            // 设置下载的图片是否缓存在SD卡中
                    .displayer(new RoundedBitmapDisplayer(LocalDisplay.dp2px(62))) // 设置成圆角图片
                    .build();
            ImageLoader.getInstance().displayImage(user.avatarSmall, avatarIv, options);
            LogUtil.d(user.avatarSmall+"====================================");
            idTv.setText("ID: " + user.userId);
            if (TextUtils.isEmpty(user.signature)) {
                statusTv.setText("");
            } else {
                statusTv.setText(user.travelStatus);
            }
        }
    }

    Handler handler=new Handler();
    Runnable  runnableUi=new  Runnable(){
        @Override
        public void run() {
            //更新界面
            refresh();
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        new Thread(){
            public void run(){
                handler.post(runnableUi);
            }
        }.start();

//        MobclickAgent.onPageStart("page_home_me");
    }
    @Override
    public void onPause() {
        super.onPause();
//        MobclickAgent.onPageEnd("page_home_me");
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.login_frame:
                PeachUser user = AccountManager.getInstance().getLoginAccount(getActivity());
                if (user == null) {
                    Intent logIntent=new Intent(getActivity(),LoginActivity.class);
                    startActivity(logIntent);
                    getActivity().overridePendingTransition(R.anim.push_bottom_in, 0);
                } else {
                    Intent accountIntent = new Intent(getActivity(), AccountActvity.class);
                    startActivity(accountIntent);
                }
                break;

            case R.id.ll_share_account:
                MobclickAgent.onEvent(getActivity(),"event_share_app_by_weichat");
                Intent intent = new Intent(getActivity(), ShareAccountActivity.class);
                startActivity(intent);
                break;

            case R.id.ll_about:
                Intent aboutIntent = new Intent(getActivity(), PeachWebViewActivity.class);
                aboutIntent.putExtra("url", String.format("%s?version=%s", H5Url.ABOUT, getResources().getString(R.string.app_version)));
                aboutIntent.putExtra("title", "关于旅行派");
                startActivity(aboutIntent);
                break;

            case R.id.ll_setting:
                Intent settingIntent = new Intent(getActivity(), SettingActivity.class);
                startActivity(settingIntent);
                break;

           /* case R.id.btn_login:
                Intent loginintent = new Intent(getActivity(), LoginActivity.class);
                startActivity(loginintent);
                break;

            case R.id.btn_reg:
                Intent regintent = new Intent(getActivity(), RegActivity.class);
                startActivityForResult(regintent, LoginActivity.REQUEST_CODE_REG);
                break;*/

            case R.id.ll_message_center:
//                Intent msgIntent = new Intent(getActivity(), MessageContents.class);
//                startActivity(msgIntent);
                PeachUser user1 = AccountManager.getInstance().getLoginAccount(getActivity());
                if (user1 != null && !TextUtils.isEmpty(user1.easemobUser)) {
                    Intent fIntent = new Intent(getActivity(), FavListActivity.class);
                    startActivity(fIntent);
                } else {
                    Intent loginIntent = new Intent(getActivity(), LoginActivity.class);
                    startActivityForResult(loginIntent, CODE_FAVORITE);
                    ToastUtil.getInstance(getActivity()).showToast("请先登录");
                }
                break;

            case R.id.ll_push_friends:
                ShareUtils.shareAppToWx(getActivity(), null);

                break;

            default:
                break;
        }
    }
    private void imLogin(final PeachUser user) {
        EMChatManager.getInstance().login(user.easemobUser, user.easemobPwd, new EMCallBack() {

            @Override
            public void onSuccess() {

                // 登陆成功，保存用户名密码
                // demo中简单的处理成每次登陆都去获取好友username，开发者自己根据情况而定
                AccountManager.getInstance().saveLoginAccount(getActivity(), user);
                boolean updatenick = EMChatManager.getInstance().updateCurrentUserNick(user.nickName);
                if (!updatenick) {
                    EMLog.e("LoginActivity", "update current user nick fail");
                }

                // 获取群聊列表(群聊里只有groupid和groupname等简单信息，不包含members),sdk会把群组存入到内存和db中

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
//                    groupUser.setUnreadMsgCount(0);
//                    userlist.put(Constant.GROUP_USERNAME, groupUser);
                // 存入内存
                AccountManager.getInstance().setContactList(userlist);
                List<IMUser> users = new ArrayList<IMUser>(userlist.values());
                IMUserRepository.saveContactList(getActivity(), users);
                // 获取群聊列表(群聊里只有groupid和groupname的简单信息),sdk会把群组存入到内存和db中
                final long startTime = System.currentTimeMillis();
                LogUtil.d("getGroupFromServer", startTime + "");
                EMGroupManager.getInstance().asyncGetGroupsFromServer(new EMValueCallBack<List<EMGroup>>() {
                    @Override
                    public void onSuccess(List<EMGroup> emGroups) {
                        long endTime = System.currentTimeMillis();
                        LogUtil.d("getGroupFromServer", endTime - startTime + "--groudSize=" + emGroups.size());

                    }

                    @Override
                    public void onError(int i, String s) {

                    }
                });

                // ** 第一次登录或者之前logout后再登录，加载所有本地群和回话
                // ** manually load all local groups and
                // conversations in case we are auto login
                EMGroupManager.getInstance().loadAllGroups();
                EMChatManager.getInstance().loadAllConversations();
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
                                user.setUnreadMsgCount(0);
                                user.setAvatar(peachUser.avatar);
                                user.setAvatarSmall(peachUser.avatarSmall);
                                user.setSignature(peachUser.signature);
                                user.setIsMyFriends(true);
                                user.setGender(peachUser.gender);
                                IMUtils.setUserHead(user);
                                userlist.put(peachUser.easemobUser, user);
                            }
                            // 存入内存
                            AccountManager.getInstance().setContactList(userlist);
                            // 存入db
                            List<IMUser> users = new ArrayList<IMUser>(userlist.values());
                            IMUserRepository.saveContactList(getActivity(), users);
                        }

                    }

                    @Override
                    public void doFailure(Exception error, String msg, String method) {
                        if (!getActivity().isFinishing())
                            ToastUtil.getInstance(getActivity()).showToast(getResources().getString(R.string.request_network_failed));
                    }
                });
                // 进入主页面
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        DialogManager.getInstance().dissMissLoadingDialog();
                        ToastUtil.getInstance(getActivity()).showToast("欢迎来到旅行派");
                        refresh();

                    }
                });


            }

            @Override
            public void onProgress(int progress, String status) {

            }

            @Override
            public void onError(int code, final String message) {
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        DialogManager.getInstance().dissMissLoadingDialog();
                        ToastUtil.getInstance(getActivity()).showToast("登录失败 " + message);
                    }
                });
            }
        });


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == LoginActivity.REQUEST_CODE_REG) {
                PeachUser user = (PeachUser) data.getSerializableExtra("user");
                DialogManager.getInstance().showLoadingDialog(getActivity(), "正在登录");
                imLogin(user);
            } else if (requestCode == CODE_FAVORITE) {
                startActivity(new Intent(getActivity(), FavListActivity.class));
            }
        }
    }
}
