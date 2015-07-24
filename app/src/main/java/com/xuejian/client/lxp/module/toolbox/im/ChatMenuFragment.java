package com.xuejian.client.lxp.module.toolbox.im;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;

import com.lv.Listener.HttpCallback;
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
    private String conversation;
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
        conversation = getArguments().getString("conversation");
        getView().findViewById(R.id.clear_all_history).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final PeachMessageDialog dialog = new PeachMessageDialog(getActivity());
                dialog.setTitle("提示");
                dialog.setMessage("确定清空全部聊天记录");
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
                final CheckedTextView ctv = (CheckedTextView) v;
                final Boolean isOpen = ctv.isChecked();
                ctv.setChecked(!isOpen);
                IMClient.getInstance().muteConversation(conversation, !isOpen, new HttpCallback() {
                    @Override
                    public void onSuccess() {
                        System.out.println("设置成功");
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                SettingConfig.getInstance().setLxpNoticeSetting(getActivity().getApplicationContext(), userId, !isOpen);
                                System.out.println(SettingConfig.getInstance().getLxpNoticeSetting(getActivity().getApplicationContext(),userId));
                            }
                        });

                    }

                    @Override
                    public void onFailed(int code) {
                        System.out.println(code);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ctv.setChecked(isOpen);
                            }
                        });
                    }
                });

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
