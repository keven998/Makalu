package com.aizou.peachtravel.module.dest;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.aizou.core.widget.listHelper.ListViewDataAdapter;
import com.aizou.core.widget.listHelper.ViewHolderBase;
import com.aizou.core.widget.listHelper.ViewHolderCreator;
import com.aizou.peachtravel.R;
import com.aizou.peachtravel.base.PeachBaseActivity;
import com.aizou.peachtravel.bean.CityBean;
import com.aizou.peachtravel.common.utils.UILUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by Rjm on 2014/11/18.
 */
public class SearchDestActivity extends PeachBaseActivity {

    private ListView mSearchResultLv;
    private ListViewDataAdapter mSearchResultAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
    }

    private void initView() {
        setContentView(R.layout.activity_search_dest);
        mSearchResultLv = (ListView) findViewById(R.id.lv_search_result);


    }

    private void initData() {
        mSearchResultAdapter = new ListViewDataAdapter(new ViewHolderCreator() {
            @Override
            public ViewHolderBase createViewHolder() {
                return new SearchResultViewHolder();
            }
        });
        mSearchResultLv.setAdapter(mSearchResultAdapter );
    }

    private class SearchResultViewHolder extends ViewHolderBase<CityBean>{
        private ImageView destIv;
        private TextView destNameTv;

        @Override
        public View createView(LayoutInflater layoutInflater) {
            View contentView = layoutInflater.inflate(R.layout.row_dest_search,null);
            destIv = (ImageView) contentView.findViewById(R.id.iv_dest);
            destNameTv = (TextView) contentView.findViewById(R.id.tv_dest_name);
            return contentView;
        }

        @Override
        public void showData(int position, CityBean itemData) {
            destNameTv.setText(itemData.zhName);
            ImageLoader.getInstance().displayImage(itemData.image, destIv,UILUtils.getDefaultOption());

        }
    }
}
