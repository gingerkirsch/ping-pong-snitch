package tv.mycujoo.community.metrics;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;

/**
 * Main Verticle.
 **/
public class MainVerticle extends AbstractVerticle {

  @Override
  public void start(Future<Void> startFuture) {
    vertx.deployVerticle(new WebServerForBoundPrometheus());

    vertx.eventBus().consumer("metrics").handler(message -> {
      JsonObject metric = JsonObject.mapFrom(message.body());
      StringBuilder postFixed = new StringBuilder();

      for (String entry:metric.getString("metric").split("[\r\n]+")) {
        if (!entry.contains("#")) {

          if (entry.contains("{")) {
            String[] res = entry.split("\\{");
            postFixed.append(res[0]);
            postFixed.append("{");
            postFixed.append("label=\"");
            postFixed.append(metric.getString("origin"));
            postFixed.append("\",");
            postFixed.append(res[1].replace(",}","}"));
            postFixed.append("\n");
          } else {
            String[] res = entry.split(" ");
            postFixed.append(res[0]);
            postFixed.append("{label=\"");
            postFixed.append(metric.getString("origin"));
            postFixed.append("\"}");
            postFixed.append(" ");
            postFixed.append(res[1]);
            postFixed.append("\n");
          }
        } else {
          postFixed.append(entry);
          postFixed.append("\n");
        }
      }
      MetricsCache.getCache().put(metric.getString("origin"), postFixed.toString());
    });
  }
}
