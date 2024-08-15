FROM openjdk:21

RUN mkdir -p -v /home/app

ARG JAR_FILE=./build/libs/employee.jar

COPY ${JAR_FILE} /home/app/employee.jar

CMD ["java","-jar","/home/app/employee.jar"]

