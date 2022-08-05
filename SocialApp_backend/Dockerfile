#FROM maven:3.8.6-eclipse-temurin-17
#WORKDIR /SocialApp_backend
#COPY . .
#RUN mvn clean install
#CMD mvn spring-boot:run

FROM maven:3.8.6-eclipse-temurin-17
RUN mkdir /opt/app
COPY ./target/SocialApp_api-0.0.1-SNAPSHOT.jar /opt/app
CMD ["java", "-jar", "/opt/app/SocialApp_api-0.0.1-SNAPSHOT.jar"]