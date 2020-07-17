package es.luis.detector

import org.slf4j.LoggerFactory
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.{EnableAutoConfiguration, SpringBootApplication}
import org.springframework.core.env.Environment

@SpringBootApplication
class InitApp

object InitApp {
  final val log = LoggerFactory.getLogger(getClass.getName)

  def main(args: Array[String]): Unit = {

    val app = SpringApplication.run(classOf[InitApp], args:_*)

    val env: Environment = app.getEnvironment
  }

  /*
  def main(args: Array[String]): Unit = {
  }*/

}
