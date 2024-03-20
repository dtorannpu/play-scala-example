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

@Singleton
class ArticlesController @Inject()(val articleRepository: ArticleRepository, val mcc: MessagesControllerComponents)(implicit ec: ExecutionContext) extends MessagesAbstractController(mcc) {


  val articleForm: Form[ArticleData] = Form(
    mapping(
      "title" -> nonEmptyText,
      "body" -> nonEmptyText(minLength = 10),
    )(ArticleData.apply)(ArticleData.unapply)
  )

  def index(): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    articleRepository.all().map(articles => Ok(views.html.articles.index(articles)))
  }

  def show(id: Long): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    articleRepository.findById(id).map(_.fold(NotFound) { article => Ok(views.html.articles.show(article)) })
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

  def edit(id: Long): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    articleRepository.findById(id).map(_.fold(NotFound) { article => Ok(views.html.articles.edit(article.id, articleForm.fill(ArticleData(article.title, article.body)))) })
  }

  def update(id: Long): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    articleForm
      .bindFromRequest()
      .fold(
        formWithErrors => {
          Future.successful(BadRequest(views.html.articles.edit(id, formWithErrors)))
        },
        articleData => {
          articleRepository.update(id, articleData.title, articleData.body).map(a => Redirect(routes.ArticlesController.show(id)))
        }
      )
  }

  def delete(id: Long) : Action[AnyContent] = Action.async {
    articleRepository.delete(id).map(_ => Ok)
  }
}
