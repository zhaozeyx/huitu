/*
 * 文件名: ImageLoader
 * 版    权：  Copyright Hengrtech Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: zhaozeyang
 * 创建时间:16/4/21
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.demon.huitu.util.imageloader;

import android.content.Context;

import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.Glide;

/**
 * [一句话功能简述]<BR>
 * [功能详细描述]
 *
 * @author zhaozeyang
 * @version [Taobei Client V20160411, 16/4/21]
 */
public class ImageLoader {
  /**
   * 加载优化的商品图片
   *
   * @param context Context
   * @param productPic 商品图片的hash
   */
  public static DrawableRequestBuilder<String> loadOptimizedProductImage(Context context,
                                                                         String productPic,
                                                                         boolean isSmall) {
    //DrawableRequestBuilder<String> load3G;
    //DrawableRequestBuilder<String> loadWifi;
    //String wifiUrl = isSmall ? ProductImageResource.hashToSmallUrl(productPic, NetworkType.WIFI)
    //    : ProductImageResource.hashToBigUrl(productPic, NetworkType.WIFI);
    //String threeGUrl = isSmall ? ProductImageResource.hashToSmallUrl(productPic, NetworkType
    // .THREEG)
    //    : ProductImageResource.hashToBigUrl(productPic, NetworkType.THREEG);
    ////wifi
    //if (NetworkTypeUtils.getNetworkType(context) == NetworkType.WIFI) {
    //  loadWifi = Glide.with(context).load(wifiUrl);
    //  load3G = Glide.with(context).using(new NetworkDisablingFetcher()).load(threeGUrl);
    //} else {
    //  load3G = Glide.with(context).load(threeGUrl);
    //  loadWifi = Glide.with(context).using(new NetworkDisablingFetcher()).load(wifiUrl);
    //}
    return null;
  }

  /**
   * 加载优化的网络图片
   *
   * @param context Context
   * @param httpPic 网络图片的hash
   */
  public static DrawableRequestBuilder<String> loadOptimizedHttpImage(Context context,
                                                                      String httpPic) {
    //DrawableRequestBuilder<String> load3G;
    //DrawableRequestBuilder<String> loadWifi;
    //String wifiUrl = HttpImageResource.hashToFullUrl(httpPic, NetworkType.WIFI);
    //String threeGUrl = HttpImageResource.hashToFullUrl(httpPic, NetworkType.THREEG);
    ////wifi
    //if (NetworkTypeUtils.getNetworkType(context) == NetworkType.WIFI) {
    //  loadWifi = Glide.with(context).load(wifiUrl);
    //  load3G = Glide.with(context).using(new NetworkDisablingFetcher()).load(threeGUrl);
    //} else {
    //  load3G = Glide.with(context).load(threeGUrl);
    //  loadWifi = Glide.with(context).using(new NetworkDisablingFetcher()).load(wifiUrl);
    //}
    return Glide.with(context).load(httpPic);
  }

}
