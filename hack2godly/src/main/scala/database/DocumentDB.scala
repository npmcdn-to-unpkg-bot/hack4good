package database

import slick.driver.H2Driver.api._
import org.json4s._
import org.json4s.native.Serialization
import org.json4s.native.Serialization.{read, write}

/**
 * Created by jannis on 12/5/15.
 */

object Languages {
  val ENGLISH   = "en"
  val DEUTSCH   = "de"
  val AllLangs  = Set(ENGLISH, DEUTSCH)
  val UNDEFINED = "Undefined"
}

object FileTypes {
  val PDF       = "PDF"
  val SOUNDFILE = "SOUND"
  val VIDEO     = "VIDEO"
  val WEBPAGE   = "PAGE"
  val IMAGE     = "IMG"
}

object TypeConversion {
  import UserRole._
  implicit def user2Role(s: String): UserRole = {
    UserRole.values.toList.find{ r => r.toString == s}.getOrElse(Undefined)
  }

  implicit def role2User(r: UserRole): String = {
    r.toString
  }
}

object UserRole extends Enumeration {
  type UserRole = Value
  val JediMaster, Padawan, Undefined= Value
}

case class User(
  id: Int,
  name: String,
  role: UserRole.UserRole,
  avatarUrl: String,
  date: Long)

object UserTable {
  import UserRole._
  val tableName = "users"
  def props = TableQuery[Props]

  def parseRow(row: (Int, String, String, String, Long)) = {
    User(row._1, row._2, TypeConversion.user2Role(row._3), row._4, row._5)
  }

  def writeRow(user: User) = {
    Some((user.id, user.name, user.role.toString, user.avatarUrl, user.date))
  }

  class Props(tag: Tag) extends Table[User](tag, tableName) {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")
    def role = column[String]("name")
    def avatarUrl = column[String]("name")
    def date = column[Long]("timestamp")
    def * = (id, name, role, avatarUrl, date).shaped <> (parseRow, writeRow)
  }
}


case class DocumentTag(
  id: Int,
  name: String,
  lang: String,
  date: Long)

object TagTable {
  val tableName = "tags"
  def props = TableQuery[Props]

  class Props(tag: Tag) extends Table[DocumentTag](tag, tableName) {

    def parseRow(row: (Int, String, String, Long)) = {
      DocumentTag(row._1, row._2, row._3, row._4)
    }

    def writeRow(tag: DocumentTag) = {
      Some((tag.id, tag.name, tag.lang.toString, tag.date))
    }

    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")
    def lang = column[String]("lang")
    def date = column[Long]("timestamp")
    def * = (id, name, lang, date).shaped <> (parseRow, writeRow)
  }
}

case class Document(
  id: Int,
  url: String,
  typ: String,
  tags: String,
  tagged: Boolean = false)


object DocumentTable {
  val tableName = "docs"
  def props = TableQuery[Props]

  class Props(tag: Tag) extends Table[Document](tag, tableName){
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def url = column[String]("content") // implicitly TEXT (unlimited)
    def typ = column[String]("fileType")
    def tags = column[String]("tags")
    def tagged = column[Boolean]("tagged")
    def * = (id, url, typ, tags, tagged).shaped <> (Document.tupled, Document.unapply)
  }
}

case class QuestionSession(
  id: Int,
  topic: String,
  data: String,
  ownerId: Int,
  date: Long,
  helperId: Int,
  messages: List[Message],
  tags: List[DocumentTag])

object SessionTable {
  implicit val formats = Serialization.formats(NoTypeHints)
  val tableName = "sessions"
  def props = TableQuery[Props]

  class Props(tag: Tag) extends Table[QuestionSession](tag, tableName) {
    def parseRow(row: (Int, String, String, Int, Long, Int, String, String)) = {
      QuestionSession(
        row._1,
        row._2,
        row._3,
        row._4,
        row._5,
        row._6,
        read[List[Message]](row._7),
        read[List[DocumentTag]](row._8))
    }

    def writeRow(s: QuestionSession) = {
      Some((s.id, s.topic, s.data, s.ownerId, s.date, s.helperId,
        write(s.messages), write(s.tags)))
    }

    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def topic = column[String]("topic")
    def data = column[String]("data")
    def ownerId = column[Int]("owner_id")
    def date = column[Long]("timestamp")
    def helperId = column[Int]("helper_id", O.Default(-1))
    def messages = column[String]("messages")
    def tags = column[String]("tags")
    def * = (id, topic, data, ownerId, date, helperId, messages, tags).shaped <> (parseRow, writeRow)
  }
}


case class Question(
  id: Int,
  topic: String,
  data: String,
  ownerId: Int,
  date: Long,
  helperId: Int,
  tags: List[DocumentTag])

case class Message(
  data: String,
  date: Long,
  sessionId: Int,
  ownerId: Int)

