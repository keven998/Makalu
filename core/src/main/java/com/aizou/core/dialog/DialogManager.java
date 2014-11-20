package com.aizou.core.dialog;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.aizou.core.R;
import com.aizou.core.constant.LayoutValue;
import com.aizou.core.http.HttpManager;


/**
 * 描述:对话框管理类， 提供普通消息提示框（一个与两个按钮） 可自定义对话框布局
 */
public class DialogManager {

    /**
     * 对话框实体
     */
    private CustomDialog mCustomDialog;
    /**
     * 通信框框实体
     */
    private CustomDialog mProgressDialog;

    private DialogManager() {
    }

    private static DialogManager mdialogDialogManager = null;

    public static DialogManager getInstance() {
        if (mdialogDialogManager == null) {
            mdialogDialogManager = new DialogManager();
        }
        return mdialogDialogManager;
    }

    /**
     * 描述: 普通信息消息框（单个按钮）
     *
     * @param context         上下文
     * @param message         要展示的信息
     * @param onclickListener 按钮监听事件 无事件传null
     */
    public void showMessageDialogWithSingleButton(Context context,
                                                  String message, OnClickListener onclickListener) {
        showMessageDialogWithSingleButton(context, null, message, onclickListener);
    }

    /**
     * 描述: 普通信息消息框（单个按钮）
     *
     * @param context         上下文
     * @param title           标题
     * @param message         要展示的信息
     * @param onclickListener 按钮监听事件 无事件传null
     */
    public void showMessageDialogWithSingleButton(Context context, String title,
                                                  String message, OnClickListener onclickListener) {

        if (mCustomDialog != null && mCustomDialog.isShowing()) {
            mCustomDialog.dismiss();
        }
        mCustomDialog = new CustomDialog(context, R.style.Theme_Dialog);
        View contentView = initDialogWithSingleButtonView(context, title, message,
                onclickListener);
        mCustomDialog.setCancelable(false);
        mCustomDialog.setContentView(contentView);
        showDialog();
    }

    /**
     * 描述:隐藏对话框
     */
    public void dissMissCustomDialog() {
        if (mCustomDialog != null & mCustomDialog.isShowing()) {
            mCustomDialog.dismiss();
        }
    }


    /**
     * 描述:展示自定义的对话框
     *
     * @param context     上下文对象
     * @param contentView 对话框视图
     */
    public void showCustomDialog(Context context,
                                 View contentView) {
        if (mCustomDialog != null && mCustomDialog.isShowing()) {
            mCustomDialog.dismiss();
        }
        mCustomDialog = new CustomDialog(context, R.style.Theme_Dialog);
        mCustomDialog.setCancelable(false);
        mCustomDialog.setContentView(contentView);
        showDialog();
    }

    /**
     * 描述:展示自定义的对话框
     *
     * @param context     上下文对象
     * @param contentView 对话框视图
     */
    public void showCustomDialog(Context context,
                                 View contentView, boolean cancelable) {
        if (mCustomDialog != null && mCustomDialog.isShowing()) {
            mCustomDialog.dismiss();
        }
        mCustomDialog = new CustomDialog(context, R.style.Theme_Dialog);
        mCustomDialog.setCancelable(cancelable);
        mCustomDialog.setContentView(contentView);
        showDialog();
    }

    /**
     * 描述: 普通信息消息框（两个按钮  自定义）
     *  @param context         上下文
     * @param comfirmStr
     * @param cancleStr
     * @param title           标题
     * @param message         要展示的信息
     * @param onclickListener 按钮监听事件 无事件传null
     */
    public void showMessageDialogWithDoubleButtonSelf(Context context, String comfirmStr, String cancleStr, String title,
                                                      String message, OnClickListener onclickListener) {
        if (mCustomDialog != null && mCustomDialog.isShowing()) {
            mCustomDialog.dismiss();
        }
        mCustomDialog = new CustomDialog(context, R.style.Theme_Dialog);
        View contentView = initDialogWithDoubleButtonViewSelf(context, comfirmStr,cancleStr,title, message,
                onclickListener);
        mCustomDialog.setCancelable(false);
        mCustomDialog.setContentView(contentView);
        showDialog();

    }

    /**
     * 描述: 普通信息消息框（两个按钮  确定 取消）
     *
     * @param title           标题
     * @param context         上下文
     * @param message         要展示的信息
     * @param onclickListener 按钮监听事件 无事件传null
     */
    public void showMessageDialogWithDoubleButton(Context context, String title,
                                                  String message, OnClickListener onclickListener) {
        if (mCustomDialog != null && mCustomDialog.isShowing()) {
            mCustomDialog.dismiss();
        }
        mCustomDialog = new CustomDialog(context, R.style.Theme_Dialog);
        View contentView = initDialogWithDoubleButtonView(context, title,message,
                onclickListener);
        mCustomDialog.setCancelable(false);
        mCustomDialog.setContentView(contentView);
        showDialog();

    }

    /**
     * 描述: 普通信息消息框（两个按钮  确定 取消）
     *
     * @param context         上下文
     * @param message         要展示的信息
     * @param onclickListener 按钮监听事件 无事件传null
     */
    public void showMessageDialogWithDoubleButton(Context context,
                                                  String message, OnClickListener onclickListener) {
        showMessageDialogWithDoubleButton(context, null, message, onclickListener);
    }

    /**
     * 描述: 定义弹出框的宽高，弹出对话框
     */
    public void showDialog() {
        if (mCustomDialog != null && mCustomDialog.isShowing()) {
            mCustomDialog.dismiss();
        }
        WindowManager.LayoutParams lp = mCustomDialog.getWindow()
                .getAttributes();
//		lp.width = LayoutValue.SCREEN_WIDTH * 2 / 3;
//		lp.height = LayoutValue.SCREEN_HEIGHT * 1 / 3;
        lp.gravity = Gravity.CENTER;
        mCustomDialog.getWindow().setAttributes(lp);
        mCustomDialog.show();
        mCustomDialog.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }

    /**
     * 描述:初始化提示框 没有提示标题 只有一个确定按钮
     *
     * @param context         上下文
     * @param message         提示信息
     * @param onclickListener 确定按钮监听
     * @return 初始化后的布局
     */
    public View initDialogWithSingleButtonView(Context context, String title, String message,
                                               final OnClickListener onclickListener) {
        View contentView = LayoutInflater.from(context).inflate(
                R.layout.comm_info_message_dialog, null);
        TextView titleTv = (TextView) contentView.findViewById(R.id.tv_mention_title);
        if (TextUtils.isEmpty(title)) {
            titleTv.setVisibility(View.GONE);
        } else {
            titleTv.setText(title);
            titleTv.setVisibility(View.VISIBLE);
        }
        Button confirmBtn = (Button) contentView.findViewById(R.id.btn_confirm_on_dialog);

        OnClickListener onClick = new OnClickListener() {

            @Override
            public void onClick(View view) {
                mCustomDialog.dismiss();
                if (onclickListener != null) {
                    onclickListener.onClick(view);
                }
            }
        };
        confirmBtn.setOnClickListener(onClick);
        TextView tvMentionMsg = (TextView) contentView
                .findViewById(R.id.tv_mention_msg);
        tvMentionMsg.setText(message);
        return contentView;

    }

    /**
     * 描述:初始化提示框 没有提示标题 只有一个确定按钮
     *
     * @param context         上下文
     * @param message         提示信息
     * @param onclickListener 确定按钮监听
     * @return 初始化后的布局
     */
    public View initDialogWithSingleButtonView(Context context, String message,
                                               final OnClickListener onclickListener) {
        View contentView = initDialogWithSingleButtonView(context, null, message, onclickListener);
        return contentView;

    }

    /**
     * 描述:初始化提示框 没有提示标题 包含两个按钮(确定 取消)
     *
     * @param context         上下文
     * @param message         提示信息
     * @param onclickListener 按钮监听 (确定按钮ID btn_confirm  取消按钮ID btn_cancle)
     * @return 初始化后的布局
     */
    public View initDialogWithDoubleButtonView(Context context, String message,
                                               final OnClickListener onclickListener) {
        View contentView = initDialogWithDoubleButtonView(context, null, message, onclickListener);
        return contentView;

    }
    public View initDialogWithDoubleButtonViewSelf(Context context,
                                                   String confirmText,String cancelText,String title, String message,
                                               final OnClickListener onclickListener) {
        View contentView = LayoutInflater.from(context).inflate(
                R.layout.comm_info_message_dialog, null);
        TextView titleTv = (TextView) contentView.findViewById(R.id.tv_mention_title);
        if (TextUtils.isEmpty(title)) {
            titleTv.setVisibility(View.GONE);
        } else {
            titleTv.setText(title);
            titleTv.setVisibility(View.VISIBLE);
        }
        Button confirmBtn = (Button) contentView.findViewById(R.id.btn_confirm_on_dialog);
        Button cancelBtn = (Button) contentView.findViewById(R.id.btn_cancle_on_dialog);
        OnClickListener onClick = new OnClickListener() {

            @Override
            public void onClick(View view) {
                mCustomDialog.dismiss();
                if (onclickListener != null) {
                    onclickListener.onClick(view);
                }
            }
        };
        confirmBtn.setText(confirmText);
        cancelBtn.setText(cancelText);
        confirmBtn.setOnClickListener(onClick);
        cancelBtn.setOnClickListener(onClick);
        contentView.findViewById(R.id.divider_on_dialog).setVisibility(View.VISIBLE);
        confirmBtn.setVisibility(View.VISIBLE);
        cancelBtn.setVisibility(View.VISIBLE);
        TextView tvMentionMsg = (TextView) contentView
                .findViewById(R.id.tv_mention_msg);
        tvMentionMsg.setText(message);
        return contentView;

    }

    public View initDialogWithDoubleButtonView(Context context, String title, String message,
                                               final OnClickListener onclickListener) {
        View contentView = LayoutInflater.from(context).inflate(
                R.layout.comm_info_message_dialog, null);
        TextView titleTv = (TextView) contentView.findViewById(R.id.tv_mention_title);
        if (TextUtils.isEmpty(title)) {
            titleTv.setVisibility(View.GONE);
        } else {
            titleTv.setText(title);
            titleTv.setVisibility(View.VISIBLE);
        }
        Button confirmBtn = (Button) contentView.findViewById(R.id.btn_confirm_on_dialog);
        Button cancelBtn = (Button) contentView.findViewById(R.id.btn_cancle_on_dialog);
        OnClickListener onClick = new OnClickListener() {

            @Override
            public void onClick(View view) {
                mCustomDialog.dismiss();
                if (onclickListener != null) {
                    onclickListener.onClick(view);
                }
            }
        };
        confirmBtn.setOnClickListener(onClick);
        cancelBtn.setOnClickListener(onClick);
        contentView.findViewById(R.id.divider_on_dialog).setVisibility(View.VISIBLE);
        confirmBtn.setVisibility(View.VISIBLE);
        cancelBtn.setVisibility(View.VISIBLE);
        TextView tvMentionMsg = (TextView) contentView
                .findViewById(R.id.tv_mention_msg);
        tvMentionMsg.setText(message);
        return contentView;

    }

    /**
     * 描述:显示通信框
     *
     * @param context 上下文
     */
    public void showProgressDialog(Context context,String message) {
        mProgressDialog = createProgressDialog(context,message,null);
    }

    /**
     * 描述:显示通信框
     *
     * @param context 上下文
     */
    public void showProgressDialog(Activity context) {
        mProgressDialog = createProgressDialog(context,null,null);
    }

    /**
     * 描述:隐藏通信框
     */
    public void dissMissProgressDialog() {
        if (mProgressDialog != null) {
            if (mProgressDialog.isShowing())
                mProgressDialog.dismiss();

        }
        mProgressDialog=null;

    }

    /**
     * 通讯提示框
     */
    public CustomDialog createProgressDialog(final Context con,String message,OnCancelListener cancleListener) {
        CustomDialog dlg = new CustomDialog(con, R.style.Theme_Dialog);
        dlg.show();
        dlg.setCancelable(true);
        Window window = dlg.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.dimAmount = 0f;  //设置背景不变暗
        window.setAttributes(params);
        LayoutInflater factory = LayoutInflater.from(con);
        // 加载progress_dialog为对话框的布局xml
        View view = factory.inflate(R.layout.progress_dialog, null);
        TextView messageTv = (TextView) view.findViewById(R.id.tvmessage);
        if(!TextUtils.isEmpty(message)){
            messageTv.setText(message);
        }
//		Button btn = (Button) view.findViewById(R.id.btnClose);
//		btn.setOnClickListener(new android.view.View.OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				//TODO 关闭通信
//				HttpManager.stopCurrentRequest();
//				if(mProgressDialog!=null & mProgressDialog.isShowing()){
//					mProgressDialog.dismiss();
//					
//				}
//			}
//		});

        dlg.setOnCancelListener(cancleListener);
        dlg.getWindow().setContentView(view);
        WindowManager.LayoutParams lp = dlg.getWindow().getAttributes();
        lp.width = LayoutValue.SCREEN_WIDTH * 2 / 4;
        lp.height = LayoutValue.SCREEN_WIDTH / 3;
        dlg.getWindow().setAttributes(lp);
        return dlg;
    }

    public CustomDialog getmCustomDialog() {
        return mCustomDialog;
    }

    public void setmCustomDialog(CustomDialog mCustomDialog) {
        this.mCustomDialog = mCustomDialog;
    }

}
