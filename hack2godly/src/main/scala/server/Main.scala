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
    val tags = TagTable.props

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
        List(DocumentTag(43, "kultur", Languages.DEUTSCH, 123545)))
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
        List(DocumentTag(43, "kultur", Languages.DEUTSCH, 123545)))
    }

    // setup data with some examples
    val setup = DBIO.seq(
      docs.schema.create,
      sessions.schema.create,
      tags.schema.create,
      docs += Document(0, "content", "test", "tags"),
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
      // Get all information for one session (including all messages)
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

      // Get all sessions which do not have a helper assigned (without their messages)
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

      // Write new message
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
        db.run(updateQ.update(write(oldMessages ++ Seq(newMessage))))
        Thread.sleep(1000)
        ResponseString("updated")

      // Insert a new document
      case req@POST(Path(Seg(base :: "documents" :: Nil)) & Params(params)) =>
        val paramsMap = params.toMap[String, Seq[String]]
        val title = paramsMap("title").head
        val url = paramsMap("url").head
        // try to determine filetype
        val filetype = paramsMap("fileType").headOption.getOrElse {
          if (url.endsWith(".pdf")) {
            FileTypes.PDF
          } else if (url.endsWith(".png") ||
            url.endsWith(".jpg") ||
            url.endsWith(".gif") ||
            url.endsWith(".jpeg") ||
            url.endsWith(".bmp")) {
            FileTypes.IMAGE
          } else if (url.endsWith(".mp3") ||
            url.endsWith(".mpeg") ||
            url.endsWith(".wav") ||
            url.endsWith(".wma") ||
            url.endsWith(".ogg") ||
            url.endsWith(".mpc") ||
            url.endsWith(".m4p") ||
            url.endsWith(".m4a") ||
            url.endsWith(".flac") ||
            url.endsWith(".aac")) {
            FileTypes.SOUNDFILE
          } else if (url.contains("youtube") ||
            url.contains("vimeo") ||
            url.endsWith(".flv") ||
            url.endsWith(".webm") ||
            url.endsWith(".mkv") ||
            url.endsWith(".vob") ||
            url.endsWith(".ogv") ||
            url.endsWith(".drc") ||
            url.endsWith(".gif") ||
            url.endsWith(".gifv") ||
            url.endsWith(".avi") ||
            url.endsWith(".mov") ||
            url.endsWith(".mpv") ||
            url.endsWith(".m2v") ||
            url.endsWith(".m4v") ||
            url.endsWith(".svi") ||
            url.endsWith(".mxf") ||
            url.endsWith(".flv")) {
            FileTypes.VIDEO
          } else {
            FileTypes.WEBPAGE
          }
        }
        addTagsAndReturn(paramsMap("tags").headOption.map{ t => read[Seq[SimpleTag]](t) }.getOrElse(Seq())) // maybe never used

        val insert = DBIO.seq(
         docs += Document(-1, url, filetype, s"""[{"name":"$language","language":"$language"}]""")
        )
        db.run(insert)
        ResponseString("Inserted Document")

//      case req@GET(Path(Seg()))
    }

    def addTagsAndReturn(inputTags: Seq[SimpleTag]): Seq[DocumentTag] = {
      // first add all missing tags
      for (simpleTag <- inputTags) {
        val q = for {
          t <- tags if t.name === simpleTag.name && t.lang === simpleTag.language
        } yield t.id

        val a = q.result
        val f: Future[Seq[(Int)]] = db.run(a)
        val res = Await.result(f, Duration(10, duration.SECONDS))
        //Add tag, if not in DB
        if (res.length == 0) {
          val insert = DBIO.seq(
            tags += DocumentTag(-1, simpleTag.name, simpleTag.language, new DateTime().getMillis)
          )
          db.run(insert)
        }
      }

      // then return them all
      val results = for {
        simpleTag <- inputTags
        q = for (t <- tags if t.name === simpleTag.name && t.lang === simpleTag.language) yield (t.id, t.name, t.lang, t.date)
        a = q.result
        f = db.run(a)
      } yield {
        Await.result(f, Duration(10, duration.SECONDS))
      }

      results.flatMap { case taglist =>
        taglist.map { case (id, name, lang, date) =>
          DocumentTag(id, name, lang, date)
        }
      }
    }
  }

  def htmlRoot: String = "learn4good"

  def setup(args: Args): unfiltered.filter.Plan = {
    new ServerApp(args)
  }
}

class Args extends Arguments {

}
