/*
 * 文件名: LogInSession
 * 版    权：  Copyright Hengrtech Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: zhaozeyang
 * 创建时间:16/4/27
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.demon.huitu.session;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.demon.huitu.CustomApp;
import com.demon.huitu.CustomAppPreferences;
import com.demon.huitu.application.BaseApplication;
import com.demon.huitu.net.model.UserInfo;
import com.demon.huitu.ui.basic.event.LoginEvent;
import com.demon.huitu.ui.basic.event.LogoutEvent;
import com.demon.huitu.util.JsonConverter;


/**
 * Session<BR>
 * 管理登录登出状态，以及缓存用户信息
 *
 * @author zhaozeyang
 * @version [Taobei Client V20160411, 16/4/27]
 */
public class LoginSession {

  private BaseApplication mContext;
  private UserInfo mUserInfo;
  private SharedPreferences mPreferences;

  public LoginSession(BaseApplication app) {
    mContext = app;
    mPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
    switchUserInfo();
  }

  public String getUserId() {
    return null == mUserInfo ? "" : mUserInfo.getId();
  }

  private void switchUserInfo() {
    String userJson = mPreferences.getString(CustomAppPreferences.KEY_USER_INFO, "");
    if (!TextUtils.isEmpty(userJson)) {
      mUserInfo = JsonConverter.jsonToObject(UserInfo.class, userJson);
      return;
    }
    mUserInfo = new UserInfo();
  }

  public boolean hasLogin() {
    return null != mUserInfo && !TextUtils.isEmpty(mUserInfo.getId());
  }

  public UserInfo getUserInfo() {
    return mUserInfo;
  }

  public void login(UserInfo userInfo) {
    saveUserInfo(userInfo);
    onLoginStatusChanged();
    mContext.getGlobalComponent().globalBus().post(new LoginEvent());
  }


  private void saveUserInfo(UserInfo userInfo) {
    SharedPreferences.Editor editor = mPreferences.edit();
    editor.putString(CustomAppPreferences.KEY_USER_INFO,
        JsonConverter.objectToJson(userInfo));
    editor.putString(CustomAppPreferences.KEY_USER_ID, userInfo
        .getId());
    editor.apply();
  }

  public void logout() {
    SharedPreferences.Editor editor = mPreferences.edit();
    editor.remove(CustomAppPreferences.KEY_USER_ID);
    editor.remove(CustomAppPreferences.KEY_USER_INFO);
    editor.apply();
    mContext.getGlobalComponent().globalBus().post(new LogoutEvent());
    onLoginStatusChanged();
  }

  public void onLoginStatusChanged() {
    switchUserInfo();
  }

  public void onDestroy() {
    //mSubscriptions.unsubscribe();
  }

  public UserInfoChangeBuilder userInfoChangeBuilder() {
    return new UserInfoChangeBuilder();
  }

  public class UserInfoChangeBuilder {

    public void update() {
      saveUserInfo(mUserInfo);
      mContext.getGlobalComponent().globalBus().post(new UserInfoUpdateEvent());
    }

    public UserInfoChangeBuilder setId(String id) {
      mUserInfo.setId(id);
      return this;
    }

    public UserInfoChangeBuilder setUsername(String username) {
      mUserInfo.setUsername(username);
      return this;
    }

    public UserInfoChangeBuilder setAccount(String account) {
      mUserInfo.setAccount(account);
      return this;
    }

    public UserInfoChangeBuilder setMobile(String mobile) {
      mUserInfo.setMobile(mobile);
      return this;
    }

    public UserInfoChangeBuilder setRole(String role) {
      mUserInfo.setRole(role);
      return this;
    }

    public UserInfoChangeBuilder setAddress(String address) {
      mUserInfo.setAddress(address);
      return this;
    }

    public UserInfoChangeBuilder setEmpno(String empno) {
      mUserInfo.setEmpno(empno);
      return this;
    }

    public UserInfoChangeBuilder setHeadurl(String headurl) {
      mUserInfo.setHeadurl(headurl);
      return this;
    }

    public UserInfoChangeBuilder setGoodwork(String goodwork) {
      mUserInfo.setGoodwork(goodwork);
      return this;
    }

    public UserInfoChangeBuilder setStatus(String status) {
      mUserInfo.setStatus(status);
      return this;
    }

    public UserInfoChangeBuilder setPower(String power) {
      mUserInfo.setPower(power);
      return this;
    }
  }

  public class UserInfoUpdateEvent {

  }
}
