name := """flipkartSMACK"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.12"

crossScalaVersions := Seq("2.11.12", "2.12.4")

libraryDependencies += guice

// Test Database
libraryDependencies += "com.h2database" % "h2" % "1.4.197"

// Testing libraries for dealing with CompletionStage...
libraryDependencies += "org.assertj" % "assertj-core" % "3.6.2" % Test
libraryDependencies += "org.awaitility" % "awaitility" % "2.0.0" % Test
libraryDependencies += "org.apache.kafka" % "kafka-clients" % "1.1.0"
libraryDependencies += "org.apache.kafka" %% "kafka" % "2.0.0"
libraryDependencies += "com.fasterxml.jackson.core" % "jackson-databind" % "2.9.5"
//libraryDependencies += "com.datastax.cassandra" % "cassandra-driver-core" % "3.6.0"
libraryDependencies += "com.datastax.spark" %% "spark-cassandra-connector" % "2.3.0"

libraryDependencies += "org.apache.spark" %% "spark-core" % "2.3.0"
libraryDependencies += "org.apache.spark" %% "spark-sql" % "2.3.0"
libraryDependencies += "org.apache.spark" %% "spark-streaming" % "2.3.0" % "provided"
libraryDependencies += "org.apache.bahir" %% "spark-streaming-akka" % "2.2.2"
libraryDependencies += "org.apache.spark" %% "spark-streaming-kafka-0-10" % "2.3.0"
libraryDependencies += "com.typesafe.akka" %% "akka-stream-kafka" % "1.0-M1"
libraryDependencies +=	"org.apache.hadoop" % "hadoop-common" % "2.8.0"
libraryDependencies +=	"org.apache.hadoop" % "hadoop-hdfs" % "2.8.0"

dependencyOverrides += "com.fasterxml.jackson.core" % "jackson-databind" % "2.6.7"


// Make verbose tests
testOptions in Test := Seq(Tests.Argument(TestFrameworks.JUnit, "-a", "-v"))
