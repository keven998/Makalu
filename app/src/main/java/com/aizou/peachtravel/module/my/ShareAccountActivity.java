package com.aizou.peachtravel.module.my;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.aizou.peachtravel.common.dialog.DialogManager;
import com.aizou.core.log.LogUtil;
import com.aizou.peachtravel.R;
import com.aizou.peachtravel.base.PeachBaseActivity;
import com.aizou.peachtravel.common.utils.ShareUtils;
import com.aizou.peachtravel.common.widget.TitleHeaderBar;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners.SocializeClientListener;
import com.umeng.socialize.controller.listener.SocializeListeners.UMAuthListener;
import com.umeng.socialize.controller.listener.SocializeListeners.UMDataListener;
import com.umeng.socialize.exception.SocializeException;
import com.umeng.socialize.sso.UMSsoHandler;
import com.umeng.socialize.utils.OauthHelper;

public class ShareAccountActivity extends PeachBaseActivity {

	private List<ShareAccount> lists;
	private ListView mListView;
	private UMSocialService mController;

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_share_account);
		initTitlebar();
		ShareUtils.configPlatforms(this);
		mController = UMServiceFactory.getUMSocialService("com.umeng.share");
		Resources res = getResources();
		String[] accounts = res.getStringArray(R.array.sns);
		// int []icons = getResources().getIntArray(R.array.sns_icons);
		TypedArray typedArray = res.obtainTypedArray(R.array.sns_icons);
		int length = accounts.length;
		lists = new ArrayList<ShareAccount>();
		ShareAccount account;
		for (int i = 0; i < length; ++i) {
			account = new ShareAccount();
			account.title = accounts[i];
			int iconId = typedArray.getResourceId(i, 0);
			account.iconId = iconId;
			if (account.title.equals("qq")) {
				account.platform = SHARE_MEDIA.QQ;
			} else if (account.title.equals("新浪微博")) {
				account.platform = SHARE_MEDIA.SINA;
			} else if (account.title.equals("豆瓣")) {
				account.platform = SHARE_MEDIA.DOUBAN;
			} else if (account.title.equals("QQ空间")) {
				account.platform = SHARE_MEDIA.QZONE;
			} else if (account.title.equals("微信")) {
                account.platform = SHARE_MEDIA.WEIXIN;
            }
//            else if (account.title.equals("腾讯微博")) {
//				account.platform = SHARE_MEDIA.TENCENT;
//			} else if (account.title.equals("人人")) {
//				account.platform = SHARE_MEDIA.RENREN;
//			}
			lists.add(account);
		}
		typedArray.recycle();

		mListView = (ListView) findViewById(R.id.base_list);
		mListView.setAdapter(new ItemAdapter());
	}

	private void initTitlebar() {
        TitleHeaderBar thb = (TitleHeaderBar)findViewById(R.id.ly_header_bar_title_wrap);
        thb.getTitleTextView().setText("分享账户管理");
        thb.enableBackKey(true);
	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		if (mController != null) {
			UMSsoHandler ssoHandler = mController.getConfig().getSsoHandler(
					arg0);
			if (ssoHandler != null) {
				ssoHandler.authorizeCallBack(arg0, arg1, arg2);
			}
		}
		super.onActivityResult(arg0, arg1, arg2);
	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub
		super.finish();
	}

	public class ItemAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return lists.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return lists.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public View getView(int positon, View convertView, ViewGroup vg) {
			View view = convertView;
			if (view == null) {
				view = View.inflate(ShareAccountActivity.this,
                        R.layout.sns_account_list_item, null);
			}

			final ShareAccount account = lists.get(positon);
            CheckBox switchBtn = (CheckBox) view.findViewById(R.id.bind_status);
			TextView textView = (TextView) view.findViewById(R.id.title);
			if (TextUtils.isEmpty(account.screen_name)) {
				textView.setText(account.title);
				if(OauthHelper.isAuthenticated(mContext, account.platform)){
						mController.getPlatformInfo(mContext, account.platform,
								new UMDataListener() {
									@Override
									public void onStart() {
									}

									@Override
									public void onComplete(int status,
											Map<String, Object> info) {
										if (status == 200 && info != null) {
											account.screen_name = (String) info.get("screen_name");
											notifyDataSetChanged();
											LogUtil.d("获取信息-----------------");
										} else {

										}

									}
								});
					}
			} else {
				textView.setText(account.title + "(" + account.screen_name + ")");
			}

			ImageView logoView = (ImageView) view.findViewById(R.id.logo);
			logoView.setImageResource(account.iconId);

			if (OauthHelper.isAuthenticated(mContext, account.platform)) {
//				switchBtn.setBackgroundResource(R.drawable.cb_on_bind);
                switchBtn.setChecked(true);
			} else {
//				switchBtn.setBackgroundResource(R.drawable.cb_off_bind);
                switchBtn.setChecked(false);
			}

			view.setTag(positon);
			view.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					final ShareAccount shareAccount = lists.get((Integer)v.getTag());
					if (!OauthHelper
							.isAuthenticated(mContext, shareAccount.platform)) {
//						showLoadingDialog("正在获取授权");
						mController.doOauthVerify(mContext, shareAccount.platform,
								new UMAuthListener() {

									@Override
									public void onStart(SHARE_MEDIA arg0) {
										// TODO Auto-generated method stub

									}

									@Override
									public void onError(
											SocializeException arg0,
											SHARE_MEDIA arg1) {
//										closeProgressDialog();

									}

									@Override
									public void onComplete(Bundle value,
											SHARE_MEDIA arg1) {
//										closeProgressDialog();
										if (value != null
												&& !TextUtils.isEmpty(value
														.getString("uid"))) {
											mController.getPlatformInfo(
													mContext, shareAccount.platform,
													new UMDataListener() {
														@Override
														public void onStart() {
														}

														@Override
														public void onComplete(
																int status,
																Map<String, Object> info) {
															if (status == 200
																	&& info != null) {
																shareAccount.screen_name = (String) info
																		.get("screen_name");
																LogUtil.d("--------------------------");
																notifyDataSetChanged();
															} else {

															}

														}
													});
											notifyDataSetChanged();
										} else {
										}

									}

									@Override
									public void onCancel(SHARE_MEDIA arg0) {
//										closeProgressDialog();

									}
								});
					} else {
//                        DialogManager.getInstance().showMessageDialogWithDoubleButtonSelf(mContext,"确定","取消","提示","确定解除绑定账户吗？",new OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
//                                DialogManager.getInstance().showLoadingDialog(mContext, "解除绑定");
//                                mController.deleteOauth(mContext, shareAccount.platform,
//												new SocializeClientListener() {
//
//													@Override
//													public void onStart() {
//														// TODO Auto-generated method stub
//
//													}
//
//													@Override
//													public void onComplete(int arg0,
//															SocializeEntity arg1) {
//                                                        DialogManager.getInstance().dissMissLoadingDialog();
//														shareAccount.screen_name = null;
//														notifyDataSetChanged();
//
//													}
//
//												});
//                            }
//                        });
//						final ComfirmDialog dialog = new ComfirmDialog(mContext);
//						dialog.setMessage(getResources().getString(
//								R.string.comfirm_delete_account));
//						dialog.setPositiveButton(
//								getResources().getString(R.string.ok),
//								new OnClickListener() {
//
//									@Override
//									public void onClick(View v) {
//										showLoadingDialog("解除绑定");
//										mController.deleteOauth(mContext, shareAccount.platform,
//												new SocializeClientListener() {
//
//													@Override
//													public void onStart() {
//														// TODO Auto-generated method stub
//
//													}
//
//													@Override
//													public void onComplete(int arg0,
//															SocializeEntity arg1) {
//														closeProgressDialog();
//														shareAccount.screen_name = null;
//														notifyDataSetChanged();
//
//													}
//
//												});
//										dialog.dismiss();
//
//									}
//								});
//						dialog.setNegativeButton(
//								getResources().getString(R.string.cancle),
//								new OnClickListener() {
//
//									@Override
//									public void onClick(View v) {
//										dialog.dismiss();
//
//									}
//								});
//						dialog.show();

						
						
					}
				}
			});
			return view;
		}

	}

	public class ShareAccount {
		public String title;
		public SHARE_MEDIA platform;
		public int iconId;
		public String url;
		public String screen_name;
	}
}
