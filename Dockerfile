#Build exchange-rate-service
FROM maven:3.5.4-jdk-11-slim
WORKDIR /usr/src/exchange-rate-service-code
COPY . /usr/src/exchange-rate-service-code/
RUN mvn package

#Run exchange-rate-service
WORKDIR /usr/src/exchange-rate-service-app
RUN cp /usr/src/exchange-rate-service-code/target/*.jar ./exchange-rate-service.jar
EXPOSE 8081
CMD ["java", "-jar", "exchange-rate-service.jar"]
