package es.luis.detector.config

import com.typesafe.config.Config
import org.apache.spark.sql.SparkSession
import org.slf4j.LoggerFactory

class SparkConfig(config: Config) {

  final val log = LoggerFactory.getLogger(getClass.getName)

  /**
   * Create sparkSession
   *
   * @return SparkSession
   */
  def startSparkSession(): SparkSession = {

    log.info("Init Spark Session")
    log.info(s"Profile: ${config.getString("app.profile")}")
    if (SparkSession.getActiveSession.isEmpty && config.getString("app.profile").equals("pro"))
      SparkSession
        .builder()
        .appName(config.getString("app.name"))
        .config("spark.es.nodes", config.getString("elasticsearch.nodes"))
        .config("spark.es.port", config.getString("elasticsearch.port"))
        .config("spark.es.nodes.wan.only", "true")
        .config("es.mapping.id", "id")
        .getOrCreate()
    else if (SparkSession.getActiveSession.isEmpty && config.getString("app.profile").equals("pro"))
      SparkSession
        .builder()
        .appName(config.getString("app.name"))
        .master(config.getString("spark.cores"))
        .config("spark.es.nodes", config.getString("elasticsearch.nodes"))
        .config("spark.es.port", config.getString("elasticsearch.port"))
        .config("spark.es.nodes.wan.only", "true")
        .config("es.mapping.id", "id")
        .getOrCreate()
    else
      SparkSession.getActiveSession.get


  }

  def getSparkSession(): SparkSession =
    SparkSession.getActiveSession.get
}
