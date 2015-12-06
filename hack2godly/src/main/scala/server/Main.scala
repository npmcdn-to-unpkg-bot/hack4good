package server

import java.security.InvalidParameterException

import database._
import org.joda.time.DateTime
import server.Protocol._

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
  val DefaultTimeout = Duration(10, duration.SECONDS)

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

    def postNewQuestion(ownerId: Int, lang: String, data: String, topic: String): Int = {
      val languageTag = {
        val q = for (tag <- tags if tag.name === lang && tag.lang === lang) yield (tag.id, tag.date)
        val a = q.result
        val f = db.run(a)
        Await.result(f, DefaultTimeout).headOption match {
          case Some((id, date)) => Seq(DocumentTag(id, lang, lang, date))
          case None => addTagsAndReturn(Seq(TagPost(lang, lang)))
        }
      }

      val sessionId = {
        val q =  (sessions returning sessions.map(_.id)) +=
          QuestionSession(-1, topic, data, ownerId, new DateTime().getMillis, -1, List(), languageTag.toList)
        val f =db.run(q)
        Await.result(f, DefaultTimeout)
      }
      sessionId
    }

    def intent = {

      // Post a new question
      case req@POST(Path(Seg(base :: "sessions" :: Nil)) & Params(params)) =>
        val paramsMap = params.toMap[String, Seq[String]]
        val bodyString = Body.string(req)
        val ownerId = paramsMap("ownerId").head.toInt
        val lang = paramsMap("language").head.toLowerCase
        val usedLang = if(Languages.AllLangs.contains(lang)) lang else Languages.UNDEFINED
        val question = read[QuestionPost](bodyString)
        ResponseString(postNewQuestion(ownerId, lang, question.data, question.topic).toString)

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
        println(s"Writing new message for session $sessionId")
        val q = for {
          sq <- sessions if sq.id === sessionId.toInt
        } yield (sq.messages, sq.ownerId)
        val a = q.result
        val f: Future[Seq[(String, Int)]] = db.run(a)
        val sess = Await.result(f, Duration(10, duration.SECONDS)).headOption.getOrElse(throw new IllegalArgumentException(s"No session with id $sessionId found"))
        val oldMessages = read[Seq[Message]](sess._1)
        println(s"found ${oldMessages.length} messages for session $sessionId")
        val bodyString = Body.string(req)
        val newMessageContent = read[MessagePost](bodyString)
        val newMessage = Message(newMessageContent.message, new DateTime().getMillis, sessionId.toInt, sess._2)
        val updateQ = for {
          sq <- sessions if sq.id === sessionId.toInt
        } yield sq.messages
        db.run(updateQ.update(write(oldMessages ++ Seq(newMessage))))
        ResponseString(s"Added message for session $sessionId")

      // Insert a new document
      case req@POST(Path(Seg(base :: "documents" :: Nil)) & Params(params)) =>
        println("inserting new document")
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
        val bodyString = Body.string(req)

        val additionalTags = if (!bodyString.isEmpty) read[Seq[TagPost]](bodyString) else Seq()

        println(s"found ${additionalTags.length} tags in request")

        val tagsForSession = addTagsAndReturn(additionalTags) // maybe never used

        val tagged = tagsForSession.length > 1

        println(s"writing document to db: title -> $title, url -> $url, filetype -> $filetype")
        val insert = DBIO.seq(
         docs += Document(-1, url, filetype, write(tagsForSession), tagged)
        )
        db.run(insert)
        ResponseString("Inserted Document")

      // Add tags to a document
      case req@POST(Path(Seg(base :: "documents" :: docId :: "tags" :: Nil)) & Params(params)) =>
        val bodyString = Body.string(req)
        val newTags = read[AddTagsPost](bodyString).tags
        addTagsToDocument(docId.toInt, newTags)
        ResponseString(s"Added ${newTags.length} new tags to document with id $docId")

      // Return all untagged documents
      case req@GET(Path(Seg(base :: "documents" :: Nil))) =>
        println("getting all untagged documents")
        ResponseString(write(getUntaggedDocuments))

      //get all docs for one session
      case req@GET(Path(Seg(base :: "sessions" :: sessionId :: "documents" :: Nil)) & Params(params)) =>
        println(s"getting all documents for session $sessionId")
        ResponseString(write(getDocumentsForSession(sessionId.toInt)))

      // get all documents for given language
      case req@GET(Path(Seg(base :: "documents" :: "languages" :: Nil)) & Params(params)) =>
        println(s"getting all documents for a language")
        val paramsMap = params.toMap[String, Seq[String]]
        val language = paramsMap("language").head
        println(s"language: $language")
        ResponseString(write(getDocumentsForLanguage(language)))
    }

    val alphaRegex = """[a-zA-Z]"""
    val boundRegex = """\b"""
    // parses through the tokenized text and tries to find tags from them
    def getDocumentsForSession(sessionId: Int): Seq[SendableDocument] = {
      val ts = getAllTags
      // tokenize all messages and search for tags
      val messageTokens = {
        val q = for (s <- sessions if s.id === sessionId) yield s.messages
        val a = q.result
        val f = db.run(a)
        val messages = Await.result(f, DefaultTimeout).headOption.map{ read[Seq[Message]](_)}.getOrElse(Seq())
        messages.flatMap{ case m =>
          val data = m.data
            data.split(boundRegex).map{_.trim.toLowerCase}.filter{_.matches(alphaRegex)}.toSet
        }.toSet
      }

      val relevantTags = ts.filter{ case t => messageTokens.contains(t.name)}
      relevantTags.flatMap{ getDocumentsForTag(_)}
    }

    def getAllTags(): Seq[DocumentTag] = {
      println("getting all tags")
      val q = for (t <- tags) yield (t.id, t.lang, t.name, t.date)
      val a = q.result
      val f = db.run(a)
      Await.result(f, DefaultTimeout).map{ case (id, lang, name, date) => DocumentTag(id, lang, name, date)}
    }

    def getDocumentsForLanguage(language: String): Seq[SendableDocument] = {
      println(s"==> ")
      getAllSendableDocs.filter{_.tags.exists{ _.lang == language.toLowerCase}}
    }

    def getDocumentsForTag(tag: DocumentTag): Seq[SendableDocument] = {
      val lang = tag.lang.toLowerCase
      val name = tag.name.toLowerCase
      println(s"getting all documents for tag $name")
      getAllSendableDocs.filter{_.tags.exists{_.name == name}}
    }

    def getAllSendableDocs() = {
      docsToSendableDocs(getAllDocs)
    }

    def docsToSendableDocs(docs: Seq[Document]) = {
      docs.map{doc => SendableDocument(doc.id, doc.url, doc.typ, read[Seq[DocumentTag]](doc.tags), doc.tagged)}
    }

    def getAllDocs() = {
      val q = for (doc <- docs ) yield (doc.id, doc.url, doc.typ, doc.tags, doc.tagged)
      val a = q.result
      val f = db.run(a)
      Await.result(f, DefaultTimeout).map{ case (id, url, typ, tags, tagged) => Document(id, url, typ, tags, tagged)}
    }

    def addTagsToDocument(docId: Int, tags: Seq[DocumentTag]) = {
      val oldTags = getDocumentTags(docId)
      val newTags = oldTags ++ tags
      val q = for(doc <- docs if doc.id === docId) yield (doc.tags, doc.tagged)
      db.run(q.update(write(newTags), true))
      println(s"Updated tags for document with id $docId")
    }

    def getDocumentTags(docId: Int) = {
      val q = for(doc <- docs if doc.id === docId) yield doc.tags
      val a = q.result
      val f = db.run(a)
      read[Seq[DocumentTag]](Await.result(f, DefaultTimeout).headOption
          .getOrElse(throw new IllegalArgumentException(s"No document with id $docId found")))
    }

    def getUntaggedDocuments(): Seq[SendableDocument] = {
      println("getting untagged documents")
      val q = for(doc <- docs if doc.tagged === false) yield (doc.id, doc.url, doc.typ, doc.tags)
      val a = q.result
      val f = db.run(a)
      Await.result(f, DefaultTimeout)
        .map{ case (id, url, typ, taggos) =>
        println(taggos)
        SendableDocument(id, url, typ, read[Seq[DocumentTag]](taggos), false)
      }
    }

    def addTagsAndReturn(inputTags: Seq[TagPost]): Seq[DocumentTag] = {
      // first add all missing tags
      for (simpleTag <- inputTags) {
        val q = for {
          t <- tags if t.name === simpleTag.name.toLowerCase && t.lang === simpleTag.language.toLowerCase
        } yield t.id

        val a = q.result
        val f: Future[Seq[(Int)]] = db.run(a)
        val res = Await.result(f, Duration(10, duration.SECONDS))
        //Add tag, if not in DB
        if (res.length == 0) {
          val insert = DBIO.seq(
            tags += DocumentTag(-1, simpleTag.name.toLowerCase, simpleTag.language.toLowerCase, new DateTime().getMillis)
          )
          db.run(insert)
        }
      }

      // then return them all
      val results = for {
        simpleTag <- inputTags
        q = for (t <- tags if t.name === simpleTag.name.toLowerCase && t.lang === simpleTag.language.toLowerCase)
          yield (t.id, t.name, t.lang, t.date)
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
