package com.aizou.peachtravel.module.dest.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.FrameLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.aizou.core.dialog.ToastUtil;
import com.aizou.core.http.HttpCallBack;
import com.aizou.core.log.LogUtil;
import com.aizou.core.widget.listHelper.ListViewDataAdapter;
import com.aizou.core.widget.listHelper.ViewHolderBase;
import com.aizou.core.widget.listHelper.ViewHolderCreator;
import com.aizou.core.widget.pagerIndicator.indicator.FixedIndicatorView;
import com.aizou.peachtravel.R;
import com.aizou.peachtravel.base.PeachBaseFragment;
import com.aizou.peachtravel.bean.ExpertBean;
import com.aizou.peachtravel.bean.ExpertFootprintBean;
import com.aizou.peachtravel.bean.GroupLocBean;
import com.aizou.peachtravel.bean.InDestBean;
import com.aizou.peachtravel.bean.LocBean;
import com.aizou.peachtravel.common.api.TravelApi;
import com.aizou.peachtravel.common.api.UserApi;
import com.aizou.peachtravel.common.dialog.DialogManager;
import com.aizou.peachtravel.common.gson.CommonJson4List;
import com.aizou.peachtravel.common.utils.CommonUtils;
import com.aizou.peachtravel.common.utils.PreferenceUtils;
import com.aizou.peachtravel.common.widget.FlowLayout;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.aizou.peachtravel.R.layout;

/**
 * Created by lxp_dqm07 on 2015/5/18.
 */

@SuppressLint("ValidFragment")
public class ExpertFragment extends PeachBaseFragment {

    public int pos;
    private FlowLayout fl;
    private ExpertDesAdapter expertDesAdapter;
    private ArrayList<LocBean> inLocs = new ArrayList<LocBean>();
    private ArrayList<LocBean> outLocs = new ArrayList<LocBean>();

    public ExpertFragment(int pos){
        this.pos=pos;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView= inflater.inflate(R.layout.expert_des_box,container,false);
        fl = (FlowLayout)rootView.findViewById(R.id.fl_expert_city_list);
        /*expertDesAdapter = new ExpertDesAdapter(new ViewHolderCreator() {
            @Override
            public ViewHolderBase createViewHolder() {
                return new InExpertDesHolder();
            }
        });*/
        initData();
        return rootView;
    }

    public void initData(){
        getData(pos);
    }

    public void bindView(ArrayList<LocBean> beans){
        fl.removeAllViews();
        for (int i=0;i<beans.size();i++) {
            View contentView = View.inflate(getActivity(), R.layout.des_text_style, null);
            final TextView cityNameTv = (TextView) contentView.findViewById(R.id.tv_cell_name);
            cityNameTv.setText(beans.get(i).zhName);
            final String id=beans.get(i).id;
            cityNameTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //进入达人列表
                    Intent intent = new Intent();
                    intent.putExtra("locId",id);
                    getActivity().setResult(Activity.RESULT_OK,intent);
                    getActivity().finish();
                }
            });
            fl.addView(contentView);
        }
    }


    public void getData(int position){
        boolean flag=false;
        if(position==0){
            flag=false;
        }else if(position==1){
            flag=true;
        }
        DialogManager.getInstance().showModelessLoadingDialog(getActivity());
            UserApi.searchExpertFootPrint(flag, new HttpCallBack<String>() {
                @Override
                public void doSucess(String result, String method) {

                }

                @Override
                public void doSucess(String result, String method, Header[] headers) {
                    DialogManager.getInstance().dissMissModelessLoadingDialog();
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(result);
                        JSONObject object=(JSONObject)jsonObject.get("result");
                        Map<String,ArrayList<LocBean>> res=new Map<String, ArrayList<LocBean>>() {
                            @Override
                            public void clear() {

                            }

                            @Override
                            public boolean containsKey(Object key) {
                                return false;
                            }

                            @Override
                            public boolean containsValue(Object value) {
                                return false;
                            }

                            @NonNull
                            @Override
                            public Set<Entry<String, ArrayList<LocBean>>> entrySet() {
                                return null;
                            }

                            @Override
                            public ArrayList<LocBean> get(Object key) {
                                return null;
                            }

                            @Override
                            public boolean isEmpty() {
                                return false;
                            }

                            @NonNull
                            @Override
                            public Set<String> keySet() {
                                return null;
                            }

                            @Override
                            public ArrayList<LocBean> put(String key, ArrayList<LocBean> value) {
                                return null;
                            }

                            @Override
                            public void putAll(Map<? extends String, ? extends ArrayList<LocBean>> map) {

                            }

                            @Override
                            public ArrayList<LocBean> remove(Object key) {
                                return null;
                            }

                            @Override
                            public int size() {
                                return 0;
                            }

                            @NonNull
                            @Override
                            public Collection<ArrayList<LocBean>> values() {
                                return null;
                            }
                        };
                        Iterator iterator=object.keys();
                        if(iterator.hasNext()){
                            String name=String.valueOf(iterator.next());
                            ArrayList<LocBean> list=new ArrayList<LocBean>();
                            for(int j=0;j<object.getJSONArray(name).length();j++){
                                list.add((LocBean)object.getJSONArray(name).get(j));
                            }
                            res.put(name, list);
                        }
                        if(iterator.hasNext()){
                            String key=(String)iterator.next();
                            for(int i=0;i<res.get(key).size();i++){
                                inLocs.add(res.get(key).get(i));
                            }
                        }
                        bindView(inLocs);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    CommonJson4List<ExpertFootprintBean> footPrintResult = CommonJson4List.fromJson(result, ExpertFootprintBean.class);

                    LogUtil.d("+++++++++++++++++++++++"+footPrintResult.toString());
                   // =footPrintResult;
                }

                @Override
                public void doFailure(Exception error, String msg, String method) {
                   DialogManager.getInstance().dissMissModelessLoadingDialog();
                   ToastUtil.getInstance(getActivity()).showToast(getResources().getString(R.string.request_network_failed));
//
                }
            });
    }

    private class ExpertDesAdapter extends ListViewDataAdapter<InDestBean> implements SectionIndexer {

        private List<String> sections;
        private SparseIntArray positionOfSection;
        private SparseIntArray sectionOfPosition;


        /**
         * @param viewHolderCreator The view holder creator will create a View Holder that extends {@link com.aizou.core.widget.listHelper.ViewHolderBase}
         */
        public ExpertDesAdapter(ViewHolderCreator viewHolderCreator) {

            super(viewHolderCreator);
            initSections();
        }



        @Override
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
            //initSections();
        }

        public void initSections() {
            int count = 1;//getCount();
            positionOfSection = new SparseIntArray();
            sectionOfPosition = new SparseIntArray();
            sections = new ArrayList<String>();
            int section = 0;
            for (int i = 0; i < count; i++) {
                String letter = getItem(i).section;
                String beforeLetter = "";
                if (i > 0) {
                    beforeLetter = getItem(i - 1).section;
                }
                if (letter != null && !beforeLetter.equals(letter)) {
                    section++;
                    sections.add(letter);
                    positionOfSection.put(section, i);
                }
                sectionOfPosition.put(i, section);
            }
        }

        @Override
        public Object[] getSections() {
            return sections.toArray();
        }

        @Override
        public int getPositionForSection(int section) {
            return positionOfSection.get(section);
        }

        @Override
        public int getSectionForPosition(int position) {
            return sectionOfPosition.get(position);
        }
    }


    private class InExpertDesHolder extends ViewHolderBase<InDestBean> {
        //InDestBean作为返回的数据传入
        String[] in={"北京","北京","北京","北京","北京","北京","北京"};
        String[] out={"美国","美国","美国","美国","美国","美国","美国"};


        @Override
        public View createView(LayoutInflater layoutInflater) {
            return fl;
        }

        @Override
        public void showData(int position, final InDestBean itemData) {
            fl.removeAllViews();
            //for (final LocBean bean : itemData.locList) {
                View contentView = View.inflate(getActivity(), R.layout.des_text_style, null);
                final TextView cityNameTv = (TextView) contentView.findViewById(R.id.tv_cell_name);
                if(pos==0){

                    cityNameTv.setText(in[position]);
                }else if(pos==1){
                    cityNameTv.setText(out[position]);
                }

                    //cityNameTv.setChecked(bean.isAdded);

                    /// 这一步必须要做,否则不会显示.
                    cityNameTv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //进入达人列表
                        }
                    });


                fl.addView(contentView);
           // }
        }
    }

}
