package com.liuying.jetpacket.http.api;

import com.liuying.jetpacket.model.BaseBean;
import com.liuying.jetpacket.model.IndexInfo;
import io.reactivex.Observable;
import java.util.Map;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * Created by liuying on 5/6/21 16:05.
 * Email: ly1203575492@163.com
 */
public interface IndexApi {
  @POST("/login") @Headers({ "Content-Type: application/json;charset=UTF-8" })
  Observable<BaseBean<String>> login(@Body Map<String, String> req);

  @POST("/index") @Headers({ "Content-Type: application/json;charset=UTF-8" })
  Observable<BaseBean<IndexInfo>> indexInfo(@Body Map<String, String> req);
}
