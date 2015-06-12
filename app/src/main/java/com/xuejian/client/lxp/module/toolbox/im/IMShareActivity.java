package com.xuejian.client.lxp.module.toolbox.im;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.aizou.core.log.LogUtil;
import com.aizou.core.utils.LocalDisplay;
import com.aizou.core.widget.listHelper.ListViewDataAdapter;
import com.aizou.core.widget.listHelper.ViewHolderBase;
import com.aizou.core.widget.listHelper.ViewHolderCreator;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMContact;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMGroup;
import com.easemob.chat.EMGroupManager;
import com.easemob.chat.EMMessage;
import com.easemob.exceptions.EaseMobException;
import com.lv.bean.ConversationBean;
import com.lv.im.IMClient;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.PeachBaseActivity;
import com.xuejian.client.lxp.bean.PeachConversation;
import com.xuejian.client.lxp.common.widget.TitleHeaderBar;
import com.xuejian.client.lxp.common.widget.circluaravatar.JoinBitmaps;
import com.xuejian.client.lxp.db.IMUser;
import com.xuejian.client.lxp.db.respository.IMUserRepository;
import com.xuejian.client.lxp.db.userDB.User;
import com.xuejian.client.lxp.db.userDB.UserDBManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Rjm on 2014/12/2.
 */
public class IMShareActivity extends PeachBaseActivity {
    @InjectView(R.id.title_bar)
    TitleHeaderBar mTitleBar;
    @InjectView(R.id.create_new_talk)
    TextView mCreateNewTalk;
    private ListView mImShareLv;
    private ListViewDataAdapter mImShareAdapter;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
    }

    private void initView() {
        setContentView(R.layout.activity_im_share);
        mImShareLv = (ListView) findViewById(R.id.im_share_lv);
        View headerView = View.inflate(mContext, R.layout.header_im_share, null);
        mImShareLv.addHeaderView(headerView);
        ButterKnife.inject(this);
        mTitleBar.getTitleTextView().setText("选择");
        mTitleBar.enableBackKey(true);
        mCreateNewTalk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext,PickContactsWithCheckboxActivity.class);
                intent.putExtra("request", IMMainActivity.NEW_CHAT_REQUEST_CODE);
                startActivityForResult(intent, IMMainActivity.NEW_CHAT_REQUEST_CODE);

            }
        });

    }

    private void initData() {
        mImShareAdapter = new ListViewDataAdapter(new ViewHolderCreator() {
            @Override
            public ViewHolderBase createViewHolder() {
                return new ShareChatViewHolder();
            }
        });
        mImShareLv.setAdapter(mImShareAdapter);

    }

    @Override
    public void onResume() {
        super.onResume();
        refresh();
        if (mImShareLv.getAdapter().getCount() <= 0) {
            setEmptyView();
        }
    }
    /**
     * 刷新页面
     */
    public void refresh() {
        mImShareAdapter.getDataList().clear();
        mImShareAdapter.getDataList().addAll(loadConversationsWithRecentChat());
        mImShareAdapter.notifyDataSetChanged();
    }

    private void setEmptyView() {
//        listView.setEmptyView();
        View emptyView = findViewById(R.id.empty_view);
        mImShareLv.setEmptyView(emptyView);
    }

    /**
     * 获取所有会话
     *
     * @return
     */
    private List<ConversationBean> loadConversationsWithRecentChat() {
        // 获取所有会话，包括陌生人
        List<ConversationBean> list=IMClient.getInstance().getConversationList();
//        Hashtable<String, EMConversation> conversations = EMChatManager.getInstance().getAllConversations();
//        List<PeachConversation> conversationList = new ArrayList<PeachConversation>();
//        //过滤掉messages seize为0的conversation
//        Iterator<EMConversation> conversationIt = conversations.values().iterator();
//        while (conversationIt.hasNext()){
//            final EMConversation conversation = conversationIt.next();
//            if(conversation.getLastMessage()==null)
//                continue;
//            if(conversation.getIsGroup()){
//                PeachConversation peachConversation = new PeachConversation();
//                peachConversation.emConversation = conversation;
//                conversationList.add(peachConversation);
//                EMGroup group=EMGroupManager.getInstance().getGroup(conversation.getUserName());
//                if(group==null){
//                    new Thread(new Runnable() {
//                        @Override
//                        public void run() {
//                            try {
//                                EMGroup emGroup =EMGroupManager.getInstance().getGroupFromServer(conversation.getUserName());
//                                EMGroupManager.getInstance().createOrUpdateLocalGroup(emGroup);
//                            } catch (EaseMobException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    }).start();
//                }
//
//            }else if(!TextUtils.isEmpty(conversation.getUserName())){
//                IMUser user = IMUserRepository.getMyFriendByUserName(mContext, conversation.getUserName());
//                if(user!=null){
//                    PeachConversation peachConversation = new PeachConversation();
//                    peachConversation.emConversation = conversation;
//                    peachConversation.imUser = user;
//                    conversationList.add(peachConversation);
//                }else{
//                    conversationIt.remove();
//                }
//            }
//        }
        // 排序
        sortConversationByLastChatTime(list);
        return list;
    }

    /**
     * 根据最后一条消息的时间排序
     *
     * @param conversationList
     */
    private void sortConversationByLastChatTime(List<ConversationBean> conversationList) {
        Collections.sort(conversationList, new Comparator<ConversationBean>() {
            @Override
            public int compare(final ConversationBean con1, final ConversationBean con2) {

                if (con1.getLastChatTime() == con2.getLastChatTime()) {
                    return 0;
                } else if (con2.getLastChatTime() > con1.getLastChatTime()) {
                    return 1;
                } else {
                    return -1;
                }
            }

        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){
            switch (requestCode){
                case IMMainActivity.NEW_CHAT_REQUEST_CODE:
                    setResult(RESULT_OK,data);
                    finish();
            }
        }
    }

    public class ShareChatViewHolder extends ViewHolderBase<ConversationBean> {

        @InjectView(R.id.avatar)
        ImageView mAvatar;
        @InjectView(R.id.name)
        TextView mName;
        View contentView;
        DisplayImageOptions options;
        ImageSize avatarSize;

        public ShareChatViewHolder(){
            super();
            options = new DisplayImageOptions.Builder()
                    .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                    .showImageForEmptyUri(R.drawable.avatar_placeholder_round)
                    .showImageOnFail(R.drawable.avatar_placeholder_round)
                    .cacheOnDisc(true)
                            // 设置下载的图片是否缓存在SD卡中
                    .displayer(new RoundedBitmapDisplayer(LocalDisplay.dp2px(6))) // 设置成圆角图片
                    .build();
            avatarSize =new ImageSize(LocalDisplay.dp2px(45),LocalDisplay.dp2px(45));
            handler = new Handler();
        }


        @Override
        public View createView(LayoutInflater layoutInflater) {
            contentView = layoutInflater.inflate(R.layout.row_im_share, mImShareLv, false);
            ButterKnife.inject(this, contentView);
            return contentView;
        }

        @Override
        public void showData(int position,final ConversationBean itemData) {
            // 获取与此用户/群组的会话
          final   User imUser = UserDBManager.getInstance().getContactByUserId(itemData.getFriendId());
            // 获取用户username或者群组groupid
            //final String username = imUser.getNickName();
            if ("group".equals(itemData.getChatType())) {
                // 群聊消息，显示群聊头像
                final List<String> members = new ArrayList<>();
                final List<Bitmap> membersAvatars = new ArrayList<>();
       //         final int size = Math.min(group.getMembers().size(), 4);
                // 群聊消息，显示群聊头像
//
//                if(size!=0){
//                    new Thread(new Runnable() {
//                        @Override
//                        public void run() {
//                            for (int i = 0; i < size; i++) {
//                                String username = group.getMembers().get(i);
//                                IMUser user = IMUserRepository.getContactByUserName(mContext, username);
//                                if (user != null) {
//                                    Bitmap bitmap = ImageLoader.getInstance().loadImageSync(user.getAvatarSmall(),avatarSize);
//
//                                    LogUtil.d("load_bitmap", user.getAvatar() + "=" + bitmap);
//                                    if(bitmap==null){
//                                        bitmap= BitmapFactory.decodeResource(mContext.getResources(), R.drawable.avatar_placeholder_round);
//                                    }
//                                    membersAvatars.add(bitmap);
//                                }else{
//                                    Bitmap bitmap=BitmapFactory.decodeResource(mContext.getResources(), R.drawable.avatar_placeholder_round);
//                                    membersAvatars.add(bitmap);
//                                }
//                            }
//                            handler.post(new Runnable() {
//                                @Override
//                                public void run() {
//                                    mAvatar.setImageBitmap(JoinBitmaps.createBitmap(LocalDisplay.dp2px(45),
//                                            LocalDisplay.dp2px(45), membersAvatars));
//                                }
//                            });
//
//
//                        }
//                    }).start();
//                }else{
                    mAvatar.setImageResource(R.drawable.avatar_placeholder_round);
 //               }
                mName.setText(imUser.getNickName() != null ? imUser.getNickName() : " ");
            } else {
                if(imUser!=null){
                    // 本地或者服务器获取用户详情，以用来显示头像和nick
                    ImageLoader.getInstance().displayImage(imUser.getAvatarSmall(), mAvatar, options);
                    if (TextUtils.isEmpty(imUser.getMemo())) {
                        mName.setText(imUser.getNickName());
                    } else {
                        mName.setText(imUser.getMemo());
                    }
                }
            }
            contentView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    if ("group".equals(itemData.getChatType())) {
                        intent.putExtra("chatType", "group");
                        intent.putExtra("toId", imUser.getUserId());

                    } else {
                        intent.putExtra("chatType", "single");
                        intent.putExtra("toId",imUser.getUserId());
                    }
                    setResult(RESULT_OK, intent);
                    finish();
                }
            });

        }
    }
}
