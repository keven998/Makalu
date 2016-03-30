package com.xuejian.client.lxp.module.customization;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.PeachBaseFragment;
import com.xuejian.client.lxp.common.widget.twowayview.layout.DividerItemDecoration;

/**
 * Created by yibiao.qin on 2016/3/28.
 */
public class CustomMainFragment extends PeachBaseFragment {

    XRecyclerView recyclerView;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_custom_info,null);
        recyclerView = (XRecyclerView) view.findViewById(R.id.recyclerview);
        recyclerView.setPullRefreshEnabled(false);
        View headView = LayoutInflater.from(getActivity()).inflate(R.layout.head_project, (ViewGroup) view.findViewById(R.id.content),false);
        recyclerView.addHeaderView(headView);
        setupRecyclerView(recyclerView);

        return view;
    }
    private void setupRecyclerView(RecyclerView recyclerView) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(false);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL_LIST));
        ProjectAdapter adapter = new ProjectAdapter(getActivity(), 0);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new ProjectAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position, long id, boolean expire) {

            }
        });
    }
}
