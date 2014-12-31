package com.aizou.peachtravel.module.dest;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;

import com.aizou.core.dialog.ToastUtil;
import com.aizou.core.http.HttpCallBack;
import com.aizou.peachtravel.R;
import com.aizou.peachtravel.base.BaseWebViewActivity;
import com.aizou.peachtravel.bean.ModifyResult;
import com.aizou.peachtravel.bean.TravelNoteBean;
import com.aizou.peachtravel.common.api.H5Url;
import com.aizou.peachtravel.common.api.OtherApi;
import com.aizou.peachtravel.common.dialog.DialogManager;
import com.aizou.peachtravel.common.gson.CommonJson;
import com.aizou.peachtravel.common.utils.IMUtils;
import com.aizou.peachtravel.common.widget.BlurDialogMenu.BlurDialogFragment;
import com.aizou.peachtravel.common.widget.BlurDialogMenu.SupportBlurDialogFragment;
import com.aizou.peachtravel.common.widget.TitleHeaderBar;
import com.aizou.peachtravel.module.toolbox.im.AddContactActivity;
import com.aizou.peachtravel.module.toolbox.im.PickContactsWithCheckboxActivity;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Rjm on 2014/12/13.
 */
public class TravelNoteDetailActivity extends BaseWebViewActivity {

    @InjectView(R.id.ly_header_bar_title_wrap)
    TitleHeaderBar titleBar;
    TravelNoteBean noteBean;
    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview_with_titlebar);
        ButterKnife.inject(this);
        mWebView = (WebView) findViewById(R.id.web_view);
        initWebView();
        titleBar.getTitleTextView().setText("游记详情");
        id = getIntent().getStringExtra("id");
        noteBean = getIntent().getParcelableExtra("travelNote");
        mWebView.loadUrl(H5Url.TRAVEL_NOTE+id);
        titleBar.enableBackKey(true);
        if(noteBean!=null){
            titleBar.setRightViewImageRes(R.drawable.ic_more);
            titleBar.setRightOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MoreMenu fragment = new MoreMenu();
                    Bundle args = new Bundle();
                    args.putInt(SupportBlurDialogFragment.BUNDLE_KEY_BLUR_RADIUS, 5);
                    args.putFloat(SupportBlurDialogFragment.BUNDLE_KEY_DOWN_SCALE_FACTOR, 6);
                    args.putString("id",id);
                    fragment.setArguments(args);
                    fragment.show(getSupportFragmentManager(), "more_menu");
                }
            });
        }

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IMUtils.onShareResult(mContext,noteBean,requestCode,resultCode,data,null);
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
                                ToastUtil.getInstance(getActivity()).showToast(getResources().getString(R.string.request_server_failed));
                            }

                        }

                        @Override
                        public void doFailure(Exception error, String msg, String method) {
                            DialogManager.getInstance().dissMissLoadingDialog();
                            ToastUtil.getInstance(getActivity()).showToast(getResources().getString(R.string.request_network_failed));
                        }
                    });

                }
            });
            return connectionDialog;
        }
    }
}
