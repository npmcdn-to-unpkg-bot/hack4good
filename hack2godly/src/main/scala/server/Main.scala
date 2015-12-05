package server

import java.security.InvalidParameterException

import database._

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

import org.json4s._
import org.json4s.native.Serialization
import org.json4s.native.Serialization.{read, write}

/**
 * Created by jannis on 12/5/15.
 */
object Main extends WebApp[Args] {

  implicit val formats = Serialization.formats(NoTypeHints)
  class ServerApp(args: Arguments) extends unfiltered.filter.Plan {

    // the database object for interactions
    val db = Database.forConfig("documentdb")
    val docs = DocumentTable.props
    val sessions = SessionTable.props

    val sess = for(i <- (1 to 10000)) yield {
      QuestionSession(
        i,
        "wurstwasserr trinken", "Muss ich, wenn ich in Deutschland normal sein will, jeden tag wurstwasser trinken?",
        12,
        1235465,
        12354,
        List(
          Message(1, "du wurst", 12345, 42, 12),
          Message(2, "trink wurstwasser", 12346, 42, 12),
          Message(3, "na gut", 12347, 42, 12)),
        List(DocumentTag(43, "kultur", Languages.Deutsch, 123545)))
    }

    // setup data with some examples
    val setup = DBIO.seq(
      docs.schema.create,
      sessions.schema.create,
      docs += Document(0, "content", "owner", "tags"),
      sessions ++= sess.toSeq
    )

    val setupFuture = db.run(setup)
    setupFuture.onComplete {
      case Failure(ex) => println(s"Could not setup db because of, $ex")
      case Success(_) => println("Successfully setup database")
    }
    Await.result(setupFuture, Duration(10, duration.SECONDS))

    val base = "rest"

    def intent = {
      case req@GET(Path(Seg(base :: "test" :: Nil)) & Params(params)) =>
        ResponseString("Das hier ist ein simpler test")

      case req@GET(Path(Seg(base :: "test2" :: Nil)) & Params(params)) =>
        val paramsMap = params.toMap[String, Seq[String]]
        ResponseString(paramsMap.getOrElse("wurst", Seq("nix geworden")).mkString(","))

      case req@GET(Path(Seg(base :: "sessions" :: sessionId :: Nil)) & Params(params)) =>
        val q = for (s <- sessions) yield s
        val a = q.result
        val f: Future[Seq[QuestionSession]] = db.run(a)
        val sess = Await.result(f, Duration(10, duration.SECONDS))
        val targetSession = sess.filter(_.id == sessionId.toInt).headOption
          .getOrElse(throw new InvalidParameterException(s"no session with id $sessionId in db"))
        ResponseString(write(targetSession))
    }
  }

  def htmlRoot: String = "learn4good"

  def setup(args: Args): unfiltered.filter.Plan = {
    new ServerApp(args)
  }

}

class Args extends Arguments {

}
