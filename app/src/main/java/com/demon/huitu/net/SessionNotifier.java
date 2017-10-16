/*
 * 文件名: SessionNotifier
 * 版    权：  Copyright Hengrtech Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: zhaozeyang
 * 创建时间:16/5/24
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.demon.huitu.net;

import android.content.Context;
import android.widget.Toast;

import com.demon.huitu.CustomApp;
import com.demon.huitu.R;
import com.demon.huitu.injection.GlobalBus;
import com.squareup.otto.Bus;

import javax.inject.Inject;

/**
 * [一句话功能简述]<BR>
 * [功能详细描述]
 *
 * @author zhaozeyang
 * @version [Taobei Client V20160411, 16/5/24]
 */
public class SessionNotifier {
  private Bus mGlobalBus;
  private Context mContext;
  private Toast mToast;

  @Inject
  public SessionNotifier(Context context, @GlobalBus Bus bus) {
    this.mGlobalBus = bus;
    this.mContext = context;
  }

  public void notifySessionExpired() {
    showShortToast(R.string.session_expired);
    ((CustomApp) mContext.getApplicationContext()).getGlobalComponent().loginSession().logout();
  }

  private void showShortToast(int msg) {
    if (null != mToast) {
      mToast.cancel();
    }
    mToast = Toast.makeText(mContext, msg, Toast.LENGTH_SHORT);
    mToast.show();
  }
}
