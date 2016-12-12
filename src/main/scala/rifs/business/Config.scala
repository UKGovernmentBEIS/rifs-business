package rifs.business

case class Config(rifs: RifsConfig)

case class RifsConfig(email: EmailConfig)

case class EmailConfig(dummyapplicant: String, replyto: String, dummymanager: String)

object Config {

  import pureconfig._

  lazy val config: Config = loadConfig[Config].get
}