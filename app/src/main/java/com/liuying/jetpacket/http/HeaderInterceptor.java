package com.liuying.jetpacket.http;

import android.content.Context;
import android.net.TrafficStats;
import android.util.Log;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.liuying.jetpacket.MyApplication;
import com.liuying.jetpacket.model.BaseBean;
import com.liuying.jetpacket.utils.CacheManager;
import com.liuying.jetpacket.utils.NetUtil;
import java.io.IOException;
import java.nio.charset.Charset;
import okhttp3.CacheControl;
import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;

/**
 * okhttp 头部拦截器
 * Created by liuying on 5/6/21 14:42.
 * Email: ly1203575492@163.com
 */
public class HeaderInterceptor implements Interceptor {
  private static final String TAG = HeaderInterceptor.class.getSimpleName();
  private boolean addHeader;
  private boolean enableCache;

  public HeaderInterceptor(boolean addHeader, boolean enableCache) {
    this.addHeader = addHeader;
    this.enableCache = enableCache;
  }

  @Override public Response intercept(Chain chain) throws IOException {
    Context context = MyApplication.getContext();
    Request request = addHeader(chain, addHeader);
    Response response = null;
    TrafficStats.setThreadStatsTag(0xF00D);
    if (!enableCache) {
      //无需缓存 直接返回
      try {
        response = chain.proceed(request);
      } catch (Exception e) {
        Log.e(TAG, e.getMessage());
      }
    } else {
      String url = request.url().url().toString();
      String params = getParams(request);

      StringBuilder sb = new StringBuilder();
      sb.append(url).append(params);
      String key = sb.toString();

      if (NetUtil.isNetWorkConnected(MyApplication.getContext())) {
        response = doRequest(chain, request, key);//有网络 直接请求
      } else {
        response = returnFromCache(chain, request, key);//缓存
      }
    }

    return response != null ? response : errorResponse(chain.request(), null);
  }

  private Response doRequest(Chain chain, Request request, String key) throws IOException {
    Context context = MyApplication.getContext();
    Charset charset = Charset.forName("UTF-8");
    Response response = chain.proceed(request);
    CacheControl cacheControl = request.cacheControl();
    //cacheControl.noStore()表示不进行缓存
    //请求成功且进行缓存
    if (!cacheControl.noStore() && response.isSuccessful()) {
      ResponseBody responseBody = response.body();
      MediaType contentType = responseBody.contentType();
      BufferedSource source = responseBody.source();
      source.request(Long.MAX_VALUE);
      Buffer buffer = source.buffer();

      if (contentType != null) {
        charset = contentType.charset(Charset.forName("UTF-8"));
      }
      //服务器返回的json原始数据
      String json = buffer.clone().readString(charset);
      CacheManager.getInstance(context).putCache(key, json);//写入磁盘
      return response.newBuilder()
          .body(ResponseBody.create(responseBody.contentType(), json))
          .build();
    } else {
      return response;//丢给下一轮处理
    }
  }

  /**
   * 返回缓存信息
   */
  private Response returnFromCache(Chain chain, Request request, String key) throws IOException {
    Context context = MyApplication.getContext();
    request = request.newBuilder().cacheControl(CacheControl.FORCE_CACHE).build();
    String cache = CacheManager.getInstance(context).getCache(key);
    Response originalResponse = chain.proceed(request);
    ResponseBody responseBody = originalResponse.body();
    return originalResponse.newBuilder()
        .code(200)
        .message("OK")
        .body(ResponseBody.create(responseBody == null ? null : responseBody.contentType(), cache))
        .build();
  }

  /**
   * 获取请求参数
   */
  private static String getParams(Request request) throws IOException {
    String reqBodyStr = "";
    String method = request.method();
    if ("POST".equals(method)) {
      StringBuilder sb = new StringBuilder();
      sb.append(request.body().contentLength());
      if (request.body() instanceof FormBody) {
        FormBody body = (FormBody) request.body();
        if (body != null && body.size() > 0) {
          for (int i = 0; i < body.size(); i++) {
            sb.append(body.encodedName(i)).append("=").append(body.encodedValue(i)).append(",");
          }
          sb.delete(sb.length() - 1, sb.length());
        }
        reqBodyStr = sb.toString();
        sb.delete(0, sb.length());
      }
    }
    return reqBodyStr;
  }

  /**
   * 添加头部信息
   */
  private Request addHeader(Chain chain, boolean addHeader) {
    if (addHeader) {
      return chain.request().newBuilder().addHeader("", "")//添加头信息
          .build();
    } else {
      return chain.request().newBuilder().build();
    }
  }

  /**
   * @错误返回 自己构建一个response
   * @ return
   */
  private Response errorResponse(Request request, MediaType contentType) {
    try {
      BaseBean<String> empty = new BaseBean<>();
      empty.setCode("-1");
      empty.setMessage("network connect error");
      empty.setResult("");
      return new Response.Builder().request(request)
          .code(400)
          .protocol(Protocol.HTTP_1_1)
          .message("")
          .body(ResponseBody.create(contentType, JSON.toJSONString(empty)))
          .build();
    } catch (JSONException e) {
      e.printStackTrace();
    }
    return new Response.Builder().request(request)
        .code(400)
        .message("")
        .protocol(Protocol.HTTP_1_1)
        .build();
  }
}
