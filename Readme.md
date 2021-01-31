Running the executable (for MacOS):

./macookie -d 2018-12-09 -f cookie_log.csv

Pre-requisites to build and run the application:

- java 11 jdk installed
- maven 3.6 installed

Build the application:

- from project directory: mvn clean package

Quickly run the built application:

- from a Java IDE, e.g. IntelliJ, by running the main method class com.quantcast.App and providing
the required parameters

- from the command line in the project target/ directory with the following command:
  java -cp classes/:/USER_HOME/.m2/repository/commons-cli/commons-cli/1.4/commons-cli-1.4.jar com.quantcast.cookieprocess.App  -d 2018-12-09 -f /some_dir/cookie_log.csv
  
USER_HOME should be the directory where the maven .m2 directory is located 

Build the native executable (tested only on MacOS):

- install GraalVM locally, e.g with SDKMan (https://sdkman.io/):

sdk install java 21.0.0.r11-grl

- configure GraalVM:

export GRAALVM_HOME=$HOME/.sdkman/candidates/java/21.0.0.r11-grl

- switch to GraaVM:

sdk use java 21.0.0.r11-grl

- build the executable with maven:

mvn package -Pnative

In the end a file named macookie will be placed in target/ dir