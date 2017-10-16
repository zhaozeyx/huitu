/*
 * 文件名: IImageDisplayView
 * 版    权：  Copyright Hengrtech Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: zhaozeyang
 * 创建时间:2016/10/31
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.demon.huitu.ui.imagepick;

import java.util.List;

/**
 * [一句话功能简述]<BR>
 * [功能详细描述]
 *
 * @author zhaozeyang
 * @version [Taobei Client V20160411, 2016/10/31]
 */
public interface IImageDisplayView {
  void show();

  void hide();

  void delete(int index);

  void add(List<String> imgPaths);

  void onImgClear();

  void setMaxCount(int count);

  void onExceedMaxCount();

  void clear();

  List<String> getSelectedImgPaths();

  void setOnItemDeleteClickListener(OnItemDeleteClickListener listener);

  void setOnAddImgClickListener(OnAddImgClickListener listener);

  interface OnItemDeleteClickListener {
    void onItemDeleteClicked(int index, String path);
  }

  interface OnAddImgClickListener {
    void onAddClicked();
  }
}
