package com.aizou.peachtravel.common.utils;

import android.app.Activity;
import android.content.Intent;

import com.aizou.peachtravel.R;
import com.aizou.peachtravel.base.BaseActivity;
import com.aizou.peachtravel.bean.ImageBean;
import com.aizou.peachtravel.bean.TravelNoteBean;
import com.aizou.peachtravel.common.api.TravelApi;
import com.aizou.peachtravel.module.dest.CityDetailActivity;
import com.aizou.peachtravel.module.dest.PicPagerActivity;
import com.aizou.peachtravel.module.dest.PicPagerActivity2;
import com.aizou.peachtravel.module.dest.PoiDetailActivity;
import com.aizou.peachtravel.module.dest.SpotDetailActivity;
import com.aizou.peachtravel.module.dest.TravelNoteDetailActivity;

import java.util.ArrayList;
import java.util.List;

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
            ((BaseActivity) act).startActivityWithNoAnim(intent);
            act.overridePendingTransition(0, R.anim.fade_in);
        } else if (type.equals(TravelApi.PeachType.SHOPPING) || type.equals(TravelApi.PeachType.RESTAURANTS) || type.equals(TravelApi.PeachType.HOTEL)) {
            Intent intent = new Intent(act, PoiDetailActivity.class);
            intent.putExtra("type", type);
            intent.putExtra("id", id);
            ((BaseActivity) act).startActivityWithNoAnim(intent);
            act.overridePendingTransition(0, R.anim.fade_in);
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
