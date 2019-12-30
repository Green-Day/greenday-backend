#TODO: Replace with gradle-docker-plugin
FROM openjdk:8-jdk-alpine

ARG user=greenday
ARG group=greenday
ARG uid=1000
ARG gid=1000
ARG home=/var/greenday-backend

ENV XMX="30M"
ENV JDBC_STRING=""

COPY build/libs/greenday-backend-*-all.jar /greenday-backend.jar

RUN mkdir -p $home \
  && chown ${uid}:${gid} $home \
  && addgroup -g ${gid} ${group} \
  && adduser -h "$home" -u ${uid} -G ${group} -s /bin/bash -D ${user} \
  && apk --update add openjdk8-jre

USER ${user}

WORKDIR ${home}

VOLUME ${home}

ENTRYPOINT java "-Xmx$XMX" -jar /greenday-backend.jar "-P:greenday.database.jdbcString=$JDBC_STRING"