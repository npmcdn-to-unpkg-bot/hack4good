package server

import database.DocumentTag

/**
 * Created by jannis on 12/6/15.
 *
 * Contains classes which are just for communication to fulfil the API
 */
object Protocol {
  case class MessagePost(message: String)
  case class TagPost(name: String, language: String)
  case class AddTagsPost(tags: Seq[DocumentTag])
  case class QuestionPost(data: String, topic: String)
}
