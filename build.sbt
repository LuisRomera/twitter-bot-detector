name := "twitter-bot-detector"

version := "0.1"

scalaVersion := "2.11.12"

val sparkVersion = "2.4.6"

// Typesafe config
libraryDependencies += "com.typesafe" % "config" % "1.4.0"

// Spark Core
libraryDependencies += "org.apache.spark" %% "spark-core" % sparkVersion
// Spark SQL
libraryDependencies += "org.apache.spark" %% "spark-sql" % sparkVersion

// https://mvnrepository.com/artifact/org.elasticsearch/elasticsearch-spark-20
libraryDependencies += "org.elasticsearch" %% "elasticsearch-spark-20" % "7.4.0"




