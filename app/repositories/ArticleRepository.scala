package repositories

import models.Article
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ArticleRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig.*
  import profile.api.*

  private class ArticleTable(tag: Tag) extends Table[Article](tag, "article") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def title = column[String]("title")

    def body = column[String]("body")

    def * = (id, title, body) <> ((Article.apply).tupled, Article.unapply)
  }

  private val article = TableQuery[ArticleTable]

  def list(): Future[Seq[Article]] = db.run(article.result)

  def create(title: String, body: String): Future[Article] = db.run {
    (article.map(a => (a.title, a.body))
      returning article.map(_.id)
      into ((titleBody, id) => Article(id, titleBody._1, titleBody._2))
      ) += (title, body)
  }
}
