package com.xuejian.client.lxp.module.my;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.aizou.core.dialog.ToastUtil;
import com.aizou.core.http.HttpCallBack;
import com.aizou.core.utils.RegexUtils;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.PeachBaseActivity;
import com.xuejian.client.lxp.common.account.AccountManager;
import com.xuejian.client.lxp.common.api.UserApi;
import com.xuejian.client.lxp.common.widget.FlowLayout;
import com.xuejian.client.lxp.db.User;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by xuyongchen on 15/8/28.
 */
public class TravelExpertApplyActivity extends PeachBaseActivity {
    @InjectView(R.id.number_input)
    EditText numberInput;
    PopupWindow popupWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.travel_expert_apply);
        ButterKnife.inject(this);
        findViewById(R.id.apply_head_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        findViewById(R.id.apply_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPhoneNumber();
            }
        });
    }

    public void checkPhoneNumber() {
        final String myphoneNumber = numberInput.getText().toString();
        if (myphoneNumber == null || TextUtils.isEmpty(myphoneNumber) || myphoneNumber.trim().length() == 0) {
            Toast.makeText(TravelExpertApplyActivity.this, "手机号不能为空哦!", Toast.LENGTH_SHORT).show();
            return;
        } else if (!RegexUtils.isMobileNO(myphoneNumber.trim())) {
            Toast.makeText(TravelExpertApplyActivity.this, "请输入正确地手机号格式~", Toast.LENGTH_SHORT).show();
            return;
        } else {
            submitToServer(myphoneNumber.trim());
        }
    }

    private void submitToServer(String phoneNumber) {
        User user = AccountManager.getInstance().getLoginAccount(mContext);
        long id = -1;
        if (user != null) {
            id = user.getUserId();
        }
        UserApi.experRequest(phoneNumber, id, new HttpCallBack<String>() {
            @Override
            public void doSuccess(String result, String method) {
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(result);
                    if (jsonObject.getInt("code") == 0) {
                        showPopUpWindow();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


            @Override
            public void doFailure(Exception error, String msg, String method) {
                ToastUtil.getInstance(TravelExpertApplyActivity.this).showToast("好像没有网络额~");
            }

            @Override
            public void doFailure(Exception error, String msg, String method, int code) {

            }
        });

    }


    private void showPopUpWindow() {
        if (popupWindow == null) {
            popupWindow = new PopupWindow(TravelExpertApplyActivity.this);
            View view = getLayoutInflater().inflate(R.layout.expert_apply_info, null, false);
            Button button = (Button) view.findViewById(R.id.i_understand);
            popupWindow.setContentView(view);
            popupWindow.setWidth(FlowLayout.LayoutParams.MATCH_PARENT);
            popupWindow.setHeight(FlowLayout.LayoutParams.MATCH_PARENT);
            //popupWindow.setAnimationStyle(R.style.PopAnimation);
            ColorDrawable drawable = new ColorDrawable(getResources().getColor(R.color.transparent_color));
            popupWindow.setBackgroundDrawable(drawable);

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    popupWindow.dismiss();
                    finish();
                }
            });
        }
        popupWindow.showAtLocation(findViewById(R.id.apply_submit), Gravity.BOTTOM, 0, 0);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
