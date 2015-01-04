package com.aizou.peachtravel.common.utils;

import android.app.Activity;
import android.content.Intent;

import com.aizou.peachtravel.common.api.TravelApi;
import com.aizou.peachtravel.module.dest.CityDetailActivity;
import com.aizou.peachtravel.module.dest.PoiDetailActivity;
import com.aizou.peachtravel.module.dest.SpotDetailActivity;

/**
 * Created by Rjm on 2014/12/29.
 */
public class IntentUtils {

    public static void intentToDetail(Activity act,String type,String id){
        if(type.equals(TravelApi.PeachType.LOC)){
            Intent intent = new Intent(act, CityDetailActivity.class);
            intent.putExtra("id",id);
            act.startActivity(intent);
        }else if(type.equals(TravelApi.PeachType.SPOT)){
            Intent intent = new Intent(act, SpotDetailActivity.class);
            intent.putExtra("id",id);
            act.startActivity(intent);
        }else if(type.equals(TravelApi.PeachType.SHOPPING)||type.equals(TravelApi.PeachType.RESTAURANTS)||type.equals(TravelApi.PeachType.HOTEL)){
            Intent intent = new Intent(act, PoiDetailActivity.class);
            intent.putExtra("type",type);
            intent.putExtra("id",id);
            act.startActivity(intent);
        }
    }
}