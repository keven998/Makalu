package com.xuejian.client.lxp.common.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.aizou.core.dialog.ToastUtil;
import com.aizou.core.utils.GsonTools;
import com.aizou.core.utils.LocalDisplay;
import com.lv.Listener.SendMsgListener;
import com.lv.bean.MessageBean;
import com.lv.im.IMClient;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.bean.LocBean;
import com.xuejian.client.lxp.bean.PoiDetailBean;
import com.xuejian.client.lxp.bean.SpotDetailBean;
import com.xuejian.client.lxp.bean.StrategyBean;
import com.xuejian.client.lxp.bean.TravelNoteBean;
import com.xuejian.client.lxp.common.account.AccountManager;
import com.xuejian.client.lxp.common.dialog.DialogManager;
import com.xuejian.client.lxp.common.imageloader.UILUtils;
import com.xuejian.client.lxp.common.share.ICreateShareDialog;
import com.xuejian.client.lxp.common.share.ShareDialogBean;
import com.xuejian.client.lxp.config.Constant;
import com.xuejian.client.lxp.db.User;
import com.xuejian.client.lxp.module.my.LoginActivity;
import com.xuejian.client.lxp.module.toolbox.im.IMShareActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by Rjm on 2014/11/5.
 */
public class IMUtils {
    public final static int IM_SHARE_REQUEST_CODE = 200;
    public final static int IM_LOGIN_REQUEST_CODE = 300;

    public static User setUserHead(User user) {
//        String username=user.getNickName();
        String headerName = user.getNickName();
//        if (!TextUtils.isEmpty(user.getNick())) {
//            headerName = user.getNick();
//        } else {
//            headerName = user.getUsername();
//        }
//        if (username.equals(Constant.NEW_FRIENDS_USERNAME)) {
//            user.setHeader("");
//        } else if (Character.isDigit(headerName.charAt(0))) {
//            user.setHeader("#");
//        } else {
        user.setHeader(com.xuejian.client.lxp.common.utils.HanziToPinyin.getInstance().get(headerName.substring(0, 1)).get(0).target.substring(
                0, 1).toUpperCase());
        char header = user.getHeader().toLowerCase().charAt(0);
        if (header < 'a' || header > 'z') {
            user.setHeader("#");
            //   }
        }
        return user;
    }

    static String getStrng(Context context, int resId) {
        return context.getResources().getString(resId);
    }

    public static void HandleCMDInfoFromMessage(MessageBean m) {
        String cmd = m.getMessage();
        try {
            JSONObject object = new JSONObject(cmd);
            String action = object.getString("action");
            switch (action) {
                case "D_INVITE":
//                    long chatId = object.getLong("groupId");
//                    long inviteId = object.getLong("userId");
//                    String nickName = object.getString("nickName");
//                    String groupName = object.getString("groupName");
//                    User group = new User(chatId, groupName, "", 8);
//                    UserDBManager.getInstance().saveContact(group);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
//    public static IMUser getUserInfoFromMessage(Context context,EMMessage message){
//        String fromUser = message.getStringAttribute(Constant.FROM_USER,"");
//        if(!TextUtils.isEmpty(fromUser)){
//            ExtFromUser user = GsonTools.parseJsonToBean(fromUser, ExtFromUser.class);
//            IMUser imUser = IMUserRepository.getContactByUserName(context, message.getFrom());
//            if(imUser!=null){
//                imUser.setNick(user.nickName);
//                imUser.setAvatar(user.avatar);
//                imUser.setUserId(user.userId);
//                imUser.setAvatarSmall(user.avatar);
//            }else{
//                imUser = new IMUser();
//                imUser.setUsername(message.getFrom());
//                imUser.setNick(user.nickName);
//                imUser.setUserId(user.userId);
//                imUser.setAvatar(user.avatar);
//                imUser.setAvatarSmall(user.avatar);
//
//            }
//            IMUserRepository.saveContact(context, imUser);
//            return imUser;
//        }
//        return null;
//    }

    public static void onClickImShare(Context context) {
        User user = AccountManager.getInstance().getLoginAccount(context);
        if (user != null) {  //&& !TextUtils.isEmpty(user.easemobUser)
            Intent intent = new Intent(context, IMShareActivity.class);
            ((Activity) context).startActivityForResult(intent, IM_SHARE_REQUEST_CODE);

        } else {
            ToastUtil.getInstance(context).showToast("请先登录");
            Intent intent = new Intent(context, LoginActivity.class);
            ((Activity) context).startActivityForResult(intent, IM_LOGIN_REQUEST_CODE);
        }
    }

    public static void onShareResult(final Context context, ICreateShareDialog iCreateShareDialog, int requestCode, int resultCode, Intent data, final OnDialogShareCallBack callback) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == IM_LOGIN_REQUEST_CODE) {
                Intent intent = new Intent(context, IMShareActivity.class);
                ((Activity) context).startActivityForResult(intent, IM_SHARE_REQUEST_CODE);
            } else if (requestCode == IM_SHARE_REQUEST_CODE) {
                final String chatType = data.getStringExtra("chatType");
                final String toId = "" + data.getLongExtra("toId", 0);
                showImShareDialog(context, iCreateShareDialog, new OnDialogShareCallBack() {
                    @Override
                    public void onDialogShareOk(Dialog dialog, int type, String content) {
                        DialogManager.getInstance().showLoadingDialog(context);
                        IMClient.getInstance().sendExtMessage(AccountManager.getCurrentUserId(), toId, chatType, content, type, new SendMsgListener() {

                            @Override
                            public void onSuccess() {
                                DialogManager.getInstance().dissMissLoadingDialog();
                                ((Activity) context).runOnUiThread(new Runnable() {
                                    public void run() {
                                        ToastUtil.getInstance(context).showToast("已发送~");

                                    }
                                });
                            }

                            @Override
                            public void onFailed(int code) {
                                DialogManager.getInstance().dissMissLoadingDialog();
                                ((Activity) context).runOnUiThread(new Runnable() {
                                    public void run() {
                                        ToastUtil.getInstance(context).showToast("好像发送失败了");

                                    }
                                });
                            }
                        });
//                        sendExtMessage(context, type, content, chatType, toId, new EMCallBack() {
//                            @Override
//                            public void onSuccess() {
//                                DialogManager.getInstance().dissMissLoadingDialog();
//                                ((Activity) context).runOnUiThread(new Runnable() {
//                                    public void run() {
//                                        ToastUtil.getInstance(context).showToast("已发送~");
//
//                                    }
//                                });
//
//                            }
//
//                            @Override
//                            public void onError(int i, String s) {
//                                DialogManager.getInstance().dissMissLoadingDialog();
//                                ((Activity) context).runOnUiThread(new Runnable() {
//                                    public void run() {
//                                        ToastUtil.getInstance(context).showToast("好像发送失败了");
//
//                                    }
//                                });
//                            }
//
//                            @Override
//                            public void onProgress(int i, String s) {
//
//                            }
//                        });
                        if (callback != null) {
                            callback.onDialogShareOk(dialog, type, content);
                        }
                    }

                    @Override
                    public void onDialogShareCancle(Dialog dialog, int type, String content) {
                        if (callback != null) {
                            callback.onDialogShareCancle(dialog, type, content);
                        }
                    }
                });

            }
        }

    }


    public static void showImShareDialog(Context context, final ICreateShareDialog iCreateShareDialog, final OnDialogShareCallBack callback) {
        final ShareDialogBean dialogBean = iCreateShareDialog.createShareBean();
        final Dialog dialog = new Dialog(context, R.style.ComfirmDialog);
        View contentView = View.inflate(context, R.layout.dialog_im_share, null);
        TextView titleTv = (TextView) contentView.findViewById(R.id.tv_title);
        ImageView vsIv = (ImageView) contentView.findViewById(R.id.iv_image);
        TextView nameTv = (TextView) contentView.findViewById(R.id.tv_name);
        TextView attrTv = (TextView) contentView.findViewById(R.id.tv_attr);
        TextView descTv = (TextView) contentView.findViewById(R.id.tv_desc);
        Button okBtn = (Button) contentView.findViewById(R.id.btn_ok);
        Button cancleBtn = (Button) contentView.findViewById(R.id.btn_cancle);
        titleTv.setText(dialogBean.getTitle());
        nameTv.setText(dialogBean.getName());
        if (TextUtils.isEmpty(dialogBean.getAttr())) {
            attrTv.setVisibility(View.GONE);
        } else {
            if (dialogBean.getExtType() == Constant.ExtType.FOOD || dialogBean.getExtType() == Constant.ExtType.HOTEL || dialogBean.getExtType() == Constant.ExtType.SHOPPING) {
                attrTv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_rating_start_highlight, 0, 0, 0);
            }
            attrTv.setVisibility(View.VISIBLE);
            attrTv.setText(dialogBean.getAttr());
        }

        descTv.setText(dialogBean.getDesc());
        ImageLoader.getInstance().displayImage(dialogBean.getImage(), vsIv, UILUtils.getRadiusOption(LocalDisplay.dp2px(2)));
        dialog.setContentView(contentView);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                callback.onDialogShareOk(dialog, dialogBean.getExtType(), GsonTools.createGsonString(dialogBean.getExtMessageBean()));
            }
        });
        cancleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                callback.onDialogShareCancle(dialog, dialogBean.getExtType(), GsonTools.createGsonString(dialogBean.getExtMessageBean()));
            }
        });
        WindowManager m = ((Activity) context).getWindowManager();
        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
        WindowManager.LayoutParams p = dialog.getWindow().getAttributes(); // 获取对话框当前的参数值
        p.width = d.getWidth() - LocalDisplay.dp2px(40); // 宽度设置为屏幕的0.65
        dialog.getWindow().setAttributes(p);
        dialog.show();
    }


    public static String createExtMessageContentForSpot(SpotDetailBean detailBean) {

        JSONObject contentJson = new JSONObject();
        try {
            contentJson.put("id", detailBean.id);
            contentJson.put("image", detailBean.images.size() > 0 ? detailBean.images.get(0).url : "");
            contentJson.put("name", detailBean.zhName);
            contentJson.put("desc", detailBean.desc);
            contentJson.put("timeCost", detailBean.timeCostDesc);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return contentJson.toString();
    }

    public static String createExtMessageContentForPoi(PoiDetailBean detailBean) {

        JSONObject contentJson = new JSONObject();
        try {
            contentJson.put("id", detailBean.id);
            contentJson.put("image", detailBean.images.size() > 0 ? detailBean.images.get(0).url : "");
            contentJson.put("name", detailBean.zhName);
            contentJson.put("desc", detailBean.desc);
            contentJson.put("rating", detailBean.rating + "");
            contentJson.put("price", detailBean.priceDesc);
            contentJson.put("address", detailBean.address);
            contentJson.put("timeCost", detailBean.timeCostDesc);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return contentJson.toString();
    }

    public static String createExtMessageContentForLoc(LocBean detailBean) {

        JSONObject contentJson = new JSONObject();
        try {
            contentJson.put("id", detailBean.id);
            contentJson.put("image", detailBean.images.size() > 0 ? detailBean.images.get(0).url : "");
            contentJson.put("name", detailBean.zhName);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return contentJson.toString();
    }

    public static String createExtMessageContentForNote(TravelNoteBean noteBean) {
        JSONObject contentJson = new JSONObject();
        try {
            contentJson.put("id", noteBean.id);
            contentJson.put("image", noteBean.images.get(0).url);
            contentJson.put("name", noteBean.title);
            String[] strArray = noteBean.summary.split("\n");
            String maxLengthStr = strArray[0];
            for (String str : strArray) {
                if (str.length() > maxLengthStr.length()) {
                    maxLengthStr = str;
                }
            }
            contentJson.put("desc", maxLengthStr);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return contentJson.toString();
    }

    public static String createExtMessageContentForGuide(StrategyBean detailBean) {

        JSONObject contentJson = new JSONObject();
        try {
            contentJson.put("id", detailBean.id);
            contentJson.put("image", detailBean.images.size() > 0 ? detailBean.images.get(0).url : "");
            contentJson.put("name", detailBean.title);
            contentJson.put("timeCost", detailBean.itineraryDays + "天");
            contentJson.put("desc", detailBean.summary);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return contentJson.toString();
    }


    public interface OnDialogShareCallBack {
        void onDialogShareOk(Dialog dialog, int type, String content);

        void onDialogShareCancle(Dialog dialog, int type, String content);
    }

    public static boolean isAppRunningForeground(Context var0) {
        ActivityManager var1 = (ActivityManager) var0.getSystemService(Context.ACTIVITY_SERVICE);
        List var2 = var1.getRunningTasks(1);
        return var0.getPackageName().equalsIgnoreCase(((ActivityManager.RunningTaskInfo) var2.get(0)).baseActivity.getPackageName());
    }

    public static String toTime(int var0) {
        var0 /= 1000;
        int var1 = var0 / 60;
        boolean var2 = false;
        if (var1 >= 60) {
            int var4 = var1 / 60;
            var1 %= 60;
        }

        int var3 = var0 % 60;
        return String.format("%02d:%02d", Integer.valueOf(var1), Integer.valueOf(var3));
    }

    public static String getDataSize(long var0) {
        DecimalFormat var2 = new DecimalFormat("###.00");
        return var0 < 1024L ? var0 + "bytes" : (var0 < 1048576L ? var2.format((double) ((float) var0 / 1024.0F)) + "KB" : (var0 < 1073741824L ? var2.format((double) ((float) var0 / 1024.0F / 1024.0F)) + "MB" : (var0 < 0L ? var2.format((double) ((float) var0 / 1024.0F / 1024.0F / 1024.0F)) + "GB" : "error")));
    }

    public static void isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS) {
            System.out.println(c + "--CJK_UNIFIED_IDEOGRAPHS");
        } else if (ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS) {
            System.out.println(c + "--CJK_COMPATIBILITY_IDEOGRAPHS");
        } else if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A) {
            // CJK Unified Ideographs Extension WikipediaUnicode扩展汉字
            // CJK Unified Ideographs Extension A 中日韩统一表意文字扩展区A ; 表意文字扩充A
            // CJK Unified Ideographs Extension B 中日韩统一表意文字扩展区B
            System.out.println(c + "--CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A");
        } else if (ub == Character.UnicodeBlock.GENERAL_PUNCTUATION) {// 通用标点
            System.out.println(c + "--GENERAL_PUNCTUATION");
        } else if (ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION) {
            System.out.println(c + "--CJK_SYMBOLS_AND_PUNCTUATION");
        } else if (ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
            System.out.println(c + "--HALFWIDTH_AND_FULLWIDTH_FORMS");
        }
    }

    public static boolean isEnglish(String charaString) {
        return charaString.matches("^[a-zA-Z]*");
    }
}
