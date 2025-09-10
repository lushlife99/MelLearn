# 1단계: 빌드 이미지
FROM gradle:7.6.0-jdk17 AS builder

WORKDIR /app
COPY . .
RUN ./gradlew clean build --no-daemon

# 2단계: 런타임 이미지
FROM amazoncorretto:17-alpine-jdk

ARG PROFILES
ARG ENV

# Elastic APM Agent 추가
ENV APM_AGENT_VERSION=1.51.0
ADD https://search.maven.org/remotecontent?filepath=co/elastic/apm/elastic-apm-agent/${APM_AGENT_VERSION}/elastic-apm-agent-${APM_AGENT_VERSION}.jar /elastic-apm-agent.jar

# 빌드 결과물만 복사
COPY --from=builder /app/build/libs/*.jar app.jar

ENTRYPOINT ["sh", "-c", "java \
  -javaagent:/elastic-apm-agent.jar \
  -Delastic.apm.service_name=backend-new \
  -Delastic.apm.server_urls=http://apm-server:8200 \
  -Delastic.apm.environment=${ENV} \
  -Delastic.apm.application_packages=com.mellearn \
  -Dspring.profiles.active=${PROFILES} \
  -Dcom.sun.management.jmxremote \
  -Dcom.sun.management.jmxremote.port=9010 \
  -Dcom.sun.management.jmxremote.rmi.port=9010 \
  -Djava.rmi.server.hostname=127.0.0.1 \
  -Dcom.sun.management.jmxremote.authenticate=false \
  -Dcom.sun.management.jmxremote.ssl=false \
  -jar app.jar"]