package com.xuejian.client.lxp.module.my;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aizou.core.dialog.ToastUtil;
import com.aizou.core.http.HttpCallBack;
import com.aizou.core.utils.LocalDisplay;
import com.aizou.core.utils.SharePrefUtil;
import com.nineoldandroids.animation.ValueAnimator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.umeng.analytics.MobclickAgent;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.PeachBaseActivity;
import com.xuejian.client.lxp.bean.ContactListBean;
import com.xuejian.client.lxp.bean.LocBean;
import com.xuejian.client.lxp.common.account.AccountManager;
import com.xuejian.client.lxp.common.api.UserApi;
import com.xuejian.client.lxp.common.dialog.DialogManager;
import com.xuejian.client.lxp.common.gson.CommonJson;
import com.xuejian.client.lxp.common.imageloader.UILUtils;
import com.xuejian.client.lxp.common.utils.CommonUtils;
import com.xuejian.client.lxp.common.utils.ImageCache;
import com.xuejian.client.lxp.common.utils.IntentUtils;
import com.xuejian.client.lxp.common.widget.CustomFrameLayout;
import com.xuejian.client.lxp.db.User;
import com.xuejian.client.lxp.db.UserDBManager;
import com.xuejian.client.lxp.module.dest.StrategyMapActivity;
import com.xuejian.client.lxp.module.toolbox.StrategyListActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by xuyongchen on 15/8/27.
 */
public class MyProfileActivity  extends PeachBaseActivity implements  View.OnClickListener{
    public final static int CODE_PLANS = 102;
    public final static int CODE_FOOTPRINT = 103;
    public final static int CODE_PICS = 104;
    private ArrayList<String> all_pics = new ArrayList<String>();

    @InjectView(R.id.profile_fragment_view)
    CustomFrameLayout profileFragmentView;
    @InjectView(R.id.my_profile_edit)
    TextView myProfileEdit;
    @InjectView(R.id.iv_avatar)
    ImageView avatarIv;
    //@InjectView(R.id.iv_constellation)
    //TextView constellationIv;

    @InjectView(R.id.tv_pictures_count)
    TextView tvPictureCount;

    @InjectView(R.id.user_info_p)
    LinearLayout userInfoP;

    @InjectView(R.id.tv_plans_count)
    TextView tvPlansCount;
    @InjectView(R.id.tv_tracks_count)
    TextView tvTracksCount;

    @InjectView(R.id.iv_user_name)
    TextView ivUserName;//姓名

    @InjectView(R.id.iv_age)
    TextView ivAge;//年龄

  //  @InjectView(R.id.iv_gender)
  //  ImageView ivGender;//性别

    @InjectView(R.id.iv_city)
    TextView ivCity;

    @InjectView(R.id.iv_about_me)
    TextView ivAboutMe;

    @InjectView(R.id.title_bar_profile)
    RelativeLayout title_bar;
    private TextView notice;
    private int picsNum = 0;
    private String Sex;
    private int[] pictures= new int[]{

    };

    private int maxHeight;

    private int startMarginTop=0;
    private Boolean isViewVisible=true;

    ArrayList<LocBean> all_foot_print_list = new ArrayList<LocBean>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_profile_activity);
        ButterKnife.inject(this);
        myProfileEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                turnToEditProfile();
            }
        });
        //genderFrame.setOnClickListener(this);
       // findViewById(R.id.tv_edit_profile).setOnClickListener(this);


        findViewById(R.id.fl_plans_entry).setOnClickListener(this);
        findViewById(R.id.fl_tracks_entry).setOnClickListener(this);
        findViewById(R.id.iv_head_back).setOnClickListener(this);
        notice = (TextView) findViewById(R.id.unread_msg_notify);
        if (SharePrefUtil.getBoolean(MyProfileActivity.this,"firstReg",false))notice.setVisibility(View.VISIBLE);
        refreshLoginStatus();
        refreshUserInfo();
        User user= AccountManager.getInstance().getLoginAccount(this);
        if(user!=null){
            initScrollView(user.getUserId());
        }

        profileFragmentView.setOnInterDispatchListener(new CustomFrameLayout.OnInterDispatchListener() {
            @Override
            public void onInterEvent(int upordown) {
                if(upordown==1){
                    if(isViewVisible==true){
                        //profileFragmentView.setCanInterTitleDown(true);
                        //userInfoP.setVisibility(View.GONE);

                        if(startMarginTop==0){
                            startMarginTop=CommonUtils.dip2px(MyProfileActivity.this,52)-userInfoP.getHeight();
                        }

                        ValueAnimator animator = ValueAnimator.ofInt(0,startMarginTop);
                        animator.setTarget(userInfoP);
                        animator.setDuration(300).start();
                        animator.setInterpolator(new AccelerateDecelerateInterpolator());
                        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                @Override
                                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                                    int maginTop = (int) valueAnimator.getAnimatedValue();
                                    LinearLayout.LayoutParams vl = (LinearLayout.LayoutParams)userInfoP.getLayoutParams();
                                    vl.setMargins(0,maginTop,0,0);
                                    userInfoP.setLayoutParams(vl);
                                    if (maginTop==startMarginTop) {
                                       // profileFragmentView.setCanInterTitleUp(false);
                                        //userInfoP.setVisibility(View.GONE);
                                        title_bar.setBackgroundResource(R.color.color_text_iii);
                                        isViewVisible=false;
                                        profileFragmentView.setIsDrawawing(false);

                                    }
                                }

                            });
                        //userInfoP.startAnimation(AnimationUtils.loadAnimation(MyProfileActivity.this,R.anim.scale_title_animation));
                    }
                }else if(upordown==2){
                    if(!isViewVisible){
                       // userInfoP.setVisibility(View.VISIBLE);
                            // profileFragmentView.setCanInterTitleUp(true);
                            title_bar.setBackgroundResource(R.color.transparent_color);
                            ValueAnimator animator = ValueAnimator.ofInt(startMarginTop,0);
                            animator.setTarget(userInfoP);
                            animator.setDuration(300).start();
                            animator.setInterpolator(new AccelerateDecelerateInterpolator());
                            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                @Override
                                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                                    int marginTop = (int) valueAnimator.getAnimatedValue();
                                    LinearLayout.LayoutParams vl = (LinearLayout.LayoutParams)userInfoP.getLayoutParams();
                                    vl.setMargins(0,marginTop,0,0);

                                    userInfoP.setLayoutParams(vl);

                                    if (marginTop == 0) {
                                        //profileFragmentView.setCanInterTitleDown(false);
                                        Log.e("下拉动结束","----------------------------");
                                        isViewVisible=true;
                                        profileFragmentView.setIsDrawawing(false);
                                    }
                                }

                            });
                    }


                }
            }
        });

    }


    private void  turnToEditProfile(){
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
           /* case R.id.iv_more_header_frame_gender1:
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
                //已删除！！！
                break;*/

           /* case R.id.rl_picture_entry:
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
                //个人相册
                break;*/

            case R.id.my_profile_edit:
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
            case R.id.iv_head_back:
                finish();
                break;
        }
    }


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

    private void refreshUserInfo() {
        final User user = AccountManager.getInstance().getLoginAccount(this);
        if (user != null) {
            if (user.getGender().equalsIgnoreCase("M")) {
                // iv_header_frame_gender.setImageResource(R.drawable.ic_home_header_boy);
            } else if (user.getGender().equalsIgnoreCase("F")) {
                // iv_header_frame_gender.setImageResource(R.drawable.ic_home_header_girl);
            } else {
                // iv_header_frame_gender.setImageResource(R.drawable.ic_home_header_unlogin);
            }

            UserApi.getUserInfo(String.valueOf(user.getUserId()), new HttpCallBack<String>() {
                @Override
                public void doSuccess(String result, String method) {
                    CommonJson<User> userResult = CommonJson.fromJson(result, User.class);
                    if (userResult.code == 0) {
                        AccountManager.getInstance().saveLoginAccount(MyProfileActivity.this, userResult.result);
                        refreshLoginStatus();
                       // refreshUserInfo();

                    }

                }

                @Override
                public void doFailure(Exception error, String msg, String method) {

                }

                @Override
                public void doFailure(Exception error, String msg, String method, int code) {

                }
            });
        }
    }
    public int getAge(String birth) {
        if (TextUtils.isEmpty(birth)) return 0;
        int age = 0;
        String birthType = birth.substring(0, 4);
        int birthYear = Integer.parseInt(birthType);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
        String date = sdf.format(new java.util.Date());
        age = Integer.parseInt(date) - birthYear;
        return age;
    }

    public void refreshLoginStatus() {
        User user = AccountManager.getInstance().getLoginAccount(MyProfileActivity.this);
        all_foot_print_list.clear();
        if (user == null) {
            ImageLoader.getInstance().displayImage("", avatarIv, new DisplayImageOptions.Builder()
                    .showImageForEmptyUri(R.drawable.messages_bg_useravatar)
                    .showImageOnFail(R.drawable.messages_bg_useravatar)
                    .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                    .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
                    .displayer(new RoundedBitmapDisplayer(LocalDisplay.dp2px(100))) // 设置成圆角图片
                    .build());
            tvPictureCount.setText("0图");
            tvPlansCount.setText("0条");
            tvTracksCount.setText("0国0城市");
          //  constellationIv.setText("星座");
        } else {
          /*  if (!user.getGender().equals(Sex)){
                ivGender.setVisibility(View.VISIBLE);
                Sex=user.getGender();
                if (user.getGender().equalsIgnoreCase("M")) {
                    ivGender.setImageResource(R.drawable.icon_boy);
                } else if (user.getGender().equalsIgnoreCase("F")) {
                    ivGender.setImageResource(R.drawable.icon_girl);
                } else {
                    ivGender.setVisibility(View.GONE);
                }
            }*/
            int countryCount = 0;
            int cityCount = 0;
            String level = "0";
            int guideCount = 0;
            int picNum=0;
            User info=null;
            String username="姓名";
            int age =0;
            String city="";
            String aboutMe=null;
            if (AccountManager.getInstance().getLoginAccountInfo() != null) {
                info = AccountManager.getInstance().getLoginAccountInfo();
                guideCount = info.getGuideCnt();
                level = info.getLevel();
                countryCount=info.getCountryCnt();
                cityCount=info.getTrackCnt();
                picNum=info.getAlbumCnt();
                username=info.getNickName();
                age=getAge(info.getBirthday());
                city=info.getResidence();
                aboutMe=info.getSignature();
            }
            if(aboutMe!=null && aboutMe.trim().length()>0){
                ivAboutMe.setText(aboutMe);
            }

            ivUserName.setText(username);
            ivAge.setText(age+"");
            ivCity.setText(city+"");
            tvPictureCount.setText(picNum + "图");
            tvTracksCount.setText(countryCount + "国" + cityCount + "城市");
            tvPlansCount.setText(guideCount + "条");
           // constellationIv.setText("星座");
            DisplayImageOptions options = new DisplayImageOptions.Builder()
                    .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                    .showImageForEmptyUri(R.drawable.ic_home_talklist_default_avatar)
                    .showImageOnFail(R.drawable.ic_home_talklist_default_avatar)
                    .resetViewBeforeLoading(true)
                    .cacheOnDisk(true)
                    .build();
            ImageLoader.getInstance().displayImage(user.getAvatar(), avatarIv, new DisplayImageOptions.Builder()
                    .showImageForEmptyUri(R.drawable.messages_bg_useravatar)
                    .showImageOnFail(R.drawable.messages_bg_useravatar)
                    .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                    .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
                    .displayer(new RoundedBitmapDisplayer(LocalDisplay.dp2px(100))) // 设置成圆角图片
                    .build());
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
                        tvPictureCount.setText(picsNum + "图");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


            @Override
            public void doFailure(Exception error, String msg, String method) {
                tvPictureCount.setText(picsNum + "图");
                ToastUtil.getInstance(MyProfileActivity.this).showToast("好像没有网络额~");
            }

            @Override
            public void doFailure(Exception error, String msg, String method, int code) {

            }
        });

    }



    public void initScrollView(long userId) {
        UserApi.getUserPicAlbumn(String.valueOf(userId), new HttpCallBack<String>() {
            @Override
            public void doSuccess(String result, String method) {
                JSONObject jsonObject = null;
                try {
                    ArrayList<String> ids = new ArrayList<String>();
                    jsonObject = new JSONObject(result);
                    if (jsonObject.getInt("code") == 0) {
                        JSONArray object = jsonObject.getJSONArray("result");
                        for (int i = 0; i < object.length(); i++) {
                            ids.add(object.getJSONObject(i).getString("id"));
                            JSONArray imgArray = object.getJSONObject(i).getJSONArray("image");
                            all_pics.add(imgArray.getJSONObject(0).getString("url"));
                        }
                      //  initScrollView(all_pics, ids);
                        //  refreshUserPics(all_pics);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


            @Override
            public void doFailure(Exception error, String msg, String method) {
                ToastUtil.getInstance(MyProfileActivity.this).showToast("好像没有网络额~");
            }

            @Override
            public void doFailure(Exception error, String msg, String method, int code) {

            }
        });
    }
   /* public void initScrollView(final ArrayList<String> picList, final ArrayList<String> ids) {
        all_pics_sv.removeAllViews();
        LinearLayout llPics = new LinearLayout(this);
        llPics.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        llPics.removeAllViews();
        for (int i = 0; i < picList.size(); i++) {
            View view = View.inflate(MyProfileActivity.this, R.layout.my_all_pics_cell, null);
            ImageView my_pics_cell = (ImageView) view.findViewById(R.id.my_pics_cell);
            final int pos = i;
            ImageLoader.getInstance().displayImage(picList.get(i), my_pics_cell, UILUtils.getDefaultOption());
            my_pics_cell.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    IntentUtils.intentToPicGallery2(MyProfileActivity.this, picList, pos);
                }
            });

            //          }
            llPics.addView(view);
        }
        all_pics_sv.addView(llPics);
    }*/


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
