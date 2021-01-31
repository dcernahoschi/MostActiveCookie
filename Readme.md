The application executable for macOS is named macookie and can be found in the dist/ folder. It can be run as follows:

./macookie -d 2018-12-09 -f cookie_log.csv

Pre-requisites to build and run the application:

- java 11 or later jdk installed
- maven 3.6 or later installed

How to build the application:

- from project directory: mvn clean package

How to quickly run the previously built application:

- from a Java IDE, e.g. IntelliJ: Just run the main method class com.quantcast.App and providing
the required parameters

- from the command line: From the project target/ directory run the following command:
  java -cp classes/:/USER_HOME/.m2/repository/commons-cli/commons-cli/1.4/commons-cli-1.4.jar com.quantcast.cookieprocess.App -d 2018-12-09 -f cookie_log.csv

USER_HOME should be the directory where the maven .m2 directory is located, /home/username on Linux or /Users/username on macOS

How to build the native executable (tested only on macOS, but it should work on other OSs too):

1. install GraalVM locally, e.g. with SDKMan (https://sdkman.io/):

sdk install java 21.0.0.r11-grl

2. configure GraalVM home dir:

export GRAALVM_HOME=$HOME/.sdkman/candidates/java/21.0.0.r11-grl

3. switch to GraalVM:

sdk use java 21.0.0.r11-grl

4. build the executable with maven:

mvn clean package -Pnative

The binary file is named macookie and will be found in the target/ dir.