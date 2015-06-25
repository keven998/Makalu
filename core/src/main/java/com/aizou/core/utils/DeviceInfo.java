package com.aizou.core.utils;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.util.DisplayMetrics;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * @ClassName：DeviceInfo
 * @Description: 设备相关信息
 */
public class DeviceInfo {
	public static String IMEI = null;
	public static String SN = null;

	/**
	*@Title: getIMEI
	*@Description: 获取手机IMEI号
	*@param context
	*@return
	*/
	public static String getIMEI(Context context) {
		if (IMEI == null) {
			TelephonyManager telManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
			String deviceId = telManager.getDeviceId(); //手机上的IMEI号
			if (deviceId == null || "".equals(deviceId)) {
				return "";
			} else {
				return deviceId;
			}
		} else {
			return IMEI;
		}
	}
	public static String getSNAddress(Context context) {
		if (SN == null) {
			TelephonyManager telManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
			String deviceId = telManager.getSimSerialNumber();
			if (deviceId == null || "".equals(deviceId)) {
				return "";
			} else {
				return deviceId;
			}
		} else {
			return SN;
		}
	}

	/**
	 *@Title: getWifiMacAddress
	 *@Description: 获取wifi Mac地址
	 *@return
	 */
	public static String getWifiMacAddress(Context context) {
		WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = wifi.getConnectionInfo();
		return info.getMacAddress();
	}

	/**
	 *@Title: getManufacturer
	 *@Description: 获取生产厂商名称（如三星 HTC等）
	 *@return
	 */
	public static String getManufacturer() {
		return Build.MANUFACTURER;
	}

	/**
	 *@Title: getManufacturer
	 *@Description: 获取手机型号
	 */
	public static String getModel() {
		return Build.MODEL;
	}

	/**
	 *@Title: getLocalIpAddress
	 *@Description:  获取手机IP地址(v4)
	 *@return
	 */
	public static String getLocalIpAddress() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();

			en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress() && (inetAddress instanceof Inet4Address)) {
						if (inetAddress.getHostAddress() == null)
							return "";
						else
							return inetAddress.getHostAddress();
					}
				}
			}
		} catch (SocketException ex) {
			return "";
		}
		return "";

	}

	/**
	 *@Title: getScreenLayout
	 *@Description: 获取手机分辨率(高 * 宽)
	 */
	public static String getScreenLayout(Context context) {
		DisplayMetrics dm = context.getResources().getDisplayMetrics();
		return dm.heightPixels + "*" + dm.widthPixels;
	}

	/**
	 *@Title: getOSVersion
	 *@Description: 获取手机系统版本号
	 *@return
	 */
	public static String getOSVersion() {
		return android.os.Build.VERSION.RELEASE;
	}

	/**
	 *@Title: getPhoneDateCid
	 *@Description: 获取基站cid
	 */
	public static String getPhoneDateCid(Context context) {
		try {
			TelephonyManager mTelNet = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
			GsmCellLocation location = (GsmCellLocation) mTelNet.getCellLocation();
			return String.valueOf(location.getCid());
		} catch (Exception e) {
			return "";
		}

	}

	/**
	 *@Title: getMnc
	 *@Description: 手机运营商代码
	 *@return
	 */
	public static String getMnc(Context context) {
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		String numeric = tm.getSimOperator();
		try {
			String mnc = numeric.substring(3, numeric.length());
			return mnc;
		} catch (Exception e2) {
			return "";
		}
	}

	/**
	 *@Title: getCountryId
	 *@Description: 获取手机国家码
	 *@param context
	 *@return
	 */
	public static String getNetworkCountryIso(Context context) {
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		return tm.getNetworkCountryIso();
	}

	/**
	 *@Title: getLocationInfo
	 *@Description: 获取手机位置信息
	 *@param context
	 *@return
	 */
	public static String getLocationInfo(Context context) {
		// 经纬度
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		criteria.setAltitudeRequired(false);
		criteria.setBearingRequired(false);
		criteria.setCostAllowed(false);
		criteria.setPowerRequirement(Criteria.POWER_LOW);
		LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		String provider = locationManager.getBestProvider(criteria, true);
		StringBuffer sb = new StringBuffer();
		if (provider != null) {
			Location location = locationManager.getLastKnownLocation(provider);
			if (location != null) {
				double lat = location.getLatitude();
				double lng = location.getLongitude();
				double mAccuracy = location.getAccuracy();
				double haiba = location.getAltitude();
				// 经度
				sb.append("longitude=").append("" + lng).append(";");
				// 纬度
				sb.append("latitude=").append("" + lat).append(";");
				// 横向精度
				sb.append("horizontalAccuracy=").append("" + mAccuracy).append(";");
				// 海拔
				sb.append("Altitude=").append("" + haiba).append(";");
				// 时间戳 改： 使用如下方法 要求得到13位
				sb.append("timestamp=").append("" + System.currentTimeMillis()).append(";");
			}
		}
		return sb.toString();
	}
	
	
	/**
	 *@Title: getlanguage
	 *@Description: 获取设备语言
	 *@param context
	 *@return
	 */
	public static String getlanguage(Context context){
		 return context.getResources().getConfiguration().locale.getCountry();
	}
}