/*
 * 文件名: NetConstant
 * 版    权：  Copyright Hengrtech Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: zhaozeyang
 * 创建时间:16/4/19
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.demon.huitu.net;

/**
 * 服务器常量<BR>
 *
 * @author zhaozeyang
 * @version [Taobei Client V20160411, 16/4/19]
 */
public class NetConstant {
  public static final String URL_DOWNLOAD_APP = "http://oa.huanzewl.com/";

  public static final String BASE_URL_LOCATION = "http://oa.huanzewl.com/";
  public static final String BASE_URL_SERVICE_SUFFIX = "Api/";
  public static final String BASE_URL = BASE_URL_LOCATION + BASE_URL_SERVICE_SUFFIX;

  private NetConstant() {

  }

  public class HttpCodeConstant {
    public static final int UNKNOWN_ERROR = -1;
    public static final int SUCCESS = 200;
    public static final int HTTP_ERROR_NOT_FOUND = 404;
    public static final int SESSION_EXPIRED = 100;
    public static final int REMOTE_SUCCESS = 1;
    public static final int REMOTE_FAILED_NOT_NULL = 103;
    public static final int REMOTE_FAILED_PARAM = 100;
    public static final int REMOTE_FAILED_RESULT_NULL = 100;
  }
}
