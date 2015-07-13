import java.net.InetSocketAddress

import com.twitter.finagle.Service
import com.twitter.finagle.builder.{ServerBuilder, ClientBuilder}
import com.twitter.finagle.http.{Request, Http}
import com.twitter.util.Future
import org.jboss.netty.handler.codec.http._
import com.twitter.util.StorageUnitConversions.intToStorageUnitableWholeNumber

object Proxy extends App {

  val target_host = sys.env.get("TARGET_HOST")

  val client: Service[HttpRequest, HttpResponse] = {
      ClientBuilder()
        .codec(Http()
          .maxResponseSize(15.megabytes)
          .maxRequestSize(15.megabytes))
        .hosts(target_host.getOrElse("192.168.1.108:8090"))
        .hostConnectionLimit(1)
        .failFast(false)
        .build()
  }

  val clientService = new Service[HttpRequest, HttpResponse] {
    def apply(req: HttpRequest): Future[HttpResponse] = {
      req.headers().add("X-SOURCE-IP", req.asInstanceOf[Request].remoteSocketAddress.getAddress())
      req.headers().add("X-WARNING", "This is not being authenticated")

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
