/*
 * 文件名: HttpErrorUiNotifier
 * 版    权：  Copyright Paitao Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: daifeng
 * 创建时间:15/4/10
 *
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.demon.huitu.net;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

import com.demon.huitu.R;
import com.demon.huitu.injection.GlobalBus;
import com.demon.huitu.util.NetworkType;
import com.demon.huitu.util.NetworkUtil;
import com.squareup.otto.Bus;

import javax.inject.Inject;
import javax.inject.Singleton;

/**§§
 * Http错误提示统一处理类
 *
 * @author daifeng
 */
@Singleton
public class HttpErrorUiNotifier {
  private final Context context;
  private final Bus rootBus;
  private Toast toast;

  @Inject
  public HttpErrorUiNotifier(Context context, @GlobalBus Bus rootBus) {
    this.context = context;
    this.rootBus = rootBus;
  }

  public void notifyUi(RpcHttpError httpError) {
    int errorCode = httpError.getHttpCode();
    if (errorCode == 503) {
      rootBus.post(new ServerUpheldEvent(httpError.getMessage()));
    } else if (NetworkUtil.getNetworkType(context) == NetworkType.NONE) {
      showHttpErrorToast(context, context.getString(R.string.network_not_normal));
    } else {
      showHttpErrorToast(context, context.getString(R.string.http_server_error));
    }
    showHttpErrorToast(context.getApplicationContext(), TextUtils.isEmpty(httpError.getMessage())
        ? context.getString(R.string.http_server_error) : httpError.getMessage());
  }

  private void showHttpErrorToast(Context context, String errorMsg) {
    if (toast != null) {
      toast.cancel();
    }
    toast = Toast.makeText(context.getApplicationContext(), errorMsg, Toast.LENGTH_SHORT);
    toast.show();
  }

  /**
   * 服务器维护的事件
   */
  public static class ServerUpheldEvent {
    private String mHtmlStr;

    public ServerUpheldEvent(String htmlStr) {
      this.mHtmlStr = htmlStr;
    }

    public String getHtmlStr() {
      return mHtmlStr;
    }
  }
}
