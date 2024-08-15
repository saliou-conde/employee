FROM openjdk:21
RUN mkdir -p -v /srv/config
RUN mkdir -p -v /srv/logs
ENV DB_URL=jdbc:postgresql://172.21.0.2:5432/employee DB_PASSWORD=19A12iou# DB_USERNAME=root JWT_SECRET_KEY=11111111111111111111111111111111111111111111111 PRIVATE_KEY="classpath:certs/private.pem" PUBLIC_KEY="classpath:certs/public.pem"
ARG JAR_FILE=employee.jar
COPY ${JAR_FILE} employee.jar
ENTRYPOINT ["java","-jar","employee.jar"]

