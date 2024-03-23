package repositories

import models.Article
import models.Comment
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ArticleRepository @Inject()(val dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) extends HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api.*

  private class ArticleTable(tag: Tag) extends Table[Article](tag, "article") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def title = column[String]("title")

    def body = column[String]("body")

    def * = (id, title, body) <> (Article.apply, Article.unapply)
  }

  private val articles = TableQuery[ArticleTable]


  private class CommentTable(tag: Tag) extends Table[Comment](tag, "comment") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def commenter = column[String]("commenter")

    def body = column[String]("body")

    def articleId = column[Long]("article_id")

    def * = (id, commenter, body, articleId) <> (Comment.apply, Comment.unapply)

    def article = foreignKey("article_fk", articleId, TableQuery[ArticleTable])(_.id)
  }

  private val comments = TableQuery[CommentTable]

  def all(): Future[Seq[Article]] = db.run(articles.result)

  def create(title: String, body: String): Future[Article] = db.run {
    (articles.map(a => (a.title, a.body))
      returning articles.map(_.id)
      into ((titleBody, id) => Article(id, titleBody._1, titleBody._2))
      ) += (title, body)
  }

  def update(articleId: Long, title: String, body: String): Future[Unit] = {
    db.run(
      articles
        .filter(_.id === articleId)
        .map(a => (a.title, a.body))
        .update((title, body))
    ).map(_ => ())
  }

  def delete(articleId: Long): Future[Unit] = {
    db.run(articles.filter(_.id === articleId).delete).map(_ => ())
  }

  def findById(articleId: Long): Future[Option[Article]] = db.run(articles.filter(_.id === articleId).result.headOption)

  def findByIdWithComment(articleId: Long): Any = {
    val query = for {
      (article, comment) <- articles join comments on (_.id === _.articleId)
    } yield (article, comment)
    db.run(query.result)
  }
  
  def createComment(commenter: String, body: String, articleId: Long): Future[Unit] = db.run(comments.map(c => (c.commenter, c.body, c.articleId)) += (commenter, body, articleId)).map { _ => () }
}
