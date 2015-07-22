package com.xuejian.client.lxp.module.my;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
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
import com.umeng.analytics.MobclickAgent;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.PeachBaseFragment;
import com.xuejian.client.lxp.bean.ContactListBean;
import com.xuejian.client.lxp.bean.LocBean;
import com.xuejian.client.lxp.common.account.AccountManager;
import com.xuejian.client.lxp.common.api.H5Url;
import com.xuejian.client.lxp.common.api.UserApi;
import com.xuejian.client.lxp.common.dialog.DialogManager;
import com.xuejian.client.lxp.common.gson.CommonJson;
import com.xuejian.client.lxp.common.utils.ConstellationUtil;
import com.xuejian.client.lxp.common.utils.IntentUtils;
import com.xuejian.client.lxp.common.utils.ShareUtils;
import com.xuejian.client.lxp.db.User;
import com.xuejian.client.lxp.db.UserDBManager;
import com.xuejian.client.lxp.module.PeachWebViewActivity;
import com.xuejian.client.lxp.module.dest.CityPictureActivity;
import com.xuejian.client.lxp.module.dest.StrategyMapActivity;
import com.xuejian.client.lxp.module.toolbox.StrategyListActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Rjm on 2014/10/9.
 */
public class MyFragment extends PeachBaseFragment implements View.OnClickListener {
    public final static int CODE_PLANS = 102;
    public final static int CODE_FOOTPRINT = 103;
    public final static int CODE_PICS = 104;

    @ViewInject(R.id.iv_avatar)
    private ImageView avatarIv;
    @ViewInject(R.id.fl_gender_bg)
    private FrameLayout fl_gender_bg;
    @ViewInject(R.id.iv_constellation)
    private ImageView constellationIv;
    @ViewInject(R.id.iv_more_header_frame_gender1)
    private ImageView genderFrame;
    @ViewInject(R.id.tv_level)
    private TextView tvLevel;

    @ViewInject(R.id.tv_title)
    private TextView nickNameTv;
    @ViewInject(R.id.tv_subtitle)
    private TextView idTv;

    @ViewInject(R.id.tv_pictures_count)
    private TextView tvPictureCount;
    @ViewInject(R.id.tv_plans_count)
    private TextView tvPlansCount;
    @ViewInject(R.id.tv_tracks_count)
    private TextView tvTracksCount;
    ArrayList<LocBean> all_foot_print_list = new ArrayList<LocBean>();
    private View rootView;
    private int picsNum = 0;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_my, null);
        ViewUtils.inject(this, rootView);
        rootView.findViewById(R.id.tv_share_appwith_friend).setOnClickListener(this);
        rootView.findViewById(R.id.tv_aboutus).setOnClickListener(this);
        rootView.findViewById(R.id.tv_app_setting).setOnClickListener(this);
        rootView.findViewById(R.id.tv_feedback).setOnClickListener(this);
        rootView.findViewById(R.id.tv_edit_profile).setOnClickListener(this);
        rootView.findViewById(R.id.iv_more_header_frame_gender1).setOnClickListener(this);
        rootView.findViewById(R.id.rl_picture_entry).setOnClickListener(this);
        rootView.findViewById(R.id.fl_plans_entry).setOnClickListener(this);
        rootView.findViewById(R.id.fl_tracks_entry).setOnClickListener(this);
        return rootView;
    }

    public void refreshLoginStatus() {
        User user = AccountManager.getInstance().getLoginAccount(getActivity());
        all_foot_print_list.clear();
        if (user == null) {
            System.out.println("user null");
            avatarIv.setImageResource(R.drawable.ic_home_userentry_unlogin);
            nickNameTv.setText("旅行派");
            idTv.setText("未登录");
            tvPictureCount.setText("0图");
            tvPlansCount.setText("0条");
            tvTracksCount.setText("0");
            tvLevel.setText("Lv0");
            tvLevel.setBackgroundResource(R.drawable.ic_home_level_bg_unknown);
            genderFrame.setImageResource(R.drawable.ic_home_header_unlogin);
            fl_gender_bg.setForeground(getResources().getDrawable(R.drawable.ic_home_avatar_border_unknown));
            constellationIv.setImageResource(R.drawable.ic_home_constellation_unknown);
        } else {
            System.out.println("user not null");
            if (user.getGender().equalsIgnoreCase("M")) {
                genderFrame.setImageResource(R.drawable.ic_home_header_boy);
                fl_gender_bg.setForeground(getResources().getDrawable(R.drawable.ic_home_avatar_border_boy));
                tvLevel.setBackgroundResource(R.drawable.ic_home_level_bg_boy);
            } else if (user.getGender().equalsIgnoreCase("F")) {
                genderFrame.setImageResource(R.drawable.ic_home_header_girl);
                fl_gender_bg.setForeground(getResources().getDrawable(R.drawable.ic_home_avatar_border_girl));
                tvLevel.setBackgroundResource(R.drawable.ic_home_level_bg_girl);
            } else {
                genderFrame.setImageResource(R.drawable.ic_home_header_unlogin);
                fl_gender_bg.setForeground(getResources().getDrawable(R.drawable.ic_home_avatar_border_unknown));
                tvLevel.setBackgroundResource(R.drawable.ic_home_level_bg_unknown);
            }
            int countryCount = 0;
            int cityCount = 0;
            String level = "0";
            int guideCount = 0;
            int picNum=0;
            User info=null;
            if (AccountManager.getInstance().getLoginAccountInfo() != null) {
                info = AccountManager.getInstance().getLoginAccountInfo();
                guideCount = info.getGuideCnt();
                level = info.getLevel();
                countryCount=info.getCountryCnt();
                cityCount=info.getTrackCnt();
                picNum=info.getAlbumCnt();
            }
            tvPictureCount.setText(picNum + "图");
            tvTracksCount.setText(countryCount + "国" + cityCount + "城市");
            tvPlansCount.setText(guideCount + "条");
            nickNameTv.setText(user.getNickName());
            idTv.setText("ID：" + user.getUserId());
            tvLevel.setText("LV" + level);
            int res= ConstellationUtil.calculateConstellation(user.getBirthday());
            if (res==0){
                constellationIv.setImageResource(R.drawable.ic_home_constellation_unknown);
            }else constellationIv.setImageResource(res);
            DisplayImageOptions options = new DisplayImageOptions.Builder()
                    .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                    .showImageForEmptyUri(R.drawable.ic_home_talklist_default_avatar)
                    .showImageOnFail(R.drawable.ic_home_talklist_default_avatar)
                    .resetViewBeforeLoading(true)
                    .cacheOnDisk(true)
                    .displayer(new RoundedBitmapDisplayer(getResources().getDimensionPixelSize(R.dimen.page_more_header_frame_height) - LocalDisplay.dp2px(20))) // 设置成圆角图片
                    .build();
            ImageLoader.getInstance().displayImage(user.getAvatarSmall(), avatarIv, options);
          //  getUserPicsNum(user.getUserId());
        }
    }

    Handler handler = new Handler();
    Runnable runnableUi = new Runnable() {
        @Override
        public void run() {
            //更新界面
            refreshLoginStatus();
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        new Thread() {
            public void run() {
                handler.post(runnableUi);
            }
        }.start();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_more_header_frame_gender1:
                User user1 = AccountManager.getInstance().getLoginAccount(getActivity());
                if (user1 == null) {
                    Intent logIntent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(logIntent);
                } else {
                    ArrayList<String> pic = new ArrayList<>();
                    pic.add(user1.getAvatar());
                    showSelectedPics(pic);
                }
                break;

            case R.id.tv_aboutus:
                Intent aboutIntent = new Intent(getActivity(), PeachWebViewActivity.class);
                aboutIntent.putExtra("url", String.format("%s?version=%s", H5Url.ABOUT, getResources().getString(R.string.app_version)));
                aboutIntent.putExtra("title", "关于旅行派");
                startActivity(aboutIntent);
                break;

            case R.id.tv_app_setting:
                Intent settingIntent = new Intent(getActivity(), SettingActivity.class);
                startActivity(settingIntent);
                break;

            case R.id.tv_feedback:
                MobclickAgent.onEvent(getActivity(), "event_feedback");
                Intent feedback = new Intent(getActivity(), FeedbackActivity.class);
                startActivity(feedback);
                break;

            case R.id.tv_share_appwith_friend:
                ShareUtils.shareAppToWx(getActivity(), null);
                break;

            case R.id.tv_edit_profile:
                User user = AccountManager.getInstance().getLoginAccount(getActivity());
                if (user == null) {
                    Intent logIntent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(logIntent);
                } else {
                    Intent accountIntent = new Intent(getActivity(), AccountActvity.class);
                    startActivity(accountIntent);
                }
                break;

            case R.id.rl_picture_entry:
                final User userPics = AccountManager.getInstance().getLoginAccount(getActivity());
                if (userPics != null) {
                    Intent intent2 = new Intent(getActivity(), CityPictureActivity.class);
                    intent2.putExtra("id", String.valueOf(userPics.getUserId()));
                    intent2.putExtra("user_name", userPics.getNickName());
                    intent2.putExtra("isUserPics", true);
                    startActivity(intent2);
                } else {
                    Intent LoginIntent = new Intent(getActivity(), LoginActivity.class);
                    startActivityForResult(LoginIntent, CODE_PICS);
                    getActivity().overridePendingTransition(R.anim.push_bottom_in, R.anim.slide_stay);
                }

                break;

            case R.id.fl_plans_entry:
                final User user2 = AccountManager.getInstance().getLoginAccount(getActivity());
                if (user2 != null) {
                    Intent intent2 = new Intent(getActivity(), StrategyListActivity.class);
                    intent2.putExtra("userId", String.valueOf(user2.getUserId()));
                    intent2.putExtra("user_name", user2.getNickName());
                    startActivity(intent2);
                } else {
                    Intent LoginIntent = new Intent(getActivity(), LoginActivity.class);
                    startActivityForResult(LoginIntent, CODE_PLANS);
                    getActivity().overridePendingTransition(R.anim.push_bottom_in, R.anim.slide_stay);
                }
                break;

            case R.id.fl_tracks_entry:
                User user3 = AccountManager.getInstance().getLoginAccount(getActivity());
                if (user3 == null) {
                    Intent logIntent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(logIntent);
                } else {
                    Intent tracks_intent = new Intent(getActivity(), StrategyMapActivity.class);
                    tracks_intent.putExtra("isMyFootPrint", true);
                    tracks_intent.putParcelableArrayListExtra("myfootprint", all_foot_print_list);
                    tracks_intent.putExtra("title", tvTracksCount.getText().toString());
                    startActivityForResult(tracks_intent, CODE_FOOTPRINT);
                }
                break;

            default:
                break;
        }
    }

    private void showSelectedPics(ArrayList<String> pics) {
        IntentUtils.intentToPicGallery2(getActivity(), pics, 0);
    }

    private void imLogin(final User user) {

        AccountManager.getInstance().saveLoginAccount(getActivity(), user);

        final ConcurrentHashMap<Long, User> userlist = new ConcurrentHashMap<Long, User>();

        UserApi.getContact(new HttpCallBack<String>() {
            @Override
            public void doSuccess(String result, String method) {
                CommonJson<ContactListBean> contactResult = CommonJson.fromJson(result, ContactListBean.class);
                if (contactResult.code == 0) {
                    for (User myUser : contactResult.result.contacts) {
                        userlist.put(myUser.getUserId(), user);
                    }
                    // 存入内存
                    AccountManager.getInstance().setContactList(userlist);
                    // 存入db
                    List<User> users = new ArrayList<User>(userlist.values());
                    UserDBManager.getInstance().saveContactList(users);
                }
            }

            @Override
            public void doFailure(Exception error, String msg, String method) {
                if (!getActivity().isFinishing())
                    ToastUtil.getInstance(getActivity()).showToast(getResources().getString(R.string.request_network_failed));
            }

            @Override
            public void doFailure(Exception error, String msg, String method, int code) {

            }
        });
        // 进入主页面
        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                DialogManager.getInstance().dissMissLoadingDialog();
                refreshLoginStatus();
            }
        });

    }

    public void getUserPicsNum(Long userId) {
        UserApi.getUserPicAlbumn(String.valueOf(userId), new HttpCallBack<String>() {
            @Override
            public void doSuccess(String result, String method) {
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(result);
                    if (jsonObject.getInt("code") == 0) {
                        JSONArray object = jsonObject.getJSONArray("result");
                        picsNum = object.length();
                        tvPictureCount.setText(picsNum + "图");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


            @Override
            public void doFailure(Exception error, String msg, String method) {
                tvPictureCount.setText(picsNum + "图");
                ToastUtil.getInstance(getActivity()).showToast("好像没有网络额~");
            }

            @Override
            public void doFailure(Exception error, String msg, String method, int code) {

            }
        });

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == LoginActivity.REQUEST_CODE_REG) {
                User user = (User) data.getSerializableExtra("user");
                DialogManager.getInstance().showLoadingDialog(getActivity(), "正在登录");
                imLogin(user);
            } else if (requestCode == CODE_PLANS) {
                final User user2 = AccountManager.getInstance().getLoginAccount(getActivity());
                if (user2 != null) {
                    Intent intent2 = new Intent(getActivity(), StrategyListActivity.class);
                    intent2.putExtra("userId", String.valueOf(user2.getUserId()));
                    intent2.putExtra("user_name", user2.getNickName());
                    startActivity(intent2);
                }
            }
        }
    }
}
