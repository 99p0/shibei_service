package cn.birdplanet.toolkit.extra.exception;

import cn.birdplanet.toolkit.extra.code.ErrorCodes;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author 杨润[uncle.yang@outlook.com]
 * @title: AppException
 * @date 2019/9/22 09:12
 */
@Slf4j
@AllArgsConstructor
@Getter
@Setter
public abstract class AppException extends RuntimeException {

  private ErrorCodes errorCodes;
  private Map<String, Object> data;
}
