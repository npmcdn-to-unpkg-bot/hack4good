package server

import database.{Document, DocumentTable}
import server.WebApp.Arguments
import unfiltered.response.ResponseString
import unfiltered.request._

import scala.concurrent.duration.Duration
import scala.concurrent.duration
import scala.concurrent.{Await, Future}
import scala.util.{Success, Failure}

//import slick.driver.MySQLDriver.api._
import slick.driver.H2Driver.api._
import scala.concurrent.ExecutionContext.Implicits.global

/**
 * Created by jannis on 12/5/15.
 */
object Main extends WebApp[Args] {

  class ServerApp(args: Arguments) extends unfiltered.filter.Plan {

    // the database object for interactions
    val db = Database.forConfig("documentdb")
    val docs = DocumentTable.props
    val setup = DBIO.seq(
      docs.schema.create,
      docs += Document(0, "content", "owner", "tags")
    )

    val setupFuture = db.run(setup)
    setupFuture.onComplete {
      case Failure(ex) => println(s"Could not setup db because of, $ex")
      case Success(_) => println("Successfully setup database")
    }
    Await.result(setupFuture, Duration(1, duration.SECONDS))

    println("test output from the database")
    val q = for (d <- docs) yield d.content
    val a = q.result
    val f: Future[Seq[String]] = db.run(a)

    f.onComplete {
      case Success(r) => println(s"Result: $r")
      case Failure(ex) => println(s"Couldn't recieve data because of $ex")
    }

    def intent = {
      case req@GET(Path(Seg("test" :: Nil)) & Params(params)) =>
        ResponseString("Das hier ist ein simpler test")

      case req@GET(Path(Seg("test2" :: Nil)) & Params(params)) =>
        val paramsMap = params.toMap[String, Seq[String]]
        ResponseString(paramsMap.getOrElse("wurst", Seq("nix geworden")).mkString(","))
    }
  }

  def htmlRoot: String = "learn4good"

  def setup(args: Args): unfiltered.filter.Plan = {
    new ServerApp(args)
  }

}

class Args extends Arguments {

}
