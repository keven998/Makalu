package com.xuejian.client.lxp.module.toolbox.im;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.FrameLayout;

import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.PeachBaseActivity;
import com.xuejian.client.lxp.common.widget.TitleHeaderBar;

import butterknife.Bind;
import butterknife.ButterKnife;


public class ContactActivity extends PeachBaseActivity {
    @Bind(R.id.title_bar)
    TitleHeaderBar titleBar;
    @Bind(R.id.content_frame)
    FrameLayout contentFrame;
    private Fragment contactListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        ButterKnife.bind(this);
        titleBar.getTitleTextView().setText("联系人");
        titleBar.enableBackKey(true);

        titleBar.setRightViewImageRes(R.drawable.navigation_addfriend);
        titleBar.setRightOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //MobclickAgent.onEvent(ContactActivity.this, "navigation_item_add_lxp_friend");
                startActivity(new Intent(ContactActivity.this, AddContactActivity.class));
            }
        });
        contactListFragment = new ContactlistFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean("isAddFriend", false);
        contactListFragment.setArguments(bundle);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, contactListFragment).commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //MobclickAgent.onPause(this);
    }

    @Override
    public void finish() {
        super.finishWithNoAnim();
        overridePendingTransition(0, R.anim.push_bottom_out);
    }
}
