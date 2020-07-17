package es.luis.detector.service

import java.io.IOException

import com.typesafe.config.{Config, ConfigFactory}
import es.luis.detector.config.SparkConfig
import es.luis.detector.twitter.UserExtractor
import es.luis.detector.utils.DateTransform
import javax.annotation.PostConstruct
import org.apache.spark.sql.{DataFrame, SaveMode}
import org.apache.spark.sql.functions._
import org.apache.spark.sql.functions.{col, dayofmonth, desc, minute, second, unix_timestamp}
import org.springframework.core.env.Environment
import org.elasticsearch.spark.sql._
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

import scala.collection.JavaConversions._
@Service
class UserProcesor(env: Environment) {
  final val log = LoggerFactory.getLogger(getClass.getName)
  final val spark = new SparkConfig(env).startSparkSession()

  def addCreated(frame: DataFrame):DataFrame =
    frame.withColumn("created",
      when(col("createdAt").contains("PM"), date_format(to_timestamp(col("createdAt"),
        "MMM dd, yyyy HH:mm:ss") + expr("INTERVAL 12 HOURS"), "yyyy-MM-dd'T'HH:mm:ssZZ"))
        .otherwise(
          date_format(
            to_timestamp(col("createdAt"),
              "MMM dd, yyyy HH:mm:ss"), "yyyy-MM-dd'T'HH:mm:ssZZ"))
    )

  @PostConstruct
//  @Scheduled(cron = "0 0 2 * * ?")
  def analyzerUser(): Unit = {

    val query = "{" +
      "        \"range\" : {" +
      "            \"created\" : {" +
      "                \"gte\" : \"now-24h\"," +
      "                \"lt\" :  \"now\"" +
      "            }" +
      "        }" +
      "    }"

    val users = spark.read
      .format("org.elasticsearch.spark.sql")
      .option("query", query)
      .load("users/users-type")
      .select("screen_name")
      .distinct()


    users.collect().map(_.get(0).toString).foreach(userName => {
      try {


        log.info("-------------------------- Extracting -------------------------- ")
        new UserExtractor(env).extract(userName)

        log.info("-------------------------- Extracted -------------------------- ")

        var probFinal = 0.0

        val userInfoDF = spark.read.json(s"users/$userName/info.txt")

        if (userInfoDF.select("isVerified").first().get(0).asInstanceOf[Boolean])
          probFinal = 0.0
        else {

          val id = userInfoDF.select("id").first().get(0).toString
          val favorites = addCreated(spark.read.json(s"users/$userName/favorites.txt"))

          favorites.saveToEs(s"${userName.toLowerCase}-favorites/${userName.toLowerCase}-favorites-type")

          val timeLineDF = addCreated(spark.read.json(s"users/$userName/timeline.txt"))


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


          val probSecond: Double = (dateTime.select("createdAt")
            .withColumn("second", second(col("createdAt")))
            .groupBy("second").count()
            .orderBy(desc("count"))
            .select("count")
            .limit((countPublish * 0.01).toInt).collect().map(r => r.get(0).asInstanceOf[Long]).toList.sum.toDouble) / countPublish.toDouble


          val probDay: Double = (dateTime.select("createdAt")
            .withColumn("day", dayofmonth(col("createdAt")))
            .groupBy("day").count()
            .orderBy(desc("count"))
            .select("count")
            .limit((countPublish * 0.01).toInt).collect().map(r => r.get(0).asInstanceOf[Long]).toList.sum.toDouble) / countPublish.toDouble


          probFinal = (probDay + probMinute + probSecond) / 3
        }
        val userSave = addCreated(userInfoDF)

        userSave.saveToEs("users-extract/users-extract-type")

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

        log.info(s"$userName -> $probFinal")
      }catch{
        case e:Exception => log.error(s"Error analyzerUser $userName: $e")
      }
    })
  }

}
