package com.xuejian.client.lxp.common.api;

import android.text.TextUtils;

import com.aizou.core.http.HttpCallBack;
import com.aizou.core.http.OkHttpClientManager;
import com.aizou.core.http.entity.PTHeader;
import com.aizou.core.http.entity.PTRequest;
import com.aizou.core.log.LogUtil;
import com.aizou.core.utils.LocalDisplay;
import com.alibaba.fastjson.JSON;
import com.xuejian.client.lxp.bean.BountiesBean;
import com.xuejian.client.lxp.bean.LocBean;
import com.xuejian.client.lxp.bean.TravellerBean;
import com.xuejian.client.lxp.common.account.AccountManager;
import com.xuejian.client.lxp.config.SystemConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TravelApi extends BaseApi {
    public static class PeachType {
        public final static String GUIDE = "guide";
        public final static String NOTE = "travelNote";
        public final static String LOC = "locality";
        public final static String SPOT = "vs";
        public final static String RESTAURANTS = "restaurant";
        public final static String SHOPPING = "shopping";
        public final static String HOTEL = "hotel";
    }

    //目的地推荐
    public final static String REC_DEST = "/recommend";
    //国内目的地列表  已修改
    public final static String IN_DESTINATIONS = "/guide/localities";
    //国外目的地列表  已修改
    public final static String OUT_DESTINATIONS = "/guide/localities";
    //目的地详情  已修改
    public final static String CITY_DETAIL = "/geo/localities/";
    //目的地图集  已修改
    public final static String CITY_GALLEY = "/geo/localities/%1$s/albums";
    //目的地美食、购物介绍
    public final static String LOC_POI_GUIDE = "/guides/locality/%1$s/%2$s";
    //poi相关
    //景点
    //public final static String SPOT_DETAIL = "/poi/vs/";
    public final static String SPOT_DETAIL = "/poi/viewspots/";
    //POI详情
    public final static String POI_DETAIL = "/poi/%1$s/";
    //POI列表
    public final static String POI_LIST_BY_LOC = "/poi/%1$s/localities/";
    //根据ID获取攻略
    public final static String GUIDEBYID = "/guides/%1$s";
    //根据目的地ID创建攻略
    //public final static String CREATE_GUIDE = "/create-guide";
    public final static String CREATE_GUIDE = "/users/%s/guides";

    //复制攻略
    public final static String COPY_GUIDE = "/copy-guide/%1$s";
    //攻略
    public final static String GUIDE = "/guides";
    //修改攻略标题
    public final static String MODIFY_GUIDE_INFO = "/guides/info/%1$s";
    //修改攻略目的地
    public final static String MODIFY_GUIDE_LOC = "/guides";

    public final static String RECOMMEND_PLAN = "/geo/localities/%s/guides";

    //收藏
    public final static String FAV = "/misc/favorites";
    //搜索
    public final static String SEARCH = "/search";

    //搜索联想
    public final static String SUGGEST = "/suggestions";
    //周边
    public final static String NEARBY = "/poi/nearby";

    //达人列表
    public final static String EXPERT_LIST = "/geo/countries";
    public final static String ANCILLARY_INFO = "/search/ancillary-info";
    public final static String RECOMMEND_LIST = "/geo/localities/recommendations";
    //poi 列表
    public final static String POI_LIST = "/poi/%s";
    //单个POI的信息
    public final static String POI_INFO = "/poi/%s/%s";
    // 单个POI的评论
    public final static String POI_COMMENTS = "/poi/%s/%s/comments";

    public final static String USERS = "/users/";

    public final static String RECOMMEND_KEYWORD = "/search/hot-queries";

    // 国家列表
    public final static String COUNTRY_LIST = "/geo/countries";

    // 城市列表
    public final static String CITY_LIST = "/geo/localities";

    // 首页专栏
    public final static String MAIN_PAGE = "/columns";

    // 首页推荐
    public final static String RECOMMEND = "/marketplace/commodities/recommendations";

    // 商品列表
    public final static String COMMODITY_LIST = "/marketplace/commodities";

    // 商品列表
    public final static String CATEGORY_LIST = "/marketplace/commodities/categories";

    // 订单创建
    public final static String CREATE_ORDER = "/marketplace/orders";

    // 商品详情
    public final static String COMMODITY_DETAIL = "/marketplace/commodities/%s";

    // 订单详情
    public final static String ORDER_DETAIL = "/marketplace/orders/%s";

    // 旅客信息
    public final static String TRAVELLER_INFO = "/users/%d/travellers";

    // 修改旅客信息
    public final static String EDIT_TRAVELLER_INFO = "/users/%d/travellers/%s";

    // 订单列表
    public final static String ORDER_LIST = "/marketplace/orders";

    // 修改订单状态
    public final static String EDIT_ORDER_STATUS = "/marketplace/orders/%d/actions";

    // 支付订单
    public final static String PAY_ORDER = "/marketplace/orders/%d/payments";

    // 商户信息
    public final static String SELLER_INFO = "/marketplace/sellers/";

    // 推荐城市
    public final static String RECOMMEND_CIRY = "/geo/localities/recommendations";

    // 发表评价
    public final static String CREATE_COMMENT = "/marketplace/commodities/%d/comments";

    // 获取评价
    public final static String COMMENT_LIST = "/marketplace/commodities/%d/comments";


    // 获取所有定制信息
    public final static String BOUNTIES = "/marketplace/bounties";


    // 获取优惠券
    public final static String COUPON_LIST = "/marketplace/coupons";

    public static void createProject
            (BountiesBean bountiesBean, HttpCallBack callback) {
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.POST);
        request.setUrl(SystemConfig.DEV_URL + BOUNTIES);
        request.setHeader(PTHeader.HEADER_CONTENT_TYPE, "application/json");

        JSONObject jsonObject = new JSONObject();

        JSONObject contact = null;
        try {
            contact = new JSONObject(JSON.toJSON(bountiesBean.getContact().get(0)).toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            jsonObject.put("departureDate", bountiesBean.getDepartureDate());
            jsonObject.put("timeCost", bountiesBean.getTimeCost());
            jsonObject.put("participantCnt", bountiesBean.getParticipantCnt());
            jsonObject.put("participants", bountiesBean.getParticipants());
            jsonObject.put("budget", bountiesBean.getBudget());
            jsonObject.put("service", bountiesBean.getService());
            jsonObject.put("topic", bountiesBean.getTopic());
            jsonObject.put("contact", contact);
            jsonObject.put("memo", bountiesBean.getMemo());
            jsonObject.put("totalPrice", bountiesBean.getTotalPrice());

            if (bountiesBean.getDestination() != null && bountiesBean.getDestination().size() > 0) {
                JSONArray array = new JSONArray();
                for (LocBean bean : bountiesBean.getDestination()) {
                    JSONObject object = new JSONObject();
                    object.put("id",bean.id);
                    object.put("zhName",bean.zhName);
                    array.put(object);
                }
                jsonObject.put("destination", array);
            }
            JSONObject departure =new JSONObject();
            departure.put("id",bountiesBean.getDeparture().get(0).id);
            departure.put("zhName",bountiesBean.getDeparture().get(0).zhName);
            jsonObject.put("departure", departure);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        System.out.println(jsonObject.toString());
        setDefaultParams(request, jsonObject.toString());
        LogUtil.d(jsonObject.toString());
        OkHttpClientManager.getInstance().request(request, jsonObject.toString(), callback);
    }
    public static void getBounties(HttpCallBack callback) {
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.GET);
        request.setUrl(SystemConfig.DEV_URL + BOUNTIES);
        setDefaultParams(request, "");
        OkHttpClientManager.getInstance().request(request, "", callback);
    }


    public static void getCouponList(long userId ,HttpCallBack callback) {
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.GET);
        request.setUrl(SystemConfig.DEV_URL + COUPON_LIST);
        request.putUrlParams("userId", String.valueOf(userId));
        setDefaultParams(request, "");
        OkHttpClientManager.getInstance().request(request, "", callback);
    }

    public static void getCommentList(long commodityId ,HttpCallBack callback) {
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.GET);
        request.setUrl(SystemConfig.DEV_URL + String.format(COMMENT_LIST, commodityId));
        setDefaultParams(request, "");
        OkHttpClientManager.getInstance().request(request, "", callback);
    }


    public static void createComment(long commodityId,long orderId,String comment,float rating,boolean anonymous,HttpCallBack callback) {
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.POST);
        request.setUrl(SystemConfig.DEV_URL + String.format(CREATE_COMMENT, commodityId));
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("contents",comment);
            jsonObject.put("rating",rating);
            jsonObject.put("anonymous",anonymous);
            jsonObject.put("orderId",orderId);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        LogUtil.d(jsonObject.toString());
        setDefaultParams(request, jsonObject.toString());
        OkHttpClientManager.getInstance().request(request, jsonObject.toString(), callback);
    }

    public static void searchCommodity(String key,HttpCallBack callback) {
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.GET);
        request.setUrl(SystemConfig.DEV_URL + COMMODITY_LIST);
        request.putUrlParams("query", key);
        setDefaultParams(request, "");
        OkHttpClientManager.getInstance().request(request, "", callback);
    }


    public static void getRecommendCity(HttpCallBack callback) {
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.GET);
        request.setUrl(SystemConfig.DEV_URL + RECOMMEND_CIRY);
        setDefaultParams(request, "");
        OkHttpClientManager.getInstance().request(request, "", callback);
    }


    public static void getSellerInfo(long userId,HttpCallBack callback) {
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.GET);
        request.setUrl(SystemConfig.DEV_URL + SELLER_INFO + String.valueOf(userId));
        setDefaultParams(request, "");
        OkHttpClientManager.getInstance().request(request, "", callback);
    }

    public static void getPrePayInfo(long orderId,String vendor,HttpCallBack callback) {
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.POST);
       request.setUrl(SystemConfig.DEV_URL + String.format(PAY_ORDER, orderId));
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("provider",vendor);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        LogUtil.d(jsonObject.toString());
        setDefaultParams(request, jsonObject.toString());
        OkHttpClientManager.getInstance().request(request, jsonObject.toString(), callback);
    }

    public static void editOrderStatus(long orderId, String action,JSONObject data, HttpCallBack callback) {
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.POST);
        request.setUrl(SystemConfig.DEV_URL + String.format(EDIT_ORDER_STATUS, orderId));
        request.setHeader(PTHeader.HEADER_CONTENT_TYPE, "application/json");

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("action", action);
            jsonObject.put("data",data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        setDefaultParams(request, jsonObject.toString());
        LogUtil.d(jsonObject.toString());
        OkHttpClientManager.getInstance().request(request, jsonObject.toString(), callback);
    }


    public static void getOrderList(long userId,String status ,String start,String count,boolean trade,HttpCallBack callback) {
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.GET);
        request.setUrl(SystemConfig.DEV_URL + ORDER_LIST);
        if (trade){
            request.putUrlParams("sellerId", String.valueOf(userId));
      //      request.putUrlParams("userId", String.valueOf(userId));
        //    request.putUrlParams("sort", "updateTime");
        }else {
            request.putUrlParams("userId", String.valueOf(userId));
        }

        if (!TextUtils.isEmpty(status)){
            request.putUrlParams("status",status);
        }
        if (!TextUtils.isEmpty(start)&&!TextUtils.isEmpty(count)) {
            request.putUrlParams("start", start);
            request.putUrlParams("count", count);
        }
        setDefaultParams(request, "");
        OkHttpClientManager.getInstance().request(request, "", callback);
    }


    public static void getTravellers(long orderId, HttpCallBack callback) {
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.GET);
        request.setUrl(SystemConfig.DEV_URL + String.format(TRAVELLER_INFO, orderId));
        setDefaultParams(request, "");
        OkHttpClientManager.getInstance().request(request, "", callback);
    }

    public static void editTraveller
            (long userId, String key,String surname, String givenName, String gender, long birthday, JSONObject idProof, JSONObject tel, String email, HttpCallBack callback) {
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.PUT);
        request.setUrl(SystemConfig.DEV_URL + String.format(EDIT_TRAVELLER_INFO, userId, key));
        request.setHeader(PTHeader.HEADER_CONTENT_TYPE, "application/json");

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("userId", userId);
            jsonObject.put("surname", surname);
            jsonObject.put("givenName", givenName);
            jsonObject.put("gender", gender);
            jsonObject.put("birthday", birthday);
            jsonObject.put("idProof", idProof);
            jsonObject.put("tel", tel);
            jsonObject.put("email", email);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        setDefaultParams(request, jsonObject.toString());
        LogUtil.d(jsonObject.toString());
        OkHttpClientManager.getInstance().request(request, jsonObject.toString(), callback);
    }



    public static void createTraveller
            (long userId, String surname, String givenName, String gender, long birthday, JSONObject idProof, JSONObject tel, String email, HttpCallBack callback) {
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.POST);
        request.setUrl(SystemConfig.DEV_URL + String.format(TRAVELLER_INFO, userId));
        request.setHeader(PTHeader.HEADER_CONTENT_TYPE, "application/json");
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("userId", userId);
            jsonObject.put("surname", surname);
            jsonObject.put("givenName", givenName);
            jsonObject.put("gender", gender);
            jsonObject.put("birthday", birthday);
            jsonObject.put("idProof", idProof);
            jsonObject.put("tel", tel);
            jsonObject.put("email", email);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        setDefaultParams(request, jsonObject.toString());
        LogUtil.d(jsonObject.toString());
        OkHttpClientManager.getInstance().request(request, jsonObject.toString(), callback);
    }



    public static void getOrderDetail(long orderId, HttpCallBack callback) {
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.GET);
        request.setUrl(SystemConfig.DEV_URL + String.format(ORDER_DETAIL, String.valueOf(orderId)));
        setDefaultParams(request, "");
        OkHttpClientManager.getInstance().request(request, "", callback);
    }


    public static void getCommodity(long commodityId,long version, HttpCallBack callback) {
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.GET);
        request.setUrl(SystemConfig.DEV_URL + String.format(COMMODITY_DETAIL, String.valueOf(commodityId)));
        if (version>0)request.putUrlParams("version",String.valueOf(version));
        setDefaultParams(request, "");
        OkHttpClientManager.getInstance().request(request, "", callback);
    }


    public static void editCommodityStatus(long commodityId,String status, HttpCallBack callback) {
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.PATCH);
        request.setUrl(SystemConfig.DEV_URL + String.format(COMMODITY_DETAIL, String.valueOf(commodityId)));
        request.setHeader(PTHeader.HEADER_CONTENT_TYPE, "application/json");

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("status", status);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        setDefaultParams(request, jsonObject.toString());
        LogUtil.d(jsonObject.toString());
        OkHttpClientManager.getInstance().request(request, jsonObject.toString(), callback);
    }

    public static void createOrder
            (long commodityId, String planId, String rendezvousTime, int quantity, JSONObject contactObject, String contactComment, ArrayList<TravellerBean> list,String couponId, HttpCallBack callback) {
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.POST);
        request.setUrl(SystemConfig.DEV_URL + CREATE_ORDER);
    //    request.setUrl("http://182.92.168.171:11219"+ CREATE_ORDER);

        request.setHeader(PTHeader.HEADER_CONTENT_TYPE, "application/json");

        JSONObject jsonObject = new JSONObject();
        JSONArray coupons = new JSONArray();
        if (!TextUtils.isEmpty(couponId))coupons.put(couponId);
        try {
            jsonObject.put("commodityId", commodityId);
            jsonObject.put("planId", planId);
            jsonObject.put("rendezvousTime", rendezvousTime);
            jsonObject.put("quantity", quantity);
            jsonObject.put("contact", contactObject);
            jsonObject.put("comment", contactComment);
            jsonObject.put("coupons", coupons);
            if (list != null && list.size() > 0) {
                JSONArray array = new JSONArray();
                for (TravellerBean bean : list) {
                    array.put(bean.getKey());
                }
                jsonObject.put("travellers", array);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        setDefaultParams(request, jsonObject.toString());
        LogUtil.d(jsonObject.toString());
        OkHttpClientManager.getInstance().request(request, jsonObject.toString(), callback);
    }


    public static void getCategoryList(String localityId, HttpCallBack callback) {
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.GET);
        request.setUrl(SystemConfig.DEV_URL + CATEGORY_LIST);
        request.putUrlParams("locality", localityId);
        setDefaultParams(request,"");
        OkHttpClientManager.getInstance().request(request, "", callback);
    }

    public static void getCommodityList(String sellerId,String status, String localityId, String category, String sortBy, String sort,String start ,String count,HttpCallBack callback) {
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.GET);
        request.setUrl(SystemConfig.DEV_URL + COMMODITY_LIST);
        if (!TextUtils.isEmpty(start)&&!TextUtils.isEmpty(count)) {
            request.putUrlParams("start", start);
            request.putUrlParams("count", count);
        }
        if (!TextUtils.isEmpty(sellerId)) request.putUrlParams("seller", sellerId);
        if (!TextUtils.isEmpty(localityId)) request.putUrlParams("locality", localityId);
        if (!TextUtils.isEmpty(category)) request.putUrlParams("category", category);
        if (!TextUtils.isEmpty(sortBy)) {
            request.putUrlParams("sortBy", sortBy);
        }
        if (!TextUtils.isEmpty(sort)) {
            request.putUrlParams("sort", sort);
        }
        if (!TextUtils.isEmpty(status)) {
            request.putUrlParams("status", status);
        }
        setDefaultParams(request,"");
        OkHttpClientManager.getInstance().request(request, "", callback);
    }

    public static void getRecommend(HttpCallBack callback) {
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.GET);
        request.setUrl(SystemConfig.DEV_URL + RECOMMEND);
        setDefaultParams(request,"");
        OkHttpClientManager.getInstance().request(request, "", callback);
    }

    public static void getMainPageColumns(long userId,HttpCallBack callback) {
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.GET);
        if (userId>0){
            request.putUrlParams("userId",String.valueOf(userId));
        }
        request.setUrl(SystemConfig.DEV_URL + MAIN_PAGE);
        setDefaultParams(request,"");
        OkHttpClientManager.getInstance().request(request, "", callback);
    }

    public static void getCountryList(String code, HttpCallBack callback) {
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.GET);
        request.setUrl(SystemConfig.DEV_URL + COUNTRY_LIST);
        request.putUrlParams("continentCode", code);
        setDefaultParams(request,"");
        OkHttpClientManager.getInstance().request(request, "", callback);
    }

    public static void getCityList(String countryId, HttpCallBack callback) {
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.GET);
        request.setUrl(SystemConfig.DEV_URL + CITY_LIST);
        request.putUrlParams("countryId", countryId);
        setDefaultParams(request,"");
        OkHttpClientManager.getInstance().request(request, "", callback);
    }

    public static void getCityInfo(String cityId, HttpCallBack callback) {
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.GET);
        request.setUrl(SystemConfig.DEV_URL + CITY_LIST + "/" + cityId);
        setDefaultParams(request,"");
        OkHttpClientManager.getInstance().request(request, "", callback);
    }

    public static void getCityDetail(String cityId, String field, HttpCallBack callback) {
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.GET);
        request.setUrl(SystemConfig.DEV_URL + CITY_LIST + "/" + cityId + "/details");
        if (!TextUtils.isEmpty(field))request.putUrlParams("field", field);
        setDefaultParams(request,"");
        OkHttpClientManager.getInstance().request(request, "", callback);
    }

    /**
     * 获取目的地推荐
     *
     * @param callback
     * @return
     */
    public static void getRecDest(HttpCallBack callback) {
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.GET);
        request.setUrl(SystemConfig.BASE_URL + REC_DEST);
        setDefaultParams(request,"");
        OkHttpClientManager.getInstance().request(request, "", callback);
        //   return HttpManager.request(request, callback);
    }

    /**
     * 获取国内目的地列表(1.0版本)
     *
     * @param callback
     * @return
     */
    @Deprecated
    public static void getInDestList(String lastModeify, HttpCallBack callback) {
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.GET);
        request.setUrl(SystemConfig.DEV_URL + IN_DESTINATIONS);
//        request.putUrlParams("groupBy","true");

        if (!TextUtils.isEmpty(lastModeify)) {
            request.addHeader("Cache-Control", "private");
            request.addHeader("If-Modified-Since", lastModeify);
        }
        setDefaultParams(request,"");
        OkHttpClientManager.getInstance().request(request, "", callback);
        //  return HttpManager.request(request, callback);
    }

    /**
     * 获取国内目的地列表
     *
     * @param callback
     * @return
     */
    public static void getInDestListByGroup(String lastModeify, HttpCallBack callback) {
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.GET);
        request.setUrl(SystemConfig.DEV_URL + IN_DESTINATIONS);
        request.putUrlParams("abroad", "false");
        request.putUrlParams("groupBy", "true");
        request.putUrlParams("imgWidth", LocalDisplay.dp2px(150) + "");

        if (!TextUtils.isEmpty(lastModeify)) {
            request.addHeader("Cache-Control", "private");
            request.addHeader("If-Modified-Since", lastModeify);
        }
        setDefaultParams(request,"");
        OkHttpClientManager.getInstance().request(request, "", callback);
        //   return HttpManager.request(request, callback);
    }

    public static void getAncillaryInfo(String type, String keyword, HttpCallBack callback) {
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.GET);
        request.setUrl(SystemConfig.DEV_URL + ANCILLARY_INFO);
        request.putUrlParams("query", keyword);
        request.putUrlParams("scope", type);
        setDefaultParams(request,"");
        OkHttpClientManager.getInstance().request(request, "", callback);
    }

    /**
     * 获取国外目的地列表
     *
     * @param callback
     * @return
     */
    public static void getOutDestList(String lastModeify, HttpCallBack callback) {
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.GET);
        request.setUrl(SystemConfig.DEV_URL + OUT_DESTINATIONS);
        request.putUrlParams("abroad", "true");
        request.putUrlParams("groupBy", "true");
        request.putUrlParams("imgWidth", LocalDisplay.dp2px(150) + "");

        if (!TextUtils.isEmpty(lastModeify)) {
            request.addHeader("Cache-Control", "private");
            request.addHeader("If-Modified-Since", lastModeify);
        }
        setDefaultParams(request,"");
        OkHttpClientManager.getInstance().request(request, "", callback);
        //   return HttpManager.request(request, callback);
    }

    /**
     * 获取目的地美食，购物介绍
     *
     * @param callback
     * @return
     */
    public static void getDestPoiGuide(String locId, String type, HttpCallBack callback) {
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.GET);
        request.setUrl(SystemConfig.BASE_URL + String.format(LOC_POI_GUIDE, locId, type));
        setDefaultParams(request,"");
        OkHttpClientManager.getInstance().request(request, "", callback);
        //  return HttpManager.request(request, callback);
    }

    public static void getRecommendPlan(String locId, HttpCallBack callback) {
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.GET);
        request.setUrl(SystemConfig.DEV_URL + String.format(RECOMMEND_PLAN, locId));
        setDefaultParams(request,"");
        OkHttpClientManager.getInstance().request(request, "", callback);
        //  return HttpManager.request(request, callback);
    }

    /**
     * 获取目的地详情
     *
     * @param callback
     * @return
     */
    public static void getCityDetail(String id, int imgWidth, HttpCallBack callback) {
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.GET);
        request.setUrl(SystemConfig.DEV_URL + CITY_DETAIL + id);
        request.putUrlParams("noteCnt", "3");
        request.putUrlParams("imgWidth", imgWidth + "");
        setDefaultParams(request,"");
        OkHttpClientManager.getInstance().request(request, "", callback);
        //  return HttpManager.request(request, callback);
    }

    /**
     * 获取目的地图集
     *
     * @param callback
     * @return
     */
    public static void getCityGalley(String id, HttpCallBack callback) {
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.GET);
        request.setUrl(SystemConfig.DEV_URL + String.format(CITY_GALLEY, id));
        request.putUrlParams("imgWidth", (int) (LocalDisplay.SCREEN_HEIGHT_PIXELS / 3 / 1.5) + "");
        request.putUrlParams("page", String.valueOf(0));
        request.putUrlParams("pageSize", String.valueOf(1000));
        setDefaultParams(request,"");
        OkHttpClientManager.getInstance().request(request, "", callback);
        // return HttpManager.request(request, callback);
    }

    public static void getRecommendKeywords(String type, HttpCallBack callback) {
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.GET);
        request.setUrl(SystemConfig.DEV_URL + RECOMMEND_KEYWORD);
        if (!TextUtils.isEmpty(type)) {
            if (type.equals("vs")) type = "viewspot";
            if (type.equals("loc")) type = "locality";
            request.putUrlParams("scope", type);
        }
        setDefaultParams(request,"");
        OkHttpClientManager.getInstance().request(request, "", callback);
    }

    /**
     * 获取景点详情
     *
     * @param callback
     * @return
     */
    public static void getSpotDetail(String id, HttpCallBack callback) {
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.GET);
        request.setUrl(SystemConfig.DEV_URL + SPOT_DETAIL + id);
        setDefaultParams(request,"");
        OkHttpClientManager.getInstance().request(request, "", callback);
        //  return HttpManager.request(request, callback);
    }

    /**
     * 获取POI列表
     *
     * @param callback
     * @return
     */
    public static void getPoiListByLoc(String type, String id, int page, HttpCallBack callback) {
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.GET);
        //  request.setUrl(SystemConfig.BASE_URL + String.format(POI_LIST_BY_LOC, type) + id);

        if (type.equals("vs")) type = "viewspots";
        if (type.equals("restaurant")) type = type + "s";
        request.setUrl(SystemConfig.DEV_URL + String.format(POI_LIST, type));
        request.putUrlParams("locality", id);


        request.putUrlParams("page", String.valueOf(page));
        request.putUrlParams("pageSize", String.valueOf(PAGE_SIZE));
        request.putUrlParams("imgWidth", LocalDisplay.dp2px(150) + "");
        setDefaultParams(request,"");
        OkHttpClientManager.getInstance().request(request, "", callback);
        //  return HttpManager.request(request, callback);
    }

    /**
     * 获取POI详情
     *
     * @param callback
     * @return
     */
    public static void getPoiDetail(String type, String id, HttpCallBack callback) {
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.GET);

        if (type.equals("vs")) type = "viewspots";
        if (type.equals("restaurant")) type = type + "s";

        request.setUrl(SystemConfig.DEV_URL + String.format(POI_DETAIL, type) + id);
        setDefaultParams(request,"");
        OkHttpClientManager.getInstance().request(request, "", callback);
        //   return HttpManager.request(request, callback);
    }


    /**
     * 根据攻略ID获取攻略详情
     *
     * @param callback
     * @return change to api-dev
     */
    public static void getGuideDetail(String userId, String id, HttpCallBack callback) {
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.GET);
        request.setUrl(SystemConfig.DEV_URL + USERS + userId + String.format(GUIDEBYID, id));
        request.putUrlParams("imgWidth", LocalDisplay.dp2px(100) + "");
        setDefaultParams(request,"");
        OkHttpClientManager.getInstance().request(request, "", callback);
        //   return HttpManager.request(request, callback);
    }

    /**
     * 新建攻略
     *
     * @param locList
     * @param callback
     * @return change to api_dev
     */
    public static void createGuide
    (String action, List<String> locList, boolean recommend, HttpCallBack callback) {
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.POST);
        request.setUrl(SystemConfig.DEV_URL + String.format(CREATE_GUIDE, AccountManager.getCurrentUserId()));
        request.putUrlParams("imgWidth", LocalDisplay.dp2px(100) + "");
        request.setHeader(PTHeader.HEADER_CONTENT_TYPE, "application/json");

        JSONObject jsonObject = new JSONObject();
        try {
            JSONArray jsonArray = new JSONArray();
            for (String locId : locList) {
                jsonArray.put(locId);
            }
            jsonObject.put("locId", jsonArray);
            //      if (recommend) {
            jsonObject.put("initViewSpots", recommend);
            //        } else {
            jsonObject.put("action", action);
            //       }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        setDefaultParams(request, jsonObject.toString());
        LogUtil.d(jsonObject.toString());
        OkHttpClientManager.getInstance().request(request, jsonObject.toString(), callback);
        //  return HttpManager.request(request, callback);
    }

    /**
     * 保存攻略
     *
     * @param guideJson
     * @param callback
     * @return
     */
    public static void saveGuide
    (String id, String guideJson, HttpCallBack callback) {
        PTRequest request = new PTRequest();

        request.setHttpMethod(PTRequest.PUT);
        request.setUrl(SystemConfig.DEV_URL + String.format(CREATE_GUIDE, AccountManager.getCurrentUserId()) + "/" + id);
        request.setHeader(PTHeader.HEADER_CONTENT_TYPE, "application/json");



//        request.setHttpMethod(PTRequest.POST);
//        request.setUrl(SystemConfig.DEV_URL+ GUIDE);
//        request.putUrlParams("id", id);
//        request.setHeader(PTHeader.HEADER_CONTENT_TYPE, "application/json");
//        setDefaultParams(request);
//        try {
//            StringEntity entity = new StringEntity(guideJson, "utf-8");
//            request.setBodyEntity(entity);
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
        setDefaultParams(request,guideJson);
        LogUtil.d(guideJson);
        OkHttpClientManager.getInstance().request(request, guideJson, callback);
        //   return HttpManager.request(request, callback);
    }

    /**
     * 获取攻略列表
     *
     * @param callback
     * @return change to api-dev
     */
    public static void getStrategyPlannedList(String userId, int page, String status, HttpCallBack callback) {
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.GET);
        request.setUrl(SystemConfig.DEV_URL + USERS + userId + GUIDE);
        request.putUrlParams("page", String.valueOf(page));
        request.putUrlParams("pageSize", String.valueOf(PAGE_SIZE));
        if (!TextUtils.isEmpty(status)) {
            request.putUrlParams("status", status);
        }
        setDefaultParams(request,"");
        OkHttpClientManager.getInstance().request(request, "", callback);
        //   return HttpManager.request(request, callback);
    }

    /**
     * 复制攻略
     *
     * @param callback
     * @return change to api-dev
     */
    public static void copyStrategy(long userId, String id, HttpCallBack callback) {
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.POST);
        request.setUrl(SystemConfig.DEV_URL + String.format(CREATE_GUIDE, AccountManager.getCurrentUserId()));
        request.setHeader(PTHeader.HEADER_CONTENT_TYPE, "application/json");
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("guideId", id);
            jsonObject.put("action", "fork");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        setDefaultParams(request, jsonObject.toString());
        LogUtil.d(jsonObject.toString());
        OkHttpClientManager.getInstance().request(request, jsonObject.toString(), callback);
        //   return HttpManager.request(request, callback);
    }

    /**
     * 删除攻略
     *
     * @param id
     * @param callBack
     * @return
     */
    public static void deleteStrategy(String id, HttpCallBack callBack) {
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.DELETE);
        request.setUrl(SystemConfig.DEV_URL + GUIDE + "/" + id);
        setDefaultParams(request,"");
        //  return HttpManager.request(request, callBack);
        OkHttpClientManager.getInstance().request(request, "", callBack);
    }

    /**
     * 修改攻略名称
     *
     * @param id
     * @param callback
     * @return
     */
    public static void modifyGuideTitle
    (String id, String title, HttpCallBack callback) {
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.PATCH);
        request.setUrl(SystemConfig.DEV_URL + String.format(CREATE_GUIDE, AccountManager.getCurrentUserId()) + "/" + id);
        request.setHeader(PTHeader.HEADER_CONTENT_TYPE, "application/json");

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("title", title);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        LogUtil.d(jsonObject.toString());
        setDefaultParams(request, jsonObject.toString());
        OkHttpClientManager.getInstance().request(request, jsonObject.toString(), callback);
        // return HttpManager.request(request, callback);
    }

    /**
     * 攻略置顶
     *
     * @param id
     * @param callback
     * @return
     */
    public static void modifyGuideTop
    (String id, Long time, HttpCallBack callback) {
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.POST);
        request.setUrl(SystemConfig.DEV_URL + GUIDE);
        request.setHeader(PTHeader.HEADER_CONTENT_TYPE, "application/json");

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", id);
            jsonObject.put("updateTime", time);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        LogUtil.d(jsonObject.toString());
        setDefaultParams(request, jsonObject.toString());
        OkHttpClientManager.getInstance().request(request, jsonObject.toString(), callback);
        //  return HttpManager.request(request, callback);
    }


    /**
     * 攻略去过
     *
     * @param id
     * @param callback
     * @return change to api-dev
     */
    public static void modifyGuideVisited
    (String id, String status, HttpCallBack callback) {
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.PATCH);
        request.setUrl(SystemConfig.DEV_URL + String.format(CREATE_GUIDE, AccountManager.getCurrentUserId()) + "/" + id);
        request.setHeader(PTHeader.HEADER_CONTENT_TYPE, "application/json");

        JSONObject jsonObject = new JSONObject();
        try {
            //   jsonObject.put("id", id);
            jsonObject.put("status", status);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        LogUtil.d(jsonObject.toString());
        setDefaultParams(request, jsonObject.toString());
        //   return HttpManager.request(request, callback);
        OkHttpClientManager.getInstance().request(request, jsonObject.toString(), callback);
    }


    /**
     * 修改攻略目的地
     *
     * @param id
     * @param callback
     * @return
     */
    public static void modifyGuideLoc
    (String id, List<LocBean> locList, HttpCallBack callback) {
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.PATCH);
        request.setUrl(SystemConfig.DEV_URL + String.format(CREATE_GUIDE, AccountManager.getCurrentUserId()) + "/" + id);
        request.setHeader(PTHeader.HEADER_CONTENT_TYPE, "application/json");

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", id);
            JSONArray locArray = new JSONArray();
            JSONObject locObject;
            for (LocBean loc : locList) {
                locObject = new JSONObject();
                locObject.put("id", loc.id);
                locObject.put("zhName", loc.zhName);
                if (TextUtils.isEmpty(loc.enName)) {
                    locObject.put("enName", "");
                } else locObject.put("enName", loc.enName);
                locArray.put(locObject);
            }
            jsonObject.put("localities", locArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

//        request.setHttpMethod(PTRequest.POST);
//        request.setUrl(SystemConfig.DEV_URL + MODIFY_GUIDE_LOC);
//        request.setHeader(PTHeader.HEADER_CONTENT_TYPE, "application/json");
//        setDefaultParams(request);
//        JSONObject jsonObject = new JSONObject();
//        try {
//            jsonObject.put("id", id);
//            JSONArray locArray = new JSONArray();
//            JSONObject locObject;
//            for (LocBean loc : locList) {
//                locObject = new JSONObject();
//                locObject.put("id", loc.id);
//                locObject.put("zhName", loc.zhName);
//                locObject.put("enName", loc.enName);
//                locArray.put(locObject);
//            }
//            jsonObject.put("localities", locArray);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        try {
//            StringEntity entity = new StringEntity(jsonObject.toString(), "utf-8");
//            request.setBodyEntity(entity);
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
        LogUtil.d(jsonObject.toString());
        setDefaultParams(request, jsonObject.toString());
        // return HttpManager.request(request, callback);
        OkHttpClientManager.getInstance().request(request, jsonObject.toString(), callback);
    }

    /**
     * 获取收藏列表
     *
     * @param page
     * @param callback
     * @return
     */
    public static void getFavist(int page, HttpCallBack callback) {
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.GET);
        request.setUrl(SystemConfig.BASE_URL + FAV);
        request.putUrlParams("page", page + "");
        request.putUrlParams("pageSize", PAGE_SIZE + "");
        request.putUrlParams("imgWidth", LocalDisplay.dp2px(100) + "");
        setDefaultParams(request,"");
        //  return HttpManager.request(request, callback);
        OkHttpClientManager.getInstance().request(request, "", callback);
    }

    /**
     * 删除收藏
     *
     * @param id
     * @param callBack
     * @return
     */
    public static void deleteFav(String id, HttpCallBack callBack) {
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.DELETE);
        request.setUrl(SystemConfig.BASE_URL + FAV + "/" + id);
        setDefaultParams(request,"");
        //   return HttpManager.request(request, callBack);
        OkHttpClientManager.getInstance().request(request, "", callBack);
    }

    //目的地查询
    public static void searchLoc(String keyword, int page, HttpCallBack callback) {
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.GET);
        request.setUrl(SystemConfig.BASE_URL + SEARCH);
        request.putUrlParams("keyword", keyword);
        request.putUrlParams("loc", "true");
        request.putUrlParams("page", String.valueOf(page));
        request.putUrlParams("pageSize", String.valueOf(PAGE_SIZE));
        setDefaultParams(request,"");
        //  return HttpManager.request(request, callback);
        OkHttpClientManager.getInstance().request(request, "", callback);
    }

    //目的地联想
    public static void suggestLoc(String keyword, HttpCallBack callback) {
        suggestForType(keyword, "loc", callback);
    }

    //联合查询
    public static void searchAll(String keyword, String loc, String vs, String hotel, String restaurant, String shopping, HttpCallBack callback) {
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.GET);
        request.setUrl(SystemConfig.BASE_URL + SEARCH);
        request.putUrlParams("keyword", keyword);
        request.putUrlParams("loc", loc);
        request.putUrlParams("vs", vs);
        request.putUrlParams("hotel", hotel);
        request.putUrlParams("restaurant", restaurant);
        request.putUrlParams("shopping", shopping);
        request.putUrlParams("imgWidth", LocalDisplay.dp2px(50) + "");
        setDefaultParams(request,"");
        //    return HttpManager.request(request, callback);
        OkHttpClientManager.getInstance().request(request, "", callback);
    }


    //搜索地点
    public static void searchForType(String keyword, String type, String locId, int page, HttpCallBack callback) {
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.GET);
        request.setUrl(SystemConfig.BASE_URL + SEARCH);
        request.putUrlParams("keyword", keyword);
        request.putUrlParams(type, "true");
        request.putUrlParams("locId", locId);
        request.putUrlParams("page", String.valueOf(page));
        request.putUrlParams("pageSize", String.valueOf(PAGE_SIZE));
        request.putUrlParams("imgWidth", LocalDisplay.dp2px(100) + "");
        setDefaultParams(request,"");
        //  return HttpManager.request(request, callback);
        OkHttpClientManager.getInstance().request(request, "", callback);
    }

    //联想地点
    public static void suggestForType(String keyword, String type, HttpCallBack callback) {
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.GET);
        request.setUrl(SystemConfig.BASE_URL + SUGGEST);
        request.putUrlParams("keyword", keyword);
        request.putUrlParams(type, "true");
        request.putUrlParams("imgWidth", LocalDisplay.dp2px(50) + "");
        setDefaultParams(request,"");
        //     return HttpManager.request(request, callback);
        OkHttpClientManager.getInstance().request(request, "", callback);
    }

    //获取周边
    public static void getNearbyPoi(double lat, double lng, int page, String type, HttpCallBack callback) {
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.GET);
        request.setUrl(SystemConfig.BASE_URL + NEARBY);
        request.putUrlParams("lat", String.valueOf(lat));
        request.putUrlParams("lng", String.valueOf(lng));
        request.putUrlParams(type, "true");
        request.putUrlParams("page", String.valueOf(page));
        request.putUrlParams("pageSize", String.valueOf(PAGE_SIZE));
        request.putUrlParams("imgWidth", LocalDisplay.dp2px(100) + "");
        setDefaultParams(request,"");
        // return HttpManager.request(request, callback);
        OkHttpClientManager.getInstance().request(request, "", callback);
    }

    //达人列表
    public static void getExpertList(HttpCallBack callback) {
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.GET);
        request.setUrl(SystemConfig.DEV_URL + EXPERT_LIST);
        setDefaultParams(request,"");
        // return HttpManager.request(request, callback);
        OkHttpClientManager.getInstance().request(request, "", callback);
    }


    public static void getRecomendCountry(HttpCallBack callback, String isAbroad) {
        PTRequest request = new PTRequest();
        request.setHttpMethod(PTRequest.GET);
        request.setUrl(SystemConfig.DEV_URL + RECOMMEND_LIST);
        request.putUrlParams("abroad", isAbroad);
        setDefaultParams(request,"");
        // return HttpManager.request(request, callback);
        OkHttpClientManager.getInstance().request(request, "", callback);
    }
}
