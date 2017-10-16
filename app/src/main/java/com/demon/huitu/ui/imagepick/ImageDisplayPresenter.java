/*
 * 文件名: ImageDisplayPresenter
 * 版    权：  Copyright Hengrtech Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: zhaozeyang
 * 创建时间:2016/11/3
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.demon.huitu.ui.imagepick;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;


import com.demon.huitu.R;
import com.demon.huitu.injection.DaggerServiceComponent;
import com.demon.huitu.injection.ServiceModule;
import com.demon.huitu.log.Logger;
import com.demon.huitu.net.NetConstant;
import com.demon.huitu.net.model.ResponseModel;
import com.demon.huitu.net.service.AppService;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;

import org.reactivestreams.Publisher;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import id.zelory.compressor.Compressor;
import id.zelory.compressor.FileUtil;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * [一句话功能简述]<BR>
 * [功能详细描述]
 *
 * @author zhaozeyang
 * @version [Taobei Client V20160411, 2016/11/3]
 */
public class ImageDisplayPresenter {
  public static final int REPORT_QUESTION = 0;
  public static final int COMPLETE_TASK = 1;
  public static final int FEEDBACK = 2;
  public static final int AVATAR = 3;
  private static final int REQUEST_CODE_ALBUM = 8;
  private static final int REQUEST_CODE_CAPTURE = 10;
  private static final String TAG = "ImageDisplayPresenter";
  private String mTempPhotoName;
  private ArrayList<String> mSelectedImgPath = new ArrayList<>();

  private IImageDisplayView iImageDisplayView;
  private Activity mContext;
  private int mMaxCount;

  private CompositeDisposable mSubscriptions = new CompositeDisposable();
  // 记录上传的个数 即 调用上传接口的次数
  private volatile int mUploadInvokeTimes = 0;
  // 有几张需要上传
  private int mNeedUploadCount = 0;

  private HashMap<String, String> mUploadedCache = new HashMap<>();
  private HashMap<String, File> mUploadBitmapCache = new HashMap<>();
  @Inject
  AppService mAppService;

  private String mCaptureFolder;
  private int mUploadCount;
  private int mType;

  public ImageDisplayPresenter(Activity context, int maxCount, IImageDisplayView displayView, int
      type) {
    inject();
    mContext = context;
    mMaxCount = maxCount;
    iImageDisplayView = displayView;
    mType = type;
    Logger.d("ImageDisplayPresenter constructor", iImageDisplayView.toString());
    iImageDisplayView.setMaxCount(maxCount);
    mCaptureFolder = Environment.getExternalStorageDirectory() + "/photos";

    iImageDisplayView.setOnAddImgClickListener(new IImageDisplayView.OnAddImgClickListener() {
      @Override
      public void onAddClicked() {
        showChooseDialog();
      }
    });

    iImageDisplayView.setOnItemDeleteClickListener(new IImageDisplayView
        .OnItemDeleteClickListener() {


      @Override
      public void onItemDeleteClicked(int index, String path) {
        mSelectedImgPath.remove(index);
        mUploadBitmapCache.remove(path);
      }
    });
  }

  private void inject() {
//    DaggerServiceComponent.builder().serviceModule(new ServiceModule()).build().inject(this);
  }

  public void setUploadedImages(HashMap<String, String> uploadedCache) {
    mUploadedCache = uploadedCache;
    List<String> uploadedPath = new ArrayList<>();
    Iterator<Map.Entry<String, String>> it = uploadedCache.entrySet().iterator();
    while (it.hasNext()) {
      Map.Entry<String, String> entry = it.next();
      uploadedPath.add(entry.getValue());
    }
    iImageDisplayView.add(uploadedPath);
  }

  public List<String> getUploadedImages() {
    List<String> uploadedId = new ArrayList<>();
    Iterator<Map.Entry<String, String>> it = mUploadedCache.entrySet().iterator();
    while (it.hasNext()) {
      Map.Entry<String, String> entry = it.next();
      uploadedId.add(entry.getKey());
    }
    return uploadedId;
  }

  public void showChooseDialog() {
    String[] items = mContext.getResources().getStringArray(R.array.choose_image_items);
    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
    builder.setItems(items, new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        switch (which) {
          case 0:
            Matisse.from(mContext)
                .choose(MimeType.of(MimeType.JPEG, MimeType.PNG))
                .theme(R.style.Matisse_Dracula)
                .countable(false)
                .maxSelectable(mMaxCount - mSelectedImgPath.size())
                .imageEngine(new GlideEngine())
                .forResult(REQUEST_CODE_ALBUM);
            break;
          case 1:
            Intent intent1 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            File file = new File(mCaptureFolder);
            mTempPhotoName = System.currentTimeMillis() + ".png";
            if (!file.exists()) {
              file.mkdirs();
            }
            File photo = new File(file, mTempPhotoName);
            Uri u = Uri.fromFile(photo);
            intent1.putExtra(MediaStore.EXTRA_OUTPUT, u);
            mContext.startActivityForResult(intent1, REQUEST_CODE_CAPTURE);
            break;
        }
      }
    });
    builder.setCancelable(true);
    builder.show();
  }

  public void uploadImg(final UploadCallback callback) {
    if (null == callback) {
      return;
    }
    if (mUploadBitmapCache.size() == 0) {
      callback.onUploadCompleted("");
      return;
    }
    Iterator<Map.Entry<String, File>> it = mUploadBitmapCache.entrySet().iterator();
    List<File> uploadFiles = new ArrayList<>();
    while (it.hasNext()) {
      Map.Entry<String, File> entry = it.next();
      File uploadFile = entry.getValue();
      uploadFiles.add(uploadFile);
    }
    final StringBuffer sb = new StringBuffer();
    mUploadCount = 0;
//    mSubscriptions.add(Flowable.fromIterable(uploadFiles).observeOn(Schedulers.newThread())
//        .flatMap(new Function<File, Publisher<?>>() {
//
//
//          @Override
//          public Publisher<ResponseModel<String>> apply(@NonNull File file) throws Exception {
//            RequestBody requestFile =
//                RequestBody.create(MediaType.parse("multipart/form-data"), file);
//
//            MultipartBody.Part body =
//                MultipartBody.Part.createFormData("files", file.getName(), requestFile);
//
//
//            String url = "Upload/uploads/type/" + mType;
//
//            return mAppService.uploadQuestionImg(url, body);
//          }
//        }).map(new Function<Object, ResponseModel<String>>() {
//
//          @Override
//          public ResponseModel<String> apply(@NonNull Object o) throws Exception {
//            return (ResponseModel<String>) o;
//          }
//        }).filter(new Predicate<ResponseModel<String>>() {
//          @Override
//          public boolean test(@NonNull ResponseModel<String> responseModel) throws Exception {
//            boolean success = responseModel.getCode() == NetConstant.HttpCodeConstant
//                .REMOTE_SUCCESS;
//            if (!success) {
//              callback.onUploadFailed();
//            }
//            return success;
//          }
//        }).map(new Function<ResponseModel<String>, String>() {
//          @Override
//          public String apply(@NonNull ResponseModel<String> responseModel) throws Exception {
//            return responseModel.getData();
//          }
//        }).subscribeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<String>() {
//          @Override
//          public void accept(@NonNull String url) throws Exception {
//            if (mUploadCount != 0) {
//              sb.append(",").append(url);
//            } else {
//              sb.append(url);
//            }
//            mUploadCount++;
//            //if (mUploadCount == mUploadBitmapCache.size()) {
//            //  callback.onUploadCompleted(sb.toString());
//            //}
//
//          }
//        }, new Consumer<Throwable>() {
//          @Override
//          public void accept(@NonNull Throwable throwable) throws Exception {
//
//          }
//        }, new Action() {
//          @Override
//          public void run() throws Exception {
//            if (mUploadCount == mUploadBitmapCache.size()) {
//              callback.onUploadCompleted(sb.toString());
//            }
//          }
//        }));

  }


  //判断图片路径是否已是选中的图片
  private boolean isExistsInList(String path, List<String> pathList) {
    if (pathList == null || pathList.size() == 0) {
      return false;
    } else {
      return pathList.contains(path);
    }
  }

  public void onDestroy() {
    mSubscriptions.dispose();
    mUploadBitmapCache.clear();
  }

  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (resultCode != Activity.RESULT_OK) {
      return;
    }
    /**
     * 对ShowImageActivity直接返回的
     */
    if (requestCode == REQUEST_CODE_ALBUM) {
      List<Uri> selectedImgs = Matisse.obtainResult(data);
      if (selectedImgs.size() > 0) {
        iImageDisplayView.show();
      }
      List<String> mAddPaths = new ArrayList<>();
      for (Uri uri : selectedImgs) {
        if (isExistsInList(uri.getPath(), mSelectedImgPath)) {
          continue;
        }
        if (mSelectedImgPath.size() < mMaxCount) {
          File compressFile = null;
          try {
            compressFile = new Compressor.Builder(mContext)
                .setMaxWidth(640)
                .setMaxHeight(480)
                .setQuality(100)
                .setCompressFormat(Bitmap.CompressFormat.PNG)
                .setDestinationDirectoryPath(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES).getAbsolutePath())
                .build()
                .compressToFile(FileUtil.from(mContext, uri));
            mSelectedImgPath.add(uri.getPath());
            mAddPaths.add(compressFile.getPath());
            mUploadBitmapCache.put(compressFile.getPath(), compressFile);
          } catch (IOException e) {
            Logger.e(TAG, e, "压缩图片出错");
          }
        }
      }
      if (mAddPaths.size() > 0) {
        iImageDisplayView.add(mAddPaths);
      }
      /**
       * 表示从拍照的Activity返回
       */
    } else if (requestCode == REQUEST_CODE_CAPTURE && resultCode == Activity.RESULT_OK) {
      File temFile = new File(mCaptureFolder + "/" + mTempPhotoName);
      if (temFile.exists()) {
        File compressFile = new Compressor.Builder(mContext)
            .setMaxWidth(800)
            .setMaxHeight(800)
            .setQuality(75)
            .setCompressFormat(Bitmap.CompressFormat.JPEG)
            .setDestinationDirectoryPath(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES).getAbsolutePath())
            .build()
            .compressToFile(temFile);
        iImageDisplayView.show();
        if (!isExistsInList(temFile.getPath(), mSelectedImgPath)) {
          if (mSelectedImgPath.size() < mMaxCount) {
            mSelectedImgPath.add(temFile.getPath());
            List<String> mAddPath = new ArrayList<>();
            mAddPath.add(compressFile.getPath());
            iImageDisplayView.add(mAddPath);
            mUploadBitmapCache.put(temFile.getPath(), compressFile);
            Logger.d("ImageDisplayPresenter camera", iImageDisplayView.toString());
          }
        }
      }
    }
  }


  public interface UploadCallback {
    void onUploadCompleted(String url);

    void onUploadFailed();
  }
}

