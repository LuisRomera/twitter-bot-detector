package es.luis.detector.config

import com.typesafe.config.Config
import org.apache.spark.sql.SparkSession

class SparkConfig (config: Config){

  /**
   * Create sparkSession
   *
   * @return SparkSession
   */
  def startSparkSession(): SparkSession =
    SparkSession
      .builder()
      .appName(config.getString("app.name"))
      .master(config.getString("spark.cores"))
      .getOrCreate()
}
