package es.luis.detector

import com.typesafe.config.{Config, ConfigFactory}
import es.luis.detector.config.SparkConfig
import org.slf4j.LoggerFactory

object InitApp {
  final val log = LoggerFactory.getLogger(getClass.getName)

  def main(args: Array[String]): Unit = {

    val config:Config = ConfigFactory.load(args(0))

    log.info(s"Init App ${config.getString("app.name")}")

    val spark = new SparkConfig(config).startSparkSession()

    val reader = spark.read.format("org.elasticsearch.spark.sql")
      .option("es.read.metadata", "true")
      .option("es.nodes.wan.only","true")
      .option("es.port","9200")
      .option("es.net.ssl","false")
      .option("es.nodes", "http://192.168.0.44")


    val df = reader.option("es.read.field.exclude", "indices").load("hashtags")
    df.show()


    //    val schemaDF = new StructType().add("name", StringType).add("age", IntegerType)
//    val json: DataFrame= spark
//      .readStream.schema(schemaDF).json("/home/luitro/repositories/twitter-bot-detector/src/main/resources/")
//
//    json.isStreaming
//
//    json
//      .writeStream
//      .format("console")
//      .trigger(Trigger.ProcessingTime("1 second"))
//      .start()
//      .awaitTermination()


  }

}
