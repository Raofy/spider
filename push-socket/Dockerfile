
FROM java:openjdk-8-jre

MAINTAINER zxlei1 18883179599@163.com

ADD push.jar push.jar

RUN echo "Asia/shanghai" > /etc/timezone

ENTRYPOINT ["java","-Xmx1g","-Xms1g","-jar","/push.jar"]
