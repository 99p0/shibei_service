package cn.birdplanet.daka.infrastructure.commons.util;

import java.util.HashMap;

public class UidUtils {

  private static final HashMap<Long, Long> UidSyncMap;

  static {
    UidSyncMap = new HashMap<>();
  }

  public static synchronized long getUid(long uid) {
    Long v = UidSyncMap.get(uid);
    if (v == null) {
      v = Long.valueOf(uid);
      UidSyncMap.put(uid, v);
    }
    return v;
  }
}
