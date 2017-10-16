package com.demon.huitu.net;
/*
 * 文件名: ResponseErrorNotifier
 * 版    权：  Copyright Shudong Edu. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: zhaozeyang
 * 创建时间:2017/5/2
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

import com.demon.huitu.CustomApp;
import com.demon.huitu.R;
import com.demon.huitu.util.NetworkType;
import com.demon.huitu.util.NetworkUtil;
import com.squareup.otto.Bus;

public class ResponseErrorNotifier {
  private Bus mGlobalBus;
  private Context mContext;
  private Toast mToast;

  private void showShortToast(int msg) {
    if (null != mToast) {
      mToast.cancel();
    }
    mToast = Toast.makeText(mContext, msg, Toast.LENGTH_SHORT);
    mToast.show();
  }

  private void showShortToast(String msg) {
    if (null != mToast) {
      mToast.cancel();
    }
    mToast = Toast.makeText(mContext, msg, Toast.LENGTH_SHORT);
    mToast.show();
  }

  private class Sessionnotifier {
    public void notifySessionExipired() {
      showShortToast(R.string.session_expired);
      ((CustomApp) mContext.getApplicationContext()).getGlobalComponent().loginSession().logout();
    }
  }

  private class HttpErrorNotifier {
    public void notifyUi(RpcHttpError httpError) {
      int errorCode = httpError.getHttpCode();
      if (NetworkUtil.getNetworkType(mContext) == NetworkType.NONE) {
        showShortToast(mContext.getString(R.string.network_not_normal));
      } else {
        showShortToast( mContext.getString(R.string.http_server_error));
      }
      showShortToast(TextUtils.isEmpty(httpError.getMessage())
          ? mContext.getString(R.string.http_server_error) : httpError.getMessage());
    }
  }
}
