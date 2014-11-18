package com.aizou.peachtravel.module.toolbox;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.aizou.core.log.LogUtil;
import com.aizou.core.utils.DateTimeUtil;
import com.aizou.core.utils.DateUtil;
import com.aizou.peachtravel.R;
import com.aizou.peachtravel.base.PeachBaseFragment;
import com.aizou.peachtravel.bean.PeachUser;
import com.aizou.peachtravel.common.account.AccountManager;
import com.aizou.peachtravel.common.utils.UILUtils;
import com.aizou.peachtravel.common.yweathergetter4a.WeatherInfo;
import com.aizou.peachtravel.common.yweathergetter4a.YahooWeather;
import com.aizou.peachtravel.common.yweathergetter4a.YahooWeatherInfoListener;
import com.aizou.peachtravel.module.my.LoginActivity;
import com.aizou.peachtravel.module.toolbox.im.IMMainActivity;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by Rjm on 2014/10/9.
 */
public class TravelFragment extends PeachBaseFragment implements View.OnClickListener {
    public final static int IM_LOGIN=100;
    @ViewInject(R.id.btn_lxq)
    Button lxqBtn;
    @ViewInject(R.id.iv_weather)
    ImageView weatherIv;
    @ViewInject(R.id.tv_weather)
    TextView weatherTv;
    private PeachUser user;
    private String[] weatherArray;
    private String weatherStr;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView =inflater.inflate(R.layout.fragment_travel,null);
        ViewUtils.inject(this, rootView);
        lxqBtn.setOnClickListener(this);
        user = AccountManager.getInstance().getLoginAccount(getActivity());
        weatherArray = getResources().getStringArray(R.array.weather);
        getWeather(116.402544,39.93242);
        return rootView;
    }

    private void getWeather(double lat,double lon){
        YahooWeather.getInstance().queryYahooWeatherByLatLon(getActivity(),lat+"",lon+"",new YahooWeatherInfoListener() {
            @Override
            public void gotWeatherInfo(WeatherInfo weatherInfo) {
                LogUtil.d(weatherInfo.getCurrentText());
                weatherStr = DateUtil.getCurrentMonthDay()+" 北京 "+weatherArray[weatherInfo.getCurrentCode()];
                ImageLoader.getInstance().displayImage(weatherInfo.getCurrentConditionIconURL(),weatherIv, UILUtils.getDefaultOption());
                weatherTv.setText(weatherStr);

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_lxq:
                if(user!=null&& !TextUtils.isEmpty(user.easemobUser)){
                    Intent intent = new Intent(getActivity(), IMMainActivity.class);
                    startActivity(intent);
                }else{
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivityForResult(intent,IM_LOGIN);
                }

                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode== Activity.RESULT_OK){
            switch (requestCode){
                case IM_LOGIN:
                    Intent intent = new Intent(getActivity(), IMMainActivity.class);
                    startActivity(intent);
                    break;
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        user = AccountManager.getInstance().getLoginAccount(getActivity());
    }
}
