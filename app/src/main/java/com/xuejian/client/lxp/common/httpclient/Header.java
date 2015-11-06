package com.xuejian.client.lxp.common.httpclient;

/**
 * Created by yibiao.qin on 2015/10/26.
 */
public interface Header {

    /**
     * Get the name of the Header.
     *
     * @return the name of the Header,  never {@code null}
     */
    String getName();

    /**
     * Get the value of the Header.
     *
     * @return the value of the Header,  may be {@code null}
     */
    String getValue();

    /**
     * Parses the value.
     *
     * @return an array of {@link HeaderElement} entries, may be empty, but is never {@code null}
     * @throws ParseException
     */
  //  HeaderElement[] getElements() throws ParseException;

}
