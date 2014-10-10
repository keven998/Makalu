package com.aizou.core.http.entity;


import com.aizou.core.base.BaseApplication;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * 请求包信息主体
 *
 * @author xby</p>
 *         <p/>
 *         例子: bodyParams:{dataId1:"dataValue1",dataId2:"dataValue2",}
 */
public class PTRequestData implements Serializable {
    private static final long serialVersionUID = 2720429558849096062L;
    String requestUrl;
    /**
     * 请求参数
     */
    List<NameValuePair> bodyParams = new ArrayList<NameValuePair>();
    List<NameValuePair> urlParams = new ArrayList<NameValuePair>();
    HttpEntity bodyEntity;
    String TOKENCODE = BaseApplication.getContext().getTokenCode();

    public void addHeader(String name, String value) {

    }

    public void setBodyEntity(HttpEntity bodyEntity) {
        this.bodyEntity = bodyEntity;
        if (bodyParams != null) {
            bodyParams.clear();
            bodyParams = null;
        }
    }

    public HttpEntity getBodyEntity(){
        return bodyEntity;
    }



    public  List<NameValuePair> getBodyParams() {
        return bodyParams;
    }

    public void setBodyParams( List<NameValuePair> bodyParams) {
        this.bodyParams = bodyParams;
    }

    public void setUrl(String url) {
        requestUrl = url;

    }

    public String readUrl() {
        return requestUrl;
    }

    public void putBodyParams(String key, String value) {
        NameValuePair pair = new BasicNameValuePair(key,value);
        bodyParams.add(pair);
    }

    public String getTOKENCODE() {
        return TOKENCODE;
    }

    public void setTOKENCODE(String TOKENCODE) {
        this.TOKENCODE = TOKENCODE;
    }

    public List<NameValuePair> getUrlParams() {
        return urlParams;
    }

    public void putUrlParams(String key, String value) {

        NameValuePair pair = new BasicNameValuePair(key,value);
        urlParams.add(pair);
    }

    public void setUrlParams(List<NameValuePair> urlParams) {
        this.urlParams = urlParams;
    }

}
