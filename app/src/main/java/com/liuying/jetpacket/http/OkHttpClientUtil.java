package com.liuying.jetpacket.http;

import android.util.Log;
import com.liuying.jetpacket.MyApplication;
import com.liuying.jetpacket.utils.FileUtil;
import java.io.File;
import java.io.IOException;
import java.net.Proxy;
import java.util.concurrent.TimeUnit;
import okhttp3.Cache;
import okhttp3.Dispatcher;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by liuying on 5/6/21 14:27.
 * Email: ly1203575492@163.com
 */
public class OkHttpClientUtil {
  private static final File CACHE_URL =
      new File(FileUtil.getExternalCacheDirPath(MyApplication.getContext()));
  //缓存大小
  private static final int CACHE_SIZE = 10 * 1024 * 1024;
  private static final int DEFAULT_CONNECT_TIMEOUT = 10000;
  private static final int DEFAULT_WRITE_TIMEOUT = 20000;
  private static final int DEFAULT_READ_TIMEOUT = 20000;

  private static OkHttpClient okHttpClient, retryOkHttpClient, okLongConentHttpClient;
  private static Dispatcher dispatcher;

  public static OkHttpClient getOkHttpClient() {
    if (okHttpClient == null) {
      OkHttpClient.Builder builder =
          new OkHttpClient.Builder().connectTimeout(DEFAULT_CONNECT_TIMEOUT,
              TimeUnit.MILLISECONDS)//链接超时 单位毫秒
              //.addInterceptor()//添加自定义拦截器
              .addInterceptor(new HeaderInterceptor(true, true))
              .writeTimeout(DEFAULT_WRITE_TIMEOUT, TimeUnit.MILLISECONDS)//写入时间
              .readTimeout(DEFAULT_READ_TIMEOUT, TimeUnit.MILLISECONDS)//读取时间
              .cache(new Cache(CACHE_URL, CACHE_SIZE))
              .proxy(Proxy.NO_PROXY);
      okHttpClient = builder.build();
      dispatcher = okHttpClient.dispatcher();
    }
    return okHttpClient;
  }

  /**
   * 获取OkHttpClient.
   *
   * @return okHttpClient
   * 单例，所有的api都是通过该client发送请求
   */
  public static OkHttpClient getOkHttpClientRetry(int maxRetry) {
    if (retryOkHttpClient == null) {
      //添加请求日志
      OkHttpClient.Builder builder = new OkHttpClient().newBuilder()
          .connectTimeout(DEFAULT_CONNECT_TIMEOUT, TimeUnit.MILLISECONDS)//连接超时时间【毫秒】
          .addInterceptor(new RetryInterceptor(maxRetry))
          .addInterceptor(new HeaderInterceptor(true, false))
          .writeTimeout(DEFAULT_WRITE_TIMEOUT, TimeUnit.MILLISECONDS)//写入时间
          .readTimeout(DEFAULT_READ_TIMEOUT, TimeUnit.MILLISECONDS)//读取时间
          .cache(new Cache(CACHE_URL, CACHE_SIZE))
          .proxy(Proxy.NO_PROXY);
      retryOkHttpClient = builder.build();
      dispatcher = retryOkHttpClient.dispatcher();
    }
    return retryOkHttpClient;
  }

  public static OkHttpClient getOkHttpLongTimeConnectClient() {
    if (okLongConentHttpClient == null) {
      //添加请求日志
      OkHttpClient.Builder builder = new OkHttpClient().newBuilder()
          .connectTimeout(2 * 60 * 1000, TimeUnit.MILLISECONDS)//连接超时时间【毫秒】
          .addInterceptor(new HeaderInterceptor(true, false))
          .writeTimeout(2 * 60 * 1000, TimeUnit.MILLISECONDS)//写入时间
          .readTimeout(2 * 60 * 1000, TimeUnit.MILLISECONDS)//读取时间
          .cache(new Cache(CACHE_URL, CACHE_SIZE))
          .proxy(Proxy.NO_PROXY);
      okLongConentHttpClient = builder.build();
      dispatcher = okLongConentHttpClient.dispatcher();
    }
    return okLongConentHttpClient;
  }

  /**
   * 重试机制 自定义重试次数
   */
  public static class RetryInterceptor implements Interceptor {
    public int maxRetry;//最大重试次数
    private int retryNum = 0;//假如设置为3次重试的话，则最大可能请求4次（默认1次+3次重试）

    public RetryInterceptor(int maxRetry) {
      this.maxRetry = maxRetry;
    }

    public RetryInterceptor() {
      this.maxRetry = 3;
    }

    @Override public Response intercept(Chain chain) throws IOException {
      Request request = chain.request();
      Response response = chain.proceed(request);
      Log.d("Retry", chain.toString());
      retryNum = 0;
      while (request != null
          && !response.isSuccessful()
          && retryNum < maxRetry
          && RetryPathConstants.matching(request.url().encodedPath())) {
        retryNum++;
        Log.e("Retry", "retryNum=" + retryNum);
        response = chain.proceed(request);
      }
      return response;
    }
  }

  /**
   * 取消所有请求
   */
  public static void cancelAllRequest() {
    dispatcher.cancelAll();
  }
}
