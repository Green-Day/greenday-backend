#TODO: Replace with gradle-docker-plugin
FROM openjdk:8-jdk-alpine

ARG user=recyclica
ARG group=recyclica
ARG uid=1000
ARG gid=1000
ARG home=/var/recyclica-backend

ENV XMX="30M"
ENV JDBC_STRING=""

COPY build/libs/recyclica-backend-*-all.jar /recyclica-backend.jar

RUN mkdir -p $home \
  && chown ${uid}:${gid} $home \
  && addgroup -g ${gid} ${group} \
  && adduser -h "$home" -u ${uid} -G ${group} -s /bin/bash -D ${user} \
  && apk --update add openjdk8-jre

USER ${user}

WORKDIR ${home}

VOLUME ${home}

ENTRYPOINT java "-Xmx$XMX" -jar /recyclica-backend.jar "-P:recyclica.database.jdbcString=$JDBC_STRING"