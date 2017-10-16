/*
 * 文件名: UpgradeHelper
 * 版    权：  Copyright Hengrtech Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: zhaozeyang
 * 创建时间:16/6/13
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.demon.huitu.util;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;


import com.demon.huitu.R;
import com.demon.huitu.injection.DaggerServiceComponent;
import com.demon.huitu.injection.ServiceModule;
import com.demon.huitu.log.Logger;
import com.demon.huitu.net.model.ResponseModel;
import com.demon.huitu.net.model.UpgradeModel;
import com.demon.huitu.net.service.AppService;
import com.demon.huitu.net.service.UpgradeService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.inject.Inject;

import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import okhttp3.ResponseBody;
import retrofit2.Response;

import static android.content.Context.TELEPHONY_SERVICE;

/**
 * 版本更新<BR>
 *
 * @author zhaozeyang
 * @version [Taobei Client V20160411, 16/6/13]
 */
public class UpgradeHelper {
  private static final String TAG = "UpgradeHelper";
  private Context mContext;
  private AlertDialog mUpdateConfirmDialog;
  private CompositeDisposable mSubscriptions = new CompositeDisposable();

  @Inject
  UpgradeService mUpgradeService;
  @Inject
  AppService mAppService;

  public UpgradeHelper(Context context) {
    this.mContext = context;
    inject();
  }

  private void inject() {
    DaggerServiceComponent.builder().serviceModule(new ServiceModule()).build().inject(this);
  }

  private void showNormalConfirmDialog(final String url) {
    if (null != mUpdateConfirmDialog) {
      if (!mUpdateConfirmDialog.isShowing()) {
        mUpdateConfirmDialog.show();
      }
      return;
    }
    mUpdateConfirmDialog = new AlertDialog.Builder(mContext).setMessage(mContext.getString(R
        .string.upgrade_info_normal))
        .setPositiveButton(R.string.btn_confirm, new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            downloadAPK(url);
            mUpdateConfirmDialog.cancel();
          }
        }).setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            mUpdateConfirmDialog.cancel();
          }
        })
        .create();
    mUpdateConfirmDialog.show();
  }

  private void showForceUpdateDialog(String url) {

  }

  private void downloadAPK(String url) {
    boolean invokeSystemDownloadManager = false;
    if (DeviceUtils.isExternalStorageAvailable()) {
      try {
        downloadBySystem(url);
        invokeSystemDownloadManager = true;
      } catch (Exception e) {
        invokeSystemDownloadManager = false;
      }
    }
    if (!invokeSystemDownloadManager) {
      // 如果调用系统下载管理器出错,则使用我们自己的下载组件下载
      downloadApp(url);
    }
  }

  private void downloadBySystem(String url) {
    DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url)).setTitle
        (mContext.getString(R.string.app_name)).setNotificationVisibility(View.VISIBLE)
        .setDestinationUri(Uri.fromFile(getSaveTarget(mContext, url)));
    DownloadManager manager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
    manager.enqueue(request);
  }

  private void downloadApp(final String url) {
    Disposable subscription = mAppService.downloadAPK(url).subscribeOn(io.reactivex.schedulers
        .Schedulers.newThread())
        .observeOn(io.reactivex.schedulers.Schedulers.newThread())
        .doOnError(new Consumer<Throwable>() {
          @Override
          public void accept(@NonNull Throwable throwable) throws Exception {
            Logger.e(TAG, throwable, "upgrade failed");
            Toast.makeText(mContext, R.string.check_upgrade_failed, Toast.LENGTH_SHORT).show();
          }
        }).doOnNext(new Consumer<Response<ResponseBody>>() {
          @Override
          public void accept(@NonNull Response<ResponseBody> responseBodyResponse) throws
              Exception {
            if (responseBodyResponse.isSuccessful()) {
              Logger.d(TAG, "server contacted and has file");

              boolean writtenToDisk = writeResponseBodyToDisk(responseBodyResponse.body(), url);

              Logger.d(TAG, "file download was a success? " + writtenToDisk);
            } else {
              Logger.d(TAG, "server contact failed");
            }
          }
        }).subscribe();
    mSubscriptions.add(subscription);
  }

  private boolean writeResponseBodyToDisk(ResponseBody body, String fileUrl) {
    try {
      File futureStudioIconFile = getSaveTarget(mContext, fileUrl);

      InputStream inputStream = null;
      OutputStream outputStream = null;

      try {
        byte[] fileReader = new byte[4096];

        long fileSize = body.contentLength();
        long fileSizeDownloaded = 0;

        inputStream = body.byteStream();
        outputStream = new FileOutputStream(futureStudioIconFile);

        while (true) {
          int read = inputStream.read(fileReader);

          if (read == -1) {
            break;
          }

          outputStream.write(fileReader, 0, read);

          fileSizeDownloaded += read;

          Logger.d(TAG, "file download: " + fileSizeDownloaded + " of " + fileSize);
        }

        outputStream.flush();

        return true;
      } catch (IOException e) {
        return false;
      } finally {
        if (inputStream != null) {
          inputStream.close();
        }

        if (outputStream != null) {
          outputStream.close();
        }
      }
    } catch (IOException e) {
      return false;
    }
  }

  public void checkUpgradeInfo(final boolean showNoNeedUpgradeInfo) {
    TelephonyManager mTm = (TelephonyManager) mContext.getSystemService(TELEPHONY_SERVICE);
    PackageManager manager = mContext.getPackageManager();
    String appId = "";
    String versionName = "";
    try {
      PackageInfo info = manager.getPackageInfo(mContext.getPackageName(), 0);
      versionName = info.versionName;
      appId = info.packageName;
    } catch (PackageManager.NameNotFoundException e) {
      e.printStackTrace();
    }
    Disposable subscription = mAppService.checkUpgrade("a", versionName)
        .subscribeOn(io.reactivex.schedulers.Schedulers.newThread()).observeOn(io.reactivex
            .android.schedulers.AndroidSchedulers.mainThread()).subscribe(new Consumer<ResponseModel<UpgradeModel>>() {

          @Override
          public void accept(@NonNull ResponseModel<UpgradeModel> responseModelResponse)
              throws Exception {
            if (null == responseModelResponse || null == responseModelResponse.getData()) {
              return;
            }
            UpgradeModel model = responseModelResponse.getData();
            if (null == model) {
              if (showNoNeedUpgradeInfo) {
                Toast.makeText(mContext, R.string.upgrade_info_no_new_version, Toast
                    .LENGTH_SHORT).show();
              }
              return;
            }
            showNormalConfirmDialog(model.getUrl());
          }
        }, new Consumer<Throwable>() {
          @Override
          public void accept(@NonNull Throwable throwable) throws Exception {
            Logger.e(TAG, throwable, "upgrade failed");
            Toast.makeText(mContext, R.string.check_upgrade_failed, Toast.LENGTH_SHORT).show();
          }
        }, new Action() {
          @Override
          public void run() throws Exception {

          }
        });
    mSubscriptions.add(subscription);
  }

  private boolean hasNewVersion(String version) {
    if (TextUtils.isEmpty(version)) {
      return false;
    }
    PackageManager manager = mContext.getPackageManager();
    try {
      PackageInfo info = manager.getPackageInfo(mContext.getPackageName(), 0);
      String versionName = info.versionName;
      if (TextUtils.isEmpty(version)) {
        return false;
      }
      float currentVersion = Float.valueOf(versionName);
      float newVersion = Float.valueOf(version);
      return currentVersion < newVersion;
    } catch (PackageManager.NameNotFoundException e) {
      e.printStackTrace();
    }
    return false;
  }

  public void onDestroy() {
    mSubscriptions.dispose();
  }

  public static class DownloadBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
      if (intent.getAction().equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
        long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
        DownloadManager downloadManager =
            (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri apk = getDownloadedFilePath(downloadManager, id);
        if (apk != null) {
          installApk(context, apk);
        }
      }
    }
  }

  private static void installApk(Context context, Uri apk) {
    Intent intent = new Intent(Intent.ACTION_VIEW);
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    intent.setDataAndType(apk, "application/vnd.android.package-archive");
    context.startActivity(intent);
  }

  private File getSaveTarget(final Context context, final String fileUrl) {
    File path = context.getCacheDir();
    if (DeviceUtils.isExternalStorageAvailable()) {
      path = Environment.getExternalStorageDirectory();
    }

    String fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
    if (TextUtils.isEmpty(fileName)) {
      fileName = context.getString(context.getApplicationInfo().labelRes)
          + "-"
          + System.currentTimeMillis()
          + ".apk";
    }
    return new File(path, fileName);
  }

  private static Uri getDownloadedFilePath(DownloadManager downloadManager, long id) {
    // to check if the file is in cache, get its destination from the
    // database
    DownloadManager.Query query = new DownloadManager.Query().setFilterById(id);
    Cursor cursor = null;
    try {
      cursor = downloadManager.query(query);
      if (cursor == null) {
        return null;
      }
      if (cursor.moveToFirst()) {
        int status = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS));
        if (DownloadManager.STATUS_SUCCESSFUL == status) {
          return Uri.parse(
              cursor.getString(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_LOCAL_URI)));
        }
      }
    } finally {
      if (cursor != null) {
        cursor.close();
      }
    }
    // downloaded file not found or its status is not 'successfully
    // completed'
    return null;
  }
}
