package es.luis.detector.config

import com.typesafe.config.Config
import org.apache.spark.sql.SparkSession
import org.slf4j.LoggerFactory
import org.springframework.core.env.Environment
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class SparkConfig @Autowired()(config: Environment){

  final val log = LoggerFactory.getLogger(getClass.getName)
  /**
   * Create sparkSession
   *
   * @return SparkSession
   */
  def startSparkSession(): SparkSession = {

    log.info("Init Spark Session")
    if (SparkSession.getActiveSession.isEmpty)
      SparkSession
        .builder()
        .appName(config.getProperty("app.name"))
        .master(config.getProperty("spark.cores"))
        .config("spark.es.nodes","192.168.0.44")
        .config("spark.es.port","9200")
        .config("spark.es.nodes.wan.only","true")
        .config("es.mapping.id", "id")
        .getOrCreate()
    else
      SparkSession.getActiveSession.get


  }

  def getSparkSession(): SparkSession =
    SparkSession.getActiveSession.get
}
