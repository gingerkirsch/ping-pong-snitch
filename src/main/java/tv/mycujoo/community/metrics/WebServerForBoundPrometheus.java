package tv.mycujoo.community.metrics;

import io.micrometer.prometheus.PrometheusMeterRegistry;
import io.vertx.core.AbstractVerticle;
import io.vertx.ext.web.Router;
import io.vertx.micrometer.backends.BackendRegistries;

public class WebServerForBoundPrometheus extends AbstractVerticle {

  @Override
  public void start() {
    
    Router router = Router.router(vertx);
    PrometheusMeterRegistry registry = (PrometheusMeterRegistry) BackendRegistries.getDefaultNow();

    router.route("/metrics").handler(ctx -> {
      StringBuilder response = new StringBuilder();

      for (String metricKey : MetricsCache.getCache().keySet()) {
        response.append(MetricsCache.getCache().get(metricKey));
      }

      StringBuilder postFixed = new StringBuilder();

      for (String entry:registry.scrape().split("[\r\n]+")) {
        if (!entry.contains("#")) {
          if (entry.contains("{")) {
            String[] res = entry.split("\\{");
            postFixed.append(res[0]);
            postFixed.append("{label=");
            postFixed.append("\"metric_verticle\"");
            postFixed.append(",");
            postFixed.append(res[1].replace(",}","}"));
            postFixed.append("\n");
          } else {
            String[] res = entry.split(" ");
            postFixed.append(res[0]);
            postFixed.append("{label=");
            postFixed.append("\"metric_verticle\"");
            postFixed.append("}");
            postFixed.append(" ");
            postFixed.append(res[1]);
            postFixed.append("\n");
          }
        } else {
          postFixed.append(entry);
          postFixed.append("\n");
        }
      }

      response.append(postFixed);

      ctx.response().end(response.toString());
    });
    vertx.createHttpServer().requestHandler(router).listen(8444);
  }
}