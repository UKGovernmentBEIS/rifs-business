package rifs.business

case class Config(cache: CacheConfig)

case class CacheConfig(timeout: Option[Int])

object Config {

  import pureconfig._

  lazy val config: Config = loadConfig[Config].get
}