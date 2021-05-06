package com.liuying.jetpacket.model;

import java.io.Serializable;

/**
 * Created by liuying.
 */

public class BaseBean<T> implements Serializable {
  public static final String CODE_SUCCESS = "0";

  public String code;//返回码
  public String msg;//返回信息
  public T data;//成功返回内容;

  public T getResult() {
    return data;
  }

  public void setResult(T data) {
    this.data = data;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getMessage() {
    return msg;
  }

  public void setMessage(String message) {
    this.msg = message;
  }

  public boolean success() {
    return CODE_SUCCESS.equals(code);
  }

  @Override public String toString() {
    return "BaseBean{"
        + "code='"
        + code
        + '\''
        + ", msg='"
        + msg
        + '\''
        + ", data='"
        + data
        + '\''
        + '}';
  }
}