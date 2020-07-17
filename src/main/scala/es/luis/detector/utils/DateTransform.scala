package es.luis.detector.utils

import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.functions.{col, to_timestamp}

object DateTransform {
  def createAtToTimeStamp(df: DataFrame):DataFrame ={
    df
      .withColumn("createdAt_tmp", to_timestamp(col("createdAt"), "MMM d, yyyy hh:mm:ss a")).drop("createdAt").withColumnRenamed("createdAt_tmp", "createdAt")
  }

}
