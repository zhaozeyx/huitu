package com.demon.huitu.ui.basic.progressdialog;
/*
 * 文件名: ProgressDialogImp
 * 版    权：  Copyright Shudong Edu. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: zhaozeyang
 * 创建时间:2017/5/3
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */

import android.app.ProgressDialog;
import android.content.Context;

public class ProgressDialogImp implements IProgressDialog {

  private Context mContext;
  private ProgressDialog mProgressDialog;

  public ProgressDialogImp(Context context) {
    this.mContext = context;
    initProgressDialog();
  }

  private void initProgressDialog() {
    mProgressDialog = new ProgressDialog(mContext);
    mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
  }

  @Override
  public void show(String msg) {
    if (mProgressDialog.isShowing()) {
      cancel();
    }
    mProgressDialog.setMessage(msg);
    mProgressDialog.show();
  }

  @Override
  public void show(int msgId) {
    if (mProgressDialog.isShowing()) {
      cancel();
    }
    mProgressDialog.setMessage(mContext.getResources().getString(msgId));
    mProgressDialog.show();
  }

  @Override
  public void show(String msg, boolean cancelable) {
    mProgressDialog.setCancelable(cancelable);
    show(msg);
  }

  @Override
  public void show(int msgId, boolean cancelable) {
    mProgressDialog.setCancelable(cancelable);
    show(msgId);
  }


  @Override
  public void cancel() {
    mProgressDialog.dismiss();
  }
}
