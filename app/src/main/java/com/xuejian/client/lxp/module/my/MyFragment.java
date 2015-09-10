package com.xuejian.client.lxp.module.my;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aizou.core.dialog.ToastUtil;
import com.aizou.core.http.HttpCallBack;
import com.aizou.core.utils.GsonTools;
import com.nineoldandroids.animation.ValueAnimator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.umeng.analytics.MobclickAgent;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.PeachBaseFragment;
import com.xuejian.client.lxp.bean.ModifyResult;
import com.xuejian.client.lxp.bean.StrategyBean;
import com.xuejian.client.lxp.common.account.AccountManager;
import com.xuejian.client.lxp.common.api.OtherApi;
import com.xuejian.client.lxp.common.api.TravelApi;
import com.xuejian.client.lxp.common.dialog.ComfirmDialog;
import com.xuejian.client.lxp.common.dialog.DialogManager;
import com.xuejian.client.lxp.common.dialog.PeachMessageDialog;
import com.xuejian.client.lxp.common.gson.CommonJson;
import com.xuejian.client.lxp.common.gson.CommonJson4List;
import com.xuejian.client.lxp.common.utils.CommonUtils;
import com.xuejian.client.lxp.common.utils.ConstellationUtil;
import com.xuejian.client.lxp.common.utils.PreferenceUtils;
import com.xuejian.client.lxp.common.widget.CustomFrameLayout;
import com.xuejian.client.lxp.common.widget.RoundImageBoarderView;
import com.xuejian.client.lxp.common.widget.swipelistview.adapters.BaseSwipeAdapter;
import com.xuejian.client.lxp.config.Constant;
import com.xuejian.client.lxp.db.User;
import com.xuejian.client.lxp.db.UserDBManager;
import com.xuejian.client.lxp.module.MainActivity;
import com.xuejian.client.lxp.module.dest.SelectDestActivity;
import com.xuejian.client.lxp.module.dest.StrategyActivity;
import com.xuejian.client.lxp.module.toolbox.HisMainPageActivity;
import com.xuejian.client.lxp.module.toolbox.im.AddContactActivity;
import com.xuejian.client.lxp.module.toolbox.im.GroupsActivity;
import com.xuejian.client.lxp.module.toolbox.im.adapter.ContactAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;

public class MyFragment extends PeachBaseFragment implements View.OnClickListener {
    private View rootView;
    private ListView my_fragment_list;
    //private ScrollView myScrollView;
    private MyPlaneAdapter myPlaneAdapter;
    private ContactAdapter myContactAdapter;
    private List<User> contactList;

    private LinearLayout plane_panel;
    private LinearLayout contact_panel;
    private TextView travel_plane;
    private TextView contact_people;
    private RoundImageBoarderView user_avatar;
    private TextView nameAndId;
    private TextView other_infos;
    private CustomFrameLayout fragment_view;
    private LinearLayout user_info_pannel;
    private RelativeLayout my_panpan_frame;

    private View travel_pline;
    private View contact_pline;
    private ImageView add_plane_icon;
    private int currentIndex = 0;
    private int startmagrinTop = 0;
    int mCurrentPage = 0;
    private User user;
    private ArrayList<StrategyBean> planeList;
    private static final int RESULT_PLAN_DETAIL = 0x222;
    public static final int REQUEST_CODE_NEW_PLAN = 0x22;
    DisplayImageOptions options;
    private boolean isPanelVisible=true;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_my, null);
        ButterKnife.inject(this, rootView);
        options = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.messages_bg_useravatar)
                .showImageOnFail(R.drawable.messages_bg_useravatar)
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中// 设置成圆角图片
                .build();
        user = AccountManager.getInstance().getLoginAccount(getActivity());
        planeList = new ArrayList<StrategyBean>();
        my_fragment_list = (ListView) rootView.findViewById(R.id.my_fragment_list);
        fragment_view = (CustomFrameLayout) rootView.findViewById(R.id.fragment_view);
        user_info_pannel = (LinearLayout) rootView.findViewById(R.id.user_info_pannel);
        user_avatar = (RoundImageBoarderView) rootView.findViewById(R.id.user_avatar);
        nameAndId = (TextView) rootView.findViewById(R.id.nameAndId);
        other_infos = (TextView) rootView.findViewById(R.id.other_infos);
        my_panpan_frame = (RelativeLayout)rootView.findViewById(R.id.my_panpan_frame);
        add_plane_icon = (ImageView)rootView.findViewById(R.id.add_plane_icon);
        initHeadTitleView(user);
        initTabView();
        TextView settingBtn = (TextView) rootView.findViewById(R.id.setting_btn);
        settingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent settingIntent = new Intent(getActivity(), SettingActivity.class);
                startActivity(settingIntent);
            }
        });
        contactList = new ArrayList<User>();
        View view = new View(getActivity());
        AbsListView.LayoutParams abp = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT);
        abp.height = 200;
        view.setLayoutParams(abp);
        my_fragment_list.addFooterView(view);
        myContactAdapter = new ContactAdapter(getActivity(), R.layout.row_contact, contactList);

        myPlaneAdapter = new MyPlaneAdapter(getActivity(), planeList);
        my_fragment_list.setAdapter(myPlaneAdapter);

        fragment_view.setOnInterDispatchListener(new CustomFrameLayout.OnInterDispatchListener() {
            @Override
            public void onInterEvent(int upordown) {

                if (upordown == 1) {
                    if (isPanelVisible == true) {

                        if (startmagrinTop == 0) {
                            startmagrinTop = CommonUtils.dip2px(getActivity(), 52) - user_info_pannel.getHeight();
                        }

                        ValueAnimator valueAnimator = ValueAnimator.ofInt(0, startmagrinTop);
                        valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
                        valueAnimator.setTarget(user_info_pannel);
                        valueAnimator.setDuration(300).start();
                        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator valueAnimator) {

                                int marginTop = (int) valueAnimator.getAnimatedValue();
                                LinearLayout.LayoutParams fp = (LinearLayout.LayoutParams) user_info_pannel.getLayoutParams();
                                fp.setMargins(0, marginTop, 0, 0);
                                user_info_pannel.setLayoutParams(fp);
                                if (startmagrinTop == marginTop) {
                                    isPanelVisible = false;
                                    my_panpan_frame.setBackgroundResource(R.color.color_text_iii);
                                    fragment_view.setIsDrawawing(false);

                                }
                                //fragment_view.setAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.transparent_to_small));

                            }
                        });

                    }
                } else if (upordown == 2) {
                    if (!isPanelVisible) {
                        my_fragment_list.setSelection(0);
                        ValueAnimator valueAnimator = ValueAnimator.ofInt(startmagrinTop, 0);
                        valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
                        valueAnimator.setTarget(user_info_pannel);
                        valueAnimator.setDuration(300).start();
                        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator valueAnimator) {

                                int marginTop = (int) valueAnimator.getAnimatedValue();
                                LinearLayout.LayoutParams fp = (LinearLayout.LayoutParams) user_info_pannel.getLayoutParams();
                                fp.setMargins(0, 0, 0, 0);
                                user_info_pannel.setLayoutParams(fp);
                                if (startmagrinTop == marginTop) {
                                    my_panpan_frame.setBackgroundResource(R.color.transparent_color);
                                    isPanelVisible = true;
                                    fragment_view.setIsDrawawing(false);
                                }
                                //fragment_view.setAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.transparent_to_small));

                            }
                        });
                    }
                }
            }
        });

        user_info_pannel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MyProfileActivity.class);
                startActivity(intent);
            }
        });
        my_fragment_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    if (currentIndex == 0) {
                        if (user != null) {
                            StrategyBean bean = (StrategyBean) planeList.get(position);
                            Intent intent = new Intent(getActivity(), StrategyActivity.class);
                            intent.putExtra("id", bean.id);
                            intent.putExtra("userId", user.getUserId() + "");
                            startActivityForResult(intent, RESULT_PLAN_DETAIL);
                        }
                    } else {
                        String username = contactList.get(position).getNickName();
                        if (Constant.NEW_FRIENDS_USERNAME.equals(username)) {
                            MobclickAgent.onEvent(getActivity(), "cell_item_new_friends_request");
                            // 进入添加好友页面
                            startActivity(new Intent(getActivity(), AddContactActivity.class));
                            getActivity().overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
                        } else if (Constant.GROUP_USERNAME.equals(username)) {
                            // 进入群聊列表页面
                            startActivity(new Intent(getActivity(), GroupsActivity.class));
                            getActivity().overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
                        } else {
                            startActivity(new Intent(getActivity(), HisMainPageActivity.class).putExtra("userId", contactList.get(position).getUserId()));
                            getActivity().overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
                        }
                    }
                } catch (Exception ex) {

                }

            }
        });

        add_plane_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SelectDestActivity.class);
                startActivityForResult(intent, REQUEST_CODE_NEW_PLAN);
            }
        });
        return rootView;

    }


    private void changeTabView(int index) {
        switch (index) {
            case 0:
                travel_plane.setTextColor(getResources().getColor(R.color.app_theme_color));
                travel_plane.setCompoundDrawablesWithIntrinsicBounds(R.drawable.my_plan_selected,0,0,0);
                contact_people.setCompoundDrawablesWithIntrinsicBounds(R.drawable.note_normal,0,0,0);
                contact_people.setTextColor(getResources().getColor(R.color.color_text_ii));
                travel_pline.setBackgroundResource(R.color.app_theme_color);
                contact_pline.setBackgroundResource(R.color.color_text_ii);
                add_plane_icon.setVisibility(View.VISIBLE);
                break;
            case 1:
                travel_plane.setCompoundDrawablesWithIntrinsicBounds(R.drawable.my_plan_normal,0,0,0);
                contact_people.setCompoundDrawablesWithIntrinsicBounds(R.drawable.note_selected,0,0,0);
                travel_plane.setTextColor(getResources().getColor(R.color.color_text_ii));

                contact_people.setTextColor(getResources().getColor(R.color.app_theme_color));
                travel_pline.setBackgroundResource(R.color.color_text_ii);
                contact_pline.setBackgroundResource(R.color.app_theme_color);
                add_plane_icon.setVisibility(View.GONE);
                break;
        }
    }

    private void initHeadTitleView(User user) {
        if (user != null) {
            ImageLoader.getInstance().displayImage(user.getAvatar(), user_avatar, options);
            StringBuffer nameSb = new StringBuffer();
            int nameLenth = 0;
            if (user.getNickName() != null) {
                nameSb.append(user.getNickName());
                nameLenth = user.getNickName().length();
            }
            int idLenght=0;
            if (user.getUserId() != null) {
                nameSb.append("  " + user.getUserId());
                idLenght=(user.getUserId()+"").length();
            }
            SpannableString spannableString = new SpannableString(nameSb.toString());
            if(idLenght>0){
                spannableString.setSpan(new AbsoluteSizeSpan(13,true),nameLenth+2,nameLenth+2+idLenght,0);
                nameAndId.setText(spannableString);
            }else{
                nameAndId.setText(nameSb.toString());
            }



            StringBuffer otherSb = new StringBuffer();
            if (user.getGender() != null) {
                if (user.getGender().equalsIgnoreCase("M")) {
                    otherSb.append("男");
                } else if (user.getGender().equalsIgnoreCase("F")) {
                    otherSb.append("女");
                } else if (user.getGender().equalsIgnoreCase("S")) {
                    otherSb.append("保密");
                } else {
                    otherSb.append("一言难尽");
                }
            }

            if (user.getBirthday() != null) {
                otherSb.append("  " + ConstellationUtil.calculateConstellationZHname(user.getBirthday()));
            }
            if (user.getLevel() != null) {
                otherSb.append("  " + "LV" + user.getLevel());
            }

            other_infos.setText(otherSb.toString());
        }
    }


    public void requestUserInfo(User user) {
        initHeadTitleView(user);
        getContactList();
        getStrategyListData(user);
    }


    private void initTabView() {
        plane_panel = (LinearLayout) rootView.findViewById(R.id.plane_panel);
        contact_panel = (LinearLayout) rootView.findViewById(R.id.contact_panel);
        travel_plane = (TextView) rootView.findViewById(R.id.travel_plane);
        contact_people = (TextView) rootView.findViewById(R.id.contact_people);
        travel_pline = rootView.findViewById(R.id.travel_pline);
        contact_pline = rootView.findViewById(R.id.contact_pline);
        changeTabView(0);

        plane_panel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentIndex != 0) {
                    currentIndex = 0;
                    changeTabView(0);
                    my_fragment_list.setAdapter(myPlaneAdapter);
                }
            }
        });
        contact_panel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentIndex != 1) {
                    currentIndex = 1;
                    changeTabView(1);
                    my_fragment_list.setAdapter(myContactAdapter);
                }
            }
        });

    }

    private void getContactList() {
        Map<Long, User> users = AccountManager.getInstance().getContactList(getActivity());
        if (users == null) {
            return;
        }
        contactList.clear();
        contactList.addAll(UserDBManager.getInstance().getContactListWithoutGroup());
        List<User> del = new ArrayList<>();
        for (User user : contactList) {
            if (user.getUserId() == 10000 || user.getUserId() == 10001) del.add(user);
        }
        contactList.removeAll(del);
        // 排序
        Collections.sort(contactList, new Comparator<User>() {

            @Override
            public int compare(User lhs, User rhs) {
                return lhs.getHeader().compareTo(rhs.getHeader());
            }
        });
//		// 加入"申请与通知"和"群聊"
        // 把"申请与通知"添加到首位
        User newFriends = new User();
        newFriends.setUserId(2);
        newFriends.setNickName("item_new_friends");
        newFriends.setType(1);
        UserDBManager.getInstance().saveContact(newFriends);
        contactList.add(0, newFriends);
    }

    private void getStrategyListData(User user) {
        if (user != null) {
            TravelApi.getStrategyPlannedList(user.getUserId() + "", mCurrentPage, null, new HttpCallBack<String>() {
                @Override
                public void doSuccess(String result, String method) {
                    CommonJson4List<StrategyBean> strategyListResult = CommonJson4List.fromJson(result, StrategyBean.class);
                    if (strategyListResult.code == 0) {
                        if (mCurrentPage == 0) {
                            planeList.clear();
                            planeList.addAll(strategyListResult.result);
                        } else {
                            mCurrentPage++;
                            planeList.addAll(strategyListResult.result);
                        }

                        if (mCurrentPage == 0 || myPlaneAdapter.getCount() < OtherApi.PAGE_SIZE * 2) {
                            cachePage();
                        }
                    }
                    myPlaneAdapter.notifyDataSetChanged();
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
        }

    }


    private void cachePage() {
        if (user == null) {
            return;
        }
        int size = myPlaneAdapter.getCount();
        if (size > OtherApi.PAGE_SIZE) {
            size = OtherApi.PAGE_SIZE;
        }
        List<StrategyBean> cd = planeList.subList(0, size);
        PreferenceUtils.cacheData(getActivity(), String.format("%s_plans", AccountManager.getCurrentUserId()), GsonTools.createGsonString(cd));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == getActivity().RESULT_OK) {
            if (requestCode == RESULT_PLAN_DETAIL) {
                StrategyBean sb = data.getParcelableExtra("strategy");
            }else if(requestCode == REQUEST_CODE_NEW_PLAN){
                StrategyBean sb = data.getParcelableExtra("strategy");
                if (sb != null) {
                    PreferenceUtils.cacheData(getActivity(), "last_strategy", GsonTools.createGsonString(sb));
                }
                mCurrentPage=0;
                getStrategyListData(user);

            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onResume() {
        super.onResume();
        final User user = AccountManager.getInstance().getLoginAccount(getActivity());
        if (user != null) {
            requestUserInfo(user);
        } else {
            MainActivity activity = (MainActivity) getActivity();
            activity.setTabForLogout();
        }
    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {


           /* case R.id.my_profile:
                Intent intent = new Intent(getActivity(),MyProfileActivity.class);
                startActivity(intent);
                break;*/
            default:
                break;
        }
    }


    class MyPlaneAdapter extends BaseSwipeAdapter {
        private ArrayList<StrategyBean> data;
        private Context context;
        private LayoutInflater inflater;
        private DisplayImageOptions picOptions;
        private LinearLayout swipe_ll;

        public MyPlaneAdapter(Context context, ArrayList<StrategyBean> data) {
            this.context = context;
            this.data = data;
            inflater = LayoutInflater.from(context);
            picOptions = new DisplayImageOptions.Builder()
                    .cacheInMemory(true)
                    .cacheOnDisk(true).bitmapConfig(Bitmap.Config.ARGB_8888)
                    .resetViewBeforeLoading(true)
                    .showImageOnFail(R.drawable.pic_loadfail)
                    .showImageOnLoading(R.drawable.pic_loadfail)
                    .showImageForEmptyUri(R.drawable.pic_loadfail)
                    .imageScaleType(ImageScaleType.IN_SAMPLE_INT).build();
        }

        @Override
        public void fillValues(int position, View convertView) {
            final StrategyBean strategyBean = data.get(position);

            ImageView plane_pic = (ImageView) convertView.findViewById(R.id.plane_pic);
            TextView plane_spans = (TextView) convertView.findViewById(R.id.plane_spans);
            TextView plane_title = (TextView) convertView.findViewById(R.id.plane_title);
            TextView city_hasGone = (TextView) convertView.findViewById(R.id.city_hasGone);
            TextView create_time = (TextView) convertView.findViewById(R.id.create_time);
            ImageView travel_hasGone = (ImageView)convertView.findViewById(R.id.travel_hasGone);


            TextView mCheck = (TextView)convertView.findViewById(R.id.sign_up);
            TextView mDelete = (TextView)convertView.findViewById(R.id.delete);
            plane_spans.setText(strategyBean.dayCnt + "天");
            plane_title.setText(strategyBean.title);
            city_hasGone.setText(strategyBean.summary);
            //plane_pic.setImageResource(R.drawable.pic_loadfail);
            create_time.setText("创建时间: " + CommonUtils.getTimestampString(new Date(strategyBean.updateTime)));

            if(strategyBean.images!=null && strategyBean.images.size()>0 && strategyBean.images.get(0)!=null && strategyBean.images.get(0).url!=null){
                plane_pic.setTag(strategyBean.images.get(0).url);
                if(plane_pic.getTag()!=null && plane_pic.getTag().equals(strategyBean.images.get(0).url)){
                    ImageLoader.getInstance().displayImage(strategyBean.images.get(0).url, plane_pic,picOptions);
                }

            }else{
                plane_pic.setImageResource(R.drawable.pic_loadfail);
            }
            if(strategyBean!=null){
                if (strategyBean.status.equals("traveled")) {
                    travel_hasGone.setVisibility(View.VISIBLE);
                } else {
                    travel_hasGone.setVisibility(View.GONE);
                }
            }else{
                travel_hasGone.setVisibility(View.GONE);
            }
            mDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteItem(strategyBean);
                }
            });
            mCheck.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MobclickAgent.onEvent(getActivity(), "ell_item_plans_change_status");
                    if (strategyBean.status.equals("planned")) {
                        haveBeenVisited(strategyBean);
                        notifyDataSetChanged();
                        final ComfirmDialog cdialog = new ComfirmDialog(context);
                        cdialog.findViewById(R.id.tv_dialog_title).setVisibility(View.VISIBLE);
                        cdialog.findViewById(R.id.btn_cancle).setVisibility(View.GONE);
                        cdialog.setTitle("提示");
                        cdialog.setMessage("已去过，旅历＋1");
                        cdialog.setPositiveButton("确定", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                cdialog.dismiss();
                                //mMyStrategyLv.doPullRefreshing(true, 0);(下拉刷新)
                            }
                        });
                        final Handler handler = new Handler() {
                            public void handleMessage(Message msg) {
                                switch (msg.what) {
                                    case 1:
                                        cdialog.show();
                                }
                                super.handleMessage(msg);
                            }
                        };
                        Message message = handler.obtainMessage(1);
                        handler.sendMessageDelayed(message, 300);
                    } else {
                        cancleVisited(strategyBean);
                        notifyDataSetChanged();
                         //mMyStrategyLv.doPullRefreshing(true, 300);(下拉刷新)
                    }
                }
            });
        }

        @Override
        public View generateView(int position, ViewGroup parent) {
            View v = LayoutInflater.from(context).inflate(R.layout.travel_plane_item, null);
            swipe_ll = (LinearLayout)v.findViewById(R.id.swipe_bg_ll);
            return v;
        }

        @Override
        public int getSwipeLayoutResourceId(int position) {
            return R.id.myswipe;
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        public ArrayList<StrategyBean> getDataList() {
            return data;
        }
       /* @Override
        public View getView(int position, View view, ViewGroup viewGroup) {
            final StrategyBean strategyBean = data.get(position);
            ViewHolder viewHolder;
            if (view == null) {
                view = inflater.inflate(R.layout.travel_plane_item, viewGroup, false);
                viewHolder = new ViewHolder();
                viewHolder.plane_pic = (ImageView) view.findViewById(R.id.plane_pic);
                viewHolder.plane_spans = (TextView) view.findViewById(R.id.plane_spans);
                viewHolder.plane_title = (TextView) view.findViewById(R.id.plane_title);
                viewHolder.city_hasGone = (TextView) view.findViewById(R.id.city_hasGone);
                viewHolder.create_time = (TextView) view.findViewById(R.id.create_time);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }
            if (strategyBean.images.size() > 0) {
                ImageLoader.getInstance().displayImage(strategyBean.images.get(0).url, viewHolder.plane_pic, picOptions);
            } else ImageLoader.getInstance().displayImage("", viewHolder.plane_pic, picOptions);
            viewHolder.plane_spans.setText(strategyBean.dayCnt + "天");
            viewHolder.plane_title.setText(strategyBean.title);
            viewHolder.city_hasGone.setText(strategyBean.summary);
            viewHolder.create_time.setText("创建时间: " + CommonUtils.getTimestampString(new Date(strategyBean.updateTime)));
            return view;
        }*/


        class ViewHolder {
            public ImageView plane_pic;
            public TextView plane_spans;
            public TextView plane_title;
            public TextView city_hasGone;
            public TextView create_time;
        }
    }



    private void haveBeenVisited(final StrategyBean beenBean) {
        try {
            DialogManager.getInstance().showLoadingDialog(getActivity());
        } catch (Exception e) {
            e.printStackTrace();
        }
        String visited = "traveled";
        TravelApi.modifyGuideVisited(beenBean.id, visited, new HttpCallBack<String>() {
            @Override
            public void doSuccess(String result, String method) {
                DialogManager.getInstance().dissMissLoadingDialog();
                CommonJson<ModifyResult> visitedResult = CommonJson.fromJson(result, ModifyResult.class);
                if (visitedResult.code == 0) {
                    deleteThisItem(beenBean);
                } else {
                    if (!getActivity().isFinishing())
                        ToastUtil.getInstance(getActivity()).showToast(getResources().getString(R.string.request_server_failed));
                }
            }

            @Override
            public void doFailure(Exception error, String msg, String method) {
                DialogManager.getInstance().dissMissLoadingDialog();
                if (!getActivity().isFinishing())
                    ToastUtil.getInstance(getActivity()).showToast(getResources().getString(R.string.request_network_failed));
            }

            @Override
            public void doFailure(Exception error, String msg, String method, int code) {

            }
        });
    }

    private void deleteItem(final StrategyBean itemData) {
        MobclickAgent.onEvent(getActivity(),"cell_item_plans_delete");
        final PeachMessageDialog dialog = new PeachMessageDialog(getActivity());
        dialog.setTitle("提示");
        dialog.setTitleIcon(R.drawable.ic_dialog_tip);
        dialog.setMessage(String.format("删除\"%s\"", itemData.title));
        dialog.setPositiveButton("确认", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                try {
                    DialogManager.getInstance().showLoadingDialog(getActivity());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                TravelApi.deleteStrategy(itemData.id, new HttpCallBack<String>() {
                    @Override
                    public void doSuccess(String result, String method) {
                        DialogManager.getInstance().dissMissLoadingDialog();
                        CommonJson<ModifyResult> deleteResult = CommonJson.fromJson(result, ModifyResult.class);
                        if (deleteResult.code == 0) {
                            deleteThisItem(itemData);
                            int cnt = AccountManager.getInstance().getLoginAccountInfo().getGuideCnt();
                            AccountManager.getInstance().getLoginAccountInfo().setGuideCnt(cnt - 1);
                        } else {
                            if (!getActivity().isFinishing())
                                ToastUtil.getInstance(getActivity()).showToast(getResources().getString(R.string.request_server_failed));
                        }
                    }

                    @Override
                    public void doFailure(Exception error, String msg, String method) {
                        DialogManager.getInstance().dissMissLoadingDialog();
                        if (!getActivity().isFinishing())
                            ToastUtil.getInstance(getActivity()).showToast(getResources().getString(R.string.request_network_failed));
                    }

                    @Override
                    public void doFailure(Exception error, String msg, String method, int code) {

                    }
                });
            }
        });
        dialog.setNegativeButton("取消", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();

    }

    private void deleteThisItem(StrategyBean data) {
        int index = myPlaneAdapter.getDataList().indexOf(data);
        myPlaneAdapter.getDataList().remove(index);
        myPlaneAdapter.notifyDataSetChanged();
        if (myPlaneAdapter.getCount() == 0) {
           // myPlaneAdapter.doPullRefreshing(true, 0);(刷新)
        } else if (index <= OtherApi.PAGE_SIZE) {
            cachePage();
        }
    }

    private void cancleVisited(final StrategyBean beenBean) {
        try {
            DialogManager.getInstance().showLoadingDialog(getActivity());
        } catch (Exception e) {
            e.printStackTrace();
        }
        String planned = "planned";
        TravelApi.modifyGuideVisited(beenBean.id, planned, new HttpCallBack<String>() {
            @Override
            public void doSuccess(String result, String method) {
                DialogManager.getInstance().dissMissLoadingDialog();
                CommonJson<ModifyResult> visitedResult = CommonJson.fromJson(result, ModifyResult.class);
                if (visitedResult.code == 0) {
                    deleteThisItem(beenBean);
                } else {
                    if (!getActivity().isFinishing())
                        ToastUtil.getInstance(getActivity()).showToast(getResources().getString(R.string.request_server_failed));
                }
            }

            @Override
            public void doFailure(Exception error, String msg, String method) {
                DialogManager.getInstance().dissMissLoadingDialog();
                if (!getActivity().isFinishing())
                    ToastUtil.getInstance(getActivity()).showToast(getResources().getString(R.string.request_network_failed));
            }

            @Override
            public void doFailure(Exception error, String msg, String method, int code) {

            }
        });
    }

}
