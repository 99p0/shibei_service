/*
 *  Birdplanet.com Inc.
 *  Copyright (c) 2019-2019 All Rights Reserved.
 */

package cn.birdplanet.daka.adapter;

import cn.birdplanet.toolkit.extra.code.ErrorCodes;
import cn.birdplanet.toolkit.extra.exception.BusinessException;
import com.google.common.collect.Maps;
import io.jsonwebtoken.JwtException;
import java.time.LocalDateTime;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.servlet.error.AbstractErrorController;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.ServletWebRequest;
import springfox.documentation.annotations.ApiIgnore;

@Slf4j
@ApiIgnore
@RestController
@RequestMapping("error")
public class ErrorController extends AbstractErrorController {

  @Value("${error.path:/error}")
  private String errorPath;

  private final ErrorAttributes currErrorAttributes;

  public ErrorController(ErrorAttributes errorAttributes) {
    super(errorAttributes);
    currErrorAttributes = errorAttributes;
  }

  @Override
  public String getErrorPath() {
    return this.errorPath;
  }

  @RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Map<String, Object>> execute(HttpServletRequest request) {
    HttpStatus status = getStatus(request);
    Map<String, Object> body = Maps.newHashMapWithExpectedSize(3);
    Map<String, Object> errorBody = super.getErrorAttributes(request, false);
    body.put("timestamp", LocalDateTime.now().toString());
    switch (status) {
      case NOT_FOUND:
        body.put("msg", "没有对应的API");
        break;
    }
    Throwable throwable = currErrorAttributes.getError(new ServletWebRequest(request));
    if (throwable instanceof BusinessException) {
      body.put("msg", ((BusinessException) throwable).getErrorCodes().getDesc());
      body.put("code", ((BusinessException) throwable).getErrorCodes().getCode());
    } else if (throwable instanceof JwtException) {
      body.put("msg", ErrorCodes.token_err.getDesc());
      body.put("code", ErrorCodes.token_err.getCode());
    } else if (throwable instanceof RedisConnectionFailureException) {
      body.put("msg", ErrorCodes.RedisConnectionFailure.getDesc());
      body.put("code", ErrorCodes.RedisConnectionFailure.getCode());
    } else if (throwable instanceof MissingServletRequestParameterException) {
      body.put("msg", ErrorCodes.missingServletRequestParameter.getDesc());
      body.put("code", ErrorCodes.missingServletRequestParameter.getCode());
    } else if (throwable instanceof TypeMismatchException) {
      body.put("msg", ErrorCodes.params_err.getDesc());
      body.put("code", ErrorCodes.params_err.getCode());
    } else {
      body.put("msg", errorBody.getOrDefault("message", "ERROR"));
      body.put("code", errorBody.getOrDefault("status", "-5000"));
    }
    log.error("ERROR:{}", throwable);
    return new ResponseEntity<>(body, HttpStatus.OK);
  }
}
