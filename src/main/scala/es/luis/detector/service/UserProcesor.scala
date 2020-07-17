package es.luis.detector.service

import java.io.IOException

import com.typesafe.config.{Config, ConfigFactory}
import es.luis.detector.config.SparkConfig
import es.luis.detector.twitter.UserExtractor
import es.luis.detector.utils.DateTransform
import org.apache.spark.sql.SaveMode
import org.apache.spark.sql.functions._
import org.apache.spark.sql.functions.{col, dayofmonth, desc, minute, second, unix_timestamp}
import org.springframework.core.env.Environment
import org.elasticsearch.spark.sql._

import scala.collection.JavaConversions._

class UserProcesor(env: Environment) {

  def analyzerUser(userName: String): (String, Double) = {

    //val config:Config = ConfigFactory.load("args(0)")

    print("-------------------------- Extrayendo -------------------------- ")
    new UserExtractor(env).extract(userName)

    print("-------------------------- Extraido -------------------------- ")


    val spark = new SparkConfig(env).startSparkSession()


    //val users = env.getProperty("userExtract").toList
    import spark.implicits._
    var probFinal = 0.0

    //   log.info(s"userName ------------> $userName")
    val userInfoDF = spark.read.json(s"users/$userName/info.txt")

    if (userInfoDF.select("isVerified").first().get(0).asInstanceOf[Boolean])
      probFinal = 0.0
    else {

      val id = userInfoDF.select("id").first().get(0).toString
      spark.read.json(s"users/$userName/favorites.txt")
        .saveToEs(s"${userName.toLowerCase}-favorites/${userName.toLowerCase}-favorites-type")

      val timeLineDF = spark.read.json(s"users/$userName/timeline.txt")

      timeLineDF.saveToEs(s"${userName.toLowerCase}-time-line/${userName.toLowerCase}-time-line")

      val dateTime = DateTransform.createAtToTimeStamp(timeLineDF)
      val fistDate = dateTime.withColumn("tmp", unix_timestamp(col("createdAt"))).orderBy(desc("tmp")).select("tmp").first().get(0)

      val countPublish = timeLineDF.count()
      val probMinute: Double = (dateTime.select("createdAt")
        .withColumn("minute", minute(col("createdAt")))
        .groupBy("minute").count()
        .orderBy(desc("count"))
        .select("count")
        .limit((countPublish * 0.01).toInt).collect().map(r => r.get(0).asInstanceOf[Long]).toList.sum.toDouble) / countPublish.toDouble

      //   log.info(s"probMinute $probMinute")

      val probSecond: Double = (dateTime.select("createdAt")
        .withColumn("second", second(col("createdAt")))
        .groupBy("second").count()
        .orderBy(desc("count"))
        .select("count")
        .limit((countPublish * 0.01).toInt).collect().map(r => r.get(0).asInstanceOf[Long]).toList.sum.toDouble) / countPublish.toDouble

      // log.info(s"probSecond $probSecond")

      val probDay: Double = (dateTime.select("createdAt")
        .withColumn("day", dayofmonth(col("createdAt")))
        .groupBy("day").count()
        .orderBy(desc("count"))
        .select("count")
        .limit((countPublish * 0.01).toInt).collect().map(r => r.get(0).asInstanceOf[Long]).toList.sum.toDouble) / countPublish.toDouble

      //log.info(s"probDay $probDay")

      //log.info(s"Total:  ${(probDay + probMinute + probSecond) / 3}")
      probFinal = (probDay + probMinute + probSecond) / 3
    }
    userInfoDF
      .withColumn("prob_bot", lit(probFinal)).saveToEs("users-extract/users-extract-type")

    import java.nio.file.{Files, Paths, Path, SimpleFileVisitor, FileVisitResult}
    import java.nio.file.attribute.BasicFileAttributes

    Files.walkFileTree(Paths.get(s"users/$userName"), new SimpleFileVisitor[Path] {
      override def visitFile(file: Path, attrs: BasicFileAttributes): FileVisitResult = {
        Files.delete(file)
        FileVisitResult.CONTINUE
      }
      override def postVisitDirectory(dir: Path, exc: IOException): FileVisitResult = {
        Files.delete(dir)
        FileVisitResult.CONTINUE
      }
    })

    (userName, probFinal)




    //    result.foreach(u => log.info(s"User: ${u._1} -> Bot probability: ${u._2}"))


  }

}
