package com.liuying.jetpacket.http;

import java.util.HashMap;
import java.util.Map;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by liuying on 5/6/21 15:21.
 * Email: ly1203575492@163.com
 */
public class ApiService {
  private static Map<String, Object> apis = new HashMap<>();

  public static <T> T createReqService(final Class<T> service) {
    return createConnect(service, IPManager.getInstance().getBaseUrl(), false, false);
  }

  private static <T> T createConnect(Class<T> service, String baseUrl, boolean isNeedRetry,
      boolean isNeedLongConnection) {
    if (apis.containsKey(service.getName())) {
      return (T) apis.get(service.getName());
    }
    
    OkHttpClient okHttpClient = null;

    //需要重试机制
    if (isNeedRetry) {
      okHttpClient = OkHttpClientUtil.getOkHttpClientRetry(3);
    }

    //是否需要日志
    if (isNeedLongConnection) {
      okHttpClient = OkHttpClientUtil.getOkHttpLongTimeConnectClient();
    }

    if (okHttpClient == null) {
      okHttpClient = OkHttpClientUtil.getOkHttpClient();
    }
    return new Retrofit.Builder().client(okHttpClient)
        .baseUrl(baseUrl)
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build()
        .create(service);
  }
}
