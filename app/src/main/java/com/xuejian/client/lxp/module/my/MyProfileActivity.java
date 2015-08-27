package com.xuejian.client.lxp.module.my;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.aizou.core.dialog.ToastUtil;
import com.aizou.core.http.HttpCallBack;
import com.aizou.core.utils.LocalDisplay;
import com.aizou.core.utils.SharePrefUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.umeng.analytics.MobclickAgent;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.bean.ContactListBean;
import com.xuejian.client.lxp.bean.LocBean;
import com.xuejian.client.lxp.common.account.AccountManager;
import com.xuejian.client.lxp.common.api.UserApi;
import com.xuejian.client.lxp.common.dialog.DialogManager;
import com.xuejian.client.lxp.common.gson.CommonJson;
import com.xuejian.client.lxp.common.utils.ConstellationUtil;
import com.xuejian.client.lxp.common.utils.ImageCache;
import com.xuejian.client.lxp.common.utils.IntentUtils;
import com.xuejian.client.lxp.db.User;
import com.xuejian.client.lxp.db.UserDBManager;
import com.xuejian.client.lxp.module.MainActivity;
import com.xuejian.client.lxp.module.dest.CityPictureActivity;
import com.xuejian.client.lxp.module.dest.StrategyMapActivity;
import com.xuejian.client.lxp.module.toolbox.StrategyListActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by xuyongchen on 15/8/27.
 */
public class MyProfileActivity  extends Activity implements  View.OnClickListener{
    public final static int CODE_PLANS = 102;
    public final static int CODE_FOOTPRINT = 103;
    public final static int CODE_PICS = 104;
    @InjectView(R.id.my_profile_edit)
    TextView myProfileEdit;
    @InjectView(R.id.iv_avatar)
    ImageView avatarIv;
    @InjectView(R.id.iv_more_header_frame_gender1)
    ImageView genderFrame;
    @InjectView(R.id.fl_gender_bg)
    FrameLayout fl_gender_bg;
    @InjectView(R.id.iv_constellation)
    ImageView constellationIv;
    @InjectView(R.id.tv_level)
    TextView tvLevel;




    @InjectView(R.id.tv_pictures_count)
    TextView tvPictureCount;


    @InjectView(R.id.tv_plans_count)
    TextView tvPlansCount;
    @InjectView(R.id.tv_tracks_count)
    TextView tvTracksCount;
    private TextView notice;
    private int picsNum = 0;
    private String Sex;
    ArrayList<LocBean> all_foot_print_list = new ArrayList<LocBean>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_profile_activity);
        ButterKnife.inject(this);
        ((TextView)findViewById(R.id.my_profile_subtitle)).setText("");
        myProfileEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                turnToEditProfile();
            }
        });
        genderFrame.setOnClickListener(this);
       // findViewById(R.id.tv_edit_profile).setOnClickListener(this);

        findViewById(R.id.rl_picture_entry).setOnClickListener(this);
        findViewById(R.id.fl_plans_entry).setOnClickListener(this);
        findViewById(R.id.fl_tracks_entry).setOnClickListener(this);
        notice = (TextView) findViewById(R.id.unread_msg_notify);
        if (SharePrefUtil.getBoolean(MyProfileActivity.this,"firstReg",false))notice.setVisibility(View.VISIBLE);

    }


    private void   turnToEditProfile(){
        MobclickAgent.onEvent(MyProfileActivity.this, "navigation_item_edit_profile");
        User user = AccountManager.getInstance().getLoginAccount(MyProfileActivity.this);
        if (user == null) {
            Intent logIntent = new Intent(MyProfileActivity.this, LoginActivity.class);
            startActivity(logIntent);
        } else {
            if (SharePrefUtil.getBoolean(MyProfileActivity.this, "firstReg", false)){
                SharePrefUtil.saveBoolean(MyProfileActivity.this,"firstReg",false);
            }
            Intent accountIntent = new Intent(MyProfileActivity.this, AccountActvity.class);
            startActivity(accountIntent);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.iv_more_header_frame_gender1:
                User user1 = AccountManager.getInstance().getLoginAccount(MyProfileActivity.this);
                if (user1 == null) {
                    Intent logIntent = new Intent(MyProfileActivity.this, LoginActivity.class);
                    startActivity(logIntent);
                } else {
                    if(!TextUtils.isEmpty(user1.getAvatar())) {
                        ArrayList<String> pic = new ArrayList<>();
                        pic.add(user1.getAvatar());
                        showSelectedPics(pic);
                    }
                }
                break;

            case R.id.rl_picture_entry:
                final User userPics = AccountManager.getInstance().getLoginAccount(MyProfileActivity.this);
                if (userPics != null) {
                    Intent intent2 = new Intent(MyProfileActivity.this, CityPictureActivity.class);
                    intent2.putExtra("id", String.valueOf(userPics.getUserId()));
                    intent2.putExtra("user_name", userPics.getNickName());
                    intent2.putExtra("isUserPics", true);
                    startActivity(intent2);
                } else {
                    Intent LoginIntent = new Intent(MyProfileActivity.this, LoginActivity.class);
                    startActivityForResult(LoginIntent, CODE_PICS);
                    MyProfileActivity.this.overridePendingTransition(R.anim.push_bottom_in, R.anim.slide_stay);
                }

                break;

            case R.id.tv_edit_profile:
                MobclickAgent.onEvent(MyProfileActivity.this,"navigation_item_edit_profile");
                User user = AccountManager.getInstance().getLoginAccount(MyProfileActivity.this);
                if (user == null) {
                    Intent logIntent = new Intent(MyProfileActivity.this, LoginActivity.class);
                    startActivity(logIntent);
                } else {
                    if (SharePrefUtil.getBoolean(MyProfileActivity.this,"firstReg",false)){
                        notice.setVisibility(View.GONE);

                        SharePrefUtil.saveBoolean(MyProfileActivity.this,"firstReg",false);
                    }
                    Intent accountIntent = new Intent(MyProfileActivity.this, AccountActvity.class);
                    startActivity(accountIntent);
                }
                break;



            case R.id.fl_plans_entry:
                final User user2 = AccountManager.getInstance().getLoginAccount(MyProfileActivity.this);
                if (user2 != null) {
                    Intent intent2 = new Intent(MyProfileActivity.this, StrategyListActivity.class);
                    intent2.putExtra("userId", String.valueOf(user2.getUserId()));
                    intent2.putExtra("user_name", user2.getNickName());
                    startActivity(intent2);
                } else {
                    Intent LoginIntent = new Intent(MyProfileActivity.this, LoginActivity.class);
                    startActivityForResult(LoginIntent, CODE_PLANS);
                    overridePendingTransition(R.anim.push_bottom_in, R.anim.slide_stay);
                }
                break;

            case R.id.fl_tracks_entry:
                User user3 = AccountManager.getInstance().getLoginAccount(MyProfileActivity.this);
                if (user3 == null) {
                    Intent logIntent = new Intent(MyProfileActivity.this, LoginActivity.class);
                    startActivity(logIntent);
                } else {
                    Intent tracks_intent = new Intent(MyProfileActivity.this, StrategyMapActivity.class);
                    tracks_intent.putExtra("isMyFootPrint", true);
                    tracks_intent.putParcelableArrayListExtra("myfootprint", all_foot_print_list);
                    tracks_intent.putExtra("title", tvTracksCount.getText().toString());
                    startActivityForResult(tracks_intent, CODE_FOOTPRINT);
                }
                break;
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

    public static Bitmap readBitMap(Context context, int resId) {
        try {
            Bitmap bitmap = ImageCache.getInstance().get(String.valueOf(resId));
            if (bitmap==null){
                BitmapFactory.Options opt = new BitmapFactory.Options();
                opt.inPreferredConfig = Bitmap.Config.RGB_565;
                opt.inPurgeable = true;
                opt.inInputShareable = true;
                InputStream is = context.getResources().openRawResource(resId);
                bitmap = BitmapFactory.decodeStream(is, null, opt);
                ImageCache.getInstance().put(String.valueOf(resId),bitmap);
            }
            return bitmap;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
    public void refreshLoginStatus() {
        User user = AccountManager.getInstance().getLoginAccount(MyProfileActivity.this);
        all_foot_print_list.clear();
        if (user == null) {
            avatarIv.setImageResource(R.drawable.ic_home_userentry_unlogin);
            //nickNameTv.setText("旅行派");
            //idTv.setText("未登录");
            tvPictureCount.setText("0图");
            tvPlansCount.setText("0条");
            tvTracksCount.setText("0国0城市");
            tvLevel.setText("Lv0");
            tvLevel.setBackgroundResource(R.drawable.ic_home_level_bg_unknown);
            genderFrame.setImageResource(R.drawable.ic_home_header_unlogin);
            fl_gender_bg.setForeground(getResources().getDrawable(R.drawable.ic_home_avatar_border_unknown));
            constellationIv.setImageResource(R.drawable.ic_home_constellation_unknown);
        } else {
            if (!user.getGender().equals(Sex)){
                Sex=user.getGender();
                if (user.getGender().equalsIgnoreCase("M")) {
                    /**
                     * OOM Exception
                     */
                    genderFrame.setImageBitmap(readBitMap(MyProfileActivity.this, R.drawable.ic_home_header_boy));
                    //               genderFrame.setImageResource(R.drawable.ic_home_header_boy);
                    fl_gender_bg.setForeground(getResources().getDrawable(R.drawable.ic_home_avatar_border_boy));
                    tvLevel.setBackgroundResource(R.drawable.ic_home_level_bg_boy);
                } else if (user.getGender().equalsIgnoreCase("F")) {
                    genderFrame.setImageBitmap(readBitMap(MyProfileActivity.this, R.drawable.ic_home_header_girl));
                    //            genderFrame.setImageResource(R.drawable.ic_home_header_girl);
                    fl_gender_bg.setForeground(getResources().getDrawable(R.drawable.ic_home_avatar_border_girl));
                    tvLevel.setBackgroundResource(R.drawable.ic_home_level_bg_girl);
                } else {
                    genderFrame.setImageBitmap(readBitMap(MyProfileActivity.this, R.drawable.ic_home_header_unlogin));
                    //               genderFrame.setImageResource(R.drawable.ic_home_header_unlogin);
                    fl_gender_bg.setForeground(getResources().getDrawable(R.drawable.ic_home_avatar_border_unknown));
                    tvLevel.setBackgroundResource(R.drawable.ic_home_level_bg_unknown);
                }
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
            tvPictureCount.setText(picNum + "张");
            tvTracksCount.setText(countryCount + "国" + cityCount + "城市");
            tvPlansCount.setText(guideCount + "条");
            //nickNameTv.setText(user.getNickName());
            //idTv.setText("ID：" + user.getUserId());
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
            if (info!=null){
                ImageLoader.getInstance().displayImage(info.getAvatar(), avatarIv, options);
            }else ImageLoader.getInstance().displayImage(user.getAvatar(), avatarIv, options);

            //  getUserPicsNum(user.getUserId());
        }
    }
    private void showSelectedPics(ArrayList<String> pics) {
        if (pics.size()==0){
            return;
        }
        IntentUtils.intentToPicGallery2(MyProfileActivity.this, pics, 0);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }


    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("page_home_mine");
        new Thread() {
            public void run() {
                handler.post(runnableUi);
            }
        }.start();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == LoginActivity.REQUEST_CODE_REG) {
                User user = (User) data.getSerializableExtra("user");
                try {
                    DialogManager.getInstance().showLoadingDialog(MyProfileActivity.this, "正在登录");
                }catch (Exception e){
                    DialogManager.getInstance().dissMissLoadingDialog();
                }
                imLogin(user);
            } else if (requestCode == CODE_PLANS) {
                final User user2 = AccountManager.getInstance().getLoginAccount(MyProfileActivity.this);
                if (user2 != null) {
                    Intent intent2 = new Intent(MyProfileActivity.this, StrategyListActivity.class);
                    intent2.putExtra("userId", String.valueOf(user2.getUserId()));
                    intent2.putExtra("user_name", user2.getNickName());
                    startActivity(intent2);
                }
            }
        }
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
                        tvPictureCount.setText(picsNum + "张");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


            @Override
            public void doFailure(Exception error, String msg, String method) {
                tvPictureCount.setText(picsNum + "张");
                ToastUtil.getInstance(MyProfileActivity.this).showToast("好像没有网络额~");
            }

            @Override
            public void doFailure(Exception error, String msg, String method, int code) {

            }
        });

    }


    private void imLogin(final User user) {

        AccountManager.getInstance().saveLoginAccount(MyProfileActivity.this, user);

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
                if (!MyProfileActivity.this.isFinishing())
                    ToastUtil.getInstance(MyProfileActivity.this).showToast(getResources().getString(R.string.request_network_failed));
            }

            @Override
            public void doFailure(Exception error, String msg, String method, int code) {

            }
        });
        // 进入主页面
        MyProfileActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                DialogManager.getInstance().dissMissLoadingDialog();
                refreshLoginStatus();
            }
        });

    }
    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("page_home_mine");
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


}
