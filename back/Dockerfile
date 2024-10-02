# Use existing image as base
FROM openjdk:21-jdk

WORKDIR /opt/app

#ENV SMTP_MAIL=${SMTP_MAIL}
#ENV SMTP_APP_PASSWORD=${SMTP_APP_PASSWORD}
#ENV SPRING_DATASOURCE_URL=${DB_URL}
#ENV SPRING_DATASOURCE_USERNAME=${DB_USERNAME}
#ENV SPRING_DATASOURCE_PASSWORD=${DB_PASSWORD}
#ENV SPRING_JPA_HIBERNATE_DDL_AUTO=update
#ENV SPRING_JPA_DATABASE_PLATFORM=org.hibernate.dialect.PostgreSQLDialect
# Retrieve needed files and dependencies
COPY ./target/tennis.tournament.manager-0.0.1.jar tennis.tournament.manager-0.0.1.jar

# Specify a start-up command
CMD ["java","-jar","/opt/app/tennis.tournament.manager-0.0.1.jar"]