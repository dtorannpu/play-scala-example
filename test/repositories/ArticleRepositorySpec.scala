package repositories

import org.scalatest.*
import org.scalatest.matchers.should.Matchers._
import org.scalatestplus.play.*

import scala.language.postfixOps

class ArticleRepositorySpec extends H2RepositorySpec {
  private lazy val articleRepository = inject[ArticleRepository]

  "ArticleRepository" should {
    "all" in {
      whenReady(articleRepository.all()) { articles =>
        articles should have size 0
      }
    }
    "create and find" in {
      whenReady(articleRepository.create("タイトル", "内容")) { a =>
        whenReady(articleRepository.findById(a.id)) { article =>
          article shouldBe defined
          article.value.title should equal ("タイトル")
        }
      }
    }
  }
}