package com.aizou.peachtravel.module.dest.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.aizou.peachtravel.R;
import com.aizou.peachtravel.base.PeachBaseFragment;
import com.aizou.peachtravel.bean.RouteDayDragBean;
import com.aizou.peachtravel.common.widget.dslv.DragSortListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Rjm on 2014/11/24.
 */
public class RouteDayFragment extends PeachBaseFragment {
    @InjectView(R.id.edit_dslv)
    DragSortListView mEditDslv;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_route_day, container, false);
        ButterKnife.inject(this, rootView);
        return rootView;

    }
    public class DragAdapter extends BaseAdapter{
        public Context context;
        public List<RouteDayDragBean> dragBeanList;
        public DragAdapter(Context context){
            this.context = context;
            dragBeanList = new ArrayList<RouteDayDragBean>();

        }

        public void resizeData(){

        }

        @Override
        public int getCount() {
            return dragBeanList.size();
        }

        @Override
        public Object getItem(int position) {
            return dragBeanList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return null;
        }
    }
}
