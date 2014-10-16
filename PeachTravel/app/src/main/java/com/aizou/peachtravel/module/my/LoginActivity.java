 package com.aizou.peachtravel.module.my;


import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.aizou.core.dialog.DialogManager;
import com.aizou.core.dialog.ToastUtil;
import com.aizou.core.http.HttpCallBack;
import com.aizou.core.log.LogUtil;
import com.aizou.peachtravel.R;
import com.aizou.peachtravel.base.PeachBaseActivity;
import com.aizou.peachtravel.bean.PeachUser;
import com.aizou.peachtravel.common.account.AccountManager;
import com.aizou.peachtravel.common.api.UserApi;
import com.aizou.peachtravel.common.gson.CommonJson;
import com.aizou.peachtravel.common.thirdpart.SnsAccountsUtils;
import com.aizou.peachtravel.common.thirdpart.weiboapi.User;
import com.aizou.peachtravel.common.thirdpart.weiboapi.WeiboUsersAPI;
import com.aizou.peachtravel.common.thirdpart.weixin.WeixinApi;
import com.aizou.peachtravel.common.utils.ShareUtils;
import com.aizou.peachtravel.common.widget.TitleHeaderBar;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuth;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.tencent.connect.UserInfo;
import com.tencent.connect.auth.QQAuth;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.sina.weibo.sdk.auth.sso.SsoHandler;

 public class LoginActivity extends PeachBaseActivity {
    @ViewInject(R.id.btn_login)
    private Button loginBtn;
    @ViewInject(R.id.title_bar)
    private TitleHeaderBar titleBar;
	private TextView weixinBtn,qqBtn, weiboBtn;
//	private View mTitlebar;
	// sina weibo sso handler
	private SsoHandler mWeiboSsoHandler;
	// tencent sso handler
//	private QQAuth mQQAuth;
	private Tencent mTencentHandler;
	
	public JSONObject uploadJson;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
	}

	private void initView() {
		setContentView(R.layout.activity_login);
        ViewUtils.inject(this);
		initTitlebar();
        weixinBtn = (TextView) findViewById(R.id.btn_weixin_login);
		qqBtn = (TextView) findViewById(R.id.btn_qq_login);
		weiboBtn = (TextView) findViewById(R.id.btn_weibo_login);
        weixinBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogManager.getInstance().showProgressDialog(mContext, "正在登录");
                weixinLogin();
//                UserApi.authSignUp("123456",new HttpCallBack() {
//                    @Override
//                    public void doSucess(Object result, String method) {
//
//                    }
//
//                    @Override
//                    public void doFailure(Exception error, String msg, String method) {
//
//                    }
//                });
            }
        });
		qqBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				uploadJson =new JSONObject();
				qqLogin(true);

			}
		});
		weiboBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
                DialogManager.getInstance().showProgressDialog(mContext, "正在登录");
				uploadJson =new JSONObject();
				weiboLogin(true);
			}
		});
        loginBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent =new Intent(mContext,AccountActvity.class);
//                startActivity(intent);
                PeachUser user = new PeachUser();
                user.nickName="小明";
                user.userId ="12345678";
                user.avatar="http://img3.imgtn.bdimg.com/it/u=1223899284,1318045701&fm=116&gp=0.jpg";
                user.signature="我就是我";
                AccountManager.getInstance().saveLoginAccount(mContext,user );
                finish();
            }
        });
        findViewById(R.id.tv_forget_pwd).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(mContext,ForgetPwdActivity.class);
                startActivity(intent);
            }
        });
	}
	private void initTitlebar() {
        titleBar.getRightTextView().setText("注册");
        titleBar.setRightOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext,RegActivity.class);
                startActivity(intent);
            }
        });

	}
	
	@Override
	public void finish() {
		// TODO Auto-generated method stub
		super.finish();
	}

	private void qqLogin(boolean login) {
		
		mTencentHandler = Tencent.createInstance(SnsAccountsUtils.TencentConstants.APP_KEY,
				this.getApplicationContext());
		if (login) {
			if (!mTencentHandler.isSessionValid()) {
				mTencentHandler.login(this, SnsAccountsUtils.TencentConstants.SCOPE,
						new TencentUiListener(TencentUiListener.TYPE_LOGIN));
			}
		} else {
			mTencentHandler.logout(this.getApplicationContext());
		}
	}

     public  void weixinLogin(){
         ShareUtils.configPlatforms(this);
         WeixinApi.getInstance().auth(this,new WeixinApi.WeixinAuthListener() {
             @Override
             public void onComplete(String code) {
                 ToastUtil.getInstance(mContext).showToast("授权成功");
                 UserApi.authSignUp(code,new HttpCallBack<String>() {
                     @Override
                     public void doSucess(String result, String method) {
                         DialogManager.getInstance().dissMissProgressDialog();
                         CommonJson<PeachUser> userResult = CommonJson.fromJson(result,PeachUser.class);
                         if(userResult.code==0){
                             AccountManager.getInstance().saveLoginAccount(mContext,userResult.result);
                             finish();
                         }else{

                         }




                     }

                     @Override
                     public void doFailure(Exception error, String msg, String method) {

                     }
                 });
             }

             @Override
             public void onError(int errCode) {
                 DialogManager.getInstance().dissMissProgressDialog();
                 ToastUtil.getInstance(mContext).showToast("授权失败");

             }

             @Override
             public void onCancel() {
                 DialogManager.getInstance().dissMissProgressDialog();
                 ToastUtil.getInstance(mContext).showToast("授权取消");
             }
         });

     }

	private void weiboLogin(boolean login) {
		if (login) {
			WeiboAuth.AuthInfo authInfo = new WeiboAuth.AuthInfo(this, SnsAccountsUtils.WeiboConstants.APP_KEY,
					SnsAccountsUtils.WeiboConstants.REDIRECT_URL, SnsAccountsUtils.WeiboConstants.SCOPE);
			WeiboAuth weiboAuth = new WeiboAuth(this, authInfo);
			if (mWeiboSsoHandler == null) {
				mWeiboSsoHandler = new SsoHandler((Activity) this, weiboAuth);
			}
			mWeiboSsoHandler.authorize(new SinaWeiboAuthListener());
		} else {
			// new
			// LogoutAPI(AccessTokenKeeper.readAccessToken(this)).logout(mLogoutListener);
		}
		
	}

	/**
	 * sina weibo登入按钮的监听器，接收授权结果。
	 */
	private class SinaWeiboAuthListener implements WeiboAuthListener {
		@SuppressLint("NewApi")
		@Override
		public void onComplete(Bundle values) {
			Oauth2AccessToken accessToken = Oauth2AccessToken
					.parseAccessToken(values);
			LogUtil.d("test", "weibo = " + values);
			if (accessToken != null && accessToken.isSessionValid()) {
				try {
					uploadJson.put("oauthId", values.getString("uid"));
					uploadJson.put("token", values.get("access_token"));
				} catch (JSONException e) {
					e.printStackTrace();
				}
				WeiboUsersAPI userApi = new WeiboUsersAPI(accessToken);
				userApi.show(Long.parseLong(values.getString("uid")), new RequestListener() {
					
					@Override
					public void onWeiboException(WeiboException arg0) {
						arg0.printStackTrace();
						
					}
					
					@Override
					public void onComplete(String arg0) {
						LogUtil.d(arg0);
						if(!TextUtils.isEmpty(arg0)){
							User user = User.parse(arg0);
							try {
								uploadJson.put("nickName", user.screen_name);
								uploadJson.put("avatar", user.avatar_large);
								uploadJson.put("provider", "weibo");
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							
							syncAccount( uploadJson.toString());
						}
						
					}
				});
			} else {
				String code = values.getString("code", "");
				LogUtil.d("test", "login weibo error code = " + code);
			}
		}

		@Override
		public void onWeiboException(WeiboException e) {
			DialogManager.getInstance().dissMissProgressDialog();
			Toast.makeText(LoginActivity.this, e.getMessage(),
					Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onCancel() {
            DialogManager.getInstance().dissMissProgressDialog();
			Toast.makeText(LoginActivity.this, "微博登录取消", Toast.LENGTH_SHORT)
					.show();
		}
	}

	private class TencentUiListener implements IUiListener {
		public static final int TYPE_LOGIN = 1;
		public static final int TYPE_GETINFO = 2;

		private int type;

		public TencentUiListener(int t) {
			super();
			type = t;
		}

		@Override
		public void onComplete(Object response) {
			 doComplete((JSONObject) response);
			LogUtil.d("test", "qq response = " + response);
//			syncAccount("qq", response.toString());
		}

		protected void doComplete(JSONObject values) {
			// Toast.makeText(LoginActivity.this, "QQ登录成功", Toast.LENGTH_SHORT)
			// .show();
			if (type == TYPE_LOGIN) {
				QQAuth qa = QQAuth.createInstance(SnsAccountsUtils.TencentConstants.APP_KEY,
						LoginActivity.this.getApplicationContext());
				try {
					uploadJson.put("oauthId", values.getString("openid"));
					uploadJson.put("token", values.getString("access_token"));
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				UserInfo info = new UserInfo(LoginActivity.this,
						qa.getQQToken());
				info.getUserInfo(new TencentUiListener(TYPE_GETINFO));  
			} else {
				try {
					uploadJson.put("nickName", values.getString("nickname"));
					uploadJson.put("avatar", values.getString("figureurl_qq_2"));
					uploadJson.put("provider", "tencent");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				syncAccount( uploadJson.toString());
			}
		}

		@Override
		public void onError(UiError e) {
            DialogManager.getInstance().dissMissProgressDialog();
			Toast.makeText(LoginActivity.this, "QQ登录失败", Toast.LENGTH_SHORT)
					.show();
		}

		@Override
		public void onCancel() {
            DialogManager.getInstance().dissMissProgressDialog();
			Toast.makeText(LoginActivity.this, "QQ登录取消", Toast.LENGTH_SHORT)
					.show();
		}
	}

	private void syncAccount( String data) {
		DialogManager.getInstance().showProgressDialog(mContext,"正在登录");
		LogUtil.d(data);


	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mWeiboSsoHandler = null;
		mTencentHandler = null;
	}

	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		
		super.onActivityResult(requestCode, resultCode, data);
		if (mWeiboSsoHandler != null) {
			mWeiboSsoHandler.authorizeCallBack(requestCode, resultCode, data);
		}
		if (mTencentHandler != null) {
			mTencentHandler.onActivityResult(requestCode, resultCode, data);
		}
	}

}
