package com.aizou.peachtravel.common.dialog;

import android.content.Context;
import android.content.DialogInterface.OnCancelListener;
import android.view.Gravity;
import android.view.WindowManager;


/**
 * 描述:对话框管理类， 提供普通消息提示框（一个与两个按钮） 可自定义对话框布局
 */
public class DialogManager {

    /**
     * 提示对话框实体
     */
    private PeachMessageDialog mMessageDialog;
    /**
     * Loading对话框
     */
    private CustomProgressDialog mLoadingDialog;

    /**
     * 进度对话框
     */
    private CustomDialog mProgressDialog;

    private DialogManager() {
    }

    private static DialogManager mDialogManager = null;

    public static DialogManager getInstance() {
        if (mDialogManager == null) {
            mDialogManager = new DialogManager();
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
    public void showLoadingDialog(Context context, String message) {
        mLoadingDialog = createLoadingDialog(context, message, null);
    }

    /**
     * 描述:显示通信框
     *
     * @param context 上下文
     */
    public void showLoadingDialog(Context context) {
        mLoadingDialog = createLoadingDialog(context, null, null);
    }

    /**
     * 描述:隐藏通信框
     */
    public void dissMissLoadingDialog() {
        if (mLoadingDialog != null) {
            if (mLoadingDialog.isShowing())
                mLoadingDialog.dismiss();

        }
        mLoadingDialog =null;

    }

    /**
     * 通讯提示框
     */
    public CustomProgressDialog createLoadingDialog(final Context con, String message, OnCancelListener cancleListener) {
        CustomProgressDialog dlg = new CustomProgressDialog(con,message);
        dlg.show();
        dlg.setCancelable(true);
        return dlg;
    }


}
