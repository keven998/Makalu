package com.xuejian.client.lxp.module.trade;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aizou.core.dialog.ToastUtil;
import com.aizou.core.http.HttpCallBack;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.PeachBaseActivity;
import com.xuejian.client.lxp.bean.LocBean;
import com.xuejian.client.lxp.common.account.AccountManager;
import com.xuejian.client.lxp.common.api.TravelApi;
import com.xuejian.client.lxp.common.gson.CommonJson4List;
import com.xuejian.client.lxp.db.User;
import com.xuejian.client.lxp.module.customization.DestMenuActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by yibiao.qin on 2016/4/21.
 */
public class SubscribeLocActivity extends PeachBaseActivity {

    @Bind(R.id.iv_nav_back)
    ImageView mIvNavBack;
    @Bind(R.id.tv_title_bar_title)
    TextView mTvTitleBarTitle;
    @Bind(R.id.tv_notice)
    TextView mTvNotice;
    @Bind(R.id.tv_subscribe)
    TextView mTvSubscribe;
    @Bind(R.id.content)
    LinearLayout mContent;
    public final static int SELECTED_TARGET = 105;

    public ArrayList<LocBean> selectedCity = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscribe_loc);
        ButterKnife.bind(this);
        mIvNavBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mTvSubscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent residentIntent = new Intent(mContext, DestMenuActivity.class);
                residentIntent.putExtra("exist",selectedCity);
                startActivityForResult(residentIntent, SELECTED_TARGET);
            }
        });
        mTvNotice.setText("旅行派APP重磅出击“定制功能了”，为了方便您能迅速抢单，请先订阅服务城市\n当有买家发布悬赏旅游需求时，平台将会根据您订阅的城市及时推送消息给您");
        User user = AccountManager.getInstance().getLoginAccount(this);
        if (user!=null){
            getData(user.getUserId());
        }

    }

    private void getData(long userId) {

        TravelApi.getSUB_CITY(userId, new HttpCallBack<String>() {

            @Override
            public void doSuccess(String result, String method) {
                CommonJson4List<LocBean> locBeanCommonJson = CommonJson4List.fromJson(result,LocBean.class);
                selectedCity.clear();
                selectedCity.addAll(locBeanCommonJson.result);
            }

            @Override
            public void doFailure(Exception error, String msg, String method) {

            }

            @Override
            public void doFailure(Exception error, String msg, String method, int code) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECTED_TARGET) {
                selectedCity.clear();
                ArrayList<LocBean> locBeans = data.getParcelableArrayListExtra("selected");
                selectedCity.addAll(locBeans);
                StringBuilder stringBuilder = new StringBuilder();
                for (int i = 0; i < selectedCity.size(); i++) {
                    if (i != 0) stringBuilder.append("、");
                    stringBuilder.append(selectedCity.get(i).zhName);
                }
                try {
                    addSubLocs(locBeans);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void addSubLocs(ArrayList<LocBean> list) throws Exception{
        JSONArray array = new JSONArray();
        for (LocBean bean : list) {
            JSONObject object = new JSONObject();
            object.put("id",bean.id);
            object.put("zhName",bean.zhName);
            array.put(object);
        }

        TravelApi.ADD_SUB_CITY(array, new HttpCallBack<String>() {

            @Override
            public void doSuccess(String result, String method) {
                ToastUtil.getInstance(mContext).showToast("订阅城市已添加");
            }

            @Override
            public void doFailure(Exception error, String msg, String method) {

            }

            @Override
            public void doFailure(Exception error, String msg, String method, int code) {

            }
        });

    }
}
