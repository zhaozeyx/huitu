/*
 * 文件名: RpcHttpError
 * 版    权：  Copyright Hengrtech Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: zhaozeyang
 * 创建时间:16/4/18
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.demon.huitu.net;

/**
 * Http返回码封装类<BR>
 *
 * @author zhaozeyang
 * @version [Taobei Client V20160411, 16/4/18]
 */
public class RpcHttpError {
  private int mHttpCode;
  private String mMessage;

  public RpcHttpError(int httpCode, String message) {
    mHttpCode = httpCode;
    mMessage = message;
  }

  public int getHttpCode() {
    return mHttpCode;
  }

  public String getMessage() {
    return mMessage;
  }
}
