package rifs.business

case class Config(cache: CachesConfig)

case class CachesConfig(user: CacheConfig)

case class CacheConfig(timeout: Int)

object Config {

  import pureconfig._

  lazy val config: Config = loadConfig[Config].get
}