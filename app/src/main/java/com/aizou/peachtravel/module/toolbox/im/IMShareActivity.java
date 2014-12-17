package com.aizou.peachtravel.module.toolbox.im;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.aizou.core.widget.listHelper.ListViewDataAdapter;
import com.aizou.core.widget.listHelper.ViewHolderBase;
import com.aizou.core.widget.listHelper.ViewHolderCreator;
import com.aizou.peachtravel.R;
import com.aizou.peachtravel.base.PeachBaseActivity;
import com.aizou.peachtravel.bean.PeachConversation;
import com.aizou.peachtravel.bean.PeachUser;
import com.aizou.peachtravel.bean.PoiDetailBean;
import com.aizou.peachtravel.common.account.AccountManager;
import com.aizou.peachtravel.common.utils.IMUtils;
import com.aizou.peachtravel.common.utils.UILUtils;
import com.aizou.peachtravel.common.widget.TitleHeaderBar;
import com.aizou.peachtravel.db.IMUser;
import com.aizou.peachtravel.db.respository.IMUserRepository;
import com.aizou.peachtravel.module.my.LoginActivity;
import com.aizou.peachtravel.module.toolbox.im.adapter.ChatAllHistoryAdapter;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMContact;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMGroup;
import com.easemob.chat.EMGroupManager;
import com.easemob.chat.EMMessage;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
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
        mCreateNewTalk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext,PickContactsWithCheckboxActivity.class);
                intent.putExtra("request",IMMainActivity.NEW_CHAT_REQUEST_CODE);
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
    private List<PeachConversation> loadConversationsWithRecentChat() {
        // 获取所有会话，包括陌生人
        Hashtable<String, EMConversation> conversations = EMChatManager.getInstance().getAllConversations();
        List<PeachConversation> conversationList = new ArrayList<PeachConversation>();
        //过滤掉messages seize为0的conversation
        for (EMConversation conversation : conversations.values()) {
            if (conversation.getAllMessages().size() != 0) {
                PeachConversation peachConversation = new PeachConversation();
                peachConversation.emConversation = conversation;
                if (!TextUtils.isEmpty(conversation.getUserName())) {
                    IMUser user = IMUserRepository.getContactByUserName(mContext, conversation.getUserName());
                    peachConversation.imUser = user;
                }
                conversationList.add(peachConversation);
            }
        }
        // 排序
        sortConversationByLastChatTime(conversationList);
        return conversationList;
    }

    /**
     * 根据最后一条消息的时间排序
     *
     * @param conversationList
     */
    private void sortConversationByLastChatTime(List<PeachConversation> conversationList) {
        Collections.sort(conversationList, new Comparator<PeachConversation>() {
            @Override
            public int compare(final PeachConversation con1, final PeachConversation con2) {

                EMMessage con2LastMessage = con2.emConversation.getLastMessage();
                EMMessage con1LastMessage = con1.emConversation.getLastMessage();
                if (con2LastMessage.getMsgTime() == con1LastMessage.getMsgTime()) {
                    return 0;
                } else if (con2LastMessage.getMsgTime() > con1LastMessage.getMsgTime()) {
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

    public class ShareChatViewHolder extends ViewHolderBase<PeachConversation> {

        @InjectView(R.id.avatar)
        ImageView mAvatar;
        @InjectView(R.id.name)
        TextView mName;
        View contentView;


        @Override
        public View createView(LayoutInflater layoutInflater) {
            contentView = layoutInflater.inflate(R.layout.row_im_share, mImShareLv, false);
            ButterKnife.inject(this, contentView);
            return contentView;
        }

        @Override
        public void showData(int position, PeachConversation itemData) {
            // 获取与此用户/群组的会话
            EMConversation conversation = itemData.emConversation;
            IMUser imUser = itemData.imUser;
            // 获取用户username或者群组groupid
            final String username = conversation.getUserName();
            List<EMGroup> groups = EMGroupManager.getInstance().getAllGroups();
            EMContact contact = null;
            boolean isGroup = false;
            for (EMGroup group : groups) {
                if (group.getGroupId().equals(username)) {
                    isGroup = true;
                    contact = group;
                    break;
                }
            }
            if (isGroup) {
                // 群聊消息，显示群聊头像
                mAvatar.setImageResource(R.drawable.group_icon);
                mName.setText(contact.getNick() != null ? contact.getNick() : username);
            } else {
                if(imUser!=null){
                    // 本地或者服务器获取用户详情，以用来显示头像和nick
                    ImageLoader.getInstance().displayImage(imUser.getAvatar(), mAvatar, UILUtils.getDefaultOption());
                    if (TextUtils.isEmpty(imUser.getMemo())) {
                        mName.setText(imUser.getNick());
                    } else {
                        mName.setText(imUser.getMemo());
                    }
                }
            }
            final boolean finalIsGroup = isGroup;
            final EMContact finalContact = contact;
            contentView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    if(finalIsGroup){
                        intent.putExtra("chatType", ChatActivity.CHATTYPE_GROUP);
                        intent.putExtra("toId", ((EMGroup) finalContact).getGroupId());

                    }else{
                        intent.putExtra("chatType", ChatActivity.CHATTYPE_SINGLE);
                        intent.putExtra("toId", username);
                    }
                    setResult(RESULT_OK,intent);
                    finish();
                }
            });

        }
    }
}
