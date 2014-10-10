
package com.aizou.core.http.entity;


import com.aizou.core.http.parser.IReponseParser;

import java.io.Serializable;

/**
 * ITL 请求
 *
 * @author xby</p>
 */
public class PTRequest implements Serializable {
    public static final String GET = "GET";
    String httpMethod = GET;
    public static final String POST = "POST";
    public static final String PUT = "PUT";
    public static final String HEAD = "HEAD";
    public static final String MOVE = "MOVE";
    public static final String COPY = "COPY";
    public static final String DELETE = "DELETE";
    public static final String OPTIONS = "OPTIONS";
    public static final String TRACE = "TRACE";
    public static final String CONNECT = "CONNECT";
    private static final long serialVersionUID = -562005098855409537L;
    /**
     * 包头
     */
    PTHeader header = new PTHeader();
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

    public PTHeader getHeader() {
        return header;
    }

    public void setHeader(PTHeader header) {
        this.header = header;
    }

    public PTRequestData getRequest() {
        return request;
    }

    public void setRequest(PTRequestData request) {
        this.request = request;
    }


}
