package com.xuejian.client.lxp.common.dialog;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.view.Gravity;
import android.view.WindowManager;

import com.xuejian.client.lxp.R;


/**
 * 描述:对话框管理类， 提供普通消息提示框（一个与两个按钮） 可自定义对话框布局
 */
public class DialogManager {

    /**
     * 提示对话框实体
     */
    private PeachMessageDialog mMessageDialog;
    /**
     * 模态Loading对话框
     */
    private CustomLoadingDialog mLoadingDialog;
    /**
     * 非模态Loading对话框
     */
    private ModelessLoadingDialog mModelessLoadingDialog;

    /**
     * 进度对话框
     */
    private CustomProgressDialog mProgressDialog;

    private DialogManager() {
    }

    private static DialogManager mDialogManager = null;

//    public static DialogManager getInstance() {
//        if (mDialogManager == null) {
//            mDialogManager = new DialogManager();
//        }
//        return mDialogManager;
//    }
    public static DialogManager getInstance() {
        if (mDialogManager == null) {
            synchronized (DialogManager.class) {
                if (mDialogManager == null) {
                    mDialogManager = new DialogManager();
                }
            }
        }
        return mDialogManager;
    }

    /**
     * 描述: 定义弹出框的宽高，弹出对话框
     */
    public void showDialog() {
        if (mMessageDialog != null && mMessageDialog.isShowing()) {
            mMessageDialog.dismiss();
        }
        WindowManager.LayoutParams lp = mMessageDialog.getWindow()
                .getAttributes();
//		lp.width = LayoutValue.SCREEN_WIDTH * 2 / 3;
//		lp.height = LayoutValue.SCREEN_HEIGHT * 1 / 3;
        lp.gravity = Gravity.CENTER;
        mMessageDialog.getWindow().setAttributes(lp);
        mMessageDialog.show();
        mMessageDialog.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }


    /**
     * 描述:显示通信框
     *
     * @param context 上下文
     */
    public CustomLoadingDialog showLoadingDialog(Context context, String message) {
        mLoadingDialog = createLoadingDialog(context, message, null);
        return mLoadingDialog;
    }

    /**
     * 描述:显示通信框
     *
     * @param context 上下文
     */
    public CustomLoadingDialog showLoadingDialog(Context context) {
        mLoadingDialog = createLoadingDialog(context, null, null);
        return mLoadingDialog;
    }

    /**
     * 描述:隐藏通信框
     */
    public void dissMissLoadingDialog() {
        if (mLoadingDialog != null&&mLoadingDialog.getOwnerActivity()!=null) {
            if (mLoadingDialog.isShowing())
                mLoadingDialog.dismiss();

        }
        mLoadingDialog = null;
    }

    /**
     * 描述:显示非模态对话框
     *
     * @param context 上下文
     */
    public CustomLoadingDialog showModelessLoadingDialog(Context context) {
        mModelessLoadingDialog = createModelessLoadingDialog(context, null, null);
        WindowManager.LayoutParams lp = mModelessLoadingDialog.getWindow()
                .getAttributes();
        lp.flags = lp.flags | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
        mModelessLoadingDialog.getWindow().setAttributes(lp);
        return mLoadingDialog;
    }

    /**
     * 描述:隐藏通信框
     */
    public void dissMissModelessLoadingDialog() {
        if (mModelessLoadingDialog != null) {
             if (mModelessLoadingDialog.isShowing())
                mModelessLoadingDialog.dismiss();

        }
        mModelessLoadingDialog = null;

    }

    /**
     * 描述:显示通信框
     *
     * @param context 上下文
     */
    public CustomProgressDialog showProgressDialog(Context context) {
        mProgressDialog = createProgressDialog(context);
        return mProgressDialog;
    }


    /**
     * 描述:隐藏通信框
     */
    public void dissMissProgressDialog() {
        if (mProgressDialog != null) {
            if (mProgressDialog.isShowing())
                mProgressDialog.dismiss();

        }
        mProgressDialog = null;

    }


    /**
     * 创建通讯提示框
     */
    private CustomLoadingDialog createLoadingDialog(Context con, String message, OnCancelListener cancleListener) {
        try {
            CustomLoadingDialog dlg = new CustomLoadingDialog(con, message);
            dlg.show();
            dlg.setCancelable(true);
            return dlg;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 创建通讯提示框
     */
    private ModelessLoadingDialog createModelessLoadingDialog(final Context con, String message, OnCancelListener cancleListener) {
        ModelessLoadingDialog dlg = new ModelessLoadingDialog(con, message);
        dlg.show();
        dlg.setCancelable(true);
        dlg.setOnCancelListener(new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (con instanceof Activity) {
                    ((Activity) con).finish();
                }
            }
        });
        return dlg;
    }

    /**
     * 创建进度框
     */
    private CustomProgressDialog createProgressDialog(Context con) {
        CustomProgressDialog dlg = new CustomProgressDialog(con);
        dlg.show();
        dlg.setMax(100);
        dlg.setTextColor(con.getResources().getColor(R.color.base_color_white));
        dlg.setTextSize(14);
        dlg.setProgress(0);
        dlg.setCancelable(true);
        return dlg;
    }
}
