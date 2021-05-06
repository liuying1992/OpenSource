package com.liuying.jetpacket.http;

import android.text.TextUtils;
import android.util.Log;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Created by liuying on 5/6/21 16:09.
 * Email: ly1203575492@163.com
 */
public abstract class SimpleSubscribe<T> implements Observer<T> {
  private String TAG;

  public SimpleSubscribe() {
    this("");
  }

  public SimpleSubscribe(String TAG) {
    if (TextUtils.isEmpty(TAG)) {
      TAG = SimpleSubscribe.class.getSimpleName();
    }
    this.TAG = TAG;
  }

  @Override public void onSubscribe(Disposable d) {
  }

  @Override public void onNext(T t) {

  }

  @Override public void onError(Throwable throwable) {
    Log.e(TAG, throwable.getMessage(), throwable);
  }

  @Override public void onComplete() {

  }
}
