FROM eclipse-temurin:17-jre

ENV SPRING_DATASOURCE_URL=${DATABASE-URL}
ENV SPRING_DATASOURCE_USERNAME=${DATABASE-USER}
ENV SPRING_DATASOURCE_PASSWORD=${DATABASE-PASSWORD}

RUN mkdir /app

WORKDIR /app

ADD ./target/product-service-0.0.1-SNAPSHOT.jar /app

EXPOSE 8080

CMD ["java", "-jar", "product-service-0.0.1-SNAPSHOT.jar"]