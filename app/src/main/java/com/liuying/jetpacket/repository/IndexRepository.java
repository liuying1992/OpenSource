package com.liuying.jetpacket.repository;

import androidx.lifecycle.MutableLiveData;
import com.liuying.jetpacket.http.ApiService;
import com.liuying.jetpacket.http.SimpleSubscribe;
import com.liuying.jetpacket.http.api.IndexApi;
import com.liuying.jetpacket.model.BaseBean;
import com.liuying.jetpacket.model.IndexInfo;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import java.util.HashMap;
import java.util.Map;

/**
 * 首页数据仓库
 * Created by liuying on 5/6/21 16:23.
 * Email: ly1203575492@163.com
 */
public class IndexRepository {
  private MutableLiveData<IndexInfo> indexInfoMutableLiveData;

  public IndexRepository(MutableLiveData<IndexInfo> indexInfoMutableLiveData) {
    this.indexInfoMutableLiveData = indexInfoMutableLiveData;
  }

  public void getIndexInfo() {
    Map<String, String> req = new HashMap<>();
    ApiService.createReqService(IndexApi.class)
        .indexInfo(req)
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeOn(Schedulers.io())
        .subscribe(new SimpleSubscribe<BaseBean<IndexInfo>>() {
          @Override public void onNext(BaseBean<IndexInfo> indexInfoBaseBean) {
            super.onNext(indexInfoBaseBean);
            if (indexInfoBaseBean.success()) {
              indexInfoMutableLiveData.postValue(indexInfoBaseBean.getResult());
            }
          }

          @Override public void onError(Throwable throwable) {
            super.onError(throwable);
            indexInfoMutableLiveData.postValue(new IndexInfo("我是失败后的数据"));
          }
        });
  }
}