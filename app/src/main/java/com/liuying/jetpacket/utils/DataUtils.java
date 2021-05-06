package com.liuying.jetpacket.utils;

import java.math.BigDecimal;
import java.text.DecimalFormat;

public class DataUtils {

  /**
   * Convert byte[] to hex string.
   * 这里我们可以将byte转换成int，然后利用Integer.toHexString(int)来转换成16进制字符串。
   *
   * @param src byte[] data
   * @return hex string
   */
  public static String bytesToHexString(byte[] src) {
    StringBuilder stringBuilder = new StringBuilder("");
    if (src == null || src.length <= 0) {
      return "";
    }
    for (int i = 0; i < src.length; i++) {
      int v = src[i] & 0xFF;
      String hv = Integer.toHexString(v);
      if (hv.length() < 2) {
        stringBuilder.append(0);
      }
      stringBuilder.append(hv);
    }
    return stringBuilder.toString();
  }

  /**
   * Convert hex string to byte[]
   *
   * @param hexString the hex string
   * @return byte[]
   */
  public static byte[] hexStringToBytes(String hexString) {
    if (hexString == null || hexString.equals("")) {
      return null;
    }
    hexString = hexString.toUpperCase();
    int length = hexString.length() / 2;
    char[] hexChars = hexString.toCharArray();
    byte[] d = new byte[length];
    for (int i = 0; i < length; i++) {
      int pos = i * 2;
      d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
    }
    return d;
  }

  /**
   * Convert char to byte
   *
   * @param c char
   * @return byte
   */
  private static byte charToByte(char c) {
    return (byte) "0123456789ABCDEF".indexOf(c);
  }

  /**
   * 计算涨跌幅
   */
  public static String calculateUpDownString(float now, float close) {
    if (close <= 0 || now <= 0) {
      return "--";
    }

    final float updown = (now / close - 1) * 100;
    String updownString = DataUtils.rahToStr((now / close - 1) * 100) + '%';
    if (updown > 0) {
      updownString = '+' + updownString;
    }
    return updownString;
  }

  /**
   * 保留digit位小数
   */
  public static String rahToStr(float val, int digit) {
    if (!Float.isNaN(val) && val != Float.NEGATIVE_INFINITY && val != Float.POSITIVE_INFINITY) {
      BigDecimal bd = new BigDecimal(val);
      val = bd.setScale(digit, BigDecimal.ROUND_HALF_UP).floatValue();
      if (digit == 2) {
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        return decimalFormat.format(val);
      } else if (digit == 0) {
        return String.valueOf((int) val);
      } else {
        StringBuffer sb = new StringBuffer("0.");
        for (int i = 0; i < digit; i++) {
          sb.append("0");
        }
        DecimalFormat decimalFormat = new DecimalFormat(sb.toString());
        return decimalFormat.format(val);
      }
    }
    return "";
  }

  public static String rahToStr(float val) {
    if (!Float.isNaN(val) && val != Float.NEGATIVE_INFINITY && val != Float.POSITIVE_INFINITY) {
      BigDecimal bd = new BigDecimal(val);
      val = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
      DecimalFormat decimalFormat = new DecimalFormat("0.00");
      String value = decimalFormat.format(val);
      if (value.endsWith(".00")) {
        value = value.substring(0, value.indexOf(".00"));
      }
      return value;
    }
    return "";
  }
}
