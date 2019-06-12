package tv.mycujoo.community.metrics;

import java.util.HashMap;

/**
 * Cache of metrics.
 **/
public class MetricsCache {

  public static HashMap<String, String> cache;

  /**
   * retrieves the cache.
   **/
  public static HashMap<String, String> getCache() {
    if (cache == null) {
      cache = new HashMap<>();
    }
    return cache;
  }
}
