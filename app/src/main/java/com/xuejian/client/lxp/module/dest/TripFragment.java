package com.xuejian.client.lxp.module.dest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.PeachBaseFragment;
import com.xuejian.client.lxp.common.account.AccountManager;
import com.xuejian.client.lxp.db.userDB.User;
import com.xuejian.client.lxp.module.my.LoginActivity;
import com.xuejian.client.lxp.module.toolbox.StrategyListActivity;
import com.xuejian.client.lxp.module.toolbox.im.ExpertListActivity;

/**
 * Created by lxp_dqm07 on 2015/4/11.
 */
public class TripFragment extends PeachBaseFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_trip, null);

        rootView.findViewById(R.id.lxp_search_bg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sear_intent = new Intent(getActivity(), SearchAllActivity.class);
                startActivityWithNoAnim(sear_intent);
                getActivity().overridePendingTransition(android.R.anim.fade_in, R.anim.slide_stay);
            }
        });

        rootView.findViewById(R.id.lxp_helper).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ExpertIntent = new Intent(getActivity(), ExpertListActivity.class);
                startActivity(ExpertIntent);
            }
        });

        rootView.findViewById(R.id.lxp_plan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final User user = AccountManager.getInstance().getLoginAccount(getActivity());
                if (user != null) {
                    Intent intent = new Intent(getActivity(), StrategyListActivity.class);
                    intent.putExtra("userId", String.valueOf(user.getUserId()));
                    intent.putExtra("isExpertPlan", false);
                    startActivity(intent);
                } else {
                    Intent LoginIntent = new Intent(getActivity(), LoginActivity.class);
                    startActivityForResult(LoginIntent, 1);
                    getActivity().overridePendingTransition(R.anim.push_bottom_in, R.anim.slide_stay);
                }
            }
        });

        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 1) {
                final User user = AccountManager.getInstance().getLoginAccount(getActivity());
                if (user != null) {
                    Intent intent = new Intent(getActivity(), StrategyListActivity.class);
                    intent.putExtra("userId", String.valueOf(user.getUserId()));
                    intent.putExtra("isExpertPlan", false);
                    startActivity(intent);
                }
            }
        }
    }

}
