FROM rwilsonltf/scala-base

COPY app /app

WORKDIR /app

RUN sbt clean stage

EXPOSE 8080

CMD ./target/universal/stage/bin/proxy-server-finagle -J-Xmx2048M -J-Xms1024M