package server

import database.DocumentTag

/**
 * Created by jannis on 12/6/15.
 *
 * Contains classes which are just for communication to fulfil the API
 */
object Protocol {
  case class SimpleMessage(message: String)
  case class SimpleTag(name: String, language: String)
  case class Tags(tags: Seq[DocumentTag])
}
