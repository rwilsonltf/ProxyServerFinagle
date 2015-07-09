FROM rwilsonltf/scala-base

RUN git clone https://github.com/rwilsonltf/ProxyServerFinagle.git

WORKDIR ProxyServerFinagle

RUN sbt compile

EXPOSE 8080

CMD sbt run
