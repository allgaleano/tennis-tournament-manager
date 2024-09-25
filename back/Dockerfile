# Use existing image as base
FROM openjdk:21-jdk

WORKDIR /opt/app

# Retrieve needed files and dependencies
COPY ./target/tennis.tournament.manager-0.0.1-SNAPSHOT.jar tennis.tournament.manager-0.0.1-SNAPSHOT.jar

# Specify a start-up command
CMD ["java","-jar","/opt/app/tennis.tournament.manager-0.0.1-SNAPSHOT.jar"]