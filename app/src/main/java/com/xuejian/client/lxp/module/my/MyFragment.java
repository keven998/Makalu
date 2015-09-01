package com.xuejian.client.lxp.module.my;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.PeachBaseFragment;

import butterknife.ButterKnife;

/**
 * Created by Rjm on 2014/10/9.
 */
public class MyFragment extends PeachBaseFragment implements View.OnClickListener {
    private View rootView;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_my, null);
        ButterKnife.inject(this, rootView);
        rootView.findViewById(R.id.my_profile).setOnClickListener(this);

        ImageView settingBtn = (ImageView)rootView.findViewById(R.id.setting_btn);
        settingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent settingIntent = new Intent(getActivity(), SettingActivity.class);
                startActivity(settingIntent);
            }
        });

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }


    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {


            case R.id.my_profile:
                Intent intent = new Intent(getActivity(),MyProfileActivity.class);
                startActivity(intent);
                break;


            default:
                break;
        }
    }








}
