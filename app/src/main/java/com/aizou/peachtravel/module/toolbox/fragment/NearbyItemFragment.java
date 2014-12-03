package com.aizou.peachtravel.module.toolbox.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.aizou.peachtravel.R;
import com.aizou.peachtravel.base.PeachBaseFragment;
import com.aizou.peachtravel.module.dest.adapter.PoiAdapter;
import com.sina.weibo.sdk.api.share.Base;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Rjm on 2014/11/24.
 */
public class NearbyItemFragment extends PeachBaseFragment {
    @InjectView(R.id.fragment_mainTab_item_progressBar)
    ProgressBar mFragmentMainTabItemProgressBar;
    private int tabIndex;
    public static final String INTENT_INT_INDEX = "intent_int_index";
    private ListView mListView;

    @Override
    public final View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_tabmain_item, container, false);
        ButterKnife.inject(this, rootView);
        tabIndex = getArguments().getInt(INTENT_INT_INDEX);
        mListView = (ListView)rootView.findViewById(R.id.list_view);
        mListView.setAdapter(new PoiAdapter(getActivity(), false));
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

}
