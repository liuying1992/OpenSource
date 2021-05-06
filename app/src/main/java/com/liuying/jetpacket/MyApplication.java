package com.liuying.jetpacket;

import android.app.Application;
import android.content.Context;

/**
 * Created by liuying on 5/6/21 14:36.
 * Email: ly1203575492@163.com
 */
public class MyApplication extends Application {
  private static Context mContext;

  @Override public void onCreate() {
    super.onCreate();
    mContext = this;
  }

  public static Context getContext() {
    return mContext;
  }
}
