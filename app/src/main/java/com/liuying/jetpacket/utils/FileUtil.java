package com.liuying.jetpacket.utils;

import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.security.MessageDigest;

/**
 * 文件相关工具类
 *
 * Created by liuying on 5/6/21 14:30.
 * Email: ly1203575492@163.com
 */
public class FileUtil {
  private static final String TAG = FileUtil.class.getSimpleName();

  /**
   * 获取sd卡路径
   */
  public static File getExternalStorageDirectory() {
    return Environment.getExternalStorageDirectory();
  }

  /**
   * 获取app在sd卡上的程序目录创建的新目录，其中程序目录为xxx/android/data/pkg/files/
   * 为避免空指针，系统方法返回null时，自己拼接路径
   *
   * @param type 用户指定的子目录
   */
  public static File getExternalFilesDir(Context context, String type) {
    File file = null;

    try {
      file = context.getExternalFilesDir(type);
    } catch (Exception e) {
      e.printStackTrace();
    }

    if (file == null) {
      if (type == null) {
        file = new File(getExternalStorageDirectory().getAbsolutePath()
            + "/Android/data/"
            + context.getPackageName()
            + "/files");
      } else {
        file = new File(getExternalStorageDirectory().getAbsolutePath()
            + "/Android/data/"
            + context.getPackageName()
            + "/files/"
            + type);
      }
    }
    if (!file.exists()) {
      file.mkdirs();
    }
    return file;
  }

  /**
   * 获取app在sd卡上的程序目录创建的新目录，其中程序目录为xxx/android/data/pkg/cache/
   * 为避免空指针，系统方法返回null时，自己拼接路径
   */
  public static File getExternalCacheDir(Context context) {
    File file = context.getExternalCacheDir();
    if (file == null) {
      file = new File(getExternalStorageDirectory().getAbsolutePath()
          + "/Android/data/"
          + context.getPackageName()
          + "/cache");
      if (!file.exists()) {
        file.mkdirs();
      }
    }
    return file;
  }

  /**
   * 获取app在sd卡上的程序目录创建的新目录，其中程序目录为xxx/android/data/pkg/cache/
   * 为避免空指针，系统方法返回null时，自己拼接路径
   *
   * @param path 用户指定的子目录
   */
  public static File getExternalPhotoFile(Context context, String dir, String path) {
    File file;
    file = new File(getExternalStorageDirectory().getAbsolutePath()
        + "/"
        + context.getPackageName()
        + "/cache/"
        + dir);
    if (!file.exists()) {
      file.mkdirs();
    }
    File file1 = new File(getExternalStorageDirectory().getAbsolutePath()
        + "/"
        + context.getPackageName()
        + "/cache/"
        + dir, path);
    return file1;
  }

  public static byte[] getByteArrayFromInputStream(InputStream in) {
    final byte[] buffer = new byte[1024];
    int byteread = 0;
    ByteArrayOutputStream out = null;
    try {
      out = new ByteArrayOutputStream();
      while ((byteread = in.read(buffer)) != -1) {
        out.write(buffer, 0, byteread);
      }
      out.flush();
      return out.toByteArray();
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      closeStream(in, out);
    }
    return null;
  }

  public static byte[] getByteArrayFromFile(File file) {
    if (file != null && file.isFile()) {
      FileInputStream in = null;
      try {
        return getByteArrayFromInputStream(new FileInputStream(file));
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    return null;
  }

  public static boolean mkParentDir(final File file) {
    final File parentDir = file.getParentFile();
    if (!parentDir.exists() || !parentDir.isDirectory()) {
      return parentDir.mkdirs();
    }
    return true;
  }

  public static boolean saveByteArrayToFile(byte[] data, File file) {
    if (file != null && data != null) {
      if (!mkParentDir(file)) {
        return false;
      }

      FileOutputStream fos = null;
      try {
        fos = new FileOutputStream(file);
        fos.write(data);
        return true;
      } catch (IOException e) {
        e.printStackTrace();
      } finally {
        closeStream(fos);
      }
    }
    return false;
  }

  public static boolean saveObjectToFile(Object obj, File file) {
    if (obj == null || file == null) {
      return false;
    }

    file.deleteOnExit();

    if (!mkParentDir(file)) {
      return false;
    }

    ObjectOutputStream out = null;
    final long start = System.currentTimeMillis();
    try {
      out = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
      out.writeObject(obj);
      out.flush();
      return true;
    } catch (Throwable e) {
      e.printStackTrace();
      if (file.isFile()) {
        file.delete();
      }
    } finally {
      closeStream(out);
      Log.d(TAG, "saveObjectToFile spend "
          + (System.currentTimeMillis() - start)
          + " "
          + file.getAbsolutePath());
    }
    return false;
  }

  public static Object getObjectFromFile(File file) {
    if (file != null && file.isFile() && file.exists()) {
      ObjectInputStream in = null;
      final long start = System.currentTimeMillis();
      try {
        in = new ObjectInputStream(new BufferedInputStream(new FileInputStream(file)));
        return in.readObject();
      } catch (Exception e) {
        e.printStackTrace();
      } finally {
        closeStream(in);
        Log.d(TAG, "getObjectFromFile spend "
            + (System.currentTimeMillis() - start)
            + " "
            + file.getAbsolutePath());
      }
    }
    return null;
  }

  public static void closeStream(InputStream in) {
    closeStream(in, null);
  }

  public static void closeStream(OutputStream out) {
    closeStream(null, out);
  }

  public static void closeStream(Reader reader) {
    closeStream(reader, null);
  }

  public static void closeStream(Writer writer) {
    closeStream(null, writer);
  }

  /**
   * 关闭流
   */
  public static void closeStream(InputStream in, OutputStream out) {
    if (in != null) {
      try {
        in.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    if (out != null) {
      try {
        out.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * 关闭流
   */
  public static void closeStream(Reader reader, Writer writer) {
    if (reader != null) {
      try {
        reader.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    if (writer != null) {
      try {
        writer.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  public static InputStream openAssetsInput(Context context, String assetsName) throws IOException {
    return context.getAssets().open(assetsName, AssetManager.ACCESS_STREAMING);
  }

  public static String getFileMD5(final File file) {
    final byte[] buffer = new byte[8 * 1024];
    int byteread = 0;
    InputStream in = null;
    try {
      MessageDigest md5Digest = java.security.MessageDigest.getInstance("MD5");
      in = new FileInputStream(file);
      while ((byteread = in.read(buffer)) != -1) {
        md5Digest.update(buffer, 0, byteread);
      }
      return DataUtils.bytesToHexString(md5Digest.digest());
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      FileUtil.closeStream(in);
    }
    return null;
  }

  public static void copyStream(final InputStream in, final OutputStream out) throws IOException {
    copyStream(in, out, 1024 * 8);
  }

  public static void copyStream(final InputStream in, final OutputStream out, final int bufferSize)
      throws IOException {
    final byte[] buffer = new byte[bufferSize];
    int readBytes = 0;
    while ((readBytes = in.read(buffer)) != -1) {
      out.write(buffer, 0, readBytes);
    }
    out.flush();
  }

  public static void cleanDirectory(File directory) {
    if (!directory.exists()) {
      Log.w(TAG, directory + " does not exist");
      return;
    }

    if (!directory.isDirectory()) {
      Log.w(TAG, directory + " is not a directory");
      return;
    }

    final File[] files = directory.listFiles();
    if (files == null) {
      return;
    }

    for (File file : files) {
      try {
        forceDelete(file);
      } catch (Exception e) {
        Log.w(TAG, e);
      }
    }
  }

  public static void forceDelete(File file) throws IOException {
    if (file.isDirectory()) {
      deleteDirectory(file);
    } else {
      boolean filePresent = file.exists();
      if (!file.delete()) {
        if (!filePresent) {
          throw new FileNotFoundException("File does not exist: " + file);
        }
        String message = "Unable to delete file: " + file;
        throw new IOException(message);
      }
    }
  }

  public static void deleteDirectory(File directory) throws IOException {
    cleanDirectory(directory);

    if (!directory.delete()) {
      String message = "Unable to delete directory " + directory + ".";
      throw new IOException(message);
    }
  }

  /**
   * 保存文件
   */
  public static boolean save(File file, byte[] data) {
    OutputStream os = null;
    try {
      os = openOutputStream(file);
      os.write(data, 0, data.length);
      return true;
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      try {
        if (os != null) os.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    return false;
  }

  /**
   * 读取文件流
   */
  //    public static byte[] read(File file) {
  //        if (file == null || !file.exists()) {
  //            return null;
  //        }
  //        InputStream in = null;
  //        try {
  //            in = openInputStream(file);
  //            int bufLen = 512;
  //            byte[] buffer = new byte[bufLen];
  //            ByteArrayBuffer arrayBuffer = new ByteArrayBuffer(bufLen);
  //
  //            int len = 0;
  //            while ((len = in.read(buffer) )!= -1) {
  //                arrayBuffer.append(buffer, 0, len);
  //            }
  //
  //            return arrayBuffer.toByteArray();
  //        } catch (Exception e) {
  //            e.printStackTrace();
  //        } finally {
  //            try {
  //                if (in != null) {
  //                    in.close();
  //                }
  //            } catch (IOException e) {
  //                e.printStackTrace();
  //            }
  //        }
  //        return null;
  //    }
  public static FileOutputStream openOutputStream(File file) throws IOException {
    if (file.exists()) {
      if (file.isDirectory()) {
        throw new IOException("File '" + file + "' exists but is a directory");
      }
      if (!file.canWrite()) {
        throw new IOException("File '" + file + "' cannot be written to");
      }
    } else {
      File parent = file.getParentFile();
      if (parent != null && !parent.exists()) {
        if (!parent.mkdirs()) {
          throw new IOException("File '" + file + "' could not be created");
        }
      }
    }
    return new FileOutputStream(file);
  }

  public static String getExternalCacheDirPath(Context context) {
    File externalCacheDir = FileUtil.getExternalCacheDir(context);
    return externalCacheDir.getAbsolutePath();
  }

  public static String getPackageDataPath(Context context) {
    return "/data/data/" + context.getPackageName();
  }

  public static String getSDCardPackageDataPath(Context context) {
    return getExternalStorageDirectory().getAbsolutePath()
        + "/Android/data/"
        + context.getPackageName();
  }

  public static long getFileLength(File file) {
    long length = 0L;
    if (file == null || !file.exists()) {
      return length;
    }

    if (file.isDirectory()) {
      // 防止栈溢出
      if (getFilePathDeep(file) > 20) {
        return length;
      }

      File[] files = null;
      try {// avoid OutOfMemoryError cause by listFiles()
        files = file.listFiles();
      } catch (Throwable e) {
        e.printStackTrace();
      }

      final int fileCount = files == null ? 0 : files.length;
      for (int i = 0; i < fileCount; i++) {
        length += getFileLength(files[i]);
      }
    } else {
      length = file.length();
    }
    return length;
  }

  public static File uri2File(Context context, final Uri uri) {
    String path;
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
      path = getPath_above19(context, uri);
    } else {
      path = getFilePath_below19(context, uri);
    }
    if (TextUtils.isEmpty(path)) {
      Log.i("ycltest", "file is null");
      return null;
    }
    return new File(path);
  }

  /**
   * API19以下获取图片路径的方法
   */
  public static String getFilePath_below19(Context context, Uri uri) {
    //这里开始的第二部分，获取图片的路径：低版本的是没问题的，但是sdk>19会获取不到
    String[] proj = { MediaStore.Images.Media.DATA };
    //好像是android多媒体数据库的封装接口，具体的看Android文档
    Cursor cursor = context.getContentResolver().query(uri, proj, null, null, null);
    //获得用户选择的图片的索引值
    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
    //将光标移至开头 ，这个很重要，不小心很容易引起越界
    cursor.moveToFirst();
    //最后根据索引值获取图片路径   结果类似：/mnt/sdcard/DCIM/Camera/IMG_20151124_013332.jpg
    String path = cursor.getString(column_index);
    cursor.close();
    return path;
  }

  /**
   * APIlevel 19以上才有
   * 添加    @TargetApi(Build.VERSION_CODES.KITKAT)即可。
   */
  @TargetApi(Build.VERSION_CODES.KITKAT) public static String getPath_above19(final Context context,
      final Uri uri) {
    final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    // DocumentProvider
    if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
      // ExternalStorageProvider
      if (isExternalStorageDocument(uri)) {
        final String docId = DocumentsContract.getDocumentId(uri);
        final String[] split = docId.split(":");
        final String type = split[0];
        if ("primary".equalsIgnoreCase(type)) {
          return Environment.getExternalStorageDirectory() + "/" + split[1];
        }
      }
      // DownloadsProvider
      else if (isDownloadsDocument(uri)) {
        final String id = DocumentsContract.getDocumentId(uri);
        final Uri contentUri =
            ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),
                Long.valueOf(id));
        return getDataColumn(context, contentUri, null, null);
      }
      // MediaProvider
      else if (isMediaDocument(uri)) {
        final String docId = DocumentsContract.getDocumentId(uri);
        final String[] split = docId.split(":");
        final String type = split[0];
        Uri contentUri = null;
        if ("image".equals(type)) {
          contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        } else if ("video".equals(type)) {
          contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        } else if ("audio".equals(type)) {
          contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        }
        final String selection = "_id=?";
        final String[] selectionArgs = new String[] {
            split[1]
        };
        return getDataColumn(context, contentUri, selection, selectionArgs);
      }
    }
    // MediaStore (and general)
    else if ("content".equalsIgnoreCase(uri.getScheme())) {
      // Return the remote address
      if (isGooglePhotosUri(uri)) return uri.getLastPathSegment();
      return getDataColumn(context, uri, null, null);
    }
    // File
    else if ("file".equalsIgnoreCase(uri.getScheme())) {
      return uri.getPath();
    }
    return null;
  }

  public static String getDataColumn(Context context, Uri uri, String selection,
      String[] selectionArgs) {
    Cursor cursor = null;
    final String column = MediaStore.Images.Media.DATA;
    final String[] projection = { column };
    try {
      cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
      if (cursor != null && cursor.moveToFirst()) {
        final int index = cursor.getColumnIndexOrThrow(column);
        return cursor.getString(index);
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      if (cursor != null) cursor.close();
    }
    return null;
  }

  public static boolean isExternalStorageDocument(Uri uri) {
    return "com.android.externalstorage.documents".equals(uri.getAuthority());
  }

  public static boolean isDownloadsDocument(Uri uri) {
    return "com.android.providers.downloads.documents".equals(uri.getAuthority());
  }

  public static boolean isMediaDocument(Uri uri) {
    return "com.android.providers.media.documents".equals(uri.getAuthority());
  }

  public static boolean isGooglePhotosUri(Uri uri) {
    return "com.google.android.apps.photos.content".equals(uri.getAuthority());
  }

  private static int getFilePathDeep(final File file) {
    return getFilePathDeep(file.getAbsolutePath());
  }

  private static int getFilePathDeep(final String absolutePath) {
    return absolutePath.split("/").length;
  }
}
