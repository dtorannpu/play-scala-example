package controllers

import play.api.mvc.{AnyContent, BaseController, ControllerComponents, Request}
import repositories.ArticleRepository

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class ArticlesController @Inject(articleRepository: ArticleRepository)(val controllerComponents: ControllerComponents)(implicit ec: ExecutionContext) extends BaseController {
  def index() = Action.async { implicit request: Request[AnyContent] =>
    articleRepository.list().map(articles => Ok(views.html.articles.index(articles)))
  }

  def show(id:Long) = Action.async { implicit request: Request[AnyContent] =>
    articleRepository.findById(id).map {
      case Some(article) => Ok(views.html.articles.show(article))
      case None => NotFound("ページが存在しません。")
    }
  }
}
