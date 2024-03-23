package models

case class Comment(id: Long, commenter: String, body: String, articleId: Long)