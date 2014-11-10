package com.aizou.peachtravel.module.travel;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.aizou.peachtravel.R;
import com.aizou.peachtravel.base.PeachBaseFragment;
import com.aizou.peachtravel.bean.PeachUser;
import com.aizou.peachtravel.common.account.AccountManager;
import com.aizou.peachtravel.module.my.LoginActivity;
import com.aizou.peachtravel.module.travel.im.IMMainActivity;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

/**
 * Created by Rjm on 2014/10/9.
 */
public class TravelFragment extends PeachBaseFragment implements View.OnClickListener {
    public final static int IM_LOGIN=100;
    @ViewInject(R.id.btn_lxq)
    Button lxqBtn;
    private PeachUser user;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView =inflater.inflate(R.layout.fragment_travel,null);
        ViewUtils.inject(this, rootView);
        lxqBtn.setOnClickListener(this);
        user = AccountManager.getInstance().getLoginAccount(getActivity());
        return rootView;
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
