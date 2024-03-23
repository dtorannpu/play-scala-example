package repositories

import org.scalatest.BeforeAndAfterEach
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.Injecting

trait H2RepositorySpec extends PlaySpec with GuiceOneAppPerSuite with Injecting with ScalaFutures with BeforeAndAfterEach {
  override def fakeApplication(): Application = new
      GuiceApplicationBuilder().configure(
    "slick.dbs.default.profile" -> "slick.jdbc.H2Profile$",
    "slick.dbs.default.db.driver" -> "org.h2.Driver",
    "slick.dbs.default.db.url" -> "jdbc:h2:mem:play;MODE=MYSQL;DATABASE_TO_UPPER=false",
  ).build()
}
