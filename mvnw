#!/bin/sh
# Maven Wrapper

MVN_VERSION=3.8.7
MVN_URL=https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/${MVN_VERSION}/apache-maven-${MVN_VERSION}-bin.zip

if [ ! -d ".mvn/wrapper" ]; then
  mkdir -p .mvn/wrapper
fi
if [ ! -f ".mvn/wrapper/maven-wrapper.jar" ]; then
  echo "Downloading Maven Wrapper..."
  curl -o .mvn/wrapper/maven-wrapper.jar $MVN_URL
fi
java -jar .mvn/wrapper/maven-wrapper.jar "$@"
