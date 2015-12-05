package server

/**
 * Created by jannis on 12/5/15.
 */

import server.WebApp.Arguments
import unfiltered.util.Port
import com.typesafe.config.ConfigFactory
import com.quantifind.sumac.{ArgMain, FieldArgs}
import com.twitter.conversions.storage._

trait WebApp[T <: Arguments] extends ArgMain[T] {

  def htmlRoot: String

  def setup(args: T): unfiltered.filter.Plan

  def get(parsed: T) = {
    val root = parsed.altRoot match {
      case Some(path) => new java.io.File(path).toURL
      case _ => getClass.getResource(htmlRoot)
    }
    implicit val conf = ConfigFactory.load()
    println("root folder for files: " + root)
    val server = unfiltered.jetty.Http(parsed.port)
      .resources(root)
      .filter(setup(parsed))
    val connector = server.underlying.getConnectors.head
    connector.setRequestHeaderSize(parsed.headerSize)
    server
  }

  override def main(parsed: T) {
    get(parsed).run()
  }

}

object WebApp {

  trait Arguments extends FieldArgs {
    var headerSize: Int = 2.megabytes.inBytes.toInt
    var port = Port.any
    var altRoot: Option[String] = None
  }

}