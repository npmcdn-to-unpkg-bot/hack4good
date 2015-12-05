package database

import slick.driver.H2Driver.api._
/**
 * Created by jannis on 12/5/15.
 */
object DocumentTable {
  val tableName = "docs"
  def props = TableQuery[Props]

  class Props(tag: Tag) extends Table[Document](tag, tableName){
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def content = column[String]("content")
    def owner = column[String]("owner")
    def tags = column[String]("tags")
    def * = (id, content, owner, tags).shaped <> (Document.tupled, Document.unapply)
  }
}

case class Document(id: Int, content: String, owner: String, tags: String)
