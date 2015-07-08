import java.net.{URL, InetSocketAddress}

import com.twitter.finagle.{Httpx, Service}
import com.twitter.finagle.builder.{ServerBuilder, ClientBuilder}
import com.twitter.finagle.http.{Request, Response, RequestBuilder, Http}
import com.twitter.finagle.httpx
import com.twitter.io.Charsets
import com.twitter.util.{Await, Future}
import org.jboss.netty.handler.codec.http._
import com.twitter.util.StorageUnitConversions.intToStorageUnitableWholeNumber
import org.jboss.netty.buffer.ChannelBuffers.copiedBuffer

object Proxy extends App {

  val client: Service[HttpRequest, HttpResponse] = {
      ClientBuilder()
        .codec(Http()
          .maxResponseSize(15.megabytes)
          .maxRequestSize(15.megabytes))
        .hosts("localhost:8888")
        .hostConnectionLimit(1)
        .failFast(false)
        .build()
  }

  val clientService = new Service[HttpRequest, HttpResponse] {
    def apply(req: HttpRequest): Future[HttpResponse] = {
      req.headers().add("X-SOURCE-IP", req.asInstanceOf[Request].remoteSocketAddress.getAddress())
      req.headers().add("X-WARNING", "something")

      client.apply(req)
    }
  }

  val server = {
    ServerBuilder()
      .codec(Http()
        .maxResponseSize(15.megabytes)
        .maxRequestSize(15.megabytes))
      .bindTo(new InetSocketAddress(8080))
      .name("TROGDOR")
      .build(clientService)
  }
}
