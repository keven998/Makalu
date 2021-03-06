package com.xuejian.client.lxp.module.toolbox.im;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.aizou.core.dialog.ToastUtil;
import com.aizou.core.http.HttpCallBack;
import com.aizou.core.utils.LocalDisplay;
import com.aizou.core.widget.listHelper.ListViewDataAdapter;
import com.aizou.core.widget.listHelper.ViewHolderBase;
import com.aizou.core.widget.listHelper.ViewHolderCreator;
import com.alibaba.fastjson.JSON;
import com.lv.Listener.HttpCallback;
import com.lv.utils.Config;
import com.lv.im.IMClient;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.PeachBaseFragment;
import com.xuejian.client.lxp.bean.ImageBean;
import com.xuejian.client.lxp.common.account.AccountManager;
import com.xuejian.client.lxp.common.api.GroupApi;
import com.xuejian.client.lxp.common.dialog.DialogManager;
import com.xuejian.client.lxp.common.dialog.PeachMessageDialog;
import com.xuejian.client.lxp.common.utils.AnimationSimple;
import com.xuejian.client.lxp.config.PeachApplication;
import com.xuejian.client.lxp.config.SettingConfig;
import com.xuejian.client.lxp.db.User;
import com.xuejian.client.lxp.db.UserDBManager;
import com.xuejian.client.lxp.module.dest.CityPictureActivity;
import com.xuejian.client.lxp.module.toolbox.HisMainPageActivity;
import com.xuejian.client.lxp.module.toolbox.im.group.GroupManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class GroupDetailFragment extends PeachBaseFragment {

    private static final int REQUEST_CODE_ADD_USER = 0;
    //    private static final int REQUEST_CODE_EXIT = 1;
//    private static final int REQUEST_CODE_EXIT_DELETE = 2;
//    private static final int REQUEST_CODE_CLEAR_ALL_HISTORY = 3;
    private static final int REQUEST_CODE_MODIFY_GROUP_NAME = 4;

    private ListView memberGv;
    private String groupId;

    private TextView groupNameTv;
    private TextView addGroup;
    private TextView delGroupMember;

    public boolean isInDeleteMode;
    public MemberAdapter memberAdapter;
    private User group;
    //清空所有聊天记录
    private ChatActivity mActivity;
    private String conversation;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_group_details, container, false);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = (ChatActivity) activity;
    }

    public void closeDeleteMode() {
        isInDeleteMode = false;
        memberAdapter.notifyDataSetChanged();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        groupId = getArguments().getString("groupId");
        conversation = getArguments().getString("conversation");
        memberGv = (ListView) getView().findViewById(R.id.gv_members);
        addGroup = (TextView) getView().findViewById(R.id.tv_add_to_group);

        delGroupMember = (TextView) getView().findViewById(R.id.tv_del_to_group);
        delGroupMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // CheckedTextView view = (CheckedTextView) v;
                isInDeleteMode = !isInDeleteMode;
                memberAdapter.notifyDataSetChanged();
            }
        });
        addGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 进入选人页面
                startActivityForResult(
                        (new Intent(getActivity(), PickContactsWithCheckboxActivity.class).putExtra("groupId", groupId)), REQUEST_CODE_ADD_USER);
            }
        });
        groupNameTv = (TextView) getView().findViewById(R.id.tv_groupName);
        CheckedTextView ctv = (CheckedTextView) getView().findViewById(R.id.ctv_msg_notify_setting);
        ctv.setChecked(SettingConfig.getInstance().getLxpNoticeSetting(getActivity().getApplicationContext(), groupId));
        ctv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final CheckedTextView ctv = (CheckedTextView) v;
                final Boolean isOpen = ctv.isChecked();
                ctv.setChecked(!isOpen);
                IMClient.getInstance().muteConversation(groupId, !isOpen, new HttpCallback() {
                    @Override
                    public void onSuccess() {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                SettingConfig.getInstance().setLxpNoticeSetting(getActivity().getApplicationContext(), groupId, !isOpen);
                            }
                        });
                    }

                    @Override
                    public void onFailed(int code) {
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

        // 获取传过来的groupid
        if (groupId != null) {
            group = UserDBManager.getInstance().getContactByUserId(Long.parseLong(groupId));
        }

        bindView();
        // 保证每次进详情看到的都是最新的group
        updateGroup();

        getView().findViewById(R.id.clear_all_history).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final PeachMessageDialog dialog = new PeachMessageDialog(getActivity());
                dialog.setTitle("提示");
                dialog.setMessage("确定清空此群的聊天记录吗？");
                dialog.setPositiveButton("确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        clearGroupHistory();
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
                                    subscriber.onNext(IMClient.getInstance().getPics(groupId));
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
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_ADD_USER:// 添加群成员
                    setUpGroupMemeber("update");
                    break;
                case REQUEST_CODE_MODIFY_GROUP_NAME: // 修改群名称
                    groupNameTv.setText(data.getStringExtra("groupName"));
                    ((ChatActivity)getActivity()).setTitleText(data.getStringExtra("groupName"));
                   // group = UserDBManager.getInstance().getContactByUserId(Long.parseLong(groupId));
                    if(group!=null){
                        group.setNickName(data.getStringExtra("groupName"));
                        UserDBManager.getInstance().saveContact(group);
                    }


                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 点击退出群组按钮
     *
     * @param
     */
    public void exitGroupTips() {
        final PeachMessageDialog dialog = new PeachMessageDialog(getActivity());
        dialog.setTitle("提示");
        dialog.setMessage("退出后，将不再接收此群聊消息");
        dialog.setPositiveButton("退出", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                exitGroup();
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
    public void clearGroupHistory() {

        IMClient.getInstance().cleanMessageHistory(groupId);
        ChatActivity.messageList.clear();
        ((ChatActivity) getActivity()).refresh();
        // EMChatManager.getInstance().clearConversation(group.getGroupId());
        //adapter.refresh(EMChatManager.getInstance().getConversation(toChatUsername));
    }


    /**
     * 退出群组
     */
    public void exitGroup() {

        try {
            IMClient.getInstance().deleteConversation(groupId);
            DialogManager.getInstance().showLoadingDialog(getActivity());
        } catch (Exception e) {
            e.printStackTrace();
        }
        GroupManager.getGroupManager().quitGroup(groupId, new HttpCallBack() {
            @Override
            public void doSuccess(Object result, String method) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        UserDBManager.getInstance().quiteGroup(groupId);
                        if (getActivity() != null) {
                            DialogManager.getInstance().dissMissLoadingDialog();
                            //  getActivity().setResult(Activity.RESULT_OK);
                            getActivity().finish();
                        }
                    }
                });

            }

            @Override
            public void doFailure(Exception error, String msg, String method) {
                DialogManager.getInstance().dissMissLoadingDialog();
                ToastUtil.getInstance(PeachApplication.getContext()).showToast("呃~网络有些问题");
            }

            @Override
            public void doFailure(Exception error, String msg, String method, int code) {

            }
        });
    }


    public void bindView() {
        memberAdapter = new MemberAdapter(new ViewHolderCreator<User>() {
            @Override
            public ViewHolderBase<User> createViewHolder() {
                return new MemberViewHolder();
            }
        });
        long creater = UserDBManager.getInstance().getGroupCreater(Long.parseLong(groupId));
        if (creater < 0 || creater != Long.parseLong(AccountManager.CurrentUserId)) {
            delGroupMember.setVisibility(View.INVISIBLE);
        }
        groupNameTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ModifyGroupNameActivity.class);
                intent.putExtra("groupId", groupId);
                startActivityForResult(intent, REQUEST_CODE_MODIFY_GROUP_NAME);
            }
        });
        if (memberGv.getFooterViewsCount() == 0) {
            View view = getActivity().getLayoutInflater().inflate(R.layout.im_group_option_footer, null);
            memberGv.addFooterView(view);
            view.findViewById(R.id.footer_btn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    exitGroupTips();
                }
            });
        }

        memberGv.setAdapter(memberAdapter);
        setUpGroupMemeber("");
        if (group != null && group.getNickName() != null) {
            groupNameTv.setText(group.getNickName());
        }
    }

    public void setUpGroupMemeber(String type) {
        final List<User> members = UserDBManager.getInstance().getGroupMember(Long.parseLong(groupId));
        final List<String> unkownMembers = new ArrayList<String>();
        memberAdapter.getDataList().clear();
        if (members == null || "update".equals(type)||members.size()==0) {
            //fetch info
            GroupApi.getGroupMemberInfo(groupId, new HttpCallBack() {
                @Override
                public void doSuccess(Object result, String method) {
                    JSONObject object = null;
                    JSONArray userList = null;
                    List<User> list = new ArrayList<User>();
                    try {
                        object = new JSONObject(result.toString());
                        userList = object.getJSONArray("result");
                        for (int i = 0; i < userList.length(); i++) {
                            String str = userList.get(i).toString();
                            User user = JSON.parseObject(str, User.class);
                            list.add(user);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    UserDBManager.getInstance().updateGroupMemberInfo(list, groupId);
                    memberAdapter.getDataList().clear();
                    memberAdapter.getDataList().addAll(list);
                    memberAdapter.notifyDataSetChanged();
                }

                @Override
                public void doFailure(Exception error, String msg, String method) {

                }

                @Override
                public void doFailure(Exception error, String msg, String method, int code) {

                }
            });

        } else {
            for (User user : members) {
                memberAdapter.getDataList().add(user);
            }
        }
        memberAdapter.notifyDataSetChanged();
        if (unkownMembers.size() > 0) {

        }

    }

    protected void updateGroup() {
        //更新本地数据
        GroupApi.getGroupInfo(groupId, new HttpCallBack() {
            @Override
            public void doSuccess(Object result, String method) {
                JSONObject object = null;
                try {
                    if (Config.isDebug) {
                        Log.i(Config.TAG, "group info : " + result);
                    }
                    object = new JSONObject(result.toString());
                    JSONObject o = object.getJSONObject("result");
                    User user = new User();
                    user.setNickName(o.get("name").toString() == null ? " " : o.get("name").toString());
                    o.remove("name");
                    user.setAvatar(o.get("avatar").toString());
                    o.remove("avatar");
                    user.setExt(o.toString());
                    user.setType(8);
                    UserDBManager.getInstance().updateGroupInfo(user, groupId);
                    try {
                        ChatActivity activity = (ChatActivity) getActivity();
                        activity.setTitleName(user.getNickName());
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                bindView();
            }

            @Override
            public void doFailure(Exception error, String msg, String method) {

            }

            @Override
            public void doFailure(Exception error, String msg, String method, int code) {

            }
        });

    }

    private class MemberAdapter extends ListViewDataAdapter<User> {

        /**
         * @param viewHolderCreator The view holder creator will create a View Holder that extends {@link com.aizou.core.widget.listHelper.ViewHolderBase}
         */
        public MemberAdapter(ViewHolderCreator viewHolderCreator) {
            super(viewHolderCreator);
        }

        @Override
        public int getCount() {
            return super.getCount();
        }
    }

    private class MemberViewHolder extends ViewHolderBase<User> {
        private View contentView;
        private ImageView avatarIv, removeIv;
        private TextView nicknameTv, viewHolderName;
        private DisplayImageOptions picOptions;

        public MemberViewHolder() {
            super();
            picOptions = new DisplayImageOptions.Builder()
                    .cacheInMemory(true)
                    .cacheOnDisk(true).bitmapConfig(Bitmap.Config.ARGB_8888)
                    .resetViewBeforeLoading(true)
                    .showImageOnFail(R.drawable.ic_home_more_avatar_unknown_round)
                    .showImageOnLoading(R.drawable.ic_home_more_avatar_unknown_round)
                    .showImageForEmptyUri(R.drawable.ic_home_more_avatar_unknown_round)
                    .displayer(new RoundedBitmapDisplayer(LocalDisplay.dp2px(20)))
                    .imageScaleType(ImageScaleType.IN_SAMPLE_INT).build();
        }


        @Override
        public View createView(LayoutInflater layoutInflater) {
            contentView = layoutInflater.inflate(R.layout.group_member_list, null);
            avatarIv = (ImageView) contentView.findViewById(R.id.iv_avatar);
            removeIv = (ImageView) contentView.findViewById(R.id.group_del_icon);
            nicknameTv = (TextView) contentView.findViewById(R.id.tv_nickname);
            return contentView;
        }

        @Override
        public void showData(int position, final User itemData) {
            if (!TextUtils.isEmpty(itemData.getMemo())){
                nicknameTv.setText(String.format("%s(%s)",itemData.getMemo() ,itemData.getNickName()));
            }
            else nicknameTv.setText(itemData.getNickName());
            ImageLoader.getInstance().displayImage(itemData.getAvatar(), avatarIv, picOptions);

            contentView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!AccountManager.getCurrentUserId().equals(String.valueOf(itemData.getUserId()))) {
                        Intent intent = new Intent(getActivity(), HisMainPageActivity.class);
                        intent.putExtra("userId", itemData.getUserId());
                        intent.putExtra("userNick", itemData.getNickName());
                        startActivity(intent);
                    }
                }
            });


            if (isInDeleteMode && itemData.getUserId() != Long.parseLong(AccountManager.getCurrentUserId())) {
                Animation animation = AnimationSimple.expand(removeIv);
                removeIv.startAnimation(animation);
                animation = AnimationSimple.expand(removeIv);
                removeIv.startAnimation(animation);
                removeIv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final PeachMessageDialog dialog = new PeachMessageDialog(getActivity());
                        dialog.setTitle("提示");
                        dialog.setMessage("确定移除该成员");
                        dialog.setPositiveButton("确定", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                                deleteMembersFromGroup(itemData.getUserId());
                                isInDeleteMode = false;
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
            } else {
                removeIv.setVisibility(View.GONE);
            }
        }

        /**
         * 删除群成员
         */
        protected void deleteMembersFromGroup(final long userID) {
            final ProgressDialog deleteDialog = new ProgressDialog(getActivity());
            deleteDialog.setMessage("正在移除");
            deleteDialog.setCanceledOnTouchOutside(false);
            deleteDialog.show();
            GroupApi.deleteGroupMember(groupId, userID, new HttpCallBack() {
                @Override
                public void doSuccess(Object result, String method) {
                    setUpGroupMemeber("update");
                    deleteDialog.dismiss();
                }

                @Override
                public void doFailure(Exception error, String msg, String method) {
                    deleteDialog.dismiss();
                    ToastUtil.getInstance(PeachApplication.getContext()).showToast("请求失败");
                }

                @Override
                public void doFailure(Exception error, String msg, String method, int code) {

                }
            });
        }
    }

    public static void refresh() {

    }
}
