/*
 * 文件名: Logger
 * 版    权：  Copyright Hengrtech Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: zhaozeyang
 * 创建时间:16/4/19
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.demon.huitu.log;


import com.demon.huitu.BuildConfig;

import okhttp3.logging.HttpLoggingInterceptor;
import timber.log.Timber;

/**
 * Logger<BR>
 *
 * @author zhaozeyang
 * @version [Taobei Client V20160411, 16/4/19]
 */
public class Logger implements HttpLoggingInterceptor.Logger{
  public static void init() {
    if (BuildConfig.DEBUG) {
      Timber.plant(new CustomDebugTree());
    }
  }

  public static void d(String tag, String message, Object... objects) {
    Timber.tag(tag);
    Timber.d(message, objects);
  }

  public static void i(String tag, String message, Object... objects) {
    Timber.tag(tag);
    Timber.i(message, objects);
  }

  public static void e(String tag, Throwable throwable, String message, Object... objects) {
    Timber.tag(tag);
    Timber.e(throwable, message, objects);
  }

  public static void e(String tag, String message, Object... objects) {
    Timber.tag(tag);
    Timber.e(message, objects);
  }

  public static void w(String tag, Throwable throwable, String message, Object... objects) {
    Timber.tag(tag);
    Timber.w(throwable, message, objects);
  }

  public static void w(String tag, String message, Object... objects) {
    Timber.tag(tag);
    Timber.w(message, objects);
  }

  @Override
  public void log(String message) {
    Timber.tag("Server");
    Timber.d(message);
  }
}
