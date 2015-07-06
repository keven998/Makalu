
package com.aizou.core.http.entity;


import com.aizou.core.http.parser.IReponseParser;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicHeader;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * ITL 请求
 *
 * @author xby</p>
 */
public class PTRequest implements Serializable {
    public static final String GET = "GET";
    String httpMethod = GET;
    public static final String PATCH = "PATCH";
    public static final String POST = "POST";
    public static final String PUT = "PUT";
    public static final String HEAD = "HEAD";
    public static final String MOVE = "MOVE";
    public static final String COPY = "COPY";
    public static final String DELETE = "DELETE";
    public static final String OPTIONS = "OPTIONS";
    public static final String TRACE = "PATCH";
    public static final String CONNECT = "CONNECT";
    private static final long serialVersionUID = -562005098855409537L;
    /**
     * 包头
     */
    PTHeader mPTheader = new PTHeader();
    /**
     * 请求包
     */
    PTRequestData request;
    IReponseParser parser;

    public String getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
    }

    public IReponseParser getParser() {
        return parser;
    }

    public void setParser(IReponseParser parser) {
        this.parser = parser;
    }

    public PTHeader getPTHeader() {
        return mPTheader;
    }

    public void setPTHeader(PTHeader header) {
        this.mPTheader = header;
    }

    public PTRequestData getRequest() {
        return request;
    }

    public void setRequest(PTRequestData request) {
        this.request = request;
    }


    public void addHeader(String key,String value) {
        if (this.mPTheader.headers == null) {
            this.mPTheader.headers = new ArrayList<Header>();
        }
        this.mPTheader.headers.add(new BasicHeader(key,value));
    }

    public void setHeader(String key,String value) {
        if (this.mPTheader.headers == null) {
            this.mPTheader.headers = new ArrayList<Header>();
        }
        this.mPTheader.overwirdeHeaders.add(new BasicHeader(key,value));
    }

    public void setBodyEntity(HttpEntity bodyEntity) {
        if(request==null){
            request = new PTRequestData();

        }
       request.setBodyEntity(bodyEntity);
    }

    public HttpEntity getBodyEntity(){
        if(request==null){
            request = new PTRequestData();

        }
        return request.getBodyEntity();
    }



    public List<NameValuePair> getBodyParams() {
        if(request==null){
            request = new PTRequestData();

        }
        return request.getBodyParams();
    }

    public void setBodyParams( List<NameValuePair> bodyParams) {

        if(request==null){
            request = new PTRequestData();

        }
        request.setBodyParams(bodyParams);
    }

    public void setUrl(String url) {
        if(request==null){
            request = new PTRequestData();

        }
        request.setUrl(url);

    }

    public String readUrl() {

        if(request==null){
            request = new PTRequestData();

        }
        return request.readUrl();
    }

    public void putBodyParams(String key, String value) {
        if(request==null){
            request = new PTRequestData();

        }
       request.putBodyParams(key,value);
    }

    public List<NameValuePair> getUrlParams() {

        if(request==null){
            request = new PTRequestData();

        }
        return request.getUrlParams();
    }

    public void putUrlParams(String key, String value) {

        if(request==null){
            request = new PTRequestData();

        }
        request.putUrlParams(key,value);
    }

    public void setUrlParams(List<NameValuePair> urlParams) {
        if(request==null){
            request = new PTRequestData();

        }
        request.setUrlParams(urlParams);
    }


}
