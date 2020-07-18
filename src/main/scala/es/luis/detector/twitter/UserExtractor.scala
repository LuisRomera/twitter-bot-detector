package es.luis.detector.twitter

import java.io.{File, PrintWriter}

import com.google.gson.Gson
import com.typesafe.config.Config
import org.slf4j.LoggerFactory
import twitter4j.{Paging, ResponseList, Status, TwitterFactory}
import twitter4j.conf.ConfigurationBuilder

import scala.util.control.Breaks.{break, breakable}

class UserExtractor(config: Config) {

  final val log = LoggerFactory.getLogger(getClass.getName)

  val cb = new ConfigurationBuilder().setDebugEnabled(true)
    .setOAuthConsumerKey(config.getString("twitter.CONSUMER_KEY"))
    .setOAuthConsumerSecret(config.getString("twitter.CONSUMER_SECRET"))
    .setOAuthAccessToken(config.getString("twitter.TOKEN"))
    .setOAuthAccessTokenSecret(config.getString("twitter.TOKEN_SECRET"))

  val tf = new TwitterFactory(cb.build())

  val twitter = tf.getInstance()

  val timeSleep = 13000


  def extract(userName: String): Unit = {
    extractUser(userName)
    log.info("Extract timeline")
  }

  def extractUser(name: String): Unit = {
    val directory = new File(s"users/$name");
    if (!directory.exists())
      directory.mkdir()
    val user = twitter.showUser(name)
    val writer = new PrintWriter(new File(s"users/$name/info.txt"))
    val gson = new Gson()
    writer.write(gson.toJson(user))
    writer.close()


    Thread.sleep(timeSleep)
    extractTimeline(name)
    log.info(s"Extracted timeline $name")
  /*  extractFollowers(name)
    log.info(s"Extracted followers $name")
    extractFriends(name)
    log.info(s"Extracted friends $name")
    */
    extractFavorites(user.getId, name)
    log.info(s"Extracted favorites $name")
  }

  def extractFavorites(id: Long, name: String): Unit = {
    val writer = new PrintWriter(new File(s"users/$name/favorites.txt"))
    val gson = new Gson()
    var cursor = 1
    breakable {
      while (cursor < config.getString("request_max").toInt) {
        try {
          val favorites = twitter.getFavorites(id, new Paging(cursor))
          if (favorites == null || favorites.size() == 0)
            break()
          else {
            for (i <- 0 until favorites.size())
              writer.write(gson.toJson(favorites.get(i)) + "\n")
            cursor += 1
          }
          Thread.sleep(timeSleep)
        } catch {
          case exception: Exception => log.error("Max request")
        }
      }
    }
    writer.close()
  }

  def extractFollowers(name: String): Unit = {
    val writer = new PrintWriter(new File(s"users/$name/followers.txt"))
    val gson = new Gson()
    var cursor = -1
    breakable {
      while (cursor < config.getString("request_max").toInt) {
        try {
          val followers = twitter.getFollowersList(name, cursor)
          if (followers == null || followers.size() == 0)
            break()
          else {
            for (i <- 0 until followers.size())
              writer.write(gson.toJson(followers.get(i)) + "\n")
            cursor += 1
          }
          Thread.sleep(timeSleep)
        } catch {
          case exception: Exception => log.error("Max request")
        }
      }
    }
    writer.close()
  }

  def extractFriends(name: String): Unit = {
    val writer = new PrintWriter(new File(s"users/$name/friends.txt"))
    val gson = new Gson()
    var cursor = -1
    breakable {
      while (cursor < config.getString("request_max").toInt) {
        try {
          val friends = twitter.getFriendsList(name, cursor)
          if (friends == null || friends.size() == 0)
            break()
          else {
            for (i <- 0 until friends.size())
              writer.write(gson.toJson(friends.get(i)) + "\n")
            cursor += 1
          }
          Thread.sleep(timeSleep)
        } catch {
          case exception: Exception => log.error("Max request")
        }
      }
    }
    writer.close()
  }


  def extractTimeline(name: String): Unit = {

    val writer = new PrintWriter(new File(s"users/$name/timeline.txt"))

    val gson = new Gson()
    var pag = 1
    breakable {
      while (pag < config.getString("request_max").toInt) {
        val page = new Paging(pag, 200)
        try {
          val timeline: ResponseList[Status] = twitter.getUserTimeline(name, page)
          if (timeline == null || timeline.size() == 0)
            break
          else {
            for (i <- 0 until timeline.size())
              writer.write(gson.toJson(timeline.get(i)) + "\n")
            pag += 1
          }
          Thread.sleep(timeSleep)
        } catch {
          case exception: Exception => log.error("Max request")
        }
      }
    }
    writer.close()
  }

}
