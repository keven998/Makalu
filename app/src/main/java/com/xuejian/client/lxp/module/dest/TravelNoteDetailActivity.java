package com.xuejian.client.lxp.module.dest;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;

import com.aizou.core.dialog.ToastUtil;
import com.aizou.core.http.HttpCallBack;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.BaseWebViewActivity;
import com.xuejian.client.lxp.bean.ModifyResult;
import com.xuejian.client.lxp.bean.TravelNoteBean;
import com.xuejian.client.lxp.common.api.H5Url;
import com.xuejian.client.lxp.common.api.OtherApi;
import com.xuejian.client.lxp.common.dialog.DialogManager;
import com.xuejian.client.lxp.common.gson.CommonJson;
import com.xuejian.client.lxp.common.utils.IMUtils;
import com.xuejian.client.lxp.common.widget.BlurDialogMenu.BlurDialogFragment;
import com.xuejian.client.lxp.common.widget.NumberProgressBar;

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
        mWebView = (WebView) findViewById(R.id.web_view);
        mProgressBar = (NumberProgressBar) findViewById(R.id.numberbar1);
        initWebView();
        id = getIntent().getStringExtra("id");
        noteBean = getIntent().getParcelableExtra("travelNote");
        String url;
        url = H5Url.TRAVEL_NOTE + id;
       /* if(TextUtils.isEmpty(noteBean.detailUrl)){
        }else{
            url = noteBean.detailUrl;
        }*/
        mWebView.loadUrl(id);
        findViewById(R.id.ly_title_bar_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        if (noteBean != null) {
            ImageView txtView2 = (ImageView) findViewById(R.id.tv_title_bar_right);
            txtView2.setVisibility(View.VISIBLE);
            txtView2.setImageResource(R.drawable.ic_lxp_grey);
            txtView2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showActionDialog();
                }
            });

        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        //MobclickAgent.onPageStart("page_tavel_notes_detail");
        //MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //MobclickAgent.onPageEnd("page_tavel_notes_detail");
        //MobclickAgent.onPause(this);
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
                //MobclickAgent.onEvent(TravelNoteDetailActivity.this,"navigation_item_travel_notes_lxp_share");
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
        lp.width = display.getWidth(); // 设置宽度
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
                    try {
                        DialogManager.getInstance().showLoadingDialog(getActivity());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    OtherApi.addFav(id, "travelNote", new HttpCallBack<String>() {
                        @Override
                        public void doSuccess(String result, String method) {
                            DialogManager.getInstance().dissMissLoadingDialog();
                            CommonJson<ModifyResult> deleteResult = CommonJson.fromJson(result, ModifyResult.class);
                            if (deleteResult.code == 0) {
                                dismiss();
//                                ToastUtil.getInstance(getActivity()).showToast("收藏成功");
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

                        @Override
                        public void doFailure(Exception error, String msg, String method, int code) {

                        }
                    });

                }
            });
            return connectionDialog;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IMUtils.onShareResult(mContext, noteBean, requestCode, resultCode, data, null);
    }
}
