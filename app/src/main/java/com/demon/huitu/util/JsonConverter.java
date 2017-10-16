/*
 * 文件名: JsonConverter
 * 版    权：  Copyright Hengrtech Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: zhaozeyang
 * 创建时间:16/4/29
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.demon.huitu.util;

import com.google.gson.Gson;

/**
 * Json转换<BR>
 *
 * @author zhaozeyang
 * @version [Taobei Client V20160411, 16/4/29]
 */
public class JsonConverter {
  public static String objectToJson(Object obj) {
    String jsonStr = "";
    try {
      Gson gson = new Gson();
      jsonStr = gson.toJson(obj);
    } catch (Exception ex) {
      System.out.println(ex.getMessage());
    }
    return jsonStr;
  }

  public static <T> T jsonToObject(Class<T> clazz, String json) {
    T t = null;
    try {
      Gson gson = new Gson();
      t = gson.fromJson(json, clazz);
    } catch (Exception ex) {
      System.out.println(ex.getMessage());
    }
    return t;
  }
}
