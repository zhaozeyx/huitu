/*
 * 文件名: DeviceUtils
 * 版    权：  Copyright Hengrtech Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: zhaozeyang
 * 创建时间:16/5/24
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.demon.huitu.util;

import android.os.Environment;

import static android.os.Environment.MEDIA_MOUNTED;

/**
 * [一句话功能简述]<BR>
 * [功能详细描述]
 *
 * @author zhaozeyang
 * @version [Taobei Client V20160411, 16/5/24]
 */
public class DeviceUtils {
  /**
   * 判断SdCard是否可用
   */
  public static boolean isExternalStorageAvailable() {
    try {
      return MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    } catch (NullPointerException | IncompatibleClassChangeError e) {
      return false;
    }
  }
}
