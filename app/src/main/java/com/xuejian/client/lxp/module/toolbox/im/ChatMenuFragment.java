package com.xuejian.client.lxp.module.toolbox.im;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;

import com.lv.im.IMClient;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.common.dialog.PeachMessageDialog;
import com.xuejian.client.lxp.config.SettingConfig;

/**
 * Created by yibiao.qin on 2015/7/10.
 */
public class ChatMenuFragment extends Fragment {
    String userId;
    private ChatActivity mActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chat_menu, container, false);
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = (ChatActivity) activity;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        userId = getArguments().getString("userId");
        getView().findViewById(R.id.clear_all_history).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final PeachMessageDialog dialog = new PeachMessageDialog(getActivity());
                dialog.setTitle("提示");
                dialog.setMessage("确定清空与此好友的聊天记录吗？");
                dialog.setPositiveButton("确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        clearMessageHistory();
                        dialog.dismiss();
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
        });
        CheckedTextView ctv = (CheckedTextView) getView().findViewById(R.id.ctv_msg_notify_setting);
        ctv.setChecked(SettingConfig.getInstance().getLxpNoticeSetting(getActivity().getApplicationContext(), userId));
        ctv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckedTextView ctv = (CheckedTextView) v;
                Boolean isOpen = ctv.isChecked();
                ctv.setChecked(!isOpen);
                SettingConfig.getInstance().setLxpNoticeSetting(getActivity().getApplicationContext(), userId, !isOpen);
            }
        });
    }

    /**
     * 清空群聊天记录
     */
    public void clearMessageHistory() {

        IMClient.getInstance().cleanMessageHistory(userId);
        // EMChatManager.getInstance().clearConversation(group.getGroupId());
        //adapter.refresh(EMChatManager.getInstance().getConversation(toChatUsername));
    }

}
