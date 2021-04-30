package cn.birdplanet.daka.infrastructure.commons.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.mobile.device.Device;

@Slf4j
public class DeviceUtil {

  public static String getDeviceType(Device device) {

    if (device.isMobile()) {
      return "Mobile";
    } else if (device.isNormal()) {
      return "Normal";
    } else if (device.isTablet()) {
      return "Tablet";
    } else {
      return "Unknown";
    }
  }

}
