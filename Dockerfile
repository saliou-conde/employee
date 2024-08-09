FROM openjdk:21
EXPOSE 8080
RUN mkdir -p -v /srv/config
RUN mkdir -p -v /srv/logs
ENV DB_URL="jdbc:postgresql://localhost:5432/employee?useSSL=false" DB_PASSWORD=19A12iou# DB_USERNAME=root JWT_SECRET_KEY=11111111111111111111111111111111111111111111111 PRIVATE_KEY="classpath:certs/private.pem" PUBLIC_KEY="classpath:certs/public.pem"
ARG JAR_FILE=employee.jar
COPY ${JAR_FILE} employee.jar
ENTRYPOINT ["java","-jar","employee.jar"]

