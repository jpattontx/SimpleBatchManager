#!/bin/bash


sbt clean demoLambdaLayer/assembly
sbt demoLambdaFunction/package

mkdir -p packaged-artifacts
cp demo-lambda/target/scala-2.13/demo-lambda_*.jar packaged-artifacts/demo-lambda-function.jar
rm -rf tmp
mkdir -p tmp/java/lib
cp demo-lambda-layer/target/scala-2.13/demo-lambda-layer-assembly-* tmp/java/lib/
cd tmp
jar cf ../packaged-artifacts/demo-lambda-layer.jar java


