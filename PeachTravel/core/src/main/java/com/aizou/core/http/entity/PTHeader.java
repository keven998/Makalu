package com.aizou.core.http.entity;

import android.os.Build;


import com.aizou.core.base.BaseApplication;
import com.aizou.core.constant.LibConfig;
import com.aizou.core.utils.DeviceInfo;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * 报文头标识
 * 
 * @author xby</p>
 * 
 *         例子:
 * 
 *         header:{ "version":"1.0", "device":"Apple,New Pad,MD368ZP",
 *         "platform":"Apple,iOS,5.1.1",}
 */
public class PTHeader implements Serializable {
    public List<Header> overwirdeHeaders =new ArrayList<Header>();
    public List<Header> headers =new ArrayList<Header>();

    public static final String HEADER_CONTENT_TYPE = "Content-Type";

	private static final long serialVersionUID = -2654425717222491543L;

	/** 终端应用版本,建议格式：“x.y”，其中x为大版本，y为小版本 */
	String version ;

	/**
	 * 设备信息用来区别终端特性，建议格式：“厂商名,产品名,型号”;如果是基于浏览器的RIA应用，则填写浏览器信息，建议格式：“厂商名,产品名,版本号”
	 */
	String device = Build.MANUFACTURER + "," + Build.MODEL + ","
			+ Build.PRODUCT;

	String macadd = DeviceInfo.getWifiMacAddress(BaseApplication.getContext());
	/** 操作系统信息,建议格式：“厂商名,产品名,版本号” */
	String platform = Build.BRAND + "," + LibConfig.APP_OS + ","
			+ Build.VERSION.RELEASE;

    public void addHeader(String key,String value) {
        if (this.headers == null) {
            this.headers = new ArrayList<Header>();
        }
        this.headers.add(new BasicHeader(key,value));
    }

    public void setHeader(String key,String value) {
        if (this.headers == null) {
            this.headers = new ArrayList<Header>();
        }
        this.overwirdeHeaders.add(new BasicHeader(key,value));
    }


    public void setVersion(String version) {
		this.version = version;
	}

	public String getDevice() {
		return device;
	}

	public void setDevice(String device) {
		this.device = device;
	}

	public String getPlatform() {
		return platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}

	public String getMacadd() {
		return macadd;
	}

	public void setMacadd(String macadd) {
		this.macadd = macadd;
	}

}
