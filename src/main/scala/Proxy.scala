import java.net.InetSocketAddress

import com.twitter.finagle.Service
import com.twitter.finagle.builder.{ServerBuilder, ClientBuilder}
import com.twitter.finagle.http.Http
import org.jboss.netty.handler.codec.http.{HttpResponse, HttpRequest}
import com.twitter.util.StorageUnitConversions.intToStorageUnitableWholeNumber

object Proxy extends App {

  val client: Service[HttpRequest, HttpResponse] = {
    ClientBuilder()
      .codec(Http()
            .maxResponseSize(15.megabytes)
            .maxRequestSize(15.megabytes))
      .hosts("localhost:8888")
      .hostConnectionLimit(1)
      .build()
  }

  val server = ServerBuilder().
    codec(Http()
    .maxResponseSize(15.megabytes)
    .maxRequestSize(15.megabytes)).
    bindTo(new InetSocketAddress(8080)).
    name("TROGDOR").
    build(client)

}
