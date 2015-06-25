package com.xuejian.client.lxp.common.yweathergetter4a;

public interface YahooWeatherExceptionListener {
    void onFailConnection(final Exception e);
    void onFailParsing(final Exception e);
    void onFailFindLocation(final Exception e);
}
