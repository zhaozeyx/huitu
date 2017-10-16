package com.demon.huitu.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

public class NetworkUtil {
  /**
   * 获取手机的网络类型
   *
   * @param context Context
   * @return 返回网络类型
   */
  public static int getMobileNetworkType(Context context) {
    TelephonyManager telephonyManager =
        (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
    int networkType = telephonyManager.getNetworkType();
    switch (networkType) {
      case TelephonyManager.NETWORK_TYPE_GPRS:
      case TelephonyManager.NETWORK_TYPE_EDGE:
      case TelephonyManager.NETWORK_TYPE_CDMA:
      case TelephonyManager.NETWORK_TYPE_1xRTT:
      case TelephonyManager.NETWORK_TYPE_IDEN:
        return NetworkType.TWOG;
      case TelephonyManager.NETWORK_TYPE_UMTS:
      case TelephonyManager.NETWORK_TYPE_EVDO_0:
      case TelephonyManager.NETWORK_TYPE_EVDO_A:
      case TelephonyManager.NETWORK_TYPE_HSDPA:
      case TelephonyManager.NETWORK_TYPE_HSUPA:
      case TelephonyManager.NETWORK_TYPE_HSPA:
      case TelephonyManager.NETWORK_TYPE_EVDO_B:
      case TelephonyManager.NETWORK_TYPE_EHRPD:
      case TelephonyManager.NETWORK_TYPE_HSPAP:
        return NetworkType.THREEG;
      case TelephonyManager.NETWORK_TYPE_LTE:
        return NetworkType.FOURG;
      default:
        return NetworkType.NONE;
    }
  }

  /**
   * 获取当前手机连接的网络
   *
   * @param context Context
   * @return 返回当前手机连接的网络
   */
  public static int getNetworkType(Context context) {
    ConnectivityManager manager =
        (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo networkInfo = manager.getActiveNetworkInfo();

    if (networkInfo != null && networkInfo.isConnected()) {
      String type = networkInfo.getTypeName();
      if (type.equalsIgnoreCase("WIFI")) {
        return NetworkType.WIFI;
      } else {
        return getMobileNetworkType(context);
      }
    } else {
      return NetworkType.NONE;
    }
  }
}
