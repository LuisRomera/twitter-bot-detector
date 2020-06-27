package es.luis.detector.config

import com.typesafe.config.Config
import org.apache.spark.sql.SparkSession
import org.slf4j.LoggerFactory

class SparkConfig (config: Config){

  final val log = LoggerFactory.getLogger(getClass.getName)
  /**
   * Create sparkSession
   *
   * @return SparkSession
   */
  def startSparkSession(): SparkSession = {

    log.info("Init Spark Session")

    SparkSession
      .builder()
      .appName(config.getString("app.name"))
      .master(config.getString("spark.cores"))
      .getOrCreate()
  }
}
