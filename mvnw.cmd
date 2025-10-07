@echo off
REM Maven Wrapper for Windows
set MVN_VERSION=3.8.7
set MVN_URL=https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/%MVN_VERSION%/apache-maven-%MVN_VERSION%-bin.zip
if not exist ".mvn\wrapper" mkdir .mvn\wrapper
if not exist ".mvn\wrapper\maven-wrapper.jar" (
  echo Downloading Maven Wrapper...
  powershell -Command "Invoke-WebRequest -Uri %MVN_URL% -OutFile .mvn\wrapper\maven-wrapper.jar"
)
java -jar .mvn\wrapper\maven-wrapper.jar %*
