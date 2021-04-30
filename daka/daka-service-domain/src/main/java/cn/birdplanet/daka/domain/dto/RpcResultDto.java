/*
 *  Birdplanet.com Inc.
 *  Copyright (c) 2019-2019 All Rights Reserved.
 */

package cn.birdplanet.daka.domain.dto;

import java.io.Serializable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class RpcResultDto<T> implements Serializable {

  /**
   * 是否响应成功
   */
  private Boolean success;
  /**
   * 响应状态码
   */
  private Integer code;
  /**
   * 响应数据
   */
  private T data;
  /**
   * 错误信息
   */
  private String message;

  // 构造器开始

  /**
   * 无参构造器(构造器私有，外部不可以直接创建)
   */
  private RpcResultDto() {
    this.code = 200;
    this.success = true;
  }

  /**
   * 有参构造器
   */
  private RpcResultDto(T obj) {
    this.code = 200;
    this.data = obj;
    this.success = true;
  }

  /**
   * 有参构造器
   */
  //private RpcResult(ResultCodeEnum resultCode) {
  //  this.success = false;
  //  this.code = resultCode.getCode();
  //  this.message = resultCode.getMessage();
  //}
  // 构造器结束

  /**
   * 通用返回成功（没有返回结果）
   */
  public static <T> RpcResultDto<T> success() {
    return new RpcResultDto();
  }

  /**
   * 返回成功（有返回结果）
   */
  public static <T> RpcResultDto<T> success(T data) {
    return new RpcResultDto<T>(data);
  }

  /**
   * 通用返回失败
   */
  //public static <T> RpcResult<T> failure(ResultCodeEnum resultCode) {
  //  return new RpcResult<T>(resultCode);
  //}

  public Boolean getSuccess() {
    return success;
  }

  public void setSuccess(Boolean success) {
    this.success = success;
  }

  public Integer getCode() {
    return code;
  }

  public void setCode(Integer code) {
    this.code = code;
  }

  public T getData() {
    return data;
  }

  public void setData(T data) {
    this.data = data;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }
}