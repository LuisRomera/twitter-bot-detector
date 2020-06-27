package es.luis.detector

import com.typesafe.config.{Config, ConfigFactory}
import es.luis.detector.config.SparkConfig
import org.apache.spark.SparkConf
import org.slf4j.LoggerFactory

object InitApp {
  final val log = LoggerFactory.getLogger(getClass.getName)

  def main(args: Array[String]): Unit = {

    val config:Config = ConfigFactory.load(args(0))
    log.info(s"Init App ${config.getString("app.name")}")

    val spark = new SparkConfig(config).startSparkSession()




  }

}
