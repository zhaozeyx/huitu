package com.demon.huitu.ui.basic.progressdialog;
/*
 * 文件名: IProgressDialog
 * 版    权：  Copyright Shudong Edu. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: zhaozeyang
 * 创建时间:2017/5/3
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */

public interface IProgressDialog {
  void show(String msg);

  void show(int msgId);

  void show(String msg, boolean cancelable);

  void show(int msgId, boolean cancelable);

  void cancel();
}
