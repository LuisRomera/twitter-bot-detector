package es.luis.detector

import com.typesafe.config.{Config, ConfigFactory}
import es.luis.detector.config.SparkConfig
import es.luis.detector.service.UserProcesor
import org.apache.log4j.{Level, Logger}



object InitApp {
//  final val log = LoggerFactory.getLogger(getClass.getName)
    val log = Logger.getLogger(InitApp.getClass)

  def main(args: Array[String]): Unit = {
//    val spark = SparkSession.builder.getOrCreate()
//
    val config:Config = ConfigFactory.load(args(0))
    val spark = new SparkConfig(config).startSparkSession()

    spark.sparkContext.setLogLevel("ERROR")

    log.setLevel(Level.INFO)
    log.info("********************************************")
    log.info(s"File: ${args.mkString(", ")}" )
    log.info("********************************************")

    log.info(s"Init App ${config.getString("app.name")}")



    new UserProcesor(config, spark).run()


  }
}
