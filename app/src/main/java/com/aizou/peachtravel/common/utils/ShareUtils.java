package com.aizou.peachtravel.common.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aizou.core.utils.LocalDisplay;
import com.aizou.peachtravel.R;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners;
import com.umeng.socialize.controller.listener.SocializeListeners.SnsPostListener;
import com.umeng.socialize.controller.listener.SocializeListeners.UMAuthListener;
import com.umeng.socialize.exception.SocializeException;
import com.umeng.socialize.media.QQShareContent;
import com.umeng.socialize.media.QZoneShareContent;
import com.umeng.socialize.media.RenrenShareContent;
import com.umeng.socialize.media.SmsShareContent;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.sso.QZoneSsoHandler;
import com.umeng.socialize.sso.SmsHandler;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.utils.OauthHelper;
import com.umeng.socialize.weixin.controller.UMWXHandler;
import com.umeng.socialize.weixin.media.CircleShareContent;
import com.umeng.socialize.weixin.media.WeiXinShareContent;

import java.util.Map;
import java.util.Set;

public class ShareUtils {
    public class PlatfromSetting {
        public final static String WX_APPID = "wx26b58c7173483529";
        public final static String WX_APPSECRET = "28daa05c021ebebe6d3cf06645b0c5ac";

        public final static String QQ_APPID = "1103275581";
        public final static String QQ_APPKEY = "VW1VnrywTEnK3vgw";

    }

    public static void showSelectPlatformDialog(Activity act) {
        final AlertDialog dialog = new AlertDialog.Builder(act).create();
        View contentView = View
                .inflate(act, R.layout.view_share_bar, null);
        TextView wxcircleTv = (TextView) contentView
                .findViewById(R.id.tv_wxcircle);
        TextView wechatTv = (TextView) contentView.findViewById(R.id.tv_wechat);
        TextView qzoneTv = (TextView) contentView.findViewById(R.id.tv_qzone);
        TextView doubanTv = (TextView) contentView.findViewById(R.id.tv_douban);
        TextView sinaTv = (TextView) contentView.findViewById(R.id.tv_sina);
        TextView qqTv = (TextView) contentView.findViewById(R.id.tv_qq);
        TextView cancleIv = (TextView) contentView
                .findViewById(R.id.tv_share_cancel);
        wxcircleTv.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();

            }
        });
        wechatTv.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();

            }
        });
        qzoneTv.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();

            }
        });
        doubanTv.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();

            }
        });
        sinaTv.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();

            }
        });
        qqTv.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();

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
        lp.width = (int) (display.getWidth()); // 设置宽度
        // lp.horizontalMargin=20;
        window.setAttributes(lp);
        window.setGravity(Gravity.BOTTOM); // 此处可以设置dialog显示的位置
        window.setWindowAnimations(R.style.SelectPicDialog); // 添加动画
    }

    public static void shareAppToWx(final Activity act,String content) {
        final UMSocialService mController = UMServiceFactory
                .getUMSocialService("com.umeng.share");
        // 设置分享内容
        mController.getConfig().closeToast();
        UMWXHandler wxHandler = new UMWXHandler(act, PlatfromSetting.WX_APPID, PlatfromSetting.WX_APPSECRET);
        wxHandler.addToSocialSDK();
        WeiXinShareContent circleMedia = new WeiXinShareContent();
        if(TextUtils.isEmpty(content)){
            circleMedia.setShareContent("我是桃子旅行，专为各位爱旅行的美眉们提供服务的贴心小App，安全下载: http://****");
        }else{
            circleMedia.setShareContent(content);
        }

        circleMedia.setTargetUrl("");
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
                    Toast.makeText(act, "分享成功.",
                            Toast.LENGTH_SHORT).show();
                } else {
                }
            }

        });
    }

//	public static UMSocialService shareRoute(final SHARE_MEDIA platform,
//			final Activity act, RouteDetail routeDetail) {
//		// 首先在您的Activity中添加如下成员变量
//		final UMSocialService mController = UMServiceFactory
//				.getUMSocialService("com.umeng.share");
//		// 设置分享内容
//		mController.getConfig().closeToast();
//		if (routeDetail.imageList != null
//				&& routeDetail.imageList.size() > 0) {
//			mController.setShareMedia(new UMImage(act,
//					routeDetail.imageList.get(0)));
//
//		}
//		mController.setShareContent("我搞了一条去 "+routeDetail.target.name+" 游玩的路线      "+routeDetail.shareUrl);
//		if (routeDetail.imageList != null && routeDetail.imageList.size() > 0) {
//			// 设置分享图片, 参数2为图片的url地址
////			mController.setShareMedia(new UMImage(act, routeDetail.imageList
////					.get(0)));
//			mController.setShareImage(new UMImage(act, routeDetail.imageList
//					.get(0)));
//
//		}
//		if (SHARE_MEDIA.WEIXIN_CIRCLE == platform) {
//			// wx967daebe835fbeac是你在微信开发平台注册应用的AppID, 这里需要替换成你注册的AppID
//			String appId = "wx59f2c267bbe88727";
//			// 添加微信平台
//			UMWXHandler wxHandler = new UMWXHandler(act, appId);
//			wxHandler.setToCircle(true);
//			wxHandler.addToSocialSDK();
//			CircleShareContent circleMedia = new CircleShareContent();
//			circleMedia.setShareContent("我搞了一条去 "+routeDetail.target.name+" 游玩的路线      ");
//			circleMedia.setTitle("我搞了一条去 "+routeDetail.target.name+" 游玩的路线      ");
//			if (routeDetail.imageList != null
//					&& routeDetail.imageList.size() > 0) {
//				// 设置分享图片, 参数2为图片的url地址
//				circleMedia.setShareImage(new UMImage(act,
//						routeDetail.imageList.get(0)));
//
//			}
//			circleMedia.setTargetUrl(routeDetail.shareUrl);
//			mController.setShareMedia(circleMedia);
//			mController.postShare(act, platform, new SnsPostListener() {
//				@Override
//				public void onStart() {
////					Toast.makeText(act, "开始分享.", Toast.LENGTH_SHORT).show();
//				}
//
//				@Override
//				public void onComplete(SHARE_MEDIA platform, int eCode,
//						SocializeEntity entity) {
//					if (eCode == 200) {
//						 Toast.makeText(act, "分享成功.",
//						 Toast.LENGTH_SHORT).show();
//					} else {
//						// String eMsg = "";
//						// if (eCode == -101) {
//						// eMsg = "没有授权";
//						// }
//						// Toast.makeText(act, "分享失败[" + eCode + "] " + eMsg,
//						// Toast.LENGTH_SHORT).show();
//					}
//				}
//
//			});
//			// wxHandler.setTitle("友盟社会化组件还不错-WXHandler...");
//		} else if (SHARE_MEDIA.WEIXIN == platform) {
//			String appId = "wx59f2c267bbe88727";
//			UMWXHandler wxHandler = new UMWXHandler(act, appId);
//			wxHandler.addToSocialSDK();
//			WeiXinShareContent circleMedia = new WeiXinShareContent();
//			circleMedia.setShareContent("我搞了一条去 "+routeDetail.target.name+" 游玩的路线      ");
//			circleMedia.setTitle("我搞了一条去 "+routeDetail.target.name+" 游玩的路线     ");
//			if (routeDetail.imageList != null
//					&& routeDetail.imageList.size() > 0) {
//				// 设置分享图片, 参数2为图片的url地址
//				circleMedia.setShareImage(new UMImage(act,
//						routeDetail.imageList.get(0)));
//
//			}
//			circleMedia.setTargetUrl(routeDetail.shareUrl);
//			mController.setShareMedia(circleMedia);
//			mController.postShare(act, platform, new SnsPostListener() {
//				@Override
//				public void onStart() {
////					Toast.makeText(act, "开始分享.", Toast.LENGTH_SHORT).show();
//				}
//
//				@Override
//				public void onComplete(SHARE_MEDIA platform, int eCode,
//						SocializeEntity entity) {
//					if (eCode == 200) {
//						 Toast.makeText(act, "分享成功.",
//						 Toast.LENGTH_SHORT).show();
//					} else {
//						// String eMsg = "";
//						// if (eCode == -101) {
//						// eMsg = "没有授权";
//						// }
//						// Toast.makeText(act, "分享失败[" + eCode + "] " + eMsg,
//						// Toast.LENGTH_SHORT).show();
//					}
//				}
//
//			});
//		} else if (SHARE_MEDIA.QQ == platform) {
//			UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(act, "1102120105",
//					"TjDERbD4yEfq3S8s");
//			qqSsoHandler.setTargetUrl(routeDetail.shareUrl);
//			qqSsoHandler.addToSocialSDK();
//			QQShareContent qqShareContent = new QQShareContent();
//			qqShareContent.setTitle("我搞了一条去 "+routeDetail.target.name+" 游玩的路线      ");
//			qqShareContent
//					.setShareContent("我搞了一条去 "+routeDetail.target.name+" 游玩的路线      "+routeDetail.shareUrl);
//			if (routeDetail.imageList != null
//					&& routeDetail.imageList.size() > 0) {
//				// 设置分享图片, 参数2为图片的url地址
//				qqShareContent.setShareMedia(new UMImage(act,
//						routeDetail.imageList.get(0)));
//
//			}
//			qqShareContent.setTargetUrl(routeDetail.shareUrl);
//			mController.setShareMedia(qqShareContent);
//			mController.postShare(act, platform, new SnsPostListener() {
//				@Override
//				public void onStart() {
////					Toast.makeText(act, "开始分享.", Toast.LENGTH_SHORT).show();
//				}
//
//				@Override
//				public void onComplete(SHARE_MEDIA platform, int eCode,
//						SocializeEntity entity) {
//					if (eCode == 200) {
//						 Toast.makeText(act, "分享成功.", Toast.LENGTH_SHORT)
//						 .show();
//					} else {
//						// String eMsg = "";
//						// if (eCode == -101) {
//						// eMsg = "没有授权";
//						// }
//						// Toast.makeText(act, "分享失败[" + eCode + "] " + eMsg,
//						// Toast.LENGTH_SHORT).show();
//					}
//				}
//
//			});
//
//		} else if (SHARE_MEDIA.QZONE == platform) {
//			QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler(act,
//					"1102120105", "TjDERbD4yEfq3S8s");
//			qZoneSsoHandler.addToSocialSDK();
//			QZoneShareContent qzone = new QZoneShareContent();
//			qzone.setShareContent("我搞了一条去 "+routeDetail.target.name+" 游玩的路线      "+routeDetail.shareUrl);
//			qzone.setTargetUrl(routeDetail.shareUrl);
//			qzone.setTitle("我搞了一条去 "+routeDetail.target.name+" 游玩的路线      ");
//			if (routeDetail.imageList != null
//					&& routeDetail.imageList.size() > 0) {
//				// 设置分享图片, 参数2为图片的url地址
//				qzone.setShareMedia(new UMImage(act, routeDetail.imageList
//						.get(0)));
//
//			}
//			mController.setShareMedia(qzone);
//			mController.postShare(act, platform, new SnsPostListener() {
//				@Override
//				public void onStart() {
////					Toast.makeText(act, "开始分享.", Toast.LENGTH_SHORT).show();
//				}
//
//				@Override
//				public void onComplete(SHARE_MEDIA platform, int eCode,
//						SocializeEntity entity) {
//					if (eCode == 200) {
//						 Toast.makeText(act, "分享成功.", Toast.LENGTH_SHORT)
//						 .show();
//					} else {
//						// String eMsg = "";
//						// if (eCode == -101) {
//						// eMsg = "没有授权";
//						// }
//						// Toast.makeText(act, "分享失败[" + eCode + "] " + eMsg,
//						// Toast.LENGTH_SHORT).show();
//					}
//				}
//
//			});
//
//		}else if(SHARE_MEDIA.RENREN==platform){
//			RenrenShareContent renren = new RenrenShareContent();
//			renren.setShareContent("我搞了一条去 "+routeDetail.target.name+" 游玩的路线      ");
//			renren.setTargetUrl(routeDetail.shareUrl);
//			renren.setTitle("我搞了一条去 "+routeDetail.target.name+" 游玩的路线      ");
//			if (routeDetail.imageList != null
//					&& routeDetail.imageList.size() > 0) {
//				// 设置分享图片, 参数2为图片的url地址
//				renren.setShareMedia(new UMImage(act, routeDetail.imageList
//						.get(0)));
//
//			}
//			mController.setAppWebSite(SHARE_MEDIA.RENREN, routeDetail.shareUrl);
//			mController.setShareMedia(renren);
//			boolean isOauth = OauthHelper.isAuthenticated(act, platform);
//			if (isOauth) {
//				mController.postShare(act, platform, new SnsPostListener() {
//					@Override
//					public void onStart() {
////						Toast.makeText(act, "开始分享.", Toast.LENGTH_SHORT).show();
//					}
//
//					@Override
//					public void onComplete(SHARE_MEDIA platform, int eCode,
//							SocializeEntity entity) {
//						if (eCode == 200) {
//							 Toast.makeText(act, "分享成功.", Toast.LENGTH_SHORT)
//							 .show();
//						} else {
//							// String eMsg = "";
//							// if (eCode == -101) {
//							// eMsg = "没有授权";
//							// }
//							// Toast.makeText(act, "分享失败[" + eCode + "] " +
//							// eMsg,
//							// Toast.LENGTH_SHORT).show();
//						}
//					}
//
//				});
//			} else {
//				mController.doOauthVerify(act, platform, new UMAuthListener() {
//
//					@Override
//					public void onStart(SHARE_MEDIA arg0) {
//						// TODO Auto-generated method stub
//
//					}
//
//					@Override
//					public void onError(SocializeException arg0,
//							SHARE_MEDIA arg1) {
//						// TODO Auto-generated method stub
//
//					}
//
//					@Override
//					public void onComplete(Bundle arg0, SHARE_MEDIA arg1) {
//						mController.postShare(act, platform,
//								new SnsPostListener() {
//									@Override
//									public void onStart() {
////										 Toast.makeText(act, "开始分享.",
////										 Toast.LENGTH_SHORT).show();
//									}
//
//									@Override
//									public void onComplete(
//											SHARE_MEDIA platform, int eCode,
//											SocializeEntity entity) {
//										if (eCode == 200) {
//											 Toast.makeText(act, "分享成功.",
//											 Toast.LENGTH_SHORT).show();
//										} else {
//											// String eMsg = "";
//											// if (eCode == -101) {
//											// eMsg = "没有授权";
//											// }
//											// Toast.makeText(
//											// act,
//											// "分享失败[" + eCode + "] "
//											// + eMsg,
//											// Toast.LENGTH_SHORT).show();
//										}
//									}
//
//								});
//
//					}
//
//					@Override
//					public void onCancel(SHARE_MEDIA arg0) {
//						// TODO Auto-generated method stub
//
//					}
//				});
//			}
//
//		}else if (SHARE_MEDIA.SMS == platform) {
////			SmsHandler smsHandler = new SmsHandler();
////			smsHandler.addToSocialSDK();
//			SmsShareContent content = new SmsShareContent();
//			content.setShareContent("我搞了一条去 "+routeDetail.target.name+" 游玩的路线      "+routeDetail.shareUrl);
//			mController.setShareMedia(content);
//			mController.directShare(act, platform, new SnsPostListener() {
//				@Override
//				public void onStart() {
//					// Toast.makeText(act, "开始分享.", Toast.LENGTH_SHORT).show();
//				}
//
//				@Override
//				public void onComplete(SHARE_MEDIA platform, int eCode,
//						SocializeEntity entity) {
//					if (eCode == 200) {
//						// Toast.makeText(act, "分享成功.", Toast.LENGTH_SHORT)
//						// .show();
//					} else {
//						// String eMsg = "";
//						// if (eCode == -101) {
//						// eMsg = "没有授权";
//						// }
//						// Toast.makeText(act, "分享失败[" + eCode + "] " + eMsg,
//						// Toast.LENGTH_SHORT).show();
//					}
//				}
//
//			});
//		} else {
//			boolean isOauth = OauthHelper.isAuthenticated(act, platform);
//			if (isOauth) {
//				mController.postShare(act, platform, new SnsPostListener() {
//					@Override
//					public void onStart() {
////						Toast.makeText(act, "开始分享.", Toast.LENGTH_SHORT).show();
//					}
//
//					@Override
//					public void onComplete(SHARE_MEDIA platform, int eCode,
//							SocializeEntity entity) {
//						if (eCode == 200) {
//							 Toast.makeText(act, "分享成功.", Toast.LENGTH_SHORT)
//							 .show();
//						} else {
//							// String eMsg = "";
//							// if (eCode == -101) {
//							// eMsg = "没有授权";
//							// }
//							// Toast.makeText(act, "分享失败[" + eCode + "] " +
//							// eMsg,
//							// Toast.LENGTH_SHORT).show();
//						}
//					}
//
//				});
//			} else {
//				mController.doOauthVerify(act, platform, new UMAuthListener() {
//
//					@Override
//					public void onStart(SHARE_MEDIA arg0) {
//						// TODO Auto-generated method stub
//
//					}
//
//					@Override
//					public void onError(SocializeException arg0,
//							SHARE_MEDIA arg1) {
//						// TODO Auto-generated method stub
//
//					}
//
//					@Override
//					public void onComplete(Bundle arg0, SHARE_MEDIA arg1) {
//						mController.postShare(act, platform,
//								new SnsPostListener() {
//									@Override
//									public void onStart() {
////										 Toast.makeText(act, "开始分享.",
////										 Toast.LENGTH_SHORT).show();
//									}
//
//									@Override
//									public void onComplete(
//											SHARE_MEDIA platform, int eCode,
//											SocializeEntity entity) {
//										if (eCode == 200) {
//											 Toast.makeText(act, "分享成功.",
//											 Toast.LENGTH_SHORT).show();
//										} else {
//											// String eMsg = "";
//											// if (eCode == -101) {
//											// eMsg = "没有授权";
//											// }
//											// Toast.makeText(
//											// act,
//											// "分享失败[" + eCode + "] "
//											// + eMsg,
//											// Toast.LENGTH_SHORT).show();
//										}
//									}
//
//								});
//
//					}
//
//					@Override
//					public void onCancel(SHARE_MEDIA arg0) {
//						// TODO Auto-generated method stub
//
//					}
//				});
//			}
//		}
//
//		return mController;
//	}

    public static void configPlatforms(Activity act) {
        String appId = "wx26b58c7173483529";
        String appSecret = "28daa05c021ebebe6d3cf06645b0c5ac";
// 添加微信平台
        UMWXHandler wxHandler = new UMWXHandler(act, appId, appSecret);
        wxHandler.addToSocialSDK();
// 添加微信朋友圈
        UMWXHandler wxCircleHandler = new UMWXHandler(act, appId, appSecret);
        wxCircleHandler.setToCircle(true);
        wxCircleHandler.addToSocialSDK();

        UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(act, "1103275581",
                "VW1VnrywTEnK3vgw");
        qqSsoHandler.addToSocialSDK();

        QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler(act,
                "1103275581", "VW1VnrywTEnK3vgw");
        qZoneSsoHandler.addToSocialSDK();

        SmsHandler smsHandler = new SmsHandler();
        smsHandler.addToSocialSDK();

    }


}
