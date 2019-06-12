FROM mycujoo/java-docker

COPY . .

ENV VERTICLE_HOME /usr/verticles
ENV VERTICLE_FILE micrometer-fat.jar
ENV PORT 8444

RUN gradle build

RUN mkdir -p $VERTICLE_HOME
RUN cp build/libs/$VERTICLE_FILE $VERTICLE_HOME/$VERTICLE_FILE

WORKDIR $VERTICLE_HOME

EXPOSE $PORT
CMD ["java", "-jar", "micrometer-fat.jar", "-cluster", "-Dvertx.logger-delegate-factory-class-name=io.vertx.core.logging.SLF4JLogDelegateFactory", "-Dlog4j.configurationFile=logging.properties", "-Dhazelcast.diagnostics.enabled=true"]