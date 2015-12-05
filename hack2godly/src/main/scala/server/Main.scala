package server

import java.security.InvalidParameterException

import database._
import org.joda.time.DateTime

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
          Message("du wurst", 12345, 42, 12),
          Message("trink wurstwasser", 12346, 42, 12),
          Message("na gut", 12347, 42, 12)),
        List(DocumentTag(43, "kultur", Languages.Deutsch, 123545)))
    }

    val sessNoHelp = for(i <- (10001 to 10010)) yield {
      QuestionSession(
        i,
        "wurstwasserr trinken", "Muss ich, wenn ich in Deutschland normal sein will, jeden tag wurstwasser trinken?",
        12,
        1235465,
        -1,
        List(
          Message("du wurst", 12345, 42, 12),
          Message("trink wurstwasser", 12346, 42, 12),
          Message("na gut", 12347, 42, 12)),
        List(DocumentTag(43, "kultur", Languages.Deutsch, 123545)))
    }

    // setup data with some examples
    val setup = DBIO.seq(
      docs.schema.create,
      sessions.schema.create,
      docs += Document(0, "content", "owner", "tags"),
      sessions ++= sess.toSeq,
      sessions ++= sessNoHelp.toSeq
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
        val q = for {
          sq <- sessions if sq.id === sessionId.toInt
        } yield (sq.id, sq.topic, sq.data, sq.ownerId, sq.date, sq.helperId, sq.messages, sq.tags)
        val a = q.result
        val f: Future[Seq[(Int, String, String, Int, Long, Int, String, String)]] = db.run(a)
        val sess = Await.result(f, Duration(10, duration.SECONDS))
        ResponseString(write(
          sess.map { case (id, topic, data, owner, date, helper, messages, tags) =>
            QuestionSession(id, topic, data, owner, date, helper,
              read[Seq[Message]](messages).toList, read[Seq[DocumentTag]](tags).toList)
          }.head))

      case req@GET(Path(Seg(base :: "sessions" :: Nil)) & Params(params)) =>
        val q = for {
          sq <- sessions if sq.helperId < 0
        } yield (sq.id, sq.topic, sq.data, sq.ownerId, sq.date, sq.helperId, sq.tags)
        val a = q.result
        val f: Future[Seq[(Int, String, String, Int, Long, Int, String)]] = db.run(a)
        val sess = Await.result(f, Duration(10, duration.SECONDS))
        ResponseString(write(sess.map { case(id, topic, data, owner, date, helper, tags) =>
          Question(id, topic, data, owner, date, helper, read[Seq[DocumentTag]](tags).toList)
        }))

      case req@POST(Path(Seg(base :: "sessions" :: sessionId :: Nil)) & Params(params)) =>
        val q = for {
          sq <- sessions if sq.id === sessionId.toInt
        } yield (sq.messages, sq.ownerId)
        val a = q.result
        val f: Future[Seq[(String, Int)]] = db.run(a)
        val sess = Await.result(f, Duration(10, duration.SECONDS)).head
        val oldMessages = read[Seq[Message]](sess._1)
        val bodyString = Body.string(req)
        val newMessageContent = read[SimpleMessage](bodyString)
        val newMessage = Message(newMessageContent.message, new DateTime().getMillis, sessionId.toInt, sess._2)
        val updateQ = for {
          sq <- sessions if sq.id === sessionId.toInt
        } yield sq.messages
        println(write(oldMessages ++ Seq(newMessage)))
        db.run(updateQ.update(write(oldMessages ++ Seq(newMessage))))
        Thread.sleep(1000)
        ResponseString("updated")




    }
  }

  def htmlRoot: String = "learn4good"

  def setup(args: Args): unfiltered.filter.Plan = {
    new ServerApp(args)
  }

}

class Args extends Arguments {

}
