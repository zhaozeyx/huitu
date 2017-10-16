package com.demon.huitu.util;
/*
 * 文件名: DialUtil
 * 版    权：  Copyright Shudong Edu. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: zhaozeyang
 * 创建时间:2017/5/16
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class DialUtil {
  public static void dial(Context context, String phoneNumber) {
    Intent intent = new Intent(Intent.ACTION_DIAL);
    Uri data = Uri.parse("tel:" + phoneNumber);
    intent.setData(data);
    context.startActivity(intent);
  }
}
