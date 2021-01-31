Running the application MacOS executable, named macookie, found in the dist/ folder:

./macookie -d 2018-12-09 -f cookie_log.csv

Pre-requisites to build and run the application:

- java 11 jdk installed
- maven 3.6 installed

Build the application:

- from project directory: mvn clean package

Quickly run the built application:

- from a Java IDE, e.g. IntelliJ, by running the main method class com.quantcast.App and providing
the required parameters
  
In order to build the native executable (tested only on MacOS):

1. install GraalVM locally, e.g. with SDKMan (https://sdkman.io/):

sdk install java 21.0.0.r11-grl

2. configure GraalVM home dir:

export GRAALVM_HOME=$HOME/.sdkman/candidates/java/21.0.0.r11-grl

3. switch to GraaVM:

sdk use java 21.0.0.r11-grl

4. build the executable with maven:

mvn package -Pnative

The binary file is named macookie and will be found in the target/ dir.