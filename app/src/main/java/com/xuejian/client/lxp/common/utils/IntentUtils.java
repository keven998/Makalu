package com.xuejian.client.lxp.common.utils;

import android.app.Activity;
import android.content.Intent;

import com.xuejian.client.lxp.R;
import com.xuejian.client.lxp.base.BaseActivity;
import com.xuejian.client.lxp.bean.ImageBean;
import com.xuejian.client.lxp.bean.TravelNoteBean;
import com.xuejian.client.lxp.common.api.TravelApi;
import com.xuejian.client.lxp.module.dest.CityDetailActivity;
import com.xuejian.client.lxp.module.dest.PicPagerActivity;
import com.xuejian.client.lxp.module.dest.PicPagerActivity2;
import com.xuejian.client.lxp.module.dest.PoiDetailActivity;
import com.xuejian.client.lxp.module.dest.SpotDetailActivity;
import com.xuejian.client.lxp.module.dest.TravelNoteDetailActivity;

import java.util.ArrayList;

/**
 * Created by Rjm on 2014/12/29.
 */
public class IntentUtils {

    public static void intentToDetail(Activity act, String type, String id) {
        if (type.equals(TravelApi.PeachType.LOC)) {
            Intent intent = new Intent(act, CityDetailActivity.class);
            intent.putExtra("id", id);
            act.startActivity(intent);
        } else if (type.equals(TravelApi.PeachType.SPOT)) {
            Intent intent = new Intent(act, SpotDetailActivity.class);
            intent.putExtra("id", id);
            act.startActivity(intent);
        } else if (type.equals(TravelApi.PeachType.SHOPPING) || type.equals(TravelApi.PeachType.RESTAURANTS) || type.equals(TravelApi.PeachType.HOTEL)) {
            Intent intent = new Intent(act, PoiDetailActivity.class);
            intent.putExtra("type", type);
            intent.putExtra("id", id);
            act.startActivity(intent);
        } else if (type.equals(TravelApi.PeachType.LOC)) {
            Intent intent = new Intent();
            intent.setClass(act, CityDetailActivity.class);
            intent.putExtra("id", id);
            act.startActivity(intent);
        }
    }

    public static void intentToNoteDetail(Activity act, TravelNoteBean bean) {
        Intent intent = new Intent();
        intent.setClass(act, TravelNoteDetailActivity.class);
        intent.putExtra("id", bean.id);
        intent.putExtra("travelNote", bean);
        act.startActivity(intent);
    }


    public static void intentToPicGallery(Activity act, ArrayList<ImageBean> imageBeanList, int pos) {
        if(imageBeanList!=null&&imageBeanList.size()>0){
            Intent intent = new Intent(act, PicPagerActivity.class);
            intent.putParcelableArrayListExtra("imageUrlList", imageBeanList);
            intent.putExtra("pos", pos);
            act.startActivity(intent);
            act.overridePendingTransition(0, R.anim.fade_in);
        }

    }


    public static void intentToPicGallery2(Activity act, ArrayList<String> urls, int pos) {

            Intent intent = new Intent(act, PicPagerActivity2.class);
            intent.putStringArrayListExtra("imageStringUrlList", urls);
            intent.putExtra("pos", pos);
            act.startActivity(intent);
            act.overridePendingTransition(0, R.anim.fade_in);


    }


}
