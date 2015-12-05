package database

import org.joda.time.DateTime
import slick.driver.H2Driver.api._

/**
 * Created by jannis on 12/5/15.
 */

object Languages {
  val English = "ENGLISH"
  val Deutsch = "DEUTSCH"
}

object TypeConversion {
  import UserRole._
  val Sep = "⬲"
  implicit def user2Role(s: String): UserRole = {
    UserRole.values.toList.find{ r => r.toString == s}.getOrElse(Undefined)
  }

  implicit def role2User(r: UserRole): String = {
    r.toString
  }

  implicit def long2Date(l: Long): DateTime = {
    new DateTime(l)
  }

  implicit def date2Long(d: DateTime): Long = {
    d.getMillis
  }

  def packMessage(messages: List[String]) = {
    messages.mkString(Sep)
  }

  def unpackMessages(messages: String) = {
    messages.split(Sep).toList
  }

  def packList[T](l: List[T]) = {
    l.mkString(Sep)
  }

  def unpackList[T](s: String) = {
    s.split(Sep).map { _.toString}
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
  date: DateTime)

object UserTable {
  import UserRole._
  val tableName = "users"
  def props = TableQuery[Props]

  def parseRow(row: (Int, String, String, String, Long)) = {
    User(row._1, row._2, TypeConversion.user2Role(row._3), row._4, TypeConversion.long2Date(row._5))
  }

  def writeRow(user: User) = {
    Some((user.id, user.name, user.role.toString, user.avatarUrl, user.date.getMillis))
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
  lang: UserRole.UserRole,
  date: DateTime)

object TagTable {
  val tableName = "tags"
  def props = TableQuery[Props]

  class Props(tag: Tag) extends Table[DocumentTag](tag, tableName) {

    def parseRow(row: (Int, String, String, Long)) = {
      DocumentTag(row._1, row._2, TypeConversion.user2Role(row._3), new DateTime(row._4))
    }

    def writeRow(tag: DocumentTag) = {
      Some((tag.id, tag.name, tag.lang.toString, tag.date.getMillis))
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
  content: String,
  owner: String,
  tags: String)

object DocumentTable {
  val tableName = "docs"
  def props = TableQuery[Props]

  class Props(tag: Tag) extends Table[Document](tag, tableName){
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def content = column[String]("content") // implicitly TEXT (unlimited)
    def owner = column[String]("owner")
    def tags = column[String]("tags")
    def * = (id, content, owner, tags).shaped <> (Document.tupled, Document.unapply)
  }
}

case class Session(
  id: Int,
  topic: String,
  data: String,
  ownerId: Int,
  date: DateTime,
  helperId: Int,
  messages: List[Int],
  tags: List[Int])

object SessionTable {
  val tableName = "sessions"
  def props = TableQuery[Props]

  class Props(tag: Tag) extends Table[Session](tag, tableName) {
    def parseRow(row: (Int, String, String, Int, Long, Int, String, String)) = {
      Session(
        row._1,
        row._2,
        row._3,
        row._4,
        new DateTime(row._5),
        row._6,
        List(),
        List())
    }

    def writeRow(s: Session) = {
      Some(s.id, s.topic, s.data, s.ownerId, s.date.getMillis, s.helperId,
        s.messages.mkString(""), s.tags.mkString(""))
    }

    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def topic = column[String]("topic")
    def data = column[String]("data")
    def ownerId = column[Int]("owner_id")
    def date = column[Long]("timestamp")
    def helperId = column[Int]("helper_id")
    def messages = column[String]("messages")
    def tags = column[String]("tags")
    def * = (id, topic, data, ownerId, date, helperId, messages, tags).shaped <> (parseRow, writeRow)
  }
}
case class Message(
  id: Long,
  data: String,
  date: DateTime,
  sessionId: Int,
  ownerId: Int)


//object TagTable {
//  val tableName = "tags"
//  def props = TableQuery[Props]
//}

