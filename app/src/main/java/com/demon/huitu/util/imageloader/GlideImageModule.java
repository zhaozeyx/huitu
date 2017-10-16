package com.demon.huitu.util.imageloader;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.integration.okhttp3.OkHttpGlideModule;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.cache.ExternalCacheDiskCacheFactory;
import com.bumptech.glide.request.target.ViewTarget;
import com.demon.huitu.R;

/**
 * The GlideModule interface to lazily configure Glide and register components like
 * ModelLoaders automatically when the first Glide request is made. <BR>
 */
public class GlideImageModule extends OkHttpGlideModule {

  public static final String DISK_CACHE_DIR = "ImageCache";

  private static final int DISK_SIZE_IN_MB = 250;
  private static final int DISK_SIZE_IN_BYTES = 1024 * 1024 * DISK_SIZE_IN_MB;

  /**
   * Apply options to the builder here.
   * 1. Bitmap configuration
   * 2. Disk Cache configuration
   * 3. Memory Cache configuration
   */
  @Override
  public void applyOptions(Context context, GlideBuilder builder) {
    super.applyOptions(context, builder);
    builder.setDecodeFormat(DecodeFormat.PREFER_ARGB_8888)
        .setDiskCache(
            new ExternalCacheDiskCacheFactory(context, DISK_CACHE_DIR, DISK_SIZE_IN_BYTES));
    ViewTarget.setTagId(R.id.glide_tag_id);
  }

  /**
   * register ModelLoaders here.
   */
  @Override
  public void registerComponents(Context context, Glide glide) {
    super.registerComponents(context, glide);
  }
}