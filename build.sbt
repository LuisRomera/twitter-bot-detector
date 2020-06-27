name := "twitter-bot-detector"

version := "0.1"

scalaVersion := "2.12.11"

val sparkVersion = "3.0.0"

// Typesafe config
libraryDependencies += "com.typesafe" % "config" % "1.4.0"

// Spark Core
libraryDependencies += "org.apache.spark" %% "spark-core" % sparkVersion
// Spark SQL
libraryDependencies += "org.apache.spark" %% "spark-sql" % sparkVersion
