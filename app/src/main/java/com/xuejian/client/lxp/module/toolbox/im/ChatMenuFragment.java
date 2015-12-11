package com.xuejian.client.lxp.module.toolbox.im;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.Toast;

import com.lv.Listener.HttpCallback;
import com.lv.im.IMClient;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.bean.ImageBean;
import com.xuejian.client.lxp.common.dialog.PeachMessageDialog;
import com.xuejian.client.lxp.config.SettingConfig;
import com.xuejian.client.lxp.db.User;
import com.xuejian.client.lxp.db.UserDBManager;
import com.xuejian.client.lxp.module.dest.CityPictureActivity;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by yibiao.qin on 2015/7/10.
 */
public class ChatMenuFragment extends Fragment {
    String userId;
    private ChatActivity mActivity;
    private String conversation;
    private static final int NEW_CHAT_REQUEST_CODE = 101;

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
                IMClient.getInstance().muteConversation(userId, !isOpen, new HttpCallback() {
                    @Override
                    public void onSuccess() {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                SettingConfig.getInstance().setLxpNoticeSetting(getActivity().getApplicationContext(), userId, !isOpen);
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

                    @Override
                    public void onSuccess(String result) {
                    }
                });

            }
        });
        getView().findViewById(R.id.tv_pics).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Action1<Throwable> onErrorAction = new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Toast.makeText(getActivity(), "图片获取失败", Toast.LENGTH_SHORT).show();
                    }
                };
                mActivity.compositeSubscription.add(
                        Observable.create(new Observable.OnSubscribe<ArrayList<String>>() {
                            @Override
                            public void call(Subscriber<? super ArrayList<String>> subscriber) {
                                try {
                                    subscriber.onNext(IMClient.getInstance().getPics(userId));
                                    subscriber.onCompleted();
                                } catch (Exception e) {
                                    subscriber.onError(e);
                                }
                            }
                        })
                                .flatMap(new Func1<ArrayList<String>, Observable<String>>() {
                                    @Override
                                    public Observable<String> call(ArrayList<String> strings) {
                                        return Observable.from(strings);
                                    }
                                })
                                .map(new Func1<String, ImageBean>() {
                                    @Override
                                    public ImageBean call(String s) {
                                        ImageBean bean = new ImageBean();
                                        bean.url = s;
                                        bean.full = s;
                                        return bean;
                                    }
                                })
                                .toList()
                                .map(new Func1<List<ImageBean>, ArrayList<ImageBean>>() {
                                    @Override
                                    public ArrayList<ImageBean> call(List<ImageBean> imageBeans) {
                                        ArrayList<ImageBean> list = new ArrayList<ImageBean>();
                                        list.addAll(imageBeans);
                                        return list;
                                    }
                                })
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Action1<ArrayList<ImageBean>>() {
                                    @Override
                                    public void call(final ArrayList<ImageBean> imageBeans) {
                                        startAlbum(imageBeans);
                                    }
                                }, onErrorAction)
                );


            }
        });
        getView().findViewById(R.id.create_group).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User friend = UserDBManager.getInstance().getContactByUserId(Long.parseLong(userId));
                if (friend == null) {
                    friend = new User();
                    friend.setUserId(Long.parseLong(userId));
                }
                Intent intent = new Intent(getActivity(), PickContactsWithCheckboxActivity.class);
                intent.putExtra("request", NEW_CHAT_REQUEST_CODE);
                intent.putExtra("fromSingle", true);
                intent.putExtra("single", friend);
                startActivityForResult(intent, NEW_CHAT_REQUEST_CODE);
            }
        });
    }

    public void startAlbum(ArrayList<ImageBean> imageBeans) {
        if (imageBeans.size() == 0) {
            Toast.makeText(getActivity(), "暂时没有聊天图片", Toast.LENGTH_SHORT).show();
        } else {
            Intent intent2 = new Intent(getActivity(), CityPictureActivity.class);
            intent2.putExtra("chatPics", imageBeans);
            intent2.putExtra("showChatImage", true);
            startActivity(intent2);
        }
    }

    /**
     * 清空群聊天记录
     */
    public void clearMessageHistory() {

        IMClient.getInstance().cleanMessageHistory(userId);
        ChatActivity.messageList.clear();
        ((ChatActivity) getActivity()).refresh();
        // EMChatManager.getInstance().clearConversation(group.getGroupId());
        //adapter.refresh(EMChatManager.getInstance().getConversation(toChatUsername));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == -1) {
            if (requestCode == NEW_CHAT_REQUEST_CODE) {
                long id = data.getLongExtra("toId", 0);
                Intent intent = new Intent(getActivity(), ChatActivity.class);
                intent.putExtra("friend_id", id + "");
                intent.putExtra("chatType", "group");
                getActivity().finish();
                startActivity(intent);

            }
        }
    }
}
