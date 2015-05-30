/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
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
import com.aizou.core.log.LogUtil;
import com.aizou.core.utils.LocalDisplay;
import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMGroup;
import com.easemob.chat.EMGroupManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.ChatBaseActivity;
import com.xuejian.client.lxp.common.account.AccountManager;
import com.xuejian.client.lxp.common.dialog.DialogManager;
import com.xuejian.client.lxp.common.utils.AnimationSimple;
import com.xuejian.client.lxp.common.utils.IMUtils;
import com.xuejian.client.lxp.common.utils.StretchAnimation;
import com.xuejian.client.lxp.common.widget.TitleHeaderBar;
import com.xuejian.client.lxp.config.Constant;
import com.xuejian.client.lxp.db.IMUser;
import com.xuejian.client.lxp.module.toolbox.im.adapter.ContactAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PickContactsWithCheckboxActivity extends ChatBaseActivity {
    private LinearLayout contentLl;
    private ListView listView;
    private RecyclerView toBeAddContactsRv;
    private List<IMUser> toBeAddContacts;
    /**
     * 是否为一个新建的群组
     */
    protected boolean isCreatingNewGroup;
    /**
     * 是否为单选
     */
    private boolean isSignleChecked;
    private PickContactAdapter contactAdapter;
    private ToBeAddContactsAdapter toBeAddAdapter;
    /**
     * group中一开始就有的成员
     */
    private List<String> exitingMembers;
    // 好友列表
    private List<IMUser> alluserList;
    private int request;
    private String groupId;
    private EMGroup group;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_pick_contacts);

        initTitleBar();

        // String groupName = getIntent().getStringExtra("groupName");
        request = getIntent().getIntExtra("request", 0);
        groupId = getIntent().getStringExtra("groupId");
        if (groupId == null) {// 创建群组
            isCreatingNewGroup = true;
        } else {
            // 获取此群组的成员列表
            group = EMGroupManager.getInstance().getGroup(groupId);
            exitingMembers = group.getMembers();
        }
        if (exitingMembers == null)
            exitingMembers = new ArrayList<String>();
        // 获取好友列表
        alluserList = new ArrayList<IMUser>();
        for (IMUser user : AccountManager.getInstance().getContactList(this).values()) {
            if (!user.getUsername().equals(Constant.NEW_FRIENDS_USERNAME) & !user.getUsername().equals(Constant.GROUP_USERNAME))
                alluserList.add(user);
        }
        // 对list进行排序
        Collections.sort(alluserList, new Comparator<IMUser>() {
            @Override
            public int compare(IMUser lhs, IMUser rhs) {
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
        toBeAddContacts = new ArrayList<IMUser>();
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
                if(showing) {
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
//        titleHeaderBar.setRightViewImageRes(R.drawable.add);
        titleHeaderBar.getLeftTextView().setText(" 取消");
        titleHeaderBar.getLeftTextView().setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

        titleHeaderBar.getRightTextView().setText("确定");
        titleHeaderBar.getRightTextView().setTextColor(getResources().getColor(R.color.app_theme_color));
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
                overridePendingTransition(0,R.anim.push_bottom_out);
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
        final StringBuffer groupName = new StringBuffer();
        final StringBuffer membersStr = new StringBuffer();
        for (int i = 0; i < toBeAddContacts.size(); i++) {
            if (i < 3) {
                groupName.append(toBeAddContacts.get(i).getNick());
                if (i != toBeAddContacts.size() - 1 && i != 2) {
                    groupName.append("、");
                }
            }
            membersStr.append(toBeAddContacts.get(i).getNick());
            if (i != toBeAddContacts.size() - 1) {
                membersStr.append("、");
            }
        }
        if (toBeAddContacts.size() > 3) {
            groupName.append("...");
        }
        if (request == IMMainActivity.NEW_CHAT_REQUEST_CODE) {
            //新建群组
            if (toBeAddContacts.size() > 1) {
                DialogManager.getInstance().showLoadingDialog(mContext, "正在新建群组");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // 调用sdk创建群组方法
                        String desc = "";
                        ArrayList<String> members = new ArrayList<String>();
                        for (IMUser imUser : toBeAddContacts) {
                            members.add(imUser.getUsername());
                        }
                        try {
                            //创建不公开群
                            final EMGroup group = EMGroupManager.getInstance().createPrivateGroup(groupName.toString(), desc, members.toArray(new String[0]), true, 50);
                            EMMessage msg = EMMessage.createReceiveMessage(EMMessage.Type.TXT);
                            msg.setChatType(EMMessage.ChatType.GroupChat);
                            msg.setFrom(AccountManager.getInstance().getLoginAccount(mContext).easemobUser);
                            msg.setReceipt(group.getGroupId());
                            IMUtils.setMessageWithTaoziUserInfo(mContext, msg);
                            String myNickmae = AccountManager.getInstance().getLoginAccount(mContext).nickName;
                            String content = String.format(mContext.getResources().getString(R.string.invate_to_group), myNickmae, membersStr.toString());
                            IMUtils.setMessageWithExtTips(mContext, msg, content);
                            msg.addBody(new TextMessageBody(content));
                            EMChatManager.getInstance().sendGroupMessage(msg, new EMCallBack() {
                                @Override
                                public void onSuccess() {

                                }

                                @Override
                                public void onError(int i, String s) {

                                }

                                @Override
                                public void onProgress(int i, String s) {

                                }
                            });
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    DialogManager.getInstance().dissMissLoadingDialog();
                                    // 被邀请
                                    Intent intent = new Intent();
                                    // it is group chat
                                    intent.putExtra("chatType", ChatActivity.CHATTYPE_GROUP);
                                    intent.putExtra("toId", group.getGroupId());
                                    setResult(RESULT_OK, intent);
                                    finishWithNoAnim();
                                }
                            });
                        } catch (final Exception e) {
                            if (!isFinishing())
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    DialogManager.getInstance().dissMissLoadingDialog();
                                    ToastUtil.getInstance(PickContactsWithCheckboxActivity.this).showToast("吖~好像请求失败了");
                                }
                            });
                        }

                    }
                }).start();
            } else if (toBeAddContacts.size() == 1) {
                Intent intent = new Intent();
                // it is group chat
                intent.putExtra("chatType", ChatActivity.CHATTYPE_SINGLE);
                intent.putExtra("toId", toBeAddContacts.get(0).getUsername());
                setResult(RESULT_OK, intent);
                finishWithNoAnim();
            }
        } else if (groupId != null) {
            DialogManager.getInstance().showLoadingDialog(PickContactsWithCheckboxActivity.this);
            new Thread(new Runnable() {

                public void run() {
                    ArrayList<String> members = new ArrayList<String>();
                    for (IMUser imUser : toBeAddContacts) {
                        members.add(imUser.getUsername());
                    }
                    try {
                        //创建者调用add方法
                        if (EMChatManager.getInstance().getCurrentUser().equals(group.getOwner())) {
                            EMGroupManager.getInstance().addUsersToGroup(groupId, members.toArray(new String[0]));
                        } else {
                            //一般成员调用invite方法
                            EMGroupManager.getInstance().inviteUser(groupId, members.toArray(new String[0]), null);
                        }
//                        group = EMGroupManager.getInstance().getGroupFromServer(groupId);
//                        EMGroupManager.getInstance().createOrUpdateLocalGroup(group);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                DialogManager.getInstance().dissMissLoadingDialog();
                                // 被邀请
                                EMMessage msg = EMMessage.createReceiveMessage(EMMessage.Type.TXT);
                                msg.setChatType(EMMessage.ChatType.GroupChat);
                                msg.setFrom(AccountManager.getInstance().getLoginAccount(mContext).easemobUser);
                                msg.setReceipt(group.getGroupId());
                                IMUtils.setMessageWithTaoziUserInfo(mContext, msg);
                                String myNickname = AccountManager.getInstance().getLoginAccount(mContext).nickName;
                                String content = String.format(mContext.getResources().getString(R.string.invate_to_group), myNickname, membersStr.toString());
                                IMUtils.setMessageWithExtTips(mContext, msg, content);
                                msg.addBody(new TextMessageBody(content));
                                EMChatManager.getInstance().sendGroupMessage(msg, new EMCallBack() {
                                    @Override
                                    public void onSuccess() {

                                    }

                                    @Override
                                    public void onError(int i, String s) {

                                    }

                                    @Override
                                    public void onProgress(int i, String s) {

                                    }
                                });
                                setResult(RESULT_OK);
                                finishWithNoAnim();
                            }
                        });

                    } catch (final Exception e) {
                        if (!isFinishing())
                        runOnUiThread(new Runnable() {
                            public void run() {
                                DialogManager.getInstance().dissMissLoadingDialog();
//                                Toast.makeText(getApplicationContext(), "添加群成员失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                ToastUtil.getInstance(getApplicationContext()).showToast("呃~好像找不到网络");
                            }
                        });
                    }
                }
            }).start();
        }

    }

    /**
     * 获取要被添加的成员
     *
     * @return
     */
    private List<IMUser> getToBeAddMembers() {
        return toBeAddContacts;
    }

    public class ToBeAddContactsAdapter extends
            RecyclerView.Adapter<ToBeAddContactsAdapter.ViewHolder> {

        private LayoutInflater mInflater;
        private List<IMUser> mDatas;
        DisplayImageOptions picOptions;

        public ToBeAddContactsAdapter(Context context, List<IMUser> datas) {
            mInflater = LayoutInflater.from(context);
            mDatas = datas;

            picOptions = new DisplayImageOptions.Builder()
                    .cacheInMemory(true)
                    .cacheOnDisk(true).bitmapConfig(Bitmap.Config.ARGB_8888)
                    .resetViewBeforeLoading(true)
                    .showImageOnFail(R.drawable.avatar_placeholder_round)
                    .showImageOnLoading(R.drawable.avatar_placeholder_round)
                    .showImageForEmptyUri(R.drawable.avatar_placeholder_round)
//				.decodingOptions(D)
//                .displayer(new FadeInBitmapDisplayer(150, true, true, false))
                    .displayer(new RoundedBitmapDisplayer(LocalDisplay.dp2px(4)))
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

        public void checkToBeAddContacts(){
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
                showing=true;
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

        public void add(IMUser item) {
            if(!mDatas.contains(item)){
                mDatas.add(item);
                notifyDataSetChanged();
                notifyItemInserted(mDatas.size()-1);
                checkToBeAddContacts();
//            notifyItemRangeChanged(0, mDatas.size());
                LogUtil.d("onItemClick"+" add--"+(mDatas.size()-1)+"--"+item.getNick());
            }

        }

        public void remove(IMUser item) {
            if(mDatas.contains(item)){
                int position = mDatas.indexOf(item);
                mDatas.remove(item);
                notifyDataSetChanged();
                notifyItemRemoved(position);
                checkToBeAddContacts();
//            notifyItemRangeChanged(0, mDatas.size());
                LogUtil.d("onItemClick"+" remove--"+position+"--"+item.getNick());
            }

        }
        public void remove(int pos){
            mDatas.remove(pos);
            notifyItemRemoved(pos);
            LogUtil.d("onItemClick"+" remove--"+pos);
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
                    if(showing){
                        return;
                    }
                    int p = viewHolder.getPosition();
                    if(p<mDatas.size()){
                        IMUser user = mDatas.get(p);
                        int index = alluserList.indexOf(user);
                        contactAdapter.isCheckedArray[index]=false;
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

            final IMUser user = mDatas.get(i);
            ImageLoader.getInstance().displayImage(mDatas.get(i).getAvatarSmall(), viewHolder.mImg, picOptions);
            viewHolder.mTxt.setText(mDatas.get(i).getNick());


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

        public PickContactAdapter(Context context, int resource, List<IMUser> users) {
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
                    .showImageOnFail(R.drawable.avatar_placeholder_round)
                    .showImageOnLoading(R.drawable.avatar_placeholder_round)
                    .showImageForEmptyUri(R.drawable.avatar_placeholder_round)
//				.decodingOptions(D)
//                .displayer(new FadeInBitmapDisplayer(150, true, true, false))
                    .displayer(new RoundedBitmapDisplayer(LocalDisplay.dp2px(6)))
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
            final IMUser user = getItem(position);
            final String username = user.getUsername();
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

            vh.nickView.setText(user.getNick());
            vh.avatarView.setTag(user.getAvatarSmall());
//            Picasso.with(mContext)
//                    .load(user.getAvatar())
//                    .placeholder(R.drawable.avatar_placeholder)
//                    .into(vh.avatarView);
//            ImageLoader.getInstance().loadImage(user.getAvatar(),picOptions,new SimpleImageLoadingListener(){
//                @Override
//                public void onLoadingComplete(String imageUrl, View view,
//                                              Bitmap loadedImage) {
////                    super.onLoadingComplete(imageUrl, view, loadedImage);
//                    if (imageUrl.equals( vh.avatarView.getTag())) {
//                        vh.avatarView.setImageBitmap(loadedImage);
//                    }
//                }
//            });
            ImageLoader.getInstance().displayImage(user.getAvatarSmall(), vh.avatarView, picOptions);

            if (vh.checkBox != null) {
                // checkBox.setOnCheckedChangeListener(null);
                if (exitingMembers != null && exitingMembers.contains(username)) {
                    vh.checkBox.setButtonDrawable(R.drawable.checkbox_bg_gray_selector);
                } else {
                    vh.checkBox.setButtonDrawable(R.drawable.checkbox_bg_selector);
                }
                vh.checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        // 群组中原来的成员一直设为选中状态
                        if (exitingMembers.contains(username)) {
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
                if (exitingMembers.contains(username)) {
                    vh.checkBox.setChecked(true);
                    isCheckedArray[position] = true;
                } else {
                    vh.checkBox.setChecked(isCheckedArray[position]);
                }
            }
            return convertView;
        }
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
