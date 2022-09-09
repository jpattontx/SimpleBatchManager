name := "demo-lambda-layer"

version := "0.1"

scalaVersion := "2.13.8"

libraryDependencies += "com.amazonaws" % "aws-java-sdk-s3" % "1.12.296"
libraryDependencies += "com.amazonaws" % "aws-lambda-java-core" % "1.2.1"
libraryDependencies += "com.google.code.gson" % "gson" % "2.8.9"

assemblyMergeStrategy in assembly := {
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case x => MergeStrategy.first
}