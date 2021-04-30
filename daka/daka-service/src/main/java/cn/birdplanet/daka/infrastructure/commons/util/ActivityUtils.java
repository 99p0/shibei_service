package cn.birdplanet.daka.infrastructure.commons.util;

/**
 *
 */
public class ActivityUtils {

  public static boolean hasRound(String rounds, int nextRound) {
    return (rounds.contains("," + nextRound + ","));
  }
}
