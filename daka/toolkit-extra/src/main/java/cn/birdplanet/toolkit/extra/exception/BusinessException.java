/*
 *  Birdplanet.com Inc.
 *  Copyright (c) 2019-2019 All Rights Reserved.
 */

package cn.birdplanet.toolkit.extra.exception;

import cn.birdplanet.toolkit.extra.code.ErrorCodes;
import com.google.common.collect.Maps;

public class BusinessException extends AppException {

  public BusinessException(ErrorCodes errorCodes) {
    super(errorCodes, Maps.newHashMap());
  }
}
