/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.xuejian.client.lxp.module.toolbox.im;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aizou.core.dialog.ToastUtil;
import com.aizou.core.http.HttpCallBack;
import com.aizou.core.log.LogUtil;
import com.aizou.core.utils.LocalDisplay;
import com.lv.Utils.Config;
import com.lv.im.IMClient;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.ChatBaseActivity;
import com.xuejian.client.lxp.common.api.GroupApi;
import com.xuejian.client.lxp.common.dialog.DialogManager;
import com.xuejian.client.lxp.common.utils.AnimationSimple;
import com.xuejian.client.lxp.common.utils.StretchAnimation;
import com.xuejian.client.lxp.common.widget.TitleHeaderBar;
import com.xuejian.client.lxp.db.User;
import com.xuejian.client.lxp.db.UserDBManager;
import com.xuejian.client.lxp.module.toolbox.im.adapter.ContactAdapter;
import com.xuejian.client.lxp.module.toolbox.im.group.GroupManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PickContactsWithCheckboxActivity extends ChatBaseActivity {
    private LinearLayout contentLl;
    private ListView listView;
    private RecyclerView toBeAddContactsRv;
    private List<User> toBeAddContacts;
//    private List<Long> userList;
//    private User _group;
    /**
     * 是否为一个新建的群组
     */
    // protected boolean isCreatingNewGroup;
    /**
     * 是否为单选
     */
    private boolean isSignleChecked;
    private PickContactAdapter contactAdapter;
    private ToBeAddContactsAdapter toBeAddAdapter;
    /**
     * group中一开始就有的成员
     */
    private List<User> exitingMembers;
    // 好友列表
    private List<User> alluserList = new ArrayList<User>();
    private int request;
    private String groupId;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_pick_contacts);

        initTitleBar();

        // String groupName = getIntent().getStringExtra("groupName");
        request = getIntent().getIntExtra("request", 0);
        groupId = getIntent().getStringExtra("groupId");
        if (groupId != null) {// 创建群组
            //     isCreatingNewGroup = true;
            //  } else {
            // 获取此群组的成员列表
            //_group=UserDBManager.getInstance().getContactByUserId(Long.parseLong(groupId));
            exitingMembers = UserDBManager.getInstance().getGroupMember(Long.parseLong(groupId));
        }
        if (exitingMembers == null)
            exitingMembers = new ArrayList<User>();
        // 获取好友列表
        alluserList = UserDBManager.getInstance().getContactListWithoutGroup();

        /**
         * 对list进行排序
         */
        Collections.sort(alluserList, new Comparator<User>() {
            @Override
            public int compare(User lhs, User rhs) {
                return (lhs.getHeader().compareTo(rhs.getHeader()));
            }
        });
        handler = new Handler();
        // 以下通过代码创建控件组动画而不使用xml文件
        contentShow = new LayoutAnimationController(AnimationUtils.loadAnimation(this, android.R.anim.fade_in));
        contentShow.setOrder(LayoutAnimationController.ORDER_NORMAL);
        contentShow.setDelay(0.4f);
        contentHide = new LayoutAnimationController(AnimationUtils.loadAnimation(this, android.R.anim.fade_out));
        contentHide.setOrder(LayoutAnimationController.ORDER_REVERSE);
        contentHide.setDelay(0.4f);
        contentLl = (LinearLayout) findViewById(R.id.ll_content);
        listView = (ListView) findViewById(R.id.list);
        toBeAddContactsRv = (RecyclerView) findViewById(R.id.rv_add_contacts);
        toBeAddContacts = new ArrayList<User>();
        //设置布局管理器
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        toBeAddContactsRv.setLayoutManager(linearLayoutManager);

        //设置适配器
        toBeAddAdapter = new ToBeAddContactsAdapter(this, toBeAddContacts);
        toBeAddContactsRv.setAdapter(toBeAddAdapter);
        toBeAddContactsRv.setItemAnimator(new DefaultItemAnimator());

        contactAdapter = new PickContactAdapter(this, R.layout.row_contact_with_checkbox, alluserList);
        listView.setAdapter(contactAdapter);
        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (showing) {
                    return;
                }
                CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkbox);
                checkBox.toggle();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
//        MobclickAgent.onPageStart("page_choose_talk_to");
    }

    @Override
    protected void onPause() {
        super.onPause();
//        MobclickAgent.onPageEnd("page_choose_talk_to");
    }

    private void initTitleBar() {
        final TitleHeaderBar titleHeaderBar = (TitleHeaderBar) findViewById(R.id.ly_header_bar_title_wrap);
        titleHeaderBar.getLeftTextView().setText("取消");
        titleHeaderBar.getLeftTextView().setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

        titleHeaderBar.getRightTextView().setText("确定");
        titleHeaderBar.getRightTextView().setTextColor(getResources().getColor(R.color.base_color_white));
        titleHeaderBar.setRightOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save(v);
            }
        });

        titleHeaderBar.getTitleTextView().setText("选择好友");
        titleHeaderBar.findViewById(R.id.ly_title_bar_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.slide_stay, R.anim.push_bottom_out);
            }
        });
    }

    /**
     * 确认选择的members
     *
     * @param v
     */
    public void save(View v) {
        if (toBeAddContacts.size() == 0) {
            ToastUtil.getInstance(mContext).showToast("请至少选择一位好友");
            return;
        }
        if (request == IMMainActivity.NEW_CHAT_REQUEST_CODE) {
            if (toBeAddContacts.size() == 1) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent();
                        intent.putExtra("chatType", "single");
                        intent.putExtra("toId", toBeAddContacts.get(0).getUserId());
                        setResult(RESULT_OK, intent);
                        finishWithNoAnim();
                    }
                });
            }
            final StringBuffer ChatName = new StringBuffer();
            final StringBuffer membersStr = new StringBuffer();
            for (int i = 0; i < toBeAddContacts.size(); i++) {
                if (i < 3) {
                    ChatName.append(toBeAddContacts.get(i).getNickName());
                    if (i != toBeAddContacts.size() - 1 && i != 2) {
                        ChatName.append("、");
                    }
                }
                membersStr.append(toBeAddContacts.get(i).getNickName());
                if (i != toBeAddContacts.size() - 1) {
                    membersStr.append("、");
                }
            }
            if (toBeAddContacts.size() > 3) {
                ChatName.append("...");
            }
            if (toBeAddContacts.size() > 1) {
                final JSONArray ids = new JSONArray();
                for (User user : toBeAddContacts) {
                    ids.put(user.getUserId());
                }
                DialogManager.getInstance().showLoadingDialog(PickContactsWithCheckboxActivity.this);
                GroupManager.getGroupManager().createGroup(ChatName.toString(), null, null, ids, new HttpCallBack() {
                    @Override
                    public void doSuccess(Object result, String method) {
                        try {
                            JSONObject object = new JSONObject(result.toString());
                            JSONObject jsonObject = object.getJSONObject("result");
                            String groupId = jsonObject.getString("groupId");
                            String name = jsonObject.getString("name");
                            String avatar = jsonObject.getString("avatar");
                            jsonObject.remove("groupId");
                            jsonObject.remove("name");
                            jsonObject.remove("avatar");
                            jsonObject.put("GroupMember", ids);
                            // long creator = jsonObject.getLong("creator");
                            IMClient.getInstance().addGroup2Conversation(groupId, null);
                            UserDBManager.getInstance().saveContact(new User(Long.parseLong(groupId), name, jsonObject.toString(), 8, avatar));
                            if (Config.isDebug) {
                                Log.i(Config.TAG, "群组更新成功");
                            }
                            DialogManager.getInstance().dissMissLoadingDialog();
                            Intent intent = new Intent();
                            intent.putExtra("chatType", "group");
                            intent.putExtra("toId", Long.parseLong(groupId));
                            setResult(RESULT_OK, intent);
                            finishWithNoAnim();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void doFailure(Exception error, String msg, String method) {
                        DialogManager.getInstance().dissMissLoadingDialog();
                        ToastUtil.getInstance(PickContactsWithCheckboxActivity.this).showToast("吖~好像请求失败了");

                    }

                    @Override
                    public void doFailure(Exception error, String msg, String method, int code) {

                    }
                });
            }
        }
        //增加成员
        else if (request == 0) {
            try {
                final JSONArray ids = new JSONArray();
                for (User user : toBeAddContacts) {
                    ids.put(user.getUserId());
                }
                if (ids.length() == 1) {
                    GroupApi.addGroupMember(groupId, ids.getLong(0), new HttpCallBack() {
                        @Override
                        public void doSuccess(Object result, String method) {
                            Intent intent = new Intent();
                            intent.putExtra("chatType", "group");
                            intent.putExtra("toId", groupId + "");
                            //   intent.putExtra("Id", toBeAddContacts.get(0).getUserId());
                            setResult(RESULT_OK, intent);
                            finishWithNoAnim();
                        }

                        @Override
                        public void doFailure(Exception error, String msg, String method) {

                        }

                        @Override
                        public void doFailure(Exception error, String msg, String method, int code) {

                        }
                    });
                } else {
                    GroupApi.editGroupMembers(groupId, ids, 1, new HttpCallBack() {
                        @Override
                        public void doSuccess(Object result, String method) {
                            Intent intent = new Intent();
                            intent.putExtra("chatType", "group");
                            intent.putExtra("toId", groupId + "");
                            //intent.putExtra("Ids", ids.);
                            setResult(RESULT_OK, intent);
                            finishWithNoAnim();
                        }

                        @Override
                        public void doFailure(Exception error, String msg, String method) {
                            error.printStackTrace();
                            System.out.println("error " + msg + " method " + method);
                        }

                        @Override
                        public void doFailure(Exception error, String msg, String method, int code) {

                        }
                    });
//                    GroupManager.getGroupManager().addMembers(groupId, ids, true, new CallBack() {
//                    @Override
//                    public void onSuccess() {
//                        Intent intent = new Intent();
//                        intent.putExtra("chatType", "group");
//                        intent.putExtra("toId", groupId + "");
//                        //intent.putExtra("Ids", ids.);
//                        setResult(RESULT_OK, intent);
//                        finishWithNoAnim();
//                    }
//
//                    @Override
//                    public void onFailed() {
//
//                    }
//                });
                }
//                GroupManager.getGroupManager().addMembers(groupId, list, true, new CallBack() {
//                    @Override
//                    public void onSuccess() {
//                        Intent intent = new Intent();
//                        intent.putExtra("chatType", "group");
//                        intent.putExtra("toId", groupId + "");
//                        intent.putExtra("Id", toBeAddContacts.get(0).getUserId());
//                        setResult(RESULT_OK, intent);
//                        finishWithNoAnim();
//                    }
//
//                    @Override
//                    public void onFailed() {
//
//                    }
//                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (toBeAddContacts.size() == 1) {
            Intent intent = new Intent();
            intent.putExtra("chatType", "single");
            intent.putExtra("toId", toBeAddContacts.get(0).getUserId());
            setResult(RESULT_OK, intent);
            finishWithNoAnim();
        } else if (toBeAddContacts.size() > 1) {
            final StringBuffer ChatName = new StringBuffer();
            final StringBuffer membersStr = new StringBuffer();
            for (int i = 0; i < toBeAddContacts.size(); i++) {
                if (i < 3) {
                    ChatName.append(toBeAddContacts.get(i).getNickName());
                    if (i != toBeAddContacts.size() - 1 && i != 2) {
                        ChatName.append("、");
                    }
                }
                membersStr.append(toBeAddContacts.get(i).getNickName());
                if (i != toBeAddContacts.size() - 1) {
                    membersStr.append("、");
                }
            }
            if (toBeAddContacts.size() > 3) {
                ChatName.append("...");
            }
            final JSONArray ids = new JSONArray();
            for (User user : toBeAddContacts) {
                ids.put(user.getUserId());
            }
            GroupManager.getGroupManager().createGroup(ChatName.toString(), null, null, ids, new HttpCallBack() {
                @Override
                public void doSuccess(Object result, String method) {
                    try {
                        JSONObject object = new JSONObject(result.toString());
                        JSONObject jsonObject = object.getJSONObject("result");
                        String groupId = jsonObject.getString("groupId");
                        String name = jsonObject.getString("name");
                        String avatar = jsonObject.getString("avatar");
                        jsonObject.remove("groupId");
                        jsonObject.remove("name");
                        jsonObject.remove("avatar");
                        jsonObject.put("GroupMember", ids);
                        // long creator = jsonObject.getLong("creator");
                        IMClient.getInstance().addGroup2Conversation(groupId, null);
                        UserDBManager.getInstance().saveContact(new User(Long.parseLong(groupId), name, jsonObject.toString(), 8, avatar));
                        if (Config.isDebug) {
                            Log.i(Config.TAG, "群组更新成功");
                        }
                        Intent intent = new Intent();
                        intent.putExtra("chatType", "group");
                        intent.putExtra("toId", Long.parseLong(groupId));
                        setResult(RESULT_OK, intent);
                        finishWithNoAnim();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void doFailure(Exception error, String msg, String method) {
                    DialogManager.getInstance().dissMissLoadingDialog();
                    ToastUtil.getInstance(PickContactsWithCheckboxActivity.this).showToast("吖~好像请求失败了");

                }

                @Override
                public void doFailure(Exception error, String msg, String method, int code) {

                }
            });

        }

    }

    /**
     * 获取要被添加的成员
     *
     * @return
     */
    private List<User> getToBeAddMembers() {
        return toBeAddContacts;
    }

    public class ToBeAddContactsAdapter extends
            RecyclerView.Adapter<ToBeAddContactsAdapter.ViewHolder> {

        private LayoutInflater mInflater;
        private List<User> mDatas;
        DisplayImageOptions picOptions;

        public ToBeAddContactsAdapter(Context context, List<User> datas) {
            mInflater = LayoutInflater.from(context);
            mDatas = datas;

            picOptions = new DisplayImageOptions.Builder()
                    .cacheInMemory(true)
                    .cacheOnDisk(true).bitmapConfig(Bitmap.Config.ARGB_8888)
                    .resetViewBeforeLoading(true)
                    .showImageOnFail(R.drawable.messages_bg_useravatar)
                    .showImageOnLoading(R.drawable.messages_bg_useravatar)
                    .showImageForEmptyUri(R.drawable.ic_home_avatar_unknown)
//				.decodingOptions(D)
//                .displayer(new FadeInBitmapDisplayer(150, true, true, false))
                    .displayer(new RoundedBitmapDisplayer(getResources().getDimensionPixelSize(R.dimen.page_more_header_frame_height) - LocalDisplay.dp2px(20)))
                    .imageScaleType(ImageScaleType.IN_SAMPLE_INT).build();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public ViewHolder(View arg0) {
                super(arg0);
//                arg0.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        LogUtil.d("onItemClick"+"onItemClick--"+getPosition());
//
//                    }
//                });

            }

            View contentView;
            ImageView mImg;
            TextView mTxt;


        }

        public void checkToBeAddContacts() {
            if (toBeAddContacts.size() > 0) {
                if (showing) {
                    return;
                }
                if (toBeAddContactsRv.getVisibility() == View.INVISIBLE) {

                    showing = true;
                    toBeAddContactsRv.setVisibility(View.VISIBLE);
                    AnimationSimple.move(contentLl, 300, 0, toBeAddContactsRv.getHeight(), new AccelerateDecelerateInterpolator());
                    toBeAddContactsRv.setLayoutAnimation(contentShow);
                    toBeAddContactsRv.startLayoutAnimation();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) contentLl.getLayoutParams();
                            lp.addRule(RelativeLayout.BELOW, toBeAddContactsRv.getId());
                            contentLl.setLayoutParams(lp);
                            contentLl.clearAnimation();
                            showing = false;
                        }
                    }, 300);

//                                    stretchanimation.startAnimation(toBeAddContactsRv);
                }
            } else {
                showing = true;
                AnimationSimple.move(contentLl, 300, 0, -toBeAddContactsRv.getHeight(), new AccelerateDecelerateInterpolator());
                toBeAddContactsRv.setLayoutAnimation(contentHide);
                toBeAddContactsRv.startLayoutAnimation();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) contentLl.getLayoutParams();
                        lp.addRule(RelativeLayout.BELOW, R.id.ly_header_bar_title_wrap);
                        toBeAddContactsRv.setVisibility(View.INVISIBLE);
                        contentLl.setLayoutParams(lp);
                        contentLl.clearAnimation();
                        showing = false;
                    }
                }, 300);
//                                    stretchanimation.startAnimation(toBeAddContactsRv);
            }
        }

        public void add(User item) {
            if (!mDatas.contains(item)) {
                mDatas.add(item);
                notifyDataSetChanged();
                notifyItemInserted(mDatas.size() - 1);
                checkToBeAddContacts();
//            notifyItemRangeChanged(0, mDatas.size());
                LogUtil.d("onItemClick" + " add--" + (mDatas.size() - 1) + "--" + item.getNickName());
            }

        }

        public void remove(User item) {
            if (mDatas.contains(item)) {
                int position = mDatas.indexOf(item);
                mDatas.remove(item);
                notifyDataSetChanged();
                notifyItemRemoved(position);
                checkToBeAddContacts();
//            notifyItemRangeChanged(0, mDatas.size());
                LogUtil.d("onItemClick" + " remove--" + position + "--" + item.getNickName());
            }

        }

        public void remove(int pos) {
            mDatas.remove(pos);
            notifyItemRemoved(pos);
            LogUtil.d("onItemClick" + " remove--" + pos);
        }

        @Override
        public int getItemCount() {
            return mDatas.size();
        }

        /**
         * 创建ViewHolder
         */
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = mInflater.inflate(R.layout.item_select_contact,
                    viewGroup, false);
            final ViewHolder viewHolder = new ViewHolder(view);
            viewHolder.contentView = view;
            viewHolder.mImg = (ImageView) view
                    .findViewById(R.id.iv_avatar);
            viewHolder.mTxt = (TextView) view
                    .findViewById(R.id.tv_nickname);
            viewHolder.contentView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (showing) {
                        return;
                    }
                    int p = viewHolder.getPosition();
                    if (p < mDatas.size()) {
                        User user = mDatas.get(p);
                        int index = alluserList.indexOf(user);
                        contactAdapter.isCheckedArray[index] = false;
                        remove(user);
                        contactAdapter.notifyDataSetChanged();
                    }


                }
            });
            return viewHolder;
        }

        /**
         * 设置值
         */
        @Override
        public void onBindViewHolder(final ViewHolder viewHolder, final int i) {

            final User user = mDatas.get(i);
            ImageLoader.getInstance().displayImage(mDatas.get(i).getAvatarSmall(), viewHolder.mImg, picOptions);
            viewHolder.mTxt.setText(mDatas.get(i).getNickName());


        }

    }

    private LayoutAnimationController contentShow;// 控件组动画显示
    private LayoutAnimationController contentHide;// 控件组动画隐藏
    private boolean showing;

    /**
     * adapter
     */
    private class PickContactAdapter extends ContactAdapter {

        public boolean[] isCheckedArray;
        int res;
        private LayoutInflater layoutInflater;
        private DisplayImageOptions picOptions;
        private StretchAnimation stretchanimation;

        public PickContactAdapter(Context context, int resource, List<User> users) {
            super(context, resource, users);
            isCheckedArray = new boolean[users.size()];
            res = resource;
            layoutInflater = getLayoutInflater();
            stretchanimation = new StretchAnimation(LocalDisplay.dp2px(103), 0, StretchAnimation.TYPE.vertical, 500);
            stretchanimation.setInterpolator(new AccelerateDecelerateInterpolator());
            stretchanimation.setDuration(500);

            picOptions = new DisplayImageOptions.Builder()
                    .cacheInMemory(true)
                    .cacheOnDisk(true).bitmapConfig(Bitmap.Config.ARGB_8888)
                    .resetViewBeforeLoading(true)
                    .showImageOnFail(R.drawable.messages_bg_useravatar)
                    .showImageOnLoading(R.drawable.messages_bg_useravatar)
                    .showImageForEmptyUri(R.drawable.messages_bg_useravatar)
//				.decodingOptions(D)
//                .displayer(new FadeInBitmapDisplayer(150, true, true, false))
                    .displayer(new RoundedBitmapDisplayer(getResources().getDimensionPixelSize(R.dimen.page_more_header_frame_height) - LocalDisplay.dp2px(20)))
                    .imageScaleType(ImageScaleType.IN_SAMPLE_INT).build();
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder vh;
            if (convertView == null) {
                convertView = layoutInflater.inflate(res, null);
                vh = new ViewHolder();
                vh.avatarView = (ImageView) convertView.findViewById(R.id.avatar);
                vh.nickView = (TextView) convertView.findViewById(R.id.name);
                vh.sectionHeader = (TextView) convertView.findViewById(R.id.header);
                vh.checkBox = (CheckBox) convertView.findViewById(R.id.checkbox);
                convertView.setTag(vh);
            } else {
                vh = (ViewHolder) convertView.getTag();
            }

//            final String username = getItem(position).getUsername();
//            final IMUser user = AccountManager.getInstance().getContactList(mContext).get(username);
            final User user = getItem(position);
            final long userId = user.getUserId();
            String header = user.getHeader();

            if (position == 0 || header != null && !header.equals(getItem(position - 1).getHeader())) {
                if ("".equals(header)) {
                    vh.sectionHeader.setVisibility(View.GONE);
                } else {
                    vh.sectionHeader.setVisibility(View.VISIBLE);
                    vh.sectionHeader.setText(header);
                }
            } else {
                vh.sectionHeader.setVisibility(View.GONE);
            }

            vh.nickView.setText(user.getNickName());
            vh.avatarView.setTag(user.getAvatarSmall());
            ImageLoader.getInstance().displayImage(user.getAvatarSmall(), vh.avatarView, picOptions);

            if (vh.checkBox != null) {
                // checkBox.setOnCheckedChangeListener(null);
                if (exitingMembers != null && exitingMembers.contains(user)) {
                    vh.checkBox.setButtonDrawable(R.drawable.checkbox_bg_gray_selector);
                } else {
                    vh.checkBox.setButtonDrawable(R.drawable.checkbox_bg_selector);
                }
                vh.checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        // 群组中原来的成员一直设为选中状态
                        if (contains(userId)) {
                            isChecked = true;
                            vh.checkBox.setChecked(true);
                        } else {
                            if (isChecked) {
                                toBeAddAdapter.add(user);

                            } else {
                                toBeAddAdapter.remove(user);
                            }

                        }
                        isCheckedArray[position] = isChecked;
                        //如果是单选模式
                        if (isSignleChecked && isChecked) {
                            for (int i = 0; i < isCheckedArray.length; i++) {
                                if (i != position) {
                                    isCheckedArray[i] = false;
                                }
                            }
                            contactAdapter.notifyDataSetChanged();
                        }
                    }
                });
                // 群组中原来的成员一直设为选中状态
                if (contains(userId)) {
                    vh.checkBox.setChecked(true);
                    isCheckedArray[position] = true;
                } else {
                    vh.checkBox.setChecked(isCheckedArray[position]);
                }
            }
            return convertView;
        }
    }

    private boolean contains(long id) {
        for (User user : exitingMembers) {
            if (user.getUserId() == id)
                return true;
        }
        return false;
    }

    public void back(View view) {
        finish();
    }

    class ViewHolder {
        public TextView sectionHeader;
        public ImageView avatarView;
        public TextView nickView;
        public CheckBox checkBox;
    }

}
