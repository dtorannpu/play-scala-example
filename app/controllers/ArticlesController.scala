package controllers

import play.api.data.*
import play.api.data.Forms.*
import play.api.mvc.*
import repositories.ArticleRepository

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

case class ArticleData(title: String, body: String)

object ArticleData {
  def unapply(a: ArticleData): Option[(String, String)] = Some((a.title, a.body))
}

case class CommentData(commenter: String, body: String)

object CommentData {
  def unapply(c: CommentData): Option[(String, String)] = Some((c.commenter, c.body))
}

@Singleton
class ArticlesController @Inject()(val articleRepository: ArticleRepository, val mcc: MessagesControllerComponents)(implicit ec: ExecutionContext) extends MessagesAbstractController(mcc) {


  val articleForm: Form[ArticleData] = Form(
    mapping(
      "title" -> nonEmptyText,
      "body" -> nonEmptyText(minLength = 10),
    )(ArticleData.apply)(ArticleData.unapply)
  )

  val commentForm: Form[CommentData] = Form(
    mapping(
      "commenter" -> nonEmptyText,
      "body" -> nonEmptyText,
    )(CommentData.apply)(CommentData.unapply)
  )

  def index(): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    articleRepository.all().map(articles => Ok(views.html.articles.index(articles)))
  }

  def show(articleId: Long): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    articleRepository.findById(articleId).map(_.fold(NotFound) { article => Ok(views.html.articles.show(article, commentForm)) })
  }

  def newArticle: Action[AnyContent] = Action { implicit request: MessagesRequest[AnyContent] =>
    Ok(views.html.articles.newArticle(articleForm))
  }

  def create: Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    articleForm
      .bindFromRequest()
      .fold(
        formWithErrors => {
          Future.successful(BadRequest(views.html.articles.newArticle(formWithErrors)))
        },
        articleData => {
          articleRepository.create(articleData.title, articleData.body).map(a => Redirect(routes.ArticlesController.show(a.id)))
        }
      )
  }

  def edit(articleId: Long): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    articleRepository.findById(articleId).map(_.fold(NotFound) { article => Ok(views.html.articles.edit(article.id, articleForm.fill(ArticleData(article.title, article.body)))) })
  }

  def update(articleId: Long): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    articleForm
      .bindFromRequest()
      .fold(
        formWithErrors => {
          Future.successful(BadRequest(views.html.articles.edit(articleId, formWithErrors)))
        },
        articleData => {
          articleRepository.update(articleId, articleData.title, articleData.body).map(a => Redirect(routes.ArticlesController.show(articleId)))
        }
      )
  }

  def delete(articleId: Long): Action[AnyContent] = Action.async {
    articleRepository.delete(articleId).map(_ => Ok)
  }

  def createComment(articleId: Long): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    commentForm
      .bindFromRequest()
      .fold(
        formWithErrors => {
          articleRepository.findById(articleId).map(_.fold(NotFound) { article => Ok(views.html.articles.show(article, formWithErrors)) })
        },
        commentData => {
          articleRepository.createComment(commentData.commenter, commentData.body, articleId).map(c => Redirect(routes.ArticlesController.show(articleId)))
        }
      )
  }
}
