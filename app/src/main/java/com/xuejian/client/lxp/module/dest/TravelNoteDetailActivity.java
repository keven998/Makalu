package com.xuejian.client.lxp.module.dest;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ImageView;

import com.aizou.core.dialog.ToastUtil;
import com.aizou.core.http.HttpCallBack;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.BaseWebViewActivity;
import com.xuejian.client.lxp.bean.ModifyResult;
import com.xuejian.client.lxp.bean.PeachUser;
import com.xuejian.client.lxp.bean.TravelNoteBean;
import com.xuejian.client.lxp.common.account.AccountManager;
import com.xuejian.client.lxp.common.api.H5Url;
import com.xuejian.client.lxp.common.api.OtherApi;
import com.xuejian.client.lxp.common.dialog.DialogManager;
import com.xuejian.client.lxp.common.gson.CommonJson;
import com.xuejian.client.lxp.common.utils.IMUtils;
import com.xuejian.client.lxp.common.widget.BlurDialogMenu.BlurDialogFragment;
import com.xuejian.client.lxp.common.widget.NumberProgressBar;
import com.xuejian.client.lxp.db.userDB.User;
import com.xuejian.client.lxp.module.my.LoginActivity;

import butterknife.ButterKnife;

/**
 * Created by Rjm on 2014/12/13.
 */
public class TravelNoteDetailActivity extends BaseWebViewActivity {

    TravelNoteBean noteBean;
    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview_with_titlebar);
        ButterKnife.inject(this);
        mWebView = (WebView) findViewById(R.id.web_view);
        mProgressBar = (NumberProgressBar) findViewById(R.id.numberbar1);
        initWebView();
        id = getIntent().getStringExtra("id");
        noteBean = getIntent().getParcelableExtra("travelNote");
        String url;
        if(TextUtils.isEmpty(noteBean.detailUrl)){
            url = H5Url.TRAVEL_NOTE+id;
        }else{
            url = noteBean.detailUrl;
        }

        mWebView.loadUrl(url);
        findViewById(R.id.ly_title_bar_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        if (noteBean != null) {
            ImageView txtView2 = (ImageView) findViewById(R.id.tv_title_bar_right);
            txtView2.setVisibility(View.VISIBLE);
            txtView2.setImageResource(R.drawable.ic_note_share_selector);
            txtView2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showActionDialog();
                }
            });

            CheckedTextView txtView = (CheckedTextView) findViewById(R.id.tv_title_bar_right_1);
            txtView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.checker_title_ic_favorite, 0);
            txtView.setVisibility(View.VISIBLE);
            txtView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    User user = AccountManager.getInstance().getLoginAccount(TravelNoteDetailActivity.this);
                    if (user != null ) { //&& !TextUtils.isEmpty(user.easemobUser)
                        favorite((CheckedTextView) v);
                    } else {
                        ToastUtil.getInstance(TravelNoteDetailActivity.this).showToast("请先登录");
                        Intent intent = new Intent(TravelNoteDetailActivity.this, LoginActivity.class);
                        startActivityForResult(intent, 11);
                    }

                }
            });
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 11) {
                CheckedTextView txtView = (CheckedTextView) findViewById(R.id.tv_title_bar_right_1);
                favorite(txtView);
            } else {
                IMUtils.onShareResult(mContext, noteBean, requestCode, resultCode, data, null);
            }
        }
    }

    private void favorite(final CheckedTextView ct) {
        final boolean isFav = ct.isChecked();
        ct.setChecked(!isFav);
        if (!isFav) {
            OtherApi.addFav(id, "travelNote", new HttpCallBack<String>() {
                @Override
                public void doSucess(String result, String method) {
                    CommonJson<ModifyResult> deleteResult = CommonJson.fromJson(result, ModifyResult.class);
                    if (deleteResult.code == 0 || deleteResult.code == getResources().getInteger(R.integer.response_favorite_exist)) {
                        ToastUtil.getInstance(TravelNoteDetailActivity.this).showToast("已收藏");
                    } else {
                        //                    ToastUtil.getInstance(getActivity()).showToast(getResources().getString(R.string.request_server_failed));
                        ct.setChecked(isFav);
                    }

                }

                @Override
                public void doFailure(Exception error, String msg, String method) {
                    if (!isFinishing()) {
                        ct.setChecked(isFav);
                        ToastUtil.getInstance(TravelNoteDetailActivity.this).showToast(getResources().getString(R.string.request_network_failed));
                    }
                }
            });
        } else {
            OtherApi.deleteFav(id, new HttpCallBack<String>() {
                @Override
                public void doSucess(String result, String method) {
                    CommonJson<ModifyResult> deleteResult = CommonJson.fromJson(result, ModifyResult.class);
                    if (deleteResult.code == 0) {
//                        ToastUtil.getInstance(TravelNoteDetailActivity.this).showToast("收藏取消");
                    } else {
                        ct.setChecked(isFav);
                    }

                }

                @Override
                public void doFailure(Exception error, String msg, String method) {
                    if (!isFinishing()) {
                        ct.setChecked(isFav);
                        ToastUtil.getInstance(TravelNoteDetailActivity.this).showToast(getResources().getString(R.string.request_network_failed));
                    }
                }
            });
        }
    }

    private void showActionDialog() {
        final Activity act = this;
        final AlertDialog dialog = new AlertDialog.Builder(act).create();
        View contentView = View.inflate(act, R.layout.dialog_home_confirm_action, null);
        Button btn = (Button) contentView.findViewById(R.id.btn_go_plan);
        btn.setText("Talk分享");
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IMUtils.onClickImShare(act);
                dialog.dismiss();
            }
        });
        contentView.findViewById(R.id.btn_cancle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
        WindowManager windowManager = act.getWindowManager();
        Window window = dialog.getWindow();
        window.setContentView(contentView);
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = (int) (display.getWidth()); // 设置宽度
        window.setAttributes(lp);
        window.setGravity(Gravity.BOTTOM); // 此处可以设置dialog显示的位置
        window.setWindowAnimations(R.style.SelectPicDialog); // 添加动画
    }

    public static class MoreMenu extends BlurDialogFragment {
        String id;
        TravelNoteBean noteBean;
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            id = (String) getArguments().get("id");
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            Dialog connectionDialog = new Dialog(getActivity(), R.style.TransparentDialog);
            View customView = getActivity().getLayoutInflater().inflate(R.layout.menu_dialog_travelnote, null);
            connectionDialog.setContentView(customView);
//            customView.findViewById(R.id.dialog_frame).setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    dismiss();
//                }
//            });
            customView.findViewById(R.id.im_share).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    IMUtils.onClickImShare(getActivity());
                    dismiss();
                }
            });

            customView.findViewById(R.id.fav).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DialogManager.getInstance().showLoadingDialog(getActivity());
                    OtherApi.addFav(id, "travelNote", new HttpCallBack<String>() {
                        @Override
                        public void doSucess(String result, String method) {
                            DialogManager.getInstance().dissMissLoadingDialog();
                            CommonJson<ModifyResult> deleteResult = CommonJson.fromJson(result, ModifyResult.class);
                            if (deleteResult.code == 0) {
                                dismiss();
                                ToastUtil.getInstance(getActivity()).showToast("收藏成功");
                            } else {
                                if (isAdded())
                                    ToastUtil.getInstance(getActivity()).showToast(getResources().getString(R.string.request_server_failed));
                            }

                        }

                        @Override
                        public void doFailure(Exception error, String msg, String method) {
                            DialogManager.getInstance().dissMissLoadingDialog();
                            if (isAdded())
                                ToastUtil.getInstance(getActivity()).showToast(getResources().getString(R.string.request_network_failed));
                        }
                    });

                }
            });
            return connectionDialog;
        }
    }
}
