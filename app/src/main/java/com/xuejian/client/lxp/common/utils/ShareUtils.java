package com.xuejian.client.lxp.common.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.aizou.core.dialog.ToastUtil;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners.SnsPostListener;
import com.umeng.socialize.controller.listener.SocializeListeners.UMAuthListener;
import com.umeng.socialize.exception.SocializeException;
import com.umeng.socialize.media.RenrenShareContent;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.utils.OauthHelper;
import com.umeng.socialize.weixin.controller.UMWXHandler;
import com.umeng.socialize.weixin.media.CircleShareContent;
import com.umeng.socialize.weixin.media.WeiXinShareContent;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.bean.StrategyBean;

public class ShareUtils {
    public class PlatfromSetting {
        public final static String WX_APPID = "wx86048e56adaf7486";
        public final static String WX_APPSECRET = "d5408e689b82c0335a728cc8bd1b3c2e";

        public final static String QQ_APPID = "1104433490";
        public final static String QQ_APPKEY = "9OCrfdw4vb31gHOU";

    }

    public static final String downloadUrl = "http://www.lvxingpai.com/download/lvxingpai.apk";

    public static void showSelectPlatformDialog(final Activity act, final StrategyBean strategy) {
        final AlertDialog dialog = new AlertDialog.Builder(act).create();
        View contentView = View
                .inflate(act, R.layout.view_share_bar, null);
//        TextView talkTv = (TextView) contentView.findViewById(R.id.tv_tao_talk);
        TextView wxcircleTv = (TextView) contentView
                .findViewById(R.id.tv_wxcircle);
        TextView wechatTv = (TextView) contentView.findViewById(R.id.tv_wechat);
        TextView qzoneTv = (TextView) contentView.findViewById(R.id.tv_qzone);
        TextView doubanTv = (TextView) contentView.findViewById(R.id.tv_douban);
        TextView sinaTv = (TextView) contentView.findViewById(R.id.tv_sina);
        TextView qqTv = (TextView) contentView.findViewById(R.id.tv_qq);
        TextView cancleIv = (TextView) contentView
                .findViewById(R.id.tv_share_cancel);
//        talkTv.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//                IMUtils.onClickImShare(act);
//            }
//        });
        wxcircleTv.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
                shareRoute(SHARE_MEDIA.WEIXIN_CIRCLE, act, strategy);


            }
        });
        wechatTv.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
                shareRoute(SHARE_MEDIA.WEIXIN, act, strategy);

            }
        });
        qzoneTv.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
                shareRoute(SHARE_MEDIA.QZONE, act, strategy);

            }
        });
        doubanTv.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
                shareRoute(SHARE_MEDIA.DOUBAN, act, strategy);

            }
        });
        sinaTv.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
                shareRoute(SHARE_MEDIA.SINA, act, strategy);

            }
        });
        qqTv.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
                shareRoute(SHARE_MEDIA.QQ, act, strategy);
            }
        });
        cancleIv.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();

            }
        });
        // dialog.setView(contentView);
        // dialog.setContentView(contentView);
        dialog.show();
        WindowManager windowManager = act.getWindowManager();
        Window window = dialog.getWindow();
        window.setContentView(contentView);
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = display.getWidth(); // 设置宽度
        // lp.horizontalMargin=20;
        window.setAttributes(lp);
        window.setGravity(Gravity.BOTTOM); // 此处可以设置dialog显示的位置
        window.setWindowAnimations(R.style.SelectPicDialog); // 添加动画
    }

    public static void shareAppToWx(final Activity act, String content) {
        final UMSocialService mController = UMServiceFactory
                .getUMSocialService("com.umeng.share");
        // 设置分享内容
        mController.getConfig().closeToast();
        UMWXHandler wxHandler = new UMWXHandler(act, PlatfromSetting.WX_APPID, PlatfromSetting.WX_APPSECRET);
        wxHandler.addToSocialSDK();
        WeiXinShareContent circleMedia = new WeiXinShareContent();
        circleMedia.setTitle("推荐\"旅行派\"给你。");
        if (TextUtils.isEmpty(content)) {
            circleMedia.setShareContent("可以向达人寻求帮助的旅行应用");
        } else {
            circleMedia.setShareContent(content);
        }
        circleMedia.setShareImage(new UMImage(act, R.drawable.ic_taozi_share));
        circleMedia.setTargetUrl(downloadUrl);
        mController.setShareMedia(circleMedia);
        mController.postShare(act, SHARE_MEDIA.WEIXIN, new SnsPostListener() {
            @Override
            public void onStart() {
//					Toast.makeText(act, "开始分享.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onComplete(SHARE_MEDIA platform, int eCode,
                                   SocializeEntity entity) {
                if (eCode == 200) {
//                    Toast.makeText(act, "分享成功.",
//                            Toast.LENGTH_SHORT).show();
                    ToastUtil.getInstance(act).showToast("已分享");
                } else {

                }
            }

        });
    }

    public static UMSocialService shareRoute(final SHARE_MEDIA platform,
                                             final Activity act, StrategyBean strategyBean) {
        // 首先在您的Activity中添加如下成员变量
        final UMSocialService mController = UMServiceFactory
                .getUMSocialService("com.umeng.share");
        // 设置分享内容
        UMImage umImage;
        if (strategyBean.images != null
                && strategyBean.images.size() > 0) {
            umImage = new UMImage(act, strategyBean.images.get(0).url);
        } else {
            umImage = new UMImage(act, R.drawable.ic_taozi_share);
        }
        mController.getConfig().closeToast();
        mController.setShareMedia(umImage);
        String shareUrl = strategyBean.detailUrl;
        String shareTitle = "分享我的旅行计划";
        String shareContent = "我的 《" + strategyBean.title + "》 来了，亲们快快来围观~ ";

        mController.setShareContent(shareContent);

        if (SHARE_MEDIA.WEIXIN_CIRCLE == platform) {
            // 添加微信平台
            UMWXHandler wxHandler = new UMWXHandler(act, PlatfromSetting.WX_APPID, PlatfromSetting.WX_APPSECRET);
            wxHandler.setToCircle(true);
            wxHandler.addToSocialSDK();
            CircleShareContent circleMedia = new CircleShareContent();
            circleMedia.setShareContent(shareContent);
            circleMedia.setTitle(shareContent);
            // 设置分享图片, 参数2为图片的url地址
            circleMedia.setShareImage(umImage);
            circleMedia.setTargetUrl(shareUrl);
            mController.setShareMedia(circleMedia);
            mController.postShare(act, platform, new SnsPostListener() {
                @Override
                public void onStart() {
//					Toast.makeText(act, "开始分享.", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onComplete(SHARE_MEDIA platform, int eCode,
                                       SocializeEntity entity) {
                    if (eCode == 200) {
                        Toast.makeText(act, "分享成功.",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        // String eMsg = "";
                        // if (eCode == -101) {
                        // eMsg = "没有授权";
                        // }
                        // Toast.makeText(act, "分享失败[" + eCode + "] " + eMsg,
                        // Toast.LENGTH_SHORT).show();
                    }
                }

            });
            // wxHandler.setTitle("友盟社会化组件还不错-WXHandler...");
        } else if (SHARE_MEDIA.WEIXIN == platform) {
            UMWXHandler wxHandler = new UMWXHandler(act, PlatfromSetting.WX_APPID, PlatfromSetting.WX_APPSECRET);
            wxHandler.addToSocialSDK();
            WeiXinShareContent circleMedia = new WeiXinShareContent();
            circleMedia.setShareContent(shareContent);
            circleMedia.setTitle(shareContent);
            circleMedia.setShareImage(umImage);
            circleMedia.setTargetUrl(shareUrl);
            mController.setShareMedia(circleMedia);
            mController.postShare(act, platform, new SnsPostListener() {
                @Override
                public void onStart() {
//					Toast.makeText(act, "开始分享.", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onComplete(SHARE_MEDIA platform, int eCode,
                                       SocializeEntity entity) {
                    if (eCode == 200) {
                        Toast.makeText(act, "分享成功.",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        // String eMsg = "";
                        // if (eCode == -101) {
                        // eMsg = "没有授权";
                        // }
                        // Toast.makeText(act, "分享失败[" + eCode + "] " + eMsg,
                        // Toast.LENGTH_SHORT).show();
                    }
                }

            });
        } else if (SHARE_MEDIA.RENREN == platform) {
            RenrenShareContent renren = new RenrenShareContent();
            renren.setShareContent(shareContent);
            renren.setTargetUrl(shareUrl);
            renren.setTitle(shareTitle);
            renren.setShareMedia(umImage);
            mController.setAppWebSite(SHARE_MEDIA.RENREN, shareUrl);
            mController.setShareMedia(renren);
            boolean isOauth = OauthHelper.isAuthenticated(act, platform);
            if (isOauth) {
                mController.postShare(act, platform, new SnsPostListener() {
                    @Override
                    public void onStart() {
//						Toast.makeText(act, "开始分享.", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onComplete(SHARE_MEDIA platform, int eCode,
                                           SocializeEntity entity) {
                        if (eCode == 200) {
                            Toast.makeText(act, "分享成功.", Toast.LENGTH_SHORT)
                                    .show();
                        } else {
                            // String eMsg = "";
                            // if (eCode == -101) {
                            // eMsg = "没有授权";
                            // }
                            // Toast.makeText(act, "分享失败[" + eCode + "] " +
                            // eMsg,
                            // Toast.LENGTH_SHORT).show();
                        }
                    }

                });
            } else {
                mController.doOauthVerify(act, platform, new UMAuthListener() {

                    @Override
                    public void onStart(SHARE_MEDIA arg0) {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void onError(SocializeException arg0,
                                        SHARE_MEDIA arg1) {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void onComplete(Bundle arg0, SHARE_MEDIA arg1) {
                        mController.postShare(act, platform,
                                new SnsPostListener() {
                                    @Override
                                    public void onStart() {
//										 Toast.makeText(act, "开始分享.",
//										 Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onComplete(
                                            SHARE_MEDIA platform, int eCode,
                                            SocializeEntity entity) {
                                        if (eCode == 200) {
                                            Toast.makeText(act, "分享成功.",
                                                    Toast.LENGTH_SHORT).show();
                                        } else {
                                            // String eMsg = "";
                                            // if (eCode == -101) {
                                            // eMsg = "没有授权";
                                            // }
                                            // Toast.makeText(
                                            // act,
                                            // "分享失败[" + eCode + "] "
                                            // + eMsg,
                                            // Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                });

                    }

                    @Override
                    public void onCancel(SHARE_MEDIA arg0) {
                        // TODO Auto-generated method stub

                    }
                });
            }

        } else {
            boolean isOauth = OauthHelper.isAuthenticated(act, platform);
            if (isOauth) {
                mController.postShare(act, platform, new SnsPostListener() {
                    @Override
                    public void onStart() {
//						Toast.makeText(act, "开始分享.", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onComplete(SHARE_MEDIA platform, int eCode,
                                           SocializeEntity entity) {
                        if (eCode == 200) {
                            Toast.makeText(act, "分享成功.", Toast.LENGTH_SHORT)
                                    .show();
                        } else {
                            // String eMsg = "";
                            // if (eCode == -101) {
                            // eMsg = "没有授权";
                            // }
                            // Toast.makeText(act, "分享失败[" + eCode + "] " +
                            // eMsg,
                            // Toast.LENGTH_SHORT).show();
                        }
                    }

                });
            } else {
                mController.doOauthVerify(act, platform, new UMAuthListener() {

                    @Override
                    public void onStart(SHARE_MEDIA arg0) {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void onError(SocializeException arg0,
                                        SHARE_MEDIA arg1) {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void onComplete(Bundle arg0, SHARE_MEDIA arg1) {
                        mController.postShare(act, platform,
                                new SnsPostListener() {
                                    @Override
                                    public void onStart() {
//										 Toast.makeText(act, "开始分享.",
//										 Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onComplete(
                                            SHARE_MEDIA platform, int eCode,
                                            SocializeEntity entity) {
                                        if (eCode == 200) {
                                            Toast.makeText(act, "分享成功.",
                                                    Toast.LENGTH_SHORT).show();
                                        } else {
                                            // String eMsg = "";
                                            // if (eCode == -101) {
                                            // eMsg = "没有授权";
                                            // }
                                            // Toast.makeText(
                                            // act,
                                            // "分享失败[" + eCode + "] "
                                            // + eMsg,
                                            // Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                });

                    }

                    @Override
                    public void onCancel(SHARE_MEDIA arg0) {
                        // TODO Auto-generated method stub

                    }
                });
            }
        }

        return mController;
    }

    public static void configPlatforms(Activity act) {
// 添加微信平台
        UMWXHandler wxHandler = new UMWXHandler(act, PlatfromSetting.WX_APPID, PlatfromSetting.WX_APPSECRET);
        wxHandler.addToSocialSDK();
// 添加微信朋友圈
        UMWXHandler wxCircleHandler = new UMWXHandler(act, PlatfromSetting.WX_APPID, PlatfromSetting.WX_APPSECRET);
        wxCircleHandler.setToCircle(true);
        wxCircleHandler.addToSocialSDK();

    }


}
