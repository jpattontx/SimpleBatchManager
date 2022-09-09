name := "SimpleBatchManager"

version := "0.1"

scalaVersion := "2.13.8"

libraryDependencies += "com.google.code.gson" % "gson" % "2.8.9"
libraryDependencies += "javax.xml.bind" % "jaxb-api" % "2.3.1"
libraryDependencies += "junit" % "junit" % "4.13.1" % Test

lazy val root = (project in file("."))

lazy val demoLambdaLayer = (project in file("demo-lambda-layer"))
  .aggregate(root).dependsOn(root)

lazy val demoLambdaFunction = (project in file("demo-lambda"))
  .aggregate(demoLambdaLayer).dependsOn(demoLambdaLayer)


assemblyMergeStrategy in assembly := {
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case x => MergeStrategy.first
}