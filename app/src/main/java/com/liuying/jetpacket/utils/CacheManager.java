package com.liuying.jetpacket.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;
import com.jakewharton.disklrucache.DiskLruCache;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * (Hangzhou) <br/>
 * Author: wzm<br/>
 * Date :  2018/6/13 18:00 </br>
 * Summary: 缓存管理
 * 使用范围:sdk_http
 * 依赖：common
 */

public class CacheManager {
  public static final String TAG = "CacheManager";
  //max cachesize 20mb
  private static final long DISK_CACHE_SIZE = 1024 * 1024 * 20;
  private static final int DISK_CACHE_INDEX = 0;
  private static final String CACHE_DIR = "responses";
  private volatile static CacheManager mCacheManager;
  private DiskLruCache mDiskLruCache;

  private CacheManager(Context mContext) {
    File diskCacheDir =
        new File(FileUtil.getExternalFilesDir(mContext, "http_cache"), "http_cache");
    Log.i(TAG, diskCacheDir.getAbsolutePath());
    if (!diskCacheDir.exists()) {
      boolean b = diskCacheDir.mkdirs();
    }
    if (diskCacheDir.getUsableSpace() > DISK_CACHE_SIZE) {
      try {
        mDiskLruCache =
            DiskLruCache.open(diskCacheDir, getAppVersion(mContext), 1, DISK_CACHE_SIZE);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  public static CacheManager getInstance(Context context) {
    if (mCacheManager == null) {
      synchronized (CacheManager.class) {
        if (mCacheManager == null) {
          mCacheManager = new CacheManager(context);
        }
      }
    }
    return mCacheManager;
  }

  /**
   * 对字符串进行MD5编码
   */

  public static String encryptMD5(String string) {

    try {
      byte[] hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));
      StringBuilder hex = new StringBuilder(hash.length * 2);
      for (byte b : hash) {
        if ((b & 0xFF) < 0x10) {
          hex.append("0");
        }
        hex.append(Integer.toHexString(b & 0xFF));
      }
      return hex.toString();
    } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
      e.printStackTrace();
    }

    return string;
  }

  /**
   * 同步设置缓存
   */

  public void putCache(String key, String value) throws IOException {

    if (mDiskLruCache == null) return;
    OutputStream os = null;
    try {
      DiskLruCache.Editor editor = mDiskLruCache.edit(encryptMD5(key));
      if (null != editor) {
        os = editor.newOutputStream(DISK_CACHE_INDEX);
        os.write(value.getBytes());
        os.flush();
        editor.commit();
      }
      if (null != mDiskLruCache) {
        mDiskLruCache.flush();
      }
    } catch (IOException e) {
      throw e;
    } finally {
      if (os != null) {
        try {
          os.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }

  /**
   * 同步获取缓存
   */

  public String getCache(String key) throws IOException {

    if (mDiskLruCache == null) {
      return "";
    }
    FileInputStream fis = null;
    ByteArrayOutputStream bos = null;
    try {
      DiskLruCache.Snapshot snapshot = mDiskLruCache.get(encryptMD5(key));
      if (snapshot != null) {
        fis = (FileInputStream) snapshot.getInputStream(DISK_CACHE_INDEX);
        bos = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        int len;
        while ((len = fis.read(buf)) != -1) {
          bos.write(buf, 0, len);
        }
        byte[] data = bos.toByteArray();
        return new String(data);
      }
    } catch (IOException e) {
      throw e;
    } finally {
      if (fis != null) {
        try {
          fis.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
      if (bos != null) {
        try {
          bos.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
    return "";
  }

  /**
   * 移除缓存
   */

  public boolean removeCache(String key) {
    if (mDiskLruCache != null) {
      try {
        return mDiskLruCache.remove(encryptMD5(key));
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return false;
  }

  /**
   * 获取缓存目录
   */

  private File getDiskCacheDir(Context context, String uniqueName) {
    String cachePath = context.getCacheDir().getPath();
    return new File(cachePath + File.separator + uniqueName);
  }

  /**
   * 获取APP版本号
   */
  private int getAppVersion(final Context context) {
    PackageManager pm = context.getPackageManager();
    try {
      PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
      return pi == null ? 0 : pi.versionCode;
    } catch (PackageManager.NameNotFoundException e) {
      e.printStackTrace();
    }
    return 0;
  }
}
