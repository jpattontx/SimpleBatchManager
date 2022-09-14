name := "demo-lambda-layer"

version := "0.1"

scalaVersion := "2.13.8"

libraryDependencies += "software.amazon.awssdk" % "s3" % "2.17.272"
libraryDependencies += "software.amazon.awssdk" % "kinesis" % "2.17.272"
libraryDependencies += "software.amazon.awssdk" % "aws-crt-client" % "2.17.272-PREVIEW"
libraryDependencies += "com.amazonaws" % "aws-lambda-java-core" % "1.2.1"
libraryDependencies += "com.google.code.gson" % "gson" % "2.8.9"

assemblyMergeStrategy in assembly := {
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case x => MergeStrategy.first
}