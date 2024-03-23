package repositories

import org.scalatest.*
import org.scalatest.matchers.should.Matchers
import org.scalatest.matchers.should.Matchers.should
import org.scalatestplus.play.*

class ArticleRepositorySpec extends H2RepositorySpec {
  private lazy val articleRepository = inject[ArticleRepository]
  
  "" in {
    var result = articleRepository.all()
    whenReady(articleRepository.all()) { articles =>
      articles should have size 0
    }
  }
}