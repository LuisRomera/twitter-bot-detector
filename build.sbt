name := "twitter-bot-detector"

version := "0.1"

scalaVersion := "2.11.12"

mainClass in assembly := Some("es.luis.detector.InitApp")

assemblyJarName in assembly := "something.jar"

assemblyMergeStrategy in assembly := {
  case PathList("META-INF", xs @ _*) =>
    xs map {_.toLowerCase} match {
      case "manifest.mf" :: Nil | "index.list" :: Nil | "dependencies" :: Nil =>
        MergeStrategy.discard
      case ps @ x :: xs if ps.last.endsWith(".sf") || ps.last.endsWith(".dsa") =>
        MergeStrategy.discard
      case "plexus" :: xs =>
        MergeStrategy.discard
      case "services" :: xs =>
        MergeStrategy.filterDistinctLines
      case "spring.schemas" :: Nil | "spring.handlers" :: Nil =>
        MergeStrategy.filterDistinctLines
      case _ => MergeStrategy.first
    }
  case "application.conf" => MergeStrategy.concat
  case "reference.conf" => MergeStrategy.concat
  case _ => MergeStrategy.first
}

//lazy val commonSettings = Seq(
//  version := "0.1-SNAPSHOT",
//  organization := "com.example",
//  scalaVersion := "2.11.12"
//)
//
//lazy val app = (project in file("app")).
//  settings(commonSettings: _*).
//  settings(
//    mainClass in assembly := Some("es.luis.detector.InitApp")
//  )
//
//mainClass in assembly := Some("es.luis.detector.InitApp")

val sparkVersion = "2.4.5"

// Typesafe config
libraryDependencies += "com.typesafe" % "config" % "1.4.0"

// Spark Core
libraryDependencies += "org.apache.spark" %% "spark-core" % sparkVersion
// Spark SQL
libraryDependencies += "org.apache.spark" %% "spark-sql" % sparkVersion

// https://mvnrepository.com/artifact/org.elasticsearch/elasticsearch-spark-20
//libraryDependencies += "org.elasticsearch" %% "elasticsearch-spark-20" % "7.4.0"

// https://mvnrepository.com/artifact/org.twitter4j/twitter4j-core
libraryDependencies += "org.twitter4j" % "twitter4j-core" % "4.0.7"

//// https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-web
//libraryDependencies += "org.springframework.boot" % "spring-boot-starter-web" % "2.0.2.RELEASE"
//
//dependencyOverrides ++= {
//  Seq(
//    "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.6.7.1",
//    "com.fasterxml.jackson.core" % "jackson-databind" % "2.6.7",
//    "com.fasterxml.jackson.core" % "jackson-core" % "2.6.7"
//  )
//}
// https://mvnrepository.com/artifact/org.elasticsearch/elasticsearch-spark-20
libraryDependencies += "org.elasticsearch" %% "elasticsearch-spark-20" % "7.4.0"


// gson
//libraryDependencies += "com.google.code.gson" % "gson" % "2.8.6"







