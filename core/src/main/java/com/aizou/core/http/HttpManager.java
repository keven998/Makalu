package com.aizou.core.http;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import com.aizou.core.base.BaseApplication;
import com.aizou.core.constant.LibConfig;
import com.aizou.core.http.entity.PTRequest;
import com.aizou.core.http.entity.PTRequestHandler;
import com.aizou.core.http.parser.IReponseParser;
import com.aizou.core.log.LogUtil;
import com.aizou.core.utils.SDcardLogUtil;
import com.aizou.core.utils.StringUtil;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.ResponseStream;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.conn.ssl.SSLSocketFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 通信管理类 包含线程池管理，支持断点续传
 *
 * @author xby
 */
public class HttpManager {
    private static final String TAG = "httplog";
    private static HashMap<String, HttpHandler> downloadsMap = new HashMap<String, HttpHandler>();
    public static final int PWD_ERROR = 401;
    public static final int PERMISSION_ERROR = 403;
    public static final int RESOURSE_CONFLICT = 409;
    public static final int PARAMETER_ERROR = 422;
    private static final int TIMEOUT_SECOND =1000000;

    private static void requestFilter(Map<String, ? extends Object> map) {
        for (Entry entryMap : map.entrySet()) {
            if (entryMap.getValue() instanceof String
                    && entryMap.getValue() == null) {
                entryMap.setValue("");
            } else if (entryMap.getValue() instanceof Map) {
                requestFilter(map);
            } else if (entryMap.getValue() instanceof List) {
                List list = (List) entryMap.getValue();
                for (Object o : list) {
                    if (o instanceof Map) {
                        Map oMap = (Map) o;
                        requestFilter(oMap);
                    }
                }
            }
        }
    }

    /**
     * 描述：普通http请求
     *
     * @param request  请求实体
     * @param callBack 回调对象 调用实例： ITLRequestData itlRequestData = new
     *                 ITLRequestData(); itlRequestData.setUrl("/login.html");
     *                 itlRequestData.putParam("testkey", "testvalue");
     *                 HttpManager.requestPost(itlRequestData, this);
     */
    public static  PTRequestHandler request(final PTRequest request,
                                               final HttpCallBack callBack) {
        try {
            final String url = request.getRequest().readUrl();
            HttpUtils httpUtils = new HttpUtils();
            if(request.getRequest().readUrl().startsWith("https")){
                httpUtils.configSSLSocketFactory(SSLSocketFactory.getSocketFactory());
            }
            RequestParams requestParams = new RequestParams();
            StringBuilder sb = new StringBuilder();
            sb.append(url);
            int i=0;
            for(NameValuePair nv:request.getUrlParams()){
                if(i==0){
                    sb.append("?"+nv.getName()+"="+nv.getValue());
                }else{
                    sb.append("&"+nv.getName()+"="+nv.getValue());
                }
                i++;
            }
            LogUtil.d(TAG, "requestUrl = " + sb.toString());
            List<NameValuePair> list = new ArrayList<NameValuePair>();
            requestParams.addHeaders(request.getPTHeader().headers);
            requestParams.setHeaders(request.getPTHeader().overwirdeHeaders);
            requestParams.addQueryStringParameter(request.getRequest().getUrlParams());
            requestParams.addBodyParameter(request.getRequest().getBodyParams());
            HttpEntity entity = request.getRequest().getBodyEntity();
            if (entity != null) {
                requestParams.setBodyEntity(entity);
            }

            RequestCallBack<String> ajaxCallBack = new RequestCallBack<String>() {

                @Override
                public void onSuccess(ResponseInfo<String> responseInfo) {
                    String result = responseInfo.result;
                    LogUtil.d(TAG, "返回结果数据=" + result);
                    IReponseParser parser = request.getParser();
                    try {
                        if (LibConfig.LOGSAVE_SDCARD) {// 保存日志到sd卡
                            SDcardLogUtil.saveLog(result,
                                    url);
                        }
                        if (callBack != null) {
                            if (!callBack.httpCallBackPreFilter(result,
                                    url)) {// 拦截过滤
                                callBack.doSuccess(result, url);
                                callBack.doSuccess(result, url, responseInfo.getAllHeaders());

                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onFailure(HttpException error, String msg) {
//					DialogManager.getInstance().dissMissProgressDialog();
                    int code =  error.getExceptionCode();
                    LogUtil.e(TAG, "error Code = " + code
                            + "error msg= " + msg);
                    if (callBack != null) {
                        callBack.doFailure(error, msg,
                                url);
                        callBack.doFailure(error, msg,
                                url,code);
                    }
                }

                @Override
                public void onStart() {
                    super.onStart();
                    if (callBack != null) {
                       callBack.onStart();
                    }
                }
            };

            HttpRequest.HttpMethod httpMethod = HttpRequest.HttpMethod.GET;
            if (request.getHttpMethod().equals(PTRequest.GET)) {
                httpMethod = HttpRequest.HttpMethod.GET;
            } else if (request.getHttpMethod().equals(PTRequest.POST)) {
                httpMethod = HttpRequest.HttpMethod.POST;
            } else if (request.getHttpMethod().equals(PTRequest.PUT)) {
                httpMethod = HttpRequest.HttpMethod.PUT;
            } else if (request.getHttpMethod().equals(PTRequest.DELETE)) {
                httpMethod = HttpRequest.HttpMethod.DELETE;
            } else if (request.getHttpMethod().equals(PTRequest.CONNECT)) {
                httpMethod = HttpRequest.HttpMethod.CONNECT;
            } else if (request.getHttpMethod().equals(PTRequest.COPY)) {
                httpMethod = HttpRequest.HttpMethod.COPY;
            } else if (request.getHttpMethod().equals(PTRequest.HEAD)) {
                httpMethod = HttpRequest.HttpMethod.HEAD;
            } else if (request.getHttpMethod().equals(PTRequest.MOVE)) {
                httpMethod = HttpRequest.HttpMethod.MOVE;
            } else if (request.getHttpMethod().equals(PTRequest.OPTIONS)) {
                httpMethod = HttpRequest.HttpMethod.OPTIONS;
            } else if (request.getHttpMethod().equals(PTRequest.TRACE)) {
                httpMethod = HttpRequest.HttpMethod.TRACE;
            }
            httpUtils.configCurrentHttpCacheExpiry(1000 * 1);
            httpUtils.configTimeout(1000 * TIMEOUT_SECOND);
            HttpHandler handler = httpUtils.send(httpMethod, url, requestParams,
                    ajaxCallBack);
            PTRequestHandler ptHandler = new PTRequestHandler();
            ptHandler.setHandler(handler);
            return ptHandler;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static   String aysnRequest(final PTRequest request ) throws HttpException {
        try {
            final String url = request.getRequest().readUrl();
            HttpUtils httpUtils = new HttpUtils();
            if(request.getRequest().readUrl().startsWith("https")){
                httpUtils.configSSLSocketFactory(SSLSocketFactory.getSocketFactory());
            }
            RequestParams requestParams = new RequestParams();
            StringBuffer sb = new StringBuffer();
            sb.append(url);
            int i=0;
            for(NameValuePair nv:request.getUrlParams()){
                if(i==0){
                    sb.append("?"+nv.getName()+"="+nv.getValue());
                }else{
                    sb.append("&"+nv.getName()+"="+nv.getValue());
                }
                i++;
            }
            LogUtil.d(TAG, "requestUrl = " + sb.toString());
            List<NameValuePair> list = new ArrayList<NameValuePair>();
            requestParams.addHeaders(request.getPTHeader().headers);
            requestParams.setHeaders(request.getPTHeader().overwirdeHeaders);
            requestParams.addQueryStringParameter(request.getRequest().getUrlParams());
            requestParams.addBodyParameter(request.getRequest().getBodyParams());
            HttpEntity entity = request.getRequest().getBodyEntity();
            if (entity != null) {
                requestParams.setBodyEntity(entity);
            }


            HttpRequest.HttpMethod httpMethod = HttpRequest.HttpMethod.GET;
            if (request.getHttpMethod().equals(PTRequest.GET)) {
                httpMethod = HttpRequest.HttpMethod.GET;
            } else if (request.getHttpMethod().equals(PTRequest.POST)) {
                httpMethod = HttpRequest.HttpMethod.POST;
            } else if (request.getHttpMethod().equals(PTRequest.PUT)) {
                httpMethod = HttpRequest.HttpMethod.PUT;
            } else if (request.getHttpMethod().equals(PTRequest.DELETE)) {
                httpMethod = HttpRequest.HttpMethod.DELETE;
            } else if (request.getHttpMethod().equals(PTRequest.CONNECT)) {
                httpMethod = HttpRequest.HttpMethod.CONNECT;
            } else if (request.getHttpMethod().equals(PTRequest.COPY)) {
                httpMethod = HttpRequest.HttpMethod.COPY;
            } else if (request.getHttpMethod().equals(PTRequest.HEAD)) {
                httpMethod = HttpRequest.HttpMethod.HEAD;
            } else if (request.getHttpMethod().equals(PTRequest.MOVE)) {
                httpMethod = HttpRequest.HttpMethod.MOVE;
            } else if (request.getHttpMethod().equals(PTRequest.OPTIONS)) {
                httpMethod = HttpRequest.HttpMethod.OPTIONS;
            } else if (request.getHttpMethod().equals(PTRequest.TRACE)) {
                httpMethod = HttpRequest.HttpMethod.TRACE;
            }
            httpUtils.configCurrentHttpCacheExpiry(1000 * 1);
            ResponseStream responseStream= httpUtils.sendSync(httpMethod, url, requestParams
                    );
            String str = StringUtil.stream2String(responseStream.getBaseStream());
            LogUtil.d(TAG, "返回结果数据=" + str);
            return str;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }






    /**
     * 描述:下载文件
     *
     * @param context  上下文
     * @param url      下载项
     * @param callBack 下载的回调
     */
    public static void downloadFile(final Context context, final String url,
                                    final String savePath, final HttpCallBack callBack) {
        String sdCardPath = Environment.getExternalStorageDirectory()
                .getAbsolutePath();
        File file = new File(savePath);
        if (!file.exists()) {
            try {
                file.mkdirs();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        HttpUtils httpUtils = new HttpUtils();
        try {
            HttpHandler handler = httpUtils.download(url, savePath, true, // 如果目标文件存在，接着未完成的部分继续下载。
                    false, // 如果从请求返回信息中获取到文件名，下载完成后自动重命名。
                    new RequestCallBack<File>() {

                        @Override
                        public void onSuccess(ResponseInfo<File> responseInfo) {
                            if (callBack != null)
                                callBack.doSuccess(responseInfo.result, url);
                        }

                        @Override
                        public void onFailure(HttpException error, String msg) {
                            if (callBack != null)
                                callBack.doFailure(error,msg,url);
                        }

                        @Override
                        public void onLoading(long total, long current,
                                              boolean isUploading) {
                            super.onLoading(total, current, isUploading);
                            if (callBack != null)
                                callBack.onLoading(total, current, isUploading);
                        }

                    });
            downloadsMap.put(url, handler);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 停止对应url路径的下载
     *
     * @param url
     */
    public static void stopDownLoad(String url) {
        downloadsMap.get(url).cancel();
    }

    /**
     * 方法功能说明：组装user-agent请求头参数的值
     *
     * @return String 请求头信息user-agent的值
     */
    private static String getUserAgent() {
        StringBuffer sb = new StringBuffer();
        sb.append("Android|");
        sb.append("1.2.0").append("|");

        String modle = Build.MODEL;
        sb.append("BOCMBC_V01.1A/");
        if ("".equals(modle)) {
            sb.append("Android00");
        } else {
            sb.append(modle);
        }
        sb.append("/BTWapView");
        return sb.toString();
    }

    /**
     * 判断是否联网
     *
     * @return
     */
    public static boolean isNetworkConnected() {
        try {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) BaseApplication
                    .getContext()
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager
                    .getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        } catch (Exception e) {
            Log.e("ART", e.getMessage());
            return false;
        }

        return false;
    }

}
