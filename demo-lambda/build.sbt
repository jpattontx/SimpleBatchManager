name := "demo-lambda"

version := "0.1"

scalaVersion := "2.13.8"

libraryDependencies += "software.amazon.awssdk" % "s3" % "2.17.272" % Provided
libraryDependencies += "software.amazon.awssdk" % "kinesis" % "2.17.272"  % Provided
libraryDependencies += "com.amazonaws" % "aws-lambda-java-core" % "1.2.1" % Provided

//libraryDependencies += "demo-lambda-layer" % "demo-lambda-layer" % "0.1"  % Provided


assemblyMergeStrategy in assembly := {
  case PathList("demo", xs @ _*) => MergeStrategy.first
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case x => {
    MergeStrategy.discard
  }
}