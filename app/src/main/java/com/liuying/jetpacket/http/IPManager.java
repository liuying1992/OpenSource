package com.liuying.jetpacket.http;

/**
 * Created by liuying on 5/6/21 15:22.
 * Email: ly1203575492@163.com
 */
public class IPManager {
  private static volatile IPManager sIPManager;

  public static IPManager getInstance() {
    if (sIPManager == null) {
      synchronized (IPManager.class) {
        if (sIPManager == null) {
          sIPManager = new IPManager();
        }
      }
    }
    return sIPManager;
  }

  public String getBaseUrl() {
    return "http://www.baidu.com";
  }
}
