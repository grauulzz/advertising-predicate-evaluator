FROM openjdk:8-jdk-alpine
VOLUME /tmp
ARG JAVA_OPTS
ENV JAVA_OPTS=$JAVA_OPTS
COPY build/libs/bd-advertising-service-grauulzz-0.0.1.jar bdadvertisingservicegrauulzz.jar
EXPOSE 8000
ENTRYPOINT exec java $JAVA_OPTS -jar bdadvertisingservicegrauulzz.jar
# For Spring-Boot project, use the entrypoint below to reduce Tomcat startup time.
#ENTRYPOINT exec java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar bdadvertisingservicegrauulzz.jar
