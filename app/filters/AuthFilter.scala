package filters

import javax.inject.Inject

import akka.stream.Materializer
import play.api.mvc.{Filter, RequestHeader, Result}

import scala.concurrent.{ExecutionContext, Future}

class AuthFilter @Inject()(implicit val mat: Materializer, ec: ExecutionContext) extends Filter {

    override def apply(nextFilter: (RequestHeader) => Future[Result])(requestHeader: RequestHeader): Future[Result] = {
        nextFilter(requestHeader)
    }
}
