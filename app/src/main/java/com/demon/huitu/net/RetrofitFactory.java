package com.demon.huitu.net;
/*
 * 文件名: RetrofitFactory
 * 版    权：  Copyright Shudong Edu. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: zhaozeyang
 * 创建时间:2017/5/2
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */

import com.demon.huitu.log.Logger;
import com.demon.huitu.net.service.AppService;
import com.demon.huitu.net.service.UpgradeService;
import com.demon.huitu.net.service.WxService;

import java.io.IOException;

import io.reactivex.schedulers.Schedulers;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitFactory {
  public static final String TAG = "RetrofitFactory";

  public static AppService createAppService() {
    Retrofit retrofit = new Retrofit.Builder().baseUrl(NetConstant.BASE_URL)
        .client(createClient()).addConverterFactory(GsonConverterFactory.create()).
            addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers
                .newThread())).
            build();
    return retrofit.create(AppService.class);
  }

  public static UpgradeService createUpgradeService() {
    Retrofit retrofit = new Retrofit.Builder().baseUrl("http://www.dcloud.io/")
        .client(createClient()).addConverterFactory(GsonConverterFactory.create()).
            addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers
                .newThread())).
            build();
    return retrofit.create(UpgradeService.class);
  }

  public static WxService createWxService() {
    Retrofit retrofit = new Retrofit.Builder().baseUrl("https://api.weixin.qq.com/sns/oauth2/")
        .client(createClient()).addConverterFactory(GsonConverterFactory.create()).
            addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers
                .newThread())).
            build();
    return retrofit.create(WxService.class);
  }


  private static OkHttpClient createClient() {
    HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor
        .Logger() {

      @Override
      public void log(String message) {
        Logger.i(TAG, message);
      }
    });
    interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
    OkHttpClient client = new OkHttpClient.Builder()
        .addInterceptor(new Interceptor() {
          @Override
          public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            //MediaType mediaType = request.body().contentType();
            //try {
            //  Field field = mediaType.getClass().getDeclaredField("mediaType");
            //  field.setAccessible(true);
            //  field.set(mediaType, "application/x-www-form-urlencoded");
            //} catch (NoSuchFieldException e) {
            //  e.printStackTrace();
            //} catch (IllegalAccessException e) {
            //  e.printStackTrace();
            //}
            request = request.newBuilder().addHeader("Content-Type",
                "application/json; charset=utf-8").build();
            Logger.i(TAG, "request header content-type ====> " + request.header("Content-Type"));
            return chain.proceed(request);
          }
        })
        .addInterceptor(interceptor).build();
    return client;
  }

  private static OkHttpClient createNormalClient() {
    HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor
        .Logger() {

      @Override
      public void log(String message) {
        Logger.i(TAG, message);
      }
    });
    interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
    OkHttpClient client = new OkHttpClient.Builder()
        .addInterceptor(interceptor).build();
    return client;
  }

  //private static OkHttpClient createClient(final CustomAppPreferences preferences) {
  //
  //  HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor
  //      .Logger() {
  //
  //    @Override
  //    public void log(String message) {
  //      Logger.i(TAG, message);
  //    }
  //  });
  //  interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
  //  OkHttpClient httpClient = new okhttp3.OkHttpClient.Builder()
  //      .cookieJar(new CookieJar() {
  //        final HashMap<HttpUrl, List<Cookie>> cookieStore = new HashMap<>();
  //
  //        @Override
  //        public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
  //          cookieStore.put(url, cookies);
  //        }
  //
  //        @Override
  //        public List<Cookie> loadForRequest(HttpUrl url) {
  //          List<Cookie> cookies = cookieStore.get(url);
  //          return cookies != null ? cookies : new ArrayList<Cookie>();
  //        }
  //      })
  //      .addInterceptor(new Interceptor() {
  //        @Override
  //        public Response intercept(Chain chain) throws IOException {
  //          Request request = chain.request();
  //          MediaType mediaType = request.body().contentType();
  //          try {
  //            Field field = mediaType.getClass().getDeclaredField("mediaType");
  //            field.setAccessible(true);
  //            field.set(mediaType, "application/json");
  //          } catch (NoSuchFieldException e) {
  //            e.printStackTrace();
  //          } catch (IllegalAccessException e) {
  //            e.printStackTrace();
  //          }
  //          return chain.proceed(request);
  //        }
  //      }).addInterceptor(new Interceptor() {
  //        @Override
  //        public Response intercept(Chain chain) throws IOException {
  //          Request request = chain.request();
  //          String sessionId = preferences.getString(CustomAppPreferences.KEY_COOKIE_SESSION_ID,
  //              "");
  //          Request newRequest = request;
  //          if (!TextUtils.isEmpty(sessionId)) {
  //            newRequest = request.newBuilder().addHeader("Cookie", "JSESSIONID=" +
  //                sessionId).build();
  //          }
  //          return chain.proceed(newRequest);
  //        }
  //      })
  //      .addInterceptor(interceptor).build();
  //  return httpClient;
  //}
}
