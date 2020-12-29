
FROM java:openjdk-8-jre-alpine

MAINTAINER zxlei1 18883179599@163.com

ADD spider-server-0.0.1-SNAPSHOT.jar spider-server.jar

RUN echo "Asia/shanghai" > /etc/timezone

EXPOSE 7200

ENTRYPOINT ["java","-Xmx1024m","-Xms1024m","-jar","/spider-server.jar"]
