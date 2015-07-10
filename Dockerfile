FROM rwilsonltf/scala-base

COPY app /app

WORKDIR /app

RUN sbt compile

EXPOSE 8080

CMD sbt run
